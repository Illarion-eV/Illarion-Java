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
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Server message: Update Items on map
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_UPDATE_ITEMS)
public final class ItemUpdateMsg implements ServerReply {
    /**
     * The value for {@link #newTileMovePoints} to indicate that the field is blocked.
     */
    private static final int BLOCKED_MOVEMENT_POINTS = 255;

    /**
     * Count values for each item on this map tile.
     */
    @Nullable
    private List<ItemCount> itemCount;

    /**
     * List of the item IDs on this map tile.
     */
    @Nullable
    private List<ItemId> itemId;

    /**
     * Amount of item stacks on the map tile.
     */
    private short itemNumber;

    /**
     * Position of the server map that is updated.
     */
    private ServerCoordinate location;

    /**
     * The new movement points of the tile.
     */
    private int newTileMovePoints;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        location = new ServerCoordinate(reader);

        itemNumber = reader.readUByte();
        itemId = Arrays.asList(new ItemId[itemNumber]);
        itemCount = Arrays.asList(new ItemCount[itemNumber]);
        for (int i = 0; i < itemNumber; ++i) {
            itemId.set(i, new ItemId(reader));
            itemCount.set(i, ItemCount.getInstance(reader.readUShort()));
        }
        newTileMovePoints = reader.readUByte();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((location == null) || (itemId == null) || (itemCount == null)) {
            throw new NotDecodedException();
        }

        MapTile tile = World.getMap().getMapAt(location);
        if (tile != null) {
            tile.updateItems(itemNumber, itemId, itemCount);
            if (newTileMovePoints == BLOCKED_MOVEMENT_POINTS) {
                tile.setMovementCost(-1);
            } else {
                tile.setMovementCost(newTileMovePoints);
            }
        }
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(ItemUpdateMsg.class, location);
    }
}
