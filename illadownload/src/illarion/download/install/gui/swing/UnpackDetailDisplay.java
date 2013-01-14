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
package illarion.download.install.gui.swing;

import illarion.download.tasks.unpack.Unpack;
import illarion.download.tasks.unpack.UnpackResult;
import illarion.download.util.Lang;

import javax.annotation.Nonnull;

/**
 * This class implements the display components that are used to display the the information on every single
 * unpacking operation that is currently going on.
 *
 * @author Martin Karing
 * @version 1.00
 * @since 1.00
 */
final class UnpackDetailDisplay
        extends AbstractProgressDetailDisplay {
    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The last bytes value reported.
     */
    private long lastReportedBytes;

    /**
     * The maximal size of the download that was reported.
     */
    private long maxSize;

    /**
     * Create a new display and set it up properly to display the unpack progress.
     *
     * @param parentDisplay the parent progress display of this class
     * @param unpack        the unpack progress that is handled
     */
    @SuppressWarnings("nls")
    UnpackDetailDisplay(final ProgressSWING parentDisplay, @Nonnull final Unpack unpack) {
        super(parentDisplay, unpack.getName());

        setProgressMessage(Lang.getMsg("illarion.download.install.gui.Progress.UnpackProgress.installing"));

        lastReportedBytes = 0;
        maxSize = 0;
    }

    /**
     * Report the result of the unpack to this display. After the result got displayed, the unpack display is not
     * supposed to change anymore.
     *
     * @param result the result that is reported
     * @return the delta to the last reported state
     */
    @SuppressWarnings("nls")
    public long reportResult(@Nonnull final UnpackResult result) {
        setProgressLimits(0, 100);
        setProgressValue(100);

        final String baseKey = "illarion.download.install.gui.Progress.UnpackProgress.";
        switch (result.getResult()) {
            case canceled:
                setProgressMessage(Lang.getMsg(baseKey + "canceled"));
                break;
            case unpacked:
                setProgressMessage(Lang.getMsg(baseKey + "installed"));
                break;
            case failed:
                setProgressMessage(Lang.getMsg(baseKey + "failed"));
                break;
            case corrupted:
                setProgressMessage(Lang.getMsg(baseKey + "corrupted"));
                break;
            case notModified:
                setProgressMessage(Lang.getMsg(baseKey + "notModified"));
                break;
        }

        lock();
        getParentDisplay().moveDetailDisplayToFinishList(this);
        setVisible(true);
        repaint();

        final long retVal = maxSize - lastReportedBytes;
        lastReportedBytes = maxSize;
        return retVal;
    }

    /**
     * Report a change of this download to this display.
     *
     * @param unpacked the amount of bytes already processed
     * @param size     the total amount of bytes of this file
     * @return the delta to the last reported state
     */
    public long reportUpdate(final long unpacked, final long size) {
        if (isLocked()) {
            return 0L;
        }

        boolean changedVisible = false;
        if (unpacked > 0L) {
            setVisible(true);
            changedVisible = true;
        }

        if (isVisible()) {
            final int unpackedBytes;
            final int totalBytes;
            if (size > Integer.MAX_VALUE) {
                final int diff = (int) (size / Integer.MAX_VALUE) + 1;
                unpackedBytes = (int) (unpacked / diff);
                totalBytes = (int) (size / diff);
            } else {
                unpackedBytes = (int) unpacked;
                totalBytes = (int) size;
            }
            setProgressLimits(0, totalBytes);
            setProgressValue(unpackedBytes);

            if (changedVisible) {
                doLayout();
                scrollRectToVisible(getBounds());
            }
        }

        maxSize = size;

        final long retVal = unpacked - lastReportedBytes;
        lastReportedBytes = unpacked;
        return retVal;
    }
}
