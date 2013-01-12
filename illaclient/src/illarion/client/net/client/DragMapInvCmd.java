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
import illarion.client.world.World;
import illarion.common.annotation.NonNull;
import illarion.common.net.NetCommWriter;
import illarion.common.types.ItemCount;
import illarion.common.types.Location;
import net.jcip.annotations.Immutable;

/**
 * Client Command: Dragging a item from a map position to a inventory slot ({@link CommandList#CMD_DRAG_MAP_INV}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class DragMapInvCmd extends AbstractDragCommand {
    /**
     * The direction the source location is at, relative to the character.
     */
    private final short direction;

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
    public DragMapInvCmd(@NonNull final Location source, final int destination, @NonNull final ItemCount count) {
        super(CommandList.CMD_DRAG_MAP_INV, count);

        direction = (short) World.getPlayer().getLocation().getDirection(source);
        dstPos = (short) destination;
    }

    @Override
    public void encode(@NonNull final NetCommWriter writer) {
        writer.writeUByte(direction);
        writer.writeUByte(dstPos);
        getCount().encode(writer);
    }

    @NonNull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Source Dir: " + direction + " Destination: " + dstPos + ' ' + getCount());
    }
}
