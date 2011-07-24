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

/**
 * This is the fail monitor. This monitor looks out for fails and epic fails all
 * over the place. So if any download fails, this monitor notices it.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 * @see <a href="http://failblog.org/">FailBlog</a>
 */
public final class FailMonitor implements UnpackCallback {
    /**
     * This is the singleton instance of this class.
     */
    private static final FailMonitor INSTANCE = new FailMonitor();

    /**
     * This list contains the failed tasks.
     */
    private final List<UnpackResult> failedList;

    /**
     * Private constructor to ensure that no instances but the singleton
     * instance are created.
     */
    private FailMonitor() {
        failedList = new ArrayList<UnpackResult>();
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static FailMonitor getInstance() {
        return INSTANCE;
    }

    /**
     * Get the amount of errors that occurred.
     * 
     * @return the amount of errors that happened
     */
    public int getErrorCount() {
        return failedList.size();
    }

    /**
     * Get the error that occurred.
     * 
     * @param index the index of the error
     * @return the result of the unpacking operation that failed
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0
     *             || index >= getErrorCount())
     */
    public UnpackResult getErrorResult(final int index) {
        return failedList.get(index);
    }

    /**
     * Check if any errors occurred.
     * 
     * @return <code>true</code> in case any error happened
     */
    public boolean hasErrors() {
        return !failedList.isEmpty();
    }

    /**
     * This class is used to monitor the unpack tasks that finish. In case any
     * of those tasks fail this monitor will record it.
     */
    @Override
    public void reportUnpackFinished(final Unpack unpack,
        final UnpackResult result) {
        if ((result.getResult() == UnpackResult.Results.canceled)
            || (result.getResult() == UnpackResult.Results.corrupted)) {
            synchronized (failedList) {
                failedList.add(result);
            }
        }
    }

    /**
     * The progress of updates does not matter at all to this class. So this is
     * just ignored.
     */
    @Override
    public void reportUnpackProgress(final Unpack unpack,
        final long bytesDone, final long bytesTotal) {
        // nothing to do
    }
}
