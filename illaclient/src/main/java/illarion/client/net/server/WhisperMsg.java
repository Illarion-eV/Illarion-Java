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
import illarion.client.util.ChatHandler.SpeechMode;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Whispering
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_WHISPER)
public final class WhisperMsg implements ServerReply {
    /**
     * The location the text was spoken at.
     */
    @Nullable
    private Location location;

    /**
     * The text that was actually spoken.
     */
    @Nullable
    private String text;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        location = new Location(reader);
        text = reader.readString();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((location == null) || (text == null)) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        World.getChatHandler().handleMessage(text, location, SpeechMode.Whisper);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(WhisperMsg.class, location, text);
    }
}
