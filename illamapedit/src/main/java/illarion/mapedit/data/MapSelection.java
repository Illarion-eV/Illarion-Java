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
package illarion.mapedit.data;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
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

    public void addSelectedTile(@Nonnull MapPosition mapPosition, MapTile tile) {
        if (!selectedTiles.containsKey(mapPosition)) {
            adjustOffsets(mapPosition);
            selectedTiles.put(mapPosition, tile);
        }
    }

    private void adjustOffsets(@Nonnull MapPosition mapPosition) {
        adjustHorizontalOffset(mapPosition.getX());
        adjustVerticalOffset(mapPosition.getY());
    }

    private void adjustHorizontalOffset(int horizontalCoordinate) {
        minX = min(minX, horizontalCoordinate);
    }

    private void adjustVerticalOffset(int verticalCoordinate) {
        minY = min(minY, verticalCoordinate);
    }

    private int min(int currentMinimum, int candidateMinimum) {
        return Math.min(currentMinimum, candidateMinimum);
    }

    public int getOffsetX() {
        return minX;
    }

    public int getOffsetY() {
        return minY;
    }

    @Nonnull
    public Collection<MapPosition> getSelectedPositions() {
        return Collections.unmodifiableCollection(selectedTiles.keySet());
    }

    public MapTile getMapTileAt(MapPosition position) {
        return selectedTiles.get(position);
    }
}
