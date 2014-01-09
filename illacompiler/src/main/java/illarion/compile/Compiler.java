package illarion.compile;

import illarion.compile.impl.Compile;
import org.apache.commons.cli.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumMap;
import java.util.Map;

/**
 * This the the main class for the compiler. It determines the kind of compiler required for the set file and performs
 * the compiling operation.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Compiler {
    private static Map<CompilerType, Path> storagePaths;

    public static void main(final String[] args) {
        Options options = new Options();

        final Option npcDir = new Option("n", "npc-dir", true, "The place where the compiled NPC files are stored.");
        npcDir.setArgs(1);
        npcDir.setArgName("directory");
        npcDir.setRequired(true);
        options.addOption(npcDir);

        final Option questDir = new Option("q", "quest-dir", true,
                                           "The place where the compiled Quest files are " + "stored.");
        questDir.setArgs(1);
        questDir.setArgName("directory");
        questDir.setRequired(true);
        options.addOption(questDir);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            storagePaths = new EnumMap<>(CompilerType.class);
            storagePaths.put(CompilerType.easyNPC, Paths.get(cmd.getOptionValue('n')));
            storagePaths.put(CompilerType.easyQuest, Paths.get(cmd.getOptionValue('q')));

            String[] files = cmd.getArgs();
            for (String file : files) {
                Path path = Paths.get(file);
                if (Files.isDirectory(path)) {
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            FileVisitResult result = super.visitFile(file, attrs);
                            if (result == FileVisitResult.CONTINUE) {
                                processPath(file);
                                return FileVisitResult.CONTINUE;
                            }
                            return result;
                        }
                    });
                } else {
                    processPath(path);
                }
            }
        } catch (final ParseException e) {
            final HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar compiler.jar [Options] File", options);
            System.exit(-1);
        } catch (final IOException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(-1);
        }
    }

    private static void processPath(@Nonnull final Path path) throws IOException {
        if (Files.isDirectory(path)) {
            return;
        }

        int compileResult = 1;
        for (CompilerType type : CompilerType.values()) {
            if (type.isValidFile(path)) {
                Compile compile = type.getImplementation();
                compile.setTargetDir(storagePaths.get(type));
                compileResult = compile.compileFile(path);
                if (compileResult == 0) {
                    break;
                }
            }
        }

        switch (compileResult) {
            case 1:
                System.out.println("Skipped file: " + path.getFileName());
                break;
            case 0:
                return;
            default:
                System.exit(compileResult);
        }
    }
}
