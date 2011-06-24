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
package illarion.common.config.gui.entries.swt;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.entries.DirectoryEntry;
import illarion.common.util.MessageSource;

/**
 * This is a special implementation for the panel that is initialized with a
 * configuration entry. Its sole purpose is the use along with the configuration
 * system. In this case the panel is filled with all components needed to set a
 * directory in the configuration properly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class DirectoryEntrySwt implements SaveableEntrySwt {
    /**
     * The listener that is added to the button. It opens the file dialog in
     * case its requested.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class ButtonListener implements Listener {
        /**
         * The entry that is used as data source for the file chooser.
         */
        private final DirectoryEntry cfgEntry;

        /**
         * The source that is used to fetch the texts displayed in this entry.
         */
        private final MessageSource messageSource;

        /**
         * The file entry that is the parent of this class instance.
         */
        private final DirectoryEntrySwt parentEntry;

        /**
         * A public constructor that enables the parent class to create a
         * instance of this class properly. It also allows the parent file entry
         * and the configuration entry to be set that are used to create this
         * handler properly.
         * 
         * @param fileEntry the file entry that is the parent of this instance
         * @param cfg the configuration entry that is the data source
         * @param msgSource the message source used as source for all texts
         *            displayed in this dialog
         */
        public ButtonListener(final DirectoryEntrySwt fileEntry,
            final DirectoryEntry cfg, final MessageSource msgSource) {
            cfgEntry = cfg;
            parentEntry = fileEntry;
            messageSource = msgSource;
        }

        @SuppressWarnings("nls")
        @Override
        public void handleEvent(final Event event) {
            final DirectoryDialog fileDiag =
                new DirectoryDialog(parentEntry.getShell(), SWT.SAVE);
            fileDiag.setFilterPath(cfgEntry.getDefaultDir());
            fileDiag.setText(messageSource
                .getMessage("illarion.common.config.gui.directory.Title"));

            final String file = fileDiag.open();
            if (file != null) {
                final File selectedFile = new File(file);
                parentEntry.setCurrentValue(selectedFile);
            }
        }
    }

    /**
     * The current value of this number entry.
     */
    private File currentValue;

    /**
     * The text entry used to initialize this instance.
     */
    private final DirectoryEntry entry;

    /**
     * The area that displays the selected folder.
     */
    private final Text input;

    /**
     * The panel this entry is displayed on.
     */
    private final Composite panel;

    /**
     * The button that opens the search dialog.
     */
    private final Button searchBtn;

    /**
     * Create a instance of this check entry and set the configuration entry
     * that is used to setup this class.
     * 
     * @param usedEntry the entry used to setup this class, the entry needs to
     *            pass the check with the static method
     * @param parentWidget the widget this widget is added to
     * @param msgs the message source that is used to fetch the texts displayed
     *            in this entry
     */
    @SuppressWarnings("nls")
    public DirectoryEntrySwt(final ConfigEntry usedEntry,
        final Composite parentWidget, final MessageSource msgs) {
        if (!isUsableEntry(usedEntry)) {
            throw new IllegalArgumentException("ConfigEntry type illegal.");
        }

        panel = new Composite(parentWidget, SWT.NONE);
        final GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        panel.setLayout(layout);
        entry = (DirectoryEntry) usedEntry;

        currentValue = entry.getValue();

        input = new Text(panel, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        input.setText(currentValue.toString());
        input.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false,
            1, 1));

        searchBtn = new Button(panel, SWT.PUSH | SWT.CENTER);
        searchBtn.setText(msgs
            .getMessage("illarion.common.config.gui.directory.Browse"));
        searchBtn.addListener(SWT.Selection, new ButtonListener(this, entry,
            msgs));
        final GridData displayData =
            new GridData(SWT.FILL, SWT.BEGINNING, false, false, 1, 1);
        displayData.horizontalIndent = 10;
        searchBtn.setLayoutData(displayData);
    }

    /**
     * Test a entry if it is usable with this class or not.
     * 
     * @param entry the entry to test
     * @return <code>true</code> in case this entry is usable with this class
     */
    public static boolean isUsableEntry(final ConfigEntry entry) {
        return (entry instanceof DirectoryEntry);
    }

    /**
     * Save the value in this text entry to the configuration.
     */
    @Override
    public void save() {
        entry.setValue(currentValue);
    }

    /**
     * Set the layout data of this entry.
     */
    @Override
    public void setLayoutData(final Object data) {
        panel.setLayoutData(data);
    }

    /**
     * Get the shell that is the parent of this entry.
     * 
     * @return the first shell that is a parent of this entry
     */
    Shell getShell() {
        return panel.getShell();
    }

    /**
     * Set the value currently set in this configuration entry.
     * 
     * @param newValue the new value that is set from now on
     */
    void setCurrentValue(final File newValue) {
        if (newValue.isDirectory()) {
            currentValue = newValue;
            input.setText(newValue.getAbsolutePath());
        }
    }
}
