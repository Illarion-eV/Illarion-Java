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

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;

import illarion.client.crash.MapProcessorCrashHandler;
import illarion.client.net.server.TileUpdate;
import illarion.client.util.SessionManager;
import illarion.client.util.SessionMember;

import illarion.common.graphics.ItemInfo;
import illarion.common.util.Location;
import illarion.common.util.Rectangle;

import illarion.graphics.SpriteColor;
import illarion.graphics.common.LightingMap;

/**
 * This handler stores all map data and ensures the updates of the map. This
 * class is fully thread save for all actions. Clipping, hiding effects and map
 * optimization is done by the GameMapProcessor.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class GameMap extends Interaction implements LightingMap,
    SessionMember {
    /**
     * The class that is used as helper class to clear all the tiles on the map.
     * Executing this will cause every tile to be cleared. Once this is done
     * they can be removed from the tile list.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class ClearHelper implements
        TObjectProcedure<MapTile> {
        /**
         * Default constructor
         */
        public ClearHelper() {
            // nothing to do
        }

        /**
         * Executed for every tile on the map. It will trigger the recycle
         * method for each tile.
         * 
         * @param tile the tile to clear
         * @return <code>true</code> in any case
         */
        @Override
        public boolean execute(final MapTile tile) {
            tile.recycle();
            return true;
        }
    }

    /**
     * This is a supporter class for the
     * {@link illarion.client.world.GameMap#renderLights()} function and it
     * triggers the renderLight function on each tile its called on.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    private static final class RenderLightsHelper implements
        TObjectProcedure<MapTile> {
        /**
         * The factor of the influence of the ambient light on this tile.
         */
        private float factor;

        /**
         * The ambient light that is used to render the real tile light.
         */
        private SpriteColor light;

        /**
         * Protected constructor so its accessible from outside.
         */
        protected RenderLightsHelper() {
            // nothing to do
        }

        /**
         * Trigger the renderLight function on one tile with the parameters that
         * were setup.
         * 
         * @param tile the tile that gets its lights rendered now
         * @return <code>true</code> at all times
         */
        @Override
        public boolean execute(final MapTile tile) {
            if (tile != null) {
                tile.renderLight(factor, light);
            }
            return true;
        }

        /**
         * Setup the helper class by setting the factor of influence of the
         * ambient light and the ambient light color itself.
         * 
         * @param newAmbientFactor the factor of influence of the ambient light
         *            color
         * @param ambientLight the ambient light color itself
         */
        void setup(final float newAmbientFactor, final SpriteColor ambientLight) {
            factor = newAmbientFactor;
            light = ambientLight;
        }
    }

    /**
     * This class is a helper class to reset the lights. Each tile this class is
     * executed receives a reset trigger for the light value of the tile. This
     * should be done before new light values are rendered on the tile.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class ResetLightsHelper implements
        TObjectProcedure<MapTile> {
        /**
         * Default constructor.
         */
        public ResetLightsHelper() {
            // nothing to do
        }

        /**
         * This method causes the tile its called for to reset the light.
         * 
         * @param tile the tile to reset
         * @return <code>true</code> in all cases
         */
        @Override
        public boolean execute(final MapTile tile) {
            tile.resetLight();
            return true;
        }
    }

    /**
     * This class is used to reset the hiding state of each tile in order to
     * reset the things the map processor did. This is needed in case the map
     * processor is restarted after a error.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class ResetMapProcessorHelper implements
        TObjectProcedure<MapTile> {
        /**
         * Default constructor.
         */
        public ResetMapProcessorHelper() {
            // nothing to do
        }

        /**
         * The hiding values of the tile this method is called for receives a
         * reset.
         * 
         * @param tile the tile to reset
         * @return <code>true</code> in all cases
         */
        @Override
        public boolean execute(final MapTile tile) {
            tile.setHidden(false);
            tile.setObstructed(false);
            return true;
        }
    }

    /**
     * The lock that is hold in case the light is currently rendered. If that is
     * done right now the map must not be rendered.
     */
    public static final Object LIGHT_LOCK = new Object();

    /**
     * Offset of the tiles due the perspective of the map view.
     */
    public static final int TILE_PERSPECTIVE_OFFSET = 3;

    /**
     * The lock that is hold in case there are remove or hide operations on the
     * map by the game map processor.
     */
    public static final Object TILES_LOCK = new Object();

    /**
     * The determines after how many remove operations the lists clean up on
     * their own.
     */
    private static final float MAP_COMPACTION_FACTOR = 0.01f;

    /**
     * This is a helper object that triggers recycle for all tiles its called
     * upon.
     */
    private final TObjectProcedure<MapTile> clearHelper = new ClearHelper();

    /**
     * The lock that secures the map tiles.
     */
    private final ReadWriteLock mapLock;

    /**
     * The handler for the overview map.
     */
    private final GameMiniMap minimap;

    /**
     * The map processor of this game map instance. This one handles the
     * clipping and the render optimization of the map.
     */
    private GameMapProcessor processor;

    /**
     * A helper class for rendering the light values on all map tiles.
     */
    private final RenderLightsHelper renderLightsHelper =
        new RenderLightsHelper();

    /**
     * This is a helper procedure that will trigger a reset on all tiles its
     * called upon.
     */
    private final TObjectProcedure<MapTile> resetLightsHelper =
        new ResetLightsHelper();

    /**
     * The tiles of the map. The key of the hash map is the location key of the
     * tiles location.
     */
    private final TLongObjectHashMap<MapTile> tiles;

    /**
     * Default constructor of the map handler.
     */
    public GameMap() {
        super();
        tiles = new TLongObjectHashMap<MapTile>(1000);
        tiles.setAutoCompactionFactor(MAP_COMPACTION_FACTOR);

        mapLock = new ReentrantReadWriteLock();

        minimap = new GameMiniMap();
        restartMapProcessor();
    }

    /**
     * Determines whether a map location accepts the light from a specific
     * direction.
     * 
     * @param loc the location of the tile
     * @param deltaX the X-Delta of the light ray direction
     * @param deltaY the Y-Delta of the light ray direction
     * @return true if the position accepts the light, false if not
     */
    @Override
    public boolean acceptsLight(final Location loc, final int deltaX,
        final int deltaY) {
        final MapTile tile = getMapAt(loc);
        if (tile != null) {
            switch (tile.getFace()) {
                default: //$FALL-THROUGH$
                case ItemInfo.FACE_ALL:
                    return true;

                case ItemInfo.FACE_W:
                    return deltaX >= 0;

                case ItemInfo.FACE_SW:
                    return (deltaY - deltaX) < 0;

                case ItemInfo.FACE_S:
                    return deltaY <= 0;
            }
        }

        return false;
    }

    /**
     * Determines how much the tile blocks the view.
     * 
     * @param loc the location of the tile
     * @return obscurity of the tile, 0 for clear view
     *         {@link illarion.graphics.common.LightingMap#BLOCKED_VIEW} for
     *         fully blocked
     */
    @Override
    public int blocksView(final Location loc) {
        final MapTile tile = getMapAt(loc);
        if (tile == null) {
            return 0;
        }
        return tile.getCoverage();
    }

    /**
     * Make the map processor checking if the player is inside a building or a
     * cave or something else and start fading out that tiles.
     */
    public void checkInside() {
        if (processor != null) {
            processor.checkInside();
        }
    }

    /**
     * Clear the entire map. That will cause that all tiles are recycled and
     * send back into the recycle factory.
     */
    public void clear() {
        mapLock.writeLock().lock();
        try {
            tiles.forEachValue(clearHelper);
            tiles.clear();
        } finally {
            mapLock.writeLock().unlock();
        }
    }

    @Override
    public void endSession() {
        if (processor != null) {
            processor.clear();
            processor.saveShutdown();
            processor = null;
        }
        clear();
    }

    /**
     * Finish a tile update of the game map.
     */
    public void finishTileUpdate() {
        if (processor != null) {
            processor.start();
        }
    }

    /**
     * Get the clipping values the map processor is working with. Careful at
     * using this. Its possible to change the values of this rectangle. This
     * will lead to really change behavior.
     * 
     * @return the clipping rectangle, this contains the distances of the
     *         clipping border around the player character is map rows and
     *         columns
     */
    public Rectangle getClipping() {
        return processor.getClipping();
    }

    /**
     * Get the tile on the map that user is currently pointing at in case the
     * player is pointing at the game map.
     * 
     * @param x the X-Coordinate of the screen position the user is pointing at
     * @param y the Y-Coordinate of the screen position the user is pointing at
     * @return the map tile the user is pointing at or null
     * @see illarion.client.world.Interaction#getComponentAt(int, int)
     */
    @Override
    public MapTile getComponentAt(final int x, final int y) {
        final int worldX = Game.getDisplay().getWorldX(x);
        final int worldY = Game.getDisplay().getWorldY(y);

        final Location helpLoc = Location.getInstance();
        helpLoc.setDC(worldX, worldY);

        final int playerBase = Game.getPlayer().getBaseLevel();
        final int base = playerBase - 2;
        final int lowX =
            helpLoc.getScX() + ((2 - playerBase) * TILE_PERSPECTIVE_OFFSET);
        final int lowY =
            helpLoc.getScY() - ((2 - playerBase) * TILE_PERSPECTIVE_OFFSET);
        helpLoc.recycle();

        MapTile foundTile;
        for (int i = 4; i >= 0; --i) {
            foundTile =
                getMapAt(lowX - (TILE_PERSPECTIVE_OFFSET * i), lowY
                    + (TILE_PERSPECTIVE_OFFSET * i), base + i);
            if ((foundTile != null) && !foundTile.isHidden()) {
                return foundTile;
            }
        }

        return null;
    }

    /**
     * Get item elevation on a special position.
     * 
     * @param loc the location that shall be checked
     * @return the elevation value
     */
    public int getElevationAt(final Location loc) {
        final MapTile ground = getMapAt(loc);
        if (ground != null) {
            return ground.getElevation();
        }
        return 0;
    }

    /**
     * Get the amount of stripes that need to be requested from the server as
     * map height.
     * 
     * @return the amount of stripes requested from the server as map height
     */
    public int getHeightStripes() {
        return processor.getHeightStripes();
    }

    /**
     * Get a map tile at a specified location.
     * 
     * @param posX the x coordinate of the location of the searched tile
     * @param posY the y coordinate of the location of the searched tile
     * @param posZ the z coordinate of the location of the searched tile
     * @return the map tile at the location or <code>null</code>
     */
    public MapTile getMapAt(final int posX, final int posY, final int posZ) {
        return getMapAt(Location.getKey(posX, posY, posZ));
    }

    /**
     * Get a map tile at a specified location.
     * 
     * @param loc the location on the map
     * @return the map tile at the location or <code>null</code>
     */
    public MapTile getMapAt(final Location loc) {
        return getMapAt(loc.getKey());
    }

    /**
     * Get a map tile at a specified location.
     * 
     * @param key the key of the location
     * @return the map tile at the location or <code>null</code>
     */
    public MapTile getMapAt(final long key) {
        mapLock.readLock().lock();
        final MapTile ret = tiles.get(key);
        mapLock.readLock().unlock();
        return ret;
    }

    /**
     * Get the overview map handler that is used currently.
     * 
     * @return the object that handles the overview map
     */
    public GameMiniMap getMinimap() {
        return minimap;
    }

    /**
     * Get the amount of stripes that need to be requested from the server as
     * map width.
     * 
     * @return the amount of stripes requested from the server as map width
     */
    public int getWidthStripes() {
        return processor.getWidthStripes();
    }

    @Override
    public void initSession() {
        SessionManager.getInstance().addMember(minimap);
    }

    /**
     * Check if there is a tile at one location on the map.
     * 
     * @param posX the x coordinate of the location of the searched tile
     * @param posY the y coordinate of the location of the searched tile
     * @param posZ the z coordinate of the location of the searched tile
     * @return <code>true</code> in case there is a tile at this position
     */
    public boolean isMapAt(final int posX, final int posY, final int posZ) {
        return isMapAt(Location.getKey(posX, posY, posZ));
    }

    /**
     * Check if there is a tile at one location on the map.
     * 
     * @param loc the location on the map
     * @return <code>true</code> in case there is a tile at this position
     */
    public boolean isMapAt(final Location loc) {
        return isMapAt(loc.getKey());
    }

    /**
     * Check if there is a tile at one location on the map.
     * 
     * @param key the key to the location on the map
     * @return <code>true</code> in case there is a tile at this position
     */
    public boolean isMapAt(final long key) {
        mapLock.readLock().lock();
        boolean ret = false;
        try {
            ret = tiles.containsKey(key);
        } finally {
            mapLock.readLock().unlock();
        }
        return ret;
    }

    /**
     * Process all tiles in the storage. The procedure in the parameter is
     * called for every tile that is currently known.
     * 
     * @param procedure the procedure that is called for every tile
     */
    public void processTiles(final TLongObjectProcedure<MapTile> procedure) {
        mapLock.readLock().lock();
        try {
            tiles.forEachEntry(procedure);
        } finally {
            mapLock.readLock().unlock();
        }
    }

    /**
     * Remove a tile by its key from the map.
     * 
     * @param key the key of the tile that is to be removed
     */
    public void removeTile(final long key) {
        mapLock.writeLock().lock();
        MapTile removedTile = null;
        try {
            removedTile = tiles.remove(key);
        } finally {
            mapLock.writeLock().unlock();
        }

        if (removedTile != null) {
            removedTile.recycle();
        }
    }

    /**
     * Remove a tile from the map.
     * 
     * @param tile the tile that is to be removed
     */
    public void removeTile(final MapTile tile) {
        removeTile(tile.getLocation().getKey());
    }

    /**
     * Render lights based on the tile light and the ambient light generated by
     * the current IG time and the weather.
     */
    @Override
    public void renderLights() {
        final float factor =
            1.f - Game.getWeather().getAmbientLight().getLuminationf();

        renderLightsHelper.setup(factor, Game.getWeather().getAmbientLight());

        synchronized (LIGHT_LOCK) {
            mapLock.readLock().lock();
            try {
                tiles.forEachValue(renderLightsHelper);
            } finally {
                mapLock.readLock().unlock();
            }

            Game.getPeople().updateLight();
        }
    }

    /**
     * Reset all tiles on the map back to black color.
     */
    @Override
    public void resetLights() {
        mapLock.readLock().lock();
        try {
            tiles.forEachValue(resetLightsHelper);
        } finally {
            mapLock.readLock().unlock();
        }
    }

    /**
     * Start or restart the map processor.
     */
    public void restartMapProcessor() {
        if (processor != null) {
            processor.clear();
            processor.saveShutdown();
            processor = null;

            tiles.forEachValue(new ResetMapProcessorHelper());
        }
        processor = new GameMapProcessor(this);
        processor.setUncaughtExceptionHandler(MapProcessorCrashHandler
            .getInstance());
        processor.start();
    }

    /**
     * Set a light color on a tile.
     * 
     * @param loc the location of the map tile on the server map
     * @param color the color that shall be set for this tile
     * @see illarion.graphics.common.LightingMap#setLight(Location, SpriteColor)
     */
    @Override
    public void setLight(final Location loc, final SpriteColor color) {
        final MapTile tile = getMapAt(loc);
        if (tile != null) {
            tile.addLight(color);
        }
    }

    @Override
    public void shutdownSession() {
        // shut down of the map requires nothing
    }

    @Override
    public void startSession() {
        restartMapProcessor();
    }

    /**
     * Prepare the game map for a tile update.
     */
    public void startTileUpdate() {
        if (processor != null) {
            processor.pause();
        }
    }

    /**
     * Update the data of a tile. This does nothing but forwarding the location
     * of the tile to the map processor so it checks the tile again.
     * 
     * @param tile the tile to check again
     */
    public void updateTile(final MapTile tile) {
        if (processor != null) {
            processor.reportUnchecked(tile.getLocation().getKey());
        }
    }

    /**
     * Perform a update of a single map tile regarding the update informations.
     * This can add a new tile, update a old one or delete one tile.
     * 
     * @param updateData the data of the update
     */
    @SuppressWarnings("nls")
    public void updateTile(final TileUpdate updateData) {
        final long locKey = updateData.getLocation().getKey();
        MapTile tile = getMapAt(locKey);

        if (updateData.getTileId() != MapTile.ID_NONE) {
            final boolean newTile = (tile == null);

            // create a tile for this location if none was found
            if (newTile) {
                tile = MapTile.create();
            }

            if (tile == null) {
                throw new IllegalStateException("Failed updateing tile");
            }

            // update tile from update info
            tile.getLocation().set(updateData.getLocation());
            tile.update(updateData);

            if (newTile) {
                mapLock.writeLock().lock();
                try {
                    tiles.put(locKey, tile);
                } finally {
                    mapLock.writeLock().unlock();
                }
            }
            Game.getLights().notifyChange(updateData.getLocation());

            if (processor != null) {
                processor.reportUnchecked(locKey);
            }

            // remember real map tile for use with overview map
            updateData.setMapTile(tile);
        } else {
            if (tile != null) {
                mapLock.writeLock().lock();
                try {
                    tiles.remove(locKey);
                } finally {
                    mapLock.writeLock().unlock();
                }
                tile.recycle();
            }
        }
    }
}
