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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Change item on map
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_CHANGE_ITEM)
public final class ChangeItemMsg implements ServerReply {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(ChangeItemMsg.class);

    /**
     * The new count value of the item.
     */
    @Nullable
    private ItemCount count;

    /**
     * The location on the map this update is performed on.
     */
    @Nullable
    private Location location;

    /**
     * The ID of the item after the change.
     */
    @Nullable
    private ItemId newItem;

    /**
     * The ID of the item before the change.
     */
    @Nullable
    private ItemId oldItem;

    /**
     * The new move points of the tile the item is located on.
     */
    private int newTileMovePoints;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        location = new Location(reader);
        oldItem = new ItemId(reader);
        newItem = new ItemId(reader);
        count = ItemCount.getInstance(reader);
        newTileMovePoints = reader.readUByte();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((location == null) || (oldItem == null) || (newItem == null) || (count == null)) {
            throw new NotDecodedException();
        }

        MapTile tile = World.getMap().getMapAt(location);
        if (tile != null) {
            tile.changeTopItem(oldItem, newItem, count);
            if (newTileMovePoints == 255) {
                tile.setMovementCost(-1);
            } else {
                tile.setMovementCost(newTileMovePoints);
            }
        } else {
            log.warn("Received change item message for a tile that does not seem to exist.");
        }
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(ChangeItemMsg.class, "Old " + oldItem, "New " + newItem, location);
    }
}
