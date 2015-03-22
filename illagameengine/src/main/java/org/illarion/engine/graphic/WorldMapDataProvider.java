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
package org.illarion.engine.graphic;

import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;

/**
 * This interface defines a class that is able to provide the world map texture creator with the actual map data.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface WorldMapDataProvider {
    /**
     * Request the tile data of a specific tile.
     *
     * @param location the location of the requested tile
     * @param callback the callback class that is supposed to receive the tile data
     */
    void requestTile(@Nonnull ServerCoordinate location, @Nonnull WorldMapDataProviderCallback callback);
}
