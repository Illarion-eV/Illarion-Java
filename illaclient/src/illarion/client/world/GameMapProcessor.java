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

import illarion.client.graphics.MapDisplayManager;
import illarion.common.annotation.NonNull;
import illarion.common.types.Location;
import net.jcip.annotations.ThreadSafe;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * This class checks the consistency of the map, the map, handles the fading out of map layers if needed and
 * optimizes the map. Clipping and the removal of unneeded map data is handled also by this.
 * <p>
 * This handler is a own thread, so its at times expensive, actions do not disturb the main loop.
 * </p>
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
public final class GameMapProcessor extends Thread {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    @NonNull
    private static final Logger LOGGER = Logger.getLogger(GameMapProcessor.class);

    /**
     * This flag is {@code true} while the inside check is considered as done. Once its toggles to {@code false} the
     * map processor will check again if the current location is really inside.
     */
    private boolean checkInsideDone;

    /**
     * The array of values if the character is inside or not. It stores the values for every level. So if the
     * character is inside a 2 level high cave the second value will be true, but the first one false.
     */
    @NonNull
    private final boolean[] insideStates = new boolean[2];

    /**
     * The map that is handled by this processor instance.
     */
    @NonNull
    private final GameMap parent;

    /**
     * This variable set to {@code true} causes the main loop to hold until its resumed with {@link #start()}.
     */
    private boolean pauseLoop;

    /**
     * This variable is {@code true} as long as the thread is supposed to run. The thread will quit as soon as
     * possible once it switches to {@code false}.
     */
    private boolean running;

    /**
     * The list of location keys that were yet not checked by the processor.
     */
    @NonNull
    private final BlockingDeque<Long> unchecked;

    /**
     * Constructor for a new instance of the game map processor. This processor will be bound to one map.
     *
     * @param parentMap the game map that is processed by this processor
     */
    @SuppressWarnings("nls")
    public GameMapProcessor(@NonNull final GameMap parentMap) {
        super("Map Processor");
        parent = parentMap;
        unchecked = new LinkedBlockingDeque<Long>();
        running = false;
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
     * The main method of the game map processor.
     */
    @SuppressWarnings("nls")
    @Override
    public void run() {
        while (running) {
            performInsideCheck();

            try {
                hasAndProcessUnchecked();
            } catch (final InterruptedException e) {
                LOGGER.info("Map processor got interrupted!");
            }
        }
    }

    /**
     * Perform a check if the player character is inside a building. In case the inside status differs for any level
     * from the state before the check, all tiles are added to the list of tiles that need to be checked once more.
     */
    private void performInsideCheck() {
        if (checkInsideDone) {
            return;
        }

        checkInsideDone = true;

        final Location playerLoc = World.getPlayer().getLocation();
        final int currX = playerLoc.getScX();
        final int currY = playerLoc.getScY();
        int currZ = playerLoc.getScZ();
        boolean nowOutside = false;
        boolean isInside = false;

        int lowestNowOutside = Integer.MAX_VALUE;
        int highestNowOutside = Integer.MIN_VALUE;

        for (int i = 0; i < 2; ++i) {
            currZ++;
            if (isInside || parent.isMapAt(currX, currY, currZ)) {
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
                    lowestNowOutside = Math.min(currZ, lowestNowOutside);
                    highestNowOutside = Math.max(currZ, highestNowOutside);
                }
            }
        }

        /*
         * If one of the values turned from inside to outside, all tiles are added to the list to be checked again.
         */
        if (nowOutside) {
            unchecked.clear();
            final List<MapTile> tileStorage = new LinkedList<MapTile>();
            parent.getTiles(tileStorage, lowestNowOutside, highestNowOutside);
            for (final MapTile tile : tileStorage) {
                addLocationToUnchecked(tile.getLocation().getKey());
            }
        }

        World.getWeather().setOutside(!isInside);
    }

    /**
     * Process the unchecked tiles that are still needed to be done.
     */
    private void hasAndProcessUnchecked() throws InterruptedException {
        final long key = unchecked.takeFirst();

        while (pauseLoop) {
            synchronized (unchecked) {
                unchecked.wait();
            }
        }

        final MapTile tile = parent.getMapAt(key);

        // no tile found, lets quit here
        if (tile == null) {
            return;
        }

        // clipping check
        if (checkClipping(tile, key)) {
            return;
        }

        // obstruction check
        if (checkObstruction(tile, key)) {
            return;
        }

        // hidden check
        checkHidden(tile, key);
    }

    /**
     * Do the clipping check of a tile.
     *
     * @param tile the tile that is checked
     * @param key  the location key of the checked tile
     * @return {@code true} in case the tile was clipped away
     */
    private boolean checkClipping(@NonNull final MapTile tile, final long key) {
        if (!World.getPlayer().hasValidLocation()) {
            return false;
        }

        final Location playerLoc = World.getPlayer().getLocation();
        final Location tileLoc = tile.getLocation();

        /*
         * Start checking the clipping of the tiles. In case a tile is found outside the clipping range, its deleted.
         */
        if ((playerLoc.getScZ() + 2) < tileLoc.getScZ()) {
            parent.removeTile(key);
            return true;
        }

        if ((playerLoc.getScZ() - 2) > tileLoc.getScZ()) {
            parent.removeTile(key);
            return true;
        }

        final MapDimensions mapDim = MapDimensions.getInstance();

        if ((playerLoc.getCol() + mapDim.getClippingOffsetLeft()) > tileLoc
                .getCol()) {
            parent.removeTile(key);
            return true;
        }

        if ((playerLoc.getCol() + mapDim.getClippingOffsetRight()) < tileLoc
                .getCol()) {
            parent.removeTile(key);
            return true;
        }

        final int level = Math.abs(tileLoc.getScZ() - playerLoc.getScZ()) * 6;

        if ((playerLoc.getRow() + mapDim.getClippingOffsetTop()) < (tileLoc
                .getRow() - level)) {
            parent.removeTile(key);
            return true;
        }

        if ((playerLoc.getRow() + mapDim.getClippingOffsetBottom()) > (tileLoc
                .getRow() + level)) {
            parent.removeTile(key);
            return true;
        }

        return false;
    }

    /**
     * Do the obstruction check of a tile. So this means that all tiles are hidden in case there is a tile above them
     * and they are fully invisible anyway. Tiles below the level of the player are removed,
     * since they won't be displayed ever anyway. The tiles at or above the players location are possibly shown in
     * case the char steps into a building or something.
     *
     * @param tile the tile that is checked
     * @param key  the location key of the checked tile
     * @return {@code true} in case the tile got removed
     */
    private boolean checkObstruction(final MapTile tile, final long key) {
        final Location playerLoc = World.getPlayer().getLocation();
        final Location tileLoc = tile.getLocation();

        final int topLimit = playerLoc.getScZ() + 2;

        int currX = tileLoc.getScX();
        int currY = tileLoc.getScY();
        int currZ = tileLoc.getScZ();

        while (currZ < topLimit) {
            currX -= MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
            currY += MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
            currZ++;

            final boolean remove = currZ < (topLimit - 2);
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
     * Check if the tile needs to be hidden because the player is inside a cave or a house or something like this.
     *
     * @param tile the tile that is checked
     * @param key  the location key of the checked tile
     * @return {@code true} in case the tile was handled
     */
    @SuppressWarnings("nls")
    private boolean checkHidden(@NonNull final MapTile tile, final long key) {
        final Location playerLoc = World.getPlayer().getLocation();
        final Location tileLoc = tile.getLocation();

        if (playerLoc == null) {
            // something is very wrong. Put this entry back into the unchecked list and try it again later.
            reportUnchecked(key);
            return true;
        }

        /*
         * And now we start with the inside check. In case the tile is below the level of the player its never hidden
         * for sure.
         */
        if (tileLoc.getScZ() <= playerLoc.getScZ()) {
            if (tile.isHidden()) {
                tile.setHidden(false);

                synchronized (unchecked) {
                    addAllBelow(tileLoc, playerLoc.getScZ() - 2);
                    addAllNeighbours(tileLoc);
                }
            }
            return true;
        }

        /*
         * Now generate the index in the list of inside states and check if the tile is on a level that is hidden or
         * not. If the tile is on such a level it does not mean for sure that it really needs to be hidden.
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
                synchronized (unchecked) {
                    addAllBelow(tileLoc, playerLoc.getScZ() - 2);
                    addAllAbove(tileLoc, playerLoc.getScZ() + 2);
                    addAllNeighbours(tileLoc);
                }
            }
            return true;
        }

        /*
         * Now check if the tile is directly above the player. In this case it needs to be hidden.
         */
        if ((tileLoc.getScX() == playerLoc.getScX()) && (tileLoc.getScY() == playerLoc.getScY())
                && (tileLoc.getScZ() > playerLoc.getScZ()) && !tile.isHidden()) {
            tile.setHidden(true);
            synchronized (unchecked) {
                addAllBelow(tileLoc, playerLoc.getScZ() - 2);
                addAllAbove(tileLoc, playerLoc.getScZ() + 2);
                addAllNeighbours(tileLoc);
            }
            return true;
        }

        if (!tile.isHidden() && hasHiddenNeighbour(tileLoc)) {
            tile.setHidden(true);
            synchronized (unchecked) {
                addAllBelow(tileLoc, playerLoc.getScZ() - 2);
                addAllAbove(tileLoc, playerLoc.getScZ() + 2);
                addAllNeighbours(tileLoc);
            }
            return true;
        }

        return false;
    }

    /**
     * Add a key of a map location to the processor that contains a location on the map that was yet unchecked.
     *
     * @param key the key of the location that needs to be checked
     */
    public void reportUnchecked(final long key) {
        addLocationToUnchecked(key);
    }

    /**
     * Search all surrounding tiles and the tile below and look for a tile that is currently hidden.
     *
     * @param searchLoc the location where the search starts
     * @return {@code true} in case a hidden tile was found
     */
    private boolean hasHiddenNeighbour(@NonNull final Location searchLoc) {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if ((x == 0) && (y == 0)) {
                    continue;
                }
                final MapTile foundTile = parent.getMapAt(searchLoc.getScX() + x, searchLoc.getScY() + y,
                        searchLoc.getScZ());
                if ((foundTile != null) && foundTile.isHidden()) {
                    return true;
                }
            }
        }

        MapTile foundTile = parent.getMapAt(searchLoc.getScX(), searchLoc.getScY(), searchLoc.getScZ() + 1);
        if ((foundTile != null) && foundTile.isHidden()) {
            return true;
        }

        //noinspection ReuseOfLocalVariable
        foundTile = parent.getMapAt(searchLoc.getScX(), searchLoc.getScY(), searchLoc.getScZ() - 1);

        return (foundTile != null) && foundTile.isHidden();
    }

    /**
     * Add all tiles in the visible perspective below one location to the list of unchecked tiles again.
     *
     * @param searchLoc the location the search starts add. This location is not added to the list of unchecked tiles
     * @param limit     the limit the Z coordinate is not going below
     */
    private void addAllBelow(@NonNull final Location searchLoc, final int limit) {
        int currX = searchLoc.getScX();
        int currY = searchLoc.getScY();
        int currZ = searchLoc.getScZ();

        while (currZ >= limit) {
            currX += MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
            currY -= MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
            currZ--;
            final long foundKey = Location.getKey(currX, currY, currZ);
            addLocationToUnchecked(foundKey);
        }
    }

    /**
     * Add all tiles in the visible perspective above one location to the list of unchecked tiles again.
     *
     * @param searchLoc the location the search starts add. This location is not added to the list of unchecked tiles
     * @param limit     the limit the Z coordinate is not going below
     */
    private void addAllAbove(@NonNull final Location searchLoc, final int limit) {
        int currX = searchLoc.getScX();
        int currY = searchLoc.getScY();
        int currZ = searchLoc.getScZ();

        while (currZ <= limit) {
            currX -= MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
            currY += MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
            currZ++;
            final long foundKey = Location.getKey(currX, currY, currZ);
            addLocationToUnchecked(foundKey);
        }
    }

    private void addLocationToUnchecked(final long locationKey) {
        if (!unchecked.contains(locationKey)) {
            try {
                if (!unchecked.offerLast(locationKey, 20, TimeUnit.MILLISECONDS)) {
                    LOGGER.error("Failed to add element to unchecked map queue.");
                }
            } catch (final InterruptedException e) {
                LOGGER.error("Error while trying add dirty tile.", e);
            }
        }
    }

    /**
     * Add all tiles surrounding this location and the tile above to the list of unchecked tiles.
     *
     * @param searchLoc the location of the start of the search.
     */
    private void addAllNeighbours(@NonNull final Location searchLoc) {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if ((x == 0) && (y == 0)) {
                    continue;
                }
                final long foundKey = Location.getKey(searchLoc.getScX() + x, searchLoc.getScY() + y,
                        searchLoc.getScZ());
                addLocationToUnchecked(foundKey);
            }
        }

        final long foundKey = Location.getKey(searchLoc.getScX(), searchLoc.getScY(), searchLoc.getScZ() + 1);
        addLocationToUnchecked(foundKey);
    }

    /**
     * This method causes the map processor to hold and stop processing data until the operations are resumed with
     * {@link #start()}.
     */
    public synchronized void pause() {
        pauseLoop = true;
    }

    /**
     * Do a inside check during the next run of the thread loop.
     */
    public void checkInside() {
        checkInsideDone = false;
    }

    /**
     * Clear the map, that should be done in case all tiles got removed from the map and the current checks need to
     * stop instantly.
     */
    public void clear() {
        unchecked.clear();
    }

    /**
     * Stop this thread as soon as possible.
     */
    public void saveShutdown() {
        running = false;
        interrupt();
    }
}
