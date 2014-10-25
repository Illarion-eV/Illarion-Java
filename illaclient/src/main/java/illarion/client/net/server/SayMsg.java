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
 * Servermessage: Talking ({@link CommandList#MSG_SAY}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_SAY)
public final class SayMsg extends AbstractReply {
    /**
     * The location the text was spoken at.
     */
    private transient Location loc;

    /**
     * The text that was actually spoken.
     */
    private String text;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        loc = decodeLocation(reader);
        text = reader.readString();
    }

    @Override
    public void executeUpdate() {
        World.getChatHandler().handleMessage(text, loc, ChatHandler.SpeechMode.Normal);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("at " + loc + " \"" + text + '"');
    }
}
