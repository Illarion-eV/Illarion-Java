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
import illarion.client.world.Game;

import illarion.common.util.Location;

/**
 * Client Command: Open a container on the map (
 * {@link illarion.client.net.CommandList#CMD_OPEN_MAP}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class OpenMapCmd extends AbstractCommand {
    /**
     * The direction relative to the player character the bag is located at.
     */
    private byte dir;

    /**
     * The showcase that shall show the the container that is going to be opend.
     */
    private byte sc;

    /**
     * Default constructor for the open container on the map command.
     */
    public OpenMapCmd() {
        super(CommandList.CMD_OPEN_MAP);
    }

    /**
     * Create a duplicate of this open container on map command.
     * 
     * @return new instance of this command
     */
    @Override
    public OpenMapCmd clone() {
        return new OpenMapCmd();
    }

    /**
     * Encode the data of this open container on map command and put the values
     * into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(dir);
        writer.writeByte(sc);
    }

    /**
     * Set the location of the where the bag is located on the map.
     * 
     * @param dstLoc the location the map is located at
     */
    public void setPosition(final Location dstLoc) {
        dir = (byte) Game.getPlayer().getLocation().getDirection(dstLoc);
    }

    /**
     * Set the showcase that shall show the container once its opend.
     * 
     * @param targetSc the showcase that shall show the container contents.
     */
    public void setShowcase(final int targetSc) {
        sc = (byte) targetSc;
    }

    /**
     * Get the data of this open container on map command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("from direction:" + dir + " in Showcase: " + sc);
    }
}
