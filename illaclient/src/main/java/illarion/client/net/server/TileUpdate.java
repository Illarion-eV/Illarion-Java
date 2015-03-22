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

import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class that stores all needed information for a update of a single tile.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@Immutable
public final class TileUpdate {
    /**
     * List of count values for the items on this tile.
     */
    @Nonnull
    private final List<ItemCount> itemCount;

    /**
     * List of Item IDs on this tile.
     */
    @Nonnull
    private final List<ItemId> itemId;

    /**
     * Count of item stacks on the tile.
     */
    private final int itemNumber;

    /**
     * Location of the tile.
     */
    @Nonnull
    private final ServerCoordinate tileLocation;

    /**
     * ID of this tile.
     */
    private final int tileId;

    /**
     * The ID of the sound track that is supposed to be played while the user is standing on this tile.
     */
    private final int tileMusic;

    /**
     * The movement cost for this tile.
     */
    private final int movementCost;

    /**
     * Constructor for this new tile update.
     */
    public TileUpdate(@Nonnull ServerCoordinate loc, @Nonnull NetCommReader reader) throws IOException {
        tileLocation = loc;

        // read tile attributes
        tileId = reader.readShort();

        // read the movement cost for this tile
        movementCost = reader.readUByte();

        // read the sound track of this tile
        tileMusic = reader.readUShort();

        // read items
        itemNumber = reader.readUByte();

        itemId = Arrays.asList(new ItemId[itemNumber]);
        itemCount = Arrays.asList(new ItemCount[itemNumber]);

        for (int i = 0; i < itemNumber; ++i) {
            itemId.set(i, new ItemId(reader));
            itemCount.set(i, ItemCount.getInstance(reader));
        }
    }

    /**
     * Get a list of item counts on this tile.
     *
     * @return the list of item counts
     */
    @Nonnull
    @Contract(pure = true)
    public List<ItemCount> getItemCount() {
        return Collections.unmodifiableList(itemCount);
    }

    /**
     * Get a list of item ids on this tile.
     *
     * @return the list of item ids
     */
    @Nonnull
    @Contract(pure = true)
    public List<ItemId> getItemId() {
        return Collections.unmodifiableList(itemId);
    }

    /**
     * Get the number of item stacks on this tile.
     *
     * @return the number of item stacks
     */
    @Contract(pure = true)
    public int getItemNumber() {
        return itemNumber;
    }

    /**
     * Get the location of the tile this updates describes.
     *
     * @return the location of the tile.
     */
    @Nonnull
    @Contract(pure = true)
    public ServerCoordinate getLocation() {
        return tileLocation;
    }

    /**
     * Get the ID of the tile this update describes.
     *
     * @return the tile id of the tile this update describes
     */
    @Contract(pure = true)
    public int getTileId() {
        return tileId;
    }

    /**
     * Get the music ID that is assigned to this tile.
     *
     * @return the music ID of this tile
     */
    @Contract(pure = true)
    public int getTileMusic() {
        return tileMusic;
    }

    /**
     * Get if the map tile is blocked by a static item.
     *
     * @return true if the tile is static blocked
     */
    @Contract(pure = true)
    public boolean isBlocked() {
        return movementCost == 255;
    }

    /**
     * Get the movement cost for a move on this file.
     *
     * @return the movement cost or {@code 255} in case the tile is blocked
     */
    @Contract(pure = true)
    public int getMovementCost() {
        return movementCost;
    }
}
