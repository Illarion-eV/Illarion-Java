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

/**
 * This event is raised in case the server caused the client to open a new item container.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class OpenContainerEvent {
    public int getContainerId() {
        return conId;
    }

    public int getSlotCount() {
        return slots;
    }

    public static final class Item {
        private final int id;
        private final int count;

        public Item(final int itemId, final int itemCount) {
            id = itemId;
            count = itemCount;
        }

        public int getItemId() {
            return id;
        }

        public int getCount() {
            return count;
        }
    }

    private final TIntObjectHashMap<OpenContainerEvent.Item> itemMap;
    private final int slots;
    private final int conId;

    public OpenContainerEvent(final int containerId, final int slotCount) {
        itemMap = new TIntObjectHashMap<OpenContainerEvent.Item>(15);
        slots = slotCount;
        conId = containerId;
    }

    public void addItem(final int slot, final OpenContainerEvent.Item item) {
        itemMap.put(slot, item);
    }

    public TIntObjectIterator<OpenContainerEvent.Item> getItemIterator() {
        return itemMap.iterator();
    }
}
