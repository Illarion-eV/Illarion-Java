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
package illarion.common.config;

import java.util.List;

import javolution.lang.Reflection;
import javolution.lang.Reflection.Constructor;
import javolution.text.TextBuilder;
import javolution.util.FastTable;

import org.apache.log4j.Logger;

import illarion.common.config.entries.ConfigEntry;
import illarion.common.util.MessageSource;

/**
 * This class allows the construction of a abstract configuration dialog that is
 * turned upon request into a configuration dialog fitting into one of the GUI
 * systems.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ConfigDialog {
    /**
     * This class describes a entry in the configuration dialog. Each line on a
     * page of the configuration dialog is a entry. Entries contain in general a
     * title and a control element.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    public static final class Entry {
        /**
         * The configuration entry that is displayed next to the title.
         */
        private ConfigEntry entry;

        /**
         * The title of the entry.
         */
        private String title;

        /**
         * Create a new entry without values. This entry is not valid to be used
         * in that state. Its absolutely needed to set the values with setTitle
         * and setEntry.
         */
        public Entry() {
            title = null;
            entry = null;
        }

        /**
         * Create a new entry with preset values.
         * 
         * @param entryTitle the title of that entry that is displayed as name
         *            of this entry
         * @param configEntry the configuration entry that defines what value is
         *            controlled how
         */
        public Entry(final String entryTitle, final ConfigEntry configEntry) {
            title = entryTitle;
            entry = configEntry;
        }

        /**
         * Get the configuration entry that is set to this entry and that
         * defines what configuration element is controlled and how.
         * 
         * @return the configuration entry of this entry
         */
        public ConfigEntry getConfigEntry() {
            return entry;
        }

        /**
         * Get the title of this entry.
         * 
         * @return The title of this entry
         */
        public String getTitle() {
            return title;
        }

        /**
         * Set the entry that is displayed next to the title.
         * 
         * @param configEntry the entry that is displayed
         */
        public void setEntry(final ConfigEntry configEntry) {
            entry = configEntry;
        }

        /**
         * Set the title of this entry.
         * 
         * @param entryTitle the title of the entry
         */
        public void setTitle(final String entryTitle) {
            title = entryTitle;
        }
    }

    /**
     * This class describes a page of the configuration dialog. Each page is
     * displayed as a tab in the dialog. Each page contains a title that is
     * displayed in the tab and a list of entries.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    public static final class Page {
        /**
         * The list of entries that is stored on this page.
         */
        private final List<Entry> lines;

        /**
         * The title of this page. This text is displayed in the tab for this
         * page.
         */
        private String title;

        /**
         * Initialize the page without any entries or a title.
         */
        public Page() {
            lines = FastTable.newInstance();
        }

        /**
         * Initialize the page with a title but without any entries.
         * 
         * @param pageTitle the title of the page that is displayed in the tab
         */
        public Page(final String pageTitle) {
            this();
            title = pageTitle;
        }

        /**
         * Initialize the page with a title and a set of entries.
         * 
         * @param pageTitle the title of the page that is displayed in the tab
         * @param entries the entries that are added to the page
         */
        public Page(final String pageTitle, final Entry... entries) {
            this(pageTitle);
            for (final Entry entrie : entries) {
                lines.add(entrie);
            }
        }

        /**
         * Add a entry to this page.
         * 
         * @param entry the entry that is supposed to be added to this page
         */
        public void addEntry(final Entry entry) {
            lines.add(entry);
        }

        /**
         * Get a entry with a specified index.
         * 
         * @param index the index of the entry requested
         * @return the entry at the specified index
         * @throws IndexOutOfBoundsException if the index is lesser then zero or
         *             larger or equal to {@link #getEntryCount()}
         */
        public Entry getEntry(final int index) {
            return lines.get(index);
        }

        /**
         * Get the amount of entries stored on this page.
         * 
         * @return the amount of entries on this page
         */
        public int getEntryCount() {
            return lines.size();
        }

        /**
         * Get the title of this page. The title is displayed as name of the tab
         * for this page.
         * 
         * @return the title of this page
         */
        public String getTitle() {
            return title;
        }

        /**
         * Set the title of this page. The title is displayed in the tab.
         * 
         * @param pageTitle the title of this page
         */
        public void setTitle(final String pageTitle) {
            title = pageTitle;
        }
    }

    /**
     * This constant set as display system means that the configuration dialog
     * is displayed using the AWT.
     */
    public static final int DISPLAY_AWT = 0;

    /**
     * This constant set as display system means that the configuration dialog
     * is displayed using Swing.
     */
    public static final int DISPLAY_SWING = 1;

    /**
     * This constant set as display system means that the configuration dialog
     * is displayed using the SWT.
     */
    public static final int DISPLAY_SWT = 2;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ConfigDialog.class);

    /**
     * The configuration that is used as data source for the entries. Also this
     * configuration is used to save the values set in the dialog.
     */
    private Config cfg;

    /**
     * The display system used in this configuration dialog. Allowed values are
     * {@link #DISPLAY_AWT}, {@link #DISPLAY_SWING} and {@link #DISPLAY_SWT}.
     */
    private int displaySystem;

    /**
     * The message source that is used.
     */
    private MessageSource messages;

    /**
     * The list of pages displayed in this configuration dialog.
     */
    private final List<Page> pages;

    /**
     * Initialize a configuration dialog. This prepares all required values.
     */
    public ConfigDialog() {
        pages = FastTable.newInstance();
        displaySystem = DISPLAY_AWT;
    }

    /**
     * Add a page to this configuration dialog. Each page is displayed as
     * separated tab.
     * 
     * @param page the page to add to this dialog
     */
    public void addPage(final Page page) {
        pages.add(page);
    }

    /**
     * Get a page of this dialog at a specified index.
     * 
     * @param index the index of the page requested
     * @return the page at the specified index
     * @throws IndexOutOfBoundsException in case the index is lesser then 0 or
     *             greater or equal then {@link #getPageCount()}
     */
    public Page getPage(final int index) {
        return pages.get(index);
    }

    /**
     * The amount of pages added to this dialog.
     * 
     * @return the pages added to this dialog
     */
    public int getPageCount() {
        return pages.size();
    }

    /**
     * Set the configuration used in this dialog. Its used as data source for
     * the entries. Also this configuration is used to save the values set in
     * the dialog.
     * 
     * @param config the configuration
     */
    public void setConfig(final Config config) {
        cfg = config;
    }

    /**
     * Set the display system used to display the configuration dialog.
     * 
     * @param newDisplay the constant of the display system
     * @see #DISPLAY_AWT
     * @see #DISPLAY_SWING
     * @see #DISPLAY_SWT
     */
    public void setDisplaySystem(final int newDisplay) {
        displaySystem = newDisplay;
    }

    /**
     * Set the message source of this configuration dialog.
     * 
     * @param msgs the message source of this configuration dialog
     */
    public void setMessageSource(final MessageSource msgs) {
        messages = msgs;
    }

    /**
     * Display the configuration dialog. This method blocks the execution until
     * the dialog is closed again. In case the user hits the save button it will
     * also save the values to the configuration before the method returns.
     */
    @SuppressWarnings("nls")
    public void show() {
        for (int page = 0; page < pages.size(); page++) {
            final Page currentPage = pages.get(page);
            for (int entry = 0; entry < currentPage.getEntryCount(); entry++) {
                currentPage.getEntry(entry).getConfigEntry().setConfig(cfg);
            }
        }

        Constructor constructor = null;
        final TextBuilder builder = TextBuilder.newInstance();
        builder.append("illarion.common.config.gui.ConfigDialog");
        switch (displaySystem) {
            case DISPLAY_AWT:
                builder.append("Awt");
                break;
            case DISPLAY_SWING:
                builder.append("Swing");
                break;
            case DISPLAY_SWT:
                builder.append("Swt");
                break;
            default:
                LOGGER.error("Invalid display system selected");
        }
        builder.append('(');
        builder.append(ConfigDialog.class.getName());
        builder.append(',');
        builder.append(MessageSource.class.getName());
        builder.append(')');
        constructor =
            Reflection.getInstance().getConstructor(builder.toString());

        TextBuilder.recycle(builder);

        if (constructor != null) {
            constructor.newInstance(this, messages);
        }
    }
}
