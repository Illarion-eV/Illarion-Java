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
package illarion.common.config;

import illarion.common.config.entries.ConfigEntry;
import illarion.common.util.MessageSource;
import javolution.util.FastTable;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * This class allows the construction of a abstract configuration dialog that is
 * turned upon request into a configuration dialog fitting into one of the GUI
 * systems.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConfigDialog {
    /**
     * This class describes a entry in the configuration dialog. Each line on a
     * page of the configuration dialog is a entry. Entries contain in general a
     * title and a control element.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    public static final class Entry {
        /**
         * The configuration entry that is displayed next to the title.
         */
        @Nonnull
        private final ConfigEntry entry;

        /**
         * The title of the entry.
         */
        @Nonnull
        private final String title;

        /**
         * Create a new entry with preset values.
         *
         * @param entryTitle the title of that entry that is displayed as name of this entry
         * @param configEntry the configuration entry that defines what value is controlled how
         */
        public Entry(@Nonnull String entryTitle, @Nonnull ConfigEntry configEntry) {
            title = entryTitle;
            entry = configEntry;
        }

        /**
         * Get the configuration entry that is set to this entry and that defines what configuration element is
         * controlled and how.
         *
         * @return the configuration entry of this entry
         */
        @Nonnull
        @Contract(pure = true)
        public ConfigEntry getConfigEntry() {
            return entry;
        }

        /**
         * Get the title of this entry.
         *
         * @return The title of this entry
         */
        @Nonnull
        @Contract(pure = true)
        public String getTitle() {
            return title;
        }
    }

    /**
     * This class describes a page of the configuration dialog. Each page is displayed as a tab in the dialog. Each
     * page contains a title that is displayed in the tab and a list of entries.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    public static final class Page implements Iterable<Entry> {
        /**
         * The list of entries that is stored on this page.
         */
        @Nonnull
        private final List<Entry> lines;

        /**
         * The title of this page. This text is displayed in the tab for this
         * page.
         */
        @Nonnull
        private final String title;

        /**
         * Initialize the page with a title but without any entries.
         *
         * @param pageTitle the title of the page that is displayed in the tab
         */
        public Page(@Nonnull String pageTitle) {
            lines = new FastTable<>();
            title = pageTitle;
        }

        /**
         * Add a entry to this page.
         *
         * @param entry the entry that is supposed to be added to this page
         */
        public void addEntry(@Nonnull Entry entry) {
            lines.add(entry);
        }

        /**
         * Get a entry with a specified index.
         *
         * @param index the index of the entry requested
         * @return the entry at the specified index
         * @throws IndexOutOfBoundsException if the index is lesser then zero or larger or equal to
         * {@link #getEntryCount()}
         */
        @Nonnull
        public Entry getEntry(int index) {
            Entry entry = lines.get(index);
            if (entry == null) {
                throw new IllegalStateException("Entry of a config dialog contained a illegal value. Null?!");
            }
            return entry;
        }

        /**
         * Get the amount of entries stored on this page.
         *
         * @return the amount of entries on this page
         */
        @Contract(pure = true)
        public int getEntryCount() {
            return lines.size();
        }

        /**
         * Get the title of this page. The title is displayed as name of the tab for this page.
         *
         * @return the title of this page
         */
        @Nonnull
        @Contract(pure = true)
        public String getTitle() {
            return title;
        }

        @Nonnull
        @Override
        @Contract(pure = true)
        public Iterator<Entry> iterator() {
            return lines.iterator();
        }
    }

    /**
     * The configuration that is used as data source for the entries. Also this
     * configuration is used to save the values set in the dialog.
     */
    @Nullable
    private Config cfg;

    /**
     * The message source that is used.
     */
    @Nullable
    private MessageSource messages;

    /**
     * The list of pages displayed in this configuration dialog.
     */
    @Nonnull
    private final List<Page> pages;

    /**
     * Initialize a configuration dialog. This prepares all required values.
     */
    public ConfigDialog() {
        pages = new FastTable<>();
    }

    /**
     * Add a page to this configuration dialog. Each page is displayed as
     * separated tab.
     *
     * @param page the page to add to this dialog
     */
    public void addPage(@Nonnull Page page) {
        pages.add(page);
        if (cfg != null) {
            for (@Nonnull Entry entry : page) {
                entry.getConfigEntry().setConfig(cfg);
            }
        }
    }

    /**
     * Get a page of this dialog at a specified index.
     *
     * @param index the index of the page requested
     * @return the page at the specified index
     * @throws IndexOutOfBoundsException in case the index is lesser then 0 or greater or equal then
     * {@link #getPageCount()}
     */
    @Nonnull
    public Page getPage(int index) {
        Page result = pages.get(index);
        if (result == null) {
            throw new IllegalStateException("Config dialog page contained illegal data. Null?!");
        }
        return result;
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
    public void setConfig(@Nullable Config config) {
        cfg = config;
        if (config != null) {
            for (@Nonnull Page page : pages) {
                for (@Nonnull Entry entry : page) {
                    entry.getConfigEntry().setConfig(config);
                }
            }
        }
    }

    /**
     * Set the message source of this configuration dialog.
     *
     * @param msgSource the message source of this configuration dialog
     */
    public void setMessageSource(@Nonnull MessageSource msgSource) {
        messages = msgSource;
    }

    @Nullable
    @Contract(pure = true)
    public MessageSource getMessageSource() {
        return messages;
    }
}
