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

import illarion.client.graphics.Item;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.client.world.items.ContainerSlot;
import illarion.client.world.items.InventorySlot;
import illarion.client.world.items.ItemContainer;
import illarion.common.types.ItemCount;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Main purpose of this class is to interconnect the GUI environment and the map environment to exchange information
 * between both.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public final class InteractionManager {
    /**
     * The logger instance of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(InteractionManager.class);
    /**
     * The object that is currently dragged around.
     */
    @Nullable
    private Draggable draggedObject;

    /**
     * This value is set {@code true} in case there is currently a dragging operation in progress.
     */
    private boolean dragging;

    /**
     * This is the task that has to be executed once the dragging operation is done.
     */
    @Nullable
    private Runnable endOfDragAction;

    /**
     * The maximal amount of movable objects in the current move operation.
     */
    @Nullable
    private ItemCount amount;

    /**
     * Get the amount of items currently moved around.
     *
     * @return the amount of items moved around or {@code null} in case there is currently no dragging operation
     */
    @Nullable
    public ItemCount getMovedAmount() {
        return amount;
    }

    /**
     * Drop a object to a container slot.
     *
     * @param container the ID of the container the object is dropped in
     * @param slot      the slot inside the container the object is dropped in
     * @param count     the amount of objects to be dropped at the container
     */
    public void dropAtContainer(final int container, final int slot, @Nonnull final ItemCount count) {
        if (draggedObject == null) {
            LOGGER.warn("Dropping to container called without a active dragging operation.");
            cancelDragging();
        }

        final ItemContainer itemContainer = World.getPlayer().getContainer(container);
        if (itemContainer == null) {
            LOGGER.error("Container a item was dropped at was not found.");
            return;
        }

        try {
            final InteractiveContainerSlot targetSlot = itemContainer.getSlot(slot).getInteractive();
            draggedObject.dragTo(targetSlot, count);
        } catch (@Nonnull final IndexOutOfBoundsException ex) {
            LOGGER.error("Tried to drop a item at a container slot that does not exist.", ex);
        } finally {
            cancelDragging();
        }
    }

    /**
     * Drop a object at a slot in the inventory.
     *
     * @param slot  the inventory slot
     * @param count the amount of items to be dropped in the inventory
     */
    public void dropAtInventory(final int slot, @Nonnull final ItemCount count) {
        if (draggedObject == null) {
            LOGGER.warn("Dropping to inventory called without a active dragging operation.");
            cancelDragging();
        }

        try {
            final InteractiveInventorySlot targetSlot = World.getPlayer().getInventory().getItem(slot).getInteractive();
            if (targetSlot == null) {
                return;
            }
            draggedObject.dragTo(targetSlot, count);
        } catch (@Nonnull final IndexOutOfBoundsException ex) {
            LOGGER.error("Tried to drop a item at a inventory slot that does not exist.", ex);
        } finally {
            cancelDragging();
        }
    }

    /**
     * Drop the currently dragged object on the map.
     *
     * @param x     the x coordinate on the screen to drop the object to
     * @param y     the y coordinate on the screen to drop the object to
     * @param count the amount of objects to be dropped at the map
     */
    public void dropAtMap(final int x, final int y, @Nonnull final ItemCount count) {
        if (draggedObject == null) {
            LOGGER.warn("Dropping to map called without a active dragging operation.");
            cancelDragging();
        }

        try {
            final InteractiveMapTile targetTile = World.getMap().getInteractive().getInteractiveTileOnScreenLoc(x, y);
            if (targetTile == null) {
                return;
            }

            draggedObject.dragTo(targetTile, count);
        } finally {
            cancelDragging();
        }
    }

    /**
     * Cancel the current dragging operation.
     */
    public void cancelDragging() {
        if (endOfDragAction != null) {
            endOfDragAction.run();
            endOfDragAction = null;
        }
        draggedObject = null;
        dragging = false;
        amount = null;
    }

    /**
     * Check if there is currently a active dragging operation.
     *
     * @return {@code true} in case there is a current dragging operation
     */
    public boolean isDragging() {
        return dragging;
    }

    /**
     * Start dragging around a item from a container slot.
     *
     * @param container   the ID of the container
     * @param slot        the slot in the container
     * @param endOfDragOp the operation to be performed at the end of the dragging operation
     */
    public void notifyDraggingContainer(final int container, final int slot, @Nullable final Runnable endOfDragOp) {
        if (!dragging) {
            final ItemContainer itemContainer = World.getPlayer().getContainer(container);
            if (itemContainer == null) {
                LOGGER.error("Start dragging notification about a container that does not exist?!");
                return;
            }
            try {
                final ContainerSlot conSlot = itemContainer.getSlot(slot);
                final InteractiveContainerSlot sourceSlot = conSlot.getInteractive();

                startDragging(sourceSlot);
                endOfDragAction = endOfDragOp;
                amount = conSlot.getCount();
                return;
            } catch (@Nonnull final IndexOutOfBoundsException ex) {
                LOGGER.error("Tried to start dragging from a container slot that does not exist?!", ex);
            }
        }
        if (endOfDragOp != null) {
            endOfDragOp.run();
        }
    }

    /**
     * Start a new dragging operation.
     *
     * @param draggable the draggable that is dragged around
     */
    private void startDragging(final Draggable draggable) {
        draggedObject = draggable;
        dragging = true;
    }

    /**
     * Start dragging around a item from a inventory slot.
     *
     * @param slot        the slot in the inventory
     * @param endOfDragOp the operation to be performed at the end of the dragging operation
     */
    public void notifyDraggingInventory(final int slot, @Nullable final Runnable endOfDragOp) {
        if (!dragging) {
            try {
                final InventorySlot invSlot = World.getPlayer().getInventory().getItem(slot);
                final InteractiveInventorySlot sourceSlot = invSlot.getInteractive();

                if (sourceSlot.isValidItem()) {
                    startDragging(sourceSlot);
                    endOfDragAction = endOfDragOp;
                    amount = invSlot.getCount();
                    return;
                }
            } catch (@Nonnull final IndexOutOfBoundsException ex) {
                LOGGER.error("Tried to start dragging from a inventory slot that does not exist?!", ex);
            }
        }
        if (endOfDragOp != null) {
            endOfDragOp.run();
        }
    }

    /**
     * Start dragging around a item from the map.
     *
     * @param targetTile  the tile on the map that is dragged around
     * @param endOfDragOp the operation to be performed at the end of the dragging operation
     */
    public void notifyDraggingMap(@Nonnull final MapTile targetTile, @Nullable final Runnable endOfDragOp) {
        if (!dragging) {
            final InteractiveMapTile interactiveMapTile = targetTile.getInteractive();

            final Item draggedItem = targetTile.getTopItem();
            if (draggedItem == null) {
                LOGGER.error("Tried start dragging on a tile without a item to drag.");
            } else if (interactiveMapTile.canDrag()) {
                startDragging(interactiveMapTile);
                endOfDragAction = endOfDragOp;
                amount = draggedItem.getCount();
                return;
            } else {
                LOGGER.error("Tried start dragging on a tile without a draggable item.");
            }
        }
        if (endOfDragOp != null) {
            endOfDragOp.run();
        }
    }
}
