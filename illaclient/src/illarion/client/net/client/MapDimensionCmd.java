/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;

/**
 * Client Command: Request the size of the transfered map.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 * @see illarion.client.net.CommandList#CMD_MAPDIMENSION
 */
public class MapDimensionCmd extends AbstractCommand {
    /**
     * The map height that is requested from the server.
     */
    private short mapHeight;

    /**
     * The map width that is requested from the server.
     */
    private short mapWidth;

    /**
     * Default constructor for the map dimension command.
     */
    public MapDimensionCmd() {
        super(CommandList.CMD_MAPDIMENSION);
    }

    /**
     * Create a duplicate of this Map Dimension command.
     * 
     * @return new instance of this command
     */
    @Override
    public MapDimensionCmd clone() {
        return new MapDimensionCmd();
    }

    /**
     * Encode the data of this login command and put the values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeUByte(mapWidth);
        writer.writeUByte(mapHeight);
    }

    /**
     * Set the dimension of the map that is requested from the server.
     * 
     * @param width the half of the needed width in stripes - 1
     * @param height the half of the needed height in stripes - 1
     */
    public void setMapDimensions(final int width, final int height) {
        mapWidth = (short) width;
        mapHeight = (short) height;
    }

    /**
     * Get the data of this map dimension command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Width: " + mapWidth + " Height: " + mapHeight);
    }

}
