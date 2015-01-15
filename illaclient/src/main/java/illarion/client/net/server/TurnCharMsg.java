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
import illarion.common.types.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nullable
    private Direction dir;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        dir = Direction.decode(reader);
        charId = new CharacterId(reader);
    }
    @Override
    public void executeUpdate() {
        if (dir == null) {
            return;
        }

        if (World.getPlayer().isPlayer(charId)) { // turn player
            World.getPlayer().getMovementHandler().executeServerRespTurn(dir);
        } else { // turn char
            Char chara = World.getPeople().getCharacter(charId);
            if (chara != null) {
                chara.setDirection(dir);
            }
        }
    }

    @Override
    public boolean processNow() {
        // no turning while the player is still moving
        return !World.getPlayer().isPlayer(charId) || !World.getPlayer().getMovementHandler().isMoving();
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(charId + " to " + dir);
    }
}
