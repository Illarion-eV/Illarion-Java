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
import illarion.client.world.MusicBox;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Server message: Play background music
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_MUSIC)
public final class MusicMsg implements ServerReply {
    /**
     * The ID of the song that shall be played.
     */
    private int song;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        song = reader.readUShort();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if (song == MusicBox.NO_TRACK) {
            World.getMusicBox().playDefaultMusic();
        } else {
            World.getMusicBox().playMusicTrack(song);
        }
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(MusicMsg.class, "Song: " + song);
    }
}
