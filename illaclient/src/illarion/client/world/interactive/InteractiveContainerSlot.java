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
import illarion.client.net.client.*;
import illarion.client.world.World;
import illarion.client.world.items.ContainerSlot;
import illarion.common.net.NetCommWriter;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

/**
 * This class holds the interactive representation of a inventory slot.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InteractiveContainerSlot extends AbstractDraggable implements DropTarget, UseTarget {
    /**
     * The container slot this interactive reference points to.
     */
    private final ContainerSlot parentSlot;

    /**
     * Create a new instance of this interactive slot and set the container slot it refers to.
     *
     * @param slot the container slot this reference points to
     */
    public InteractiveContainerSlot(final ContainerSlot slot) {
        parentSlot = slot;
    }

    /**
     * Drag a inventory item to a character. Does nothing currently.
     */
    @Override
    public void dragTo(final InteractiveChar targetChar, final ItemCount count) {
        // nothing
    }

    /**
     * Drag the item in this inventory slot to another inventory slot.
     *
     * @param targetSlot the slot to drag the item to
     */
    @Override
    public void dragTo(final InteractiveInventorySlot targetSlot, final ItemCount count) {
        if (!targetSlot.acceptItem(getItemId())) {
            return;
        }

        final DragScInvCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_DRAG_SC_INV, DragScInvCmd.class);
        cmd.setSource(getContainerId(), parentSlot.getLocation());
        cmd.setTarget(targetSlot.getSlotId());
        cmd.setCount(count);
        cmd.send();
    }

    public void use() {
        final UseCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_USE, UseCmd.class);
        cmd.addUse(this);
        cmd.send();
    }

    public void lookAt() {
        final LookatShowcaseCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_LOOKAT_SHOWCASE,
                LookatShowcaseCmd.class);
        cmd.setSlot(getContainerId(), parentSlot.getLocation());
        cmd.send();
    }

    public boolean acceptItem(final ItemId itemId) {
        return !isValidItem() || itemId.equals(getItemId());
    }

    /**
     * Drag the item in the inventory to a location on the map.
     *
     * @param targetTile the target location on the map
     */
    @Override
    public void dragTo(final InteractiveMapTile targetTile, final ItemCount count) {
        final DragScMapCmd cmd =
                CommandFactory.getInstance().getCommand(
                        CommandList.CMD_DRAG_SC_MAP, DragScMapCmd.class);
        cmd.setSource(getContainerId(), parentSlot.getLocation());
        cmd.setTarget(targetTile.getLocation());
        cmd.setCount(count);
        cmd.send();
    }

    @Override
    public void dragTo(final InteractiveContainerSlot targetSlot, final ItemCount count) {
        final DragScScCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_DRAG_SC_SC, DragScScCmd.class);
        cmd.setSource(getContainerId(), parentSlot.getLocation());
        cmd.setTarget(targetSlot.getSlot().getContainerId(), targetSlot.getSlot().getLocation());
        cmd.setCount(count);
        cmd.send();
    }

    /**
     * Encode a use operation with this slot in the inventory.
     *
     * @param writer the writer to receive the encoded data
     */
    @Override
    public void encodeUse(final NetCommWriter writer) {
        writer.writeByte((byte) parentSlot.getContainerId());
        writer.writeByte((byte) getSlotId());
    }

    /**
     * Get the ID of the slot.
     *
     * @return the location ID
     */
    public int getSlotId() {
        return parentSlot.getLocation();
    }

    public int getContainerId() {
        return parentSlot.getContainerId();
    }

    public ContainerSlot getSlot() {
        return parentSlot;
    }

    /**
     * The ID of the item in this slot.
     *
     * @return the ID of the item in this slot
     */
    public ItemId getItemId() {
        return parentSlot.getItemID();
    }

    /**
     * Sell the item from the slot to the trader.
     */
    public void sell() {
        if (!World.getPlayer().hasMerchantList()) {
            return;
        }

        final TradeItemCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_TRADE_ITEM,
                TradeItemCmd.class);
        cmd.setSellFromContainer(getContainerId(), getSlotId(), parentSlot.getCount());
        cmd.setDialogId(World.getPlayer().getMerchantList().getId());
        cmd.send();
    }

    public boolean isValidItem() {
        return (getItemId() != null) && (getItemId().getValue() > 0);
    }
}
