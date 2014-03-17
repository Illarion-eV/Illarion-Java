package illarion.download.cleanup;

import illarion.common.util.DirectoryManager;
import illarion.common.util.EnvironmentDetect;
import illarion.common.util.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;

/**
 * This is the cleaner implementation of the downloader. While the downloader in general only downloads stuff and
 * stores it on the drive, this class is tasked with the cleanup process. Its able to delete old artifacts or just
 * delete everything in its way.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Cleaner {
    /**
     * These are the modes that can be applied to the cleaner.
     */
    public enum Mode {
        /**
         * The maintenance mode only removed old snapshots and old artifact version. Nothing that is required to
         * execute the applications is removed.
         */
        Maintenance,

        /**
         * Remove all binary files. This causes every single file in the data directory to be removed.
         */
        RemoveBinaries,

        /**
         * This mode causes a full cleanup of all data of Illarion. It will remove any and all files in the
         * directories assigned to Illarion.
         */
        RemoveEverything
    }

    /**
     * The logger that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Cleaner.class);

    /**
     * The mode the cleaner is working with
     */
    @Nonnull
    private final Mode selectedMode;

    /**
     * The executor service that is used to balance the load to find all required files across multiple threads.
     */
    @Nullable
    private ExecutorService executorService;

    @Nonnull
    private final ProgressMonitor monitor;

    /**
     * Create the cleaner and set the mode that its supposed to operate in.
     *
     * @param mode the cleaner mode
     */
    public Cleaner(@Nonnull final Mode mode) {
        selectedMode = mode;
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
            final List<Path> filesToDelete = getRemovalTargets();
            deleteFiles(filesToDelete);
        } catch (IOException e) {
            LOGGER.warn("Failed to cleanup.", e);
        }
        executorService.shutdown();

        if (selectedMode == Mode.RemoveBinaries || selectedMode == Mode.RemoveEverything) {
            try {
                deleteDownloader();
            } catch (URISyntaxException ignored) {
            }

            final DirectoryManager dm = DirectoryManager.getInstance();
            dm.unsetDirectory(DirectoryManager.Directory.Data);
            if (selectedMode == Mode.RemoveEverything) {
                dm.unsetDirectory(DirectoryManager.Directory.User);
            }
            dm.save();
        }
    }

    private void deleteDownloader() throws URISyntaxException {
        if (EnvironmentDetect.isWebstart()) {
            return;
        }

        final Path file = Paths.get(Cleaner.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(".jar")) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        Files.delete(file);
                    } catch (IOException ignored) {
                    }
                }
            });
        }
    }

    private void deleteFiles(@Nonnull final List<Path> files) throws IOException {
        final int count = files.size();
        for (int i = 0; i < count; i++) {
            final Path fileToDelete = files.get(i);
            Files.delete(fileToDelete);
            monitor.setProgress((float) i / (float) count);
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
        final DirectoryManager dm = DirectoryManager.getInstance();

        final List<Path> removalList = new ArrayList<>();

        final FilenameFilter userDirFilter;
        if (selectedMode == Mode.RemoveEverything) {
            userDirFilter = null;
        } else {
            userDirFilter = new UserDirectoryFilenameFilter();
        }


        final Path userDir = dm.getDirectory(DirectoryManager.Directory.User);
        if (userDir != null) {
            removalList.addAll(enlistRecursively(userDir, userDirFilter));
        }

        final Path dataDir = dm.getDirectory(DirectoryManager.Directory.Data);
        if (dataDir != null) {
            if (selectedMode == Mode.RemoveEverything || selectedMode == Mode.RemoveBinaries) {
                removalList.addAll(enlistRecursively(dataDir, null));
            } else {
                removalList.addAll(enlistArtifactsRecursively(dataDir));
            }
        }

        printFileList(removalList);
        return removalList;
    }

    private List<Path> enlistArtifactsRecursively(@Nonnull final Path rootDir) throws IOException {
        if (executorService == null) {
            throw new IllegalStateException("Executor is not ready");
        }

        final List<Path> artifactDirList = new LinkedList<>();
        if (isArtifactComplete(rootDir, artifactDirList)) {
            artifactDirList.add(rootDir);
        }

        final List<Path> resultList = new LinkedList<>();
        final List<Future<List<Path>>> artifactScans = new LinkedList<>();

        for (@Nonnull final Path artifactDirectory : artifactDirList) {
            artifactScans.add(executorService.submit(new Callable<List<Path>>() {
                @Override
                public List<Path> call() throws Exception {
                    return enlistOldArtifacts(artifactDirectory);
                }
            }));
        }

        for (final Future<List<Path>> artifactScan : artifactScans) {
            try {
                resultList.addAll(artifactScan.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Failed to get results of directory scan.");
            }
        }

        return resultList;
    }

    private final Comparator<Path> versionComparator = new VersionComparator();

    private List<Path> enlistOldArtifacts(@Nonnull final Path artifactDir) throws IOException {
        if (executorService == null) {
            throw new IllegalStateException("Executor is not ready");
        }

        final List<Path> resultList = new LinkedList<>();
        final List<Path> releaseList = new LinkedList<>();
        final List<Path> snapshotList = new LinkedList<>();

        try (DirectoryStream<Path> subDirectories = Files.newDirectoryStream(artifactDir)) {
            for (@Nonnull final Path versionDir : subDirectories) {
                if (Files.isDirectory(versionDir)) {
                    if (versionDir.toString().endsWith("SNAPSHOT")) {
                        snapshotList.add(versionDir);
                    } else {
                        releaseList.add(versionDir);
                    }
                }
            }
        }

        final List<Future<List<Path>>> dirScans = new LinkedList<>();

        final List<List<Path>> versionLists = new ArrayList<>();
        versionLists.add(releaseList);
        versionLists.add(snapshotList);
        for (@Nonnull final List<Path> versionList : versionLists) {
            Collections.sort(versionList, versionComparator);
            while (versionList.size() > 1) {
                final Path dir = versionList.remove(0);
                dirScans.add(executorService.submit(new Callable<List<Path>>() {
                    @Override
                    public List<Path> call() throws Exception {
                        if (isArtifactComplete(dir, null)) {
                            return enlistRecursively(dir, null);
                        }
                        return Collections.emptyList();
                    }
                }));
            }
        }

        if (snapshotList.size() == 1) {
            dirScans.add(executorService.submit(new Callable<List<Path>>() {
                @Override
                public List<Path> call() throws Exception {
                    return enlistOldSnapshots(snapshotList.get(0));
                }
            }));
        }

        for (final Future<List<Path>> dirScan : dirScans) {
            try {
                resultList.addAll(dirScan.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Failed to get results of directory scan.");
            }
        }

        return resultList;
    }

    private List<Path> enlistOldSnapshots(@Nonnull final Path snapshotDir) throws IOException {
        final List<Path> snapshotJars = enlistFiles(snapshotDir, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                final String fileName = entry.getFileName().toString();
                return fileName.endsWith(".jar") && fileName.contains("SNAPSHOT");
            }
        });
        if (snapshotJars.size() < 2) {
            return Collections.emptyList();
        }

        Collections.sort(snapshotJars);

        final List<String> snapshotNames = new ArrayList<>();
        for (Path snapshotJar : snapshotJars) {
            snapshotNames.add(snapshotJar.getFileName().toString().replace(".jar", ""));
        }

        return enlistFiles(snapshotDir, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                final String fileName = entry.getFileName().toString();
                for (@Nonnull final String baseName : snapshotNames) {
                    if (fileName.startsWith(baseName)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Nonnull
    private List<Path> enlistFiles(@Nonnull Path rootPath, @Nonnull DirectoryStream.Filter<Path> filter) throws
            IOException {
        final List<Path> snapshotJars = new ArrayList<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(rootPath, filter)) {
            for (Path file : files) {
                snapshotJars.add(file);
            }
        }
        return snapshotJars;
    }

    private boolean isArtifactComplete(@Nonnull final Path rootDir, @Nullable final List<Path> artifactDirs)
            throws IOException {
        boolean noDirectories = true;
        boolean noJarFiles = true;

        try (DirectoryStream<Path> contentFiles = Files.newDirectoryStream(rootDir)) {
            for (final Path contentFile : contentFiles) {
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

    private List<Path> enlistRecursively(@Nonnull final Path rootDir, @Nullable final FilenameFilter filter)
            throws IOException {
        if (executorService == null) {
            throw new IllegalStateException("Executor is not ready");
        }

        final List<Path> resultList = new LinkedList<>();
        boolean removeDirectory = true;

        if (Files.isDirectory(rootDir)) {
            final List<Future<List<Path>>> subDirScans = new ArrayList<>();
            try (DirectoryStream<Path> files = Files.newDirectoryStream(rootDir)) {
                for (final Path contentFile : files) {
                    if (Files.isDirectory(contentFile)) {
                        subDirScans.add(executorService.submit(new Callable<List<Path>>() {
                            @Override
                            public List<Path> call() throws Exception {
                                return enlistRecursively(contentFile, filter);
                            }
                        }));
                    } else {
                        if (filter == null || filter.accept(rootDir.toFile(), contentFile.getFileName().toString())) {
                            resultList.add(contentFile);
                        } else {
                            removeDirectory = false;
                        }
                    }
                }
                for (final Future<List<Path>> subDirScan : subDirScans) {
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

    private static void printFileList(@Nonnull final List<Path> files) throws IOException {
        long size = 0L;
        for (@Nonnull final Path file : files) {
            size += Files.size(file);
            LOGGER.debug(file.toAbsolutePath().toString());
        }

        LOGGER.info("Files to delete: {} ({} Bytes)", files.size(), size);
    }
}
