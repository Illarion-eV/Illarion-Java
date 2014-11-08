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

import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * This event is raised in case the server caused the client to open a new item container.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class OpenContainerEvent implements ServerEvent {
    /**
     * Get the ID of the container.
     *
     * @return the container ID
     */
    public int getContainerId() {
        return conId;
    }

    /**
     * Get the amount of slots in this container.
     *
     * @return the slot count
     */
    public int getSlotCount() {
        return slots;
    }

    @Nonnull
    public String getTitle() {
        return title;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    /**
     * This class represents a single item that is placed in the container.
     */
    public static final class Item {
        /**
         * The slot where the item is placed.
         */
        private final int slot;

        /**
         * The ID of the item.
         */
        private final ItemId id;

        /**
         * The size of the item stack.
         */
        private final ItemCount count;

        /**
         * Constructor for a new item.
         *
         * @param slot the slot of the item
         * @param itemId the item ID
         * @param itemCount the stack count
         */
        public Item(int slot, ItemId itemId, ItemCount itemCount) {
            this.slot = slot;
            id = itemId;
            count = itemCount;
        }

        /**
         * Get the ID of the item.
         *
         * @return the item ID
         */
        public ItemId getItemId() {
            return id;
        }

        /**
         * Get the size of the item stack.
         *
         * @return the size of the stack
         */
        public ItemCount getCount() {
            return count;
        }

        public int getSlot() {
            return slot;
        }
    }

    /**
     * The items that are stored in the container.
     */
    @Nonnull
    private final Collection<Item> items;

    /**
     * The amount of slots in this container.
     */
    private final int slots;

    /**
     * The ID of the container.
     */
    private final int conId;

    @Nonnull
    private final String title;

    @Nonnull
    private final String description;

    /**
     * Constructor of the container opened event.
     *
     * @param containerId the ID of the container
     * @param title the title of the container
     * @param description the description of the container
     * @param slotCount the amount of slots in the container
     */
    public OpenContainerEvent(int containerId, @Nonnull String title, @Nonnull String description, int slotCount) {
        this.title = title;
        this.description = description;
        items = new ArrayList<>();
        slots = slotCount;
        conId = containerId;
    }

    /**
     * Add a item to this container.
     *
     * @param item the item to be added
     */
    public void addItem(@Nonnull Item item) {
        items.add(item);
    }

    /**
     * Get the list of items stored in this event.
     *
     * @return the items
     */
    @Nonnull
    public Collection<Item> getItems() {
        return Collections.unmodifiableCollection(items);
    }
}
