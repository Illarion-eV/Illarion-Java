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
package illarion.mapedit.graphics;

import illarion.mapedit.MapEditor;

import illarion.common.graphics.TileInfo;
import illarion.common.util.RecycleFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * The factory that loads all tiles and prepares the values to be used by the
 * rest of the map editor.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class TileFactory extends RecycleFactory<Tile> implements
    TableLoaderSink {
    /**
     * The singleton instance of this factory.
     */
    private static final TileFactory INSTANCE = new TileFactory();

    /**
     * Column index of the ID of the map color of the tile.
     */
    private static final int TB_COLOR = 5;

    /**
     * Column index of the walking cost flag of this tile.
     */
    private static final int TB_COST = 9;

    /**
     * Column index of the frame count of this tile graphic.
     */
    private static final int TB_FRAME = 2;

    /**
     * Column index of the ground id of the tile.
     */
    private static final int TB_GROUND_ID = 7;

    /**
     * Column index of the mode of the tile. (still image, variances, animation)
     */
    private static final int TB_MODE = 3;

    /**
     * Column index of the resource name for the tile graphic.
     */
    private static final int TB_NAME = 1;

    /**
     * Column index of the old flag. Old flagged tiles are not load.
     */
    private static final int TB_OLD = 8;

    /**
     * Column index of the opaque flag of this tile.
     */
    private static final int TB_OPAQUE = 12;

    /**
     * Column index of the title name of the tile. This is displayed for the
     * users of the map editor.
     */
    private static final int TB_TITLE = 10;

    /**
     * Private constructor to avoid any instances but the singleton instance.
     */
    private TileFactory() {
        super();
    }

    /**
     * Get the singleton instance of this factory.
     * 
     * @return the singleton instance of this factory
     */
    public static TileFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The init function preapares all prototyped that are needed to work with
     * this function.
     */
    @SuppressWarnings("nls")
    public void init() {
//        MapEditor.getMainFrame().getRightToolbar().getPartSelector()
//            .getTilesList().addGroup("Normal Tiles");
//        MapEditor.getMainFrame().getRightToolbar().getPartSelector()
//            .getTilesList().addGroup("Detail Tiles");

        new TableLoader("Tiles", this);

        register(new BlindTile());
        mapDefault(0, 1);
        finish();

    }

    /**
     * Process one line of the item definition table and create a tile
     * definition from it.
     * 
     * @param line the number of the line that is currently processed
     * @param loader the table loader that handles this line and supplies the
     *            data
     * @return <code>true</code> to go on reading the table, false to cancel the
     *         reading process
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        // ignore all obsolete tiles
        if (loader.getBoolean(TB_OLD)) {
            return true;
        }

        final int id = loader.getInt(TB_GROUND_ID);
        final int mode = loader.getInt(TB_MODE);
        final String title = loader.getString(TB_TITLE);
        Tile tile = null;
        final TileInfo info =
            new TileInfo(loader.getInt(TB_COLOR), loader.getInt(TB_COST),
                loader.getBoolean(TB_OPAQUE));
        switch (mode) {
        // animated image
            case 1:
                tile =
                    new Tile(id, loader.getString(TB_NAME),
                        loader.getInt(TB_FRAME), 1, info, title);
                break;

            // variant image
            case 2:
                tile =
                    new Tile(id, loader.getString(TB_NAME),
                        loader.getInt(TB_FRAME), 0, info, title);
                break;

            // still image
            default:
                tile = new Tile(id, loader.getString(TB_NAME), info, title);
                break;

        }
        register(tile);

        int group = 0;
        if (id > 34) {
            group = 1;
        }
//        MapEditor.getMainFrame().getRightToolbar().getPartSelector()
//            .getTilesList().addPart(group, tile);

        return true;
    }
}
