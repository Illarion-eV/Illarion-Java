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
import illarion.client.util.UpdateTask;
import illarion.client.world.Char;
import illarion.client.world.CharMovementMode;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import illarion.common.types.Location;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Move of a character ( {@link illarion.client.net.CommandList#MSG_MOVE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_MOVE)
public final class MoveMsg extends AbstractReply implements UpdateTask {
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
    private CharacterId charId;

    /**
     * The new location of the character.
     */
    private transient Location loc;

    /**
     * The moving mode of the character. Valid values are {@link #MODE_NO_MOVE}, {@link #MODE_MOVE}, {@link
     * #MODE_PUSH}.
     */
    private short mode;

    /**
     * The moving speed of the character.
     */
    private short speed;

    /**
     * Decode the character move data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws IOException thrown in case there was not enough data received to decode the full message
     * @see illarion.client.net.server.AbstractReply#decode(NetCommReader)
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader)
            throws IOException {
        charId = new CharacterId(reader);
        loc = decodeLocation(reader);
        mode = reader.readUByte();
        speed = reader.readUByte();
    }

    @Override
    public void onUpdateGame(@Nonnull final GameContainer container, final StateBasedGame game, final int delta) {
        if (World.getPlayer().isPlayer(charId)) {
            final CharMovementMode moveMode;
            if (mode == MODE_MOVE) {
                moveMode = CharMovementMode.Walk;
            } else if (mode == MODE_PUSH) {
                moveMode = CharMovementMode.Push;
            } else if (mode == MODE_RUN) {
                moveMode = CharMovementMode.Run;
            } else {
                moveMode = CharMovementMode.None;
            }
            World.getPlayer().getMovementHandler().acknowledgeMove(moveMode, loc, speed);
            return;
        }

        // other char not on screen, just remove it.
        if (!World.getPlayer().isOnScreen(loc, 1)) {
            World.getPeople().removeCharacter(charId);
            return;
        }

        final Char chara = World.getPeople().accessCharacter(charId);
        switch (mode) {
            case MODE_NO_MOVE:
                chara.setLocation(loc);
                break;
            case MODE_MOVE:
                chara.moveTo(loc, CharMovementMode.Walk, speed);
                break;
            case MODE_RUN:
                chara.moveTo(loc, CharMovementMode.Run, speed);
                break;
            default:
                chara.moveTo(loc, CharMovementMode.Push, 0);
        }
    }

    /**
     * Execute the character move message and send the decoded data to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @SuppressWarnings("nls")
    @Override
    public boolean executeUpdate() {
        if ((mode != MODE_NO_MOVE) && (mode != MODE_MOVE) && (mode != MODE_PUSH) && (mode != MODE_RUN)) {
            LOGGER.warn("Move char message called in unknown mode " + mode);
            return true;
        }

        World.getUpdateTaskManager().addTask(this);
        return true;
    }

    /**
     * Get the data of this character move message as string.
     *
     * @return the string that contains the values that were decoded for this message
     */
    @Nonnull
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
