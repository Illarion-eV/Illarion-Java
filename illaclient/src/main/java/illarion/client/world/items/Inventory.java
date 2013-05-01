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

import illarion.client.world.World;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

import javax.annotation.Nonnull;

/**
 * This class is used to store the current inventory of the player character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Inventory {
    /**
     * The amount of available inventory slots.
     */
    public static final int SLOT_COUNT = 18;

    /**
     * The items stored in this inventory.
     */
    @Nonnull
    private final InventorySlot[] slots;

    /**
     * Prepare the internal data structures to store the items in this
     * inventory.
     */
    public Inventory() {
        slots = new InventorySlot[SLOT_COUNT];
        for (int i = 0; i < SLOT_COUNT; i++) {
            slots[i] = new InventorySlot(i);
        }
    }

    /**
     * Get the inventory item at one slot.
     *
     * @param slot the slot
     * @return the item in the slot
     * @throws IndexOutOfBoundsException in case {@code slot} is outside of the valid range
     */
    @Nonnull
    public InventorySlot getItem(final int slot) {
        return slots[slot];
    }

    /**
     * Change a item in the inventory.
     *
     * @param slot  the slot to change
     * @param id    the ID of the new item
     * @param count the new item count
     */
    public void setItem(final int slot, @Nonnull final ItemId id, @Nonnull final ItemCount count) {
        slots[slot].setData(id, count);
        World.getGameGui().getInventoryGui().setItemSlot(slot, id, count);
    }
}
