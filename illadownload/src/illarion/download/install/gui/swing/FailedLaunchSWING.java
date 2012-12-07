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

import illarion.download.util.Lang;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class FailedLaunchSWING extends AbstractContentSWING {
    /**
     * This is a little helper class to update the result value.
     *
     * @author Martin Karing
     */
    private final class UpdateResultClass
            implements ActionListener {
        /**
         * The result value that is set in case this button is clicked.
         */
        private final FailureAction targetResult;

        /**
         * Construct a new instance of this class. This will also set the required values for this listener to work.
         *
         * @param newResult the result value that is supposed to be set
         */
        private UpdateResultClass(final FailureAction newResult) {
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
     * The possible user actions to react on the problem displayed here.
     */
    public enum FailureAction {
        /**
         * This action means that the launch is just supposed to be repeated.
         */
        Retry,

        /**
         * This action means that all downloaded files are supposed to be deleted and downloaded again.
         */
        DeleteRetry
    }

    private static final String LANG_ROOT = "illarion.download.install.gui.FailedLaunch.";

    /**
     * The error information that are supposed to be displayed.
     */
    private final String errorData;

    /**
     * Create a instance of this content that displays the launch error data.
     *
     * @param errors the launch errors
     */
    public FailedLaunchSWING(final String errors) {
        errorData = errors;
    }

    @Override
    public void fillButtons(final BaseSWING base, final JPanel buttonPanel) {
        final JButton retryButton = BaseSWING.getPanelButton();
        retryButton.setText(Lang.getMsg(LANG_ROOT + "retry.text"));
        retryButton.setToolTipText(Lang.getMsg(LANG_ROOT + "retry.tooltip"));
        retryButton.addActionListener(new UpdateResultClass(FailureAction.Retry));
        buttonPanel.add(retryButton);

        final JButton startAnywayButton = BaseSWING.getPanelButton();
        startAnywayButton.setText(Lang.getMsg(LANG_ROOT + "deleteRetry.text"));
        startAnywayButton.setToolTipText(Lang.getMsg(LANG_ROOT + "deleteRetry.tooltip"));
        startAnywayButton.addActionListener(new UpdateResultClass(FailureAction.DeleteRetry));
        buttonPanel.add(startAnywayButton);

        buttonPanel.add(base.getExitButton());
    }

    @Override
    public void fillContent(final BaseSWING base, final JPanel contentPanel) {
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
        con.weighty = 1.0;
        con.insets.set(0, 0, 10, 0);

        final JLabel headLabel = new JLabel(Lang.getMsg(LANG_ROOT + "title"));
        contentPanel.add(headLabel, con);
        headLabel.setFont(headLabel.getFont().deriveFont(Font.BOLD, 14.f));

        con.gridy = line++;
        final JXLabel textField = new JXLabel();
        textField.setCursor(null);
        textField.setOpaque(false);
        textField.setFocusable(false);
        textField.setLineWrap(true);
        textField.setMaxLineSpan(BaseSWING.WINDOW_WIDTH - 20);
        textField.setTextAlignment(JXLabel.TextAlignment.JUSTIFY);
        textField.setText(Lang.getMsg(LANG_ROOT + "content"));
        contentPanel.add(textField, con);

        con.gridy = line++;
        con.gridwidth = 2;
        con.weightx = 1.0;
        final JTextArea errorArea = new JTextArea(10, 10);
        errorArea.setText(errorData);
        errorArea.setLineWrap(false);
        errorArea.setEditable(false);
        contentPanel.add(new JScrollPane(errorArea), con);
    }

    @Override
    public void prepareDisplay(final BaseSWING base) {
        base.setVisible(true);
    }

    /**
     * The user selected result of this dialog.
     */
    public FailureAction result = FailureAction.Retry;

    /**
     * Get the result of this information display.
     *
     * @return the result value
     */
    public FailureAction getResult() {
        return result;
    }

    /**
     * Update the value of the result variable.
     *
     * @param newResult the new value of the result variable.
     */
    protected void setResult(final FailureAction newResult) {
        result = newResult;
        reportContinue();
    }
}
