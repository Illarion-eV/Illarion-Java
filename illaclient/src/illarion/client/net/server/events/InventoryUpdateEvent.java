/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server.events;

/**
 * This event is used to publish updates of the inventory to the rest of
 * the client.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InventoryUpdateEvent {
    /**
     * The ID of the item that is now in the inventory slot.
     */
    private final int item;
    
    /**
     * The slot of the inventory.
     */
    private final int slot;
    
    /**
     * The amount of items in this inventory slot.
     */
    private final int count;
    
    /**
     * Constructor to this update event.
     * 
     * @param itemId the ID of the item
     * @param slotId the ID of the slot
     * @param count the amount of items
     */
    public InventoryUpdateEvent(final int itemId, final int slotId, final int count) {
        item = itemId;
        slot = slotId;
        this.count = count;
    }
    
    /**
     * Get the ID of the item.
     * 
     * @return the item ID
     */
    public int getItemId() {
        return item;
    }
    
    /**
     * Get the ID of the inventory slot.
     * 
     * @return the ID of the inventory slot
     */
    public int getSlotId() {
        return slot;
    }
    
    /**
     * Get the amount of items in the slot.
     * 
     * @return the amount of items in the slot
     */
    public int getCount() {
        return count;
    }
}