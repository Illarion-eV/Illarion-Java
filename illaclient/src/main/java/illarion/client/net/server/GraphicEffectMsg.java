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
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Graphic effect.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_GRAPHIC_FX)
public final class GraphicEffectMsg implements ServerReply {
    /**
     * ID of the effect that shall be shown.
     */
    private int effectId;

    /**
     * The location the effect occurs on.
     */
    @Nullable
    private ServerCoordinate location;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        location = new ServerCoordinate(reader);
        effectId = reader.readUShort();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if (location == null) {
            throw new NotDecodedException();
        }
        if (World.getMap().isEmpty()) {
            //got no map data yet
            return ServerReplyResult.Reschedule;
        }

        MapTile tile = World.getMap().getMapAt(location);
        if (tile != null) {
            tile.showEffect(effectId);
            return ServerReplyResult.Success;
        }
        return ServerReplyResult.Failed;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(GraphicEffectMsg.class, location, "ID: " + effectId);
    }
}
