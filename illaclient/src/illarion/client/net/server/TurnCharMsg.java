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
import illarion.client.world.Char;
import illarion.client.world.Game;

import illarion.common.util.Location;

/**
 * Servermessage: Turn a character (
 * {@link illarion.client.net.CommandList#MSG_TURN_CHAR}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class TurnCharMsg extends AbstractReply {
    /**
     * The ID of the character that is turned.
     */
    private long charId;

    /**
     * The new direction of the character.
     */
    private short dir;

    /**
     * Default constructor for the character turn message.
     */
    public TurnCharMsg() {
        super(CommandList.MSG_TURN_CHAR);
    }

    /**
     * Create a new instance of the character turn message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public TurnCharMsg clone() {
        return new TurnCharMsg();
    }

    /**
     * Decode the character turn data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        dir = reader.readUByte();
        charId = reader.readUInt();
    }

    /**
     * Execute the character turn message and send the decoded data to the rest
     * of the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        if (dir == Location.DIR_ZERO) {
            return true;
        }

        if (Game.getPlayer().isPlayer(charId)) { // turn player
            Game.getPlayer().getMovementHandler().acknowledgeTurn(dir);
        } else { // turn char
            final Char chara = Game.getPeople().getCharacter(charId);
            if (chara != null) {
                chara.setDirection(dir);
            }
        }

        return true;
    }

    /**
     * Check if the character can be executed now, or if its needed to wait and
     * keep this data to have it executed later.
     * 
     * @return true to execute the update now, false to execute it later
     */
    @Override
    public boolean processNow() {
        // no turning while the player is still moving
        return !Game.getPlayer().isPlayer(charId)
            || !Game.getPlayer().getMovementHandler().isMoving();
    }

    /**
     * Get the data of this character turn message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("ID: " + charId + " to " + dir);
    }
}
