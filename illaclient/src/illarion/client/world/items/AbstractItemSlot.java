/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world.items;

import illarion.client.graphics.Item;
import illarion.client.resources.ItemFactory;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the abstract item slot that contains all functions shared by the different item slots, like the inventory
 * slots or the container slots.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractItemSlot {
    /**
     * The count of items on this slot.
     */
    @Nullable
    private ItemCount count;

    /**
     * The ID of the item on this slot.
     */
    @Nullable
    private ItemId itemId;

    /**
     * Check if this slot stores a item.
     *
     * @return {@code true} in case this slot stores a item
     */
    public boolean containsItem() {
        return ItemId.isValidItem(itemId);
    }

    /**
     * Get the amount of items.
     *
     * @return the item count or {@code null} in case there is not item in this slot
     */
    @Nullable
    public ItemCount getCount() {
        return count;
    }

    /**
     * Get the ID of the item.
     *
     * @return the ID or {@code null} in case there is not item in this slot
     */
    @Nullable
    public ItemId getItemID() {
        return itemId;
    }

    /**
     * Get the prototype of the item that is located in this slot.
     *
     * @return the item or {@code null} in case there is not item in this slot
     */
    @Nullable
    public Item getItemPrototype() {
        if ((itemId == null) || (itemId.getValue() == 0)) {
            return null;
        }
        return ItemFactory.getInstance().getPrototype(itemId.getValue());
    }

    /**
     * Set the information's about this item.
     *
     * @param newId    the ID of the item
     * @param newCount the amount of items
     */
    public void setData(@Nonnull final ItemId newId, @Nonnull final ItemCount newCount) {
        itemId = newId;
        count = newCount;
    }

    /**
     * Empty this slot.
     */
    public void clearSlot() {
        itemId = null;
        count = null;
    }
}
