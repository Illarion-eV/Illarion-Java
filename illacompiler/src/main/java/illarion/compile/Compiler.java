package illarion.compile;

import illarion.compile.impl.Compile;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Compiler.class);
    private static Map<CompilerType, Path> storagePaths;

    public static void main(final String[] args) {
        ByteArrayOutputStream stdOutBuffer = new ByteArrayOutputStream();
        PrintStream orgStdOut = System.out;
        System.setOut(new PrintStream(stdOutBuffer));

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        Options options = new Options();

        final Option npcDir = new Option("n", "npc-dir", true, "The place where the compiled NPC files are stored.");
        npcDir.setArgs(1);
        npcDir.setArgName("directory");
        npcDir.setRequired(false);
        options.addOption(npcDir);

        final Option questDir = new Option("q", "quest-dir", true,
                                           "The place where the compiled Quest files are " + "stored.");
        questDir.setArgs(1);
        questDir.setArgName("directory");
        questDir.setRequired(false);
        options.addOption(questDir);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            storagePaths = new EnumMap<>(CompilerType.class);
            String npcPath = cmd.getOptionValue('n');
            if (npcPath != null) {
                storagePaths.put(CompilerType.easyNPC, Paths.get(npcPath));
            }
            String questPath = cmd.getOptionValue('q');
            if (questPath != null) {
                storagePaths.put(CompilerType.easyQuest, Paths.get(questPath));
            }

            String[] files = cmd.getArgs();
            if (files.length > 0) {
                // restore std out
                System.setOut(orgStdOut);
                stdOutBuffer.writeTo(orgStdOut);

                // process files
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
            }
        } catch (final ParseException e) {
            final HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar compiler.jar [Options] File", options, true);
            System.exit(-1);
        } catch (final IOException e) {
            LOGGER.error(e.getLocalizedMessage());
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
                if (path.isAbsolute()) {
                    if (storagePaths.containsKey(type)) {
                        compile.setTargetDir(storagePaths.get(type));
                    } else {
                        compile.setTargetDir(path.getParent());
                    }
                } else {
                    if (storagePaths.containsKey(type)) {
                        Path parent = path.getParent();
                        if (parent == null) {
                            compile.setTargetDir(storagePaths.get(type));
                        } else {
                            compile.setTargetDir(storagePaths.get(type).resolve(parent));
                        }
                    } else {
                        Path parent = path.getParent();
                        if (parent == null) {
                            compile.setTargetDir(path.toAbsolutePath().getParent());
                        } else {
                            compile.setTargetDir(parent);
                        }
                    }
                }
                compileResult = compile.compileFile(path.toAbsolutePath());
                if (compileResult == 0) {
                    break;
                }
            }
        }

        switch (compileResult) {
            case 1:
                LOGGER.info("Skipped file: {}", path.getFileName());
                break;
            case 0:
                return;
            default:
                System.exit(compileResult);
        }
    }
}
