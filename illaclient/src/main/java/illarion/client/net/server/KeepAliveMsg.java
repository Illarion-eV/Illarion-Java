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
import illarion.client.util.ConnectionPerformanceClock;
import illarion.common.net.NetCommReader;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * This is the command received in response to a keep alive command.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_KEEP_ALIVE)
public class KeepAliveMsg extends AbstractReply {
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        ConnectionPerformanceClock.notifyNetCommDecode();
    }

    @Override
    public boolean executeUpdate() {
        ConnectionPerformanceClock.notifyPublishToClient();
        return true;
    }

    @Nonnull
    @Override
    public String toString() {
        return toString("");
    }
}
