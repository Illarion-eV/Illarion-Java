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

/**
 * Utility class that holds a few constants that are needed to define the map
 * correctly.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class MapConstants {
    /**
     * Bitmask to filter the base tile id from a tile ID.
     */
    public static final int BASE_MASK = 0x001F;
    /**
     * Bitmask to filter the overlay tile id from a tile ID.
     */
    public static final int OVERLAY_MASK = 0x03E0;
    /**
     * Bitmask to filter the overlay shape id from a tile ID.
     */
    public static final int SHAPE_MASK = 0xFC00;
    /**
     * Step size in X direction. Means how much pixels the map as to scroll to
     * reach the next tile. Since the tiles on one row are optical half shifted
     * against each other, the step size is always half of the tile width in X
     * direction.
     */
    public static final int STEP_X;

    /**
     * Step size in Y direction. Means how much pixels the map as to scroll to
     * reach the next tile. Since the tiles on one row are optical half shifted
     * against each other, the step size is always half of the tile height in Y
     * direction.
     */
    public static final int STEP_Y;

    /**
     * Height of a tile in pixels. This should fit to the size of the images to
     * ensure that it looks good.
     */
    public static final int TILE_H = 37;

    /**
     * Width of a tile in pixels. This should fit to the size of the images to
     * ensure that it looks good.
     */
    public static final int TILE_W = 76;

    static {
        STEP_X = TILE_W / 2;
        STEP_Y = (TILE_H + 1) / 2;
    }

    /**
     * Private constructor to ensure that nothing can create a instance of this
     * utility class.
     */
    private MapConstants() {
        // private constructor does nothing at all
    }

}
