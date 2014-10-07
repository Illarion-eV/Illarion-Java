/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Request the size of the transferred map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @see CommandList#CMD_MAPDIMENSION
 */
@Immutable
public final class MapDimensionCmd extends AbstractCommand {
    /**
     * The map height that is requested from the server.
     */
    private final short mapHeight;

    /**
     * The map width that is requested from the server.
     */
    private final short mapWidth;

    /**
     * Default constructor for the map dimension command.
     *
     * @param width the half of the needed width in stripes - 1
     * @param height the half of the needed height in stripes - 1
     */
    public MapDimensionCmd(int width, int height) {
        super(CommandList.CMD_MAPDIMENSION);
        mapWidth = (short) width;
        mapHeight = (short) height;
    }

    @Override
    public void encode(@Nonnull NetCommWriter writer) {
        writer.writeUByte(mapWidth);
        writer.writeUByte(mapHeight);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Width: " + mapWidth + " Height: " + mapHeight);
    }
}
