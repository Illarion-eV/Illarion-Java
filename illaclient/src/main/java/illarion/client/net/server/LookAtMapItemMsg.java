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

import illarion.client.gui.Tooltip;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Look at description of a map item
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_LOOKAT_MAPITEM)
public final class LookAtMapItemMsg implements ServerReply {
    /**
     * The location of the tile on the server map.
     */
    @Nullable
    private Location location;

    /**
     * The position of the referenced item.
     */
    private int stackPosition;

    /**
     * The tooltip that is displayed for the item on the specified location.
     */
    @Nullable
    private Tooltip tooltip;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        location = new Location(reader);
        stackPosition = reader.readUByte();
        tooltip = new Tooltip(reader);
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((location == null) || (tooltip == null)) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        World.getGameGui().getGameMapGui().showItemTooltip(location, stackPosition, tooltip);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(LookAtMapItemMsg.class, location, tooltip);
    }
}
