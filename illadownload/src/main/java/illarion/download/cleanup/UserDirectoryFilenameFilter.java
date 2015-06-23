/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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

import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Path;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class UserDirectoryFilenameFilter implements Filter<Path> {
    @Override
    public boolean accept(Path entry) throws IOException {
        if (entry == null) {
            throw new IllegalArgumentException("The path must not be null.");
        }

        // keep everything in the alternative binary storage
        for (int i = 0; i < (entry.getNameCount() - 1); i++) {
            Path dirName = entry.getName(i);
            if ("bin".equals(dirName.toString())) {
                return false;
            }
        }

        String name = entry.getFileName().toString();
        // old map files
        if (name.startsWith("level") && name.endsWith(".map")) {
            return true;
        }
        // old name files
        if ("names.tbl".equals(name) || "names.dat".equals(name)) {
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
