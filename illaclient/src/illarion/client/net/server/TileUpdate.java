/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server;

import java.io.IOException;

import javolution.util.FastList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TShortArrayList;

import illarion.client.net.NetCommReader;
import illarion.client.world.MapTile;

import illarion.common.util.Location;
import illarion.common.util.Reusable;

/**
 * Class that stores all needed informations for a update of a single tile.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class TileUpdate implements Reusable {
    /**
     * The factory that creates instances of the TileUpdate class and stores
     * them for reuse.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class TileUpdateFactory {
        /**
         * The list used to store the currently unused instances of this class.
         */
        private final FastList<TileUpdate> buffer;

        /**
         * A public constructor to create a instance of this class in the parent
         * class.
         */
        public TileUpdateFactory() {
            buffer = new FastList<TileUpdate>();
        }

        /**
         * Get a new instance of the buffer object. Its either reused or new
         * created.
         * 
         * @return the instance that is now ready to be used
         */
        public TileUpdate object() {
            synchronized (buffer) {
                if (!buffer.isEmpty()) {
                    return buffer.removeLast();
                }
            }

            return create();
        }

        /**
         * Recycle the tile update by placing it at the end of the list.
         * 
         * @param update the tile update instance to recycle
         */
        public void recycle(final TileUpdate update) {
            synchronized (buffer) {
                buffer.addLast(update);
            }
        }

        /**
         * Create a new instance of the managed object.
         * 
         * @return the new instance of tile update
         */
        protected TileUpdate create() {
            return new TileUpdate();
        }

    }

    /**
     * Default size of the arrays that store the items of this tile update. The
     * size is increased automatically in case its needed.
     */
    private static final int DEFAULT_SIZE = 5;

    /**
     * The factory that creates and stores the instances of this class.
     */
    private static final TileUpdateFactory FACTORY = new TileUpdateFactory();

    /**
     * List of count values for the items on this tile.
     */
    private final TShortArrayList itemCount =
        new TShortArrayList(DEFAULT_SIZE);

    /**
     * List of Item IDs on this tile.
     */
    private final TIntArrayList itemId = new TIntArrayList(DEFAULT_SIZE);

    /**
     * Count of item stacks on the tile.
     */
    private int itemNumber;

    /**
     * Location of the tile.
     */
    private final transient Location loc;

    /**
     * Reference to the map tile that is used here.
     */
    private transient MapTile mapTile;

    /**
     * ID of this tile.
     */
    private int tileId;

    /**
     * The ID of the sound track that is supposed to be played while the user is
     * standing on this tile.
     */
    private int tileMusic;

    /**
     * Constructor for this new tile update.
     */
    TileUpdate() {
        loc = new Location();
    }

    /**
     * Get a instance of the TileUpdate that is currently not in use. Its either
     * taken from the storage or newly created.
     * 
     * @return the TileUpdate instance that is now free to be used
     */
    public static TileUpdate getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the coverage of the map tile that was created by this update. This
     * also clears the reference to the map tile.
     * 
     * @return the coverage of the map tile
     */
    public int getCoverage() {
        if (mapTile != null) {
            return mapTile.getCoverage();
        }
        return 0;
    }

    /**
     * Get a list of item counts on this tile.
     * 
     * @return the list of item counts
     */
    public TShortArrayList getItemCount() {
        return itemCount;
    }

    /**
     * Get a list of item ids on this tile.
     * 
     * @return the list of item ids
     */
    public TIntArrayList getItemId() {
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
    public Location getLocation() {
        return loc;
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
        if (mapTile != null) {
            return mapTile.isObstacle();
        }
        return false;
    }

    /**
     * Clean the references from this function in case there are any remaining.
     * This function should be called after the last operation is done with this
     * object.
     */
    @Override
    public void recycle() {
        FACTORY.recycle(this);
    }

    /**
     * Reset the state of this instance to make it ready for the next use.
     */
    @Override
    public void reset() {
        mapTile = null;
        itemId.reset();
        itemCount.reset();
    }

    /**
     * Change the location this Tile update points at.
     * 
     * @param newLoc the new location.
     */
    public void setLocation(final Location newLoc) {
        loc.set(newLoc);
        mapTile = null;
    }

    /**
     * Set the real map tile that was created by this update, for later usage
     * along with the mini map update.
     * 
     * @param newMapTile the map tile that was created by this update
     */
    public void setMapTile(final MapTile newMapTile) {
        mapTile = newMapTile;
    }

    /**
     * Decode the tile data the receiver got and store it until the update is
     * executed.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    protected void decode(final NetCommReader reader) throws IOException {
        // read tile attributes
        tileId = reader.readShort();

        // read the sound track of this tile
        tileMusic = reader.readUShort();

        // read items
        itemNumber = reader.readUByte();

        for (int i = 0; i < itemNumber; ++i) {
            itemId.add(reader.readShort());
            itemCount.add(reader.readUByte());
        }
    }
}
