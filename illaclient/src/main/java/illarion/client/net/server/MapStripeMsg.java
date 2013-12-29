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
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.GameMap;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Servermessage: Map stripe ( {@link illarion.client.net.CommandList#MSG_MAP_STRIPE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_MAP_STRIPE)
public final class MapStripeMsg extends AbstractReply {
    /**
     * Constant if the map stripe goes from top to bottom.
     */
    private static final short DIR_DOWN = 1;

    /**
     * Constant if the map stripe goes from left to right.
     */
    private static final short DIR_RIGHT = 0;

    /**
     * Count of the tiles in this stripe.
     */
    private short count;

    /**
     * The direction of the stripe. Valid values are {@link #DIR_DOWN} and {@link #DIR_RIGHT}.
     */
    private short dir;

    /**
     * The location the stripe starts at.
     */
    private transient Location loc;

    /**
     * The list of tiles that are inside the update and all containing informations.
     */
    private final List<TileUpdate> tiles = new LinkedList<>();

    /**
     * Decode the map stripe data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        loc = decodeLocation(reader);

        final Location workLoc = new Location();
        workLoc.set(loc);

        dir = reader.readUByte();

        // create array of sufficient size
        count = reader.readUByte();

        for (int i = 0; i < count; ++i) {
            final TileUpdate workUpdate = new TileUpdate();
            workUpdate.setLocation(workLoc);
            workUpdate.decode(reader);
            if (dir == DIR_DOWN) {
                workLoc.addSC(-1, 1, 0);
            } else if (dir == DIR_RIGHT) {
                workLoc.addSC(1, 1, 0);
            }
            tiles.add(workUpdate);
        }
    }

    /**
     * Execute the map stripe message and send the decoded data to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     * @see illarion.client.net.server.AbstractReply#executeUpdate()
     */
    @Override
    public boolean executeUpdate() {
        final GameMap map = World.getMap();
        map.startTileUpdate();
        map.updateTiles(tiles);
        map.finishTileUpdate();

        World.getLights().refresh();
        return true;
    }

    /**
     * Check if that function can be update can be executed now. The map update must only be send in case the time data
     * of the server is already available. If not keep the update for later.
     *
     * @return true if the update can be done now
     */
    @Override
    public boolean processNow() {
        return true;
    }

    /**
     * Get the data of this map stripe message as string.
     *
     * @return the string that contains the values that were decoded for this message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(loc.toString() + " dir=" + dir + " tiles=" + count);
    }
}
