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
 * Servermessage: Introduce character (
 * {@link illarion.client.net.CommandList#MSG_INTRODUCE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_INTRODUCE)
public final class IntroduceMsg extends AbstractReply {
    /**
     * The ID of the character who is introduced.
     */
    private CharacterId charId;

    /**
     * The name of the character.
     */
    private String text;

    /**
     * Decode the introduce data the receiver got and prepare it for the
     * execution.
     *
     * @param reader the receiver that got the data from the server that needs
     * to be decoded
     * @throws IOException thrown in case there was not enough data received to
     * decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        charId = new CharacterId(reader);
        text = reader.readString();
    }

    /**
     * Execute the introduce message and send the decoded data to the rest of
     * the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        final Char chara = World.getPeople().getCharacter(charId);
        if (chara != null) {
            chara.setName(text);
        }
        return true;
    }

    /**
     * Get the data of this introduce message as string.
     *
     * @return the string that contains the values that were decoded for this
     * message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Chat(" + charId + ") is named \"" + text + '"');
    }
}
