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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;

/**
 * This command is used to sell or buy a item or close the merchant dialog.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class TradeItemCmd extends AbstractCommand {
    /**
     * This enumerator contains the possible action values of this class.
     */
    private enum Action {
        /**
         * This value is used for the action to sell a item from the player of this client to the NPC.
         */
        sell,

        /**
         * This value is used for the action to buy a item from the NPC.
         */
        buy,

        /**
         * This value is used for the action to close the dialog ID.
         */
        close;
    }

    /**
     * The ID of the dialog to interact with.
     */
    private int dialogId = -1;

    /**
     * The location for the buy or sell operation.
     * <p/>
     * For selling operations this contains {@code 0} to select the inventory, and any other number is handled as the
     * ID of the container.
     * <p/>
     * For buying operations this value contains the index of the item in the list that was send from the merchant
     * that is supposed to be bought.
     */
    private short location;

    /**
     * This value is only used for selling operations. In this case it either marks the slot in the inventory or the
     * slot in the container where the item is supposed to be taken from.
     */
    private int slot;

    /**
     * This value contains the amount of items to buy or sell in this operation.
     */
    private short amount;

    /**
     * The selected action.
     */
    private TradeItemCmd.Action selectedAction;

    /**
     * Default constructor for the trade item command.
     */
    public TradeItemCmd() {
        super(CommandList.CMD_TRADE_ITEM);
    }

    /**
     * Set the ID of the dialog this command refers to.
     *
     * @param newId the ID of the dialog
     */
    public void setDialogId(final int newId) {
        dialogId = newId;
    }

    /**
     * Set this command to transfer a close operation. In this case nothing will be bought or sold. The server is
     * only informed that the dialog was closed.
     */
    public void setCloseDialog() {
        selectedAction = TradeItemCmd.Action.close;
    }

    /**
     * Set the command to sell a item from the inventory.
     *
     * @param invSlot   the number of the inventory slot to take the item from
     * @param itemCount the amount of items to sell from this slot
     */
    public void setSellFromInventory(final int invSlot, final int itemCount) {
        setSellFromContainer(0, invSlot, itemCount);
    }

    /**
     * Set the command to sell a item from a opened item container.
     *
     * @param containerId the ID of the item container
     * @param invSlot     the slot of the item to sell inside the container
     * @param itemCount   the amount of items to sell from this slot
     */
    public void setSellFromContainer(final int containerId, final int invSlot, final int itemCount) {
        selectedAction = TradeItemCmd.Action.sell;
        location = (short) containerId;
        slot = invSlot;
        amount = (short) itemCount;
    }

    /**
     * Set the command to buy a item from the NPC.
     *
     * @param index     the index of the item to buy in the list of item that was send for the menu
     * @param itemCount the amount of items to buy from this type
     */
    public void setBuy(final int index, final int itemCount) {
        selectedAction = TradeItemCmd.Action.buy;
        location = (short) index;
        amount = (short) itemCount;
    }

    @Override
    public TradeItemCmd clone() {
        return new TradeItemCmd();
    }

    @Override
    public void encode(final NetCommWriter writer) {
        if (dialogId == -1) {
            throw new IllegalStateException("Dialog ID was not set properly.");
        }
        writer.writeInt(dialogId);
        switch (selectedAction) {
            case close:
                writer.writeByte((byte) 0);
                break;
            case sell:
                writer.writeByte((byte) 1);
                writer.writeUByte(location);
                writer.writeUShort(slot);
                writer.writeUByte(amount);
                break;
            case buy:
                writer.writeByte((byte) 2);
                writer.writeUByte(location);
                writer.writeUByte(amount);
        }
    }

    @Override
    public String toString() {
        return toString("dialog ID: " + dialogId);
    }
}
