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
package illarion.client.net.server.events;

import illarion.client.world.items.CraftingItem;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * This event is send once the server sends a crafting dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class DialogCraftingReceivedEvent {
    /**
     * The ID of the dialog.
     */
    private final int requestId;

    /**
     * The title of the dialog.
     */
    private final String title;

    /**
     * The names of the groups the items belong to.
     */
    @Nonnull
    private final String[] groups;

    /**
     * The items that can be crafted.
     */
    @Nonnull
    private final CraftingItem[] craftItems;

    /**
     * Constructor of this event.
     *
     * @param requestId the ID of the dialog that is send
     * @param title the title of the dialog that is displayed
     * @param groups the names of the groups
     * @param craftItems the items that can be crafted
     */
    public DialogCraftingReceivedEvent(
            final int requestId,
            final String title,
            @Nonnull final String[] groups,
            @Nonnull final CraftingItem[] craftItems) {
        this.requestId = requestId;
        this.title = title;
        this.groups = Arrays.copyOf(groups, groups.length);
        this.craftItems = Arrays.copyOf(craftItems, craftItems.length);
    }

    /**
     * Get the dialog ID.
     *
     * @return the dialog Id
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Get the title of the dialog.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the amount of groups.
     *
     * @return the groups
     */
    public int getGroupCount() {
        return groups.length;
    }

    /**
     * Get the name of a group.
     *
     * @param index the index of the group
     * @return the name of the group
     */
    public String getGroupTitle(final int index) {
        return groups[index];
    }

    /**
     * Get the amount of items that can be crafted.
     *
     * @return the amount of items
     */
    public int getCraftingItemCount() {
        return craftItems.length;
    }

    /**
     * Get a crafting item.
     *
     * @param index the index of the item
     * @return the crafting item
     */
    public CraftingItem getCraftingItem(final int index) {
        return craftItems[index];
    }
}
