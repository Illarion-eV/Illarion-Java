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
package illarion.common.config.gui.entries.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.entries.FileEntry;
import illarion.common.config.gui.entries.SaveableEntry;
import illarion.common.util.MessageSource;

/**
 * This is a special implementation for the panel that is initialized with a
 * configuration entry. Its sole purpose is the use along with the configuration
 * system. In this case the panel is filled with all components needed to set a
 * file in the configuration properly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class FileEntrySwing extends JPanel implements SaveableEntry {
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
        private static final class Filter extends FileFilter {
            /**
             * The description for that files that is displayed in the dialog.
             */
            private final String description;

            /**
             * The list of regular expressions of the files that are needed to
             * be displayed in the file dialog. Each file only has to match one
             * of this regular expressions.
             */
            private final String validFiles;

            /**
             * The public constructor used so the parent class is able to create
             * a proper instance. It also takes the list of regular expressions
             * that are matched against the files shown in the dialog.
             * 
             * @param files the list of regular expressions
             * @param desc the description that is displayed in the dialog
             */
            public Filter(final String files, final String desc) {
                validFiles = files;
                description = desc;
            }

            /**
             * This method tests all files with the list of regular expressions
             * and allows only those files to be displayed that match the
             * regular expressions. Also it allows the directories to be shown.
             */
            @SuppressWarnings("nls")
            @Override
            public boolean accept(final File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }

                final String[] names = validFiles.split(";");
                for (final String testName : names) {
                    final String fixedTextName =
                        testName.replace(".", "\\.").replace("*", ".+");
                    if (Pattern.matches(testName, fixedTextName)) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * Return the description shown in the file dialog.
             */
            @Override
            public String getDescription() {
                return description;
            }

        }

        /**
         * The entry that is used as data source for the file chooser.
         */
        private final FileEntry cfgEntry;

        /**
         * The source that is used to fetch the texts displayed in this entry.
         */
        private final MessageSource messageSource;

        /**
         * The file entry that is the parent of this class instance.
         */
        private final FileEntrySwing parentEntry;

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
        public ButtonListener(final FileEntrySwing fileEntry,
            final FileEntry cfg, final MessageSource msgSource) {
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
            final JFileChooser fileDiag = new JFileChooser();
            fileDiag.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDiag.setCurrentDirectory(new File(cfgEntry.getDefaultDir()));
            fileDiag.setSelectedFile(new File(cfgEntry.getName()));
            fileDiag.setFileFilter(new Filter(cfgEntry.getFileEndings(),
                cfgEntry.getFileDesc()));
            fileDiag.setDialogTitle(messageSource
                .getMessage("illarion.common.config.gui.file.Title"));
            fileDiag.setVisible(true);

            if (fileDiag.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                final File file = fileDiag.getSelectedFile();
                parentEntry.setCurrentValue(file);
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
    private final FileEntry entry;

    /**
     * The area that displays the selected folder.
     */
    private final JTextField input;

    /**
     * The button that opens the search dialog.
     */
    private final JButton searchBtn;

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
    public FileEntrySwing(final ConfigEntry usedEntry, final MessageSource msgs) {
        super(new BorderLayout(10, 0));
        if (!isUsableEntry(usedEntry)) {
            throw new IllegalArgumentException("ConfigEntry type illegal.");
        }
        entry = (FileEntry) usedEntry;

        currentValue = entry.getValue();

        input = new JTextField(currentValue.toString());
        input.setColumns(20);
        add(input, BorderLayout.CENTER);

        searchBtn =
            new JButton(
                msgs.getMessage("illarion.common.config.gui.file.Browse"));
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
        return (entry instanceof FileEntry);
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
        if (newValue.isFile()) {
            currentValue = newValue;
            input.setText(newValue.getAbsolutePath());
        }
    }

}
