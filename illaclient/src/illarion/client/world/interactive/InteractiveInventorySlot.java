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
package illarion.client.world.interactive;

import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;
import illarion.client.net.client.*;
import illarion.client.world.World;
import illarion.client.world.items.InventorySlot;

/**
 * This class holds the interactive representation of a inventory slot.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InteractiveInventorySlot extends AbstractDraggable implements DropTarget, UseTarget {
    /**
     * The ID that is needed to tell the server that the operations refer to a slot in the inventory.
     */
    private static final byte REFERENCE_ID = 3;

    /**
     * The inventory item this interactive class refers to.
     */
    private final InventorySlot parentItem;

    /**
     * Create a new instance of this interactive slot and set the inventory slot it refers to.
     *
     * @param item the inventory item that is the parent of this interactive
     *             item
     */
    public InteractiveInventorySlot(final InventorySlot item) {
        parentItem = item;
    }

    /**
     * Drag a inventory item to a character. Does nothing currently.
     */
    @Override
    public void dragTo(final InteractiveChar targetChar) {
        // nothing
    }

    /**
     * Drag the item in this inventory slot to another inventory slot.
     *
     * @param targetSlot the slot to drag the item to
     */
    @Override
    public void dragTo(final InteractiveInventorySlot targetSlot) {
        if (!isValidItem()) {
            return;
        }

        if (!targetSlot.acceptItem(getItemId())) {
            return;
        }

        final DragInvInvCmd cmd =
                CommandFactory.getInstance().getCommand(
                        CommandList.CMD_DRAG_INV_INV, DragInvInvCmd.class);
        cmd.setDrag(getSlotId(), targetSlot.getSlotId());
        cmd.send();
    }

    public void use() {
        if (!isValidItem()) {
            return;
        }

        final UseCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_USE, UseCmd.class);
        cmd.addUse(this);
        cmd.send();
    }

    public void openContainer() {
        if (!isValidItem()) {
            return;
        }

        final OpenBagCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_OPEN_BAG, OpenBagCmd.class);
        cmd.setShowcase(0);
        cmd.send();
    }

    public void lookAt() {
        if (!isValidItem()) {
            return;
        }

        final LookatInvCmd cmd =
                CommandFactory.getInstance().getCommand(
                        CommandList.CMD_LOOKAT_INV, LookatInvCmd.class);
        cmd.setSlot(this.getSlotId());
        cmd.send();
    }

    public boolean acceptItem(final int itemId) {
        return !isValidItem() || (itemId == getItemId());
    }

    /**
     * Send the command to the server to sell this item.
     */
    public void sell() {
        if (!World.getPlayer().hasMerchantList()) {
            return;
        }

        final TradeItemCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_TRADE_ITEM,
                TradeItemCmd.class);
        cmd.setSellFromInventory(getSlotId(), 1);
        cmd.send();
    }

    /**
     * Drag the item in the inventory to a location on the map.
     *
     * @param targetTile the target location on the map
     */
    @Override
    public void dragTo(final InteractiveMapTile targetTile) {
        if (!isValidItem()) {
            return;
        }

        final DragInvMapCmd cmd =
                CommandFactory.getInstance().getCommand(
                        CommandList.CMD_DRAG_INV_MAP, DragInvMapCmd.class);
        cmd.setDragFrom(getSlotId());
        cmd.setDragTo(targetTile.getLocation());
        cmd.send();
    }

    @Override
    public void dragTo(final InteractiveContainerSlot targetSlot) {
        if (!isValidItem()) {
            return;
        }

        final DragInvScCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_DRAG_INV_SC,
                DragInvScCmd.class);
        cmd.setSource(getSlotId());
        cmd.setTarget(targetSlot.getSlot().getContainerId(), targetSlot.getSlot().getLocation());
        cmd.send();
    }

    /**
     * Encode a use operation with this slot in the inventory.
     *
     * @param writer the writer to receive the encoded data
     */
    @Override
    public void encodeUse(final NetCommWriter writer) {
        writer.writeByte(REFERENCE_ID);
        writer.writeByte((byte) getSlotId());
    }

    /**
     * Get the ID of the slot.
     *
     * @return the location ID
     */
    public int getSlotId() {
        return parentItem.getSlot();
    }

    /**
     * Check if this interactive slot refers to a valid item.
     *
     * @return <code>true</code> in case this interactive item refers to a valid
     *         item
     */
    public boolean isValidItem() {
        return parentItem.containsItem();
    }

    /**
     * The ID of the item in this slot.
     *
     * @return the ID of the item in this slot
     */
    public int getItemId() {
        return parentItem.getItemID();
    }
}
