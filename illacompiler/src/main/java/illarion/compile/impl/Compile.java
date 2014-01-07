package illarion.compile.impl;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Compile {
    void setTargetDir(@Nonnull Path directory);

    int compileFile(@Nonnull Path file);
}
