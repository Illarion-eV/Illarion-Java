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
import illarion.common.net.NetCommReader;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Look at description of a character (
 * {@link illarion.client.net.CommandList#MSG_LOOKAT_CHAR}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_LOOKAT_CHAR)
public final class LookAtCharMsg extends AbstractReply {
    /**
     * The ID of the character the look at text is related to.
     */
    private long charId;

    /**
     * The text that is the look at result.
     */
    private String text;

    /**
     * Decode the character look at text data the receiver got and prepare it
     * for the execution.
     *
     * @param reader the receiver that got the data from the server that needs
     * to be decoded
     * @throws IOException thrown in case there was not enough data received to
     * decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        charId = reader.readUInt();
        text = reader.readString();
    }

    /**
     * Execute the character look at text message and send the decoded data to
     * the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        // final Char ch = Game.getPeople().getCharacter(charId);
        // if (ch != null) {
        // ch.showText(text, SpeechMode.normal);
        // }
        return true;
    }

    /**
     * Get the data of this character look at text message as string.
     *
     * @return the string that contains the values that were decoded for this
     * message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Char: " + charId + " Message: " + text);
    }
}
