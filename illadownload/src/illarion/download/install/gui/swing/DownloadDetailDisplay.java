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

import illarion.download.tasks.download.Download;
import illarion.download.tasks.download.DownloadResult;
import illarion.download.util.Lang;

import javax.annotation.Nonnull;

/**
 * This class implements the display components that are used to display the the information on every single
 * download that is currently going on.
 *
 * @author Martin Karing
 * @version 1.00
 * @since 1.00
 */
final class DownloadDetailDisplay
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
     * Create a new display and set it up properly to display the download progress.
     *
     * @param parentDisplay the parent progress display of this class
     * @param download      the download that is handled
     */
    @SuppressWarnings("nls")
    DownloadDetailDisplay(final ProgressSWING parentDisplay, @Nonnull final Download download) {
        super(parentDisplay, download.getName());

        setProgressMessage(Lang.getMsg("illarion.download.install.gui.Progress.DownloadProgress.download"));

        lastReportedBytes = 0;
        maxSize = 0;
    }

    /**
     * Report the result of the download to this display. After the result got displayed, the download is not
     * supposed to change anymore.
     *
     * @param result the result that is reported
     * @return the delta to the last reported state
     */
    @SuppressWarnings("nls")
    public long reportResult(@Nonnull final DownloadResult result) {
        setProgressLimits(0, 100);
        setProgressValue(100);

        final String baseKey = "illarion.download.install.gui.Progress.DownloadProgress.";
        switch (result.getResult()) {
            case canceled:
                setProgressMessage(Lang.getMsg(baseKey + "canceled"));
                break;
            case downloaded:
                setProgressMessage(Lang.getMsg(baseKey + "downloaded"));
                break;
            case downloadFailed:
                setProgressMessage(Lang.getMsg(baseKey + "failed"));
                break;
            case notModified:
                setProgressMessage(Lang.getMsg(baseKey + "notModified"));
                break;
        }

        lock();
        getParentDisplay().moveDetailDisplayToFinishList(this);
        setVisible(true);
        getParentDisplay().setVisible(true);
        repaint();

        final long retVal = maxSize - lastReportedBytes;
        lastReportedBytes = maxSize;
        return retVal;
    }

    /**
     * Report a change of this download to this display.
     *
     * @param downloaded the amount of bytes now downloaded
     * @param size       the total amount of bytes of this file
     * @return the delta to the last reported state
     */
    public long reportUpdate(final long downloaded, final long size) {
        if (isLocked()) {
            return 0L;
        }

        boolean changedVisible = false;
        if (downloaded > 0L) {
            setVisible(true);
            changedVisible = true;
        }

        if (isVisible()) {
            final int downloadedBytes;
            final int totalBytes;
            if (size > Integer.MAX_VALUE) {
                final int diff = (int) (size / Integer.MAX_VALUE) + 1;
                downloadedBytes = (int) (downloaded / diff);
                totalBytes = (int) (size / diff);
            } else {
                downloadedBytes = (int) downloaded;
                totalBytes = (int) size;
            }
            setProgressLimits(0, totalBytes);
            setProgressValue(downloadedBytes);

            if (changedVisible) {
                doLayout();
                scrollRectToVisible(getBounds());
            }
        }

        maxSize = size;

        final long retVal = downloaded - lastReportedBytes;
        lastReportedBytes = downloaded;
        return retVal;
    }
}
