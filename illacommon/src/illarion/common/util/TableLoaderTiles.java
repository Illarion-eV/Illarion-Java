/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import illarion.common.graphics.TileInfo;

import java.io.File;
import java.io.InputStream;

/**
 * This is a special implementation of the table loader that targets the tile table file.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class TableLoaderTiles extends TableLoader {
    /**
     * Tile mode value for simple tiles.
     */
    public static final int TILE_MODE_SIMPLE = 0;

    /**
     * Tile mode value for animated tiles.
     */
    public static final int TILE_MODE_ANIMATED = 1;

    /**
     * Tile mode value for variant tiles.
     */
    public static final int TILE_MODE_VARIANT = 2;
    /**
     * The column index of the minimap color of that tile in the resource table.
     */
    private static final int TB_COLOR = 5;

    /**
     * The column index of the walking cost of that tile in the resource table.
     */
    private static final int TB_COST = 9;

    /**
     * The column index of the frame count of that tile in the resource table.
     */
    private static final int TB_FRAME = 2;

    /**
     * The column index of the ground type ID of that tile in the resource table.
     */
    private static final int TB_GROUND_ID = 7;

    /**
     * The column index of the display mode of that tile in the resource table.
     */
    private static final int TB_MODE = 3;

    /**
     * The column index of the file name of that tile in the resource table.
     */
    private static final int TB_NAME = 1;

    /**
     * The column index of the opaque flag of that tile in the resource table.
     */
    private static final int TB_OPAQUE = 12;

    /**
     * The column index of the animation speed of that tile in the resource table.
     */
    private static final int TB_SPEED = 4;

    public TableLoaderTiles(final File table, final TableLoaderSink<TableLoaderTiles> callback) {
        super(table, callback);
    }

    public TableLoaderTiles(final String table, final TableLoaderSink<TableLoaderTiles> callback) {
        super(table, callback);
    }

    public TableLoaderTiles(final File table, final TableLoaderSink<TableLoaderTiles> callback, final String tableDelim) {
        super(table, callback, tableDelim);
    }

    public TableLoaderTiles(final InputStream resource, final boolean ndsc, final TableLoaderSink<TableLoaderTiles> callback, final String tableDelim) {
        super(resource, ndsc, callback, tableDelim);
    }

    public TableLoaderTiles(final String table, final boolean ndsc, final TableLoaderSink<TableLoaderTiles> callback, final String tableDelim) {
        super(table, ndsc, callback, tableDelim);
    }

    /**
     * Get the animation speed of this item.
     *
     * @return the animation speed of the item
     */
    public int getAnimationSpeed() {
        return getInt(TB_SPEED);
    }

    /**
     * Get the amount of frames of the animation or the variances of this tile.
     *
     * @return the frame count of the tile
     */
    public int getFrameCount() {
        return getInt(TB_FRAME);
    }

    /**
     * Get the ID of this item.
     *
     * @return the tile ID
     */
    public int getTileId() {
        return getInt(TB_GROUND_ID);
    }

    /**
     * Get the movement cost for moves on this tile. The higher the cost, the slower the character is expected to move
     * on this tile.
     *
     * @return the movement cost for this tile
     */
    public int getMovementCost() {
        return getInt(TB_COST);
    }

    /**
     * Get the resource name of the tile. This name is supposed to be used to fetch the graphics of this tile from
     * the resource loader.
     *
     * @return the resource name of this tile
     */
    public String getResourceName() {
        return getString(TB_NAME);
    }

    /**
     * Get the index of the color of this tile that is supposed to be used for displaying the tile in a large distance.
     *
     * @return the color index
     * @see TileInfo#getMapColor()
     */
    public int getTileColor() {
        return getInt(TB_COLOR);
    }

    /**
     * Get the tile mode value of this tile. This is used to define if this is a simple tile, a tile with a animated
     * graphics or a tile with variances as graphics.
     *
     * @return the mode value of this tile
     * @see #TILE_MODE_SIMPLE
     * @see #TILE_MODE_ANIMATED
     * @see #TILE_MODE_VARIANT
     */
    public int getTileMode() {
        return getInt(TB_MODE);
    }

    /**
     * Check if this tile is opaque. Players can't see past opaque tiles.
     *
     * @return {@code true} in case the item is opaque
     */
    public boolean isOpaque() {
        return getBoolean(TB_OPAQUE);
    }
}
