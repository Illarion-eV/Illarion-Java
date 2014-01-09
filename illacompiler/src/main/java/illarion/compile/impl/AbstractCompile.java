package illarion.compile.impl;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractCompile implements Compile {
    private Path targetDir;

    @Override
    public void setTargetDir(@Nonnull Path directory) {
        targetDir = directory;
    }

    protected Path getTargetDir() {
        return targetDir;
    }

    protected void ensureTargetDir() throws IOException {
        if (Files.isDirectory(targetDir)) {
            return;
        }
        if (Files.exists(targetDir)) {
            throw new IOException("Target directory points to a file: " + targetDir);
        }
        Files.createDirectories(targetDir);
    }
}
