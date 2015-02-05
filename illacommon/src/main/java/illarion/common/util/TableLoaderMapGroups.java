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
package illarion.common.util;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

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
    public TableLoaderMapGroups(@Nonnull TableLoaderSink<TableLoaderMapGroups> callback) {
        super("MapGroup", false, callback, ",");
    }

    /**
     * Get the Index of the map group.
     *
     * @return the index of the map group
     */
    @Contract(pure = true)
    public int getId() {
        return getInt(TB_ID);
    }

    /**
     * Get the german name of the map group.
     *
     * @return the german name of the map group
     */
    @Nonnull
    @Contract(pure = true)
    public String getNameGerman() {
        return getString(TB_NAME_GERMAN);
    }

    /**
     * Get the english name of the map group.
     *
     * @return the english name of the map group
     */
    @Nonnull
    @Contract(pure = true)
    public String getNameEnglish() {
        return getString(TB_NAME_ENGLISH);
    }
}
