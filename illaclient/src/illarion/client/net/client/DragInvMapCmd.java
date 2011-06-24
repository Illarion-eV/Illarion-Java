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

import illarion.common.util.Location;

/**
 * Client Command: Dragging a item from a inventory slot to the game map (
 * {@link illarion.client.net.CommandList#CMD_DRAG_INV_MAP}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class DragInvMapCmd extends AbstractCommand {
    /**
     * Count of Items that shall be moved.
     */
    private byte count;

    /**
     * The location on the map that is the target of the move operation.
     */
    private final transient Location dstLoc;

    /**
     * Inventory position the drag starts at.
     */
    private byte srcPos;

    /**
     * Default constructor for the dragging from inventory to map command.
     */
    public DragInvMapCmd() {
        super(CommandList.CMD_DRAG_INV_MAP);
        dstLoc = new Location();
    }

    /**
     * Create a duplicate of this dragging from inventory to map command.
     * 
     * @return new instance of this command
     */
    @Override
    public DragInvMapCmd clone() {
        return new DragInvMapCmd();
    }

    /**
     * Encode the data of this dragging from inventory to map command and put
     * the values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(srcPos);
        writer.writeLocation(dstLoc);
        writer.writeByte(count);
    }

    /**
     * Set the source inventory slot of this dragging event. When this function
     * is executed also the value of the counter is stored automatically.
     * 
     * @param dragSrcPos slot number of the inventory slot where the drag
     *            started
     */
    public void setDragFrom(final int dragSrcPos) {
        srcPos = (byte) dragSrcPos;
        count = 1;
    }

    /**
     * The the location on the map that is the target of the dragging operation.
     * 
     * @param newLoc the location the object is dragged to
     */
    public void setDragTo(final Location newLoc) {
        dstLoc.set(newLoc);
    }

    /**
     * Get the data of this dragging from inventory to map command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Source: ");
        builder.append(srcPos);
        builder.append(" Destination: ");
        builder.append(dstLoc.toString());
        builder.append(" Counter: ");
        builder.append(count);
        return toString(builder.toString());
    }
}
