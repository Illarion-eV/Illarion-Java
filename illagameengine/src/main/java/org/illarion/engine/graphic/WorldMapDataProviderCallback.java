/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package org.illarion.engine.graphic;

import illarion.common.types.Location;

/**
 * This interface defines a callback the world map data provider is using to report the requested map data.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface WorldMapDataProviderCallback {
    /**
     * Set the data of the tile that was requested.
     *
     * @param loc the location of the tile that is updated
     * @param tileId the map ID of the tile (this is not the tile ID)
     * @param overlayId the map ID of the overlay (this is not the tile ID)
     * @param blocked {@code true} in case the player can't step onto this tile
     */
    void setTile(Location loc, int tileId, int overlayId, boolean blocked);
}
