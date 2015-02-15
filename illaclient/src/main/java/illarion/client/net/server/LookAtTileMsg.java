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
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Server message: Look at description of a tile
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_LOOKAT_TILE)
public final class LookAtTileMsg implements ServerReply {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(LookAtTileMsg.class);

    /**
     * The location of the tile on the server map.
     */
    private transient Location loc;

    /**
     * The look at text for the tile.
     */
    private String text;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        loc = new Location(reader);
        text = reader.readString();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        log.warn("Received look at for a tile. That shouldn't happen! Received \"{}\" for {}", text, loc);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(LookAtTileMsg.class, loc, text);
    }
}
