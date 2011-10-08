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
package illarion.client.resources;

import illarion.client.graphics.Tile;
import illarion.common.util.RecycleFactory;

/**
 * The tile factory loads and stores all graphical representations of the tiles
 * that create the map of Illarion.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class TileFactory extends RecycleFactory<Tile> implements
    ResourceFactory<Tile> {
    /**
     * The singleton instance of this class.
     */
    private static final TileFactory INSTANCE = new TileFactory();

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    public static TileFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor of the tile factory that prepares this factory for
     * operation.
     */
    private TileFactory() {
        super();
    }

    /**
     * Apply the mappings that are needed to turn the map into a winter look.
     */
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
     * The initialize function that can be used to prepare this factory to take
     * the data from the resource loader.
     */
    @Override
    public void init() {
    }

    /**
     * Finish the loading sequence and prepare the factory for normal operation
     * and distribution of the load data.
     */
    @Override
    public void loadingFinished() {
        mapDefault(0, 1);
        finish();
    }

    /**
     * Store a resource inside that factory.
     */
    @Override
    public void storeResource(final Tile resource) {
        register(resource);
    }
}
