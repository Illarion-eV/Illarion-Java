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
package illarion.client.world.items;

import illarion.common.types.ItemId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This item is the entry of a selection dialog that contains a item reference along with a name.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class SelectionItem {
    /**
     * The index of this selection item.
     */
    private final int index;

    /**
     * The ID of the item.
     */
    @Nullable
    private final ItemId id;

    /**
     * The name of the selection item.
     */
    @Nonnull
    private final String name;

    protected SelectionItem(@Nonnull SelectionItem org) {
        index = org.index;
        id = org.id;
        name = org.name;
    }

    /**
     * Create a new instance of this selection item and set the values needed.
     *
     * @param itemIndex the index of this entry
     * @param itemId the item ID of this item
     * @param itemName the item name of this item
     */
    public SelectionItem(int itemIndex, @Nullable ItemId itemId, @Nonnull String itemName) {
        index = itemIndex;
        id = itemId;
        name = itemName;
    }

    /**
     * Get the ID of the item.
     *
     * @return the ID of the item
     */
    @Nullable
    public ItemId getId() {
        return id;
    }

    /**
     * Get the name of the item.
     *
     * @return the name of the item
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Get the index of the item.
     *
     * @return the index of the item
     */
    public int getIndex() {
        return index;
    }
}
