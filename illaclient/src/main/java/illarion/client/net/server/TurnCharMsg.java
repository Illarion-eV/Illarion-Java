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
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Turn a character (
 * {@link illarion.client.net.CommandList#MSG_TURN_CHAR}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_TURN_CHAR)
public final class TurnCharMsg extends AbstractReply {
    /**
     * The ID of the character that is turned.
     */
    private CharacterId charId;

    /**
     * The new direction of the character.
     */
    private short dir;

    /**
     * Decode the character turn data the receiver got and prepare it for the
     * execution.
     *
     * @param reader the receiver that got the data from the server that needs
     * to be decoded
     * @throws IOException thrown in case there was not enough data received to
     * decode the full message
     */
    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        dir = reader.readUByte();
        charId = new CharacterId(reader);
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

        if (World.getPlayer().isPlayer(charId)) { // turn player
            World.getPlayer().getMovementHandler().getExecutor().handleTurnServerResponse(dir);
        } else { // turn char
            Char chara = World.getPeople().getCharacter(charId);
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
        return !World.getPlayer().isPlayer(charId) || !World.getPlayer().getMovementHandler().isMoving();
    }

    /**
     * Get the data of this character turn message as string.
     *
     * @return the string that contains the values that were decoded for this
     * message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(charId + " to " + dir);
    }
}
