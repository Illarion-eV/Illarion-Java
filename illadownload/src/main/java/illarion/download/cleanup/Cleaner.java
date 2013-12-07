package illarion.download.cleanup;

import illarion.common.util.DirectoryManager;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    /**
     * Create the cleaner and set the mode that its supposed to operate in.
     *
     * @param mode the cleaner mode
     */
    public Cleaner(@Nonnull final Mode mode) {
        selectedMode = mode;
    }

    public void clean() {
        executorService = Executors.newCachedThreadPool();
        getRemovalTargets();
        executorService.shutdown();
    }

    public static void main(final String[] args) {
        new Cleaner(Mode.Maintenance).clean();
    }

    /**
     * This function creates a list of all files to be removed.
     *
     * @return the files that should be removed
     */
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
            }
        }

        printFileList(removalList);
        return removalList;
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
        for (@Nonnull final File file : files) {
            System.out.println(file.getAbsolutePath());
        }
    }
}
