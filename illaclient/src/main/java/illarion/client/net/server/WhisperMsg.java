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
import illarion.client.util.ChatHandler;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Whispering ({@link illarion.client.net.CommandList#MSG_WHISPER}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_WHISPER)
public final class WhisperMsg extends AbstractReply {

    /**
     * The location the text was spoken at.
     */
    private transient Location loc;

    /**
     * The text that was actually spoken.
     */
    private String text;

    /**
     * Decode the talking data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws java.io.IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        loc = decodeLocation(reader);
        text = reader.readString();
    }

    /**
     * Execute the talking message and send the decoded data to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        World.getChatHandler().handleMessage(text, loc, ChatHandler.SpeechMode.Whisper);
        return true;
    }

    /**
     * Get the data of this talking message as string.
     *
     * @return the string that contains the values that were decoded for this message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("at " + loc.toString() + " \"" + text + '"');
    }
}
