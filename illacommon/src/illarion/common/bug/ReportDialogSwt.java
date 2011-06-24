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

import javolution.text.TextBuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import illarion.common.util.MessageSource;

/**
 * This is the dialog implementation for SWT. It will display a dialog that
 * contains the error problem description and the error message and allows the
 * user to choose what to do.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ReportDialogSwt implements ReportDialog {
    /**
     * Button listener helper class. This class is assigned to the buttons of
     * the dialog.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private final class ButtonListener implements Listener {
        /**
         * The shell that is closed when this button is used.
         */
        private final Shell closingShell;

        /**
         * The value that will be set as result value in case this button is
         * used.
         */
        private final int newResult;

        /**
         * Create a new instance of this button listener. It will monitor one
         * button and once clicked close the parent shell and set the assigned
         * result value.
         * 
         * @param parentShell the shell that is to be closed
         * @param setResult the result value that should be set
         */
        public ButtonListener(final Shell parentShell, final int setResult) {
            newResult = setResult;
            closingShell = parentShell;
        }

        @Override
        public void handleEvent(final Event event) {
            switch (event.type) {
                case SWT.Selection:
                    setResult(newResult);
                    closingShell.close();
                    closingShell.dispose();
                    break;
            }
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

        final Display display = new Display();
        final Shell shell =
            new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        shell.setLayout(new GridLayout(4, true));

        shell.setText(messages.getMessage("illarion.common.bug.Title"));

        final Label title = new Label(shell, SWT.WRAP);
        title.setText(messages.getMessage(crashData.getDescription()));
        title.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false,
            false, 4, 1));

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

        final Text details =
            new Text(shell, SWT.READ_ONLY | SWT.BORDER | SWT.WRAP
                | SWT.V_SCROLL);
        final GridData layoutGridData =
            new GridData(SWT.FILL, SWT.BEGINNING, false, false, 4, 1);
        layoutGridData.heightHint = 320;
        details.setLayoutData(layoutGridData);
        details.setSize(50, 50);
        details.setText(builder.toString());
        details.setCursor(null);
        TextBuilder.recycle(builder);

        final GridData buttonGridData =
            new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        buttonGridData.widthHint = 135;

        final Button alwaysButton = new Button(shell, SWT.PUSH);
        alwaysButton.setText(messages
            .getMessage("illarion.common.bug.buttons.always"));
        alwaysButton.addListener(SWT.Selection, new ButtonListener(shell,
            SEND_ALWAYS));
        alwaysButton.setLayoutData(buttonGridData);

        final Button onceButton = new Button(shell, SWT.PUSH);
        onceButton.setText(messages
            .getMessage("illarion.common.bug.buttons.once"));
        onceButton.addListener(SWT.Selection, new ButtonListener(shell,
            SEND_ONCE));
        onceButton.setLayoutData(buttonGridData);

        final Button notButton = new Button(shell, SWT.PUSH);
        notButton.setText(messages
            .getMessage("illarion.common.bug.buttons.not"));
        notButton.addListener(SWT.Selection, new ButtonListener(shell,
            SEND_NOT));
        notButton.setLayoutData(buttonGridData);

        final Button neverButton = new Button(shell, SWT.PUSH);
        neverButton.setText(messages
            .getMessage("illarion.common.bug.buttons.never"));
        neverButton.addListener(SWT.Selection, new ButtonListener(shell,
            SEND_NEVER));
        neverButton.setLayoutData(buttonGridData);

        shell.pack();
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                // If no more entries in the event queue
                display.sleep();
            }
        }
        display.dispose();
    }

    /**
     * Set the result value.
     * 
     * @param newResult the new result value
     */
    void setResult(final int newResult) {
        result = newResult;
    }
}
