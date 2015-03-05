/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Move of a character
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_MOVE)
public final class MoveMsg implements ServerReply {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(MoveMsg.class);

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
     * Mode information that the move request arrived at the server too early. That mode response is only valid in
     * for move commands related to the player character.
     */
    private static final int MODE_TOO_EARLY = 0x09;

    /**
     * The ID of the moving character.
     */
    @Nullable
    private CharacterId charId;

    /**
     * The new location of the character.
     */
    @Nullable
    private Location location;

    /**
     * The moving mode of the character. Valid values are {@link #MODE_NO_MOVE}, {@link #MODE_MOVE}, {@link
     * #MODE_PUSH}.
     */
    private int mode;

    /**
     * The moving duration of the character (in milliseconds)
     */
    private int duration;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        charId = new CharacterId(reader);
        location = new Location(reader);
        mode = reader.readUByte();
        duration = reader.readUShort();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((charId == null) || (location == null)) {
            throw new NotDecodedException();
        }

        switch (mode) {
            case MODE_NO_MOVE:
            case MODE_MOVE:
            case MODE_PUSH:
            case MODE_RUN:
            case MODE_TOO_EARLY:
                break;
            default:
                log.warn("Move char message called in unknown mode {}", mode);
                return ServerReplyResult.Failed;
        }

        if (World.getPlayer().isPlayer(charId)) {
            return executeForPlayer();
        }
        return executeForOther();
    }

    @Nonnull
    private ServerReplyResult executeForPlayer() {
        if (location == null) {
            throw new NotDecodedException(); // shouldn't be possible
        }

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
            case MODE_TOO_EARLY:
                World.getPlayer().getMovementHandler().executeServerRespMoveTooEarly();
                return ServerReplyResult.Success;
            default:
                moveMode = CharMovementMode.None;
        }
        World.getPlayer().getMovementHandler().executeServerRespMove(moveMode, location, duration);
        return ServerReplyResult.Success;
    }

    @Nonnull
    private ServerReplyResult executeForOther() {
        if ((charId == null) || (location == null)) {
            throw new NotDecodedException();  // shouldn't be possible
        }

        // other char not on screen, just remove it.
        if (!World.getPlayer().isOnScreen(location, 1)) {
            World.getPeople().removeCharacter(charId);
            return ServerReplyResult.Success;
        }

        Char chara = World.getPeople().accessCharacter(charId);
        switch (mode) {
            case MODE_NO_MOVE:
                chara.setLocation(location);
                break;
            case MODE_MOVE:
                chara.moveTo(location, CharMovementMode.Walk, duration);
                break;
            case MODE_RUN:
                chara.moveTo(location, CharMovementMode.Run, duration);
                break;
            case MODE_TOO_EARLY:
                log.warn("Received MODE_TOO_EARLY for a character other then the player character. That is wrong.");
                return ServerReplyResult.Failed;
            default:
                chara.moveTo(location, CharMovementMode.Push, 0);
        }
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(MoveMsg.class, charId, location, "Duration: " + duration +
                "ms");
    }
}
