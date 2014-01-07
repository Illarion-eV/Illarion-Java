package illarion.compile.impl;

import javax.annotation.Nonnull;
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
}
