/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.common.graphics;

import org.jetbrains.annotations.Contract;

/**
 * This class is used to store some general information about a tile.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class TileInfo {
    /**
     * The mask value used to fetch the base tile ID.
     */
    private static final int BASE_MASK = 0x001F;

    /**
     * The mask value used to fetch the overlay tile ID.
     */
    private static final int OVERLAY_MASK = 0x03E0;

    /**
     * The mask value used to fetch the mask ID.
     */
    private static final int SHAPE_MASK = 0xFC00;
    /**
     * The color of the tile on the map.
     */
    private final int mapColor;

    /**
     * This stores if the tile is opaque or transparent. This has some effect
     * how the tiles below are handled.
     */
    private final boolean opaque;

    /**
     * This creates a instance of the TileInfo and sets the values.
     *
     * @param color the color of the tile on the map
     * @param isOpaque {@code true} in case the tile is opaque
     */
    public TileInfo(int color, boolean isOpaque) {
        mapColor = color;
        opaque = isOpaque;
    }

    /**
     * Get the base tile ID from a tile ID.
     *
     * @param id the full tile ID
     * @return the base tile ID
     */
    @Contract(pure = true)
    public static int getBaseID(int id) {
        if ((id & SHAPE_MASK) != 0) {
            return id & BASE_MASK;
        }
        return id;
    }

    /**
     * Get the overlay tile ID from a tile ID.
     *
     * @param id the full tile ID
     * @return the overlay tile ID
     */
    @Contract(pure = true)
    public static int getOverlayID(int id) {
        if ((id & SHAPE_MASK) != 0) {
            return (id & OVERLAY_MASK) >> 5;
        }
        return 0;
    }

    /**
     * Check if there is a overlay tile and a shape encoded in this tile ID.
     *
     * @param id the full tile ID
     * @return {@code true} in case there is a overlay encoded in the tile ID
     */
    @Contract(pure = true)
    public static boolean hasOverlay(int id) {
        return getShapeId(id) > 0;
    }

    /**
     * Get the shape ID from a tile ID.
     *
     * @param id the full tile ID
     * @return the shape ID
     */
    @Contract(pure = true)
    public static int getShapeId(int id) {
        return (id & SHAPE_MASK) >> 10;
    }

    /**
     * Get the color of this tile.
     *
     * @return the simplified color code of this tile
     */
    @Contract(pure = true)
    public int getMapColor() {
        return mapColor;
    }

    /**
     * Check if the tile is opaque.
     *
     * @return {@code true} in case the tile is opaque
     */
    @Contract(pure = true)
    public boolean isOpaque() {
        return opaque;
    }
}
