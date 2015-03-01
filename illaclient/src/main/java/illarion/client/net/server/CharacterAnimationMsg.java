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
import illarion.client.util.UpdateTask;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import org.illarion.engine.GameContainer;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Execute a character animation
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_CHARACTER_ANIMATION)
public final class CharacterAnimationMsg implements ServerReply, UpdateTask {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(CharacterAnimationMsg.class);

    /**
     * The ID of the animation that is shown.
     */
    private short animationId;

    /**
     * The ID of the character that is animated.
     */
    @Nullable
    private CharacterId charId;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        charId = new CharacterId(reader);
        animationId = reader.readUByte();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if (charId == null) {
            throw new NotDecodedException();
        }
        World.getUpdateTaskManager().addTask(this);
        return ServerReplyResult.Success;
    }

    @Override
    public void onUpdateGame(@Nonnull GameContainer container, int delta) {
        Char character = World.getPeople().getCharacter(charId);
        if (character == null) {
            log.error("Received animation request for character {}, but the character is nowhere to be found", charId);
            return;
        }
        character.startAnimation(animationId, Char.DEFAULT_ANIMATION_SPEED);
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(CharacterAnimationMsg.class, charId, "Animation ID: " + animationId);
    }
}
