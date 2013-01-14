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

import de.lessvoid.nifty.slick2d.render.SlickRenderUtils;
import de.lessvoid.nifty.slick2d.render.image.SlickRenderImage;
import illarion.client.graphics.Tile;
import illarion.client.graphics.shader.MiniMapShader;
import illarion.client.graphics.shader.Shader;
import illarion.client.graphics.shader.ShaderManager;
import illarion.client.gui.events.HideMiniMap;
import illarion.client.net.server.TileUpdate;
import illarion.client.resources.TileFactory;
import illarion.common.graphics.MapColor;
import illarion.common.types.Location;
import illarion.common.types.Rectangle;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class stores a reduced version of the full map the character knows. The map data is packed to a minimized and
 * fast readable size that can be stored on the hard disk.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public final class GameMiniMap {
    /**
     * This class is used to render the minimap to a GUI element. This is a special implementation of the render
     * image for the Nifty-GUI as the image needs to move around as the player moves.
     */
    @NotThreadSafe
    private final class GameMiniMapRenderImage implements SlickRenderImage {
        /**
         * This instance of a slick color is used to avoid the need to create a new color instance every time this
         * image is rendered.
         */
        @Nonnull
        private final Color slickColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);

        /**
         * The shader required to render the map.
         */
        @Nullable
        private MiniMapShader miniMapShader;

        @Override
        public void dispose() {
            // nothing
        }

        @Override
        public int getHeight() {
            return MINI_MAP_HEIGHT;
        }

        @Override
        public int getWidth() {
            return MINI_MAP_WIDTH;
        }

        @Override
        public void renderImage(@Nonnull final Graphics g, final int x, final int y, final int width, final int height,
                                @Nonnull final de.lessvoid.nifty.tools.Color color, final float scale) {
            renderImage(g, x, y, width, height, 0, 0, MINI_MAP_WIDTH, MINI_MAP_HEIGHT, color, scale, MINI_MAP_WIDTH / 2,
                    MINI_MAP_HEIGHT / 2);
        }

        @Override
        public void renderImage(@Nonnull final Graphics g, final int x, final int y, final int w, final int h,
                                final int srcX, final int srcY, final int srcW, final int srcH,
                                @Nonnull final de.lessvoid.nifty.tools.Color color, final float scale,
                                final int centerX, final int centerY) {
            if (disabled || (worldMapTexture == null)) {
                return;
            }

            if (miniMapShader == null) {
                miniMapShader = ShaderManager.getShader(Shader.MiniMap, MiniMapShader.class);
            }

            g.pushTransform();
            g.translate(centerX, centerY);
            g.scale(scale, scale);
            g.rotate(0, 0, -45.f);
            g.translate(-centerX, -centerY);

            miniMapShader.bind();
            miniMapShader.setTexture(0);

            final float miniMapCenterX = (miniMapOriginX + (MINI_MAP_WIDTH / 2.f)) / WORLDMAP_WIDTH;
            final float miniMapCenterY = (miniMapOriginY + (MINI_MAP_HEIGHT / 2.f)) / WORLDMAP_HEIGHT;
            miniMapShader.setCenter(miniMapCenterX, miniMapCenterY);
            miniMapShader.setRadius((float) MINI_MAP_HEIGHT / 2.f / (float) WORLDMAP_HEIGHT);
            miniMapShader.setMarkerSize(2.f / (float) WORLDMAP_HEIGHT);

            g.drawImage(worldMapTexture, x, y, x + w, y + h, srcX + miniMapOriginX, srcY + miniMapOriginY,
                    srcX + miniMapOriginX + srcW, srcY + miniMapOriginY + srcH,
                    SlickRenderUtils.convertColorNiftySlick(color, slickColor));

            miniMapShader.unbind();

            g.popTransform();
        }
    }

    /**
     * The height of the world map in tiles.
     */
    public static final int WORLDMAP_HEIGHT = 1024;

    /**
     * The width of the world map in tiles.
     */
    public static final int WORLDMAP_WIDTH = 1024;

    /**
     * The modifier value that is multiplied to the color values in case the tile is blocked.
     */
    private static final float BLOCKED_COLOR_MOD = 0.7f;

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
     * The AND mask to get the tile ID from a encoded tile.
     */
    private static final int TILE_ID_MASK = 0x1f;

    /**
     * The AND mask to get a overlay ID from a encoded tile.
     */
    private static final int TILE_OVERLAY_MASK = 0x1f;

    /**
     * The height of the overview map in tiles.
     */
    private static final int MINI_MAP_HEIGHT = 162;

    /**
     * The width of the overview map in tiles.
     */
    private static final int MINI_MAP_WIDTH = 162;

    /**
     * The maximal size of a area that is updated at once.
     */
    private static final int MAX_UPDATE_AREA_SIZE = 64;

    /**
     * This flag is {@code true} while not map is loaded.
     */
    private boolean noMapLoaded;

    /**
     * This variable stores if the map is currently loaded.
     */
    private boolean loadingMap;

    /**
     * The data storage for the map data that was loaded in this mini map.
     */
    @Nonnull
    @GuardedBy("mapData")
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
     * The areas that require a update.
     */
    @Nonnull
    private final List<Rectangle> updateAreas;

    /**
     * The texture that is used to draw the world map sprite.
     */
    @Nullable
    private Image worldMapTexture;

    /**
     * This flag is turned {@code true} in case rendering the mini map is disabled.
     */
    private boolean disabled;

    /**
     * This value is set {@code true} in case areas inside the mini map are not rendered.
     */
    private boolean miniMapDirty;

    /**
     * The x coordinate of the origin location of the mini map.
     */
    private int miniMapOriginX;

    /**
     * The y coordinate of the origin location of the mini map.
     */
    private int miniMapOriginY;

    /**
     * Constructor of the game map that sets up all instance variables.
     */
    public GameMiniMap() {
        mapData = ByteBuffer.allocate(WORLDMAP_WIDTH * WORLDMAP_HEIGHT * BYTES_PER_TILE);
        mapData.order(ByteOrder.nativeOrder());
        noMapLoaded = true;

        updateAreas = new ArrayList<Rectangle>();
        disabled = false;
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
     * @throws NullPointerException in case the parameter <code>loc</code> is set to <code>null</code>
     * @see #getMapOrigin()
     */
    @Nonnull
    public Location getMapOrigin(@Nonnull final Location loc) {
        loc.setSC(mapOriginX, mapOriginY, mapLevel);
        return loc;
    }

    /**
     * Get the render image used to display the mini map.
     *
     * @return the render image used to display the mini map on the GUI
     */
    @Nonnull
    public SlickRenderImage getMiniMap() {
        return new GameMiniMapRenderImage();
    }

    /**
     * Get the level of the mini/world-map that is currently displayed. This equals the server Z coordinate of the
     * current player location.
     *
     * @return the level of the maps
     */
    public int getMapLevel() {
        return mapLevel;
    }

    /**
     * Get the X coordinate of the origin of the current world map. This coordinate depends on the area the player is
     * currently at. <p> This coordinate is calculated the following way:<br /> <code>originX = floor(playerX / {@link
     * #WORLDMAP_WIDTH}) * {@link #WORLDMAP_WIDTH}</code> </p>
     *
     * @return the X coordinate of the map origin
     */
    public int getMapOriginX() {
        return mapOriginX;
    }

    /**
     * Get the Y coordinate of the origin of the current world map. This coordinate depends on the area the player is
     * currently at. <p> This coordinate is calculated the following way:<br /> <code>originY = floor(playerY / {@link
     * #WORLDMAP_HEIGHT}) * {@link #WORLDMAP_HEIGHT}</code> </p>
     *
     * @return the Y coordinate of the map origin
     */
    public int getMapOriginY() {
        return mapOriginY;
    }

    /**
     * The render function should be triggered at every render run of the mini map or the world map. In case the map is
     * dirty this will trigger a update of the whole map.
     */
    public void render() {
        if (disabled) {
            return;
        }

        if (worldMapTexture == null) {
            try {
                worldMapTexture = Image.createOffscreenImage(WORLDMAP_WIDTH, WORLDMAP_HEIGHT);
                final Graphics g = worldMapTexture.getGraphics();
                g.setColor(Color.black);
                g.fillRect(0, 0, WORLDMAP_WIDTH, WORLDMAP_HEIGHT);
            } catch (final SlickException e) {
                LOGGER.error("Failed to create minimap texture.", e);
                disableMiniMap();
                return;
            }
        }
        if (noMapLoaded || loadingMap) {
            return;
        }

        try {
            drawMap();
        } catch (SlickException e) {
            LOGGER.error("Drawing the minimap failed!", e);
        }
    }

    /**
     * Deactivate the mini map.
     */
    private void disableMiniMap() {
        disabled = true;
        EventBus.publish(new HideMiniMap());
    }

    /**
     * Render the dirty areas on the map.
     *
     * @throws SlickException in case the rendering fails
     */
    private void drawMap() throws SlickException {
        if (updateAreas.isEmpty()) {
            return;
        }

        final int allowedArea = MAX_UPDATE_AREA_SIZE * MAX_UPDATE_AREA_SIZE;
        int processedArea = 0;

        if (miniMapDirty) {
            final Rectangle miniMapRect = new Rectangle(miniMapOriginX + mapOriginX, miniMapOriginY + mapOriginY,
                    MINI_MAP_WIDTH, MINI_MAP_HEIGHT);

            for (int i = 0; i < updateAreas.size(); i++) {
                final Rectangle testRect = updateAreas.get(i);
                if (testRect == null) {
                    continue;
                }
                if (testRect.intersects(miniMapRect)) {
                    final int currentArea = testRect.getArea();
                    drawMapArea(updateAreas.remove(i));

                    processedArea += currentArea;
                    if (processedArea >= allowedArea) {
                        return;
                    }
                    //noinspection AssignmentToForLoopParameter
                    --i;
                }
            }
        }

        miniMapDirty = false;

        if ((processedArea < allowedArea) && !updateAreas.isEmpty()) {
            final Rectangle currentArea = updateAreas.remove(0);
            if (currentArea == null) {
                return;
            }
            processedArea += currentArea.getArea();
            drawMapArea(currentArea);
        }
    }

    private void drawMapArea(@Nonnull final Rectangle rectangle) throws SlickException {
        if (worldMapTexture == null) {
            return;
        }

        final Graphics graphics = worldMapTexture.getGraphics();
        graphics.setColor(Color.black);
        graphics.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());

        synchronized (mapData) {
            for (int x = rectangle.getLeft(); x < rectangle.getRight(); x++) {
                for (int y = rectangle.getBottom(); y < rectangle.getTop(); y++) {
                    final int location = encodeLocation(x, y);

                    final short tileData = mapData.getShort(location);

                    if (tileData == 0) {
                        continue;
                    }

                    graphics.pushTransform();
                    graphics.translate(x - mapOriginX, y - mapOriginY);
                    drawTile(tileData, graphics);
                    graphics.popTransform();
                }
            }
        }
    }

    /**
     * Encode a server location to the index in the map data buffer.
     *
     * @param x the x coordinate of the location on the map
     * @param y the y coordinate of the location on the map
     * @return the index of the location in the map data buffer
     */
    private int encodeLocation(final int x, final int y) {
        if ((y < mapOriginY) || (y >= (mapOriginY + WORLDMAP_HEIGHT))) {
            throw new IllegalArgumentException("y out of range");
        }
        if ((x < mapOriginX) || (x >= (mapOriginX + WORLDMAP_WIDTH))) {
            throw new IllegalArgumentException("x out of range");
        }
        return ((y - mapOriginY) * WORLDMAP_WIDTH * BYTES_PER_TILE) + ((x - mapOriginX) * BYTES_PER_TILE);
    }

    /**
     * Draw a single tile on the mini map.
     *
     * @param tileData the data of the tile
     * @param graphics the graphics instance used to draw
     */
    private static void drawTile(final short tileData, @Nonnull final Graphics graphics) {
        final byte tileID = (byte) (tileData & TILE_ID_MASK);
        final Color drawColor;
        if (tileID == 0) {
            drawColor = Color.black;
        } else {
            final byte tileOverlayID = (byte) ((tileData >> SHIFT_OVERLAY) & TILE_OVERLAY_MASK);
            final boolean tileBlocked = (tileData >> SHIFT_BLOCKED) > 0;

            final Color tileColor = MapColor.getColor(TileFactory.getInstance().getMapColor(tileID));

            drawColor = new Color(tileColor);

            if (tileOverlayID > 0) {
                final Color overlayTileColor = MapColor.getColor(TileFactory.getInstance().getMapColor(tileOverlayID));

                drawColor.r += (overlayTileColor.r - tileColor.r) / 2.f;
                drawColor.g += (overlayTileColor.g - tileColor.g) / 2.f;
                drawColor.b += (overlayTileColor.b - tileColor.b) / 2.f;
            }
            if (tileBlocked) {
                drawColor.scale(BLOCKED_COLOR_MOD);
                drawColor.a = 1.f;
            }
        }

        graphics.setColor(drawColor);
        graphics.fillRect(0.f, 0.f, 1.f, 1.f);
    }

    /**
     * Set the location of the player. This tells the world map handler what map it needs to draw.
     *
     * @param playerLoc the location of the player
     */
    public void setPlayerLocation(@Nonnull final Location playerLoc) {
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

        if (noMapLoaded || (newMapLevel != mapLevel) || (newMapOriginX != mapOriginX) || (newMapOriginY != mapOriginY)
                ) {
            saveMap();

            updateAreas.clear();
            mapLevel = newMapLevel;
            mapOriginX = newMapOriginX;
            mapOriginY = newMapOriginY;
            loadMap();
        }

        miniMapOriginX = playerLoc.getScX() - mapOriginX - (MINI_MAP_WIDTH >> 1);
        miniMapOriginY = playerLoc.getScY() - mapOriginY - (MINI_MAP_HEIGHT >> 1);
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
        if (noMapLoaded) {
            return;
        }
        final File mapFile = getCurrentMapFilename();
        if (mapFile.exists() && !mapFile.canWrite()) {
            LOGGER.error("mapfile File locked, can't write the" + " name table.");
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
    private File getCurrentMapFilename() {
        final StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        builder.append("map");
        builder.append(mapOriginX / WORLDMAP_WIDTH);
        builder.append(mapOriginY / WORLDMAP_HEIGHT);
        builder.append(mapLevel);
        builder.append(".dat");
        return new File(World.getPlayer().getPath(), builder.toString());
    }

    /**
     * Load a map from its file. Any other formerly loaded map is discarded when this happens.
     */
    @SuppressWarnings("nls")
    public void loadMap() {
        loadingMap = true;
        final File mapFile = getCurrentMapFilename();

        if (!mapFile.exists()) {
            loadEmptyMap();
            return;
        }

        InputStream inStream = null;
        try {
            inStream = new GZIPInputStream(new FileInputStream(mapFile));
            final ReadableByteChannel inChannel = Channels.newChannel(inStream);

            synchronized (mapData) {
                mapData.rewind();

                int read = 1;
                while (read > 0) {
                    read = inChannel.read(mapData);
                }
                inChannel.close();
            }

            performFullUpdate();
        } catch (final IOException e) {
            LOGGER.error("Failed loading the map data from its file.", e);
            loadEmptyMap();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (final IOException e) {
                    LOGGER.error("Failed closing the file stream.");
                }
            }
        }
        noMapLoaded = false;
        loadingMap = false;
    }

    /**
     * Once this function is called the mini map will be rendered completely again.
     */
    public void performFullUpdate() {
        updateAreas.clear();

        int x = 0;
        int y = 0;
        while (x < WORLDMAP_WIDTH) {
            while (y < WORLDMAP_HEIGHT) {
                addUpdateArea(new Rectangle(x + mapOriginX, y + mapOriginY, MAX_UPDATE_AREA_SIZE, MAX_UPDATE_AREA_SIZE));
                y += MAX_UPDATE_AREA_SIZE;
            }
            y = 0;
            x += MAX_UPDATE_AREA_SIZE;
        }
    }

    /**
     * Add a area on the map that requires a update.
     *
     * @param rect the area to update
     */
    private void addUpdateArea(@Nonnull final Rectangle rect) {
        updateAreas.add(rect);
        miniMapDirty = true;
    }

    /**
     * Load a empty map in case it was not possible to load it from a file. This creates a full new map and ensures
     * that
     * there are no remaining from the last loaded map in.
     */
    private void loadEmptyMap() {
        loadingMap = true;
        synchronized (mapData) {
            mapData.rewind();
            while (mapData.remaining() > 0) {
                mapData.put((byte) 0);
            }
        }
        noMapLoaded = false;
        loadingMap = false;
    }

    /**
     * Update one tile of the overview map.
     *
     * @param updateData the data that is needed for the update
     */
    public void update(@Nonnull final TileUpdate updateData) {
        final Location tileLoc = updateData.getLocation();

        if ((tileLoc.getScX() < mapOriginX) || (tileLoc.getScX() >= (mapOriginX + WORLDMAP_WIDTH)) || (tileLoc.getScY
                () < mapOriginY) || (tileLoc.getScY() >= (mapOriginY + WORLDMAP_HEIGHT)) || (tileLoc.getScZ() !=
                mapLevel)) {
            return;
        }

        if (saveTile(tileLoc, updateData.getTileId(), updateData.isBlocked())) {
            addUpdateArea(new Rectangle(tileLoc.getScX(), tileLoc.getScY(), 1, 1));
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

        if (tileID == MapTile.ID_NONE) {
            synchronized (mapData) {
                if (mapData.getShort(index) == 0) {
                    return false;
                }
                mapData.putShort(index, (short) 0);
            }
            return true;
        }

        short encodedTileValue = (short) Tile.baseID(tileID);
        encodedTileValue += Tile.overlayID(tileID) << SHIFT_OVERLAY;
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
