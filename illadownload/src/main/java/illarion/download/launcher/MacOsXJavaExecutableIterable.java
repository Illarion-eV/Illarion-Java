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
package illarion.download.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The sole purpose of this java executable iterator is to combat the inability of Apple Inc. to create a operating
 * system that has even remotely a proper design.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MacOsXJavaExecutableIterable extends AbstractJavaExecutableIterable {
    /**
     * The logger instance for this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(MacOsXJavaExecutableIterable.class);

    /**
     * This is the iterator implementation that is created to walk over the possible locations for java on a Mac OS
     * operating system.
     */
    private static class MacOsXJavaExecutableIterator extends AbstractJavaExecutableIterator {
        /**
         * The data source for most of the paths possible.
         */
        private final MacOsXJavaExecutableIterable source;

        /**
         * The index that is used to track the current path set for Mac.
         */
        private int currentIndex = -1;

        /**
         * The internal iterator supplying the possible alternative paths.
         */
        private Iterator<Path> alternativePaths;

        /**
         * Create a new instance of this iterator.
         *
         * @param source the data source
         */
        public MacOsXJavaExecutableIterator(@Nonnull MacOsXJavaExecutableIterable source) {
            super(source);
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            if (super.hasNext()) {
                return true;
            }
            if ((currentIndex == -1) && (source.getJavaHomeDirectory() != null)) {
                return true;
            }
            if (currentIndex <= 0) {
                if (alternativePaths == null) {
                    alternativePaths = getLibraryDirectoryCandidates().iterator();
                }
                return alternativePaths.hasNext();
            }
            return false;
        }

        @Nonnull
        @Override
        public Path next() {
            if (super.hasNext()) {
                return super.next();
            }
            while (hasNext()) {
                currentIndex++;
                switch (currentIndex) {
                    case 0:
                        Path homePath = source.getJavaHomeDirectory();
                        if (homePath != null) {
                            return homePath;
                        }
                        break;
                    case 1:
                        if (alternativePaths != null) {
                            currentIndex--;
                            return alternativePaths.next();
                        }
                    default:
                        throw new NoSuchElementException();
                }
            }
            throw new NoSuchElementException();
        }
    }

    /**
     * This flag is set to {@code true} once the java home directory was tried to be discovered.
     */
    private boolean javaHomeDirectoryFetched;

    /**
     * The received path of the java home directory.
     */
    @Nullable
    private Path javaHomeDirectory;

    /**
     * This function tries to receive the java home directory for map from /usr/libexec/java_home
     *
     * @return the located directory or {@code null} in case the locating failed
     */
    @Nullable
    private Path getJavaHomeDirectory() {
        if (javaHomeDirectoryFetched) {
            return javaHomeDirectory;
        }
        javaHomeDirectoryFetched = true;
        LOGGER.warn("Platform independent locating tries for java executable failed. Entering MacOS mode.");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/usr/libexec/java_home", "-v", "14");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            //noinspection resource
            process.getOutputStream().close();

            String firstLine;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
                firstLine = reader.readLine();
            }
            if ((firstLine == null) || (process.waitFor() != 0)) {
                LOGGER.error("Failed to locate valid java version.");
                return null;
            }
            Path resultPath = Paths.get(firstLine);
            if (Files.isDirectory(resultPath)) {
                LOGGER.warn("Java home directory located at: {}", resultPath);
                javaHomeDirectory = extendHomeToExecutable(resultPath);
                return resultPath;
            }
        } catch (@Nonnull InterruptedException | IOException e) {
            LOGGER.error("Error fetching java home directory.", e);
        }
        return null;
    }

    /**
     * This function tries to locate the Java executable within a directory. It performs a recursive search if the
     * executable is not at the default location. This is required because the MacOS installments of java have
     * sometimes a custom file structure.
     *
     * @param home the origin of the search
     * @return the path to the java executable or {@code null} in case the search failed
     */
    @Nullable
    private static Path extendHomeToExecutable(@Nonnull Path home) {
        Path defaultPath = home.resolve("bin").resolve("java");
        if (Files.isExecutable(defaultPath)) {
            return defaultPath;
        }

        try {
            Path[] resultFile = new Path[1];
            Files.walkFileTree(home, new SimpleFileVisitor<Path>() {
                @Nonnull
                @Override
                public FileVisitResult visitFile(@Nonnull Path file, BasicFileAttributes attrs) throws IOException {
                    if ("java".equals(file.getFileName().toString())) {
                        resultFile[0] = file;
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            return resultFile[0];
        } catch (IOException e) {
            LOGGER.error("Accessing the directory failed.", e);
        }
        return null;
    }

    /**
     * Get a list of possible candidates for the locations of java. This is a kind of last resort to locate the java
     * installation. If this one fails, all is over.
     *
     * @return the list of java installment candidates
     */
    @Nonnull
    private static List<Path> getLibraryDirectoryCandidates() {
        Stream<Path> possiblePaths = Stream.empty();
        try {
            Path systemJvmPath = Paths.get("/System/Library/Frameworks/JavaVM.framework/Versions/");
            if (Files.isDirectory(systemJvmPath)) {
                Stream<Path> paths = Files.list(systemJvmPath).map(path -> path.resolve("Home"));
                possiblePaths = Stream.concat(possiblePaths, paths);
            }

            Path appletPluginPath = Paths.get("/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home");
            if (Files.isDirectory(appletPluginPath)) {
                possiblePaths = Stream.concat(possiblePaths, Stream.of(appletPluginPath));
            }

            Path libraryJvmPath = Paths.get("/Library/Java/JavaVirtualMachines/");
            if (Files.isDirectory(libraryJvmPath)) {
                Stream<Path> paths = Files.list(libraryJvmPath)
                        .map(path -> path.resolve("Contents").resolve("Home"));
                possiblePaths = Stream.concat(possiblePaths, paths);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to resolve the directories.", e);
        }

        return possiblePaths.map(MacOsXJavaExecutableIterable::extendHomeToExecutable).collect(Collectors.toList());
    }

    @Override
    public Iterator<Path> iterator() {
        return new MacOsXJavaExecutableIterator(this);
    }
}
