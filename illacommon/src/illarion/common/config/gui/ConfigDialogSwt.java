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

import java.util.List;

import javolution.lang.Reflection;
import javolution.lang.Reflection.Constructor;
import javolution.lang.Reflection.Method;
import javolution.text.TextBuilder;
import javolution.util.FastTable;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import illarion.common.config.ConfigDialog;
import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.gui.entries.SaveableEntry;
import illarion.common.config.gui.entries.swt.SaveableEntrySwt;
import illarion.common.util.MessageSource;

/**
 * This method implements the configuration dialog that uses the standard window
 * toolkit (SWT {@link org.eclipse.swt}) to display the GUI for the
 * configuration.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ConfigDialogSwt {
    /**
     * The listener of the cancel button for this dialog. It causes the dialog
     * to be closed.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class CancelButtonListener implements Listener {
        /**
         * The parent dialog that is supposed to be closed once the action is
         * performed.
         */
        private final Shell parentDialog;

        /**
         * The public constructor of this class that allows the parent class to
         * create a proper instance and that takes all data needed for this
         * object to work properly.
         * 
         * @param parent the parent dialog that is closed once this action is
         *            performed
         */
        public CancelButtonListener(final Shell parent) {
            parentDialog = parent;
        }

        /**
         * This method is called in case the button this object is bound to is
         * pressed. In case its called the parent dialog is closed.
         */
        @Override
        public void handleEvent(final Event event) {
            parentDialog.close();
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
    private static final class SaveButtonListener implements Listener {
        /**
         * The parent dialog that is supposed to be closed once the action is
         * performed.
         */
        private final Shell parentDialog;

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
            final Shell parent) {
            todoList = saveList;
            parentDialog = parent;
        }

        /**
         * This method is called in case the button this object is bound to is
         * pressed. In case its called all elements are saved and the parent
         * dialog is closed.
         */
        @Override
        public void handleEvent(final Event event) {
            final int count = todoList.size();
            for (int i = 0; i < count; i++) {
                todoList.get(i).save();
            }
            parentDialog.close();
            parentDialog.dispose();
        }
    }

    /**
     * This is the list of possible entries that are checked to be added to the
     * configuration dialog.
     */
    @SuppressWarnings("nls")
    private static final String[] KNOWN_ENTRIES = new String[] {
        "illarion.common.config.gui.entries.swt.CheckEntrySwt",
        "illarion.common.config.gui.entries.swt.TextEntrySwt",
        "illarion.common.config.gui.entries.swt.SelectEntrySwt",
        "illarion.common.config.gui.entries.swt.NumberEntrySwt",
        "illarion.common.config.gui.entries.swt.FileEntrySwt",
        "illarion.common.config.gui.entries.swt.DirectoryEntrySwt" };

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(ConfigDialogSwt.class);

    /**
     * Constructor for this SWT GUI representation of the configuration dialog.
     * 
     * @param dialog the configuration dialog that is needed to be displayed
     * @param msgs the source for the messages to be displayed
     */
    @SuppressWarnings("nls")
    public ConfigDialogSwt(final ConfigDialog dialog, final MessageSource msgs) {

        final FastTable<SaveableEntry> contentList =
            new FastTable<SaveableEntry>();

        final Display display = new Display();
        final Shell window =
            new Shell(display, SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE
                | SWT.BORDER);
        window.setText(msgs.getMessage("illarion.common.config.gui.Title"));
        window.setLayout(new GridLayout(2, true));

        final TabFolder tabArea = new TabFolder(window, SWT.TOP);
        tabArea.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false,
            false, 2, 1));

        {
            final GridData button1GridData =
                new GridData(SWT.RIGHT, SWT.BEGINNING, false, false);
            button1GridData.widthHint = 100;
            button1GridData.horizontalIndent = 15;

            final GridData button2GridData =
                new GridData(SWT.LEFT, SWT.BEGINNING, false, false);
            button2GridData.widthHint = 100;
            button2GridData.horizontalIndent = 15;

            final Button saveButton = new Button(window, SWT.PUSH);
            saveButton.setText(msgs
                .getMessage("illarion.common.config.gui.Save"));
            saveButton.addListener(SWT.Selection, new SaveButtonListener(
                contentList, window));
            saveButton.setLayoutData(button1GridData);

            final Button closeButton = new Button(window, SWT.PUSH);
            closeButton.setText(msgs
                .getMessage("illarion.common.config.gui.Cancel"));
            closeButton.addListener(SWT.Selection, new CancelButtonListener(
                window));
            closeButton.setLayoutData(button2GridData);
        }

        final int pageCount = dialog.getPageCount();
        ConfigDialog.Page currentPage;
        Composite currentPanel;
        TabItem currentTab;

        final GridData titleGridData =
            new GridData(SWT.RIGHT, SWT.BEGINNING, false, false);
        titleGridData.verticalIndent = 4;
        final GridData contentGridData =
            new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        contentGridData.verticalIndent = 4;
        contentGridData.widthHint = 300;

        for (int i = 0; i < pageCount; i++) {
            currentPage = dialog.getPage(i);
            currentPanel =
                new Composite(tabArea, SWT.NO_FOCUS | SWT.NO_BACKGROUND
                    | SWT.NO_MERGE_PAINTS | SWT.NO_RADIO_GROUP | SWT.EMBEDDED);
            currentPanel.setLayout(new GridLayout(2, false));

            currentTab = new TabItem(tabArea, SWT.NONE);
            currentTab.setText(msgs.getMessage(currentPage.getTitle()));
            currentTab.setControl(currentPanel);

            final int entryCount = currentPage.getEntryCount();
            ConfigDialog.Entry entry;
            Label title;
            for (int k = 0; k < entryCount; k++) {
                entry = currentPage.getEntry(k);

                title = new Label(currentPanel, SWT.SHADOW_NONE);
                title.setText(msgs.getMessage(entry.getTitle()));
                title.setLayoutData(titleGridData);

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
                        int parameters = 2;
                        builder.setLength(0);
                        builder.append(currentClass);
                        builder.append('(');
                        builder.append(ConfigEntry.class.getName());
                        builder.append(',');
                        builder.append(Composite.class.getName());
                        builder.append(')');
                        Constructor constructor =
                            Reflection.getInstance().getConstructor(
                                builder.toString());

                        if (constructor == null) {
                            parameters = 3;
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
                        if (parameters == 2) {
                            object =
                                constructor.newInstance(
                                    entry.getConfigEntry(), currentPanel);
                        } else {
                            object =
                                constructor
                                    .newInstance(entry.getConfigEntry(),
                                        currentPanel, msgs);
                        }

                        final SaveableEntrySwt newObject =
                            (SaveableEntrySwt) object;
                        newObject.setLayoutData(contentGridData);
                        contentList.add(newObject);
                        break;
                    }
                    TextBuilder.recycle(builder);
                }
            }
        }

        window.pack(true);
        window.pack(false);
        window.open();

        while (!window.isDisposed()) {
            if (!display.readAndDispatch()) {
                // If no more entries in the event queue
                display.sleep();
            }
        }
        window.dispose();
    }
}
