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
package illarion.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        User,

        /**
         * The data directory that stores the application binary data required to launch the applications.
         */
        Data
    }

    /**
     * The singleton instance of this class.
     */
    @Nonnull
    private static final DirectoryManager INSTANCE = new DirectoryManager();

    /**
     * The detected working directory.
     */
    @Nonnull
    private final Path workingDirectory;

    /**
     * The binary directory that got selected.
     */
    @Nullable
    private Path binaryDirectory;

    /**
     * Private constructor to ensure that only the singleton instance exists.
     */
    private DirectoryManager() {
        String installationDir = System.getProperty("org.illarion.install.dir");
        workingDirectory = Paths.get((installationDir == null) ? "." : installationDir);
        binaryDirectory = null;

        Path userDir = getDirectory(Directory.User);
        if (Files.isRegularFile(userDir)) {
            try {
                Files.delete(userDir);
            } catch (IOException e) {
                log.error("Failed to delete old .illarion file.", e);
            }
        }
        if (!Files.isDirectory(userDir)) {
            try {
                Files.createDirectories(userDir);
            } catch (IOException e) {
                log.error("Failed to create the .illarion directory.", e);
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
    @Nonnull
    public Path getDirectory(@Nonnull Directory dir) {
        switch (dir) {
            case User:
                if (System.getProperty("os.name").contains("Mac OS X")) {
                    return Paths.get(System.getProperty("user.home"), "Library", "org.illarion");
                }
                return Paths.get(System.getProperty("user.home"), ".illarion");
            case Data:
                return getBinaryDirectory();
        }
        throw new IllegalArgumentException("Parameter 'dir' was set to an illegal value: " + dir);
    }

    @Nonnull
    private Path getBinaryDirectory() {
        if (binaryDirectory == null) {
            Path firstChoice = workingDirectory.resolve("bin");
            if (!Files.exists(firstChoice)) {
                try {
                    return Files.createDirectories(firstChoice);
                } catch (IOException ignored) {
                    // not accessible
                }
            }
            if (Files.isDirectory(firstChoice)) {
                try {
                    Path temporaryTestFile = firstChoice.resolve("writing.test");
                    if (Files.isRegularFile(temporaryTestFile)) {
                        Files.delete(temporaryTestFile);
                    }
                    Path newCreatedFile = Files.createFile(temporaryTestFile);
                    if (Files.isRegularFile(temporaryTestFile)) {
                        binaryDirectory = firstChoice;
                    }
                    Files.delete(newCreatedFile);
                } catch (IOException e) {
                    log.info("Accessing the directory failed: {}", e.getMessage());
                }
            }
            if (binaryDirectory == null) {
                Path userDir = getDirectory(Directory.User);
                binaryDirectory = userDir.resolve("bin");
                assert binaryDirectory != null;
                if (!Files.exists(binaryDirectory)) {
                    try {
                        return Files.createDirectories(binaryDirectory);
                    } catch (IOException e) {
                        log.error("Critical error! No possible binary directory.");
                    }
                }
            }
        }
        return binaryDirectory;
    }

    @Nonnull
    public Path resolveFile(@Nonnull Directory dir, @Nonnull String... segments) {
        Path result = getDirectory(dir);
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
    @Nonnull
    public Path getWorkingDirectory() {
        return workingDirectory;
    }
}
