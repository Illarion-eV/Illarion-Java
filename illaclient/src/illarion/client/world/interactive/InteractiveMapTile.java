/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world.interactive;

import illarion.client.graphics.Item;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;
import illarion.client.net.client.DragMapInvCmd;
import illarion.client.net.client.DragMapMapCmd;
import illarion.client.net.client.UseCmd;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.util.Location;
import illarion.common.util.Reusable;
import javolution.context.ObjectFactory;

/**
 * This is the interactive representation of a tile on the map.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class InteractiveMapTile extends AbstractDraggable implements DropTarget, UseTarget, Reusable {
    /**
     * This class defines the factory used to generate new instances of the
     * interactive tiles.
     * 
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class InteractiveTileFactory extends
        ObjectFactory<InteractiveMapTile> {
        /**
         * Public constructor so the parent class is able to create a instance.
         */
        public InteractiveTileFactory() {
            super();
        }

        /**
         * Create a new instance.
         */
        @Override
        protected InteractiveMapTile create() {
            return new InteractiveMapTile();
        }
    }

    /**
     * The factory used to create new instances of this class and recycle unused
     * ones.
     */
    private static final InteractiveTileFactory FACTORY =
        new InteractiveTileFactory();

    /**
     * The ID that is needed to tell the server that the operations refer to a
     * tile on the map.
     */
    private static final byte REFERENCE_ID = 1;

    /**
     * Create a duplicate of a existing interactive reference to a tile.
     * 
     * @param tile the original interactive tile
     * @return the new interactive tile that refers to the same interactive tile
     *         as the original one
     */
    public static InteractiveMapTile getInteractiveTile(
        final InteractiveMapTile tile) {
        final InteractiveMapTile newTile = FACTORY.object();
        newTile.parentTile = tile.parentTile;
        return newTile;
    }

    /**
     * Create a new instance of the interactive tile that refers to a map tile.
     * 
     * @param tile the tile its supposed to refer to
     * @return the new interactive tile that refers to the selected map tile
     */
    public static InteractiveMapTile getInteractiveTile(final MapTile tile) {
        final InteractiveMapTile newTile = FACTORY.object();
        newTile.parentTile = tile;
        return newTile;
    }

    /**
     * The tile this interactive tile refers to.
     */
    private MapTile parentTile;

    /**
     * Private constructor ensuring that only the factory created instances.
     */
    private InteractiveMapTile() {
        // nothing
    }

    /**
     * Check if it is possible to drag this tile to another location. This
     * implies that there is something on this tile that can be dragged.
     * 
     * @return <code>true</code> in case a dragging operation is valid for this
     *         tile
     */
    public boolean canDrag() {
        return (isInUseRange() && parentTile.canMoveItem());
    }

    /**
     * Drag something from a map tile to
     */
    @Override
    public void dragTo(final InteractiveChar targetChar) {
        if (!canDrag()) {
            return;
        }

        final InteractiveMapTile tile =
            World.getMap().getInteractive()
                .getInteractiveTileOnMapLoc(targetChar.getLocation());
        dragTo(tile);
        tile.recycle();
    }

    @Override
    public void dragTo(final InteractiveInventorySlot targetSlot) {
        if (!canDrag()) {
            return;
        }
        
        if (!targetSlot.acceptItem(getTopItemId())) {
            return;
        }

        final DragMapInvCmd cmd =
            CommandFactory.getInstance().getCommand(
                CommandList.CMD_DRAG_MAP_INV, DragMapInvCmd.class);
        cmd.setDragFrom(getLocation());
        cmd.setDragTo(targetSlot.getSlotId());
        cmd.send();
    }

    /**
     * Drag this tile to another tile.
     * 
     * @param targetTile the tile to drag this tile to
     */
    @Override
    public void dragTo(final InteractiveMapTile targetTile) {
        if (!canDrag()) {
            return;
        }

        final DragMapMapCmd cmd =
            CommandFactory.getInstance().getCommand(
                CommandList.CMD_DRAG_MAP_MAP_N + getDirection(),
                DragMapMapCmd.class);
        cmd.setDragTo(targetTile.getLocation());
        cmd.setCounter();
        cmd.send();
    }

    public void use() {
        if (!isInUseRange()) {
            return;
        }

        final UseCmd cmd =
                CommandFactory.getInstance().getCommand(
                        CommandList.CMD_USE,
                        UseCmd.class);
        cmd.addUse(this);
        cmd.send();
    }

    /**
     * Encode a use operation to this tile.
     * 
     * @param writer the use operation to this tile
     */
    @Override
    public void encodeUse(final NetCommWriter writer) {
        writer.writeByte(REFERENCE_ID);
        writer.writeLocation(getLocation());
    }

    /**
     * Get the direction constant for the relative direction from the player
     * location to the target tile.
     * 
     * @return the direction constant
     */
    public int getDirection() {
        return World.getPlayer().getLocation().getDirection(getLocation());
    }

    /**
     * Get the location of the tile this interactive tile refers to
     * 
     * @return the location of this tile
     */
    public Location getLocation() {
        return parentTile.getLocation();
    }

    /**
     * Check if the tile is inside the valid using range of the player
     * character.
     * 
     * @return <code>true</code> in case the character is allowed to use
     *         anything on this tile or the tile itself
     */
    public boolean isInUseRange() {
        return (World.getPlayer().getLocation().getDistance(getLocation()) < 2);
    }

    /**
     * Recycle this tile. This should be called once this instance is not needed
     * anymore.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset this instance. Shouldn't be called as it renders this instance
     * unusable.
     */
    @Override
    public void reset() {
        parentTile = null;
    }

    /**
     * Get the item that is located on top of the tile.
     * 
     * @return the item on top of the tile
     */
    public Item getTopImage() {
        return parentTile.getTopItem();
    }
    
    /**
     * Get the ID of the first item on this tile.
     * 
     * @return the item ID
     */
    public int getTopItemId() {
        return getTopImage().getId();
    }
}
