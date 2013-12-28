package illarion.download.cleanup;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class UserDirectoryFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, @Nonnull String name) {
        // old map files
        if (name.startsWith("level") && name.endsWith(".map")) {
            return true;
        }
        // old name files
        if (name.equals("names.tbl") || name.equals("names.dat")) {
            return true;
        }
        // old crash dump file
        if (name.startsWith("crash_") && name.endsWith(".dump")) {
            return true;
        }
        // old config file
        if (name.endsWith(".cfg")) {
            return true;
        }

        // keep everything else
        return false;
    }
}
