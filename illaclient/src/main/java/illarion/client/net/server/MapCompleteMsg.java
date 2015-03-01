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
 * Server message: This message is generated once the map is fully transferred.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_MAP_COMPLETE)
public final class MapCompleteMsg implements ServerReply {
    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        // nothing to decode
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        World.getMapDisplay().setActive(true);
        World.getLights().refresh();
        World.getMap().checkInside();
        World.getMap().getMiniMap().performFullUpdate();

        if (World.getGameGui().isReady()) {
            World.getGameGui().getQuestGui().updateAllQuests();
        }

        World.getNet().setLoginDone(true);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(MapCompleteMsg.class);
    }
}
