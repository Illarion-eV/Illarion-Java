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
package illarion.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to manage the global directory manager that takes care for the directories the applications need
 * to use.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DirectoryManager {
    private static final Logger log = LoggerFactory.getLogger(DirectoryManager.class);

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

        Directory(@Nonnull String header, @Nonnull String defaultDir) {
            this.header = header;
            this.defaultDir = defaultDir;
        }

        @Nonnull
        public String getHeader() {
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
    @Nonnull
    private static final DirectoryManager INSTANCE = new DirectoryManager();

    /**
     * The character set used to save the configuration file.
     */
    @Nonnull
    private final Charset charSet;

    /**
     * The assignment of the directory identifiers and the actual directory.
     */
    @Nonnull
    private final Map<Directory, Path> directories;

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
    private Path workingDirectory;

    /**
     * Private constructor to ensure that only the singleton instance exists.
     */
    @SuppressWarnings("nls")
    private DirectoryManager() {
        charSet = Charset.forName("UTF-8");
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
            Path workingDir = Paths.get(".");

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
        if ((workingDirectory == null) || !hasMissingDirectory() || !isRelativeDirectoryPossible()) {
            return;
        }

        Path settingsFile = workingDirectory.resolve(".illarion");
        fetchDirectories(settingsFile, true);
    }

    /**
     * Fetch the directories that are set to be located on a absolute location on the file system. The settings
     * depend on the logged in user.
     */
    private void fetchAbsoluteDirectories() {
        if (!hasMissingDirectory()) {
            return;
        }

        Path settingsFile = Paths.get(System.getProperty("user.home"), ".illarion");
        fetchDirectories(settingsFile, false);
    }

    private void fetchDirectories(@Nonnull Path settingsFile, boolean relative) {
        if (!Files.isRegularFile(settingsFile)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(settingsFile, charSet);
            for (String line : lines) {
                for (Directory dir : Directory.values()) {
                    if (line.startsWith(dir.getHeader())) {
                        Path testDir;
                        if (relative) {
                            testDir = Paths.get(dir.getDefaultDir());
                        } else {
                            testDir = Paths.get(line.substring(dir.getHeader().length()));
                        }

                        if (testDirectory(testDir)) {
                            if (createDataDirFile(testDir)) {
                                directories.put(dir, testDir);
                                relativeDirectory.put(dir, relative);
                            }
                        }
                    }
                }
            }
        } catch (@Nonnull IOException ignored) {
        }
    }

    /**
     * Close a closeable object in any case. This function silently catches all possible problems.
     *
     * @param closeable the closeable to close, if this si {@code null} the function does nothing
     */
    private static void closeSilently(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (@Nonnull IOException ignored) {
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
    public Path getDirectory(@Nonnull Directory dir) {
        return directories.get(dir);
    }

    @Nonnull
    public Path resolveFile(@Nonnull Directory dir, @Nonnull String... segments) {
        Path dirPath = getDirectory(dir);
        if (dirPath == null) {
            throw new IllegalStateException("Root directory is not yet load.");
        }
        Path result = dirPath;
        for (String segment : segments) {
            result = result.resolve(segment);
        }
        return result;
    }

    /**
     * In case the directory manager supports relative directories, this is the working directory the client needs to
     * be launched in.
     *
     * @return the working directory or {@code null} in case none is supported
     */
    @Nullable
    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * This function unsets a directory.
     *
     * @param dir the directory to unset
     */
    public void unsetDirectory(@Nonnull Directory dir) {
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
    public boolean isDirectoryRelative(@Nonnull Directory dir) {
        if (isRelativeDirectoryPossible()) {
            Boolean relative = relativeDirectory.get(dir);
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
    public boolean isDirectorySet(@Nonnull Directory dir) {
        return directories.get(dir) != null;
    }

    /**
     * Check if one of the required directories are missing.
     *
     * @return {@code true} in case one of the directories is not set
     */
    public boolean hasMissingDirectory() {
        for (@Nonnull Directory dir : Directory.values()) {
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
        Path settingsFile = Paths.get(System.getProperty("user.home"), ".illarion");

        Files.deleteIfExists(settingsFile);

        boolean foundAbsoluteDirectory = false;
        for (@Nonnull Directory dir : Directory.values()) {
            if (isDirectorySet(dir) && !isDirectoryRelative(dir)) {
                foundAbsoluteDirectory = true;
                break;
            }
        }

        if (foundAbsoluteDirectory) {
            try (BufferedWriter writer = Files.newBufferedWriter(settingsFile, charSet)) {

                for (@Nonnull Directory dir : Directory.values()) {
                    if (isDirectorySet(dir) && !isDirectoryRelative(dir)) {
                        writer.write(dir.getHeader());
                        //null condition checked by isDirectorySet
                        //noinspection ConstantConditions
                        writer.write(getDirectory(dir).toAbsolutePath().toString());
                        writer.newLine();
                    }
                }
                writer.flush();
            }
        }
    }

    /**
     * Save the directories that have a relative local.
     *
     * @throws IOException in case anything goes wrong at saving this settings
     */
    private void saveRelative() throws IOException {
        if ((workingDirectory == null) || !isRelativeDirectoryPossible()) {
            return;
        }
        Path settingsFile = workingDirectory.resolve(".illarion");

        Files.deleteIfExists(settingsFile);

        boolean foundRelativeDirectory = false;
        for (@Nonnull Directory dir : Directory.values()) {
            if (isDirectoryRelative(dir)) {
                foundRelativeDirectory = true;
                break;
            }
        }

        if (foundRelativeDirectory) {
            try (BufferedWriter writer = Files.newBufferedWriter(settingsFile, charSet)) {
                for (@Nonnull Directory dir : Directory.values()) {
                    if (isDirectoryRelative(dir)) {
                        writer.write(dir.getHeader());
                        writer.newLine();
                    }
                }
                writer.flush();
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
        } catch (@Nonnull Exception e) {
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
    public void setDirectory(@Nonnull Directory dir, @Nonnull Path location) {
        if (!location.equals(getDirectory(dir))) {
            if (isNewDirectoryValid(location)) {
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
    public void setDirectoryRelative(@Nonnull Directory dir) {
        if (!isRelativeDirectoryPossible()) {
            throw new IllegalStateException(
                    "Can't set directories relative while relative directories are not possible.");
        }
        if (!isDirectoryRelative(dir)) {
            Path testDir = Paths.get(dir.getDefaultDir());
            if (isNewDirectoryValid(testDir)) {
                directories.put(dir, testDir);
                relativeDirectory.put(dir, Boolean.TRUE);
                dirty = true;
            }
        }
    }

    private static boolean isNewDirectoryValid(@Nonnull Path location) {
        return testDirectory(location) && isValidDataDirectory(location) && createDataDirFile(location);
    }

    private static boolean isValidDataDirectory(@Nullable Path dir) {
        if (dir == null) {
            log.error("Checking for valid directory failed: Directory was NULL");
            return false;
        }

        if (!Files.isDirectory(dir)) {
            log.error("Checking for valid directory failed: Element is no directory.");
            return false;
        }

        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
            boolean emptyDirectory = true;
            for (Path file : dirStream) {
                emptyDirectory = false;
                if ("illarionDir".equals(file.getFileName().toString())) {
                    return true;
                }
            }
            if (!emptyDirectory) {
                log.error("Checking for valid directory failed: Directory is not empty.");
                return false;
            }
            return true;
        } catch (@Nonnull IOException e) {
            log.error("Checking for valid directory failed", e);
            return false;
        }
    }

    private static boolean createDataDirFile(@Nullable Path dir) {
        if (dir != null) {
            Path dataDirFile = dir.resolve("illarionDir");
            if (Files.isRegularFile(dataDirFile)) {
                return true;
            }
            try {
                Files.createFile(dataDirFile);
                return true;
            } catch (@Nonnull IOException e) {
                log.error("Creating the data directory file failed.", e);
            }
        }
        log.error("Creating the data directory file failed. Directory was NULL.");
        return false;
    }

    /**
     * This function is used to test if a file objects points to any existing directory. Also the function will test
     * if this directory is usable for all required operations.
     *
     * @param dir the object to test
     * @return {@code true} in case the object points to a existing directory
     */
    private static boolean testDirectory(@Nullable Path dir) {
        if (dir == null) {
            log.error("Directory test failed: Test directory was NULL");
            return false;
        }
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                log.error("Directory test failed: Creating missing directories failed.", e);
                return false;
            }
        }

        if (!Files.isDirectory(dir)) {
            log.error("Directory test failed. Selected element is not a directory.");
            return false;
        }

        if (!Files.isWritable(dir)) {
            log.error("Directory test failed. Selected element is not writable.");
            return false;
        }

        if (!Files.isReadable(dir)) {
            log.error("Directory test failed. Selected element is not readable.");
            return false;
        }

        return true;
    }
}
