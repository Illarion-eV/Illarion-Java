/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is used to manage the global directory manager that takes care for
 * the directories the manager needs to use.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class DirectoryManager {
    /**
     * This header is used to identify the application directory in the folder
     * configuration file.
     */
    private static final String APP_HEADER = "App:"; //$NON-NLS-1$

    /**
     * The singleton instance of this class.
     */
    private static final DirectoryManager INSTANCE = new DirectoryManager();

    /**
     * This header is used to identify the user directory in the folder
     * configuration file.
     */
    private static final String USER_HEADER = "User:"; //$NON-NLS-1$

    /**
     * The directory that stores the application data (so the *.jar files)
     */
    private File dataDirectory;

    /**
     * This flag is set <code>true</code> in case the directories got changed
     * and need to be saved.
     */
    private boolean dirty;

    /**
     * The directory that stores the user data.
     */
    private File userDirectory;

    /**
     * Private constructor to ensure that only the singleton instance exists.
     */
    @SuppressWarnings("nls")
    private DirectoryManager() {
        final String userPath =
            System.getProperty("user.home") + File.separator + ".illarion";

        final File settingsFile = new File(userPath);

        if (!settingsFile.exists() || !settingsFile.isFile()) {
            return;
        }

        BufferedReader inFile = null;
        try {
            File testDir = null;
            inFile = new BufferedReader(new FileReader(settingsFile));
            String line = inFile.readLine();

            while (line != null) {
                if (line.startsWith(USER_HEADER)) {
                    testDir = new File(line.substring(USER_HEADER.length()));
                    if (testDirectory(testDir)) {
                        userDirectory = testDir;
                    }
                } else if (line.startsWith(APP_HEADER)) {
                    testDir = new File(line.substring(APP_HEADER.length()));
                    if (testDirectory(testDir)) {
                        dataDirectory = testDir;
                    }
                } else {
                    if (userDirectory == null) {
                        testDir = new File(line);
                        if (testDirectory(testDir)) {
                            userDirectory = testDir;
                        }
                    }
                }
                line = inFile.readLine();
            }
        } catch (final FileNotFoundException e) {
            return;
        } catch (final IOException e) {
            return;
        } finally {
            try {
                if (inFile != null) {
                    inFile.close();
                }
            } catch (final IOException e) {
                return;
            }
        }
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    public static DirectoryManager getInstance() {
        return INSTANCE;
    }

    /**
     * Get the directory that is used to store the application data for the
     * Illarion Java applications.
     * 
     * @return the directory or <code>null</code> in case there is no directory
     *         set
     */
    public File getDataDirectory() {
        return dataDirectory;
    }

    /**
     * Get the directory for the user data.
     * 
     * @return the directory for the user data or <code>null</code> in case its
     *         not set
     */
    public File getUserDirectory() {
        return userDirectory;
    }

    /**
     * Check if a application data directory is set.
     * 
     * @return <code>true</code> in case the application data directory is set
     */
    public boolean hasDataDirectory() {
        return (dataDirectory != null);
    }

    /**
     * Check if a user directory is set.
     * 
     * @return <code>true</code> in case a user directory is set
     */
    public boolean hasUserDirectory() {
        return (userDirectory != null);
    }

    /**
     * Save the directory settings to the hard disk.
     * 
     * @return <code>true</code> in case the saving operation went well
     */
    @SuppressWarnings("nls")
    public boolean save() {
        if (!dirty) {
            return true;
        }
        final String userPath =
            System.getProperty("user.home") + File.separator + ".illarion";

        final File settingsFile = new File(userPath);

        if (settingsFile.exists()) {
            if (!settingsFile.isFile() || !settingsFile.canWrite()) {
                return false;
            } else if (!settingsFile.delete()) {
                return false;
            }
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(settingsFile));
            if (userDirectory != null) {
                writer.write(userDirectory.getAbsolutePath());
                writer.write("\n");
                writer.write(USER_HEADER);
                writer.write(userDirectory.getAbsolutePath());
                writer.write("\n");
            }
            if (dataDirectory != null) {
                writer.write(APP_HEADER);
                writer.write(dataDirectory.getAbsolutePath());
            }

            writer.flush();
        } catch (final IOException e) {
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (final IOException e) {
                return false;
            }
        }
        dirty = false;

        return true;
    }

    /**
     * Set a application directory. This causes the directory manager to check
     * if the directory is valid or not. In case its not the directory won't be
     * set.
     * 
     * @param dir the directory that is supposed to be the new application data
     *            directory
     */
    public void setDataDirectory(final File dir) {
        if (testDirectory(dir) && !dir.equals(dataDirectory)) {
            dataDirectory = dir;
            dirty = true;
        }
    }

    /**
     * Set a user directory. This causes the directory manager to check if the
     * directory is valid or not. In case its not the directory won't be set.
     * 
     * @param dir the directory that is supposed to be the new user directory
     */
    public void setUserDirectory(final File dir) {
        if (testDirectory(dir) && !dir.equals(userDirectory)) {
            userDirectory = dir;
            dirty = true;
        }
    }

    /**
     * This function is used to test if a file objects points to any existing
     * directory. Also the function will test if this directory is usable for
     * all required operations.
     * 
     * @param dir the object to test
     * @return <code>true<code> in case the object points to a existing directory
     */
    private boolean testDirectory(final File dir) {
        if (dir == null) {
            return false;
        }
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return false;
            }
        }

        if (!dir.isDirectory()) {
            return false;
        }

        final File testFile = new File(dir, "rw.test"); //$NON-NLS-1$
        try {
            if (!testFile.createNewFile()) {
                return false;
            }

            if (!testFile.canRead() || !testFile.canWrite()) {
                return false;
            }

            if (!testFile.delete()) {
                return false;
            }
        } catch (final Exception ex) {
            return false;
        }

        return true;
    }
}
