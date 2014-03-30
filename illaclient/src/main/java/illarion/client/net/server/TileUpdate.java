/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server;

import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.util.ArrayList;
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
     * Default size of the arrays that store the items of this tile update. The size is increased automatically in
     * case its needed.
     */
    private static final int DEFAULT_SIZE = 5;

    /**
     * List of count values for the items on this tile.
     */
    private final List<ItemCount> itemCount = new ArrayList<>(DEFAULT_SIZE);

    /**
     * List of Item IDs on this tile.
     */
    private final List<ItemId> itemId = new ArrayList<>(DEFAULT_SIZE);

    /**
     * Count of item stacks on the tile.
     */
    private final int itemNumber;

    /**
     * Location of the tile.
     */
    @Nonnull
    private final Location tileLocation;

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
    public TileUpdate(@Nonnull Location loc, @Nonnull NetCommReader reader) throws IOException {
        tileLocation = new Location(loc);

        // read tile attributes
        tileId = reader.readShort();

        // read the movement cost for this tile
        movementCost = reader.readUByte();

        // read the sound track of this tile
        tileMusic = reader.readUShort();

        // read items
        itemNumber = reader.readUByte();

        for (int i = 0; i < itemNumber; ++i) {
            itemId.add(new ItemId(reader));
            itemCount.add(ItemCount.getInstance(reader));
        }
    }

    /**
     * Get a list of item counts on this tile.
     *
     * @return the list of item counts
     */
    @Nonnull
    public List<ItemCount> getItemCount() {
        return itemCount;
    }

    /**
     * Get a list of item ids on this tile.
     *
     * @return the list of item ids
     */
    @Nonnull
    public List<ItemId> getItemId() {
        return itemId;
    }

    /**
     * Get the number of item stacks on this tile.
     *
     * @return the number of item stacks
     */
    public int getItemNumber() {
        return itemNumber;
    }

    /**
     * Get the location of the tile this updates describes.
     *
     * @return the location of the tile.
     */
    @Nonnull
    public Location getLocation() {
        return tileLocation;
    }

    /**
     * Get the ID of the tile this update describes.
     *
     * @return the tile id of the tile this update describes
     */
    public int getTileId() {
        return tileId;
    }

    /**
     * Get the music ID that is assigned to this tile.
     *
     * @return the music ID of this tile
     */
    public int getTileMusic() {
        return tileMusic;
    }

    /**
     * Get if the map tile is blocked by a static item.
     *
     * @return true if the tile is static blocked
     */
    public boolean isBlocked() {
        return movementCost == 255;
    }

    /**
     * Get the movement cost for a move on this file.
     *
     * @return the movement cost or {@code 255} in case the tile is blocked
     */
    public int getMovementCost() {
        return movementCost;
    }
}
