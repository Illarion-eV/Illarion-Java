/*
 * This file is part of the Illarion Download Utility.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Download Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Download Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Download Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.install.resources.libs;

import illarion.common.util.DirectoryManager;
import illarion.download.install.resources.ResourceDirectory;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * The resource directory for the library resources.
 *
 * @author Martin Karing
 * @version 1.00
 * @since 1.00
 */
final class LibraryDirectory implements ResourceDirectory {
    /**
     * This is the singleton instance of this class.
     */
    private static final LibraryDirectory INSTANCE = new LibraryDirectory();

    /**
     * The directory that will be exposed to the development resources.
     */
    private String dir;

    /**
     * Private constructor to ensure that no instances but the singleton
     * instance are created. Also the directory exposed by this directory
     * handler is created here.
     */
    @SuppressWarnings("nls")
    private LibraryDirectory() {
        final String extDir = System.getProperty("illarion.download.lib.dir");
        if (extDir != null) {
            dir = extDir;
        } else {
            dir =
                    DirectoryManager.getInstance().getDataDirectory()
                            .getAbsolutePath()
                            + File.separator + LibraryResource.LOCAL_LIB_PATH;
        }
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static LibraryDirectory getInstance() {
        return INSTANCE;
    }

    /**
     * Fetch the directory that is needed for the library resources.
     */
    @Override
    public String getDirectory() {
        return dir;
    }

}
