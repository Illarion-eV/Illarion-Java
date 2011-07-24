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
package illarion.download.tasks.download;

/**
 * This interface is used to keep track about the currently ongoing downloads.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public interface DownloadCallback {
    /**
     * Report that a download has finished downloading with a specified result.
     * 
     * @param download the download that finished
     * @param result the result of the download
     */
    void reportDownloadFinished(Download download, DownloadResult result);

    /**
     * This function reports about a ongoing download progress.
     * 
     * @param download the download that reports this
     * @param bytesDone the amount of bytes already downloaded
     * @param bytesTotal the total amount of bytes to download
     */
    void reportDownloadProgress(Download download, long bytesDone,
        long bytesTotal);
}
