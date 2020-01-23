/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.world.interactive;

import illarion.client.net.client.*;
import illarion.client.world.World;
import illarion.client.world.items.ContainerSlot;
import illarion.client.world.items.InventorySlot;
import illarion.client.world.items.MerchantList;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * This class holds the interactive representation of a inventory slot.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class InteractiveInventorySlot implements Draggable, DropTarget {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(InteractiveInventorySlot.class);
    /**
     * The inventory item this interactive class refers to.
     */
    @Nonnull
    private final InventorySlot parentItem;

    /**
     * Create a new instance of this interactive slot and set the inventory slot it refers to.
     *
     * @param item the inventory item that is the parent of this interactive item
     */
    public InteractiveInventorySlot(@Nonnull InventorySlot item) {
        parentItem = item;
    }

    /**
     * Drag a inventory item to a character. Does nothing currently.
     */
    @Override
    public void dragTo(@Nonnull InteractiveChar targetChar, @Nonnull ItemCount count) {
        // nothing
    }

    /**
     * Drag the item in this inventory slot to another inventory slot.
     */
    @Override
    public void dragTo(@Nonnull InteractiveInventorySlot targetSlot, @Nonnull ItemCount count) {
        if (!isValidItem()) {
            log.error("Tried dragging a invalid item!");
            return;
        }
        ItemId draggedItem = getItemId();
        assert draggedItem != null;

        if (!targetSlot.isAcceptingItem(draggedItem)) {
            return;
        }

        World.getNet().sendCommand(new DragInvInvCmd(getSlotId(), targetSlot.getSlotId(), count));
    }

    /**
     * Drag the item in the inventory to a location on the map.
     *
     * @param targetTile the target location on the map
     * @param count the amount of items to drag to the new location
     */
    @Override
    public void dragTo(@Nonnull InteractiveMapTile targetTile, @Nonnull ItemCount count) {
        if (!isValidItem()) {
            return;
        }

        World.getNet().sendCommand(new DragInvMapCmd(getSlotId(), targetTile.getLocation(), count));
    }

    @Override
    public void dragTo(@Nonnull InteractiveContainerSlot targetSlot, @Nonnull ItemCount count) {
        if (!isValidItem()) {
            return;
        }

        ContainerSlot slot = targetSlot.getSlot();
        World.getNet().sendCommand(new DragInvScCmd(getSlotId(), slot.getContainerId(), slot.getLocation(), count));
    }

    /**
     * Use the item in this inventory slot.
     */
    public void use() {
        if (!isValidItem()) {
            return;
        }

        World.getNet().sendCommand(new UseInventoryCmd(getSlotId()));
    }

    /**
     * Open the container in the inventory.
     */
    public void openContainer() {
        if (!isValidItem()) {
            return;
        }

        World.getNet().sendCommand(new OpenBagCmd());
    }

    /**
     * Request a look at at this inventory slot.
     */
    public void lookAt() {
        if (!isValidItem()) {
            return;
        }

        World.getNet().sendCommand(new LookatInvCmd(getSlotId()));
    }

    /**
     * Check if it is valid to drop a item on this inventory slot.
     *
     * @param itemId the ID of the item that should be dropped on this slot
     * @return {@code true} in case its legal to drop a item with the specified ID on the inventory slot
     */
    public boolean isAcceptingItem(@Nonnull ItemId itemId) {
        return !isValidItem() || itemId.equals(getItemId());
    }

    /**
     * Send the command to the server to sell this item.
     */
    public void sell() {
        if (!World.getPlayer().hasMerchantList()) {
            return;
        }
        MerchantList merchantList = World.getPlayer().getMerchantList();
        assert merchantList != null;

        ItemCount count = parentItem.getCount();
        if (!ItemCount.isGreaterZero(count)) {
            log.error("Tried sell from a slot that contains no items!");
            return;
        }
        assert count != null;

        World.getNet().sendCommand(new SellInventoryItemCmd(merchantList.getId(), getSlotId(), count));
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
     * @return {@code true} in case this interactive item refers to a valid
     * item
     */
    public boolean isValidItem() {
        return parentItem.containsItem();
    }

    /**
     * The ID of the item in this slot.
     *
     * @return the ID of the item in this slot
     */
    @Nullable
    public ItemId getItemId() {
        return parentItem.getItemID();
    }
}
