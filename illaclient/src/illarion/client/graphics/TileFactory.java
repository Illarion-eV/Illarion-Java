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
package illarion.client.graphics;

import illarion.common.graphics.TileInfo;
import illarion.common.util.RecycleFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * Created: 20.08.2005 22:41:23
 */
public class TileFactory extends RecycleFactory<Tile> implements
    TableLoaderSink {
    public static final int TB_COLOR = 5;
    public static final int TB_COST = 9;
    public static final int TB_FRAME = 2;
    public static final int TB_GROUND_ID = 7;
    public static final int TB_ID = 0;
    public static final int TB_MODE = 3;
    public static final int TB_NAME = 1;
    public static final int TB_OLD = 8;
    public static final int TB_OPAQUE = 12;
    public static final int TB_PRELOAD = 6;
    public static final int TB_SPEED = 4;

    private static final TileFactory instance = new TileFactory();

    private TileFactory() {
        super();
    }

    public static TileFactory getInstance() {
        return instance;
    }

    public final void activateWinter() {
        forceMap(11, 10);
        forceMap(41, 10);
        forceMap(43, 10);
    }

    /**
     * Query the factory for the map color of a certain tile
     * 
     * @param id
     * @return
     */
    public int getMapColor(int id) {
        id = Tile.baseID(id);

        // no mapping to replacement to ingore winter mode for mini-map
        return getPrototype(id).getMapColor();

    }

    /**
     * The init function preapares all prototyped that are needed to work with
     * this function.
     */
    public void init() {
        new TableLoader("Tiles", this);
        mapDefault(0, 1);
        finish();
    }

    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        // ignore all obsolete tiles
        if (loader.getBoolean(TB_OLD)) {
            return true;
        }

        // int id = loader.getInt(TB_ID);
        final int id = loader.getInt(TB_GROUND_ID);
        final int mode = loader.getInt(TB_MODE);
        Tile tile = null;
        final TileInfo info =
            new TileInfo(loader.getInt(TB_COLOR), loader.getInt(TB_COST),
                loader.getBoolean(TB_OPAQUE));
        switch (mode) {
        // animated image
            case 1:
                tile =
                    new Tile(id, loader.getString(TB_NAME),
                        loader.getInt(TB_FRAME), loader.getInt(TB_SPEED), info);
                break;

            // variant image
            case 2:
                tile =
                    new Tile(id, loader.getString(TB_NAME),
                        loader.getInt(TB_FRAME), 0, info);
                break;

            // still image
            default:
                tile = new Tile(id, loader.getString(TB_NAME), info);
                break;

        }
        register(tile);
        tile.activate(tile.getId());

        return true;
    }
}
