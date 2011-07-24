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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import illarion.download.tasks.download.Download;
import illarion.download.tasks.download.DownloadCallback;
import illarion.download.tasks.download.DownloadResult;

/**
 * The task of this class is to unpack the downloaded resource file so the
 * content becomes usable for java.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class UnpackManager implements DownloadCallback {
    /**
     * This is the list of callback classes that need to be notified in case the
     * state of a unpacking operation changes.
     */
    private final List<UnpackCallback> callbacks;

    /**
     * The executor service that generates the threads the unpacking tasks are
     * processed in.
     */
    private final ExecutorService service;

    /**
     * Public constructor for the unpacking manager. This will prepare the
     * manager to work properly.
     */
    public UnpackManager() {
        service = Executors.newFixedThreadPool(1);
        callbacks = new ArrayList<UnpackCallback>();
    }

    /**
     * Add a objects that is notified about the progress of every single
     * download that is in progress.
     * 
     * @param callback the callback object
     */
    public void addUnpackProgressListener(final UnpackCallback callback) {
        callbacks.add(callback);
    }

    /**
     * Remove a download progress listener. This class won't be notified anymore
     * about any changes to running downloads.
     * 
     * @param callback the object that is to be removed from the list
     */
    public void removeUnpackProgressListener(final UnpackCallback callback) {
        callbacks.remove(callback);
    }

    @Override
    public void reportDownloadFinished(final Download download,
        final DownloadResult result) {
        final Unpack task = new Unpack(download, result, this);
        service.submit(task);
    }

    @Override
    public void reportDownloadProgress(final Download download,
        final long bytesDone, final long bytesTotal) {
        // nothing to do at all. This class only monitors downloads that are
        // done.
    }

    /**
     * Report to all listeners that the unpacking operation is done.
     * 
     * @param unpack the unpacking operation
     * @param result the result of the unpacking operation
     */
    public void reportFinished(final Unpack unpack, final UnpackResult result) {
        if (!callbacks.isEmpty()) {
            final int count = callbacks.size();
            for (int i = 0; i < count; i++) {
                callbacks.get(i).reportUnpackFinished(unpack, result);
            }
        }
    }

    /**
     * This function is called to update the state of a unpacking operation.
     * 
     * @param unpack the unpacking operation
     * @param bytesDone the amount of bytes processed already
     * @param bytesTotal the total amount of bytes of this resource
     */
    public void reportProgress(final Unpack unpack, final long bytesDone,
        final long bytesTotal) {
        if (!callbacks.isEmpty()) {
            final int count = callbacks.size();
            for (int i = 0; i < count; i++) {
                callbacks.get(i).reportUnpackProgress(unpack, bytesDone,
                    bytesTotal);
            }
        }
    }

    /**
     * Stop the unpacking manager. Ensure to call this once the download manager
     * is stopped for sure. Else bad things will happen.
     */
    public void shutdown() {
        service.shutdown();
        try {
            service.awaitTermination(24, TimeUnit.DAYS);
        } catch (final InterruptedException e) {
            // nothing to do
        }
    }

}
