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
package illarion.common.config.gui.entries.awt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.entries.DirectoryEntry;
import illarion.common.config.gui.entries.SaveableEntry;
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
public final class DirectoryEntryAwt extends Panel implements SaveableEntry {
    /**
     * The listener that is added to the button. It opens the file dialog in
     * case its requested.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class ButtonListener implements ActionListener {
        /**
         * The file that is applied to the file dialog. It ensures that only the
         * files expected to be visible are shown.
         * 
         * @author Martin Karing
         * @since 1.22
         * @version 1.22
         */
        private static final class Filter implements FilenameFilter {
            /**
             * The public constructor used so the parent class is able to create
             * a proper instance.
             */
            public Filter() {
                // nothing to do
            }

            /**
             * This method tests all files with the list of regular expressions
             * and allows only those files to be displayed that match the
             * regular expressions. Also it allows the directories to be shown.
             */
            @Override
            public boolean accept(final File dir, final String name) {
                final File file = new File(dir + File.separator + name);
                return file.isDirectory();
            }

        }

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
        private final DirectoryEntryAwt parentEntry;

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
        public ButtonListener(final DirectoryEntryAwt fileEntry,
            final DirectoryEntry cfg, final MessageSource msgSource) {
            cfgEntry = cfg;
            parentEntry = fileEntry;
            messageSource = msgSource;
        }

        /**
         * This function called causes the file selection dialog to be
         * displayed.
         */
        @SuppressWarnings("nls")
        @Override
        public void actionPerformed(final ActionEvent e) {
            final FileDialog fileDiag =
                new FileDialog((Dialog) null, null, FileDialog.LOAD);
            fileDiag.setAlwaysOnTop(true);
            fileDiag.setDirectory(cfgEntry.getDefaultDir());
            fileDiag.setTitle(messageSource
                .getMessage("illarion.common.config.gui.directory.Title"));
            fileDiag.setLocationByPlatform(true);
            fileDiag.setModalityType(ModalityType.APPLICATION_MODAL);
            fileDiag.setFilenameFilter(new Filter());
            fileDiag.setVisible(true);

            final String directory = fileDiag.getDirectory();
            if (directory != null) {
                final File selectedFile = new File(directory);
                parentEntry.setCurrentValue(selectedFile);
            }
        }
    }

    /**
     * The serialization UID of this file entry.
     */
    private static final long serialVersionUID = 1L;

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
    private final TextField input;

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
     * @param msgs the message source that is used to fetch the texts displayed
     *            in this entry
     */
    @SuppressWarnings("nls")
    public DirectoryEntryAwt(final ConfigEntry usedEntry,
        final MessageSource msgs) {
        super(new BorderLayout(10, 0));
        if (!isUsableEntry(usedEntry)) {
            throw new IllegalArgumentException("ConfigEntry type illegal.");
        }
        entry = (DirectoryEntry) usedEntry;

        currentValue = entry.getValue();

        input = new TextField(currentValue.toString());
        input.setColumns(20);
        add(input, BorderLayout.CENTER);

        searchBtn =
            new Button(
                msgs.getMessage("illarion.common.config.gui.directory.Browse"));
        searchBtn.addActionListener(new ButtonListener(this, entry, msgs));
        add(searchBtn, BorderLayout.EAST);

        setMinimumSize(new Dimension(300, 10));
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
