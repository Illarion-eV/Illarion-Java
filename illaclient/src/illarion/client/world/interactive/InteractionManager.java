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

import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.client.world.items.ContainerSlot;
import illarion.client.world.items.InventorySlot;
import illarion.client.world.items.ItemContainer;
import illarion.common.annotation.NonNull;
import illarion.common.annotation.Nullable;
import illarion.common.types.ItemCount;
import net.jcip.annotations.NotThreadSafe;
import org.apache.log4j.Logger;

/**
 * Main purpose of this class is to interconnect the GUI environment and the map environment to exchange information
 * between both.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public final class InteractionManager {
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
     * The logger instance of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(InteractionManager.class);

    /**
     * Drop the currently dragged object on the map.
     *
     * @param x     the x coordinate on the screen to drop the object to
     * @param y     the y coordinate on the screen to drop the object to
     * @param count the amount of objects to be dropped at the map
     */
    public void dropAtMap(final int x, final int y, @NonNull final ItemCount count) {
        if (draggedObject == null) {
            LOGGER.warn("Dropping to map called without a active dragging operation.");
            return;
        }
        final InteractiveMapTile targetTile = World.getMap().getInteractive().getInteractiveTileOnScreenLoc(x, y);

        if (targetTile == null) {
            return;
        }

        draggedObject.dragTo(targetTile, count);
        cancelDragging();
    }

    /**
     * Drop a object to a inventory slot.
     *
     * @param container the ID of the container the object is dropped in
     * @param slot      the slot inside the container the object is dropped in
     * @param count     the amount of objects to be dropped at the container
     */
    public void dropAtContainer(final int container, final int slot, final ItemCount count) {
        if (draggedObject == null) {
            return;
        }

        final ItemContainer itemContainer = World.getPlayer().getContainer(container);
        if (itemContainer == null) {
            LOGGER.error("Container a item was dropped at was not found.");
            return;
        }

        final InteractiveContainerSlot targetSlot = itemContainer.getSlot(slot).getInteractive();

        draggedObject.dragTo(targetSlot, count);
        cancelDragging();
    }

    public void dropAtInventory(final int slot, final ItemCount count) {
        if (draggedObject == null) {
            return;
        }

        final InteractiveInventorySlot targetSlot = World.getPlayer().getInventory().getItem(slot).getInteractive();

        if (targetSlot == null) {
            return;
        }

        draggedObject.dragTo(targetSlot, count);
        cancelDragging();
    }

    public void cancelDragging() {
        draggedObject = null;
        dragging = false;
        if (endOfDragAction != null) {
            endOfDragAction.run();
            endOfDragAction = null;
        }
    }

    public void startDragging(final Draggable draggable) {
        draggedObject = draggable;
        dragging = true;
    }

    public void notifyDraggingContainer(final int container, final int slot, final Runnable endOfDragOp) {
        if (!dragging) {
            final ItemContainer itemContainer = World.getPlayer().getContainer(container);
            if (itemContainer == null) {
                LOGGER.error("Start dragging notification about a container that does not exist?!");
                return;
            }
            final ContainerSlot conSlot = itemContainer.getSlot(slot);
            final InteractiveContainerSlot sourceSlot = conSlot.getInteractive();

            startDragging(sourceSlot);
            endOfDragAction = endOfDragOp;
            amount = conSlot.getCount();
        }
    }

    public void notifyDraggingInventory(final int slot, final Runnable endOfDragOp) {
        if (!dragging) {
            final InventorySlot invSlot = World.getPlayer().getInventory().getItem(slot);
            final InteractiveInventorySlot sourceSlot = invSlot.getInteractive();

            if (sourceSlot == null) {
                return;
            }

            if (!sourceSlot.isValidItem()) {
                return;
            }

            startDragging(sourceSlot);
            endOfDragAction = endOfDragOp;
            amount = invSlot.getCount();
        }
    }

    public void notifyDraggingMap(final MapTile targetTile, final Runnable endOfDragOp) {
        if (!dragging) {
            if (targetTile == null) {
                return;
            }

            final InteractiveMapTile interactiveMapTile = targetTile.getInteractive();

            if (!interactiveMapTile.canDrag()) {
                return;
            }

            startDragging(interactiveMapTile);
            endOfDragAction = endOfDragOp;
            amount = targetTile.getTopItem().getCount();
        }
    }

    public ItemCount getMovedAmount() {
        return amount;
    }

    public boolean isDragging() {
        return dragging;
    }
}
