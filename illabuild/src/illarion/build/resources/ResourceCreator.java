/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Build Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build.resources;

import illarion.common.util.Pack200Helper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.*;
import org.tukaani.xz.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Pack200;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * This class is used to create resource bundles that contain the Illarion
 * applications and get downloaded.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ResourceCreator extends Task {
    /**
     * The extension that is added to the file in case the pack200 compression
     * is applied.
     */
    private static final String PACK200_EXT = ".pack"; //$NON-NLS-1$

    /**
     * Print a list of files that were copied into this resource.
     */
    private boolean fileList;

    /**
     * The file lists that are supposed to be included into the resource.
     */
    private final List<FileList> filelists;

    /**
     * The file sets that are supposed to be included into the resource.
     */
    private final List<AbstractFileSet> filesets;

    /**
     * The file sets that are supposed to be included into the resource.
     */
    private final List<AbstractFileSet> nativeFilesets;

    /**
     * The file that will store the created resource.
     */
    private File targetFile;

    /**
     * This variable to set to {@code true} in case pack200 is supposed to
     * be used to compress the data further.
     */
    private boolean useP200;

    /**
     * This variable marks the trial of the creator. Different trials with step by step lower the efficiency of the
     * compressor in order to get a valid build.
     */
    private int trial = 0;

    /**
     * The default constructor that prepares this class for proper operation.
     */
    public ResourceCreator() {
        filesets = new ArrayList<AbstractFileSet>();
        nativeFilesets = new ArrayList<AbstractFileSet>();
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
    public void addNativeFileset(final ZipFileSet set) {
        nativeFilesets.add(set);
    }

    /**
     * Execute the task and pack the jar files that are specified.
     */
    @Override
    public void execute() throws BuildException {
        trial++;
        try {
            validate();

            if (targetFile.exists() && !targetFile.delete()) {
                throw new BuildException(
                        "Can't access the target file properly"); //$NON-NLS-1$
            }

            final LZMA2Options lzmaOptions;
            final X86Options x86Options;
            int usedCheck = XZ.CHECK_CRC64;

            try {
                final int presetTrial = (trial - 1) % (LZMA2Options.PRESET_MAX + 1);
                final int usedPreset = LZMA2Options.PRESET_MAX - presetTrial;
                lzmaOptions = new LZMA2Options(usedPreset);

                switch ((trial - 1) / (LZMA2Options.PRESET_MAX + 1)) {
                    case 0:
                        usedCheck = XZ.CHECK_CRC64;
                        break;
                    case 1:
                        usedCheck = XZ.CHECK_SHA256;
                        break;
                    case 2:
                        usedCheck = XZ.CHECK_CRC32;
                        break;
                    case 3:
                        if (useP200) {
                            useP200 = false;
                            trial = 0;
                            execute();
                            return;
                        }
                        System.err.println("No more trials left to build resource file. Build failed.");
                        throw new BuildException("Failed to create resource bundle: " + targetFile.getName());
                }
                x86Options = new X86Options();
            } catch (UnsupportedOptionsException e1) {
                throw new BuildException("Failed to setup compressor.");
            }

            final FilterOptions[] defaultOptions = {lzmaOptions};
            final FilterOptions[] nativeOptions = {x86Options, lzmaOptions};

            ZipOutputStream zOut = null;

            try {
                final FileOutputStream fOut = new FileOutputStream(targetFile);
                final XZOutputStream xOut = new XZOutputStream(fOut, defaultOptions, usedCheck);
                final BufferedOutputStream bOut = new BufferedOutputStream(xOut);
                zOut = new ZipOutputStream(bOut);

                zOut.setLevel(0);
                zOut.setMethod(ZipEntry.DEFLATED);

                for (final AbstractFileSet fileset : filesets) {
                    final DirectoryScanner ds =
                            fileset.getDirectoryScanner(getProject());

                    Resource res = null;
                    for (final String fileName : ds.getIncludedFiles()) {
                        res = ds.getResource(fileName);
                        InputStream in = null;
                        try {
                            in = res.getInputStream();
                            handleStream(in, res.getName(), zOut);
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                        }
                    }
                }

                for (final FileList filelist : filelists) {
                    final File dir = filelist.getDir(getProject());
                    for (final String fileName : filelist
                            .getFiles(getProject())) {

                        InputStream in = null;
                        try {
                            in = new FileInputStream(new File(dir, fileName));
                            handleStream(in, fileName, zOut);
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                        }
                    }
                }

                final boolean orgUseP200 = useP200;
                useP200 = false;
                boolean firstFile = true;
                for (final AbstractFileSet fileset : nativeFilesets) {
                    final DirectoryScanner ds =
                            fileset.getDirectoryScanner(getProject());

                    Resource res = null;
                    for (final String fileName : ds.getIncludedFiles()) {
                        try {
                            res = ds.getResource(fileName);
                            final ZipInputStream nativeIn =
                                    new ZipInputStream(res.getInputStream());
                            ZipEntry nativeEntry = nativeIn.getNextEntry();
                            while (nativeEntry != null) {
                                if (!nativeEntry.isDirectory()
                                        && isNativeFilename(nativeEntry.getName())) {
                                    if (firstFile) {
                                        firstFile = false;
                                        zOut.flush();
                                        xOut.endBlock();
                                        xOut.updateFilters(nativeOptions);
                                    }
                                    handleStream(nativeIn,
                                            nativeEntry.getName(), zOut);
                                    nativeIn.closeEntry();
                                }
                                nativeEntry = nativeIn.getNextEntry();
                            }
                            nativeIn.close();
                        } catch (final Exception ex) {
                            System.out.println("Native resource " + fileName //$NON-NLS-1$
                                    + " not detected as such."); //$NON-NLS-1$
                            ex.printStackTrace();
                        }
                    }
                }

                zOut.flush();
                zOut.finish();

                useP200 = orgUseP200;
            } catch (final FileNotFoundException e) {
                throw new BuildException(e);
            } catch (final IOException e) {
                throw new BuildException(e);
            } finally {
                closeStream(zOut);
            }

        } catch (final BuildException e) {
            e.printStackTrace();
            throw e;
        }

        /** Checking the created file. */
        BufferedInputStream bInStream = null;
        try {
            bInStream = new BufferedInputStream(new XZInputStream(new FileInputStream(targetFile)));
            final byte[] tempArray = new byte[2048];
            while (bInStream.read(tempArray) != -1) {
            }
        } catch (Exception e) {
            System.out.println("Trial #" + Integer.toString(trial) + " Compressed data was corrupted ("
                    + e.getMessage() + "). Trying compression again at less aggressive levels.");
            closeStream(bInStream);
            execute();
            return;
        } finally {
            closeStream(bInStream);
        }

        System.out.print("Creating " + targetFile.getName() + " is done."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static void closeStream(final Closeable outStream) {
        if (outStream != null) {
            try {
                outStream.close();
            } catch (final IOException e) {
                // nothing
            }
        }
    }

    /**
     * This class is used to dump the files that were copied into the created
     * resource.
     *
     * @param value the new value of this flag
     */
    public void setDumpFileList(final boolean value) {
        fileList = value;
    }

    /**
     * Set the target file.
     *
     * @param file the target file
     */
    public void setTarget(final File file) {
        targetFile = file;
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
     * Handle one stream regarding the settings done and add it to the resource
     * output stream.
     *
     * @param sourceStream the source stream that is processed by this class
     * @param fileName     the name of the file to process
     * @param outputStream the output stream that receives the data
     * @throws IOException in case the file operations go wrong
     */
    private void handleStream(final InputStream sourceStream,
                              final String fileName, final ZipOutputStream outputStream)
            throws IOException {
        if (fileList) {
            System.out.println("Added file: " + fileName); //$NON-NLS-1$
        }
        String extension = ""; //$NON-NLS-1$
        if (useP200) {
            extension = PACK200_EXT;
        }
        final ZipEntry zEntry = new ZipEntry(fileName + extension);
        outputStream.putNextEntry(zEntry);

        File tempFile = streamToTempFile(sourceStream);

        FileChannel tempFileChannel = null;
        WritableByteChannel outChannel = null;
        OutputStream out = null;

        if (useP200) {
            final Pack200.Packer packer = Pack200Helper.getPacker();
            final File p200tempFile =
                    File.createTempFile("packing", ".pack200.tmp"); //$NON-NLS-1$ //$NON-NLS-2$
            tempFile.deleteOnExit();

            out = new BufferedOutputStream(new FileOutputStream(p200tempFile));
            packer.pack(new JarFile(tempFile), out);
            out.flush();
            out.close();

            tempFile = p200tempFile;
        }

        tempFileChannel = new FileInputStream(tempFile).getChannel();
        final long size = tempFileChannel.size();
        outChannel = Channels.newChannel(outputStream);

        long position = 0L;
        while (position < size) {
            position += tempFileChannel.transferTo(position, size - position, outChannel);
        }

        outputStream.flush();
        outputStream.closeEntry();
    }

    /**
     * This function checks if a file name has a valid file extension for a
     * native file.
     *
     * @param name the name to check
     * @return <code>true</code> if the name is valid
     */
    private boolean isNativeFilename(final String name) {
        if (name.endsWith(".dll")) { //$NON-NLS-1$
            return true;
        }
        if (name.endsWith(".so")) { //$NON-NLS-1$
            return true;
        }
        if (name.endsWith(".jnilib")) { //$NON-NLS-1$
            return true;
        }
        return false;
    }

    /**
     * The count of bytes maximal transfered during copy operations.
     */
    private static final int MAX_TRANSFER_SIZE = 1000000;

    /**
     * This function copies a stream to a temporary file that is marked for
     * deletion automatically.
     *
     * @param in the input stream, this stream will not be closed automatically
     * @return the pointer to the temporary file
     * @throws IOException in case anything goes wrong
     */
    private File streamToTempFile(final InputStream in) throws IOException {
        final File tempFile = File.createTempFile("packing", ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$
        tempFile.deleteOnExit();

        ReadableByteChannel inChannel = null;
        FileChannel outChannel = null;

        System.gc();

        try {
            inChannel = Channels.newChannel(in);
            outChannel = new FileOutputStream(tempFile).getChannel();

            if (inChannel instanceof FileChannel) {
                final FileChannel fInChannel = (FileChannel) inChannel;

                final long size = fInChannel.size();
                long position = 0;
                while (position < size) {
                    position +=
                            fInChannel.transferTo(position,
                                    Math.min(MAX_TRANSFER_SIZE, size - position),
                                    outChannel);
                }
            } else {
                final ByteBuffer tempBuffer =
                        ByteBuffer.allocateDirect(MAX_TRANSFER_SIZE);

                boolean eof = false;
                while (!eof) {
                    eof = (inChannel.read(tempBuffer) < 0);
                    tempBuffer.flip();

                    while (tempBuffer.hasRemaining()) {
                        outChannel.write(tempBuffer);
                    }
                    tempBuffer.clear();
                }
            }
        } finally {
            if (outChannel != null) {
                outChannel.close();
            }
        }

        return tempFile;
    }

    /**
     * Check if the settings of this task are good to be executed.
     *
     * @throws BuildException in case anything at the settings for this task is
     *                        wrong
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
