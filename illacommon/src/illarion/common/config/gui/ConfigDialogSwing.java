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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import javolution.lang.Reflection;
import javolution.lang.Reflection.Constructor;
import javolution.lang.Reflection.Method;
import javolution.text.TextBuilder;
import javolution.util.FastTable;

import org.apache.log4j.Logger;

import illarion.common.config.ConfigDialog;
import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.gui.entries.SaveableEntry;
import illarion.common.util.MessageSource;

/**
 * This method implements the configuration dialog that uses the SWING (
 * {@link javax.swing}) to display the GUI for the configuration.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ConfigDialogSwing extends JDialog {
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
        private final JDialog parentDialog;

        /**
         * The public constructor of this class that allows the parent class to
         * create a proper instance and that takes all data needed for this
         * object to work properly.
         * 
         * @param parent the parent dialog that is closed once this action is
         *            performed
         */
        public CancelButtonListener(final JDialog parent) {
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
        private final JDialog parentDialog;

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
            final JDialog parent) {
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
        "illarion.common.config.gui.entries.swing.CheckEntrySwing",
        "illarion.common.config.gui.entries.swing.TextEntrySwing",
        "illarion.common.config.gui.entries.swing.SelectEntrySwing",
        "illarion.common.config.gui.entries.swing.NumberEntrySwing",
        "illarion.common.config.gui.entries.swing.FileEntrySwing",
        "illarion.common.config.gui.entries.swing.DirectoryEntrySwing" };

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(ConfigDialogSwing.class);

    /**
     * The serialization UID of this dialog.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for this Swing GUI representation of the configuration
     * dialog.
     * 
     * @param dialog the configuration dialog that is needed to be displayed
     * @param msgs the source for the messages to be displayed
     */
    @SuppressWarnings("nls")
    public ConfigDialogSwing(final ConfigDialog dialog,
        final MessageSource msgs) {
        super((JDialog) null, msgs
            .getMessage("illarion.common.config.gui.Title"), true);

        final FastTable<SaveableEntry> contentList =
            new FastTable<SaveableEntry>();

        final JPanel content = new JPanel(new BorderLayout(5, 5));
        add(content);

        {
            final JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            content.add(buttonPanel, BorderLayout.SOUTH);

            final JButton saveButton =
                new JButton(msgs.getMessage("illarion.common.config.gui.Save"));
            saveButton.addActionListener(new SaveButtonListener(contentList
                .unmodifiable(), this));
            saveButton.setPreferredSize(new Dimension(100, 25));
            buttonPanel.add(saveButton);

            final JButton cancelButton =
                new JButton(
                    msgs.getMessage("illarion.common.config.gui.Cancel"));
            cancelButton.addActionListener(new CancelButtonListener(this));
            cancelButton.setPreferredSize(new Dimension(100, 25));
            buttonPanel.add(cancelButton);
        }

        final JTabbedPane tabs = new JTabbedPane();
        content.add(tabs, BorderLayout.CENTER);

        final int pageCount = dialog.getPageCount();
        ConfigDialog.Page currentPage;
        JPanel currentPanel;
        for (int i = 0; i < pageCount; i++) {
            currentPage = dialog.getPage(i);
            currentPanel = new JPanel(new GridBagLayout());
            currentPanel
                .setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tabs.addTab(msgs.getMessage(currentPage.getTitle()), currentPanel);

            final GridBagConstraints con = new GridBagConstraints();
            final int entryCount = currentPage.getEntryCount();
            ConfigDialog.Entry entry;
            for (int k = 0; k < entryCount; k++) {
                entry = currentPage.getEntry(k);

                con.gridwidth = GridBagConstraints.RELATIVE;
                con.gridheight = 1;
                con.fill = GridBagConstraints.HORIZONTAL;
                con.anchor = GridBagConstraints.WEST;
                con.insets.left = 0;
                con.insets.right = 5;
                con.insets.top = 5;
                con.weightx = 0.0;
                currentPanel.add(
                    new JLabel(msgs.getMessage(entry.getTitle())), con);

                con.gridwidth = GridBagConstraints.REMAINDER;
                con.gridheight = 1;
                con.fill = GridBagConstraints.HORIZONTAL;
                con.anchor = GridBagConstraints.WEST;
                con.insets.right = 0;
                con.insets.left = 5;
                con.weightx = 0.5;

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

                        currentPanel.add((JComponent) object, con);
                        contentList.add((SaveableEntry) object);
                        break;
                    }
                    TextBuilder.recycle(builder);
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
