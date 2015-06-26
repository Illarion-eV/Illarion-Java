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
import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Dragging a item from the game map to the game map ({@link CommandList#CMD_DRAG_MAP_MAP}.
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
    private final ServerCoordinate srcLoc;

    /**
     * The location on the map that is the target of the move operation.
     */
    @Nonnull
    private final ServerCoordinate dstLoc;

    /**
     * Default constructor for the dragging from map to map command.
     *
     * @param source the location from where the item is taken
     * @param destination the destination location on the map
     * @param count the amount of items to move
     */
    public DragMapMapCmd(
            @Nonnull ServerCoordinate source, @Nonnull ServerCoordinate destination, @Nonnull ItemCount count) {
        super(CommandList.CMD_DRAG_MAP_MAP, count);
        srcLoc = source;
        dstLoc = destination;
    }

    @Override
    public void encode(@Nonnull NetCommWriter writer) {
        srcLoc.encode(writer);
        dstLoc.encode(writer);
        getCount().encode(writer);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString("Source: " + srcLoc + " Destination: " + dstLoc + ' ' + getCount());
    }
}
