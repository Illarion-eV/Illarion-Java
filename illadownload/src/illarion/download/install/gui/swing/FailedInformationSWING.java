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

import illarion.download.tasks.unpack.FailMonitor;
import illarion.download.tasks.unpack.UnpackResult;
import illarion.download.util.Lang;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXLabel.TextAlignment;
import org.jdesktop.swingx.JXList;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is used to display some informations about failed downloads in case this happened.
 *
 * @author Martin Karing
 * @version 1.00
 * @since 1.00
 */
public final class FailedInformationSWING
        extends AbstractContentSWING {
    /**
     * This is a little helper class to update the result value.
     *
     * @author Martin Karing
     * @version 1.00
     * @since 1.00
     */
    private final class UpdateResultClass
            implements ActionListener {
        /**
         * The result value that is set in case this button is clicked.
         */
        private final int targetResult;

        /**
         * Construct a new instance of this class. This constructor is public to allow the parent class to create a
         * instance without problems. This will also set the required values for this listener to work.
         *
         * @param newResult the result value that is supposed to be set
         */
        private UpdateResultClass(final int newResult) {
            targetResult = newResult;
        }

        /**
         * This function is called in case the button is clicked and will set the new result value.
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            setResult(targetResult);
        }
    }

    /**
     * This is the value of the result variable in case the launcher is ordered to launch the target application
     * anyway.
     */
    public static final int RESULT_LAUNCH = 2;

    /**
     * This is the value of the result variable in case the application is ordered to retry downloading the
     * application.
     */
    public static final int RESULT_RETRY = 1;

    /**
     * The result value of this dialog.
     */
    private int result = -1;

    /**
     * Create the buttons to be created during the installation.
     */
    @SuppressWarnings("nls")
    @Override
    public void fillButtons(@Nonnull final BaseSWING base, @Nonnull final JPanel buttonPanel) {
        final JButton retryButton = BaseSWING.getPanelButton();
        retryButton.setText(Lang.getMsg("illarion.download.install.gui.FailedInformation.retry.text"));
        retryButton.setToolTipText(Lang.getMsg("illarion.download.install.gui.FailedInformation.retry.tooltip"));
        retryButton.addActionListener(new UpdateResultClass(RESULT_RETRY));
        buttonPanel.add(retryButton);

        final JButton startAnywayButton = BaseSWING.getPanelButton();
        startAnywayButton.setText(Lang.getMsg("illarion.download.install.gui.FailedInformation.startAnyway.text"));
        startAnywayButton.setToolTipText(Lang.getMsg("illarion.download.install.gui.FailedInformation.startAnyway" +
                ".tooltip"));
        startAnywayButton.addActionListener(new UpdateResultClass(RESULT_LAUNCH));
        buttonPanel.add(startAnywayButton);

        buttonPanel.add(base.getExitButton());
    }

    /**
     * Fill the content of this information window.
     */
    @SuppressWarnings("nls")
    @Override
    public void fillContent(final BaseSWING base, @Nonnull final JPanel contentPanel) {
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

        final JLabel headLabel = new JLabel(Lang.getMsg("illarion.download.install.gui.FailedInformation.title"));
        contentPanel.add(headLabel, con);
        headLabel.setFont(headLabel.getFont().deriveFont(Font.BOLD, 14.f));

        con.gridy = line++;
        final JXLabel textField = new JXLabel();
        textField.setCursor(null);
        textField.setOpaque(false);
        textField.setFocusable(false);
        textField.setLineWrap(true);
        textField.setMaxLineSpan(BaseSWING.WINDOW_WIDTH - 20);
        textField.setTextAlignment(TextAlignment.JUSTIFY);
        textField.setText(Lang.getMsg("illarion.download.install.gui.FailedInformation.content"));
        contentPanel.add(textField, con);

        con.gridy = line++;
        con.gridx = 0;
        con.gridwidth = 2;
        con.weightx = 1.0;
        final FailMonitor failMon = FailMonitor.getInstance();
        final int errCnt = failMon.getErrorCount();
        final String[] failedPackages = new String[errCnt];
        for (int i = 0; i < errCnt; i++) {
            final UnpackResult failedResult = failMon.getErrorResult(i);
            failedPackages[i] = failedResult.getTaskName() + " - " + failedResult.getErrorMessage();
        }
        contentPanel.add(new JXList(failedPackages), con);

        con.gridy = line++;
        con.gridx = 0;
        con.gridwidth = 2;
        con.weightx = 1.0;
        contentPanel.add(new JLabel(), con);
    }

    /**
     * Get the result of this information display.
     *
     * @return the result value
     * @see #RESULT_LAUNCH
     * @see #RESULT_RETRY
     */
    public int getResult() {
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * illarion.download.install.gui.swing.AbstractContentSWING#prepareDisplay
     * (illarion.download.install.gui.swing.BaseSWING)
     */
    @Override
    public void prepareDisplay(@Nonnull final BaseSWING base) {
        base.setVisible(true);
    }

    /**
     * Update the value of the result variable.
     *
     * @param newResult the new value of the result variable.
     */
    protected void setResult(final int newResult) {
        result = newResult;
        reportContinue();
    }
}
