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
import org.newdawn.slick.opengl.shader.ShaderProgram;

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
public final class GameMiniMap {
    /**
     * This class is used to render the minimap to a GUI element. This is a special implementation of the render
     * image for the Nifty-GUI as the image needs to move around as the player moves.
     */
    private final class GameMiniMapRenderImage implements SlickRenderImage {
        /**
         * This instance of a slick color is used to avoid the need to create a new color instance every time this
         * image is rendered.
         */
        private final Color slickColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);

        /**
         * The shader required to render the map.
         */
        private ShaderProgram miniMapShader;

        @Override
        public void renderImage(final Graphics g, final int x, final int y, final int width, final int height,
                                final de.lessvoid.nifty.tools.Color color, final float scale) {
            renderImage(g, x, y, width, height, 0, 0, MINI_MAP_WIDTH, MINI_MAP_HEIGHT, color, scale, MINI_MAP_WIDTH / 2,
                    MINI_MAP_HEIGHT / 2);
        }

        @Override
        public void renderImage(final Graphics g, final int x, final int y, final int w, final int h,
                                final int srcX, final int srcY, final int srcW, final int srcH,
                                final de.lessvoid.nifty.tools.Color color, final float scale, final int centerX,
                                final int centerY) {
            if (!enabled) {
                return;
            }

            if (miniMapShader == null) {
                try {
                    miniMapShader = ShaderProgram.loadProgram(
                            "illarion/client/graphics/shader/minimap.vert",
                            "illarion/client/graphics/shader/minimap.frag");
                } catch (SlickException e) {
                    disableMiniMap();
                    LOGGER.error("Error loading shader!", e);
                    return;
                }
            }

            g.pushTransform();
            g.translate(centerX, centerY);
            g.scale(scale, scale);
            g.rotate(0, 0, -45.f);
            g.translate(-centerX, -centerY);

            miniMapShader.bind();
            miniMapShader.setUniform1i("tex0", 0);

            final float miniMapCenterX = (minimapOriginX + (MINI_MAP_WIDTH / 2.f)) / WORLDMAP_WIDTH;
            final float miniMapCenterY = (minimapOriginY + (MINI_MAP_HEIGHT / 2.f)) / WORLDMAP_HEIGHT;
            miniMapShader.setUniform2f("center", miniMapCenterX, miniMapCenterY);
            miniMapShader.setUniform1f("radius", (float) MINI_MAP_HEIGHT / 2.f / (float) WORLDMAP_HEIGHT);

            g.drawImage(worldmapTexture, x, y, x + w, y + h, srcX + minimapOriginX, srcY + minimapOriginY,
                    srcX + minimapOriginX + srcW, srcY + minimapOriginY + srcH,
                    SlickRenderUtils.convertColorNiftySlick(color, slickColor));

            miniMapShader.unbind();

            g.popTransform();
        }

        @Override
        public int getWidth() {
            return MINI_MAP_WIDTH;
        }

        @Override
        public int getHeight() {
            return MINI_MAP_HEIGHT;
        }

        @Override
        public void dispose() {
            // nothing
        }
    }

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
     * The height of the world map in tiles.
     */
    public static final int WORLDMAP_HEIGHT = 1024;

    /**
     * The width of the world map in tiles.
     */
    public static final int WORLDMAP_WIDTH = 1024;

    /**
     * Indicates if a map is loaded or not.
     */
    private boolean loadedMap;

    /**
     * This variable stores if the map is currently loaded.
     */
    private volatile boolean loadingMap;

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
     * The height of the overview map in tiles.
     */
    private static final int MINI_MAP_HEIGHT = 162;

    /**
     * The width of the overview map in tiles.
     */
    private static final int MINI_MAP_WIDTH = 162;

    /**
     * The areas that require a update.
     */
    private final List<Rectangle> updateAreas;

    /**
     * The maximal size of a area that is updated at once.
     */
    private final int MAX_UPDATE_AREA_SIZE = 64;

    /**
     * The texture that is used to draw the world map sprite.
     */
    private Image worldmapTexture;

    /**
     * This flag is turned true in case rendering the mini map is disabled.
     */
    private boolean enabled;

    /**
     * Constructor of the game map that sets up all instance variables.
     */
    public GameMiniMap() {
        mapData = ByteBuffer.allocate(WORLDMAP_WIDTH * WORLDMAP_HEIGHT * BYTES_PER_TILE);
        mapData.order(ByteOrder.nativeOrder());
        loadedMap = false;

        updateAreas = new ArrayList<Rectangle>();
        enabled = true;
    }

    public SlickRenderImage getMiniMap() {
        return new GameMiniMapRenderImage();
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
                addUpdateArea(new Rectangle(x, y, MAX_UPDATE_AREA_SIZE, MAX_UPDATE_AREA_SIZE));
                y += MAX_UPDATE_AREA_SIZE;
            }
            y = 0;
            x += MAX_UPDATE_AREA_SIZE;
        }
    }

    private void disableMiniMap() {
        enabled = false;
        EventBus.publish(new HideMiniMap());
    }

    private void drawTile(final short tileData, final Graphics graphics) {
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
     * Encode a server location to the index in the map data buffer.
     *
     * @param loc the server location that shall be encoded
     * @return the index of the location in the map data buffer
     */
    private int encodeLocation(final Location loc) {
        return encodeLocation(loc.getScX(), loc.getScY());
    }

    /**
     * Encode a server location to the index in the map data buffer.
     *
     * @param x the x coordinate of the location on the map
     * @param y the y coordinate of the location on the map
     * @return the index of the location in the map data buffer
     */
    private int encodeLocation(final int x, final int y) {
        return ((y - mapOriginY) * WORLDMAP_WIDTH * BYTES_PER_TILE) + ((x - mapOriginX) * BYTES_PER_TILE);
    }

    /**
     * Get the full path string to the file for the currently selected map. This file needs to be used to store and
     * load
     * the map data.
     *
     * @return the path and the filename of the map file
     */
    @SuppressWarnings("nls")
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
     * Get the level of the mini/world-map that is currently displayed. This equals the server Z coordinate of the
     * current player location.
     *
     * @return the level of the maps
     */
    public int getMapLevel() {
        return mapLevel;
    }

    /**
     * Get the entire origin of the current world map. The origin is stored in a {@link illarion.common.types.Location} class instance that
     * is
     * newly fetched from the buffer. In case its not used anymore it should be put back into the buffer. <p> The
     * server
     * X and Y coordinate are the coordinates of the current origin of the world map. The Z coordinate is the current
     * level. </p> <p> For details on each coordinate see the functions that request the single coordinates. </p>
     *
     * @return the location of the overview map origin
     * @see #getMapLevel()
     * @see #getMapOriginX()
     * @see #getMapOriginY()
     */
    public Location getMapOrigin() {
        return getMapOrigin(new Location());
    }

    /**
     * This function is similar to {@link #getMapOrigin()}. The only difference is that this function does not fetch a
     * new instance of the location class, it rather uses the instance set as argument and fills it with the data.
     *
     * @param loc the location instance that is filled with the origin informations
     * @return the same instance of the {@link Location} class that was set as parameter
     * @throws NullPointerException in case the parameter <code>loc</code> is set to <code>null</code>
     * @see #getMapOrigin()
     */
    public Location getMapOrigin(final Location loc) {
        loc.setSC(mapOriginX, mapOriginY, mapLevel);
        return loc;
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
     * Load a empty map in case it was not possible to load it from a file. This creates a full new map and ensures
     * that
     * there are no remaining from the last loaded map in.
     */
    private void loadEmptyMap() {
        loadingMap = true;
        mapData.rewind();
        while (mapData.remaining() > 0) {
            mapData.put((byte) 0);
        }
        loadedMap = true;
        loadingMap = false;
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
        loadedMap = true;
        loadingMap = false;
    }

    /**
     * The render function should be triggered at every render run of the mini map or the world map. In case the map is
     * dirty this will trigger a update of the whole map.
     */
    public void render() {
        if (!enabled) {
            return;
        }

        if (worldmapTexture == null) {
            try {
                worldmapTexture = Image.createOffscreenImage(WORLDMAP_WIDTH, WORLDMAP_HEIGHT);
                final Graphics g = worldmapTexture.getGraphics();
                g.setColor(Color.black);
                g.fillRect(0, 0, WORLDMAP_WIDTH, WORLDMAP_HEIGHT);
            } catch (final SlickException e) {
                LOGGER.error("Failed to create minimap texture.", e);
                disableMiniMap();
                return;
            }
        }
        if (!loadedMap || loadingMap) {
            return;
        }

        try {
            drawMap();
        } catch (SlickException e) {
            LOGGER.error("Drawing the minimap failed!", e);
        }
    }

    private void drawMapArea(final Rectangle rectangle) throws SlickException {
        final Graphics graphics = worldmapTexture.getGraphics();
        graphics.setColor(Color.black);
        graphics.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());

        for (int x = rectangle.getLeft(); x < rectangle.getRight(); x++) {
            for (int y = rectangle.getBottom(); y < rectangle.getTop(); y++) {
                final int location = encodeLocation(x, y);

                final short tileData = mapData.getShort(location);

                if (tileData == 0) {
                    continue;
                }

                graphics.translate(x, y);
                drawTile(tileData, graphics);
                graphics.translate(-x, -y);
            }
        }
    }

    private boolean miniMapUpdated;

    private void addUpdateArea(final Rectangle rect) {
        updateAreas.add(rect);
        miniMapUpdated = false;
    }

    private void drawMap() throws SlickException {
        if (updateAreas.isEmpty()) {
            return;
        }

        final int allowedArea = MAX_UPDATE_AREA_SIZE * MAX_UPDATE_AREA_SIZE;
        int processedArea = 0;

        if (!miniMapUpdated) {
            final Rectangle miniMapRect = new Rectangle(minimapOriginX, minimapOriginY, MINI_MAP_WIDTH, MINI_MAP_HEIGHT);

            for (int i = 0; i < updateAreas.size(); i++) {
                final Rectangle testRect = updateAreas.get(i);
                if (testRect.intersects(miniMapRect)) {
                    final int currentArea = testRect.getArea();
                    drawMapArea(updateAreas.remove(i));

                    processedArea += currentArea;
                    if (processedArea >= allowedArea) {
                        return;
                    }
                    --i;
                }
            }
        }

        miniMapUpdated = true;

        if ((processedArea < allowedArea) && !updateAreas.isEmpty()) {
            final Rectangle currentArea = updateAreas.remove(0);
            processedArea += currentArea.getArea();
            drawMapArea(currentArea);
        }
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
     * Save the information about a tile within the map data. This will overwrite any existing data about a tile.
     *
     * @param loc     the location of the tile
     * @param tileID  the ID of tile that is located at the position
     * @param blocked true in case this tile is not passable
     * @return <code>true</code> in case the new ID of the tile and the already set ID are not equal and the ID did
     *         change this way
     */
    private boolean saveTile(final Location loc, final int tileID, final boolean blocked) {

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

    /**
     * Set the location of the player. This tells the world map handler what map it needs to draw.
     *
     * @param playerLoc the location of the player
     */
    public void setPlayerLocation(final Location playerLoc) {
        final int newMapLevel = playerLoc.getScZ();

        int newMapOriginX = 0;
        if (playerLoc.getScX() >= 0) {
            newMapOriginX = (playerLoc.getScX() / WORLDMAP_WIDTH) * WORLDMAP_WIDTH;
        } else {
            newMapOriginX = ((playerLoc.getScX() / WORLDMAP_WIDTH) * WORLDMAP_WIDTH) - WORLDMAP_WIDTH;
        }

        int newMapOriginY = 0;
        if (playerLoc.getScY() >= 0) {
            newMapOriginY = (playerLoc.getScY() / WORLDMAP_HEIGHT) * WORLDMAP_HEIGHT;
        } else {
            newMapOriginY = ((playerLoc.getScY() / WORLDMAP_HEIGHT) * WORLDMAP_HEIGHT) - WORLDMAP_HEIGHT;
        }

        if (!loadedMap || (newMapLevel != mapLevel) || (newMapOriginX != mapOriginX) || (newMapOriginY != mapOriginY)
                ) {
            saveMap();
            mapLevel = newMapLevel;
            mapOriginX = newMapOriginX;
            mapOriginY = newMapOriginY;
            loadMap();
        }

        int minimapOriginX = playerLoc.getScX() - mapOriginX - (MINI_MAP_WIDTH >> 1);
        int minimapOriginY = playerLoc.getScY() - mapOriginY - (MINI_MAP_HEIGHT >> 1);

        if (minimapOriginX < 0) {
            minimapOriginX = 0;
        } else if ((minimapOriginX + MINI_MAP_WIDTH) >= WORLDMAP_WIDTH) {
            minimapOriginX = WORLDMAP_WIDTH - MINI_MAP_WIDTH - 1;
        }
        if (minimapOriginY < 0) {
            minimapOriginY = 0;
        } else if ((minimapOriginY + MINI_MAP_HEIGHT) >= WORLDMAP_HEIGHT) {
            minimapOriginY = WORLDMAP_HEIGHT - MINI_MAP_HEIGHT - 1;
        }

        this.minimapOriginX = minimapOriginX;
        this.minimapOriginY = minimapOriginY;
    }

    private int minimapOriginX;
    private int minimapOriginY;

    /**
     * Update one tile of the overview map.
     *
     * @param updateData the data that is needed for the update
     */
    public void update(final TileUpdate updateData) {
        final Location tileLoc = updateData.getLocation();

        if ((tileLoc.getScX() < mapOriginX) || (tileLoc.getScX() >= (mapOriginX + WORLDMAP_WIDTH)) || (tileLoc.getScY
                () < mapOriginY) || (tileLoc.getScY() >= (mapOriginY + WORLDMAP_HEIGHT)) || (tileLoc.getScZ() !=
                mapLevel)) {
            return;
        }

        if (saveTile(tileLoc, updateData.getTileId(), updateData.isBlocked())) {
            addUpdateArea(new Rectangle(tileLoc.getScX() - mapOriginX, tileLoc.getScY() - mapOriginY, 1, 1));
        }
    }
}
