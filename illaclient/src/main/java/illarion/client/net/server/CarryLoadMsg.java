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
 * Server message: Report the current and the maximal carry load of the character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_CARRY_LOAD)
public final class CarryLoadMsg implements ServerReply {
    /**
     * The load the character currently carries.
     */
    private int currentLoad;

    /**
     * The load the character is maximal able to carry.
     */
    private int maximumLoad;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        currentLoad = reader.readUShort();
        maximumLoad = reader.readUShort();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        World.getPlayer().getCarryLoad().updateLoad(currentLoad, maximumLoad);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(CarryLoadMsg.class, "Current: " + currentLoad, "Max:" + maximumLoad);
    }
}
