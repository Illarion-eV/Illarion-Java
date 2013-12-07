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
package illarion.client.world;

import illarion.client.net.server.TileUpdate;
import illarion.client.resources.TileFactory;
import illarion.client.util.GlobalExecutorService;
import illarion.common.graphics.TileInfo;
import illarion.common.types.Location;
import org.apache.log4j.Logger;
import org.illarion.engine.Engine;
import org.illarion.engine.EngineException;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.WorldMapDataProvider;
import org.illarion.engine.graphic.WorldMapDataProviderCallback;
import org.illarion.engine.nifty.IgeMiniMapRenderImage;
import org.illarion.engine.nifty.IgeRenderImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class stores a reduced version of the full map the character knows. The map data is packed to a minimized and
 * fast readable size that can be stored on the hard disk.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public final class GameMiniMap implements WorldMapDataProvider {
    /**
     * The height of the world map in tiles.
     */
    public static final int WORLDMAP_HEIGHT = 1024;

    /**
     * The width of the world map in tiles.
     */
    public static final int WORLDMAP_WIDTH = 1024;

    /**
     * The bytes that are reserved for one tile in the internal storage.
     */
    private static final int BYTES_PER_TILE = 2;

    /**
     * The log file handler that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger LOGGER = Logger.getLogger(GameMiniMap.class);

    /**
     * Indicated how many bits the blocked bit is shifted.
     */
    private static final int SHIFT_BLOCKED = 10;

    /**
     * Indicated how many bit the value of the overlay tile is shifted.
     */
    private static final int SHIFT_OVERLAY = 5;

    /**
     * The radius of the mini map.
     */
    private static final int MINI_RADIUS = 81;

    /**
     * This map contains all mini map data that is load. All map data is stored as soft references to allow them to
     * be cleaned up as needed.
     */
    @Nonnull
    private final Map<Location, Reference<ByteBuffer>> mapDataStorage;

    /**
     * This list is used to keep strong references to the map data that is currently active used in order to avoid
     * that this map data is cleared too soon.
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Nonnull
    private final List<ByteBuffer> strongMapDataStorage;

    /**
     * The origin location of the map.
     */
    @Nonnull
    private final Location mapOrigin;

    /**
     * The engine implementation of the world map.
     */
    @Nonnull
    private final WorldMap worldMap;

    /**
     * The image of the mini map as its rendered by the Nifty-GUI.
     */
    @Nonnull
    private final IgeRenderImage miniMapImage;

    /**
     * This variable turns {@code true} and stays true once the origin is set for the first time.
     */
    private boolean firstTimeSet;

    /**
     * Constructor of the game map that sets up all instance variables.
     */
    public GameMiniMap(@Nonnull final Engine engine) throws EngineException {
        worldMap = engine.getAssets().createWorldMap(this);
        miniMapImage = new IgeMiniMapRenderImage(engine, worldMap, MINI_RADIUS);

        mapDataStorage = new HashMap<Location, Reference<ByteBuffer>>();
        strongMapDataStorage = new ArrayList<ByteBuffer>(5);
        mapOrigin = new Location();
    }

    private static ByteBuffer createMapBuffer() {
        final int size = WorldMap.WORLD_MAP_WIDTH * WorldMap.WORLD_MAP_HEIGHT * BYTES_PER_TILE;
        final ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }

    /**
     * Get the entire origin of the current world map. The origin is stored in a {@link Location} class instance that
     * is newly fetched from the buffer. In case its not used anymore it should be put back into the buffer. <p> The
     * server X and Y coordinate are the coordinates of the current origin of the world map. The Z coordinate is the
     * current level. </p> <p> For details on each coordinate see the functions that request the single coordinates.
     * </p>
     *
     * @return the location of the overview map origin
     * @see #getMapLevel()
     * @see #getMapOriginX()
     * @see #getMapOriginY()
     */
    @Nonnull
    public Location getMapOrigin() {
        return getMapOrigin(new Location());
    }

    /**
     * This function is similar to {@link #getMapOrigin()}. The only difference is that this function does not fetch a
     * new instance of the location class, it rather uses the instance set as argument and fills it with the data.
     *
     * @param loc the location instance that is filled with the origin information
     * @return the same instance of the {@link Location} class that was set as parameter
     * @throws NullPointerException in case the parameter {@code loc} is set to {@code null}
     * @see #getMapOrigin()
     */
    @Nonnull
    public Location getMapOrigin(@Nonnull final Location loc) {
        loc.set(mapOrigin);
        return loc;
    }

    /**
     * Get the render image used to display the mini map.
     *
     * @return the render image used to display the mini map on the GUI
     */
    @Nonnull
    public IgeRenderImage getMiniMap() {
        return miniMapImage;
    }

    /**
     * Get the level of the mini/world-map that is currently displayed. This equals the server Z coordinate of the
     * current player location.
     *
     * @return the level of the maps
     */
    public int getMapLevel() {
        return mapOrigin.getScZ();
    }

    /**
     * Get the X coordinate of the origin of the current world map. This coordinate depends on the area the player is
     * currently at. <p> This coordinate is calculated the following way:<br /> {@code originX = floor(playerX / {@link
     * #WORLDMAP_WIDTH}) * {@link #WORLDMAP_WIDTH}} </p>
     *
     * @return the X coordinate of the map origin
     */
    public int getMapOriginX() {
        return mapOrigin.getScX();
    }

    /**
     * Get the Y coordinate of the origin of the current world map. This coordinate depends on the area the player is
     * currently at. <p> This coordinate is calculated the following way:<br /> {@code originY = floor(playerY / {@link
     * #WORLDMAP_HEIGHT}) * {@link #WORLDMAP_HEIGHT}} </p>
     *
     * @return the Y coordinate of the map origin
     */
    public int getMapOriginY() {
        return mapOrigin.getScY();
    }

    /**
     * Update the world map texture.
     */
    public void render(@Nonnull final GameContainer container) {
        worldMap.render(container);
    }

    /**
     * Encode a server location to the index in the map data buffer.
     *
     * @param x the x coordinate of the location on the map
     * @param y the y coordinate of the location on the map
     * @return the index of the location in the map data buffer
     * @throws IllegalArgumentException in case either x or y is out of the local range
     */
    private int encodeLocation(final int x, final int y) {
        final int mapOriginY = getMapOriginY();
        if ((y < mapOriginY) || (y >= (mapOriginY + WORLDMAP_HEIGHT))) {
            throw new IllegalArgumentException("y out of range");
        }

        final int mapOriginX = getMapOriginX();
        if ((x < mapOriginX) || (x >= (mapOriginX + WORLDMAP_WIDTH))) {
            throw new IllegalArgumentException("x out of range");
        }

        return ((y - mapOriginY) * WORLDMAP_WIDTH * BYTES_PER_TILE) + ((x - mapOriginX) * BYTES_PER_TILE);
    }

    /**
     * Check if a location is within the coordinate space of the currently load set of mini maps.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return {@code true} in case both coordinates are inside the legal range for the currently load maps.
     */
    private boolean isLocationOnMap(final int x, final int y, final int z) {
        final int mapOriginZ = getMapLevel();
        if ((z < (mapOriginZ - 2)) || (z > (mapOriginZ + 2))) {
            return false;
        }

        final int mapOriginY = getMapOriginY();
        if ((y < mapOriginY) || (y >= (mapOriginY + WORLDMAP_HEIGHT))) {
            return false;
        }

        final int mapOriginX = getMapOriginX();
        return !((x < mapOriginX) || (x >= (mapOriginX + WORLDMAP_WIDTH)));
    }

    /**
     * Check if a location is within the coordinate space of the currently load set of mini maps.
     *
     * @param loc the location
     * @return {@code true} in case both coordinates are inside the legal range for the currently load maps.
     */
    private boolean isLocationOnMap(@Nonnull final Location loc) {
        return isLocationOnMap(loc.getScX(), loc.getScY(), loc.getScZ());
    }

    /**
     * This function causes the byte buffer that holds the mini map data in the storage to be stored only with a soft
     * reference. After this was called its possible that the data is cleared at some point in future.
     *
     * @param origin the origin location of the map.
     */
    private void weakenMapDataStorage(@Nonnull final Location origin) {
        final ByteBuffer mapData = getMapDataStorage(origin);
        if (mapData == null) {
            mapDataStorage.remove(origin);
        } else {
            strongMapDataStorage.remove(mapData);
        }
    }

    /**
     * Fetch the map data from the storage.
     *
     * @param mapOrigin the origin of the map
     * @return the map data or {@code null} in case the data for this map is not load
     */
    @Nullable
    private ByteBuffer getMapDataStorage(@Nonnull final Location mapOrigin) {
        @Nullable final Reference<ByteBuffer> mapDataRef = mapDataStorage.get(mapOrigin);
        if (mapDataRef == null) {
            return null;
        }
        @Nullable final ByteBuffer mapData = mapDataRef.get();
        return mapData;
    }

    /**
     * Get the list of origins that needs to be handled as alive.
     *
     * @param origin the tested origin
     * @return the list that contains the five origin locations
     */
    @Nonnull
    private static List<Location> getOriginsList(@Nonnull final Location origin) {
        final List<Location> list = new ArrayList<Location>(5);
        for (int i = -2; i <= 2; i++) {
            final Location loc = new Location();
            loc.setSC(origin.getScX(), origin.getScY(), origin.getScZ() + i);
            list.add(loc);
        }
        return list;
    }

    /**
     * Check if the origin has changed and needs to be updated.
     *
     * @param newOrigin the new origin
     * @return {@code true} in case the origin has changed and updates are required
     */
    public boolean hasOriginChanged(@Nonnull final Location newOrigin) {
        return !(newOrigin.equals(mapOrigin) && firstTimeSet);
    }

    /**
     * This function handles everything that is needed to change the origin.
     *
     * @param newOrigin the new origin that should be applied
     */
    private void changeOrigin(@Nonnull final Location newOrigin) {
        if (!hasOriginChanged(newOrigin)) {
            return;
        }
        final Location oldOrigin = new Location(mapOrigin);
        mapOrigin.set(newOrigin);

        if (!firstTimeSet) {
            final List<Location> loadList = getOriginsList(newOrigin);
            for (@Nonnull final Location loc : loadList) {
                strengthenOrLoadMap(loc);
            }
            firstTimeSet = true;
            return;
        }

        /* Find out what origins turn in active and what new ones get active. */
        final List<Location> oldActive = getOriginsList(oldOrigin);
        final List<Location> newActive = getOriginsList(newOrigin);

        @Nonnull final Iterator<Location> oldActiveItr = oldActive.iterator();
        while (oldActiveItr.hasNext()) {
            final Location checkedOrigin = oldActiveItr.next();
            if (newActive.contains(checkedOrigin)) {
                newActive.remove(checkedOrigin);
                oldActiveItr.remove();
            }
        }

        final Collection<Callable<Void>> updateList = new ArrayList<Callable<Void>>();

        /* Save all the old data to the file system and weaken the storage of each map. */
        for (@Nonnull final Location loc : oldActive) {
            updateList.add(new Callable<Void>() {
                @Nullable
                @Override
                public Void call() {
                    saveMap(loc);
                    weakenMapDataStorage(loc);
                    return null;
                }
            });
        }

        /* Reactivate or load all the maps that are newly inside the player range. */
        for (@Nonnull final Location loc : newActive) {
            updateList.add(new Callable<Void>() {
                @Nullable
                @Override
                public Void call() {
                    strengthenOrLoadMap(loc);
                    return null;
                }
            });
        }

        boolean executionInProgress = true;
        while (executionInProgress) {
            executionInProgress = false;
            try {
                GlobalExecutorService.getService().invokeAll(updateList);
            } catch (@Nonnull final InterruptedException ignored) {
                executionInProgress = true;
            }
        }
    }

    /**
     * The mask to fetch the ID of the tile.
     */
    private static final int MASK_TILE_ID = 0x1F;

    /**
     * The mask to fetch the ID of the overlay.
     */
    private static final int MASK_OVERLAY_ID = 0x3E0;

    /**
     * The mask to fetch the blocked flag.
     */
    private static final int MASK_BLOCKED = 0x400;

    @Override
    public void requestTile(@Nonnull final Location location, @Nonnull final WorldMapDataProviderCallback callback) {
        if ((location.getScZ() != getMapLevel()) || !isLocationOnMap(location)) {
            callback.setTile(location, WorldMap.NO_TILE, WorldMap.NO_TILE, false);
            return;
        }
        final ByteBuffer mapData = getMapDataStorage(mapOrigin);
        if (mapData == null) {
            callback.setTile(location, WorldMap.NO_TILE, WorldMap.NO_TILE, false);
            return;
        }
        final int tileData = mapData.getShort(encodeLocation(location));

        if (tileData == 0) {
            callback.setTile(location, WorldMap.NO_TILE, WorldMap.NO_TILE, false);
        } else {
            final int tileId = tileData & MASK_TILE_ID;
            final int overlayId = tileData & MASK_OVERLAY_ID;
            final boolean blocked = (tileData & MASK_BLOCKED) > 0;

            final int tileMapColor;
            final int overlayMapColor;
            if (TileFactory.getInstance().hasTemplate(tileId)) {
                tileMapColor = TileFactory.getInstance().getTemplate(tileId).getTileInfo().getMapColor();
                if (TileFactory.getInstance().hasTemplate(overlayId)) {
                    overlayMapColor = TileFactory.getInstance().getTemplate(overlayId).getTileInfo().getMapColor();
                } else {
                    overlayMapColor = WorldMap.NO_TILE;
                }
            } else {
                tileMapColor = WorldMap.NO_TILE;
                overlayMapColor = WorldMap.NO_TILE;
            }

            callback.setTile(location, tileMapColor, overlayMapColor, blocked);
        }
    }

    @Nonnull
    private static Location getOriginLocation(@Nonnull final Location playerLoc) {
        final int newMapLevel = playerLoc.getScZ();

        final int newMapOriginX;
        if (playerLoc.getScX() >= 0) {
            newMapOriginX = (playerLoc.getScX() / WORLDMAP_WIDTH) * WORLDMAP_WIDTH;
        } else {
            newMapOriginX = ((playerLoc.getScX() / WORLDMAP_WIDTH) * WORLDMAP_WIDTH) - WORLDMAP_WIDTH;
        }

        final int newMapOriginY;
        if (playerLoc.getScY() >= 0) {
            newMapOriginY = (playerLoc.getScY() / WORLDMAP_HEIGHT) * WORLDMAP_HEIGHT;
        } else {
            newMapOriginY = ((playerLoc.getScY() / WORLDMAP_HEIGHT) * WORLDMAP_HEIGHT) - WORLDMAP_HEIGHT;
        }

        return new Location(newMapOriginX, newMapOriginY, newMapLevel);
    }

    /**
     * Set the location of the player. This tells the world map handler what map it needs to draw.
     *
     * @param playerLoc the location of the player
     */
    public void setPlayerLocation(@Nonnull final Location playerLoc) {
        @Nonnull final Location newOrigin = getOriginLocation(playerLoc);
        if (hasOriginChanged(newOrigin)) {
            changeOrigin(newOrigin);
            worldMap.setMapOrigin(newOrigin);
        }
        worldMap.setPlayerLocation(playerLoc);
    }

    /**
     * Save all maps that are currently load to the hard disk.
     */
    public void saveAllMaps() {
        final List<Location> origins = getOriginsList(mapOrigin);
        for (@Nonnull final Location loc : origins) {
            saveMap(loc);
        }
    }

    /**
     * Save the current map to its file.
     */
    @SuppressWarnings("nls")
    private void saveMap(@Nonnull final Location origin) {
        @Nullable final Reference<ByteBuffer> mapDataRef = mapDataStorage.get(origin);
        if (mapDataRef == null) {
            return;
        }
        @Nullable final ByteBuffer mapData = mapDataRef.get();
        if (mapData == null) {
            return;
        }

        final File mapFile = getMapFilename(origin);
        if (mapFile.exists() && !mapFile.canWrite()) {
            LOGGER.error("The map file is not accessible. Can't write anything.");
            return;
        }

        WritableByteChannel outChannel = null;
        try {
            final FileOutputStream outStream = new FileOutputStream(mapFile);
            final GZIPOutputStream gOutStream = new GZIPOutputStream(outStream);
            outChannel = Channels.newChannel(gOutStream);
            synchronized (mapData) {
                mapData.rewind();
                int toWrite = mapData.remaining();
                while (toWrite > 0) {
                    toWrite -= outChannel.write(mapData);
                }
            }
        } catch (@Nonnull final FileNotFoundException e) {
            LOGGER.error("Target file not found", e);
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Error while writing minimap file", e);
        } finally {
            closeQuietly(outChannel);
        }
    }

    /**
     * This function closes a closeable object without exposing any kind of error handling.
     *
     * @param closeable the object to be closed
     */
    private static void closeQuietly(@Nullable final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (@Nonnull final IOException ignored) {
            }
        }
    }

    /**
     * Get the full path string to the file for the currently selected map. This file needs to be used to store and
     * load
     * the map data.
     *
     * @return the path and the filename of the map file
     */
    @SuppressWarnings("nls")
    @Nonnull
    private static File getMapFilename(@Nonnull final Location mapOrigin) {
        final StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        builder.append("map");
        builder.append(mapOrigin.getScX() / WORLDMAP_WIDTH);
        builder.append(mapOrigin.getScY() / WORLDMAP_HEIGHT);
        builder.append(mapOrigin.getScZ());
        builder.append(".dat");
        return new File(World.getPlayer().getPath(), builder.toString());
    }

    /**
     * This function either creates a strong storage entry of the map data or loads the map data from the file system
     * and create the strong storage after.
     *
     * @param mapOrigin the origin of the map
     */
    private void strengthenOrLoadMap(@Nonnull final Location mapOrigin) {
        /* First check if the map is still around. */
        final ByteBuffer existingMapData = getMapDataStorage(mapOrigin);
        if (existingMapData != null) {
            strongMapDataStorage.add(existingMapData);
            return;
        }

        @Nonnull final ByteBuffer mapData = createMapBuffer();
        final File mapFile = getMapFilename(mapOrigin);

        mapDataStorage.put(mapOrigin, new SoftReference<ByteBuffer>(mapData));
        strongMapDataStorage.add(mapData);

        if (!mapFile.exists()) {
            return;
        }

        @Nullable ReadableByteChannel inChannel = null;
        try {
            final InputStream inStream = new GZIPInputStream(new FileInputStream(mapFile));
            inChannel = Channels.newChannel(inStream);

            synchronized (mapData) {
                mapData.rewind();

                int read = 1;
                while (read > 0) {
                    read = inChannel.read(mapData);
                }
                inChannel.close();
            }

            performFullUpdate();
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Failed loading the map data from its file.", e);
            synchronized (mapData) {
                mapData.rewind();
                while (mapData.hasRemaining()) {
                    mapData.put((byte) 0);
                }
            }
        } finally {
            closeQuietly(inChannel);
        }
    }

    /**
     * Once this function is called the mini map will be rendered completely again.
     */
    public void performFullUpdate() {
        GlobalExecutorService.getService().submit(new Runnable() {
            @Override
            public void run() {
                worldMap.setMapChanged();
            }
        });
    }

    /**
     * Update one tile of the overview map.
     *
     * @param updateData the data that is needed for the update
     */
    public void update(@Nonnull final TileUpdate updateData) {
        final Location tileLoc = updateData.getLocation();

        if (isLocationOnMap(tileLoc)) {
            if (saveTile(tileLoc, updateData.getTileId(), updateData.isBlocked())) {
                worldMap.setTileChanged(tileLoc);
            }
        }
    }

    /**
     * Save the information about a tile within the map data. This will overwrite any existing data about a tile.
     *
     * @param loc     the location of the tile
     * @param tileID  the ID of tile that is located at the position
     * @param blocked true in case this tile is not passable
     * @return {@code true} in case the new ID of the tile and the already set ID are not equal and the ID did change
     *         this way
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    private boolean saveTile(@Nonnull final Location loc, final int tileID, final boolean blocked) {
        final int index = encodeLocation(loc);

        final Location origin = getOriginLocation(loc);
        final ByteBuffer mapData = getMapDataStorage(origin);
        if (mapData == null) {
            return false;
        }

        if (tileID == MapTile.ID_NONE) {
            synchronized (mapData) {
                if (mapData.getShort(index) == 0) {
                    return false;
                }
                mapData.putShort(index, (short) 0);
            }
            return true;
        }

        short encodedTileValue = (short) TileInfo.getBaseID(tileID);
        encodedTileValue += TileInfo.getOverlayID(tileID) << SHIFT_OVERLAY;
        if (blocked) {
            encodedTileValue += 1 << SHIFT_BLOCKED;
        }

        synchronized (mapData) {
            if (mapData.getShort(index) == encodedTileValue) {
                return false;
            }
            mapData.putShort(index, encodedTileValue);
        }
        return true;
    }

    /**
     * Encode a server location to the index in the map data buffer.
     *
     * @param loc the server location that shall be encoded
     * @return the index of the location in the map data buffer
     */
    private int encodeLocation(@Nonnull final Location loc) {
        return encodeLocation(loc.getScX(), loc.getScY());
    }
}
