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
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Location;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Add a item on a map tile
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_PUT_ITEM)
public final class PutItemMsg implements ServerReply {
    /**
     * The ID of the item that is placed on the ground.
     */
    @Nullable
    private ItemId itemId;

    /**
     * The location the item is placed at.
     */
    @Nullable
    private Location loc;

    /**
     * The count value of the item that is placed on the ground.
     */
    @Nullable
    private ItemCount number;

    /**
     * The new move points of the tile the item is located on.
     */
    private int newTileMovePoints;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        loc = new Location(reader);
        itemId = new ItemId(reader);
        number = ItemCount.getInstance(reader);
        newTileMovePoints = reader.readUByte();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((loc == null) || (itemId == null) || (number == null)) {
            throw new NotDecodedException();
        }

        MapTile tile = World.getMap().getMapAt(loc);
        if (tile != null) {
            tile.addItem(itemId, number);
            if (newTileMovePoints == 255) {
                tile.setMovementCost(-1);
            } else {
                tile.setMovementCost(newTileMovePoints);
            }
            return ServerReplyResult.Success;
        }
        return ServerReplyResult.Failed;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(PutItemMsg.class, itemId, loc);
    }
}
