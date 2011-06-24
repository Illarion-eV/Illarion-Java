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

/**
 * Client Command: Dragging an item from a container to the inventory (
 * {@link illarion.client.net.CommandList#CMD_DRAG_SC_INV}).
 * 
 * @author Blay09
 * @since 1.22
 */
public final class DragScInvCmd extends AbstractCommand {

    /**
     * The source container of the dragging event.
     */
    private byte sourceContainer;

    /**
     * The source container item of the dragging event.
     */
    private byte sourceContainerItem;

    /**
     * The target inventory slot of the dragging event.
     */
    private byte targetSlot;

    /**
     * The default constructor of this DragScInvCmd.
     */
    public DragScInvCmd() {
        super(CommandList.CMD_DRAG_SC_INV);
    }

    /**
     * Create a duplicate of this dragging from container to inventory command.
     * 
     * @return new instance of this command
     */
    @Override
    public DragScInvCmd clone() {
        return new DragScInvCmd();
    }

    /**
     * Encode the data of this dragging from container to inventory command and
     * put the values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(sourceContainer);
        writer.writeByte(sourceContainerItem);
        writer.writeByte(targetSlot);
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
     * @param Slot the inventory slot to which the item was dragged.
     */
    public void setTarget(final byte Slot) {
        targetSlot = Slot;
    }

    /**
     * Get the data of this dragging from container to inventory command as
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
        sb.append(" TargetSlot: ");
        sb.append(targetSlot);
        return sb.toString();
    }

}
