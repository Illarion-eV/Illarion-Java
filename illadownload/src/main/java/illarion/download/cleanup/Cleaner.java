package illarion.download.cleanup;

import illarion.common.util.DirectoryManager;
import illarion.common.util.EnvironmentDetect;
import illarion.common.util.ProgressMonitor;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FilenameFilter;
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
    private static final Logger LOGGER = Logger.getLogger(Cleaner.class);

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
        final List<File> filesToDelete = getRemovalTargets();
        deleteFiles(filesToDelete);
        executorService.shutdown();

        if (selectedMode == Mode.RemoveBinaries || selectedMode == Mode.RemoveEverything) {
            deleteDownloader();

            final DirectoryManager dm = DirectoryManager.getInstance();
            dm.unsetDirectory(DirectoryManager.Directory.Data);
            if (selectedMode == Mode.RemoveEverything) {
                dm.unsetDirectory(DirectoryManager.Directory.User);
            }
            dm.save();
        }
    }

    private void deleteDownloader() {
        if (EnvironmentDetect.isWebstart()) {
            return;
        }

        final File file = new File(Cleaner.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        if (file.exists() && file.isFile() && file.getName().endsWith(".jar")) {
            file.deleteOnExit();
        }
    }

    private void deleteFiles(@Nonnull final List<File> files) {
        final int count = files.size();
        for (int i = 0; i < count; i++) {
            final File fileToDelete = files.get(i);
            if (!fileToDelete.delete()) {
                fileToDelete.deleteOnExit();
            }
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
    private List<File> getRemovalTargets() {
        final DirectoryManager dm = DirectoryManager.getInstance();

        final List<File> removalList = new ArrayList<>();

        final FilenameFilter userDirFilter;
        if (selectedMode == Mode.RemoveEverything) {
            userDirFilter = null;
        } else {
            userDirFilter = new UserDirectoryFilenameFilter();
        }

        final File userDir = dm.getDirectory(DirectoryManager.Directory.User);
        if (userDir != null) {
            removalList.addAll(enlistRecursively(userDir, userDirFilter));
        }


        final File dataDir = dm.getDirectory(DirectoryManager.Directory.Data);
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

    private List<File> enlistArtifactsRecursively(@Nonnull final File rootDir) {
        if (executorService == null) {
            throw new IllegalStateException("Executor is not ready");
        }

        final List<File> artifactDirList = new LinkedList<>();
        if (isArtifactComplete(rootDir, artifactDirList)) {
            artifactDirList.add(rootDir);
        }

        final List<File> resultList = new LinkedList<>();
        final List<Future<List<File>>> artifactScans = new LinkedList<>();

        for (@Nonnull final File artifactDirectory : artifactDirList) {
            artifactScans.add(executorService.submit(new Callable<List<File>>() {
                @Override
                public List<File> call() throws Exception {
                    return enlistOldArtifacts(artifactDirectory);
                }
            }));
        }

        for (final Future<List<File>> artifactScan : artifactScans) {
            try {
                resultList.addAll(artifactScan.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Failed to get results of directory scan.");
            }
        }

        return resultList;
    }

    private final Comparator<File> versionComparator = new VersionComparator();

    private List<File> enlistOldArtifacts(@Nonnull final File artifactDir) {
        if (executorService == null) {
            throw new IllegalStateException("Executor is not ready");
        }

        final List<File> resultList = new LinkedList<>();
        final List<File> releaseList = new LinkedList<>();
        final List<File> snapshotList = new LinkedList<>();

        final File[] subDirs = artifactDir.listFiles();
        if (subDirs == null) {
            return resultList;
        }

        for (@Nonnull final File versionDir : subDirs) {
            if (versionDir.isDirectory()) {
                if (versionDir.getName().endsWith("SNAPSHOT")) {
                    snapshotList.add(versionDir);
                } else {
                    releaseList.add(versionDir);
                }
            }
        }

        final List<Future<List<File>>> dirScans = new LinkedList<>();

        final List<List<File>> versionLists = new ArrayList<>();
        versionLists.add(releaseList);
        versionLists.add(snapshotList);
        for (@Nonnull final List<File> versionList : versionLists) {
            Collections.sort(versionList, versionComparator);
            while (versionList.size() > 1) {
                final File dir = versionList.remove(0);
                dirScans.add(executorService.submit(new Callable<List<File>>() {
                    @Override
                    public List<File> call() throws Exception {
                        if (isArtifactComplete(dir, null)) {
                            return enlistRecursively(dir, null);
                        }
                        return Collections.EMPTY_LIST;
                    }
                }));
            }
        }

        if (snapshotList.size() == 1) {
            dirScans.add(executorService.submit(new Callable<List<File>>() {
                @Override
                public List<File> call() throws Exception {
                    return enlistOldSnapshots(snapshotList.get(0));
                }
            }));
        }

        for (final Future<List<File>> dirScan : dirScans) {
            try {
                resultList.addAll(dirScan.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Failed to get results of directory scan.");
            }
        }

        return resultList;
    }

    private List<File> enlistOldSnapshots(@Nonnull final File snapshotDir) {
        final File[] snapshotJars = snapshotDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar") && !name.contains("SNAPSHOT");
            }
        });
        if (snapshotJars == null || snapshotJars.length < 2) {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }

        Arrays.sort(snapshotJars);

        final List<String> snapshotNames = new LinkedList<>();
        for (int i = 0; i < snapshotJars.length - 1; i++) {
            final File snapshot = snapshotJars[i];
            snapshotNames.add(snapshot.getName().replace(".jar", ""));
        }

        final File[] snapshotFiles = snapshotDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                for (@Nonnull final String baseName : snapshotNames) {
                    if (name.startsWith(baseName)) {
                        return true;
                    }
                }
                return false;
            }
        });

        if (snapshotFiles == null) {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }

        return Arrays.asList(snapshotFiles);
    }

    private boolean isArtifactComplete(@Nonnull final File rootDir, @Nullable final List<File> artifactDirs) {
        final File[] contentFiles = rootDir.listFiles();
        if (contentFiles == null) {
            return true;
        }
        boolean noDirectories = true;
        boolean noJarFiles = true;

        for (final File contentFile : contentFiles) {
            if (contentFile.getName().endsWith(".jar")) {
                noJarFiles = false;
            }
            if (contentFile.isDirectory()) {
                noDirectories = false;
                if (artifactDirs != null) {
                    if (isArtifactComplete(contentFile, artifactDirs)) {
                        artifactDirs.add(rootDir);
                        return false;
                    }
                }
            }
        }

        return noDirectories && !noJarFiles;
    }

    private List<File> enlistRecursively(@Nonnull final File rootDir, @Nullable final FilenameFilter filter) {
        if (executorService == null) {
            throw new IllegalStateException("Executor is not ready");
        }

        final List<File> resultList = new LinkedList<>();
        boolean removeDirectory = true;

        if (rootDir.isDirectory()) {
            final File[] content = rootDir.listFiles();
            if (content != null) {
                final List<Future<List<File>>> subDirScans = new ArrayList<>();
                for (final File contentFile : content) {
                    if (contentFile.isDirectory()) {
                        subDirScans.add(executorService.submit(new Callable<List<File>>() {
                            @Override
                            public List<File> call() throws Exception {
                                return enlistRecursively(contentFile, filter);
                            }
                        }));
                    } else {
                        if (filter == null || filter.accept(rootDir, contentFile.getName())) {
                            resultList.add(contentFile);
                        } else {
                            removeDirectory = false;
                        }
                    }
                }
                for (final Future<List<File>> subDirScan : subDirScans) {
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

    private static void printFileList(@Nonnull final List<File> files) {
        long size = 0L;
        for (@Nonnull final File file : files) {
            size += file.length();
            System.out.println(file.getAbsolutePath());
        }

        System.out.println("Files to delete: " + files.size() + " (" + size + " Bytes)");

    }
}
