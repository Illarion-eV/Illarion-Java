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
package illarion.client.net.server;

import java.io.IOException;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.world.Game;
import illarion.client.world.MapTile;

import illarion.common.util.Location;

/**
 * Servermessage: Remove the top item on a tile (
 * {@link illarion.client.net.CommandList#MSG_REMOVE_ITEM}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class RemoveItemMsg extends AbstractReply {
    /**
     * The location the top item shall be removed from.
     */
    private transient Location loc;

    /**
     * Default constructor for the remove top item message.
     */
    public RemoveItemMsg() {
        super(CommandList.MSG_REMOVE_ITEM);
    }

    /**
     * Create a new instance of the remove top item message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public RemoveItemMsg clone() {
        return new RemoveItemMsg();
    }

    /**
     * Decode the remove top item data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        loc = decodeLocation(reader);
    }

    /**
     * Execute the remove top item message and send the decoded data to the rest
     * of the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        final MapTile tile = Game.getMap().getMapAt(loc);
        if (tile != null) {
            tile.removeTopItem();
        }

        return true;
    }

    /**
     * Cleanup the references in this object that are not needed any longer.
     */
    @Override
    public void reset() {
        if (loc != null) {
            loc.recycle();
            loc = null;
        }
    }

    /**
     * Get the data of this remove top item message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("location: " + loc.toString());
    }
}
