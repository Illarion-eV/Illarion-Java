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

import illarion.common.util.Location;

/**
 * Client Command: Looking at a tile on the map (
 * {@link illarion.client.net.CommandList#CMD_LOOKAT_TILE}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class LookatTileCmd extends AbstractCommand {
    /**
     * The position on the map we are going to look at.
     */
    private final transient Location loc;

    /**
     * Default constructor for the look at tile command.
     */
    public LookatTileCmd() {
        super(CommandList.CMD_LOOKAT_TILE);
        loc = new Location();
    }

    /**
     * Create a duplicate of this look at tile command.
     * 
     * @return new instance of this command
     */
    @Override
    public LookatTileCmd clone() {
        return new LookatTileCmd();
    }

    /**
     * Encode the data of this look at map command and put the values into the
     * buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeLocation(loc);
    }

    /**
     * Set the location we are looking at.
     * 
     * @param lookAtLoc the location we are looking at.
     */
    public void setPosition(final Location lookAtLoc) {
        loc.set(lookAtLoc);
    }

    /**
     * Get the data of this look at map command as string.
     * 
     * @return the data of this command as string
     * @see illarion.client.net.client.AbstractCommand#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("position: " + loc.toString());
    }
}
