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

import java.io.File;
import java.net.URL;

/**
 * This class contains the result values of a download that has finished or got
 * canceled.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class DownloadResult {
    /**
     * This enumerator contains a list of the possible results of a download.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    public static enum Results {
        /**
         * This constant means that the download of the file was canceled.
         */
        canceled,

        /**
         * This constant means that the file was downloaded successfully and is
         * now stored in the target file.
         */
        downloaded,

        /**
         * This constant means that the download of the file failed.
         */
        downloadFailed,

        /**
         * This constant means that the file was not modified since the time
         * that was set. The cached file is supposed to be used.
         */
        notModified;
    }

    /**
     * The time when the file was last modified on the server.
     */
    private final long lastMod;

    /**
     * The message that describes the state of the download.
     */
    private final String message;

    /**
     * Stores the result of the download.
     */
    private final Results result;

    /**
     * The URL that was downloaded.
     */
    private final URL source;

    /**
     * The target file that got created.
     */
    private final File target;

    /**
     * Create a new instance of a download result.
     * 
     * @param downloadResult the result of the download
     * @param resultMessage the message that describes the result
     * @param sourceURL the URL that got downloaded
     * @param targetFile the file that was the target of the download
     * @param lastModified the time when this file was last modified on the web
     *            server
     */
    public DownloadResult(final Results downloadResult,
        final String resultMessage, final URL sourceURL,
        final File targetFile, final long lastModified) {
        result = downloadResult;
        message = resultMessage;
        source = sourceURL;
        target = targetFile;
        lastMod = lastModified;
    }

    /**
     * Get the last modified date of the file that was assigned to this
     * download.
     * 
     * @return the file that was downloaded
     */
    public long getLastModified() {
        return lastMod;
    }

    /**
     * The message that was stored in this download result.
     * 
     * @return the message of this download result
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the actual result value of this download result.
     * 
     * @return the actual result value
     */
    public Results getResult() {
        return result;
    }

    /**
     * Get the source of the download that triggered this result.
     * 
     * @return the source of the download
     */
    public URL getSource() {
        return source;
    }

    /**
     * Get the target of the download that triggered this result.
     * 
     * @return the target of the download
     */
    public File getTarget() {
        return target;
    }
}
