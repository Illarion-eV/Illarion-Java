/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.Char;
import illarion.client.world.CharMovementMode;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import illarion.common.types.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Move of a character ( {@link illarion.client.net.CommandList#MSG_MOVE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_MOVE)
public final class MoveMsg extends AbstractReply {
    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveMsg.class);

    /**
     * Mode information that a normal move was done.
     */
    private static final int MODE_MOVE = 0x0B;

    /**
     * Mode information that no move was done.
     */
    private static final int MODE_NO_MOVE = 0x0A;

    /**
     * Mode information that a push was done.
     */
    private static final int MODE_PUSH = 0x0C;

    /**
     * Mode information that a running move was done.
     */
    private static final int MODE_RUN = 0x0D;

    /**
     * The ID of the moving character.
     */
    private CharacterId charId;

    /**
     * The new location of the character.
     */
    private Location loc;

    /**
     * The moving mode of the character. Valid values are {@link #MODE_NO_MOVE}, {@link #MODE_MOVE}, {@link
     * #MODE_PUSH}.
     */
    private int mode;

    /**
     * The moving speed of the character.
     */
    private int speed;

    /**
     * Decode the character move data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        charId = new CharacterId(reader);
        loc = decodeLocation(reader);
        mode = reader.readUByte();
        speed = reader.readUByte();
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
            LOGGER.warn("Move char message called in unknown mode {}", mode);
            return true;
        }

        if (World.getPlayer().isPlayer(charId)) {
            CharMovementMode moveMode;
            switch (mode) {
                case MODE_MOVE:
                    moveMode = CharMovementMode.Walk;
                    break;
                case MODE_PUSH:
                    moveMode = CharMovementMode.Push;
                    break;
                case MODE_RUN:
                    moveMode = CharMovementMode.Run;
                    break;
                default:
                    moveMode = CharMovementMode.None;
            }
            World.getPlayer().getMovementHandler().executeServerRespMove(moveMode, loc, speed);
            return true;
        }

        // other char not on screen, just remove it.
        if (!World.getPlayer().isOnScreen(loc, 1)) {
            World.getPeople().removeCharacter(charId);
            return true;
        }

        Char chara = World.getPeople().accessCharacter(charId);
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
        return toString("ID: " + charId + " to: " + loc + " mode: " + mode);
    }
}
