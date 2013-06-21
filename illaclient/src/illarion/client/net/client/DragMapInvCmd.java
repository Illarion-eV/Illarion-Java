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
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Dragging a item from a map position to a inventory slot ({@link CommandList#CMD_DRAG_MAP_INV}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class DragMapInvCmd extends AbstractDragCommand {
    /**
     * The location the item that is moved by this command is located at.
     */
    @Nonnull
    private final Location srcLoc;

    /**
     * The inventory slot that is the target of this drag operation.
     */
    private final short dstPos;

    /**
     * Default constructor for the dragging from map to inventory command.
     *
     * @param source      the location from where the item is taken
     * @param destination the destination slot in the inventory
     * @param count       the amount of items to move
     */
    public DragMapInvCmd(@Nonnull final Location source, final int destination, @Nonnull final ItemCount count) {
        super(CommandList.CMD_DRAG_MAP_INV, count);

        srcLoc = new Location(source);
        dstPos = (short) destination;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeLocation(srcLoc);
        writer.writeUByte(dstPos);
        getCount().encode(writer);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Source: " + srcLoc + " Destination: " + dstPos + ' ' + getCount());
    }
}
