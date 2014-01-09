package illarion.compile.impl;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.Parser;
import illarion.easynpc.ScriptWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class EasyNpcCompile extends AbstractCompile {
    @Override
    public int compileFile(@Nonnull final Path file) {
        Objects.requireNonNull(file);
        Path targetDir = Objects.requireNonNull(getTargetDir());
        try {
            final EasyNpcScript script = new EasyNpcScript(file);
            ParsedNpc npc = Parser.getInstance().parse(script);
            if (npc.hasErrors()) {
                System.err.println("Parsing the NPC failed with " + npc.getErrorCount() + " errors");
                for (int i = 0; i < npc.getErrorCount(); i++) {
                    ParsedNpc.Error error = npc.getError(i);
                    System.err.println("\tLine " + error.getLine().getLineNumber() + ": " + error.getMessage());
                }
                return -1;
            }
            final ScriptWriter writer = new ScriptWriter();
            writer.setSource(npc);
            writer.setTargetLanguage(ScriptWriter.ScriptWriterTarget.LUA);
            writer.setGenerated(true);
            try (Writer write = Files
                    .newBufferedWriter(targetDir.resolve(npc.getLuaFilename()), EasyNpcScript.DEFAULT_CHARSET)) {
                writer.setWritingTarget(write);
                writer.write();
                write.flush();
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return -1;
        }
        return 0;
    }
}
