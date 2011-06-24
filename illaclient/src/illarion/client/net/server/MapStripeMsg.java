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

import javolution.util.FastList;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.world.Game;
import illarion.client.world.GameMap;

import illarion.common.util.Location;

/**
 * Servermessage: Map stripe (
 * {@link illarion.client.net.CommandList#MSG_MAP_STRIPE}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
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
     * The direction of the stripe. Valid values are {@link #DIR_DOWN} and
     * {@link #DIR_RIGHT}.
     */
    private short dir;

    /**
     * The location the stripe starts at.
     */
    private transient Location loc;

    /**
     * The list of tiles that are inside the update and all containing
     * informations.
     */
    private final FastList<TileUpdate> tiles = new FastList<TileUpdate>();

    /**
     * Default constructor for the map stripe message.
     */
    public MapStripeMsg() {
        super(CommandList.MSG_MAP_STRIPE);
    }

    /**
     * Create a new instance of the map stripe message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public MapStripeMsg clone() {
        return new MapStripeMsg();
    }

    /**
     * Decode the map stripe data the receiver got and prepare it for the
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

        final Location workLoc = Location.getInstance();
        workLoc.set(loc);

        dir = reader.readUByte();

        // create array of sufficient size
        count = reader.readUByte();

        for (int i = 0; i < count; ++i) {
            final TileUpdate workUpdate = TileUpdate.getInstance();
            workUpdate.setLocation(workLoc);
            workUpdate.decode(reader);
            if (dir == DIR_DOWN) {
                workLoc.addSC(-1, 1, 0);
            } else if (dir == DIR_RIGHT) {
                workLoc.addSC(1, 1, 0);
            }
            tiles.addLast(workUpdate);
        }
        workLoc.recycle();
    }

    /**
     * Execute the map stripe message and send the decoded data to the rest of
     * the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     * @see illarion.client.net.server.AbstractReply#executeUpdate()
     */
    @Override
    public boolean executeUpdate() {
        final GameMap map = Game.getMap();
        map.startTileUpdate();
        while (!tiles.isEmpty()) {
            final TileUpdate currUpdate = tiles.removeFirst();
            map.updateTile(currUpdate);
            map.getMinimap().update(currUpdate);
            currUpdate.reset();
            currUpdate.recycle();
        }
        map.finishTileUpdate();

        Game.getLights().refresh();

        if (Game.getDisplay().isActive()) {
            map.getMinimap().finishUpdate();
        }

        return true;
    }

    /**
     * Check if that function can be update can be executed now. The map update
     * must only be send in case the time data of the server is already
     * available. If not keep the update for later.
     * 
     * @return true if the update can be done now
     */
    @Override
    public boolean processNow() {
        return true;// Gui.getInstance().getClock().isSet();
    }

    /**
     * Cleanup the references that are not needed anymore.
     */
    @Override
    public void reset() {
        if (loc != null) {
            loc.recycle();
            loc = null;
        }
    }

    /**
     * Get the data of this map stripe message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(loc.toString() + " dir=" + dir + " tiles=" + count);
    }
}
