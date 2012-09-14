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
 * Client Command: Dragging a item from one inventory slot to another ({@link CommandList#CMD_DRAG_INV_INV}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DragInvInvCmd extends AbstractDragCommand {
    /**
     * The inventory position the drag ends at.
     */
    private byte dstPos;

    /**
     * The inventory position the drag starts at.
     */
    private byte srcPos;

    /**
     * Default constructor for the dragging from inventory to inventory command.
     */
    public DragInvInvCmd() {
        super(CommandList.CMD_DRAG_INV_INV);
    }

    /**
     * Create a duplicate of this dragging from inventory to inventory command.
     *
     * @return new instance of this command
     */
    @Override
    public DragInvInvCmd clone() {
        return new DragInvInvCmd();
    }

    /**
     * Encode the data of this dragging from inventory to inventory command and
     * put the values into the buffer.
     *
     * @param writer the interface that allows writing data to the network
     *               communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(srcPos);
        writer.writeByte(dstPos);
        writer.writeUShort(getCount());
    }

    /**
     * Set the source and the destination inventory slot of this dragging event.
     * When this function is executed also the value of the counter is stored
     * automatically.
     *
     * @param dragSrcPos slot number of the inventory slot where the drag
     *                   started
     * @param dragDstPos slot number of the inventory slot where the drag ended
     */
    public void setDrag(final int dragSrcPos, final int dragDstPos) {
        srcPos = (byte) dragSrcPos;
        dstPos = (byte) dragDstPos;
    }

    /**
     * Get the data of this dragging from inventory to inventory command as
     * string.
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
        builder.append(dstPos);
        builder.append(" Counter: ");
        builder.append(getCount());
        return toString(builder.toString());
    }
}
