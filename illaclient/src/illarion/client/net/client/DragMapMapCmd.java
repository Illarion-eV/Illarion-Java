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

import javolution.text.TextBuilder;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;

import illarion.common.util.Location;

/**
 * Client Command: Dragging a item from the game map to the game map (
 * {@link illarion.client.net.CommandList#CMD_DRAG_MAP_MAP_N},
 * {@link illarion.client.net.CommandList#CMD_DRAG_MAP_MAP_NE},
 * {@link illarion.client.net.CommandList#CMD_DRAG_MAP_MAP_E},
 * {@link illarion.client.net.CommandList#CMD_DRAG_MAP_MAP_SE},
 * {@link illarion.client.net.CommandList#CMD_DRAG_MAP_MAP_S},
 * {@link illarion.client.net.CommandList#CMD_DRAG_MAP_MAP_SW},
 * {@link illarion.client.net.CommandList#CMD_DRAG_MAP_MAP_W},
 * {@link illarion.client.net.CommandList#CMD_DRAG_MAP_MAP_NW}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class DragMapMapCmd extends AbstractCommand {
    /**
     * Count of Items that shall be moved.
     */
    private byte count;

    /**
     * The location on the map that is the target of the move operation.
     */
    private final transient Location dstLoc;

    /**
     * Default constructor for the dragging from map to map command.
     */
    public DragMapMapCmd() {
        super(CommandList.CMD_DRAG_MAP_MAP_N);
        dstLoc = new Location();
    }

    /**
     * Create a duplicate of this dragging from map to map command.
     * 
     * @return new instance of this command
     */
    @Override
    public DragMapMapCmd clone() {
        return new DragMapMapCmd();
    }

    /**
     * Encode the data of this dragging from map to map command and put the
     * values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeLocation(dstLoc);
        writer.writeByte(count);
    }

    /**
     * Read the current counter settings and store those.
     */
    public void setCounter() {
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
     * Get the data of this dragging from map to map command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final int currID = getId();
        final TextBuilder builder = TextBuilder.newInstance();
        try {
            builder.append("Source: ");
            switch (currID) {
                case CommandList.CMD_DRAG_MAP_MAP_N:
                    builder.append("North");
                    break;
                case CommandList.CMD_DRAG_MAP_MAP_NE:
                    builder.append("Northeast");
                    break;
                case CommandList.CMD_DRAG_MAP_MAP_E:
                    builder.append("East");
                    break;
                case CommandList.CMD_DRAG_MAP_MAP_SE:
                    builder.append("Southeast");
                    break;
                case CommandList.CMD_DRAG_MAP_MAP_S:
                    builder.append("South");
                    break;
                case CommandList.CMD_DRAG_MAP_MAP_SW:
                    builder.append("Southwest");
                    break;
                case CommandList.CMD_DRAG_MAP_MAP_W:
                    builder.append("West");
                    break;
                case CommandList.CMD_DRAG_MAP_MAP_NW:
                    builder.append("Northwest");
                    break;
                default:
                    builder.append("unknown");
            }
            builder.append(" Destination: ");
            builder.append(dstLoc.toString());
            builder.append(" Counter: ");
            builder.append(count);
            return toString(builder.toString());
        } finally {
            TextBuilder.recycle(builder);
        }
    }
}
