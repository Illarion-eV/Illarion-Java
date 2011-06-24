/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.graphics;

import javolution.lang.Immutable;

/**
 * This class is used to store some general informations about a tile.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class TileInfo implements Immutable {
    /**
     * The color of the tile on the map.
     */
    private final int mapColor;

    /**
     * The movement costs of this tile. This will be taken into consideration
     * when performing a path finding operation.
     */
    private final int movementCost;

    /**
     * This stores if the tile is opaque or transparent. This has some effect
     * how the tiles below are handled.
     */
    private final boolean opaque;

    /**
     * This creates a instance of the TileInfo and sets the values.
     * 
     * @param color the color of the tile on the map
     * @param cost the cost to move over this tile
     * @param isOpaque <code>true</code> in case the tile is opaque
     */
    public TileInfo(final int color, final int cost, final boolean isOpaque) {
        mapColor = color;
        movementCost = cost;
        opaque = isOpaque;
    }

    /**
     * Get the color of this tile.
     * 
     * @return the simplified color code of this tile
     */
    public int getMapColor() {
        return mapColor;
    }

    /**
     * Get how much is costs to move over this tile.
     * 
     * @return the movement costs of this tile
     */
    public int getMovementCost() {
        return movementCost;
    }

    /**
     * Check if the tile is opaque.
     * 
     * @return <code>true</code> in case the tile is opaque
     */
    public boolean isOpaque() {
        return opaque;
    }
}
