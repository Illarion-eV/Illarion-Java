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
 * Client Command: Dragging an item from a container to the map (
 * {@link illarion.client.net.CommandList#CMD_DRAG_SC_MAP}).
 * 
 * @author Blay09
 * @since 1.22
 */
public final class DragScMapCmd extends AbstractCommand {

    /**
     * The source container of the dragging event.
     */
    private byte sourceContainer;

    /**
     * The source container item of the dragging event.
     */
    private byte sourceContainerItem;

    /**
     * The target location of the dragging event.
     */
    private final Location targetLocation;

    /**
     * The default constructor of this DragScMapCmd.
     */
    public DragScMapCmd() {
        super(CommandList.CMD_DRAG_SC_MAP);
        targetLocation = new Location();
    }

    /**
     * Create a duplicate of this dragging from container to map command.
     * 
     * @return new instance of this command
     */
    @Override
    public DragScMapCmd clone() {
        return new DragScMapCmd();
    }

    /**
     * Encode the data of this dragging from container to map command and put
     * the values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(sourceContainer);
        writer.writeByte(sourceContainerItem);
        writer.writeLocation(targetLocation);
        writer.writeByte((byte) 0); // Counter
    }

    /**
     * Sets the dragging source.
     * 
     * @param Container the container from which the item was dragged
     * @param ContainerItem the container item id which was dragged
     */
    public void setSource(final byte Container, final byte ContainerItem) {
        sourceContainer = Container;
        sourceContainerItem = ContainerItem;
    }

    /**
     * Sets the dragging target.
     * 
     * @param location the location on the map where the item was dragged
     */
    public void setTarget(final Location location) {
        targetLocation.set(location);
    }

    /**
     * Get the data of this dragging from container to map command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final TextBuilder builder = TextBuilder.newInstance();
        try {
            builder.append("SourceContainer: ");
            builder.append(sourceContainer);
            builder.append(" SourceConItemID: ");
            builder.append(sourceContainerItem);
            builder.append(" TargetLocation: ");
            builder.append(targetLocation.toString());
            return builder.toString();
        } finally {
            TextBuilder.recycle(builder);
        }
    }

}
