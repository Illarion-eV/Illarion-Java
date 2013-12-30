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
import illarion.common.types.ItemCount;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Dragging a item from one inventory slot to another ({@link CommandList#CMD_DRAG_INV_INV}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class DragInvInvCmd extends AbstractDragCommand {
    /**
     * The inventory position the drag ends at.
     */
    private final short dstPos;

    /**
     * The inventory position the drag starts at.
     */
    private final short srcPos;

    /**
     * Default constructor for the dragging from inventory to inventory command.
     *
     * @param source      the inventory position where the drag starts
     * @param destination the inventory position where the drag ends
     * @param count       the amount of items to drag
     */
    public DragInvInvCmd(final int source, final int destination, @Nonnull final ItemCount count) {
        super(CommandList.CMD_DRAG_INV_INV, count);

        srcPos = (short) source;
        dstPos = (short) destination;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeUByte(srcPos);
        writer.writeUByte(dstPos);
        getCount().encode(writer);
    }


    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Source: " + srcPos + " Destination: " + dstPos + ' ' + getCount());
    }
}
