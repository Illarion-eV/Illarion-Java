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
package illarion.client.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.log4j.Logger;

import illarion.client.graphics.Tile;
import illarion.client.graphics.TileFactory;
import illarion.client.net.server.TileUpdate;
import illarion.client.util.SessionMember;

import illarion.common.util.Location;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import illarion.graphics.Texture;
import illarion.graphics.TextureAtlas;
import illarion.graphics.common.MapColor;

/**
 * This class stores a reduced version of the full map the character knows. The
 * map data is packed to a minimized and fast readable size that can be stored
 * on the hard disk.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class GameMiniMap implements SessionMember {
    /**
     * The height of the world map in tiles.
     */
    public static final int WORLDMAP_HEIGHT = 1024;

    /**
     * The width of the world map in tiles.
     */
    public static final int WORLDMAP_WIDTH = 1024;

    /**
     * The modifier value that is multiplied to the color values in case the
     * tile is blocked.
     */
    private static final float BLOCKED_COLOR_MOD = 0.7f;

    /**
     * The string builder that is used to create the strings for this class.
     */
    private static final StringBuilder BUILDER = new StringBuilder();

    /**
     * The bytes that are reserved for one tile in the internal storage.
     */
    private static final int BYTES_PER_TILE = 2;

    /**
     * The log file handler that takes care for the logging output of this
     * class.
     */
    private static final Logger LOGGER = Logger.getLogger(GameMiniMap.class);

    /**
     * The minimal height and width of the texture area that is updated during a
     * full texture update.
     */
    private static final int MINIMAL_UPDATE_AREA = 4;

    /**
     * Indicated how many bits the blocked bit is shifted.
     */
    private static final int SHIFT_BLOCKED = 10;

    /**
     * Indicated how many bit the value of the overlay tile is shifted.
     */
    private static final int SHIFT_OVERLAY = 5;

    /**
     * The texture name for the minimap texture.
     */
    @SuppressWarnings("nls")
    private static final String TEX_NAME_MINI = "minimap";

    /**
     * The texture name of the worldmap texture.
     */
    @SuppressWarnings("nls")
    private static final String TEX_NAME_WORLD = "worldmap";

    /**
     * The AND mask to get the tile ID from a encoded tile.
     */
    private static final int TILE_ID_MASK = 0x1f;

    /**
     * The AND mask to get a overlay ID from a encoded tile.
     */
    private static final int TILE_OVERLAY_MASK = 0x1f;

    /**
     * The texture atlas that handles the texture image itself.
     */
    private final TextureAtlas atlas;

    /**
     * The dirty flag, this will be true in case there are any changes on the
     * map that were yet not transfered to the world map texture.
     */
    private boolean dirty = false;

    /**
     * The X coordinate of the first point that describes the dirty rectangle of
     * the map.
     */
    private int dirtyAreaX1;

    /**
     * The X coordinate of the second point that describes the dirty rectangle
     * of the map.
     */
    private int dirtyAreaX2;

    /**
     * The Y coordinate of the first point that describes the dirty rectangle of
     * the map.
     */
    private int dirtyAreaY1;

    /**
     * The Y coordinate of the second point that describes the dirty rectangle
     * of the map.
     */
    private int dirtyAreaY2;

    /**
     * Indicates if a map is loaded or not.
     */
    private boolean loadedMap;

    /**
     * This variable stores if the map is currently loaded.
     */
    private volatile boolean loadingMap = false;

    /**
     * The data storage for the map data that was loaded in this mini map.
     */
    private final ByteBuffer mapData;

    /**
     * The level of the current world map.
     */
    private int mapLevel;

    /**
     * The origin x coordinate of this world map.
     */
    private int mapOriginX;

    /**
     * The origin y coordinate of this world map.
     */
    private int mapOriginY;

    /**
     * The sprite that is used the draw the mini map.
     */
    private final Sprite minimap;

    /**
     * The height of the overview map in tiles.
     */
    private int minimapHeight = 256;

    /**
     * The texture that is used to draw the mini map sprite.
     */
    private final Texture minimapTexture;

    /**
     * The width of the overview map in tiles.
     */
    private int minimapWidth = 256;

    /**
     * The area position and sizes for the updates.
     */
    private final LinkedList<int[]> updateAreas = new LinkedList<int[]>();

    /**
     * The byte buffers that stored the required data for the minimap updates.
     */
    private final LinkedList<ByteBuffer> updateBuffers =
        new LinkedList<ByteBuffer>();

    /**
     * This variable is used to reduce the amount of minimap updates triggered.
     */
    private int updateSlowdown = 0;

    /**
     * The sprite that is used the draw the world map.
     */
    private final Sprite worldmap;

    /**
     * The texture that is used to draw the world map sprite.
     */
    private final Texture worldmapTexture;

    /**
     * Constructor of the game map that sets up all instance variables.
     */
    public GameMiniMap() {
        mapData =
            ByteBuffer.allocate(WORLDMAP_WIDTH * WORLDMAP_HEIGHT
                * BYTES_PER_TILE);
        mapData.order(ByteOrder.nativeOrder());
        loadedMap = false;

        atlas = Graphics.getInstance().getTextureAtlas();
        atlas.setDimensions(WORLDMAP_WIDTH, WORLDMAP_HEIGHT);

        atlas.addImage(TEX_NAME_WORLD, 0, 0, WORLDMAP_WIDTH, WORLDMAP_HEIGHT);
        atlas.addImage(TEX_NAME_MINI, 0, 0, minimapWidth, minimapHeight);

        worldmapTexture = atlas.getTexture(TEX_NAME_WORLD);
        minimapTexture = atlas.getTexture(TEX_NAME_MINI);

        worldmap = Graphics.getInstance().getSprite(1);
        worldmap.addTexture(worldmapTexture);

        minimap = Graphics.getInstance().getSprite(1);
        minimap.addTexture(minimapTexture);
    }

    @Override
    public void endSession() {
        finishUpdate();
        saveMap();

        updateAreas.clear();
        updateBuffers.clear();

        dirty = false;
        dirtyAreaX1 = 0;
        dirtyAreaX2 = 0;
        dirtyAreaY1 = 0;
        dirtyAreaY2 = 0;

        mapLevel = 0;
        mapOriginX = 0;
        mapOriginY = 0;

        minimapHeight = 256;
        minimapWidth = 256;

        loadedMap = false;
        loadingMap = false;

        updateSlowdown = 0;

        mapData.clear();
        final byte nullByte = (byte) 0;
        while (mapData.hasRemaining()) {
            mapData.put(nullByte);
        }
        mapData.clear();
    }

    /**
     * This update prepares a minimap update and stores the required data to the
     * buffer to the render thread is able to update the minimap according to
     * this update.
     */
    public void finishUpdate() {
        if (!dirty || !loadedMap || loadingMap) {
            return;
        }

        updateSlowdown = ++updateSlowdown % 2;
        if (updateSlowdown != 0) {
            return;
        }

        if ((dirtyAreaX1 == mapOriginX) && (dirtyAreaY1 == mapOriginY)
            && (dirtyAreaX2 == ((mapOriginX + WORLDMAP_WIDTH) - 1))
            && (dirtyAreaY2 == ((mapOriginY + WORLDMAP_HEIGHT) - 1))) {
            prepareUpdateFullImage();
        } else {
            prepareUpdateAreaImage(dirtyAreaX1, dirtyAreaY1,
                (dirtyAreaX2 - dirtyAreaX1) + 1,
                (dirtyAreaY2 - dirtyAreaY1) + 1);
        }

        dirty = false;
    }

    /**
     * Get the level of the mini/world-map that is currently displayed. This
     * equals the server Z coordinate of the current player location.
     * 
     * @return the level of the maps
     */
    public int getMapLevel() {
        return mapLevel;
    }

    /**
     * Get the entire origin of the current world map. The origin is stored in a
     * {@link Location} class instance that is newly fetched from the buffer. In
     * case its not used anymore it should be put back into the buffer.
     * <p>
     * The server X and Y coordinate are the coordinates of the current origin
     * of the world map. The Z coordinate is the current level.
     * </p>
     * <p>
     * For details on each coordinate see the functions that request the single
     * coordinates.
     * </p>
     * 
     * @return the location of the overview map origin
     * @see #getMapLevel()
     * @see #getMapOriginX()
     * @see #getMapOriginY()
     */
    public Location getMapOrigin() {
        return getMapOrigin(Location.getInstance());
    }

    /**
     * This function is similar to {@link #getMapOrigin()}. The only difference
     * is that this function does not fetch a new instance of the location
     * class, it rather uses the instance set as argument and fills it with the
     * data.
     * 
     * @param loc the location instance that is filled with the origin
     *            informations
     * @return the same instance of the {@link Location} class that was set as
     *         parameter
     * @throws NullPointerException in case the parameter <code>loc</code> is
     *             set to <code>null</code>
     * @see #getMapOrigin()
     */
    public Location getMapOrigin(final Location loc) {
        loc.setSC(mapOriginX, mapOriginY, mapLevel);
        return loc;
    }

    /**
     * Get the X coordinate of the origin of the current world map. This
     * coordinate depends on the area the player is currently at.
     * <p>
     * This coordinate is calculated the following way:<br />
     * <code>originX = floor(playerX / {@link #WORLDMAP_WIDTH}) * {@link #WORLDMAP_WIDTH}</code>
     * </p>
     * 
     * @return the X coordinate of the map origin
     */
    public int getMapOriginX() {
        return mapOriginX;
    }

    /**
     * Get the Y coordinate of the origin of the current world map. This
     * coordinate depends on the area the player is currently at.
     * <p>
     * This coordinate is calculated the following way:<br />
     * <code>originY = floor(playerY / {@link #WORLDMAP_HEIGHT}) * {@link #WORLDMAP_HEIGHT}</code>
     * </p>
     * 
     * @return the Y coordinate of the map origin
     */
    public int getMapOriginY() {
        return mapOriginY;
    }

    /**
     * Get the sprite that is used to draw the mini map.
     * 
     * @return the sprite that holds the mini map
     */
    public Sprite getMinimap() {
        return minimap;
    }

    /**
     * Get the zoom value of the minimap.
     * 
     * @return the value of the zoom on the minimap
     */
    public int getMinimapZoom() {
        return minimapHeight;
    }

    /**
     * Get the sprite that is used to draw the world map.
     * 
     * @return the sprite that holds the world map
     */
    public Sprite getWorldmap() {
        return worldmap;
    }

    @Override
    public void initSession() {
        atlas.finish();
    }

    /**
     * The render function should be triggered at every render run of the mini
     * map or the world map. In case the map is dirty this will trigger a update
     * of the whole map.
     */
    public void render() {
        if (!loadedMap || loadingMap) {
            return;
        }

        performUpdate();
    }

    /**
     * Save the current map to its file.
     */
    @SuppressWarnings("nls")
    public void saveMap() {
        while (loadingMap) {
            try {
                Thread.sleep(1);
            } catch (final InterruptedException e) {
                // nothing
            }
        }
        if (!loadedMap) {
            return;
        }
        final File mapFile = getCurrentMapFilename();
        if (mapFile.exists() && !mapFile.canWrite()) {
            LOGGER.error("mapfile File locked, can't write the"
                + " name table.");
            return;
        }
        WritableByteChannel outChannel = null;
        final Deflater def = new Deflater();
        def.setLevel(Deflater.BEST_COMPRESSION);
        def.setStrategy(Deflater.DEFAULT_STRATEGY);
        try {
            final FileOutputStream outStream = new FileOutputStream(mapFile);
            final DeflaterOutputStream dOutStream =
                new DeflaterOutputStream(outStream, def);
            outChannel = Channels.newChannel(dOutStream);
            synchronized (mapData) {
                mapData.rewind();
                int toWrite = mapData.remaining();
                while (toWrite > 0) {
                    toWrite -= outChannel.write(mapData);
                }
            }
        } catch (final FileNotFoundException e) {
            LOGGER.error("Target file not found", e);
        } catch (final IOException e) {
            LOGGER.error("Error while writing minimap file", e);
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (final IOException e) {
                    LOGGER.error("Failed closing the file stream.");
                }
            }
        }
        def.end();
    }

    /**
     * Set the zoom value of the minimap.
     * 
     * @param zoom the new zoom value
     */
    public void setMinimapZoom(final int zoom) {
        if ((zoom > 90) && (zoom < 256) && (zoom != minimapHeight)) {
            minimapHeight = zoom;
            minimapWidth = zoom;
            setPlayerLocation(Game.getPlayer().getLocation());
        }
    }

    /**
     * Set the location of the player. This tells the world map handler what map
     * it needs to draw.
     * 
     * @param playerLoc the location of the player
     */
    public void setPlayerLocation(final Location playerLoc) {
        final int newMapLevel = playerLoc.getScZ();

        int newMapOriginX = 0;
        if (playerLoc.getScX() >= 0) {
            newMapOriginX =
                (playerLoc.getScX() / WORLDMAP_WIDTH) * WORLDMAP_WIDTH;
        } else {
            newMapOriginX =
                ((playerLoc.getScX() / WORLDMAP_WIDTH) * WORLDMAP_WIDTH)
                    - WORLDMAP_WIDTH;
        }

        int newMapOriginY = 0;
        if (playerLoc.getScY() >= 0) {
            newMapOriginY =
                (playerLoc.getScY() / WORLDMAP_HEIGHT) * WORLDMAP_HEIGHT;
        } else {
            newMapOriginY =
                ((playerLoc.getScY() / WORLDMAP_HEIGHT) * WORLDMAP_HEIGHT)
                    - WORLDMAP_HEIGHT;
        }

        if (!loadedMap || (newMapLevel != mapLevel)
            || (newMapOriginX != mapOriginX) || (newMapOriginY != mapOriginY)) {
            saveMap();
            mapLevel = newMapLevel;
            mapOriginX = newMapOriginX;
            mapOriginY = newMapOriginY;
            loadMap();
        }

        int minimapOffsetX = 0;
        int minimapOffsetY = 0;
        int minimapOriginX =
            playerLoc.getScX() - mapOriginX - (minimapWidth >> 1);
        int minimapOriginY =
            playerLoc.getScY() - mapOriginY - (minimapHeight >> 1);

        if (minimapOriginX < 0) {
            minimapOffsetX = -minimapOriginX;
            minimapOriginX = 0;
        } else if ((minimapOriginX + minimapWidth) >= WORLDMAP_WIDTH) {
            minimapOffsetX =
                WORLDMAP_WIDTH - minimapOriginX - minimapWidth - 1;
            minimapOriginX = WORLDMAP_WIDTH - minimapWidth - 1;
        }
        if (minimapOriginY < 0) {
            minimapOffsetY = minimapOriginY;
            minimapOriginY = 0;
        } else if ((minimapOriginY + minimapHeight) >= WORLDMAP_HEIGHT) {
            minimapOffsetY =
                -(WORLDMAP_HEIGHT - minimapOriginY - minimapHeight - 1);
            minimapOriginY = WORLDMAP_HEIGHT - minimapHeight - 1;
        }

        minimapTexture.setImageLocation(minimapOriginX, minimapOriginY);
        minimap.setOffset(minimapOffsetX, minimapOffsetY);
    }

    /**
     * Clean up the resources used by this minimap.
     */
    @Override
    public void shutdownSession() {
        minimap.remove();
        worldmap.remove();
    }

    @Override
    public void startSession() {
        loadMap();
    }

    /**
     * Update one tile of the overview map.
     * 
     * @param updateData the data that is needed for the update
     */
    public void update(final TileUpdate updateData) {
        final Location tileLoc = updateData.getLocation();

        if ((tileLoc.getScX() < mapOriginX)
            || (tileLoc.getScX() >= (mapOriginX + WORLDMAP_WIDTH))
            || (tileLoc.getScY() < mapOriginY)
            || (tileLoc.getScY() >= (mapOriginY + WORLDMAP_HEIGHT))
            || (tileLoc.getScZ() != mapLevel)) {
            return;
        }

        if (saveTile(tileLoc, updateData.getTileId(), updateData.isBlocked())) {
            if (!dirty) {
                dirtyAreaX1 = tileLoc.getScX();
                dirtyAreaY1 = tileLoc.getScY();
                dirtyAreaX2 = dirtyAreaX1;
                dirtyAreaY2 = dirtyAreaY1;
                dirty = true;
            } else {
                dirtyAreaX1 = Math.min(dirtyAreaX1, tileLoc.getScX());
                dirtyAreaY1 = Math.min(dirtyAreaY1, tileLoc.getScY());
                dirtyAreaX2 = Math.max(dirtyAreaX2, tileLoc.getScX());
                dirtyAreaY2 = Math.max(dirtyAreaY2, tileLoc.getScY());
            }
        }
    }

    /**
     * Increase the zoom on the minimap. This will increase the size of the
     * pixels on the minimap until a maximal value is reached.
     */
    public void zoomMinimapIn() {
        if (minimapHeight > 90) {
            minimapHeight--;
            minimapWidth--;
            setPlayerLocation(Game.getPlayer().getLocation());
        }
    }

    /**
     * Decrease the zoom on the minimap. This will decrease the size of the
     * pixels on the minimap until a minimal value is reached.
     */
    public void zoomMinimapOut() {
        if (minimapHeight < 256) {
            minimapHeight++;
            minimapWidth++;
            setPlayerLocation(Game.getPlayer().getLocation());
        }
    }

    /**
     * Load a map from its file. Any other formerly loaded map is discarded when
     * this happens.
     */
    @SuppressWarnings("nls")
    protected void loadMap() {
        loadingMap = true;
        final File mapFile = getCurrentMapFilename();

        if (!mapFile.exists()) {
            loadEmptyMap();
            return;
        }

        FileInputStream inStream = null;
        InflaterInputStream dInStream = null;
        final Inflater inf = new Inflater();
        try {
            inStream = new FileInputStream(mapFile);
            dInStream = new InflaterInputStream(inStream, inf);
            final ReadableByteChannel inChannel =
                Channels.newChannel(dInStream);

            synchronized (mapData) {
                mapData.rewind();

                int read = 1;
                while (read > 0) {
                    read = inChannel.read(mapData);
                }
                inChannel.close();
            }

            dirty = true;
            dirtyAreaX1 = mapOriginX;
            dirtyAreaY1 = mapOriginY;
            dirtyAreaX2 = (mapOriginX + WORLDMAP_WIDTH) - 1;
            dirtyAreaY2 = (mapOriginY + WORLDMAP_HEIGHT) - 1;
        } catch (final IOException e) {
            LOGGER.error("Failed loading the map data from its file.", e);
            loadEmptyMap();
        } finally {
            if (dInStream != null) {
                try {
                    dInStream.close();
                } catch (final IOException e) {
                    LOGGER.error("Failed closing the file stream.");
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (final IOException e) {
                    LOGGER.error("Failed closing the file stream.");
                }
            }
        }
        inf.end();
        loadedMap = true;
        loadingMap = false;
    }

    /**
     * Decode a tile to a image data array.
     * 
     * @param tileData the encoded informations about the tile
     * @param imageData the byte array that stores the image data
     */
    private void decodeTile(final short tileData, final ByteBuffer imageData) {
        final byte tileID = (byte) (tileData & TILE_ID_MASK);
        if (tileID == 0) {
            imageData.put((byte) 0).put((byte) 0).put((byte) 0).put((byte) 0);
            return;
        }
        final byte tileOverlayID =
            (byte) ((tileData >> SHIFT_OVERLAY) & TILE_OVERLAY_MASK);
        final boolean tileBlocked = ((tileData >> SHIFT_BLOCKED) > 0);

        final int[] tileColor =
            MapColor.getColor(TileFactory.getInstance().getMapColor(tileID));

        final int[] colorValue = new int[MapColor.COLOR_VALUES];
        colorValue[0] = tileColor[0];
        colorValue[1] = tileColor[1];
        colorValue[2] = tileColor[2];

        if (tileOverlayID > 0) {
            final int[] overlayTileColor =
                MapColor.getColor(TileFactory.getInstance().getMapColor(
                    tileOverlayID));

            colorValue[0] += (overlayTileColor[0] - tileColor[0]) >> 1;
            colorValue[1] += (overlayTileColor[1] - tileColor[1]) >> 1;
            colorValue[2] += (overlayTileColor[2] - tileColor[2]) >> 1;
        }
        if (tileBlocked) {
            colorValue[0] *= BLOCKED_COLOR_MOD;
            colorValue[1] *= BLOCKED_COLOR_MOD;
            colorValue[2] *= BLOCKED_COLOR_MOD;
        }
        imageData.put((byte) (colorValue[0] & 0xFF));
        imageData.put((byte) (colorValue[1] & 0xFF));
        imageData.put((byte) (colorValue[2] & 0xFF));
        imageData.put((byte) -1);
    }

    /**
     * Encode a server location to the index in the map data buffer.
     * 
     * @param loc the server location that shall be encoded
     * @return the index of the location in the map data buffer
     */
    private int encodeLocation(final Location loc) {
        return ((loc.getScY() - mapOriginY) * WORLDMAP_WIDTH * BYTES_PER_TILE)
            + ((loc.getScX() - mapOriginX) * BYTES_PER_TILE);
    }

    /**
     * Get the full path string to the file for the currently selected map. This
     * file needs to be used to store and load the map data.
     * 
     * @return the path and the filename of the map file
     */
    @SuppressWarnings("nls")
    private File getCurrentMapFilename() {
        BUILDER.setLength(0);
        BUILDER.append("map");
        BUILDER.append(mapOriginX / WORLDMAP_WIDTH);
        BUILDER.append(mapOriginY / WORLDMAP_HEIGHT);
        BUILDER.append(mapLevel);
        BUILDER.append(".dat");
        return new File(Game.getPlayer().getPath(), BUILDER.toString());
    }

    /**
     * Load a empty map in case it was not possible to load it from a file. This
     * creates a full new map and ensures that there are no remaining from the
     * last loaded map in.
     */
    private void loadEmptyMap() {
        loadingMap = true;
        mapData.rewind();
        while (mapData.remaining() > 0) {
            mapData.put((byte) 0);
        }
        loadedMap = true;
        prepareUpdateFullImage();
        loadingMap = false;
    }

    /**
     * In case there is a update of the map prepared, put it into place. This
     * will always write one update to the minimap per call. In case there is
     * more then one update, the function has to be called multiple times.
     */
    @SuppressWarnings("nls")
    private void performUpdate() {
        if (updateAreas.isEmpty() || updateBuffers.isEmpty()) {
            return;
        }

        final int[] updateSize = updateAreas.removeFirst();
        final ByteBuffer updateBuffer = updateBuffers.removeFirst();

        if (atlas.getTextureID() != 0) {
            final long beforeTime = System.currentTimeMillis();
            atlas.updateTextureArea(updateSize[0], updateSize[1],
                updateSize[2], updateSize[3], updateBuffer);
            LOGGER.info("Changing Minimap Texture took "
                + Long.toString(System.currentTimeMillis() - beforeTime)
                + "ms");
        } else if ((updateSize[0] == 0) && (updateSize[1] == 0)
            && (updateSize[2] == WORLDMAP_WIDTH)
            && (updateSize[3] == WORLDMAP_HEIGHT)) {
            atlas.setTextureImage(updateBuffer);
            atlas.activateTexture(true, true);

            worldmapTexture.setParent(atlas);
            minimapTexture.setParent(atlas);
        }
    }

    /**
     * Prepare a update of a given area of the map. This encodes all required
     * data to a buffer and stores it so the render thread can copy the data to
     * the minimap texture.
     * 
     * @param x the x coordinate of the area to update
     * @param y the y coordinate of the area to update
     * @param w the width of the area to update
     * @param h the height of the area to update
     */
    private void prepareUpdateAreaImage(final int x, final int y, final int w,
        final int h) {

        // the origin needs to be a multiple of 4
        final int originX = (x - mapOriginX) - ((x - mapOriginX) % 4);
        final int originY = (y - mapOriginY) - ((y - mapOriginY) % 4);

        final int neededWidth = w + ((x - mapOriginX) % 4);
        final int neededHeight = h + ((y - mapOriginY) % 4);

        int updateAreaWidth = 1;
        while ((updateAreaWidth < neededWidth)
            || (updateAreaWidth < MINIMAL_UPDATE_AREA)) {
            updateAreaWidth <<= 1;
        }

        int updateAreaHeight = 1;
        while ((updateAreaHeight < neededHeight)
            || (updateAreaHeight < MINIMAL_UPDATE_AREA)) {
            updateAreaHeight <<= 1;
        }

        int updateAreaX = originX;
        if ((updateAreaX + updateAreaWidth) >= WORLDMAP_WIDTH) {
            updateAreaX = WORLDMAP_WIDTH - updateAreaWidth;
        }

        int updateAreaY = originY;
        if ((updateAreaY + updateAreaHeight) >= WORLDMAP_HEIGHT) {
            updateAreaY = WORLDMAP_HEIGHT - updateAreaHeight;
        }

        final ByteBuffer buffer =
            ByteBuffer.allocateDirect(updateAreaWidth * updateAreaHeight * 4);

        synchronized (mapData) {
            mapData.rewind();
            final int limitY = updateAreaY + updateAreaHeight;
            final int limitX = updateAreaX + updateAreaWidth;
            for (int currY = updateAreaY; currY < limitY; ++currY) {
                mapData.position((currY * WORLDMAP_WIDTH * BYTES_PER_TILE)
                    + (updateAreaX * BYTES_PER_TILE));
                for (int currX = updateAreaX; currX < limitX; ++currX) {
                    decodeTile(mapData.getShort(), buffer);
                }
            }
        }

        buffer.flip();

        updateBuffers.addLast(buffer);
        updateAreas.addLast(new int[] { updateAreaX, updateAreaY,
            updateAreaWidth, updateAreaHeight });
    }

    /**
     * Update the whole image of the world map.
     */
    @SuppressWarnings("nls")
    private void prepareUpdateFullImage() {
        LOGGER.info("Full Minimap Update triggered");

        final ByteBuffer buffer =
            ByteBuffer.allocateDirect(WORLDMAP_WIDTH * WORLDMAP_HEIGHT * 4);

        synchronized (mapData) {
            mapData.rewind();

            try {
                while (mapData.remaining() > 0) {
                    decodeTile(mapData.getShort(), buffer);
                }
            } catch (final ArrayIndexOutOfBoundsException ex1) {
                LOGGER.error(
                    "Map data corrupted, discard map and load blank map", ex1);
                loadEmptyMap();
            }
        }

        buffer.flip();

        updateBuffers.addLast(buffer);
        updateAreas
            .addLast(new int[] { 0, 0, WORLDMAP_WIDTH, WORLDMAP_HEIGHT });
    }

    /**
     * Save the informations about a tile within the map data. This will
     * overwrite any existing data about a tile.
     * 
     * @param loc the location of the tile
     * @param tileID the ID of tile that is located at the position
     * @param blocked true in case this tile is not passable
     * @return <code>true</code> in case the new ID of the tile and the already
     *         set ID are not equal and the ID did change this way
     */
    private boolean saveTile(final Location loc, final int tileID,
        final boolean blocked) {

        final int index = encodeLocation(loc);

        if (tileID == MapTile.ID_NONE) {
            if (mapData.getShort(index) == 0) {
                return false;
            }
            mapData.putShort(index, (short) 0);
            return true;
        }

        short encodedTileValue = (short) Tile.baseID(tileID);
        encodedTileValue += Tile.overlayID(tileID) << SHIFT_OVERLAY;
        if (blocked) {
            encodedTileValue += 1 << SHIFT_BLOCKED;
        }

        if (mapData.getShort(index) == encodedTileValue) {
            return false;
        }
        mapData.putShort(index, encodedTileValue);
        return true;
    }
}
