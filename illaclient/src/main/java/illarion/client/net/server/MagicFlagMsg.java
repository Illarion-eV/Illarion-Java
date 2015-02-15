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
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Server message: Magic flags of the player character
 * <p />
 * <b>With magic not properly implemented, this command does currently nothing.</b>
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_MAGIC_FLAG)
public final class MagicFlagMsg implements ServerReply {
    /**
     * Flags of the magic that are available. So the runes a character is allowed to use.
     */
    private long flags;

    /**
     * Type of magic that is used. Such a magician, druid, bard, priest.
     */
    private short type;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        type = reader.readUByte();
        flags = reader.readUInt();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(MagicFlagMsg.class, "Type: " + type, "Flags: " + flags);
    }
}
