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

import illarion.client.world.interactive.InteractiveInventorySlot;

/**
 * This class is used to store the data of a single slot in the inventory.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InventorySlot extends AbstractItemSlot {
    /**
     * The interactive reference to this slot.
     */
    private final InteractiveInventorySlot interactive;

    /**
     * The inventory slot this instance refers to
     */
    private final int slot;

    /**
     * Create a inventory slot for the inventory.
     *
     * @param itemSlot the inventory slot
     */
    public InventorySlot(final int itemSlot) {
        slot = itemSlot;
        interactive = new InteractiveInventorySlot(this);
    }

    /**
     * Get the interactive inventory slot that refers to this inventory slot.
     *
     * @return the interactive inventory slot
     */
    public InteractiveInventorySlot getInteractive() {
        return interactive;
    }

    /**
     * Get the slot of this inventory item.
     *
     * @return the slot of the item
     */
    public int getSlot() {
        return slot;
    }
}
