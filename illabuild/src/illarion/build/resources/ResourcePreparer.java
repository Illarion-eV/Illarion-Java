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
package illarion.build.resources;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;

import illarion.common.util.Pack200Helper;

/**
 * This class is a ant task that is used to prepare resources for the resource
 * creator. It is mainly used to convert the resources as needed before they get
 * signed and load into the resource bundles. In case no task is needed to be
 * done this task does simply copy the files in question. No matter the settings
 * the files created will hold exactly the same name as before.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ResourcePreparer extends Task {
    /**
     * The file lists that are supposed to be included into the resource.
     */
    private final List<FileList> filelists;

    /**
     * The file sets that are supposed to be included into the resource.
     */
    private final List<AbstractFileSet> filesets;

    /**
     * The file that will store the created resource.
     */
    private File targetFile;

    /**
     * This variable to set to <code>true</code> in case pack200 is supposed to
     * be used to compress the data further.
     */
    private boolean useP200;

    /**
     * The default constructor that prepares this class for proper operation.
     */
    public ResourcePreparer() {
        filesets = new ArrayList<AbstractFileSet>();
        filelists = new ArrayList<FileList>();
        useP200 = false;
    }

    /**
     * Adds a list of files to process.
     * 
     * @param list a list of files to process
     */
    public void addFilelist(final FileList list) {
        filelists.add(list);
    }

    /**
     * Adds a set of files to process.
     * 
     * @param set a set of files to process
     */
    public void addFileset(final FileSet set) {
        filesets.add(set);
    }

    /**
     * Adds a ZIP file set to process to the creator.
     * 
     * @param set a set of files to process
     */
    public void addZipfileset(final ZipFileSet set) {
        filesets.add(set);
    }

    /**
     * Set the group of ZIP files. All ZIP files in the file set are added to
     * the main list of file sets.
     * 
     * @param set the set of zip files
     */
    public void addZipGroupFileset(final FileSet set) {
        final DirectoryScanner ds = set.getDirectoryScanner(getProject());

        final File dir = set.getDir(getProject());
        for (final String fileName : ds.getIncludedFiles()) {
            final ZipFileSet tSet = new ZipFileSet();
            tSet.setProject(set.getProject());
            tSet.setSrc(new File(dir, fileName));
            filesets.add(tSet);
        }
    }

    /**
     * Execute the task and pack the jar files that are specified.
     */
    @Override
    public void execute() throws BuildException {
        validate();

        int counter = 0;

        try {
            for (final AbstractFileSet fileset : filesets) {
                final DirectoryScanner ds =
                    fileset.getDirectoryScanner(getProject());

                final File dir = fileset.getDir(getProject());
                for (final String fileName : ds.getIncludedFiles()) {
                    try {
                        processFile(new File(dir, fileName));
                        counter++;
                    } catch (final IOException e) {
                        System.err.println("Problem with File: " + fileName); //$NON-NLS-1$
                    }
                }
            }

            for (final FileList filelist : filelists) {
                final File dir = filelist.getDir(getProject());
                for (final String fileName : filelist.getFiles(getProject())) {
                    try {
                        processFile(new File(dir, fileName));
                        counter++;
                    } catch (final IOException e) {
                        System.err.println("Problem with File: " + fileName); //$NON-NLS-1$
                    }
                }
            }
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw e;
        }

        System.out.println("Prepared " + Integer.toString(counter) + " Files"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Set the target file.
     * 
     * @param dir the directory where the target files are supposed to be moved
     */
    public void setTargetDir(final File dir) {
        targetFile = dir;
    }

    /**
     * Set the flag if the input jar files are supposed to be compressed even
     * further using the pack200 compression.
     * 
     * @param value the new value of this flag
     */
    public void setUsePack200(final boolean value) {
        useP200 = value;
    }

    /**
     * Process a single file.
     * 
     * @param file the file to process
     * @throws IOException in case anything goes wrong
     */
    private void processFile(final File file) throws IOException {
        if (useP200) {
            final int count = processFileP200(file, 0L);
            if (count > 1) {
                System.out.println("Required " + Integer.toString(count) //$NON-NLS-1$
                    + " rounds stabilise " + file.getName()); //$NON-NLS-1$
            }
        } else {
            processFileNormal(file);
        }
    }

    /**
     * This is the function that processes the files with normal settings.
     * Currently that just copies the file to its new location.
     * 
     * @param file the file to process
     * @throws IOException in case anything goes wrong
     */
    private void processFileNormal(final File file) throws IOException {
        final File destFile = new File(targetFile, file.getName());

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(file).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    /**
     * This is the function that processes the files with pack200 compression.
     * The resulting effect is that this file is packed and unpacked again to
     * the destination directory. This will be repeated until the file does not
     * change anymore.
     * 
     * @param file the file to process
     * @param lastSize the last size of the file, set this parameter at the
     *            first call to 0. It is used to the recursive calls of this
     *            function
     * @throws IOException in case anything goes wrong
     */
    private int processFileP200(final File file, final long lastSize)
        throws IOException {
        final File tempFile = File.createTempFile("p200temp", ".pack.tmp"); //$NON-NLS-1$ //$NON-NLS-2$
        final File destFile = new File(targetFile, file.getName());

        final Pack200.Packer packer = Pack200Helper.getPacker();
        final Pack200.Unpacker unpacker = Pack200Helper.getUnpacker();

        OutputStream out = null;
        JarOutputStream jOut = null;

        try {
            out = new BufferedOutputStream(new FileOutputStream(tempFile));
            packer.pack(new JarFile(file), out);
            out.flush();
            out.close();
            out = null;

            jOut =
                new JarOutputStream(new BufferedOutputStream(
                    new FileOutputStream(destFile)));
            unpacker.unpack(tempFile, jOut);
            jOut.finish();
            jOut.flush();
            jOut.close();
            jOut = null;
        } finally {
            if (out != null) {
                out.close();
            }
            if (jOut != null) {
                jOut.close();
            }
        }

        if (destFile.length() != lastSize) {
            return processFileP200(file, destFile.length()) + 1;
        }
        return 0;
    }

    /**
     * Check if the settings of this task are good to be executed.
     * 
     * @throws BuildException in case anything at the settings for this task is
     *             wrong
     */
    @SuppressWarnings("nls")
    private void validate() throws BuildException {
        if (targetFile == null) {
            throw new BuildException("a target file is required");
        }
        if (filesets.isEmpty() && filelists.isEmpty()) {
            throw new BuildException("input files is required");
        }
    }
}
