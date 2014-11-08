/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.common.util;

import javax.annotation.Nonnull;
import java.io.InputStream;

/**
 * This is the special implementation of a table loader that is used to load the map group names of the items.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class TableLoaderMapGroups extends TableLoader {
    /**
     * The index of the table group ID.
     */
    private static final int TB_ID = 0;

    /**
     * The index of the german name in the table row.
     */
    private static final int TB_NAME_GERMAN = 2;

    /**
     * The index of the english name in the table row.
     */
    private static final int TB_NAME_ENGLISH = 3;

    /**
     * Default constructor that just uses the callback to create this loader.
     *
     * @param callback the callback that will receive the data of this loader
     */
    public TableLoaderMapGroups(@Nonnull final TableLoaderSink<TableLoaderMapGroups> callback) {
        this("MapGroup", callback);
    }

    /**
     * Constructor to create this table loader with advanced settings.
     *
     * @param table the name of the table that is supposed to be read using this table loader
     * @param callback the callback that will receive the data of this loader
     */
    public TableLoaderMapGroups(final String table, @Nonnull final TableLoaderSink<TableLoaderMapGroups> callback) {
        super(table, false, callback, ",");
    }

    /**
     * Constructor to create this table loader with advanced settings.
     *
     * @param resource the stream that is read for the data that is used in this table loader
     * @param callback the callback that will receive the data of this loader
     */
    public TableLoaderMapGroups(
            @Nonnull final InputStream resource,
            @Nonnull final TableLoaderSink<TableLoaderMapGroups> callback) {
        super(resource, false, callback, ",");
    }

    /**
     * Get the Index of the map group.
     *
     * @return the index of the map group
     */
    public int getId() {
        return getInt(TB_ID);
    }

    /**
     * Get the german name of the map group.
     *
     * @return the german name of the map group
     */
    public String getNameGerman() {
        return getString(TB_NAME_GERMAN);
    }

    /**
     * Get the english name of the map group.
     *
     * @return the english name of the map group
     */
    public String getNameEnglish() {
        return getString(TB_NAME_ENGLISH);
    }
}
