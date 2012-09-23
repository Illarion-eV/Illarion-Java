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
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;

import java.io.IOException;

/**
 * Servermessage: ID of the player character (
 * {@link illarion.client.net.CommandList#MSG_PLAYER_ID}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class PlayerIdMsg extends AbstractReply {
    /**
     * The ID if the character, played with this client.
     */
    private CharacterId playerId;

    /**
     * Default constructor for the player id message.
     */
    public PlayerIdMsg() {
        super(CommandList.MSG_PLAYER_ID);
    }

    /**
     * Create a new instance of the player id message as recycle object.
     *
     * @return a new instance of this message object
     */
    @Override
    public PlayerIdMsg clone() {
        return new PlayerIdMsg();
    }

    /**
     * Decode the player id data the receiver got and prepare it for the
     * execution.
     *
     * @param reader the receiver that got the data from the server that needs
     *               to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *                     decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        playerId = new CharacterId(reader);
    }

    /**
     * Execute the player id message and send the decoded data to the rest of
     * the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        World.getPlayer().setPlayerID(playerId);
        return true;
    }

    /**
     * Get the data of this player id message as string.
     *
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(playerId.toString());
    }
}
