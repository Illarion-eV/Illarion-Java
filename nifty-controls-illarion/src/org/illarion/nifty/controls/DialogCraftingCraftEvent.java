/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls;

/**
 * This event is fired in case the player wants to craft something from the crafting dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogCraftingCraftEvent extends DialogEvent {
    /**
     * The item that was bought.
     */
    private final CraftingListEntry item;

    /**
     * The index of the item that is bought.
     */
    private final int itemIndex;

    /**
     * The amount of items to craft
     */
    private final int count;

    /**
     * Create a new instance of this event and set the ID of the dialog that was used to craft items from.
     *
     * @param id         the ID of the dialog
     * @param craftItem  the item to craft
     * @param craftIndex the index of the item to craft
     * @param craftCount the amount of items to craft
     */
    public DialogCraftingCraftEvent(final int id, final CraftingListEntry craftItem, final int craftIndex,
                                    final int craftCount) {
        super(id);
        item = craftItem;
        itemIndex = craftIndex;
        count = craftCount;
    }

    /**
     * Get the item the player wants to craft.
     *
     * @return the item that is created
     */
    public CraftingListEntry getItem() {
        return item;
    }

    /**
     * Get the index of the item the player crafts.
     *
     * @return the index of the item
     */
    public int getItemIndex() {
        return itemIndex;
    }

    /**
     * Get the amount of items to craft.
     *
     * @return the amount of items to craft
     */
    public int getCount() {
        return count;
    }
}
