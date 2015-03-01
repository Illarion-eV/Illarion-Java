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
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Current date and time ({@link CommandList#MSG_DATETIME}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_DATETIME)
public final class DateTimeMsg implements ServerReply {
    /**
     * Day of the current IG time.
     */
    private short day;

    /**
     * Hour of the current IG time.
     */
    private short hour;

    /**
     * Minute of the current IG time.
     */
    private short minute;

    /**
     * Month of the current IG time.
     */
    private short month;

    /**
     * Year of the current IG time.
     */
    private int year;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        hour = reader.readUByte();
        minute = reader.readUByte();
        day = reader.readUByte();
        month = reader.readUByte();
        year = reader.readUShort();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        World.getClock().setDateTime(year, month, day, hour, minute);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(DateTimeMsg.class,
                String.format("Date: %1$d/%2$d/%3$d - Time: %4$d:%5$d", month, day, year, hour, minute));
    }
}
