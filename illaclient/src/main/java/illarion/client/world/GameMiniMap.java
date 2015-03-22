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
package illarion.client.world;

import illarion.client.net.server.TileUpdate;
import illarion.client.resources.TileFactory;
import illarion.client.util.GlobalExecutorService;
import illarion.common.graphics.TileInfo;
import illarion.common.types.ServerCoordinate;
import illarion.common.util.Stoppable;
import org.illarion.engine.Engine;
import org.illarion.engine.EngineException;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.WorldMapDataProvider;
import org.illarion.engine.graphic.WorldMapDataProviderCallback;
import org.illarion.engine.nifty.IgeMiniMapRenderImage;
import org.illarion.engine.nifty.IgeRenderImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
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
public final class GameMiniMap implements WorldMapDataProvider, Stoppable {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMiniMap.class);

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
    private final Map<ServerCoordinate, Reference<ByteBuffer>> mapDataStorage;

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
    @Nullable
    private ServerCoordinate mapOrigin;

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
     * Constructor of the game map that sets up all instance variables.
     */
    public GameMiniMap(@Nonnull Engine engine) throws EngineException {
        worldMap = engine.getAssets().createWorldMap(this);
        miniMapImage = new IgeMiniMapRenderImage(engine, worldMap, MINI_RADIUS);

        mapDataStorage = new HashMap<>();
        strongMapDataStorage = new ArrayList<>(5);
    }

    @Nonnull
    private static ByteBuffer createMapBuffer() {
        int size = WorldMap.WORLD_MAP_WIDTH * WorldMap.WORLD_MAP_HEIGHT * BYTES_PER_TILE;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }

    /**
     * Get the entire origin of the current world map. The origin is stored in a
     * {@link illarion.common.types.ServerCoordinate} class instance that
     * is newly fetched from the buffer. In case its not used anymore it should be put back into the buffer. <p> The
     * server X and Y coordinate are the coordinates of the current origin of the world map. The Z coordinate is the
     * current level. </p> <p> For details on each coordinate see the functions that request the single coordinates.
     * </p>
     *
     * @return the location of the overview map origin
     */
    @Nonnull
    public ServerCoordinate getMapOrigin() {
        if (mapOrigin == null) {
            throw new IllegalStateException("The origin of the mini map was not set yet.");
        }
        return mapOrigin;
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
     * Update the world map texture.
     */
    public void render(@Nonnull GameContainer container) {
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
    private int encodeLocation(int x, int y) {
        if (mapOrigin == null) {
            throw new IllegalStateException(
                    "Encoding the location is not possible until the origin of the map is set.");
        }
        int mapOriginY = mapOrigin.getY();
        if ((y < mapOriginY) || (y >= (mapOriginY + WORLDMAP_HEIGHT))) {
            throw new IllegalArgumentException("y out of range");
        }

        int mapOriginX = mapOrigin.getX();
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
    private boolean isLocationOnMap(int x, int y, int z) {
        if (mapOrigin == null) {
            throw new IllegalStateException(
                    "Checking if a location is on the mini map is not possible until the origin of the map is set.");
        }

        int mapOriginZ = mapOrigin.getZ();
        if ((z < (mapOriginZ - 2)) || (z > (mapOriginZ + 2))) {
            return false;
        }

        int mapOriginY = mapOrigin.getY();
        if ((y < mapOriginY) || (y >= (mapOriginY + WORLDMAP_HEIGHT))) {
            return false;
        }

        int mapOriginX = mapOrigin.getX();
        return !((x < mapOriginX) || (x >= (mapOriginX + WORLDMAP_WIDTH)));
    }

    /**
     * Check if a location is within the coordinate space of the currently load set of mini maps.
     *
     * @param loc the location
     * @return {@code true} in case both coordinates are inside the legal range for the currently load maps.
     */
    private boolean isLocationOnMap(@Nonnull ServerCoordinate loc) {
        return isLocationOnMap(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * This function causes the byte buffer that holds the mini map data in the storage to be stored only with a soft
     * reference. After this was called its possible that the data is cleared at some point in future.
     *
     * @param origin the origin location of the map.
     */
    private void weakenMapDataStorage(@Nonnull ServerCoordinate origin) {
        ByteBuffer mapData = getMapDataStorage(origin);
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
    private ByteBuffer getMapDataStorage(@Nonnull ServerCoordinate mapOrigin) {
        @Nullable Reference<ByteBuffer> mapDataRef = mapDataStorage.get(mapOrigin);
        if (mapDataRef == null) {
            return null;
        }
        @Nullable ByteBuffer mapData = mapDataRef.get();
        return mapData;
    }

    /**
     * Get the list of origins that needs to be handled as alive.
     *
     * @param origin the tested origin
     * @return the list that contains the five origin locations
     */
    @Nonnull
    private static List<ServerCoordinate> getOriginsList(@Nonnull ServerCoordinate origin) {
        List<ServerCoordinate> list = new ArrayList<>(5);
        for (int i = -2; i <= 2; i++) {
            list.add(new ServerCoordinate(origin, 0, 0, i));
        }
        return list;
    }

    /**
     * This function handles everything that is needed to change the origin.
     *
     * @param newOrigin the new origin that should be applied
     */
    private void changeOrigin(@Nonnull ServerCoordinate newOrigin) {
        if (Objects.equals(mapOrigin, newOrigin)) {
            return;
        }

        ServerCoordinate oldOrigin = mapOrigin;
        mapOrigin = newOrigin;

        if (oldOrigin == null) {
            List<ServerCoordinate> loadList = getOriginsList(newOrigin);
            for (@Nonnull ServerCoordinate loc : loadList) {
                strengthenOrLoadMap(loc);
            }
            return;
        }

        /* Find out what origins turn in active and what new ones get active. */
        List<ServerCoordinate> oldActive = getOriginsList(oldOrigin);
        List<ServerCoordinate> newActive = getOriginsList(newOrigin);

        @Nonnull Iterator<ServerCoordinate> oldActiveItr = oldActive.iterator();
        while (oldActiveItr.hasNext()) {
            ServerCoordinate checkedOrigin = oldActiveItr.next();
            if (newActive.contains(checkedOrigin)) {
                newActive.remove(checkedOrigin);
                oldActiveItr.remove();
            }
        }

        Collection<Callable<Void>> updateList = new ArrayList<>();

        /* Save all the old data to the file system and weaken the storage of each map. */
        for (@Nonnull final ServerCoordinate loc : oldActive) {
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
        for (@Nonnull final ServerCoordinate loc : newActive) {
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
            } catch (@Nonnull InterruptedException ignored) {
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
    public void requestTile(@Nonnull ServerCoordinate location, @Nonnull WorldMapDataProviderCallback callback) {
        if (mapOrigin == null) {
            throw new IllegalStateException("Requesting a new tile is illegal while the origin of the map is not set.");
        }

        if ((location.getZ() != mapOrigin.getZ()) || !isLocationOnMap(location)) {
            callback.setTile(location, WorldMap.NO_TILE, WorldMap.NO_TILE, false);
            return;
        }
        ByteBuffer mapData = getMapDataStorage(mapOrigin);
        if (mapData == null) {
            callback.setTile(location, WorldMap.NO_TILE, WorldMap.NO_TILE, false);
            return;
        }
        int tileData = mapData.getShort(encodeLocation(location));

        if (tileData == 0) {
            callback.setTile(location, WorldMap.NO_TILE, WorldMap.NO_TILE, false);
        } else {
            int tileId = tileData & MASK_TILE_ID;
            int overlayId = tileData & MASK_OVERLAY_ID;
            boolean blocked = (tileData & MASK_BLOCKED) > 0;

            int tileMapColor;
            int overlayMapColor;
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
    private static ServerCoordinate getOriginLocation(@Nonnull ServerCoordinate playerLoc) {
        int newMapLevel = playerLoc.getZ();

        int newMapOriginX;
        if (playerLoc.getX() >= 0) {
            newMapOriginX = (playerLoc.getX() / WORLDMAP_WIDTH) * WORLDMAP_WIDTH;
        } else {
            newMapOriginX = ((playerLoc.getX() / WORLDMAP_WIDTH) * WORLDMAP_WIDTH) - WORLDMAP_WIDTH;
        }

        int newMapOriginY;
        if (playerLoc.getY() >= 0) {
            newMapOriginY = (playerLoc.getY() / WORLDMAP_HEIGHT) * WORLDMAP_HEIGHT;
        } else {
            newMapOriginY = ((playerLoc.getY() / WORLDMAP_HEIGHT) * WORLDMAP_HEIGHT) - WORLDMAP_HEIGHT;
        }

        return new ServerCoordinate(newMapOriginX, newMapOriginY, newMapLevel);
    }

    /**
     * Set the location of the player. This tells the world map handler what map it needs to draw.
     *
     * @param playerLoc the location of the player
     */
    public void setPlayerLocation(@Nonnull ServerCoordinate playerLoc) {
        @Nonnull ServerCoordinate newOrigin = getOriginLocation(playerLoc);
        if (!Objects.equals(mapOrigin, newOrigin)) {
            changeOrigin(newOrigin);
            worldMap.setMapOrigin(newOrigin);
        }
        worldMap.setPlayerLocation(playerLoc);
    }

    /**
     * Save all maps that are currently load to the hard disk.
     */
    public void saveAllMaps() {
        if (mapOrigin == null) {
            throw new IllegalStateException("Saving the maps is illegal while the map origin is not set.");
        }
        List<ServerCoordinate> origins = getOriginsList(mapOrigin);
        for (@Nonnull ServerCoordinate loc : origins) {
            saveMap(loc);
        }
    }

    /**
     * Save the current map to its file.
     */
    @SuppressWarnings("nls")
    private void saveMap(@Nonnull ServerCoordinate origin) {
        @Nullable ByteBuffer mapData = getMapDataStorage(origin);
        if (mapData == null) {
            return;
        }

        Path mapFile = getMapFilename(origin);
        if (Files.exists(mapFile) && !Files.isWritable(mapFile)) {
            LOGGER.error("The map file is not accessible. Can't write anything.");
            return;
        }

        try (WritableByteChannel outChannel = Channels
                .newChannel(new GZIPOutputStream(Files.newOutputStream(mapFile)))) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (mapData) {
                mapData.rewind();
                int toWrite = mapData.remaining();
                while (toWrite > 0) {
                    toWrite -= outChannel.write(mapData);
                }
            }
        } catch (@Nonnull FileNotFoundException e) {
            LOGGER.error("Target file not found", e);
        } catch (@Nonnull IOException e) {
            LOGGER.error("Error while writing minimap file", e);
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
    private static Path getMapFilename(@Nonnull ServerCoordinate mapOrigin) {
        StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        builder.append("map");
        builder.append(mapOrigin.getX() / WORLDMAP_WIDTH);
        builder.append(mapOrigin.getY() / WORLDMAP_HEIGHT);
        builder.append(mapOrigin.getZ());
        builder.append(".dat");
        return World.getPlayer().getPath().resolve(builder.toString());
    }

    /**
     * This function either creates a strong storage entry of the map data or loads the map data from the file system
     * and create the strong storage after.
     *
     * @param mapOrigin the origin of the map
     */
    private void strengthenOrLoadMap(@Nonnull ServerCoordinate mapOrigin) {
        /* First check if the map is still around. */
        ByteBuffer existingMapData = getMapDataStorage(mapOrigin);
        if (existingMapData != null) {
            strongMapDataStorage.add(existingMapData);
            return;
        }

        @Nonnull ByteBuffer mapData = createMapBuffer();
        Path mapFile = getMapFilename(mapOrigin);

        mapDataStorage.put(mapOrigin, new SoftReference<>(mapData));
        strongMapDataStorage.add(mapData);

        if (!Files.isRegularFile(mapFile)) {
            return;
        }

        try (ReadableByteChannel inChannel = Channels.newChannel(new GZIPInputStream(Files.newInputStream(mapFile)))) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (mapData) {
                mapData.rewind();

                int read = 1;
                while (read > 0) {
                    read = inChannel.read(mapData);
                }
                inChannel.close();
            }

            performFullUpdate();
        } catch (@Nonnull IOException e) {
            LOGGER.error("Failed loading the map data from its file.", e);
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (mapData) {
                mapData.rewind();
                while (mapData.hasRemaining()) {
                    mapData.put((byte) 0);
                }
            }
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
    public void update(@Nonnull TileUpdate updateData) {
        ServerCoordinate tileLoc = updateData.getLocation();

        if (isLocationOnMap(tileLoc)) {
            if (saveTile(tileLoc, updateData.getTileId(), updateData.isBlocked())) {
                worldMap.setTileChanged(tileLoc);
            }
        }
    }

    /**
     * Save the information about a tile within the map data. This will overwrite any existing data about a tile.
     *
     * @param loc the location of the tile
     * @param tileID the ID of tile that is located at the position
     * @param blocked true in case this tile is not passable
     * @return {@code true} in case the new ID of the tile and the already set ID are not equal and the ID did change
     * this way
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    private boolean saveTile(@Nonnull ServerCoordinate loc, int tileID, boolean blocked) {
        int index = encodeLocation(loc);

        ServerCoordinate origin = getOriginLocation(loc);
        ByteBuffer mapData = getMapDataStorage(origin);
        if (mapData == null) {
            return false;
        }

        if (tileID == MapTile.ID_NONE) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
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

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
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
    private int encodeLocation(@Nonnull ServerCoordinate loc) {
        return encodeLocation(loc.getX(), loc.getY());
    }

    @Override
    public void saveShutdown() {
        worldMap.dispose();
    }
}
