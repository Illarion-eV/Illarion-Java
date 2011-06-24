/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Build Utility. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build.pack200;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.Pack200;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * This is the main task class that handles the Pack200 compression for the JAR
 * files.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class Pack200Task extends Task {
    /**
     * This is the task that is executed to process one file.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    private static final class FileTask implements Runnable {
        /**
         * The file that is processed.
         */
        private final File file;

        /**
         * The engine that is used to process the file.
         */
        private final PackingEngine pack;

        /**
         * In case this flag is set to <code>true</code> to file will be
         * repacked and not just packed
         */
        private final boolean repack;

        /**
         * Create a new instance of this file task to process one file.
         * 
         * @param packEngine the engine used to pack the file
         * @param repackingFlag <code>true</code> in case the file is needed to
         *            be repacked
         * @param processedFile the file that is processed
         */
        public FileTask(final PackingEngine packEngine,
            final boolean repackingFlag, final File processedFile) {
            pack = packEngine;
            repack = repackingFlag;
            file = processedFile;
        }

        /**
         * Execute this task.
         */
        @SuppressWarnings("nls")
        @Override
        public void run() {
            if (repack) {
                System.out.println("Repacking JAR: " + file);
                pack.repack(file);
            } else {
                System.out.println("Packing JAR: " + file);
                pack.pack(file);
            }
        }

    }

    /**
     * The engine that takes care of the packing operations themselves.
     */
    private final PackingEngine engine;

    /**
     * This list stores the file sets that are set to this task. This file sets
     * contain the references to all files that are needed to be processed.
     */
    private final List<FileSet> filesets;

    /**
     * This value stores if the task is supposed to pack or the repack the
     * archive.
     */
    private boolean repacking = false;

    /**
     * The source file that is supposed to be packed.
     */
    private File sourceFile;

    /**
     * Create a new instance of this pack200 task that takes care for handling
     * pack200 archives.
     */
    public Pack200Task() {
        engine = new PackingEngine();
        filesets = new ArrayList<FileSet>();

        // Default values
        setKeepModificationTime(true);
        setSingleSegment(false);
        setKeepOrder(false);
    }

    /**
     * Check if a file is available and readable.
     * 
     * @param file the file to check
     * @throws BuildException in case the file does not exist or is not readable
     */
    @SuppressWarnings("nls")
    private static void checkFile(final File file) throws BuildException {
        if (!file.canRead() || !file.isFile()) {
            throw new BuildException("File does not exist or can't be read: "
                + file);
        }
    }

    /**
     * Adds a set of files to copy.
     * 
     * @param set a set of files to copy
     */
    public void addFileset(final FileSet set) {
        filesets.add(set);
    }

    /**
     * Execute the task and pack the jar files that are specified.
     */
    @Override
    public void execute() throws BuildException {
        validate();

        final List<File> files = new ArrayList<File>();
        if (sourceFile != null) {
            checkFile(sourceFile);
            files.add(sourceFile);
        } else {
            for (final FileSet fs : filesets) {
                final DirectoryScanner ds =
                    fs.getDirectoryScanner(getProject());

                final File dir = fs.getDir(getProject());
                for (final String fileName : ds.getIncludedFiles()) {
                    final File newFile = new File(dir, fileName);

                    checkFile(newFile);
                    files.add(newFile);
                }
            }
        }

        final ExecutorService exe =
            Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors());
        for (final File f : files) {
            exe.submit(new FileTask(engine, repacking, f));
        }
        exe.shutdown();
        try {
            exe.awaitTermination(1, TimeUnit.HOURS);
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Set the directory that is the destination. All packed files will be
     * created there.
     * 
     * @param dir the destination directory
     */
    public void setDestdir(final File dir) {
        engine.setDestinationDir(dir);
    }

    /**
     * Set the keep modification flag. In case set to <code>true</code> the
     * modification time of the files will be preserved. In case its not set the
     * time stamp of all files will be set to the value of the last created
     * file.
     * 
     * @param enabled <code>true</code> to keep the modification times
     */
    public void setKeepModificationTime(final boolean enabled) {
        if (enabled) {
            engine.setProperty(Pack200.Packer.MODIFICATION_TIME,
                Pack200.Packer.KEEP);
        }
        engine.setProperty(Pack200.Packer.MODIFICATION_TIME,
            Pack200.Packer.LATEST);
    }

    /**
     * Set the keep order flag. In case the order of the file is not kept its
     * likely that the resulting file will be smaller but features like indexing
     * the jar won't work anymore.
     * 
     * @param enabled <code>true</code> to ensure that the order of the files is
     *            kept
     */
    public void setKeepOrder(final boolean enabled) {
        if (enabled) {
            engine.setProperty(Pack200.Packer.KEEP_FILE_ORDER,
                Pack200.Packer.TRUE);
        }
        engine.setProperty(Pack200.Packer.KEEP_FILE_ORDER,
            Pack200.Packer.FALSE);
    }

    /**
     * Set the task to repacking. The result is that the file will be packed and
     * unpacked again right away.
     * 
     * @param repack <code>true</code> to make the task repacking
     */
    public void setRepack(final boolean repack) {
        repacking = repack;
    }

    /**
     * Set the segment size of the packed file. The larger the better the
     * compression rate and the larger the requirements to unpack it.
     * 
     * @param size the new maximal size of a segment in bytes
     */
    public void setSegmentLimit(final int size) {
        engine.setProperty(Pack200.Packer.SEGMENT_LIMIT,
            Integer.toString(size));
    }

    /**
     * Set the archive to use a single segment or not. In case enabled the
     * entire file will be stored in one segment. For large files that results
     * in very high requirements to unpack the archive, how ever it will lead to
     * a better compression rate.
     * 
     * @param enabled <code>true</code> to pack the file into a single segment
     */
    public void setSingleSegment(final boolean enabled) {
        if (enabled) {
            setSegmentLimit(-1);
        }
        setSegmentLimit(1000000);
    }

    /**
     * Set the file that is supposed to be packed.
     * 
     * @param file the file that is processed
     */
    public void setSrcfile(final File file) {
        sourceFile = file;
    }

    /**
     * Check if the settings of this task are good to be executed.
     * 
     * @throws BuildException in case anything at the settings for this task is
     *             wrong
     */
    @SuppressWarnings("nls")
    private void validate() throws BuildException {
        if ((sourceFile == null) && (filesets.size() == 0)) {
            throw new BuildException("need to specify either file or fileset");
        }
        if ((sourceFile != null) && (filesets.size() > 0)) {
            throw new BuildException("can't specify both file and fileset");
        }
    }
}
