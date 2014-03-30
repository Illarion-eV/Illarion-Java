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
package illarion.mapedit.data;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * This class represents the selected tile in a map
 *
 * @author Fredrik K
 */
public class MapSelection {

    @Nonnull
    private final HashMap<MapPosition, MapTile> selectedTiles;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;

    public MapSelection() {
        selectedTiles = new HashMap<>();
    }

    public void addSelectedTile(@Nonnull final MapPosition mapPosition, final MapTile tile) {
        if (!selectedTiles.containsKey(mapPosition)) {
            minX = Math.min(minX, mapPosition.getX());
            minY = Math.min(minY, mapPosition.getY());
            selectedTiles.put(mapPosition, tile);
        }
    }

    public int getOffsetX() {
        return minX;
    }

    public int getOffsetY() {
        return minY;
    }

    @Nonnull
    public HashMap<MapPosition, MapTile> getTiles() {
        return selectedTiles;
    }
}
