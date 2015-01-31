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
package illarion.common.config.gui;

import illarion.common.config.ConfigDialog;
import illarion.common.config.ConfigDialog.Entry;
import illarion.common.config.ConfigDialog.Page;
import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.gui.entries.SavableEntry;
import illarion.common.config.gui.entries.swing.*;
import illarion.common.util.MessageSource;
import javolution.util.FastTable;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * This method implements the configuration dialog that uses the SWING (
 * {@link javax.swing}) to display the GUI for the configuration.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConfigDialogSwing extends JDialog {
    /**
     * The listener of the cancel button for this dialog. It causes the dialog
     * to be closed.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class CancelButtonListener implements ActionListener {
        /**
         * The parent dialog that is supposed to be closed once the action is
         * performed.
         */
        @Nonnull
        private final JDialog parentDialog;

        /**
         * The public constructor of this class that allows the parent class to
         * create a proper instance and that takes all data needed for this
         * object to work properly.
         *
         * @param parent the parent dialog that is closed once this action is
         * performed
         */
        public CancelButtonListener(@Nonnull JDialog parent) {
            parentDialog = parent;
        }

        /**
         * This method is called in case the button this object is bound to is
         * pressed. In case its called the parent dialog is closed.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            parentDialog.setVisible(false);
            parentDialog.dispose();
        }
    }

    /**
     * The listener of the save button for this dialog. It causes all elements to be saved and the dialog to be closed.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class SaveButtonListener implements ActionListener {
        /**
         * The parent dialog that is supposed to be closed once the action is performed.
         */
        @Nonnull
        private final JDialog parentDialog;

        /**
         * The list that stores all save able items of this dialog. When the action is performed all this items are
         * saved.
         */
        @Nonnull
        private final List<SavableEntry> todoList;

        /**
         * The public constructor of this class that allows the parent class to create a proper instance and that
         * takes all data needed for this object to work properly.
         *
         * @param saveList the list of objects saved in case this action is performed
         * @param parent the parent dialog that is closed once this action is performed
         */
        public SaveButtonListener(@Nonnull List<SavableEntry> saveList, @Nonnull JDialog parent) {
            //noinspection AssignmentToCollectionOrArrayFieldFromParameter
            todoList = saveList;
            parentDialog = parent;
        }

        /**
         * This method is called in case the button this object is bound to is
         * pressed. In case its called all elements are saved and the parent
         * dialog is closed.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            for (SavableEntry aTodoList : todoList) {
                aTodoList.save();
            }
            parentDialog.setVisible(false);
            parentDialog.dispose();
        }
    }

    /**
     * The serialization UID of this dialog.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for this Swing GUI representation of the configuration
     * dialog.
     *
     * @param dialog the configuration dialog that is needed to be displayed
     */
    public ConfigDialogSwing(@Nonnull ConfigDialog dialog) {
        super((JDialog) null, dialog.getMessageSource().getMessage("illarion.common.config.gui.Title"), true);

        MessageSource msgs = dialog.getMessageSource();

        FastTable<SavableEntry> contentList = new FastTable<>();

        JPanel content = new JPanel(new BorderLayout(5, 5));
        add(content);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        content.add(buttonPanel, BorderLayout.SOUTH);

        JButton saveButton = new JButton(msgs.getMessage("illarion.common.config.gui.Save"));
        saveButton.addActionListener(new SaveButtonListener(contentList.unmodifiable(), this));
        saveButton.setPreferredSize(new Dimension(100, 25));
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton(msgs.getMessage("illarion.common.config.gui.Cancel"));
        cancelButton.addActionListener(new CancelButtonListener(this));
        cancelButton.setPreferredSize(new Dimension(100, 25));
        buttonPanel.add(cancelButton);

        JTabbedPane tabs = new JTabbedPane();
        content.add(tabs, BorderLayout.CENTER);

        int pageCount = dialog.getPageCount();
        for (int i = 0; i < pageCount; i++) {
            Page currentPage = dialog.getPage(i);
            JPanel currentPanel = new JPanel(new GridBagLayout());
            currentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tabs.addTab(msgs.getMessage(currentPage.getTitle()), currentPanel);

            GridBagConstraints con = new GridBagConstraints();
            int entryCount = currentPage.getEntryCount();
            for (int k = 0; k < entryCount; k++) {
                Entry entry = currentPage.getEntry(k);

                con.gridwidth = GridBagConstraints.RELATIVE;
                con.gridheight = 1;
                con.fill = GridBagConstraints.HORIZONTAL;
                con.anchor = GridBagConstraints.WEST;
                con.insets.left = 0;
                con.insets.right = 5;
                con.insets.top = 5;
                con.weightx = 0.0;
                currentPanel.add(new JLabel(msgs.getMessage(entry.getTitle())), con);

                con.gridwidth = GridBagConstraints.REMAINDER;
                con.gridheight = 1;
                con.fill = GridBagConstraints.HORIZONTAL;
                con.anchor = GridBagConstraints.WEST;
                con.insets.right = 0;
                con.insets.left = 5;
                con.weightx = 0.5;

                ConfigEntry configEntry = entry.getConfigEntry();
                if (CheckEntrySwing.isUsableEntry(configEntry)) {
                    CheckEntrySwing swingEntry = new CheckEntrySwing(configEntry);
                    currentPanel.add(swingEntry, con);
                    contentList.add(swingEntry);
                } else if (DirectoryEntrySwing.isUsableEntry(configEntry)) {
                    DirectoryEntrySwing swingEntry = new DirectoryEntrySwing(configEntry, msgs);
                    currentPanel.add(swingEntry, con);
                    contentList.add(swingEntry);
                } else if (FileEntrySwing.isUsableEntry(configEntry)) {
                    FileEntrySwing swingEntry = new FileEntrySwing(configEntry, msgs);
                    currentPanel.add(swingEntry, con);
                    contentList.add(swingEntry);
                } else if (NumberEntrySwing.isUsableEntry(configEntry)) {
                    NumberEntrySwing swingEntry = new NumberEntrySwing(configEntry);
                    currentPanel.add(swingEntry, con);
                    contentList.add(swingEntry);
                } else if (SelectEntrySwing.isUsableEntry(configEntry)) {
                    SelectEntrySwing swingEntry = new SelectEntrySwing(configEntry);
                    currentPanel.add(swingEntry, con);
                    contentList.add(swingEntry);
                } else if (TextEntrySwing.isUsableEntry(configEntry)) {
                    TextEntrySwing swingEntry = new TextEntrySwing(configEntry);
                    currentPanel.add(swingEntry, con);
                    contentList.add(swingEntry);
                }
            }

            con.weighty = 0.5;
            con.gridwidth = 2;
            currentPanel.add(new JLabel(), con);
        }

        validate();
        pack();

        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setLocationRelativeTo(null);

        setVisible(true);
    }
}
