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
package illarion.client.resources.loaders;

import illarion.client.graphics.Tile;
import illarion.client.resources.ResourceFactory;
import illarion.common.graphics.TileInfo;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

import org.apache.log4j.Logger;

/**
 * This class is used to load the tile definitions from the resource table that
 * was created using the configuration tool. The class will create the required
 * tile objects and send them to the tile factory that takes care for
 * distributing those objects.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TileLoader extends ResourceLoader<Tile> implements
    TableLoaderSink {
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
     * The column index of the ground type ID of that tile in the resource
     * table.
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
     * The column index of the animation speed of that tile in the resource
     * table.
     */
    private static final int TB_SPEED = 4;

    /**
     * Tile mode value for animated tiles.
     */
    private static final int TILE_MODE_ANIMATED = 1;

    /**
     * Tile mode value for variant tiles.
     */
    private static final int TILE_MODE_VARIANT = 2;

    /**
     * The logger that is used to report error messages.
     */
    private final Logger logger = Logger.getLogger(ItemLoader.class);

    /**
     * Trigger the loading sequence for this loader.
     */
    @Override
    public void load() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<Tile> factory = getTargetFactory();

        factory.init();
        new TableLoader("Tiles", this);
        factory.loadingFinished();
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final int id = loader.getInt(TB_GROUND_ID);
        final int mode = loader.getInt(TB_MODE);
        final String name = loader.getString(TB_NAME);
        Tile tile = null;
        final TileInfo info =
            new TileInfo(loader.getInt(TB_COLOR), loader.getInt(TB_COST),
                loader.getBoolean(TB_OPAQUE));
        switch (mode) {
            case TILE_MODE_ANIMATED:
                tile =
                    new Tile(id, name, loader.getInt(TB_FRAME),
                        loader.getInt(TB_SPEED), info);
                break;

            case TILE_MODE_VARIANT:
                tile = new Tile(id, name, loader.getInt(TB_FRAME), 0, info);
                break;

            default:
                tile = new Tile(id, name, info);
                break;

        }

        try {
            getTargetFactory().storeResource(tile);
            tile.activate(id);
        } catch (final IllegalStateException ex) {
            logger.error("Failed adding tile to internal factory. ID: "
                + Integer.toString(id) + " - Filename: " + name);
        }

        return true;
    }

}
