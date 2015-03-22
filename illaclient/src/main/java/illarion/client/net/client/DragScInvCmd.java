/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;
import illarion.common.types.ItemCount;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Dragging an item from a container to the inventory ({@link CommandList#CMD_DRAG_SC_INV}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class DragScInvCmd extends AbstractDragCommand {
    /**
     * The source container of the dragging event.
     */
    private final short sourceContainer;

    /**
     * The source container item of the dragging event.
     */
    private final short sourceContainerItem;

    /**
     * The target inventory slot of the dragging event.
     */
    private final short targetSlot;

    /**
     * The default constructor of this DragScInvCmd.
     *
     * @param sourceContainer the container that is the source
     * @param sourceSlot the slot in the container that is the source
     * @param destination the inventory slot that is the destination of the drag
     * @param count the amount of items to move
     */
    public DragScInvCmd(int sourceContainer, int sourceSlot, int destination, @Nonnull ItemCount count) {
        super(CommandList.CMD_DRAG_SC_INV, count);

        this.sourceContainer = (short) sourceContainer;
        sourceContainerItem = (short) sourceSlot;
        targetSlot = (short) destination;
    }

    @Override
    public void encode(@Nonnull NetCommWriter writer) {
        writer.writeUByte(sourceContainer);
        writer.writeUByte(sourceContainerItem);
        writer.writeUByte(targetSlot);
        getCount().encode(writer);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString("Source: " + sourceContainer + '/' + sourceContainerItem + " Destination: " + targetSlot +
                                ' ' + getCount());
    }
}
