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
 * Client Command: Dragging an item from the inventory to a container ({@link CommandList#CMD_DRAG_INV_SC}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class DragInvScCmd extends AbstractDragCommand {
    /**
     * The source inventory slot of the dragging event.
     */
    private final short sourceSlot;

    /**
     * The target container of the dragging event.
     */
    private final short targetContainer;

    /**
     * The target slot of the container.
     */
    private final short targetContainerSlot;

    /**
     * The default constructor of this DragInvScCmd.
     */
    public DragInvScCmd(final int source, final int targetContainer, final int targetSlot,
                        @Nonnull final ItemCount count) {
        super(CommandList.CMD_DRAG_INV_SC, count);

        sourceSlot = (short) source;
        this.targetContainer = (short) targetContainer;
        targetContainerSlot = (short) targetSlot;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeUByte(sourceSlot);
        writer.writeUByte(targetContainer);
        writer.writeUByte(targetContainerSlot);
        getCount().encode(writer);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString("Source: " + sourceSlot + " Destination: " + targetContainer + '/' + targetContainerSlot +
                ' ' + getCount());
    }
}
