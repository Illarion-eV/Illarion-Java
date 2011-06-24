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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;
import illarion.client.world.Game;

import illarion.common.util.Location;

/**
 * Client Command: Dragging a item from a map position to a inventory slot (
 * {@link illarion.client.net.CommandList#CMD_DRAG_INV_SC}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class DragMapInvCmd extends AbstractCommand {
    /**
     * The amount of items that shall be moved at once.
     */
    private byte count;

    /**
     * The direction the source location is at, relative to the character.
     */
    private byte dir;

    /**
     * The inventory slot that is the target of this drag operation.
     */
    private byte dstPos;

    /**
     * Default constructor for the dragging from map to inventory command.
     */
    public DragMapInvCmd() {
        super(CommandList.CMD_DRAG_MAP_INV);
    }

    /**
     * Create a duplicate of this dragging from map to inventory command.
     * 
     * @return new instance of this command
     */
    @Override
    public DragMapInvCmd clone() {
        return new DragMapInvCmd();
    }

    /**
     * Encode the data of this dragging from map to inventory command and put
     * the values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(dir);
        writer.writeByte(dstPos);
        writer.writeByte(count);
    }

    /**
     * Set the source location on the map for this dragging command.
     * 
     * @param loc the location on the map the drag starts at
     */
    public void setDragFrom(final Location loc) {
        dir = (byte) Game.getPlayer().getLocation().getDirection(loc);
    }

    /**
     * Set the destination of the dragging event.
     * 
     * @param dragDstPos the inventory slot that is the destination of the drag
     *            event
     */
    public void setDragTo(final int dragDstPos) {
        dstPos = (byte) dragDstPos;
        count = 1;
    }

    /**
     * Get the data of this dragging from map to inventory command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Source Dir: ");
        builder.append(dir);
        builder.append(" Destination: ");
        builder.append(dstPos);
        builder.append(" Counter: ");
        builder.append(count);
        return toString(builder.toString());
    }
}
