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
package illarion.common.config.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javolution.lang.Reflection;
import javolution.lang.Reflection.Constructor;
import javolution.lang.Reflection.Method;
import javolution.text.TextBuilder;
import javolution.util.FastTable;

import org.apache.log4j.Logger;

import com.magelang.tabsplitter.TabNamePanel;
import com.magelang.tabsplitter.TabPanel;

import illarion.common.config.ConfigDialog;
import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.gui.entries.SaveableEntry;
import illarion.common.util.MessageSource;

/**
 * This method implements the configuration dialog that uses the abstract window
 * toolkit (AWT {@link java.awt}) to display the GUI for the configuration.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ConfigDialogAwt extends Dialog {
    /**
     * The listener of the cancel button for this dialog. It causes the dialog
     * to be closed.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class CancelButtonListener implements ActionListener {
        /**
         * The parent dialog that is supposed to be closed once the action is
         * performed.
         */
        private final Dialog parentDialog;

        /**
         * The public constructor of this class that allows the parent class to
         * create a proper instance and that takes all data needed for this
         * object to work properly.
         * 
         * @param parent the parent dialog that is closed once this action is
         *            performed
         */
        public CancelButtonListener(final Dialog parent) {
            parentDialog = parent;
        }

        /**
         * This method is called in case the button this object is bound to is
         * pressed. In case its called the parent dialog is closed.
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            parentDialog.setVisible(false);
            parentDialog.dispose();
        }
    }

    /**
     * The listener of the save button for this dialog. It causes all elements
     * to be saved and the dialog to be closed.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class SaveButtonListener implements ActionListener {
        /**
         * The parent dialog that is supposed to be closed once the action is
         * performed.
         */
        private final Dialog parentDialog;

        /**
         * The list that stores all save able items of this dialog. When the
         * action is performed all this items are saved.
         */
        private final List<SaveableEntry> todoList;

        /**
         * The public constructor of this class that allows the parent class to
         * create a proper instance and that takes all data needed for this
         * object to work properly.
         * 
         * @param saveList the list of objects saved in case this action is
         *            performed
         * @param parent the parent dialog that is closed once this action is
         *            performed
         */
        public SaveButtonListener(final List<SaveableEntry> saveList,
            final Dialog parent) {
            todoList = saveList;
            parentDialog = parent;
        }

        /**
         * This method is called in case the button this object is bound to is
         * pressed. In case its called all elements are saved and the parent
         * dialog is closed.
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            final int count = todoList.size();
            for (int i = 0; i < count; i++) {
                todoList.get(i).save();
            }
            parentDialog.setVisible(false);
            parentDialog.dispose();
        }
    }

    /**
     * This is the list of possible entries that are checked to be added to the
     * configuration dialog.
     */
    @SuppressWarnings("nls")
    private static final String[] KNOWN_ENTRIES = new String[] {
        "illarion.common.config.gui.entries.awt.CheckEntryAwt",
        "illarion.common.config.gui.entries.awt.TextEntryAwt",
        "illarion.common.config.gui.entries.awt.SelectEntryAwt",
        "illarion.common.config.gui.entries.awt.NumberEntryAwt",
        "illarion.common.config.gui.entries.awt.FileEntryAwt",
        "illarion.common.config.gui.entries.awt.DirectoryEntryAwt" };

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(ConfigDialogAwt.class);

    /**
     * The serialization UID of this dialog.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for this AWT GUI representation of the configuration dialog.
     * 
     * @param dialog the configuration dialog that is needed to be displayed
     * @param msgs the source for the messages to be displayed
     */
    @SuppressWarnings("nls")
    public ConfigDialogAwt(final ConfigDialog dialog, final MessageSource msgs) {
        super((Dialog) null, msgs
            .getMessage("illarion.common.config.gui.Title"), true);

        final FastTable<SaveableEntry> contentList =
            new FastTable<SaveableEntry>();

        final Panel content = new Panel(new BorderLayout(5, 5));
        add(content);

        {
            final Panel buttonPanel =
                new Panel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            content.add(buttonPanel, BorderLayout.SOUTH);

            final Button saveButton =
                new Button(msgs.getMessage("illarion.common.config.gui.Save"));
            saveButton.addActionListener(new SaveButtonListener(contentList
                .unmodifiable(), this));
            saveButton.setPreferredSize(new Dimension(100, 25));
            buttonPanel.add(saveButton);

            final Button cancelButton =
                new Button(
                    msgs.getMessage("illarion.common.config.gui.Cancel"));
            cancelButton.addActionListener(new CancelButtonListener(this));
            cancelButton.setPreferredSize(new Dimension(100, 25));
            buttonPanel.add(cancelButton);
        }

        final TabPanel tabs = new TabPanel();
        content.add(tabs, BorderLayout.CENTER);

        final int pageCount = dialog.getPageCount();
        ConfigDialog.Page currentPage;
        TabNamePanel currentPanel;
        for (int i = 0; i < pageCount; i++) {
            currentPage = dialog.getPage(i);
            currentPanel = new TabNamePanel(new GridBagLayout());
            currentPanel.setName(msgs.getMessage(currentPage.getTitle()));
            currentPanel.setTabName(currentPanel.getName());

            tabs.add(currentPanel);

            final GridBagConstraints con = new GridBagConstraints();
            final int entryCount = currentPage.getEntryCount();
            ConfigDialog.Entry entry;
            for (int k = 0; k < entryCount; k++) {
                entry = currentPage.getEntry(k);

                con.gridwidth = GridBagConstraints.RELATIVE;
                con.gridheight = 1;
                con.fill = GridBagConstraints.HORIZONTAL;
                con.anchor = GridBagConstraints.WEST;

                currentPanel.add(new Label(msgs.getMessage(entry.getTitle())),
                    con);

                con.gridwidth = GridBagConstraints.REMAINDER;
                con.gridheight = 1;
                con.fill = GridBagConstraints.HORIZONTAL;
                con.anchor = GridBagConstraints.WEST;

                for (final String currentClass : KNOWN_ENTRIES) {
                    final TextBuilder builder = TextBuilder.newInstance();
                    builder.append(currentClass);
                    builder.append(".isUsableEntry");
                    builder.append('(');
                    builder.append(ConfigEntry.class.getName());
                    builder.append(')');
                    final Method testMethod =
                        Reflection.getInstance().getMethod(builder.toString());
                    if (testMethod == null) {
                        LOGGER.error("Configuration entry class not found: "
                            + currentClass);
                        break;
                    }
                    if (Boolean.TRUE.equals(testMethod.invoke(null,
                        entry.getConfigEntry()))) {
                        int parameters = 1;
                        builder.setLength(0);
                        builder.append(currentClass);
                        builder.append('(');
                        builder.append(ConfigEntry.class.getName());
                        builder.append(')');
                        Constructor constructor =
                            Reflection.getInstance().getConstructor(
                                builder.toString());

                        if (constructor == null) {
                            parameters = 2;
                            builder.setLength(builder.length() - 1);
                            builder.append(',');
                            builder.append(MessageSource.class.getName());
                            builder.append(')');
                            constructor =
                                Reflection.getInstance().getConstructor(
                                    builder.toString());
                        }

                        TextBuilder.recycle(builder);
                        if (constructor == null) {
                            LOGGER.error("Required constructor not found: "
                                + currentClass);
                            break;
                        }
                        Object object;
                        if (parameters == 1) {
                            object =
                                constructor
                                    .newInstance(entry.getConfigEntry());
                        } else {
                            object =
                                constructor.newInstance(
                                    entry.getConfigEntry(), msgs);
                        }

                        currentPanel.add((Component) object, con);
                        contentList.add((SaveableEntry) object);
                        break;
                    }
                    TextBuilder.recycle(builder);
                }
            }
        }

        validate();
        pack();

        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setLocationRelativeTo(null);

        setVisible(true);
    }
}
