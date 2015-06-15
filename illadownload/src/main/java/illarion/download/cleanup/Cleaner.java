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

import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import illarion.common.util.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * This is the cleaner implementation of the downloader. While the downloader in general only downloads stuff and
 * stores it on the drive, this class is tasked with the cleanup process. Its able to delete old artifacts or just
 * delete everything in its way.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Cleaner {
    /**
     * The logger that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Cleaner.class);

    /**
     * The executor service that is used to balance the load to find all required files across multiple threads.
     */
    @Nullable
    private ExecutorService executorService;

    @Nonnull
    private final ProgressMonitor monitor;

    /**
     * Create the cleaner and set the mode that its supposed to operate in.
     */
    public Cleaner() {
        monitor = new ProgressMonitor();
    }

    @Nonnull
    public ProgressMonitor getProgressMonitor() {
        return monitor;
    }

    public void clean() {
        executorService = Executors.newCachedThreadPool();
        monitor.setProgress(0);
        try {
            List<Path> filesToDelete = getRemovalTargets();
            deleteFiles(filesToDelete);
        } catch (IOException e) {
            LOGGER.warn("Failed to cleanup.", e);
        }
        executorService.shutdown();
    }

    private void deleteFiles(@Nonnull List<Path> files) throws IOException {
        int count = files.size();
        for (int i = 0; i < count; i++) {
            Path fileToDelete = files.get(i);
            Files.delete(fileToDelete);
            monitor.setProgress((float) i / count);
        }
        monitor.setProgress(1.f);
    }

    /**
     * This function creates a list of all files to be removed.
     *
     * @return the files that should be removed
     */
    @Nonnull
    private List<Path> getRemovalTargets() throws IOException {
        DirectoryManager dm = DirectoryManager.getInstance();

        List<Path> removalList = new ArrayList<>();

        FilenameFilter userDirFilter = new UserDirectoryFilenameFilter();

        Path userDir = dm.getDirectory(Directory.User);
        removalList.addAll(enlistRecursively(userDir, userDirFilter));

        Path dataDir = dm.getDirectory(Directory.Data);
        removalList.addAll(enlistArtifactsRecursively(dataDir));

        printFileList(removalList);
        return removalList;
    }

    @Nonnull
    private Collection<Path> enlistArtifactsRecursively(@Nonnull Path rootDir) throws IOException {
        if (executorService == null) {
            throw new IllegalStateException("Executor is not ready");
        }

        List<Path> artifactDirList = new LinkedList<>();
        if (isArtifactComplete(rootDir, artifactDirList)) {
            artifactDirList.add(rootDir);
        }

        Collection<Path> resultList = new LinkedList<>();
        Collection<Future<List<Path>>> artifactScans = new LinkedList<>();

        for (@Nonnull Path artifactDirectory : artifactDirList) {
            artifactScans.add(executorService.submit(() -> enlistOldArtifacts(artifactDirectory)));
        }

        for (Future<List<Path>> artifactScan : artifactScans) {
            try {
                resultList.addAll(artifactScan.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Failed to get results of directory scan.");
            }
        }

        return resultList;
    }

    private final Comparator<Path> versionComparator = new VersionComparator();

    private List<Path> enlistOldArtifacts(@Nonnull Path artifactDir) throws IOException {
        if (executorService == null) {
            throw new IllegalStateException("Executor is not ready");
        }

        List<Path> resultList = new LinkedList<>();
        List<Path> releaseList = new LinkedList<>();
        List<Path> snapshotList = new LinkedList<>();

        try (DirectoryStream<Path> subDirectories = Files.newDirectoryStream(artifactDir)) {
            for (@Nonnull Path versionDir : subDirectories) {
                if (Files.isDirectory(versionDir)) {
                    if (versionDir.toString().endsWith("SNAPSHOT")) {
                        snapshotList.add(versionDir);
                    } else {
                        releaseList.add(versionDir);
                    }
                }
            }
        }

        Collection<Future<List<Path>>> dirScans = new LinkedList<>();

        Collection<List<Path>> versionLists = new ArrayList<>();
        versionLists.add(releaseList);
        versionLists.add(snapshotList);
        for (@Nonnull List<Path> versionList : versionLists) {
            Collections.sort(versionList, versionComparator);
            while (versionList.size() > 1) {
                Path dir = versionList.remove(0);
                dirScans.add(executorService.submit(() -> {
                    if (isArtifactComplete(dir, null)) {
                        return enlistRecursively(dir, null);
                    }
                    return Collections.emptyList();
                }));
            }
        }

        if (snapshotList.size() == 1) {
            dirScans.add(executorService.submit(() -> enlistOldSnapshots(snapshotList.get(0))));
        }

        for (Future<List<Path>> dirScan : dirScans) {
            try {
                resultList.addAll(dirScan.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Failed to get results of directory scan.");
            }
        }

        return resultList;
    }

    @Nonnull
    private static List<Path> enlistOldSnapshots(@Nonnull Path snapshotDir) throws IOException {
        List<Path> snapshotJars = enlistFiles(snapshotDir, entry -> {
            String fileName = entry.getFileName().toString();
            return fileName.endsWith(".jar") && fileName.contains("SNAPSHOT");
        });
        if (snapshotJars.size() < 2) {
            return Collections.emptyList();
        }

        Collections.sort(snapshotJars);

        Collection<String> snapshotNames = snapshotJars.stream()
                .map(snapshotJar -> snapshotJar.getFileName().toString().replace(".jar", ""))
                .collect(Collectors.toList());

        return enlistFiles(snapshotDir, entry -> {
            String fileName = entry.getFileName().toString();
            for (@Nonnull String baseName : snapshotNames) {
                if (fileName.startsWith(baseName)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Nonnull
    private static List<Path> enlistFiles(@Nonnull Path rootPath, @Nonnull Filter<Path> filter)
            throws IOException {
        List<Path> snapshotJars = new ArrayList<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(rootPath, filter)) {
            for (Path file : files) {
                snapshotJars.add(file);
            }
        }
        return snapshotJars;
    }

    private static boolean isArtifactComplete(@Nonnull Path rootDir, @Nullable List<Path> artifactDirs)
            throws IOException {
        boolean noDirectories = true;
        boolean noJarFiles = true;

        try (DirectoryStream<Path> contentFiles = Files.newDirectoryStream(rootDir)) {
            for (Path contentFile : contentFiles) {
                if (contentFile.toString().endsWith(".jar")) {
                    noJarFiles = false;
                }
                if (Files.isDirectory(contentFile)) {
                    noDirectories = false;
                    if (artifactDirs != null) {
                        if (isArtifactComplete(contentFile, artifactDirs)) {
                            artifactDirs.add(rootDir);
                            return false;
                        }
                    }
                }
            }
        }

        return noDirectories && !noJarFiles;
    }

    private List<Path> enlistRecursively(@Nonnull Path rootDir, @Nullable FilenameFilter filter)
            throws IOException {
        if (executorService == null) {
            throw new IllegalStateException("Executor is not ready");
        }

        List<Path> resultList = new LinkedList<>();
        boolean removeDirectory = true;

        if (Files.isDirectory(rootDir)) {
            Collection<Future<List<Path>>> subDirScans = new ArrayList<>();
            try (DirectoryStream<Path> files = Files.newDirectoryStream(rootDir)) {
                for (Path contentFile : files) {
                    if (Files.isDirectory(contentFile)) {
                        subDirScans.add(executorService.submit(() -> enlistRecursively(contentFile, filter)));
                        removeDirectory = false;
                    } else {
                        if ((filter == null) || filter.accept(rootDir.toFile(), contentFile.getFileName().toString())) {
                            resultList.add(contentFile);
                        } else {
                            removeDirectory = false;
                        }
                    }
                }
                for (Future<List<Path>> subDirScan : subDirScans) {
                    try {
                        resultList.addAll(subDirScan.get());
                    } catch (InterruptedException | ExecutionException e) {
                        LOGGER.error("Failed to get results of directory scan.");
                    }
                }
            }
        }

        if (removeDirectory) {
            resultList.add(rootDir);
        }
        return resultList;
    }

    private static void printFileList(@Nonnull List<Path> files) throws IOException {
        long size = 0L;
        for (@Nonnull Path file : files) {
            size += Files.size(file);
            LOGGER.debug(file.toAbsolutePath().toString());
        }

        LOGGER.info("Files to delete: {} ({} Bytes)", files.size(), size);
    }
}
