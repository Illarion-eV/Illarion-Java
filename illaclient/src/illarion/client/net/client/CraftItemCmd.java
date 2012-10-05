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
import illarion.common.net.NetCommWriter;

/**
 * This command is used for any interaction with the crafting menu.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class CraftItemCmd extends AbstractCommand {
    /**
     * This enumerator contains the possible action values of this class.
     */
    private enum Action {
        /**
         * Look at a item that can be crafted.
         */
        lookAtItem,

        /**
         * Look at a ingredient that is needed.
         */
        lookAtIngredient,

        /**
         * This value is used in case crafting a item is started
         */
        craft,

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
     * The index of the item in the crafting list that is referred to.
     */
    private int craftingIndex;

    /**
     * The amount of items to be crafted.
     */
    private int amount;

    /**
     * The index of the ingredient that is referred to.
     */
    private int ingredientIndex;

    /**
     * The selected action.
     */
    private CraftItemCmd.Action selectedAction = CraftItemCmd.Action.lookAtItem;

    /**
     * Default constructor for the trade item command.
     */
    public CraftItemCmd() {
        super(CommandList.CMD_CRAFT_ITEM);
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
     * Set this command to transfer a close operation. In case no further items are needed to be crafted and the dialog
     * is closed, this has to be send.
     */
    public void setCloseDialog() {
        selectedAction = CraftItemCmd.Action.close;
    }

    /**
     * Craft a item.
     *
     * @param craftIndex the item of the item to be crafted
     * @param craftCount the amount of items that are supposed to be created
     */
    public void setCraftItem(final int craftIndex, final int craftCount) {
        selectedAction = CraftItemCmd.Action.craft;
        craftingIndex = craftIndex;
        amount = craftCount;
    }

    /**
     * Look at a item to craft at.
     *
     * @param craftIndex the item you want to look at
     */
    public void setLookAtItem(final int craftIndex) {
        selectedAction = CraftItemCmd.Action.lookAtItem;
        craftingIndex = craftIndex;
    }

    /**
     * Set the command to look at a ingredient.
     *
     * @param craftIndex      the item you want to look at
     * @param ingredientIndex the index of the ingredient to look at
     */
    public void setLookAtIngredient(final int craftIndex, final int ingredientIndex) {
        selectedAction = CraftItemCmd.Action.lookAtIngredient;
        craftingIndex = craftIndex;
        this.ingredientIndex = ingredientIndex;
    }

    @Override
    public CraftItemCmd clone() {
        return new CraftItemCmd();
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
            case craft:
                writer.writeByte((byte) 1);
                writer.writeUByte((short) craftingIndex);
                writer.writeUByte((short) amount);
                break;
            case lookAtItem:
                writer.writeByte((byte) 2);
                writer.writeUByte((short) craftingIndex);
                break;
            case lookAtIngredient:
                writer.writeByte((byte) 3);
                writer.writeUByte((short) craftingIndex);
                writer.writeUByte((short) ingredientIndex);
                break;
        }
    }

    @Override
    public String toString() {
        return toString("dialog ID: " + dialogId);
    }
}
