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

import illarion.common.annotation.NonNull;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

/**
 * This class represents a item container that is displayed currently by the character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemContainer {
    /**
     * This array stores the container slots and their items
     */
    @NonNull
    private final ContainerSlot[] slots;

    /**
     * The ID of the container.
     */
    private final int containerId;

    /**
     * Create a new container with a specified ID.
     *
     * @param id        the ID of the container
     * @param slotCount the amount of slots this container has
     */
    public ItemContainer(final int id, final int slotCount) {
        containerId = id;
        slots = new ContainerSlot[slotCount];
        for (int i = 0; i < slotCount; i++) {
            slots[i] = new ContainerSlot(containerId, i);
        }
    }

    /**
     * Get a container slot that is assigned to a specified ID.
     *
     * @param slotId the slot ID
     * @return the slot that is assigned to the requested id or {@code null} in case no item is applied to this slot
     * @throws IndexOutOfBoundsException in case the {@code slotId} parameter is too small or too large
     */
    @NonNull
    public ContainerSlot getSlot(final int slotId) {
        return slots[slotId];
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
     * Get the amount of slots this container has.
     *
     * @return the slot count
     */
    public int getSlotCount() {
        return slots.length;
    }

    /**
     * Set or change a item in the container.
     *
     * @param slot  the slot to change
     * @param id    the ID of the new item
     * @param count the new item count
     * @throws IndexOutOfBoundsException in case the {@code slot} parameter is too small or too large
     */
    public void setItem(final int slot, @NonNull final ItemId id, @NonNull final ItemCount count) {
        if ((slot < 0) || (slot >= slots.length)) {
            throw new IndexOutOfBoundsException("Requested slot outside of allowed range: " + slot);
        }
        if (ItemId.isValidItem(id)) {
            slots[slot].setData(id, count);
        } else {
            slots[slot].setData(new ItemId(0), ItemCount.getInstance(0));
        }
    }
}
