/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.bug;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javolution.text.TextBuilder;

import illarion.common.util.MessageSource;

/**
 * This is the dialog implementation for Swing. It will display a dialog that
 * contains the error problem description and the error message and allows the
 * user to choose what to do.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ReportDialogSwing implements ReportDialog {
    /**
     * Button listener helper class. This class is assigned to the buttons of
     * the dialog.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private final class ButtonListener implements ActionListener {
        /**
         * The dialog that is closed upon calling this listener.
         */
        private final JDialog closingDialog;

        /**
         * The result value that is set in case the button is pressed.
         */
        private final int resultValue;

        /**
         * Public constructor so the parent class is able to create a instance.
         * Also this sets the result value that is put in place in case the
         * button this listener is assigned to is clicked.
         * 
         * @param dialog the dialog that is closed upon calling this listener
         * @param setResult the result value that is supposed to be set
         */
        public ButtonListener(final JDialog dialog, final int setResult) {
            resultValue = setResult;
            closingDialog = dialog;
        }

        /**
         * The action performed when the button is pressed.
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            setResult(resultValue);
            closingDialog.setVisible(false);
        }

    }

    /**
     * The newline string that is used in the dialog.
     */
    @SuppressWarnings("nls")
    private static final String NL = "\n".intern();

    /**
     * The data about the crash.
     */
    private CrashData crashData;

    /**
     * The source of the messages displayed in the dialog.
     */
    private MessageSource messages;

    /**
     * The result received from the displayed dialog.
     */
    private int result = 0;

    /**
     * Get the result of the dialog.
     */
    @Override
    public int getResult() {
        return result;
    }

    /**
     * Set the crash data that is displayed in this dialog.
     */
    @Override
    public void setCrashData(final CrashData data) {
        crashData = data;
    }

    /**
     * Set the source of the messages displayed in this dialog.
     */
    @Override
    public void setMessageSource(final MessageSource source) {
        messages = source;
    }

    /**
     * Create and show the dialog. This method blocks until the dialog is
     * closed.
     */
    @SuppressWarnings("nls")
    @Override
    public void showDialog() {
        if ((messages == null) || (crashData == null)) {
            throw new IllegalStateException(
                "The message source and the crash data needs to be set.");
        }
        final JDialog dialog = new JDialog();

        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        dialog.setTitle(messages.getMessage("illarion.common.bug.Title"));
        dialog.setAlwaysOnTop(true);

        final JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        dialog.getContentPane().add(mainPanel);

        final JTextArea introText =
            new JTextArea(messages.getMessage(crashData.getDescription()));
        introText.setMargin(new Insets(10, 10, 10, 10));
        introText.setEditable(false);
        introText.setCursor(null);
        introText.setOpaque(false);
        introText.setFocusable(false);
        mainPanel.add(introText, BorderLayout.NORTH);

        final TextBuilder builder = TextBuilder.newInstance();
        builder.append(messages
            .getMessage("illarion.common.bug.details.Intro"));
        builder.append(NL).append(NL);

        builder.append(messages
            .getMessage("illarion.common.bug.details.Application"));
        builder.append(' ');
        builder.append(crashData.getApplicationName());
        builder.append(NL);

        builder.append(messages
            .getMessage("illarion.common.bug.details.Version"));
        builder.append(' ');
        builder.append(crashData.getApplicationVersion());
        builder.append(NL);

        builder.append(messages.getMessage("illarion.common.bug.details.OS"));
        builder.append(' ');
        builder.append(CrashData.getOSName());
        builder.append(NL);

        builder.append(messages
            .getMessage("illarion.common.bug.details.Thread"));
        builder.append(' ');
        builder.append(crashData.getThreadName());
        builder.append(NL);

        builder.append(messages
            .getMessage("illarion.common.bug.details.Stack"));
        builder.append(NL);
        builder.append(crashData.getStackBacktrace());

        final JTextArea detailsText = new JTextArea(builder.toString());
        TextBuilder.recycle(builder);
        detailsText.setEditable(false);
        detailsText.setCursor(null);
        detailsText.setOpaque(false);
        detailsText.setFocusable(false);

        final JScrollPane detailsScroll = new JScrollPane(detailsText);
        mainPanel.add(detailsScroll, BorderLayout.CENTER);

        final JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        final JButton alwaysButton =
            new JButton(
                messages.getMessage("illarion.common.bug.buttons.always"));
        final JButton onceButton =
            new JButton(
                messages.getMessage("illarion.common.bug.buttons.once"));
        final JButton notButton =
            new JButton(messages.getMessage("illarion.common.bug.buttons.not"));
        final JButton neverButton =
            new JButton(
                messages.getMessage("illarion.common.bug.buttons.never"));

        alwaysButton
            .addActionListener(new ButtonListener(dialog, SEND_ALWAYS));
        onceButton.addActionListener(new ButtonListener(dialog, SEND_ONCE));
        notButton.addActionListener(new ButtonListener(dialog, SEND_NOT));
        neverButton.addActionListener(new ButtonListener(dialog, SEND_NEVER));

        buttonPanel.add(alwaysButton);
        buttonPanel.add(onceButton);
        buttonPanel.add(notButton);
        buttonPanel.add(neverButton);

        dialog.setPreferredSize(new Dimension(550, 300));

        dialog.validate();
        dialog.pack();

        dialog.setLocationRelativeTo(null);

        setResult(SEND_NOT);

        dialog.setVisible(true);
        dialog.dispose();
    }

    /**
     * Set the result value. This is used instead of a synthetic accessor.
     * 
     * @param newResult the new result value
     */
    void setResult(final int newResult) {
        result = newResult;
    }
}
