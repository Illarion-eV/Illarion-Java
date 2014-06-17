/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import illarion.client.graphics.Item;
import illarion.client.graphics.Tile;
import illarion.client.net.client.*;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.client.world.items.ContainerSlot;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the interactive representation of a tile on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Vilarion &lt;vilarion@illarion.org&gt;
 */
public class InteractiveMapTile implements Draggable, DropTarget, Usable {
    /**
     * The ID that is needed to tell the server that the operations refer to a
     * tile on the map.
     */
    private static final byte REFERENCE_ID = 1;

    /**
     * The tile this interactive tile refers to.
     */
    private final MapTile parentTile;

    /**
     * Copy constructor.
     *
     * @param tile the instance that shall be copied
     */
    public InteractiveMapTile(@Nonnull final InteractiveMapTile tile) {
        parentTile = tile.parentTile;
    }

    /**
     * Constructor that allows setting the tile to be used.
     *
     * @param tile the tile this interactive class refers to
     */
    public InteractiveMapTile(final MapTile tile) {
        parentTile = tile;
    }

    /**
     * Check if it is possible to drag this tile to another location. This
     * implies that there is something on this tile that can be dragged.
     *
     * @return <code>true</code> in case a dragging operation is valid for this
     * tile
     */
    public boolean canDrag() {
        return isInUseRange() && parentTile.canMoveItem();
    }

    /**
     * The logging instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractiveMapTile.class);

    /**
     * Drag something from a map tile to
     */
    @Override
    public void dragTo(@Nonnull final InteractiveChar targetChar, @Nonnull final ItemCount count) {
        if (!canDrag()) {
            LOGGER.error("Finished dragging of tile that can't be dragged.");
            return;
        }

        final MapTile tile = World.getMap().getMapAt(targetChar.getLocation());
        if (tile == null) {
            LOGGER.error("Dragged to a tile that does not exist.");
            return;
        }
        dragTo(tile.getInteractive(), count);
    }

    @Override
    public void dragTo(@Nonnull final InteractiveInventorySlot targetSlot, @Nonnull final ItemCount count) {
        if (!canDrag()) {
            LOGGER.error("Finished dragging of tile that can't be dragged.");
            return;
        }

        final ItemId topItemId = getTopItemId();
        assert topItemId != null;

        if (!targetSlot.isAcceptingItem(topItemId)) {
            return;
        }

        World.getNet().sendCommand(new DragMapInvCmd(getLocation(), targetSlot.getSlotId(), count));
    }

    /**
     * Drag this tile to another tile.
     *
     * @param targetTile the tile to drag this tile to
     */
    @Override
    public void dragTo(@Nonnull final InteractiveMapTile targetTile, @Nonnull final ItemCount count) {
        if (!canDrag()) {
            return;
        }

        if (targetTile.getLocation().equals(World.getPlayer().getLocation())) {
            World.getNet().sendCommand(new PickUpItemCmd(getLocation()));
        } else {
            World.getNet().sendCommand(new DragMapMapCmd(getLocation(), targetTile.getLocation(), count));
        }
    }

    @Override
    public void dragTo(@Nonnull final InteractiveContainerSlot targetSlot, @Nonnull final ItemCount count) {
        if (!canDrag()) {
            return;
        }

        final ItemId topItemId = getTopItemId();
        assert topItemId != null;

        if (!targetSlot.acceptItem(topItemId)) {
            return;
        }

        final ContainerSlot slot = targetSlot.getSlot();
        World.getNet().sendCommand(new DragMapScCmd(getLocation(), slot.getContainerId(), slot.getLocation(), count));
    }

    /**
     * Perform a use operation on this tile.
     */
    @Override
    public void use() {
        if (!isInUseRange()) {
            return;
        }

        final Item topItem = getTopItem();
        if ((topItem != null) && topItem.getTemplate().getItemInfo().isContainer()) {
            World.getNet().sendCommand(new OpenOnMapCmd(getLocation()));
            return;
        }

        World.getNet().sendCommand(new UseMapCmd(getLocation()));
    }

    /**
     * Request a look at on this tile.
     */
    public void lookAt() {
        World.getNet().sendCommand(new LookatTileCmd(getLocation()));
    }

    /**
     * Get the location of the tile this interactive tile refers to
     *
     * @return the location of this tile
     */
    @Nonnull
    public Location getLocation() {
        return parentTile.getLocation();
    }

    /**
     * Check if the tile is inside the valid using range of the player character.
     *
     * @return {@code true} in case the character is allowed to use anything on this tile or the tile itself
     */
    @Override
    public boolean isInUseRange() {
        @Nonnull Location playerLocation = World.getPlayer().getMovementHandler().getServerLocation();
        if (playerLocation.getScZ() == getLocation().getScZ()) {
            return playerLocation.getDistance(getLocation()) <= getUseRange();
        }
        return false;
    }

    @Override
    public int getUseRange() {
        return 1;
    }

    /**
     * Get the item that is located on top of the tile.
     *
     * @return the item on top of the tile
     */
    @Nullable
    public Item getTopItem() {
        return parentTile.getTopItem();
    }

    /**
     * Get the ID of the first item on this tile.
     *
     * @return the item ID
     */
    @Nullable
    public ItemId getTopItemId() {
        final Item topItem = getTopItem();
        if (topItem == null) {
            return null;
        }
        return topItem.getItemId();
    }

    /**
     * Get the display level. This can be used to determine the order of multiple objects in the render list. In this
     * case this function is quite a hack so use it with care. It does only use the order of the tile in case there
     * are no items on it. In case there are any items that have a elevation, the function returns the display level
     * of the elevating object.
     *
     * @return the display level
     */
    public int getElevationDisplayLevel() {
        @Nullable final Tile tile = parentTile.getTile();
        if (tile == null) {
            return Integer.MAX_VALUE;
        }
        @Nullable final Item elevatingItem = parentTile.getElevatingItem();
        if (elevatingItem == null) {
            return tile.getOrder();
        }
        return elevatingItem.getOrder();
    }
}
