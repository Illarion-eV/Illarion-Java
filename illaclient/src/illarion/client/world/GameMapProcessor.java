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
import illarion.common.types.Location;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
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
    @Nonnull
    private static final Logger LOGGER = Logger.getLogger(GameMapProcessor.class);

    /**
     * The map that is handled by this processor instance.
     */
    @Nonnull
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
    @Nonnull
    private final BlockingDeque<Long> unchecked;

    /**
     * Constructor for a new instance of the game map processor. This processor will be bound to one map.
     *
     * @param parentMap the game map that is processed by this processor
     */
    @SuppressWarnings("nls")
    public GameMapProcessor(@Nonnull final GameMap parentMap) {
        super("Map Processor");
        setDaemon(true);
        setPriority(MIN_PRIORITY);
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
            synchronized (this) {
                notify();
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
            try {
                hasAndProcessUnchecked();
                yield();
            } catch (@Nonnull final InterruptedException e) {
                LOGGER.info("Map processor got interrupted!");
            }
        }
    }

    /**
     * Process the unchecked tiles that are still needed to be done.
     */
    private void hasAndProcessUnchecked() throws InterruptedException {
        final long key = unchecked.takeFirst();

        while (pauseLoop) {
            synchronized (this) {
                wait();
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
    }

    /**
     * Do the clipping check of a tile.
     *
     * @param tile the tile that is checked
     * @param key  the location key of the checked tile
     * @return {@code true} in case the tile was clipped away
     */
    private boolean checkClipping(@Nonnull final MapTile tile, final long key) {
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
     * Add a key of a map location to the processor that contains a location on the map that was yet unchecked.
     *
     * @param key the key of the location that needs to be checked
     */
    public void reportUnchecked(final long key) {
        addLocationToUnchecked(key);
    }

    /**
     * Add all tiles in the visible perspective below one location to the list of unchecked tiles again.
     *
     * @param searchLoc the location the search starts add. This location is not added to the list of unchecked tiles
     * @param limit     the limit the Z coordinate is not going below
     */
    private void addAllBelow(@Nonnull final Location searchLoc, final int limit) {
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
    private void addAllAbove(@Nonnull final Location searchLoc, final int limit) {
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
            } catch (@Nonnull final InterruptedException e) {
                LOGGER.error("Error while trying add dirty tile.", e);
            }
        }
    }

    /**
     * Add all tiles surrounding this location and the tile above to the list of unchecked tiles.
     *
     * @param searchLoc the location of the start of the search.
     */
    private void addAllNeighbours(@Nonnull final Location searchLoc) {
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
