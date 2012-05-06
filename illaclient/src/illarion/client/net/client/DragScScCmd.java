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
import illarion.client.net.NetCommWriter;

/**
 * Client Command: Dragging an item from one container to another (
 * {@link illarion.client.net.CommandList#CMD_DRAG_SC_SC}).
 *
 * @author Blay09
 */
public final class DragScScCmd extends AbstractCommand {

    /**
     * The source container of the dragging event.
     */
    private byte sourceContainer;

    /**
     * The source container item of the dragging event.
     */
    private byte sourceContainerItem;

    /**
     * The target container of the dragging event.
     */
    private byte targetContainer;

    /**
     * The target container item of the dragging event.
     */
    private byte targetContainerItem;

    /**
     * The default constructor of this DragScScCmd.
     */
    public DragScScCmd() {
        super(CommandList.CMD_DRAG_SC_SC);
    }

    /**
     * Create a duplicate of this dragging from container to container command.
     *
     * @return new instance of this command
     */
    @Override
    public DragScScCmd clone() {
        return new DragScScCmd();
    }

    /**
     * Encode the data of this dragging from container to container command and
     * put the values into the buffer.
     *
     * @param writer the interface that allows writing data to the network
     *               communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(sourceContainer);
        writer.writeByte(sourceContainerItem);
        writer.writeByte(targetContainer);
        writer.writeByte(targetContainerItem);
        writer.writeByte((byte) 1); // Counter
    }

    /**
     * Sets the dragging source.
     *
     * @param Container     the container from which the item was dragged
     * @param ContainerItem the container item id which was dragged
     */
    public void setSource(final int Container, final int ContainerItem) {
        sourceContainer = (byte) Container;
        sourceContainerItem = (byte) ContainerItem;
    }

    /**
     * Sets the dragging target.
     *
     * @param Container     the container to which the item was dragged
     * @param ContainerItem the slot that is the target of the container
     */
    public void setTarget(final int Container, final int ContainerItem) {
        targetContainer = (byte) Container;
        targetContainerItem = (byte) ContainerItem;
    }

    /**
     * Get the data of this dragging from container to container command as
     * string.
     *
     * @return the data of this command as string
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SourceContainer: ");
        sb.append(sourceContainer);
        sb.append(" SourceConItemID: ");
        sb.append(sourceContainerItem);
        sb.append(" TargetContainer: ");
        sb.append(targetContainer);
        sb.append(" SourceConItemID: ");
        sb.append(targetContainerItem);
        return sb.toString();
    }

}
