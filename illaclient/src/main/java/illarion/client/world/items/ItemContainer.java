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

import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class represents a item container that is displayed currently by the character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemContainer {
    /**
     * This array stores the container slots and their items
     */
    @Nonnull
    private final ContainerSlot[] slots;

    /**
     * The ID of the container.
     */
    private final int containerId;

    @Nonnull
    private final String description;

    @Nonnull
    private final String title;

    /**
     * Create a new container with a specified ID.
     *
     * @param id the ID of the container
     * @param title the title of the container
     * @param description the description of the container
     * @param slotCount the amount of slots this container has
     */
    public ItemContainer(int id, @Nonnull String title, @Nonnull String description, int slotCount) {
        containerId = id;
        this.title = title;
        this.description = description;
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
    @Nonnull
    public ContainerSlot getSlot(int slotId) {
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
     * @param slot the slot to change
     * @param id the ID of the new item
     * @param count the new item count
     * @throws IndexOutOfBoundsException in case the {@code slot} parameter is too small or too large
     */
    public void setItem(int slot, @Nullable ItemId id, @Nullable ItemCount count) {
        if ((slot < 0) || (slot >= slots.length)) {
            throw new IndexOutOfBoundsException("Requested slot outside of allowed range: " + slot);
        }
        if (ItemId.isValidItem(id) && ItemCount.isGreaterZero(count)) {
            slots[slot].setData(id, count);
        } else {
            slots[slot].clearSlot();
        }
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    @Nonnull
    public String getTitle() {
        return title;
    }
}
