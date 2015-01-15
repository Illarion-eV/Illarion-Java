/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.download.cleanup;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class UserDirectoryFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(@Nonnull File dir, @Nonnull String name) {
        // keep everything in the alternative binary storage
        if (dir.toString().contains("/bin/") || dir.toString().contains("\\bin\\")) {
            return false;
        }

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
