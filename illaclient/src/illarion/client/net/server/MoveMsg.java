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

import org.apache.log4j.Logger;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.world.Char;
import illarion.client.world.Game;
import illarion.client.world.PlayerMovement;

import illarion.common.util.Location;

/**
 * Servermessage: Move of a character (
 * {@link illarion.client.net.CommandList#MSG_MOVE}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class MoveMsg extends AbstractReply {
    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(MoveMsg.class);

    /**
     * Mode information that a normal move was done.
     */
    private static final short MODE_MOVE = 0x0B;

    /**
     * Mode information that no move was done.
     */
    private static final short MODE_NO_MOVE = 0x0A;

    /**
     * Mode information that a push was done.
     */
    private static final short MODE_PUSH = 0x0C;

    /**
     * Mode information that a running move was done.
     */
    private static final short MODE_RUN = 0x0D;

    /**
     * The ID of the moving character.
     */
    private long charId;

    /**
     * The new location of the character.
     */
    private transient Location loc;

    /**
     * The moving mode of the character. Valid values are {@link #MODE_NO_MOVE},
     * {@link #MODE_MOVE}, {@link #MODE_PUSH}.
     */
    private short mode;

    /**
     * The moving speed of the character.
     */
    private short speed;

    /**
     * Default constructor for the character move message.
     */
    public MoveMsg() {
        activate(CommandList.MSG_MOVE);
    }

    /**
     * Create a new instance of the character move message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public MoveMsg clone() {
        return new MoveMsg();
    }

    /**
     * Decode the character move data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     * @see illarion.client.net.server.AbstractReply#decode(NetCommReader)
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        charId = reader.readUInt();
        loc = decodeLocation(reader);
        mode = reader.readUByte();
        speed = reader.readUByte();
    }

    /**
     * Execute the character move message and send the decoded data to the rest
     * of the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @SuppressWarnings("nls")
    @Override
    public boolean executeUpdate() {
        if ((mode != MODE_NO_MOVE) && (mode != MODE_MOVE)
            && (mode != MODE_PUSH) && (mode != MODE_RUN)) {
            LOGGER.warn("Move char message called in unknown mode " + mode);
            return true;
        }

        if (Game.getPlayer().isPlayer(charId)) {
            int moveMode = PlayerMovement.MOVE_MODE_NONE;
            if (mode == MODE_MOVE) {
                moveMode = PlayerMovement.MOVE_MODE_WALK;
            } else if (mode == MODE_PUSH) {
                moveMode = PlayerMovement.MOVE_MODE_PUSH;
            } else if (mode == MODE_RUN) {
                moveMode = PlayerMovement.MOVE_MODE_RUN;
            }
            Game.getPlayer().getMovementHandler()
                .acknowledgeMove(moveMode, loc, speed);
            return true;
        }

        // other char not on screen, just remove it.
        if (!Game.getPlayer().isOnScreen(loc, 1)) {
            Game.getPeople().removeCharacter(charId);
            return true;
        }

        final Char chara = Game.getPeople().accessCharacter(charId);
        switch (mode) {
            case MODE_NO_MOVE:
                chara.setLocation(loc);
                break;
            case MODE_MOVE:
                chara.moveTo(loc, Char.MOVE_WALK, speed);
                break;
            case MODE_RUN:
                chara.moveTo(loc, Char.MOVE_RUN, speed);
                break;
            default:
                chara.moveTo(loc, Char.MOVE_PUSH, 0);
        }

        return true;
    }

    /**
     * Clean up the objects that are not needed any longer.
     */
    @Override
    public void reset() {
        if (loc != null) {
            loc.recycle();
            loc = null;
        }
    }

    /**
     * Get the data of this character move message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ID: ");
        builder.append(charId);
        builder.append(" to: ");
        builder.append(loc.toString());
        builder.append(" mode: ");
        builder.append(mode);
        return toString(builder.toString());
    }
}
