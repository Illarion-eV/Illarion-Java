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
package illarion.client.world;

import illarion.client.graphics.Item;
import illarion.client.resources.ItemFactory;

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
    private int count;

    /**
     * The ID of the item on this slot.
     */
    private int itemId;

    /**
     * Check if this slot stores a item.
     *
     * @return <code>true</code> in case this slot stores a item
     */
    public boolean containsItem() {
        return itemId != 0;
    }

    /**
     * Get the amount of items.
     *
     * @return the item count
     */
    public int getCount() {
        return count;
    }

    /**
     * Get the ID of the item.
     *
     * @return the ID
     */
    public int getItemID() {
        return itemId;
    }

    /**
     * Get the prototype of the item that is located in this slot.
     *
     * @return the item
     */
    public Item getItemPrototype() {
        return ItemFactory.getInstance().getPrototype(itemId);
    }

    /**
     * Set the information's about this item.
     *
     * @param newId    the ID of the item
     * @param newCount the amount of items
     */
    public void setData(final int newId, final int newCount) {
        itemId = newId;
        count = newCount;
    }
}
