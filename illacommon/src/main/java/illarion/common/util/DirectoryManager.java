/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * This class is used to manage the global directory manager that takes care for the directories the applications need
 * to use.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DirectoryManager {
    /**
     * The enumeration of directories that are managed by this manager.
     */
    public enum Directory {
        /**
         * The user directory that stores the user related data like log files, character data and settings.
         */
        User("User:", "usr"),

        /**
         * The data directory that stores the application binary data required to launch the applications.
         */
        Data("App:", "bin");

        /**
         * The header of this directory identifier.
         */
        @Nonnull
        private final String header;

        /**
         * The default name for the directory.
         */
        @Nonnull
        private final String defaultDir;

        private Directory(@Nonnull final String header, @Nonnull final String defaultDir) {
            this.header = header;
            this.defaultDir = defaultDir;
        }

        @Nonnull
        protected String getHeader() {
            return header;
        }

        @Nonnull
        public String getDefaultDir() {
            return defaultDir;
        }
    }

    /**
     * The singleton instance of this class.
     */
    private static final DirectoryManager INSTANCE = new DirectoryManager();

    /**
     * The assignment of the directory identifiers and the actual directory.
     */
    @Nonnull
    private final Map<Directory, File> directories;

    /**
     * The relative flags of each directory.
     */
    @Nonnull
    private final Map<Directory, Boolean> relativeDirectory;

    /**
     * This flag is set <code>true</code> in case the directories got changed
     * and need to be saved.
     */
    private boolean dirty;

    /**
     * This variable stores the result of the test if relative directory references are possible at all.
     */
    private boolean relativeDirectoryPossible;

    /**
     * The detected working directory.
     */
    @Nullable
    private File workingDirectory;

    /**
     * Private constructor to ensure that only the singleton instance exists.
     */
    @SuppressWarnings("nls")
    private DirectoryManager() {
        directories = new EnumMap<>(Directory.class);
        relativeDirectory = new EnumMap<>(Directory.class);

        testRelativeDirectorySupport();

        fetchRelativeDirectories();
        fetchAbsoluteDirectories();
    }

    /**
     * Detect if its possible to use a relative directory reference. And if its possible also locate the working
     * directory or rather the location of the executed JAR file.
     */
    private void testRelativeDirectorySupport() {
        if (EnvironmentDetect.isWebstart()) {
            relativeDirectoryPossible = false;
        } else {
            final File workingDir = new File(".");

            relativeDirectoryPossible = testDirectory(workingDir);
            if (relativeDirectoryPossible) {
                workingDirectory = workingDir;
            }
        }
    }

    /**
     * Check if it is possible to set a directory to be relative to the executed JAR file.
     *
     * @return {@code true} in case a relative directory is possible
     */
    public boolean isRelativeDirectoryPossible() {
        return relativeDirectoryPossible;
    }

    /**
     * Fetch the directories that are set to be located relative to the launched file. This only works in case the
     * application was not launched as applet and not launched as webstart application.
     */
    private void fetchRelativeDirectories() {
        if (!hasMissingDirectory() || !isRelativeDirectoryPossible()) {
            return;
        }

        final File settingsFile = new File(workingDirectory, ".illarion");

        if (!settingsFile.exists() || !settingsFile.isFile()) {
            return;
        }

        BufferedReader inFile = null;
        try {
            inFile = new BufferedReader(new FileReader(settingsFile));
            String line = inFile.readLine();

            while (line != null) {
                for (final Directory dir : Directory.values()) {
                    if (line.startsWith(dir.getHeader())) {
                        final File testDir = new File(dir.getDefaultDir());
                        if (testDirectory(testDir)) {
                            if (createDataDirFile(testDir)) {
                                directories.put(dir, testDir);
                                relativeDirectory.put(dir, Boolean.TRUE);
                            }
                        }
                    }
                }
                line = inFile.readLine();
            }
        } catch (@Nonnull final IOException ignored) {
        } finally {
            closeSilently(inFile);
        }
    }

    /**
     * Fetch the directories that are set to be located on a absolute location on the file system. The settings
     * depend on the logged in user.
     */
    private void fetchAbsoluteDirectories() {
        if (!hasMissingDirectory()) {
            return;
        }

        final File settingsFile = new File(System.getProperty("user.home"), ".illarion");

        if (!settingsFile.exists() || !settingsFile.isFile()) {
            return;
        }

        BufferedReader inFile = null;
        try {
            inFile = new BufferedReader(new FileReader(settingsFile));
            String line = inFile.readLine();

            while (line != null) {
                for (final Directory dir : Directory.values()) {
                    if (line.startsWith(dir.getHeader())) {
                        final File testDir = new File(line.substring(dir.getHeader().length()));
                        if (testDirectory(testDir)) {
                            if (createDataDirFile(testDir)) {
                                directories.put(dir, testDir);
                                relativeDirectory.put(dir, Boolean.FALSE);
                            }
                        }
                    }
                }
                line = inFile.readLine();
            }
        } catch (@Nonnull final IOException ignored) {
        } finally {
            closeSilently(inFile);
        }
    }

    /**
     * Close a closeable object in any case. This function silently catches all possible problems.
     *
     * @param closeable the closeable to close, if this si {@code null} the function does nothing
     */
    private static void closeSilently(@Nullable final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (@Nonnull final IOException ignored) {
            }
        }
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance
     */
    @Nonnull
    public static DirectoryManager getInstance() {
        return INSTANCE;
    }

    /**
     * Get the location of the specified directory in the local file system.
     *
     * @param dir the directory
     * @return the location of the directory in the local file system or {@code null} in case the directory is not set
     */
    @Nullable
    public File getDirectory(@Nonnull final Directory dir) {
        return directories.get(dir);
    }

    /**
     * In case the directory manager supports relative directories, this is the working directory the client needs to
     * be launched in.
     *
     * @return the working directory or {@code null} in case none is supported
     */
    @Nullable
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * This function unsets a directory.
     *
     * @param dir the directory to unset
     */
    public void unsetDirectory(@Nonnull final Directory dir) {
        directories.remove(dir);
        relativeDirectory.remove(dir);
        dirty = true;
    }

    /**
     * Check if a directory has a absolute location in the file system or is placed relative to the executed JAR file.
     *
     * @param dir the directory
     * @return {@code true} if the directory is set as relative directory
     */
    public boolean isDirectoryRelative(@Nonnull final Directory dir) {
        if (isRelativeDirectoryPossible()) {
            final Boolean relative = relativeDirectory.get(dir);
            if (relative == null) {
                return false;
            }
            return relative;
        }
        return false;
    }

    /**
     * Check if the reference to the local file system is set for a directory.
     *
     * @param dir the directory to check
     * @return {@code true} if the directory is set
     */
    public boolean isDirectorySet(@Nonnull final Directory dir) {
        return directories.get(dir) != null;
    }

    /**
     * Check if one of the required directories are missing.
     *
     * @return {@code true} in case one of the directories is not set
     */
    public boolean hasMissingDirectory() {
        for (@Nonnull final Directory dir : Directory.values()) {
            if (!isDirectorySet(dir)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Store the directories that are set to be absolute.
     *
     * @throws IOException in case saving the directory fails for any reason
     */
    private void saveAbsolute() throws IOException {
        final File settingsFile = new File(System.getProperty("user.home"), ".illarion");

        if (settingsFile.exists()) {
            if (!settingsFile.delete()) {
                throw new IOException("Failed to remove old settings file.");
            }
        }

        boolean foundAbsoluteDirectory = false;
        for (@Nonnull final Directory dir : Directory.values()) {
            if (isDirectorySet(dir) && !isDirectoryRelative(dir)) {
                foundAbsoluteDirectory = true;
                break;
            }
        }

        if (foundAbsoluteDirectory) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(settingsFile));

                for (@Nonnull final Directory dir : Directory.values()) {
                    if (isDirectorySet(dir) && !isDirectoryRelative(dir)) {
                        writer.write(dir.getHeader());
                        //null condition checked by isDirectorySet
                        //noinspection ConstantConditions
                        writer.write(getDirectory(dir).getAbsolutePath());
                        writer.newLine();
                    }
                }
                writer.flush();
            } finally {
                closeSilently(writer);
            }
        }
    }

    /**
     * Save the directories that have a relative local.
     *
     * @throws IOException in case anything goes wrong at saving this settings
     */
    private void saveRelative() throws IOException {
        if (!isRelativeDirectoryPossible()) {
            return;
        }
        final File settingsFile = new File(workingDirectory, ".illarion");

        if (settingsFile.exists()) {
            if (!settingsFile.delete()) {
                throw new IOException("Failed to remove old settings file.");
            }
        }

        boolean foundRelativeDirectory = false;
        for (@Nonnull final Directory dir : Directory.values()) {
            if (isDirectoryRelative(dir)) {
                foundRelativeDirectory = true;
                break;
            }
        }

        if (foundRelativeDirectory) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(settingsFile));

                for (@Nonnull final Directory dir : Directory.values()) {
                    if (isDirectoryRelative(dir)) {
                        writer.write(dir.getHeader());
                        writer.newLine();
                    }
                }
                writer.flush();
            } finally {
                closeSilently(writer);
            }
        }
    }

    /**
     * Save the directory settings to the hard disk.
     *
     * @return {@code true} in case the saving operation went well
     */
    @SuppressWarnings("nls")
    public boolean save() {
        if (!dirty) {
            return true;
        }

        try {
            saveRelative();
            saveAbsolute();
        } catch (@Nonnull final Exception e) {
            return false;
        }

        dirty = false;

        return true;
    }

    /**
     * Set the file system reference of a directory.
     *
     * @param dir the directory
     * @param location the local of the directory in the local file system
     */
    public void setDirectory(@Nonnull final Directory dir, @Nonnull final File location) {
        if (!location.equals(getDirectory(dir))) {
            if (isNewDirectoryValid(dir, location)) {
                directories.put(dir, location);
                relativeDirectory.put(dir, Boolean.FALSE);
                dirty = true;
            }
        }
    }

    /**
     * Set a directory to be stored relative to the executed JAR file.
     *
     * @param dir the directory to handle relative
     */
    public void setDirectoryRelative(@Nonnull final Directory dir) {
        if (!isRelativeDirectoryPossible()) {
            throw new IllegalStateException(
                    "Can't set directories relative while relative directories are not possible.");
        }
        if (!isDirectoryRelative(dir)) {
            final File testDir = new File(dir.getDefaultDir());
            if (isNewDirectoryValid(dir, testDir)) {
                directories.put(dir, testDir);
                relativeDirectory.put(dir, Boolean.TRUE);
                dirty = true;
            }
        }
    }

    private static boolean isNewDirectoryValid(@Nonnull final Directory dir, @Nonnull final File location) {
        return testDirectory(location) && isValidDataDirectory(location) && createDataDirFile(location);
    }

    private static boolean isValidDataDirectory(@Nullable final File dir) {
        if (dir == null) {
            return false;
        }

        if (!dir.isDirectory()) {
            return false;
        }

        final File[] contents = dir.listFiles();
        if (contents == null) {
            return false;
        }

        if (contents.length == 0) {
            return true;
        }

        final File dataDirFile = new File(dir, "illarionDir");
        return dataDirFile.exists();
    }

    private static boolean createDataDirFile(@Nullable final File dir) {
        if (dir != null) {
            final File dataDirFile = new File(dir, "illarionDir");
            if (dataDirFile.exists()) {
                return true;
            }
            try {
                return dataDirFile.createNewFile();
            } catch (@Nonnull final IOException ignored) {
            }
        }
        return false;
    }

    /**
     * This function is used to test if a file objects points to any existing directory. Also the function will test
     * if this directory is usable for all required operations.
     *
     * @param dir the object to test
     * @return {@code true} in case the object points to a existing directory
     */
    private static boolean testDirectory(@Nullable final File dir) {
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
        } catch (@Nonnull final Exception ex) {
            return false;
        }

        return true;
    }
}
