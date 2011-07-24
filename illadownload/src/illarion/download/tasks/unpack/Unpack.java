/*
 * This file is part of the Illarion Download Manager.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Download Manager is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Download Manager is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Download Manager. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.tasks.unpack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import illarion.common.util.DirectoryManager;
import illarion.common.util.Pack200Helper;
import illarion.common.util.lzma.LzmaInputStream;

import illarion.download.install.resources.ResourceManager;
import illarion.download.tasks.download.Download;
import illarion.download.tasks.download.DownloadResult;

/**
 * This class defines the task that actually takes care for unpacking the
 * resources to the hard drive.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.01
 */
public final class Unpack implements Callable<UnpackResult> {
    /**
     * The monitor that is applied to the monitor input stream that is used to
     * generate the callback to the other parts of the application.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    private static final class StreamMonitor implements
        CountBytesInputStream.Callback {
        /**
         * The full file size that is reported to the manager.
         */
        private final long fileSize;

        /**
         * The task this monitor is a part of.
         */
        private final Unpack parent;

        /**
         * The manager that is informed about the process.
         */
        private final UnpackManager unpackManager;

        /**
         * Create a new stream monitor and setup all required informations to
         * get it working properly.
         * 
         * @param upManager the manager that will be informed about the process
         * @param size the total amount of data that is reported to the manager
         * @param parentUnpack the unpack task that is the parent to this
         *            monitor
         */
        public StreamMonitor(final UnpackManager upManager, final long size,
            final Unpack parentUnpack) {
            unpackManager = upManager;
            fileSize = size;
            parent = parentUnpack;
        }

        @Override
        public void reportUpdate(final long newPos) {
            unpackManager.reportProgress(parent, newPos, fileSize);
        }

    }

    /**
     * The result of the download that is processed further.
     */
    private final DownloadResult downloadResult;

    /**
     * The file that is supposed to be unpacked
     */
    private final File file;

    /**
     * The manager that handles this task.
     */
    private final UnpackManager manager;

    /**
     * The human readable name of this task.
     */
    private final String name;

    /**
     * The directory that will store the unpacked files
     */
    private final File targetDir;

    /**
     * Create the unpack task based on a download that was handled before. This
     * unpacking task will try to extract the data downloaded.
     * 
     * @param download the download that is further processed by this task
     * @param result the result of the download
     * @param unpManager the unpacking manager that handles this task
     */
    Unpack(final Download download, final DownloadResult result,
        final UnpackManager unpManager) {
        file = download.getTarget();
        targetDir =
            new File(DirectoryManager.getInstance().getDataDirectory(),
                download.getDirectory());
        downloadResult = result;
        manager = unpManager;
        name = download.getName();
    }

    @Override
    public UnpackResult call() throws Exception {
        final UnpackResult result = callImpl();
        manager.reportFinished(this, result);
        return result;
    }

    /**
     * This is the implementation of the call function that is launched when
     * this task is executed.
     * 
     * @return the result of the unpacking operation
     * @throws Exception in case anything at all goes wrong
     */
    public UnpackResult callImpl() throws Exception {
        switch (downloadResult.getResult()) {
            case canceled:
                return new UnpackResult(name, UnpackResult.Results.canceled,
                    "unpack.cancled", file); //$NON-NLS-1$
            case downloadFailed:
                return new UnpackResult(name, UnpackResult.Results.canceled,
                    "unpack.downloadfailed", file); //$NON-NLS-1$
            case notModified:
                return new UnpackResult(name,
                    UnpackResult.Results.notModified,
                    "unpack.notmodified", file); //$NON-NLS-1$
            case downloaded:
                break;
        }

        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new IllegalStateException(
                "Can't create required directories."); //$NON-NLS-1$
        }

        CountBytesInputStream cIn = null;
        ZipInputStream zIn = null;
        final long fileSize = file.length();
        final long blockSize = Math.max(1024, fileSize / 100);

        final List<File> installedFiles = new ArrayList<File>();

        ReadableByteChannel inChannel = null;
        FileChannel outChannel = null;
        JarOutputStream jOutStream = null;
        try {
            cIn = new CountBytesInputStream(new FileInputStream(file));

            cIn.addCallback(new StreamMonitor(manager, fileSize, this));
            cIn.setCallbackInterval(blockSize);

            zIn =
                new ZipInputStream(new BufferedInputStream(
                    new LzmaInputStream(cIn)));
            inChannel = Channels.newChannel(zIn);

            ZipEntry currEntry = zIn.getNextEntry();
            long posInFile = 0;
            while (currEntry != null) {
                final String entryName = currEntry.getName();
                boolean pack200 = false;
                if (entryName.endsWith(".pack")) { //$NON-NLS-1$
                    pack200 = true;
                }
                final File targetFile = new File(targetDir, entryName);
                final String fullPath = targetFile.getAbsolutePath();

                new File(fullPath.substring(0,
                    fullPath.lastIndexOf(File.separatorChar))).mkdirs();

                outChannel = new FileOutputStream(targetFile).getChannel();
                posInFile = 0;
                while (zIn.available() > 0) {
                    posInFile +=
                        outChannel
                            .transferFrom(inChannel, posInFile, fileSize);
                }
                outChannel.close();
                outChannel = null;

                if (pack200) {
                    final File targetFileP200 =
                        new File(targetDir, entryName.substring(0,
                            entryName.length() - 5));
                    final Pack200.Unpacker p200unpacker =
                        Pack200Helper.getUnpacker();
                    installedFiles.add(targetFileP200);

                    jOutStream =
                        new JarOutputStream(new FileOutputStream(
                            targetFileP200));
                    p200unpacker.unpack(targetFile, jOutStream);

                    jOutStream.finish();
                    jOutStream.flush();
                    jOutStream.close();
                    jOutStream = null;

                    if (!targetFile.delete()) {
                        targetFile.deleteOnExit();
                    }
                } else {
                    installedFiles.add(targetFile);
                }

                currEntry = zIn.getNextEntry();
            }
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (jOutStream != null) {
                jOutStream.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
            if (zIn != null) {
                zIn.close();
            }
        }

        final URL downloadURL = downloadResult.getSource();
        ResourceManager.getInstance().reportResourceInstalled(downloadURL,
            downloadResult.getLastModified());

        for (final File currentFile : installedFiles) {
            ResourceManager.getInstance().reportFileInstalled(downloadURL,
                currentFile);
        }

        return new UnpackResult(name, UnpackResult.Results.unpacked,
            "unpack.unpacked", file); //$NON-NLS-1$
    }

    /**
     * Get the name of this unpacking task. This name is displayed in the GUI.
     * 
     * @return the name of this task
     */
    public String getName() {
        return name;
    }
}
