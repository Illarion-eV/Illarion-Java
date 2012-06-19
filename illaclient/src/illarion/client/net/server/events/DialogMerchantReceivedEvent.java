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

import illarion.client.world.items.MerchantItem;

import java.util.Arrays;

/**
 * This event is send to the application once a merchant dialog is requested.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class DialogMerchantReceivedEvent extends AbstractDialogReceivedEvent {
    /**
     * The list of items sold by this NPC.
     */
    private final MerchantItem[] items;

    /**
     * Create a new instance of this event.
     *
     * @param dialogId    the ID of this dialog
     * @param dialogTitle the title of the dialog
     * @param tradeItems  a array of items that can be bought from the NPC
     */
    public DialogMerchantReceivedEvent(final int dialogId, final String dialogTitle, final MerchantItem[] tradeItems) {
        super(dialogId, dialogTitle);
        items = Arrays.copyOf(tradeItems, tradeItems.length);
    }

    /**
     * Get the amount of items stored in this event.
     *
     * @return the amount of items
     */
    public int getItemCount() {
        return items.length;
    }

    /**
     * Get the item at a specified index in this event.
     *
     * @param index the index of the item
     * @return the merchant item
     */
    public MerchantItem getItem(final int index) {
        return items[index];
    }
}
