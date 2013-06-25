/*
 * This file is part of the Illarion Download Utility.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Download Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Download Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Download Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.tasks.download;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class contains the download manager of the Illarion application download
 * and run system. This manager is the key to the fast download of the Illarion
 * applications.
 *
 * @author Martin Karing
 */
public final class DownloadManager {
    /**
     * The list of objects that are informed in case the state of a download
     * changed.
     */
    @Nonnull
    private final List<DownloadCallback> callbacks;

    /**
     * The executor service that generates the threads each download is handled
     * in.
     */
    private final ExecutorService service;

    /**
     * Create a new download manager that is able to maintain the download of
     * many files.
     */
    public DownloadManager() {
        service = Executors.newFixedThreadPool(1);
        callbacks = new ArrayList<DownloadCallback>();

        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (InetAddress.getByName("illarion.org").isReachable(4000)) {
                        connected = ConnectionState.Connected;
                    } else {
                        connected = ConnectionState.Disconnected;
                    }
                } catch (IOException e) {
                    connected = ConnectionState.Disconnected;
                }
            }
        });
    }

    /**
     * Add a objects that is notified about the progress of every single
     * download that is in progress.
     *
     * @param callback the callback object
     */
    public void addDownloadProgressListener(final DownloadCallback callback) {
        callbacks.add(callback);
    }

    /**
     * Remove a download progress listener. This class won't be notified anymore
     * about any changes to running downloads.
     *
     * @param callback the object that is to be removed from the list
     */
    public void removeDownloadProgressListener(final DownloadCallback callback) {
        callbacks.remove(callback);
    }

    /**
     * Report to all listeners that the download has finished.
     *
     * @param download the download that finished
     * @param result   the result of the download
     */
    public void reportDownloadFinished(final Download download,
                                       final DownloadResult result) {
        if (!callbacks.isEmpty()) {
            final int count = callbacks.size();
            for (int i = 0; i < count; i++) {
                callbacks.get(i).reportDownloadFinished(download, result);
            }
        }
    }

    /**
     * This function is called by the different downloads to update their state.
     *
     * @param download   the download that reports the progress
     * @param bytesDone  the amount of bytes of this download now done in total
     * @param bytesTotal the amount of total bytes for this download
     */
    public void reportProgress(final Download download, final long bytesDone,
                               final long bytesTotal) {
        if (!callbacks.isEmpty()) {
            final int count = callbacks.size();
            for (int i = 0; i < count; i++) {
                callbacks.get(i).reportDownloadProgress(download, bytesDone, bytesTotal);
            }
        }
    }

    /**
     * This enumerator stores the possible values of the connection test.
     */
    private enum ConnectionState {
        /**
         * This constant means that the connection test is not done yet.
         */
        NotTested,

        /**
         * This constant means that the connection test is done and the connection does not seem active.
         */
        Disconnected,

        /**
         * This constant means that the test is done and the connection seems working
         */
        Connected
    }

    /**
     * This flag is set {@code true} once a active connection is confirmed.
     */
    @Nonnull
    private ConnectionState connected = ConnectionState.NotTested;

    /**
     * Check if there is a proper connection to the internet.
     *
     * @return {@code true} in case the downloader is able to access the internet
     */
    public boolean isConnected() {
        while (true) {
            switch (connected) {
                case Connected:
                    return true;
                case Disconnected:
                    return false;
                case NotTested:
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // does not matter
                    }
                    break;
            }
        }
    }

    /**
     * This functions places a new download in the manager.
     *
     * @param title        the title of the download file
     * @param dir          the sub directory the downloaded file need to be extracted to
     * @param source       the link to the file in the web that is supposed to be downloaded
     * @param target       the location the file is downloaded to
     * @param lastModified the timestamp when the file was last modified
     */
    public void scheduleDownload(final String title, final String dir,
                                 final URL source, final File target, final long lastModified) {
        final Download newDownload = new Download(title, dir, source, target, lastModified, this);
        if (newDownload.prepare()) {
            service.submit(newDownload);
        }
    }

    /**
     * Stop the download manager once all requested files are downloaded.
     */
    public void shutdown() {
        service.shutdown();
        try {
            service.awaitTermination(24, TimeUnit.DAYS);
        } catch (@Nonnull final InterruptedException e) {
            // nothing to do
        }
    }
}
