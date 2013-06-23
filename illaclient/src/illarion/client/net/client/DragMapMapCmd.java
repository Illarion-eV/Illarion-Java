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
 * Client Command: Dragging a item from the game map to the game map ({@link CommandList#CMD_DRAG_MAP_MAP_N},
 * {@link CommandList#CMD_DRAG_MAP_MAP_NE}, {@link CommandList#CMD_DRAG_MAP_MAP_E},
 * {@link CommandList#CMD_DRAG_MAP_MAP_SE}, {@link CommandList#CMD_DRAG_MAP_MAP_S},
 * {@link CommandList#CMD_DRAG_MAP_MAP_SW}, {@link CommandList#CMD_DRAG_MAP_MAP_W},
 * {@link CommandList#CMD_DRAG_MAP_MAP_NW}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class DragMapMapCmd extends AbstractDragCommand {
    /**
     * The source location of the move operation.
     */
    @Nonnull
    private final Location srcLoc;

    /**
     * The location on the map that is the target of the move operation.
     */
    @Nonnull
    private final Location dstLoc;

    /**
     * Default constructor for the dragging from map to map command.
     *
     * @param source      the location from where the item is taken
     * @param destination the destination location on the map
     * @param count       the amount of items to move
     */
    public DragMapMapCmd(@Nonnull final Location source, @Nonnull final Location destination,
                         @Nonnull final ItemCount count) {
        super(CommandList.CMD_DRAG_MAP_MAP, count);
        srcLoc = new Location(source);
        dstLoc = new Location(destination);
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeLocation(srcLoc);
        writer.writeLocation(dstLoc);
        getCount().encode(writer);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Source: ");
        builder.append(srcLoc.toString());
        builder.append(" Destination: ");
        builder.append(dstLoc.toString());
        builder.append(' ');
        builder.append(getCount());
        return toString(builder.toString());
    }
}
