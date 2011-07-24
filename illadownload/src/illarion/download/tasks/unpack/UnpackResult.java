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

import java.io.File;

/**
 * This class contains the result values of a unpacking operation. It is
 * generated once a file is fully unpacked.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class UnpackResult {
    /**
     * This enumerator contains a list of the possible results of a unpacking
     * operation.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    public static enum Results {
        /**
         * This constant means that extracting the file got canceled.
         */
        canceled,

        /**
         * This constant means that the resource file is corrupted and couldn't
         * be extracted.
         */
        corrupted,

        /**
         * This constant means that the resource was not altered and does not
         * need to be processed.
         */
        notModified,

        /**
         * This constant means that the resource got completely extracted and is
         * now ready to be used.
         */
        unpacked;
    }

    /**
     * The message that describes the state of the download.
     */
    private final String message;

    /**
     * Stores the result of the download.
     */
    private final Results result;

    /**
     * The target file that got created.
     */
    private final File target;

    /**
     * The name of this task.
     */
    private final String taskName;

    /**
     * Create a new instance of a download result.
     * 
     * @param name the name of the unpack operation that failed
     * @param downloadResult the result of the download
     * @param resultMessage the message that describes the result
     * @param targetFile the file that was the target of the download
     */
    public UnpackResult(final String name, final Results downloadResult,
        final String resultMessage, final File targetFile) {
        taskName = name;
        result = downloadResult;
        message = resultMessage;
        target = targetFile;
    }

    /**
     * The message that is stored in this result.
     * 
     * @return the message stored in this result
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the actual result value.
     * 
     * @return the result that is stored in this object
     */
    public Results getResult() {
        return result;
    }

    /**
     * The file target that triggered that result.
     * 
     * @return the result
     */
    public File getTarget() {
        return target;
    }

    /**
     * The name of the task that triggered this result.
     * 
     * @return the name of the task that triggered this result
     */
    public String getTaskName() {
        return taskName;
    }
}
