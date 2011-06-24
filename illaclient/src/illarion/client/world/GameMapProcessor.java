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

import org.apache.log4j.Logger;

import gnu.trove.list.array.TLongArrayList;
import gnu.trove.procedure.TLongObjectProcedure;

import illarion.client.ClientWindow;

import illarion.common.graphics.MapConstants;
import illarion.common.util.Location;
import illarion.common.util.Rectangle;

/**
 * This class checks the consistency of the map, the map, handles the fading out
 * of map layers if needed and optimizes the map. Clipping and the removal of
 * unneeded map data is handled also by this.
 * <p>
 * This handler is a own thread, so its at times expensive, actions do not
 * disturb the main loop.
 * </p>
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class GameMapProcessor extends Thread implements
    TLongObjectProcedure<MapTile> {
    /**
     * This value is added to the clipping border in all directions around the
     * screen in addition to the actual visible range. If this value is set too
     * high the client has to keep tiles in the memory that are not needed
     * anymore. If the value is too small black stripes will show up while
     * walking.
     */
    private static final int ADD_CLIPPING_RANGE = 7;

    /**
     * This is the amount of rows and columns that are requested from the server
     * in addition to the tiles needed to fill the screen size. If this value is
     * chosen too high the result is that large items and light sources are
     * known to the client too late and just "pop" in.
     */
    private static final int ADD_MAP_RANGE = 4;

    /**
     * This is the additional map range that is attached to the bottom of the
     * clipping ranges.
     */
    private static final int ADD_MAP_RANGE_BOTTOM = 9;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(GameMapProcessor.class);

    /**
     * If this flag is set to true, the map processor will perform a check if
     * the player is inside at the next run.
     */
    private volatile boolean checkInside = false;

    /**
     * This rectangle is used to store the clipping borders.
     */
    private final Rectangle clipping;

    /**
     * This variable stores if there is a full check of the entire map needed or
     * if its was unchecked since the last change.
     */
    private volatile boolean fullCheckNeeded = true;

    /**
     * The array of values if the character is inside or not. It stores the
     * values for every level. So if the character is inside a 2 level high cave
     * the second value will be true, but the first one false.
     */
    private final boolean insideStates[] = new boolean[2];

    /**
     * The map that is handled by this processor instance.
     */
    private final GameMap parent;

    /**
     * This variable set to <code>true</code> causes the main loop to hold until
     * its resumed with {@link #start()}.
     */
    private boolean pauseLoop = false;

    /**
     * This variable is <code>true</code> as long as the thread is supposed to
     * run. The thread will quit as soon as possible once it switches to
     * <code>false</code>.
     */
    private volatile boolean running;

    /**
     * The list of location keys that were yet not checked by the processor.
     */
    private final TLongArrayList unchecked;

    /**
     * Constructor for a new instance of the game map processor. This processor
     * will be bound to one map.
     * 
     * @param parentMap the game map that is processed by this processor
     */
    @SuppressWarnings("nls")
    public GameMapProcessor(final GameMap parentMap) {
        super("Map Processor");
        parent = parentMap;
        unchecked = new TLongArrayList();
        clipping = Rectangle.getInstance();
        clipping.set(-18, -24, 36, 42);
        running = false;

        calculateClippingBorders(ClientWindow.getInstance().getScreenWidth(),
            ClientWindow.getInstance().getScreenHeight());
    }

    /**
     * Do a inside check during the next run of the thread loop.
     */
    public void checkInside() {
        checkInside = true;
    }

    /**
     * Clear the map, that should be done in case all tiles got removed from the
     * map and the current checks need to stop instantly.
     */
    public void clear() {
        synchronized (unchecked) {
            unchecked.clear();
        }
    }

    /**
     * Procedure function that is used to collect data from the map of the game.
     * <p>
     * Do not call this function from any other class.
     * </p>
     */
    @Override
    public boolean execute(final long key, final MapTile tile) {
        unchecked.add(key);

        return true;
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
        return clipping;
    }

    /**
     * Get the amount of stripes that need to be requested from the server as
     * map height.
     * 
     * @return the amount of stripes requested from the server as map height
     */
    public int getHeightStripes() {
        return clipping.getTop() >> 1;
    }

    /**
     * Get the amount of stripes that need to be requested from the server as
     * map width.
     * 
     * @return the amount of stripes requested from the server as map width
     */
    public int getWidthStripes() {
        return clipping.getRight() >> 1;
    }

    /**
     * This method causes the map processor to hold and stop processing data
     * until the operations are resumed with {@link #start()}.
     */
    public synchronized void pause() {
        pauseLoop = true;
    }

    /**
     * Add a key of a map location to the processor that contains a location on
     * the map that was yet unchecked.
     * 
     * @param key the key of the location that needs to be checked
     */
    public void reportUnchecked(final long key) {
        synchronized (unchecked) {
            if (unchecked.contains(key)) {
                unchecked.add(key);
            }
            unchecked.notify();
        }
        fullCheckNeeded = true;
    }

    /**
     * The main method of the game map processor.
     */
    @SuppressWarnings("nls")
    @Override
    public void run() {
        while (running) {
            while (pauseLoop) {
                try {
                    synchronized (unchecked) {
                        unchecked.wait();
                    }
                } catch (final InterruptedException e) {
                    LOGGER.debug("Unexpected wakeup during pause.", e);
                }
            }
            performInsideCheck();

            if (processUnchecked()) {
                continue;
            }

            if (workloadCheck()) {
                continue;
            }

            try {
                synchronized (unchecked) {
                    unchecked.wait();
                }
            } catch (final InterruptedException e) {
                LOGGER.debug("Unexpected wake up of the map processor", e);
            }
        }
    }

    /**
     * Stop this thread as soon as possible.
     */
    public void saveShutdown() {
        running = false;
        synchronized (unchecked) {
            unchecked.notify();
        }
    }

    /**
     * This method starts or resumes the map processor.
     */
    @Override
    public synchronized void start() {
        pauseLoop = false;
        if (running) {
            synchronized (unchecked) {
                unchecked.notify();
            }
        } else {
            running = true;
            super.start();
        }
    }

    /**
     * Add all tiles in the visible perspective above one location to the list
     * of unchecked tiles again.
     * 
     * @param searchLoc the location the search starts add. This location is not
     *            added to the list of unchecked tiles
     * @param limit the limit the Z coordinate is not going below
     */
    private void addAllAbove(final Location searchLoc, final int limit) {
        int currX = searchLoc.getScX();
        int currY = searchLoc.getScY();
        int currZ = searchLoc.getScZ();

        while (currZ <= limit) {
            currX -= GameMap.TILE_PERSPECTIVE_OFFSET;
            currY += GameMap.TILE_PERSPECTIVE_OFFSET;
            currZ++;
            final long foundKey = Location.getKey(currX, currY, currZ);
            synchronized (unchecked) {
                if (!unchecked.contains(foundKey)) {
                    unchecked.add(foundKey);
                }
            }
        }
    }

    /**
     * Add all tiles in the visible perspective below one location to the list
     * of unchecked tiles again.
     * 
     * @param searchLoc the location the search starts add. This location is not
     *            added to the list of unchecked tiles
     * @param limit the limit the Z coordinate is not going below
     */
    private void addAllBelow(final Location searchLoc, final int limit) {
        int currX = searchLoc.getScX();
        int currY = searchLoc.getScY();
        int currZ = searchLoc.getScZ();

        while (currZ >= limit) {
            currX += GameMap.TILE_PERSPECTIVE_OFFSET;
            currY -= GameMap.TILE_PERSPECTIVE_OFFSET;
            currZ--;
            final long foundKey = Location.getKey(currX, currY, currZ);
            synchronized (unchecked) {
                if (!unchecked.contains(foundKey)) {
                    unchecked.add(foundKey);
                }
            }
        }
    }

    /**
     * Add all tiles surrounding this location and the tile above to the list of
     * unchecked tiles.
     * 
     * @param searchLoc the location of the start of the search.
     */
    private void addAllNeighbours(final Location searchLoc) {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if ((x == 0) && (y == 0)) {
                    continue;
                }
                final long foundKey =
                    Location.getKey(searchLoc.getScX() + x, searchLoc.getScY()
                        + y, searchLoc.getScZ());
                synchronized (unchecked) {
                    if (!unchecked.contains(foundKey)) {
                        unchecked.add(foundKey);
                    }
                }
            }
        }

        final long foundKey =
            Location.getKey(searchLoc.getScX(), searchLoc.getScY(),
                searchLoc.getScZ() + 1);
        synchronized (unchecked) {
            if (!unchecked.contains(foundKey)) {
                unchecked.add(foundKey);
            }
        }
    }

    /**
     * Calculate the clipping borders again in relation to the size of the
     * displayed window.
     * 
     * @param width the width of the area the map is displayed in
     * @param height the height of the area the map is displayed in
     */
    private void calculateClippingBorders(final int width, final int height) {
        final int leftRight =
            (int) Math.ceil(width / (float) MapConstants.TILE_W);
        final int topBottom =
            (int) Math.ceil(height / (float) MapConstants.TILE_H);

        int leftClipping = -(leftRight + ADD_MAP_RANGE);
        if ((leftClipping % 2) != 0) {
            leftClipping -= 1;
        }

        int topClipping = topBottom + ADD_MAP_RANGE;
        if ((topClipping % 2) != 0) {
            topClipping += 1;
        }

        clipping.set(leftClipping, -(topClipping + ADD_MAP_RANGE_BOTTOM),
            -(leftClipping << 1), (topClipping << 1) + ADD_MAP_RANGE_BOTTOM);
    }

    /**
     * Do the clipping check of a tile.
     * 
     * @param tile the tile that is checked
     * @param key the location key of the checked tile
     * @return <code>true</code> in case the tile was clipped away
     */
    private boolean checkClipping(final MapTile tile, final long key) {
        final Location playerLoc = Game.getPlayer().getLocation();
        final Location tileLoc = tile.getLocation();

        /*
         * Start checking the clipping of the tiles. In case a tile is found
         * outside the clipping range, its deleted.
         */
        if ((playerLoc.getScZ() + 2) < tileLoc.getScZ()) {
            parent.removeTile(key);
            LOGGER.debug("Removed tile at location " + tileLoc.toString()
                + " (tile.x > player.z + 2)");
            return true;
        }

        if ((playerLoc.getScZ() - 2) > tileLoc.getScZ()) {
            parent.removeTile(key);
            LOGGER.debug("Removed tile at location " + tileLoc.toString()
                + " (tile.x < player.z - 2)");
            return true;
        }

        if (((playerLoc.getCol() + clipping.getLeft()) - ADD_CLIPPING_RANGE) > tileLoc
            .getCol()) {
            parent.removeTile(key);
            LOGGER.debug("Removed tile at location " + tileLoc.toString()
                + " (outside of left clipping)");
            LOGGER.debug("Ply Col: " + Integer.toString(playerLoc.getCol())
                + " Clipping Left: " + Integer.toString(clipping.getLeft())
                + " Tile Col: " + Integer.toString(tileLoc.getCol()));
            return true;
        }

        if ((playerLoc.getCol() + clipping.getRight() + ADD_CLIPPING_RANGE) < tileLoc
            .getCol()) {
            parent.removeTile(key);
            LOGGER.debug("Removed tile at location " + tileLoc.toString()
                + " (outside of right clipping)");
            LOGGER.debug("Ply Col: " + Integer.toString(playerLoc.getCol())
                + " Clipping Right: " + Integer.toString(clipping.getRight())
                + " Tile Col: " + Integer.toString(tileLoc.getCol()));
            return true;
        }

        final int level = Math.abs(tileLoc.getScZ() - playerLoc.getScZ()) * 6;

        if ((playerLoc.getRow() + clipping.getTop() + ADD_CLIPPING_RANGE) < (tileLoc
            .getRow() - level)) {
            parent.removeTile(key);
            LOGGER.debug("Removed tile at location " + tileLoc.toString()
                + " (outside of top clipping)");
            LOGGER.debug("Ply Row: " + Integer.toString(playerLoc.getRow())
                + " Clipping Top: " + Integer.toString(clipping.getTop())
                + " Tile Row: " + Integer.toString(tileLoc.getRow()));
            return true;
        }

        if (((playerLoc.getRow() + clipping.getBottom()) - ADD_CLIPPING_RANGE) > (tileLoc
            .getRow() + level)) {
            parent.removeTile(key);
            LOGGER.debug("Removed tile at location " + tileLoc.toString()
                + " (outside of bottom clipping)");
            LOGGER.debug("Ply Row: " + Integer.toString(playerLoc.getRow())
                + " Clipping Bottom: "
                + Integer.toString(clipping.getBottom()) + " Tile Row: "
                + Integer.toString(tileLoc.getRow()));
            return true;
        }

        return false;
    }

    /**
     * Check if the tile needs to be hidden because the player is inside a cave
     * or a house or something like this.
     * 
     * @param tile the tile that is checked
     * @param key the location key of the checked tile
     * @return <code>true</code> in case the tile was handled
     */
    @SuppressWarnings("nls")
    private boolean checkHidden(final MapTile tile, final long key) {
        final Location playerLoc = Game.getPlayer().getLocation();
        final Location tileLoc = tile.getLocation();

        if ((playerLoc == null) || (tileLoc == null)) {
            // something is very wrong. Put this entry back into the unchecked
            // list and try it again later.
            reportUnchecked(key);
            return true;
        }

        /*
         * And now we start with the inside check. In case the tile is below the
         * level of the player its never hidden for sure.
         */
        if (tileLoc.getScZ() <= playerLoc.getScZ()) {
            if (tile.isHidden()) {
                tile.setHidden(false);
                addAllBelow(tileLoc, playerLoc.getScZ() - 2);
                addAllNeighbours(tileLoc);
            }
            return true;
        }

        /*
         * Now generate the index in the list of inside states and check if the
         * tile is on a level that is hidden or not. If the tile is on such a
         * level it does not mean for sure that it really needs to be hidden.
         */
        final int insideIndex = tileLoc.getScZ() - playerLoc.getScZ() - 1;

        if ((insideIndex < 0) || (insideIndex > 1)) {
            LOGGER.warn("Invalid inside index: " + insideIndex);
            return true;
        }

        /*
         * Tile is not on a inside level. In case its hidden, show it again.
         */
        if (!insideStates[insideIndex]) {
            if (tile.isHidden()) {
                tile.setHidden(false);
                addAllBelow(tileLoc, playerLoc.getScZ() - 2);
                addAllAbove(tileLoc, playerLoc.getScZ() + 2);
                addAllNeighbours(tileLoc);
            }
            return true;
        }

        /*
         * Now check if the tile is directly above the player. In this case it
         * needs to be hidden.
         */
        if ((tileLoc.getScX() == playerLoc.getScX())
            && (tileLoc.getScY() == playerLoc.getScY())
            && (tileLoc.getScZ() > playerLoc.getScZ()) && !tile.isHidden()) {
            tile.setHidden(true);
            addAllBelow(tileLoc, playerLoc.getScZ() - 2);
            addAllAbove(tileLoc, playerLoc.getScZ() + 2);
            addAllNeighbours(tileLoc);
            return true;
        }

        if (!tile.isHidden() && searchHiddenNeighbour(tileLoc)) {
            tile.setHidden(true);
            addAllBelow(tileLoc, playerLoc.getScZ() - 2);
            addAllAbove(tileLoc, playerLoc.getScZ() + 2);
            addAllNeighbours(tileLoc);
            return true;
        }

        return false;
    }

    /**
     * Do the obstruction check of a tile. So this means that all tiles are
     * hidden in case there is a tile above them and they are fully invisible
     * anyway. Tiles below the level of the player are removed, since they won't
     * be displayed ever anyway. The tiles at or above the players location are
     * possibly shown in case the char steps into a building or something.
     * 
     * @param tile the tile that is checked
     * @param key the location key of the checked tile
     * @return <code>true</code> in case the tile got removed
     */
    private boolean checkObstruction(final MapTile tile, final long key) {
        final Location playerLoc = Game.getPlayer().getLocation();
        final Location tileLoc = tile.getLocation();

        final int topLimit = playerLoc.getScZ() + 2;

        int currX = tileLoc.getScX();
        int currY = tileLoc.getScY();
        int currZ = tileLoc.getScZ();

        while (currZ < topLimit) {
            currX -= GameMap.TILE_PERSPECTIVE_OFFSET;
            currY += GameMap.TILE_PERSPECTIVE_OFFSET;
            currZ++;

            final boolean remove = (currZ < (topLimit - 2));
            final MapTile foundTile = parent.getMapAt(currX, currY, currZ);

            if ((foundTile != null) && foundTile.isOpaque()
                && !foundTile.isHidden()) {
                if (remove) {
                    parent.removeTile(key);
                    return true;
                }
                tile.setObstructed(true);
                return false;
            }
        }
        if (tile.isObstructed()) {
            tile.setObstructed(false);
            addAllBelow(tileLoc, playerLoc.getScZ() - 2);
        }
        return false;
    }

    /**
     * Perform a check if the player character is inside a building. In case the
     * inside status differs for any level from the state before the check, all
     * tiles are added to the list of tiles that need to be checked once more.
     */
    private void performInsideCheck() {
        if (!checkInside) {
            return;
        }

        checkInside = false;

        final Location playerLoc = Game.getPlayer().getLocation();
        final int currX = playerLoc.getScX();
        final int currY = playerLoc.getScY();
        int currZ = playerLoc.getScZ();
        boolean nowOutside = false;
        boolean isInside = false;

        for (int i = 0; i < 2; ++i) {
            currZ++;
            if (isInside || (parent.isMapAt(currX, currY, currZ))) {
                if (!insideStates[i]) {
                    insideStates[i] = true;
                    synchronized (unchecked) {
                        unchecked.add(Location.getKey(currX, currY, currZ));
                    }
                }
                isInside = true;
            } else {
                if (insideStates[i]) {
                    insideStates[i] = false;
                    nowOutside = true;
                }
            }
        }

        /*
         * If one of the values turned from inside to outside, all tiles are
         * added to the list to be checked again.
         */
        if (nowOutside) {
            synchronized (unchecked) {
                unchecked.clear();
                parent.processTiles(this);
            }

        }

        Game.getWeather().setOutside(!isInside);
    }

    /**
     * Process the unchecked tiles that are still needed to be done.
     * 
     * @return <code>true</code> in case a tile was handled.
     */
    private boolean processUnchecked() {
        long key;
        synchronized (unchecked) {
            final int size = unchecked.size();
            if (size == 0) {
                return false;
            }
            key = unchecked.removeAt(size - 1);
        }

        final MapTile tile = parent.getMapAt(key);

        // no tile found, lets quit here
        if (tile == null) {
            return true;
        }

        // clipping check
        if (checkClipping(tile, key)) {
            return true;
        }

        // obstruction check
        if (checkObstruction(tile, key)) {
            return true;
        }

        // hidden check
        if (checkHidden(tile, key)) {
            return true;
        }

        return true;
    }

    /**
     * Search all surrounding tiles and the tile below and look for a tile that
     * is currently hidden.
     * 
     * @param searchLoc the location where the search starts
     * @return <code>true</code> in case a hidden tile was found
     */
    private boolean searchHiddenNeighbour(final Location searchLoc) {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if ((x == 0) && (y == 0)) {
                    continue;
                }
                final MapTile foundTile =
                    parent.getMapAt(searchLoc.getScX() + x, searchLoc.getScY()
                        + y, searchLoc.getScZ());
                if ((foundTile != null) && foundTile.isHidden()) {
                    return true;
                }
            }
        }

        MapTile foundTile =
            parent.getMapAt(searchLoc.getScX(), searchLoc.getScY(),
                searchLoc.getScZ() + 1);
        if ((foundTile != null) && foundTile.isHidden()) {
            return true;
        }

        foundTile =
            parent.getMapAt(searchLoc.getScX(), searchLoc.getScY(),
                searchLoc.getScZ() - 1);
        if ((foundTile != null) && foundTile.isHidden()) {
            return true;
        }

        return false;
    }

    /**
     * Check if the map processor can fetch some additional work to do in case
     * he does not have enough to do.
     * 
     * @return <code>true</code> in case there is more work to be done now
     *         available
     */
    private boolean workloadCheck() {
        if (fullCheckNeeded) {
            synchronized (unchecked) {
                parent.processTiles(this);
            }
            fullCheckNeeded = false;
            return true;
        }
        return false;
    }
}
