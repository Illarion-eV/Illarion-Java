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
package illarion.client.net.server.events;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

import javax.annotation.Nonnull;

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

    /**
     * This class represents a single item that is placed in the container.
     */
    public static final class Item {
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
         * @param itemId    the item ID
         * @param itemCount the stack count
         */
        public Item(final ItemId itemId, final ItemCount itemCount) {
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
    }

    /**
     * The items that are stored in the container.
     */
    @Nonnull
    private final TIntObjectHashMap<OpenContainerEvent.Item> itemMap;

    /**
     * The amount of slots in this container.
     */
    private final int slots;

    /**
     * The ID of the container.
     */
    private final int conId;

    /**
     * Constructor of the container opened event.
     *
     * @param containerId the ID of the container
     * @param slotCount   the amount of slots in the container
     */
    public OpenContainerEvent(final int containerId, final int slotCount) {
        itemMap = new TIntObjectHashMap<OpenContainerEvent.Item>(15);
        slots = slotCount;
        conId = containerId;
    }

    /**
     * Add a item to this container.
     *
     * @param slot the slot where the item is located
     * @param item the item to be added
     */
    public void addItem(final int slot, final OpenContainerEvent.Item item) {
        itemMap.put(slot, item);
    }

    /**
     * Get the iterator over all the items.
     *
     * @return the item iterator
     */
    public TIntObjectIterator<OpenContainerEvent.Item> getItemIterator() {
        return itemMap.iterator();
    }
}
