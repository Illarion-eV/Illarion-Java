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
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Remove the top item on a tile ( {@link illarion.client.net.CommandList#MSG_REMOVE_ITEM}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_REMOVE_ITEM)
public final class RemoveItemMsg extends AbstractReply {
    /**
     * The location the top item shall be removed from.
     */
    private Location loc;

    private int newTileMovePoints;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        loc = decodeLocation(reader);
        newTileMovePoints = reader.readUByte();
    }

    @Override
    public void executeUpdate() {
        MapTile tile = World.getMap().getMapAt(loc);
        if (tile != null) {
            tile.removeTopItem();
            if (newTileMovePoints == 255) {
                tile.setMovementCost(-1);
            } else {
                tile.setMovementCost(newTileMovePoints);
            }
        }
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("location: " + loc.toString());
    }
}
