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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;
import illarion.common.util.Location;

/**
 * Client Command: Dragging an item from the map to a container (
 * {@link illarion.client.net.CommandList#CMD_DRAG_MAP_SC}).
 *
 * @author Blay09
 */
public final class DragMapScCmd extends AbstractDragCommand {

    /**
     * The source location of the dragging event.
     */
    private Location sourceLocation;

    /**
     * The target container of the dragging event.
     */
    private byte targetContainer;

    /**
     * The target slot of the container.
     */
    private byte targetContainerSlot;

    /**
     * The default constructor of this DragMapScCmd.
     */
    public DragMapScCmd() {
        super(CommandList.CMD_DRAG_MAP_SC);
    }

    /**
     * Create a duplicate of this dragging from map to container command.
     *
     * @return new instance of this command
     */
    @Override
    public DragMapScCmd clone() {
        return new DragMapScCmd();
    }

    /**
     * Encode the data of this dragging from map to container command and put
     * the values into the buffer.
     *
     * @param writer the interface that allows writing data to the network
     *               communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeLocation(sourceLocation);
        writer.writeByte(targetContainer);
        writer.writeByte(targetContainerSlot);
        getCount().encode(writer);
    }

    /**
     * Sets the dragging source.
     *
     * @param location the location on the map from which the item was dragged
     */
    public void setSource(final Location location) {
        sourceLocation = location;
    }

    /**
     * Sets the dragging target.
     *
     * @param container the container to which the item was dragged
     * @param slot      the slot of the target container
     */
    public void setTarget(final int container, final int slot) {
        targetContainer = (byte) container;
        targetContainerSlot = (byte) slot;
    }

    /**
     * Get the data of this dragging from map to container command as string.
     *
     * @return the data of this command as string
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SourceLocation: ");
        sb.append(sourceLocation.toString());
        sb.append(" TargetContainer: ");
        sb.append(targetContainer);
        sb.append(" TargetPosition: ");
        sb.append(targetContainerSlot);
        return sb.toString();
    }
}
