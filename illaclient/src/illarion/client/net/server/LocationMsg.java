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

import illarion.common.util.Location;

import illarion.graphics.common.LightTracer;

/**
 * Servermessage: Current player position (
 * {@link illarion.client.net.CommandList#MSG_LOCATION}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class LocationMsg extends AbstractReply {
    /**
     * The location of the player.
     */
    private transient Location loc;

    /**
     * Default constructor for the player location message.
     */
    public LocationMsg() {
        super(CommandList.MSG_LOCATION);
    }

    /**
     * Create a new instance of the player location message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public LocationMsg clone() {
        return new LocationMsg();
    }

    /**
     * Decode the player location data the receiver got and prepare it for the
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
     * Execute the player location message and send the decoded data to the rest
     * of the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        Game.getDisplay().setActive(false);

        // drop the whole map and expect update
        Game.getMap().clear();

        // logical location
        Game.getPlayer().setLocation(loc);
        // graphics location
        Game.getDisplay().setLocation(loc);
        LightTracer.setBaseLevel(loc.getScZ());

        // switch mini-map if required
        Game.getMap().getMinimap().setPlayerLocation(loc);

        Game.getPlayer().getCharacter().relistLight();
        Game.getPlayer().getCharacter().updateLight(loc);

        return true;
    }

    /**
     * Wait with processing this command until the game factory and the player
     * is load for sure.
     */
    @Override
    public boolean processNow() {
        return (Game.getPlayer() != null);
    }

    /**
     * Clean up all references that are not needed anymore.
     */
    @Override
    public void reset() {
        if (loc != null) {
            loc.recycle();
            loc = null;
        }
    }

    /**
     * Get the data of this player location message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("to " + loc.toString());
    }

}
