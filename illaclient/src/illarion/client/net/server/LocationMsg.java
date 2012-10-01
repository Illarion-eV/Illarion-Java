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
import illarion.client.world.World;
import illarion.common.graphics.LightTracer;
import illarion.common.net.NetCommReader;
import illarion.common.util.Location;

import java.io.IOException;

/**
 * Servermessage: Current player position ( {@link illarion.client.net.CommandList#MSG_LOCATION}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_LOCATION)
public final class LocationMsg
        extends AbstractReply {
    /**
     * The location of the player.
     */
    private transient Location loc;

    /**
     * Decode the player location data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(final NetCommReader reader)
            throws IOException {
        loc = decodeLocation(reader);
    }

    /**
     * Execute the player location message and send the decoded data to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        World.getMapDisplay().setActive(false);

        // drop the whole map and expect update
        World.getMap().clear();

        // logical location
        World.getPlayer().setLocation(loc);
        // graphics location
        World.getMapDisplay().setLocation(loc);
        LightTracer.setBaseLevel(loc.getScZ());

        // switch mini-map if required
        World.getMap().getMinimap().setPlayerLocation(loc);

        World.getPlayer().getCharacter().relistLight();
        World.getPlayer().getCharacter().updateLight(loc);

        return true;
    }

    /**
     * Wait with processing this command until the game factory and the player is load for sure.
     */
    @Override
    public boolean processNow() {
        return World.getPlayer() != null;
    }

    /**
     * Get the data of this player location message as string.
     *
     * @return the string that contains the values that were decoded for this message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("to " + loc.toString());
    }

}
