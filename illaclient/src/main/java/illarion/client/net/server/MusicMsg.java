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
import illarion.client.world.MusicBox;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Play background music (
 * {@link illarion.client.net.CommandList#MSG_MUSIC}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_MUSIC)
public final class MusicMsg extends AbstractReply {
    /**
     * The ID of the song that shall be played.
     */
    private int song;

    /**
     * Decode the play music data the receiver got and prepare it for the
     * execution.
     *
     * @param reader the receiver that got the data from the server that needs
     * to be decoded
     * @throws IOException thrown in case there was not enough data received to
     * decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        song = reader.readUShort();
    }

    /**
     * Execute the play music message and send the decoded data to the rest of
     * the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        if (song == MusicBox.NO_TRACK) {
            World.getMusicBox().playDefaultMusic();
        } else {
            World.getMusicBox().playMusicTrack(song);
        }
        return true;
    }

    /**
     * Get the data of this play music message as string.
     *
     * @return the string that contains the values that were decoded for this
     * message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("ID: " + song);
    }
}
