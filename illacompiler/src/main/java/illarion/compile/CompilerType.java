package illarion.compile;

import illarion.compile.impl.Compile;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This enumeration contains all the compiler types the compiler knows.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CompilerType {
    /**
     * Constant for easyNPC files.
     */
    easyNPC("illarion.compile.impl.EasyNpcCompile", ".npc"),

    /**
     * Constant for easyQuest files.
     */
    easyQuest("illarion.compile.impl.EasyQuestCompile", ".quest");

    @Nonnull
    private final String compilerClass;
    @Nonnull
    private final String[] extensions;

    CompilerType(@Nonnull String compilerClass, @Nonnull String... extensions) {
        this.compilerClass = compilerClass;
        this.extensions = extensions;
    }

    /**
     * Get the compiler implementation.
     *
     * @return the compiler implementation
     */
    public Compile getImplementation() {
        try {
            return (Compile) Class.forName(compilerClass).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if the file is valid for this compiler.
     *
     * @param file the file to check
     * @return {@code true} in case this file is valid for this compiler
     */
    public boolean isValidFile(@Nonnull final Path file) {
        if (!Files.isReadable(file)) {
            return false;
        }

        for (String extension : extensions) {
            if (file.toFile().getName().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
