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

import gnu.trove.map.hash.TIntObjectHashMap;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

/**
 * This class represents a item container that is displayed currently by the character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemContainer {
    /**
     * This map contains all slots that are present and filled with an item.
     */
    private final TIntObjectHashMap<ContainerSlot> slots;

    /**
     * The ID of the container.
     */
    private final int containerId;

    /**
     * Create a new container with a specified ID.
     *
     * @param id the ID of the container
     */
    public ItemContainer(final int id) {
        containerId = id;
        slots = new TIntObjectHashMap<ContainerSlot>();
    }

    /**
     * Get the ID of this container.
     *
     * @return the ID of the container
     */
    public int getContainerId() {
        return containerId;
    }

    /**
     * Set or change a item in the container.
     *
     * @param slot  the slot to change
     * @param id    the ID of the new item
     * @param count the new item count
     */
    public void setItem(final int slot, final ItemId id, final ItemCount count) {
        if (ItemId.isValidItem(id)) {
            removeItem(slot);
        } else {
            final ContainerSlot containerSlot;
            if (slots.containsKey(slot)) {
                containerSlot = slots.get(slot);
            } else {
                containerSlot = new ContainerSlot(containerId, slot);
                slots.put(slot, containerSlot);
            }
            containerSlot.setData(id, count);
        }
    }

    /**
     * Remove a item from a specified slot.
     *
     * @param slot the slot to remove the item from
     */
    public void removeItem(final int slot) {
        if (slots.containsKey(slot)) {
            slots.remove(slot);
        }
    }

    /**
     * Get a container slot that is assigned to a specified ID.
     *
     * @param slotId the slot ID
     * @return the slot that is assigned to the requested id
     */
    public ContainerSlot getSlot(final int slotId) {
        if (slots.containsKey(slotId)) {
            return slots.get(slotId);
        } else {
            final ContainerSlot containerSlot;
            if (slots.containsKey(slotId)) {
                containerSlot = slots.get(slotId);
            } else {
                containerSlot = new ContainerSlot(containerId, slotId);
                slots.put(slotId, containerSlot);
            }
            return containerSlot;
        }
    }
}
