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
import illarion.common.annotation.NonNull;
import illarion.common.net.NetCommWriter;
import net.jcip.annotations.Immutable;

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
     * @param width  the half of the needed width in stripes - 1
     * @param height the half of the needed height in stripes - 1
     */
    public MapDimensionCmd(final int width, final int height) {
        super(CommandList.CMD_MAPDIMENSION);
        mapWidth = (short) width;
        mapHeight = (short) height;
    }

    @Override
    public void encode(@NonNull final NetCommWriter writer) {
        writer.writeUByte(mapWidth);
        writer.writeUByte(mapHeight);
    }

    @NonNull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Width: " + mapWidth + " Height: " + mapHeight);
    }

}
