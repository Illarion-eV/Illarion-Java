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
package illarion.download.install.gui.swing;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXLabel;

import illarion.download.tasks.download.Download;
import illarion.download.tasks.download.DownloadCallback;
import illarion.download.tasks.download.DownloadResult;
import illarion.download.tasks.unpack.Unpack;
import illarion.download.tasks.unpack.UnpackCallback;
import illarion.download.tasks.unpack.UnpackResult;
import illarion.download.util.Lang;

/**
 * This window is used to display the download and the installation progress of
 * The selected application.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class ProgressSWING extends AbstractContentSWING implements
    DownloadCallback, UnpackCallback {
    /**
     * This class implements the display components that are used to display the
     * the information on every single download that is currently going on.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    private static final class DownloadDetailDisplay extends
        AbstractProgressDetailDisplay {
        /**
         * The serialization UID of this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The last bytes value reported.
         */
        private long lastReportedBytes;

        /**
         * This variable is set to true once the download is not supposed to be
         * updated anymore.
         */
        private boolean locked;

        /**
         * The maximal size of the download that was reported.
         */
        private long maxSize;

        /**
         * The parent of this detail display component.
         */
        private final ProgressSWING parent;

        /**
         * Create a new display and set it up properly to display the download
         * progress.
         * 
         * @param parentDisplay the parent progress display of this class
         * @param download the download that is handled
         */
        @SuppressWarnings("nls")
        public DownloadDetailDisplay(final ProgressSWING parentDisplay,
            final Download download) {
            super(download.getName());

            setProgressMessage(Lang
                .getMsg("illarion.download.intall.gui.Progress.DownloadProgress.download"));

            locked = false;
            lastReportedBytes = 0;
            maxSize = 0;
            parent = parentDisplay;
        }

        /**
         * Report the result of the download to this display. After the result
         * got displayed, the download is not supposed to change anymore.
         * 
         * @param result the result that is reported
         * @return the delta to the last reported state
         */
        @SuppressWarnings("nls")
        public long reportResult(final DownloadResult result) {
            setProgressLimits(0, 100);
            setProgressValue(100);

            switch (result.getResult()) {
                case canceled:
                    setProgressMessage(Lang
                        .getMsg("illarion.download.intall.gui.Progress.DownloadProgress.canceled"));
                    break;
                case downloaded:
                    setProgressMessage(Lang
                        .getMsg("illarion.download.intall.gui.Progress.DownloadProgress.downloaded"));
                    break;
                case downloadFailed:
                    setProgressMessage(Lang
                        .getMsg("illarion.download.intall.gui.Progress.DownloadProgress.failed"));
                    break;
                case notModified:
                    setProgressMessage(Lang
                        .getMsg("illarion.download.intall.gui.Progress.DownloadProgress.notModified"));
                    break;
            }
            locked = true;
            setVisible(true);
            parent.setVisible(true);
            repaint();

            final long retVal = maxSize - lastReportedBytes;
            lastReportedBytes = maxSize;
            return retVal;
        }

        /**
         * Report a change of this download to this display.
         * 
         * @param downloaded the amount of bytes now downloaded
         * @param size the total amount of bytes of this file
         * @return the delta to the last reported state
         */
        public long reportUpdate(final long downloaded, final long size) {
            if (locked) {
                return 0L;
            }

            boolean changedVisible = false;
            if (downloaded > 0L) {
                setVisible(true);
                changedVisible = true;
            }

            if (isVisible()) {
                int downloadedBytes = 0;
                int totalBytes = 0;
                if (size > Integer.MAX_VALUE) {
                    final int diff = ((int) (size / Integer.MAX_VALUE)) + 1;
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

    /**
     * This class implements the display components that are used to display the
     * the information on every single unpacking operation that is currently
     * going on.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    private static final class UnpackDetailDisplay extends
        AbstractProgressDetailDisplay {
        /**
         * The serialization UID of this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The last bytes value reported.
         */
        private long lastReportedBytes;

        /**
         * This variable is set to true once the download is not supposed to be
         * updated anymore.
         */
        private boolean locked;

        /**
         * The maximal size of the download that was reported.
         */
        private long maxSize;

        /**
         * Create a new display and set it up properly to display the unpack
         * progress.
         * 
         * @param unpack the unpack progress that is handled
         */
        @SuppressWarnings("nls")
        public UnpackDetailDisplay(final Unpack unpack) {
            super(unpack.getName());

            setProgressMessage(Lang
                .getMsg("illarion.download.intall.gui.Progress.UnpackProgress.installing"));

            locked = false;
            lastReportedBytes = 0;
            maxSize = 0;
        }

        /**
         * Report the result of the unpack to this display. After the result got
         * displayed, the unpack display is not supposed to change anymore.
         * 
         * @param result the result that is reported
         * @return the delta to the last reported state
         */
        @SuppressWarnings("nls")
        public long reportResult(final UnpackResult result) {
            setProgressLimits(0, 100);
            setProgressValue(100);

            switch (result.getResult()) {
                case canceled:
                    setProgressMessage(Lang
                        .getMsg("illarion.download.intall.gui.Progress.UnpackProgress.canceled"));
                    break;
                case unpacked:
                    setProgressMessage(Lang
                        .getMsg("illarion.download.intall.gui.Progress.UnpackProgress.installed"));
                    break;
                case corrupted:
                    setProgressMessage(Lang
                        .getMsg("illarion.download.intall.gui.Progress.UnpackProgress.currupted"));
                    break;
                case notModified:
                    setProgressMessage(Lang
                        .getMsg("illarion.download.intall.gui.Progress.UnpackProgress.notModified"));
                    break;
            }
            locked = true;
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
         * @param size the total amount of bytes of this file
         * @return the delta to the last reported state
         */
        public long reportUpdate(final long unpacked, final long size) {
            if (locked) {
                return 0L;
            }

            boolean changedVisible = false;
            if (unpacked > 0L) {
                setVisible(true);
                changedVisible = true;
            }

            if (isVisible()) {
                int unpackedBytes = 0;
                int totalBytes = 0;
                if (size > Integer.MAX_VALUE) {
                    final int diff = ((int) (size / Integer.MAX_VALUE)) + 1;
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

    /**
     * The base frame that displays this progress.
     */
    private BaseSWING baseFrame;

    /**
     * This variable stores the amount of bytes already downloaded.
     */
    private long currentlyDownloadedBytes = 0;

    /**
     * The map that stores all downloads that are currently known.
     */
    private final Map<Download, DownloadDetailDisplay> downloadMap =
        new HashMap<Download, DownloadDetailDisplay>();

    /**
     * This panel holds all detailed operations in progress that are currently
     * going on.
     */
    private JPanel progressDetailList;

    /**
     * The line number for the next entry to add to the progress detail list.
     */
    private int progressDetailListLine;

    /**
     * This variable is used to store the amount of bytes that need to be
     * downloaded in total.
     */
    private long totalDownloadBytes = 0;

    /**
     * The progress bar used to display the total progress of the download.
     */
    private JProgressBar totalProgressBar;

    /**
     * The map that stores all unpack operations that are currently known.
     */
    private final Map<Unpack, UnpackDetailDisplay> unpackMap =
        new HashMap<Unpack, UnpackDetailDisplay>();

    /**
     * Add the buttons needed for this display.
     */
    @Override
    public void fillButtons(final BaseSWING base, final JPanel buttonPanel) {
        baseFrame = base;
        buttonPanel.add(base.getCancelButton());
    }

    /**
     * Fill the content area of the download window.
     */
    @SuppressWarnings("nls")
    @Override
    public void fillContent(final BaseSWING base, final JPanel contentPanel) {
        baseFrame = base;
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        int line = 0;
        final GridBagConstraints con = new GridBagConstraints();
        con.anchor = GridBagConstraints.WEST;
        con.fill = GridBagConstraints.HORIZONTAL;
        con.gridheight = 1;
        con.gridwidth = 2;
        con.gridx = 0;
        con.gridy = line++;
        con.weightx = 1.0;
        con.weighty = 0.0;
        con.insets.set(0, 0, 10, 0);

        final JLabel headLabel =
            new JLabel(
                Lang.getMsg("illarion.download.intall.gui.Progress.title"));
        contentPanel.add(headLabel, con);
        headLabel.setFont(headLabel.getFont().deriveFont(Font.BOLD, 14.f));

        con.gridx = 0;
        con.gridy = line++;
        final JXLabel textField = new JXLabel();
        textField.setCursor(null);
        textField.setOpaque(false);
        textField.setFocusable(false);
        textField.setLineWrap(true);
        textField.setMaxLineSpan(contentPanel.getWidth() - 20);
        textField.setText(Lang
            .getMsg("illarion.download.intall.gui.Progress.content"));
        contentPanel.add(textField, con);

        con.gridx = 0;
        con.gridy = line++;
        con.gridwidth = 2;
        con.weighty = 0.0;
        final JLabel textField2 = new JLabel();
        textField2.setCursor(null);
        textField2.setOpaque(false);
        textField2.setFocusable(false);
        textField2.setText(Lang
            .getMsg("illarion.download.intall.gui.Progress.progressTitle"));
        contentPanel.add(textField2, con);
        textField2.setFont(textField2.getFont().deriveFont(Font.BOLD));

        con.gridy = line++;
        con.gridwidth = 2;
        con.gridx = 0;
        totalProgressBar = new JProgressBar(SwingConstants.HORIZONTAL);
        contentPanel.add(totalProgressBar, con);

        progressDetailList = new JPanel(new GridBagLayout());
        final JScrollPane scrollArea =
            new JScrollPane(progressDetailList,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        con.gridy = line++;
        con.weighty = 1.0;
        con.fill = GridBagConstraints.BOTH;
        contentPanel.add(scrollArea, con);
        con.gridy = 2000;
        con.gridwidth = 1;
        con.insets.set(0, 0, 0, 0);
        progressDetailList.add(new JLabel(), con);
        progressDetailListLine = 0;
    }

    @Override
    public void prepareDisplay(final BaseSWING base) {
        // nothing to do
    }

    @Override
    public void reportDownloadFinished(final Download download,
        final DownloadResult result) {
        final DownloadDetailDisplay display =
            getDownloadDisplay(download, 0L, false);
        if (display != null) {
            currentlyDownloadedBytes += display.reportResult(result);
        }
        updateProgressBar();
    }

    @Override
    public void reportDownloadProgress(final Download download,
        final long bytesDone, final long bytesTotal) {
        final DownloadDetailDisplay display =
            getDownloadDisplay(download, bytesTotal, true);

        currentlyDownloadedBytes +=
            display.reportUpdate(bytesDone, bytesTotal);
        updateProgressBar();
    }

    @Override
    public void reportUnpackFinished(final Unpack unpack,
        final UnpackResult result) {
        final UnpackDetailDisplay display = getUnpackDisplay(unpack, false);
        if (display != null) {
            currentlyDownloadedBytes += display.reportResult(result);
        }
        updateProgressBar();
    }

    @Override
    public void reportUnpackProgress(final Unpack unpack,
        final long bytesDone, final long bytesTotal) {
        final UnpackDetailDisplay display = getUnpackDisplay(unpack, true);

        currentlyDownloadedBytes +=
            display.reportUpdate(bytesDone, bytesTotal);
        updateProgressBar();
    }

    /**
     * This function does nothing but setting the progress bar so it looks like
     * everything is finished.
     */
    public void setToFinished() {
        if (baseFrame.isVisible()) {
            totalProgressBar.setMinimum(0);
            totalProgressBar.setMaximum(100);
            totalProgressBar.setValue(100);
            totalProgressBar.invalidate();
            totalProgressBar.repaint();
        }
    }

    /**
     * This function is used to properly set this display visible in case its
     * needed.
     * 
     * @param flag the new value for the visible flag
     */
    protected void setVisible(final boolean flag) {
        if (flag && !baseFrame.isVisible()) {
            baseFrame.setVisible(true);
            updateProgressBar();
        }
    }

    /**
     * Get the graphical display of a download.
     * 
     * @param download the download thats display is needed
     * @param bytesTotal the maximal size of this download
     * @param createNew create a new instance of this class in case none was
     *            created yet
     * @return the display of the download
     */
    private DownloadDetailDisplay getDownloadDisplay(final Download download,
        final long bytesTotal, final boolean createNew) {
        DownloadDetailDisplay display = null;
        if (downloadMap.containsKey(download)) {
            display = downloadMap.get(download);
        } else if (createNew) {
            display = new DownloadDetailDisplay(this, download);
            downloadMap.put(download, display);

            final GridBagConstraints con = new GridBagConstraints();
            con.anchor = GridBagConstraints.WEST;
            con.fill = GridBagConstraints.HORIZONTAL;
            con.gridheight = 1;
            con.gridwidth = 1;
            con.gridx = 0;
            con.gridy = progressDetailListLine++;
            con.weightx = 1.0;
            con.weighty = 0.0;
            con.insets.set(0, 0, 1, 0);

            progressDetailList.add(display, con);
            totalDownloadBytes += bytesTotal;
        }
        return display;
    }

    /**
     * Get the graphical display of a download.
     * 
     * @param download the download thats display is needed
     * @param createNew create a new instance in case none was created before
     * @return the display of the download
     */
    private UnpackDetailDisplay getUnpackDisplay(final Unpack unpack,
        final boolean createNew) {
        UnpackDetailDisplay display = null;
        if (unpackMap.containsKey(unpack)) {
            display = unpackMap.get(unpack);
        } else if (createNew) {
            display = new UnpackDetailDisplay(unpack);
            unpackMap.put(unpack, display);

            final GridBagConstraints con = new GridBagConstraints();
            con.anchor = GridBagConstraints.WEST;
            con.fill = GridBagConstraints.HORIZONTAL;
            con.gridheight = 1;
            con.gridwidth = 1;
            con.gridx = 0;
            con.gridy = progressDetailListLine++;
            con.weightx = 1.0;
            con.weighty = 0.0;
            con.insets.set(0, 0, 1, 0);

            progressDetailList.add(display, con);
        }
        return display;
    }

    /**
     * Update the the state of the progress bar.
     */
    private void updateProgressBar() {
        if (baseFrame.isVisible()) {
            int downloadedBytes = 0;
            int totalBytes = 0;
            if (totalDownloadBytes > Integer.MAX_VALUE) {
                final int diff =
                    ((int) (totalDownloadBytes / Integer.MAX_VALUE)) + 1;
                downloadedBytes = (int) (currentlyDownloadedBytes / diff);
                totalBytes = (int) (totalDownloadBytes / diff);
            } else {
                downloadedBytes = (int) currentlyDownloadedBytes;
                totalBytes = (int) totalDownloadBytes;
            }
            totalProgressBar.setMaximum(totalBytes);
            totalProgressBar.setMinimum(-totalBytes);
            totalProgressBar.setValue(downloadedBytes - totalBytes);
            totalProgressBar.invalidate();
            totalProgressBar.repaint();
        }
    }

}
