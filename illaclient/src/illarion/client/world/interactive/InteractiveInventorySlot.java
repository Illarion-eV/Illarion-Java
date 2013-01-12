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

import illarion.client.net.client.*;
import illarion.client.world.World;
import illarion.client.world.items.ContainerSlot;
import illarion.client.world.items.InventorySlot;
import illarion.client.world.items.MerchantList;
import illarion.common.annotation.NonNull;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

/**
 * This class holds the interactive representation of a inventory slot.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InteractiveInventorySlot implements Draggable, DropTarget {
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
    public void dragTo(@NonNull final InteractiveChar targetChar, @NonNull final ItemCount count) {
        // nothing
    }

    /**
     * Drag the item in this inventory slot to another inventory slot.
     *
     * @param targetSlot the slot to drag the item to
     */
    @Override
    public void dragTo(@NonNull final InteractiveInventorySlot targetSlot, @NonNull final ItemCount count) {
        if (!isValidItem()) {
            return;
        }

        if (!targetSlot.acceptItem(getItemId())) {
            return;
        }

        World.getNet().sendCommand(new DragInvInvCmd(getSlotId(), targetSlot.getSlotId(), count));
    }

    public void use() {
        if (!isValidItem()) {
            return;
        }

        World.getNet().sendCommand(new UseInventoryCmd(getSlotId()));
    }

    public void openContainer() {
        if (!isValidItem()) {
            return;
        }

        World.getNet().sendCommand(new OpenBagCmd());
    }

    public void lookAt() {
        if (!isValidItem()) {
            return;
        }

        World.getNet().sendCommand(new LookatInvCmd(getSlotId()));
    }

    public boolean acceptItem(final ItemId itemId) {
        return !isValidItem() || itemId.equals(getItemId());
    }

    /**
     * Send the command to the server to sell this item.
     */
    public void sell() {
        if (!World.getPlayer().hasMerchantList()) {
            return;
        }

        final MerchantList merchantList = World.getPlayer().getMerchantList();
        if (merchantList == null) {
            return;
        }

        World.getNet().sendCommand(new SellInventoryItemCmd(merchantList.getId(), getSlotId(), parentItem.getCount()));
    }

    /**
     * Drag the item in the inventory to a location on the map.
     *
     * @param targetTile the target location on the map
     * @param count
     */
    @Override
    public void dragTo(@NonNull final InteractiveMapTile targetTile, @NonNull final ItemCount count) {
        if (!isValidItem()) {
            return;
        }

        World.getNet().sendCommand(new DragInvMapCmd(getSlotId(), targetTile.getLocation(), count));
    }

    @Override
    public void dragTo(@NonNull final InteractiveContainerSlot targetSlot, @NonNull final ItemCount count) {
        if (!isValidItem()) {
            return;
        }

        final ContainerSlot slot = targetSlot.getSlot();
        World.getNet().sendCommand(new DragInvScCmd(getSlotId(), slot.getContainerId(), slot.getLocation(), count));
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
    public ItemId getItemId() {
        return parentItem.getItemID();
    }
}
