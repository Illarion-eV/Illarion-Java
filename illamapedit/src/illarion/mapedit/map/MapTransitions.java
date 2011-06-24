/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.map;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;

import illarion.mapedit.graphics.Overlay;
import illarion.mapedit.graphics.OverlayFactory;
import illarion.mapedit.graphics.Tile;

import illarion.common.util.Location;

/**
 * This class is used to calculate the proper overlays to be placed.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MapTransitions {
    /**
     * The singleton instance of this class.
     */
    private static final MapTransitions INSTANCE = new MapTransitions();

    /**
     * List of the amount of tiles found of each type.
     */
    private final TIntIntHashMap analysedTiles = new TIntIntHashMap();

    /**
     * Helper list that stores the references to the tiles around the checked
     * tile.
     */
    private final MapTile[] checkTiles = new MapTile[8];

    /**
     * List of the found tiles.
     */
    private final TIntArrayList foundTiles = new TIntArrayList();

    /**
     * This list stores the definitions for all masks avaiable. The content of
     * the list is build up at the first usage of this class.
     */
    private final int[] transitions = new int[28];

    /**
     * Private constructor that prepares the required values and ensures that no
     * instance but the singleton instance is created.
     */
    @SuppressWarnings("nls")
    private MapTransitions() {
        /*
         * Build up the transions list. The definion values store the locations
         * around the actual tile where the tile with the largest layer value is
         * located. Values start at the north location with bit 0 and go around
         * the center tile clockwise.
         */
        // +-+-+-+
        // |7|0|1|
        // +-+-+-+
        // |6|C|2|
        // +-+-+-+
        // |5|4|3|
        // +-+-+-+

        // Transition 0 - Tiles at 0 and 7
        transitions[0] = (1 << 0);
        transitions[0] |= (1 << 7);

        // Transition 1 - Tiles at 0 and 1
        transitions[1] = (1 << 0);
        transitions[1] |= (1 << 1);

        // Transition 2 - Tiles at 6 and 7
        transitions[2] = (1 << 6);
        transitions[2] |= (1 << 7);

        // Transition 3 - Tiles at 5 and 6
        transitions[3] = (1 << 5);
        transitions[3] |= (1 << 6);

        // Transition 4 - Tiles at 4 and 5
        transitions[4] = (1 << 4);
        transitions[4] |= (1 << 5);

        // Transition 5 - Tiles at 3 and 4
        transitions[5] = (1 << 3);
        transitions[5] |= (1 << 4);

        // Transition 6 - Tiles at 1 and 2
        transitions[6] = (1 << 1);
        transitions[6] |= (1 << 2);

        // Transition 7 - Tiles at 2 and 3
        transitions[7] = (1 << 2);
        transitions[7] |= (1 << 3);

        // Transition 8 - Tiles at 0, 1 and 7
        transitions[8] = (1 << 0);
        transitions[8] |= (1 << 1);
        transitions[8] |= (1 << 7);

        // Transition 9 - Tiles at 5, 6 and 7
        transitions[9] = (1 << 5);
        transitions[9] |= (1 << 6);
        transitions[9] |= (1 << 7);

        // Transition 10 - Tiles at 3, 4 and 5
        transitions[10] = (1 << 3);
        transitions[10] |= (1 << 4);
        transitions[10] |= (1 << 5);

        // Transition 11 - Tiles at 1, 2 and 3
        transitions[11] = (1 << 1);
        transitions[11] |= (1 << 2);
        transitions[11] |= (1 << 3);

        // Transition 12 - Tiles at 0, 6 and 7
        transitions[12] = (1 << 0);
        transitions[12] |= (1 << 6);
        transitions[12] |= (1 << 7);

        // Transition 13 - Tiles at 0, 1 and 2
        transitions[13] = (1 << 0);
        transitions[13] |= (1 << 1);
        transitions[13] |= (1 << 2);

        // Transition 14 - Tiles at 4, 5 and 6
        transitions[14] = (1 << 4);
        transitions[14] |= (1 << 5);
        transitions[14] |= (1 << 6);

        // Transition 15 - Tiles at 2, 3 and 4
        transitions[15] = (1 << 2);
        transitions[15] |= (1 << 3);
        transitions[15] |= (1 << 4);

        // Transition 16 - Tiles at 0, 1, 6 and 7
        transitions[16] = (1 << 0);
        transitions[16] |= (1 << 1);
        transitions[16] |= (1 << 6);
        transitions[16] |= (1 << 7);

        // Transition 17 - Tiles at 0, 1, 2 and 7
        transitions[17] = (1 << 0);
        transitions[17] |= (1 << 1);
        transitions[17] |= (1 << 2);
        transitions[17] |= (1 << 7);

        // Transition 18 - Tiles at 0, 5, 6 and 7
        transitions[18] = (1 << 0);
        transitions[18] |= (1 << 5);
        transitions[18] |= (1 << 6);
        transitions[18] |= (1 << 7);

        // Transition 19 - Tiles at 4, 5, 6 and 7
        transitions[19] = (1 << 4);
        transitions[19] |= (1 << 5);
        transitions[19] |= (1 << 6);
        transitions[19] |= (1 << 7);

        // Transition 20 - Tiles at 2, 3, 4 and 5
        transitions[20] = (1 << 2);
        transitions[20] |= (1 << 3);
        transitions[20] |= (1 << 4);
        transitions[20] |= (1 << 5);

        // Transition 21 - Tiles at 3, 4, 5 and 6
        transitions[21] = (1 << 3);
        transitions[21] |= (1 << 4);
        transitions[21] |= (1 << 5);
        transitions[21] |= (1 << 6);

        // Transition 22 - Tiles at 0, 1, 2 and 3
        transitions[22] = (1 << 0);
        transitions[22] |= (1 << 1);
        transitions[22] |= (1 << 2);
        transitions[22] |= (1 << 3);

        // Transition 23 - Tiles at 1, 2, 3 and 4
        transitions[23] = (1 << 1);
        transitions[23] |= (1 << 2);
        transitions[23] |= (1 << 3);
        transitions[23] |= (1 << 4);

        // Transition 24 - Tiles at 0, 1, 5, 6 and 7
        transitions[24] = (1 << 0);
        transitions[24] |= (1 << 1);
        transitions[24] |= (1 << 5);
        transitions[24] |= (1 << 6);
        transitions[24] |= (1 << 7);

        // Transition 25 - Tiles at 0, 1, 2, 3 and 7
        transitions[25] = (1 << 0);
        transitions[25] |= (1 << 1);
        transitions[25] |= (1 << 2);
        transitions[25] |= (1 << 3);
        transitions[25] |= (1 << 7);

        // Transition 26 - Tiles at 3, 4, 5, 6 and 7
        transitions[26] = (1 << 3);
        transitions[26] |= (1 << 4);
        transitions[26] |= (1 << 5);
        transitions[26] |= (1 << 6);
        transitions[26] |= (1 << 7);

        // Transition 27 - Tiles at 1, 2, 3, 4 and 5
        transitions[27] = (1 << 1);
        transitions[27] |= (1 << 2);
        transitions[27] |= (1 << 3);
        transitions[27] |= (1 << 4);
        transitions[27] |= (1 << 5);
    }

    /**
     * Get the instance of the map transitions utility.
     * 
     * @return the singleton instance of this class
     */
    public static MapTransitions getInstance() {
        return INSTANCE;
    }

    /**
     * Check a single tile for the need of transitions.
     * 
     * @param loc the location of the tile to check
     */
    public void checkTile(final Location loc) {
        placeTransition(loc);
    }

    /**
     * Check the tile at the location and all 8 tiles around it also.
     * 
     * @param loc the location to check
     */
    public void checkTileAndSurround(final Location loc) {
        checkTile(loc);
        final Location tempLoc = Location.getInstance();

        tempLoc.setSC(loc.getScX() - 1, loc.getScY() - 1, loc.getScZ());
        checkTile(tempLoc);

        tempLoc.setSC(loc.getScX(), loc.getScY() - 1, loc.getScZ());
        checkTile(tempLoc);

        tempLoc.setSC(loc.getScX() + 1, loc.getScY() - 1, loc.getScZ());
        checkTile(tempLoc);

        tempLoc.setSC(loc.getScX() - 1, loc.getScY(), loc.getScZ());
        checkTile(tempLoc);

        tempLoc.setSC(loc.getScX() + 1, loc.getScY(), loc.getScZ());
        checkTile(tempLoc);

        tempLoc.setSC(loc.getScX() - 1, loc.getScY() + 1, loc.getScZ());
        checkTile(tempLoc);

        tempLoc.setSC(loc.getScX(), loc.getScY() + 1, loc.getScZ());
        checkTile(tempLoc);

        tempLoc.setSC(loc.getScX() + 1, loc.getScY() + 1, loc.getScZ());
        checkTile(tempLoc);

        tempLoc.recycle();
    }

    /**
     * Check the found tiles and generate a list with all tile IDs that occur at
     * least 2 times around the center tile. The IDs of the found tiles are
     * stored in {@link #foundTiles} then.
     */
    private void analyseTiles() {
        analysedTiles.clear();
        foundTiles.clear();
        for (int i = 0; i < 8; i++) {
            if (checkTiles[i] == null) {
                continue;
            }
            final int tileId = Tile.baseID(checkTiles[i].getTileId());
            if (analysedTiles.contains(tileId)) {
                analysedTiles.put(tileId, analysedTiles.get(tileId) + 1);
            } else {
                analysedTiles.put(tileId, 1);
                foundTiles.add(tileId);
            }
        }
        if (foundTiles.isEmpty()) {
            return;
        }
        int length = foundTiles.size();
        for (int i = 0; i < length; i++) {
            final int tileId = foundTiles.get(i);
            if (analysedTiles.get(tileId) < 2) {
                foundTiles.removeAt(i);
                analysedTiles.remove(tileId);
                length--;
                i--;
            }
        }
    }

    /**
     * Create the mask for one tile ID.
     * 
     * @param id the ID of the tile
     * @return the mask for a shape for this ID
     */
    private int buildMask(final int id) {
        int mask = 0;
        for (int i = 0; i < 8; i++) {
            if (checkTiles[i] == null) {
                continue;
            }
            if (checkTiles[i].getTileId() == id) {
                mask |= (1 << i);
            }
        }
        return mask;
    }

    /**
     * Clean up the tiles list and remove all tiles that equal the ID of the
     * tile in the center or that have a lower layer then the tile in the
     * center. Also all tiles with ID 0 or a ID over 31 are removed.
     * 
     * @param centerTileID the ID of the tile in the center
     */
    private void cleanupTiles(final int centerTileID) {
        int centerLayer = 0;
        if (OverlayFactory.getInstance().prototypeExists(centerTileID)) {
            final Overlay ovl =
                OverlayFactory.getInstance().getPrototype(centerTileID);
            if (ovl != null) {
                centerLayer = ovl.getLayer();
            }
        }

        for (int i = 0; i < 8; i++) {
            if (checkTiles[i] == null) {
                continue;
            }
            final int tileId = Tile.baseID(checkTiles[i].getTileId());
            if ((tileId == 0) || (tileId > 31) || (tileId == centerTileID)) {
                checkTiles[i] = null;
                continue;
            }
            Overlay tileOvl = null;
            if (OverlayFactory.getInstance().prototypeExists(tileId)) {
                tileOvl = OverlayFactory.getInstance().getPrototype(tileId);
            }
            if (tileOvl == null) {
                checkTiles[i] = null;
                continue;
            }
            if (tileOvl.getLayer() <= centerLayer) {
                checkTiles[i] = null;
                continue;
            }
        }
    }

    /**
     * Find the tile with the largest layer and remove it from the list.
     * 
     * @return the tile ID of the tile with the largest layer that was found
     */
    @SuppressWarnings("null")
    private int findAndRemoveHighestLayer() {
        final int length = foundTiles.size();
        int largestOffset = 0;
        int largestLayer = 0;
        int largestID = 0;
        for (int i = 0; i < length; i++) {
            final int tileId = foundTiles.get(i);
            Overlay tileOvl = null;
            if (OverlayFactory.getInstance().prototypeExists(tileId)) {
                tileOvl = OverlayFactory.getInstance().getPrototype(tileId);
            }
            if (tileOvl.getLayer() > largestLayer) {
                largestLayer = tileOvl.getLayer();
                largestOffset = i;
                largestID = tileOvl.getId();
            }
        }

        foundTiles.removeAt(largestOffset);
        return largestID;
    }

    /**
     * Find the largest shake that fits the mask.
     * 
     * @param mask the mask value
     * @return the ID of the shape that fits the mask
     */
    private int findMask(final int mask) {
        for (int i = transitions.length - 1; i >= 0; i--) {
            if (transitions[i] == mask) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Place a transition at one tile in case its needed.
     * 
     * @param loc the location where a transition could be placed
     */
    private void placeTransition(final Location loc) {
        final MapTile centerTile = MapStorage.getInstance().getMapTile(loc);
        if (centerTile == null) {
            return;
        }

        final int centerTileId = Tile.baseID(centerTile.getTileId());
        if ((centerTileId == 0) || (centerTileId > 31)) {
            return;
        }

        // at this point we have a tile that could possibly get a overlay
        populateTiles(loc);
        cleanupTiles(centerTileId);
        analyseTiles();

        while (!foundTiles.isEmpty()) {
            final int testId = findAndRemoveHighestLayer();
            final int mask = buildMask(testId);
            final int maskId = findMask(mask);
            if (maskId == -1) {
                continue;
            }
            final int tileId =
                Tile.generateTileId(centerTileId, testId, maskId + 1);
            centerTile.setTileId(tileId);
            return;
        }
        centerTile.setTileId(centerTileId);
    }

    /**
     * Fill the {@link #checkTiles} array with the 8 tiles around the center
     * location.
     * 
     * @param centerLoc the center location
     */
    private void populateTiles(final Location centerLoc) {
        final MapStorage mapSt = MapStorage.getInstance();
        final Location searchLoc = Location.getInstance();

        searchLoc.setSC(centerLoc.getScX(), centerLoc.getScY() - 1,
            centerLoc.getScZ());
        checkTiles[0] = mapSt.getMapTile(searchLoc);

        searchLoc.setSC(centerLoc.getScX() + 1, centerLoc.getScY() - 1,
            centerLoc.getScZ());
        checkTiles[1] = mapSt.getMapTile(searchLoc);

        searchLoc.setSC(centerLoc.getScX() + 1, centerLoc.getScY(),
            centerLoc.getScZ());
        checkTiles[2] = mapSt.getMapTile(searchLoc);

        searchLoc.setSC(centerLoc.getScX() + 1, centerLoc.getScY() + 1,
            centerLoc.getScZ());
        checkTiles[3] = mapSt.getMapTile(searchLoc);

        searchLoc.setSC(centerLoc.getScX(), centerLoc.getScY() + 1,
            centerLoc.getScZ());
        checkTiles[4] = mapSt.getMapTile(searchLoc);

        searchLoc.setSC(centerLoc.getScX() - 1, centerLoc.getScY() + 1,
            centerLoc.getScZ());
        checkTiles[5] = mapSt.getMapTile(searchLoc);

        searchLoc.setSC(centerLoc.getScX() - 1, centerLoc.getScY(),
            centerLoc.getScZ());
        checkTiles[6] = mapSt.getMapTile(searchLoc);

        searchLoc.setSC(centerLoc.getScX() - 1, centerLoc.getScY() - 1,
            centerLoc.getScZ());
        checkTiles[7] = mapSt.getMapTile(searchLoc);

        searchLoc.recycle();
    }
}
