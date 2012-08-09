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

import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.TradeItemCmd;
import illarion.client.world.events.CloseDialogEvent;
import org.bushe.swing.event.EventBus;

/**
 * This classes are used to store to information about the goods a merchant is trading.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MerchantList {
    /**
     * This is the list of items the merchant is trading.
     */
    private final MerchantItem[] itemList;

    /**
     * This is the ID of the list.
     */
    private final int listId;

    /**
     * Create a new instance of this list with the specified ID.
     *
     * @param id    the ID used to refer to this list
     * @param count the amount of items in this list
     */
    public MerchantList(final int id, final int count) {
        listId = id;
        itemList = new MerchantItem[count];
    }

    /**
     * Set the item that is stored at a specified index.
     *
     * @param index the index of the item to set
     * @param item  the item to set at this slot
     */
    public void setItem(final int index, final MerchantItem item) {
        itemList[index] = item;
    }

    /**
     * Get one item of this merchant list.
     *
     * @param index the index of the requested item
     * @return the merchant item at this entry
     * @throws ArrayIndexOutOfBoundsException in case the index is too large or too small
     */
    public MerchantItem getItem(final int index) {
        return itemList[index];
    }

    /**
     * The amount of items in this list.
     *
     * @return the count of items
     */
    public int getItemCount() {
        return itemList.length;
    }

    /**
     * Get the ID of this merchant list.
     *
     * @return the ID of the merchant list
     */
    public int getId() {
        return listId;
    }

    /**
     * Close this dialog by sending the command to the server that orders the dialog to close.
     */
    public void closeDialog() {
        final TradeItemCmd cmd = (TradeItemCmd) CommandFactory.getInstance().getCommand(CommandList.CMD_TRADE_ITEM);
        cmd.setCloseDialog();
        cmd.setDialogId(listId);
        cmd.send();

        EventBus.publish(new CloseDialogEvent(listId, CloseDialogEvent.DialogType.Merchant));
    }

    /**
     * Buy this item.
     *
     * @param item  the index of the item to buy
     * @param count the amount of items to buy
     */
    public void buyItem(final MerchantItem item, final int count) {
        if (itemList[item.getIndex()] != item) {
            throw new IllegalArgumentException("This item is not part of this merchant list");
        }
        buyItem(item.getIndex(), count);
    }

    /**
     * Buy this item.
     *
     * @param index the index of the item to buy
     * @param count the amount of items to buy
     */
    public void buyItem(final int index, final int count) {
        final TradeItemCmd cmd = (TradeItemCmd) CommandFactory.getInstance().getCommand(CommandList.CMD_TRADE_ITEM);
        cmd.setBuy(index, count);
        cmd.send();
    }
}
