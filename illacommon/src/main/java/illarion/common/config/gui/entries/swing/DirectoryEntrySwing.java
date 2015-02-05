/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.common.config.gui.entries.swing;

import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.entries.DirectoryEntry;
import illarion.common.config.gui.entries.SavableEntry;
import illarion.common.util.MessageSource;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This is a special implementation for the panel that is initialized with a
 * configuration entry. Its sole purpose is the use along with the configuration
 * system. In this case the panel is filled with all components needed to set a
 * directory in the configuration properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DirectoryEntrySwing extends JPanel implements SavableEntry {
    /**
     * The listener that is added to the button. It opens the file dialog in
     * case its requested.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class ButtonListener implements ActionListener {
        /**
         * The file that is applied to the file dialog. It ensures that only the
         * files expected to be visible are shown.
         *
         * @author Martin Karing &lt;nitram@illarion.org&gt;
         */
        private static final class Filter extends FileFilter {
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
            public boolean accept(@Nonnull File pathname) {
                return pathname.isDirectory();
            }

            /**
             * Return the description shown in the file dialog.
             */
            @Nullable
            @Override
            @Contract(value = "-> null", pure = true)
            public String getDescription() {
                return null;
            }
        }

        /**
         * The entry that is used as data source for the file chooser.
         */
        @Nonnull
        private final DirectoryEntry cfgEntry;

        /**
         * The source that is used to fetch the texts displayed in this entry.
         */
        @Nonnull
        private final MessageSource messageSource;

        /**
         * The file entry that is the parent of this class instance.
         */
        @Nonnull
        private final DirectoryEntrySwing parentEntry;

        /**
         * A public constructor that enables the parent class to create a
         * instance of this class properly. It also allows the parent file entry
         * and the configuration entry to be set that are used to create this
         * handler properly.
         *
         * @param fileEntry the file entry that is the parent of this instance
         * @param cfg the configuration entry that is the data source
         * @param msgSource the message source used as source for all texts
         * displayed in this dialog
         */
        public ButtonListener(@Nonnull DirectoryEntrySwing fileEntry, @Nonnull DirectoryEntry cfg,
                              @Nonnull MessageSource msgSource) {
            cfgEntry = cfg;
            parentEntry = fileEntry;
            messageSource = msgSource;
        }

        /**
         * This function called causes the file selection dialog to be
         * displayed.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileDiag = new JFileChooser();
            fileDiag.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileDiag.setCurrentDirectory(new File(cfgEntry.getDefaultDir()));
            fileDiag.setFileFilter(new Filter());
            fileDiag.setDialogTitle(messageSource.getMessage("illarion.common.config.gui.directory.Title"));
            fileDiag.setVisible(true);

            if (fileDiag.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileDiag.getSelectedFile();
                if (file != null) {
                    parentEntry.setCurrentValue(file.toPath());
                }
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
    @Nullable
    private Path currentValue;

    /**
     * The text entry used to initialize this instance.
     */
    @Nonnull
    private final DirectoryEntry entry;

    /**
     * The area that displays the selected folder.
     */
    @Nonnull
    private final JTextField input;

    /**
     * Create a instance of this check entry and set the configuration entry that is used to setup this class.
     *
     * @param usedEntry the entry used to setup this class, the entry needs to pass the check with the static method
     * @param messageSource the message source that is used to fetch the texts displayed in this entry
     */
    public DirectoryEntrySwing(@Nonnull ConfigEntry usedEntry, @Nonnull MessageSource messageSource) {
        super(new BorderLayout(10, 0));
        if (!isUsableEntry(usedEntry)) {
            throw new IllegalArgumentException("ConfigEntry type illegal.");
        }
        entry = (DirectoryEntry) usedEntry;
        currentValue = entry.getValue();

        input = new JTextField((currentValue == null) ? null : currentValue.toString());
        input.setColumns(20);
        add(input, BorderLayout.CENTER);

        JButton searchBtn = new JButton(messageSource.getMessage("illarion.common.config.gui.directory.Browse"));
        searchBtn.addActionListener(new ButtonListener(this, entry, messageSource));
        add(searchBtn, BorderLayout.EAST);

        setMinimumSize(new Dimension(300, 10));
    }

    /**
     * Test a entry if it is usable with this class or not.
     *
     * @param entry the entry to test
     * @return {@code true} in case this entry is usable with this class
     */
    public static boolean isUsableEntry(ConfigEntry entry) {
        return entry instanceof DirectoryEntry;
    }

    /**
     * Save the value in this text entry to the configuration.
     */
    @Override
    public void save() {
        if (currentValue != null) {
            entry.setValue(currentValue);
        }
    }

    /**
     * Set the value currently set in this configuration entry.
     *
     * @param newValue the new value that is set from now on
     */
    void setCurrentValue(@Nonnull Path newValue) {
        if (Files.isDirectory(newValue)) {
            currentValue = newValue;
            input.setText(newValue.toAbsolutePath().toString());
        }
    }
}
