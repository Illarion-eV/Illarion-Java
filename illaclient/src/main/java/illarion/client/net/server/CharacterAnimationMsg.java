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

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Character animation (
 * {@link illarion.client.net.CommandList#MSG_CHARACTER_ANIMATION}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_CHARACTER_ANIMATION)
public final class CharacterAnimationMsg extends AbstractReply {
    /**
     * The ID of the animation that is shown.
     */
    private short animationId;

    /**
     * The ID of the character that is animated.
     */
    private CharacterId charId;

    /**
     * Decode the character animation data the receiver got and prepare it for
     * the execution.
     *
     * @param reader the receiver that got the data from the server that needs
     * to be decoded
     * @throws IOException thrown in case there was not enough data received to
     * decode the full message
     */
    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        charId = new CharacterId(reader);
        animationId = reader.readUByte();
    }

    /**
     * Execute the message and send the decoded appearance data to the rest of
     * the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        Char ch = World.getPeople().getCharacter(charId);
        if (ch == null) {
            // Update for illegal character
            return true;
        }
        ch.startAnimation(animationId, Char.DEFAULT_ANIMATION_SPEED);
        return true;
    }

    /**
     * Get the data of this character animation message as string.
     *
     * @return the string that contains the values that were decoded for this
     * message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(charId + " - Animation ID: " + Integer.toString(animationId));
    }
}
