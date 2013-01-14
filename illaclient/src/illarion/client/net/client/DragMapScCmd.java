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
import illarion.common.net.NetCommWriter;
import illarion.common.types.ItemCount;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Dragging an item from the map to a container ({@link CommandList#CMD_DRAG_MAP_SC}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class DragMapScCmd extends AbstractDragCommand {
    /**
     * The source location of the dragging event.
     */
    private final short direction;

    /**
     * The target container of the dragging event.
     */
    private final short targetContainer;

    /**
     * The target slot of the container.
     */
    private final short targetContainerSlot;

    /**
     * The default constructor of this DragMapScCmd.
     *
     * @param source               the location from where the item is taken
     * @param destinationContainer the container that is the destination
     * @param destinationSlot      the slot in the container that is the destination
     * @param count                the amount of items to move
     */
    public DragMapScCmd(final Location source, final int destinationContainer, final int destinationSlot,
                        @Nonnull final ItemCount count) {
        super(CommandList.CMD_DRAG_MAP_SC, count);

        direction = (short) World.getPlayer().getLocation().getDirection(source);
        targetContainer = (short) destinationContainer;
        targetContainerSlot = (short) destinationSlot;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeUByte(direction);
        writer.writeUByte(targetContainer);
        writer.writeUByte(targetContainerSlot);
        getCount().encode(writer);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString("SourceDirection: " + direction + " Destination: " + targetContainer + '/' +
                targetContainerSlot + ' ' + getCount());
    }
}
