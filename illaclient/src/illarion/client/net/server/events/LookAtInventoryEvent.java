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

/**
 * This event is triggered once the server sends the response on a look at that was done on a inventory slot.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class LookAtInventoryEvent {
    /**
     * The inventory slot this event is assigned to.
     */
    private final int slot;

    /**
     * The text that is supposed to be displayed as text of this inventory event.
     */
    private final String text;

    /**
     * The value entry that is supposed to be displayed. It contains the price of the item.
     */
    private final long value;

    /**
     * The constructor of that event that applies the values that are supposed to be published along with this event.
     *
     * @param invSlot    the inventory slot this look-at will be bound to
     * @param lookAtText the text that will be displayed in the generated popup
     * @param itemValue  the the price of the item that will be displayed
     */
    public LookAtInventoryEvent(final int invSlot, final String lookAtText, final long itemValue) {
        slot = invSlot;
        text = lookAtText;
        value = itemValue;
    }

    /**
     * The inventory slot the look-at is assigned to.
     *
     * @return the ID of the inventory slot
     */
    public int getSlot() {
        return slot;
    }

    /**
     * The text that is supposed to be displayed in the look-at.
     *
     * @return the look at text
     */
    public String getText() {
        return text;
    }

    /**
     * The price of the item this look-at is assigned to in copper coins.
     *
     * @return item value in copper
     */
    public long getValue() {
        return value;
    }
}
