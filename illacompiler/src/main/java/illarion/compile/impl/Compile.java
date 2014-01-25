package illarion.compile.impl;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Compile {
    void setTargetDir(@Nonnull Path directory);

    int compileFile(@Nonnull Path file);

    int compileStream(@Nonnull InputStream in, @Nonnull OutputStream out);
}
