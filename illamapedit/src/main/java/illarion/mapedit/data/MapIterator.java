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
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This is a special iterator implementation that allows to iterate over all tiles of a map in a swift way.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MapIterator implements Iterator<MapTile> {
    private int currentIndex = -1;
    private final int tileCount;
    @Nonnull
    private final Map parentMap;

    MapIterator(@Nonnull Map map, int tileCount) {
        parentMap = map;
        this.tileCount = tileCount;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < (tileCount - 1);
    }

    @Override
    public MapTile next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return parentMap.getTileAtIndex(++currentIndex);
    }

    public int getCurrentX() {
        if (currentIndex == -1) {
            throw new IllegalStateException("Can't request coordinate while next() was never called.");
        }
        return parentMap.indexToMapX(currentIndex);
    }

    public int getCurrentY() {
        if (currentIndex == -1) {
            throw new IllegalStateException("Can't request coordinate while next() was never called.");
        }
        return parentMap.indexToMapY(currentIndex);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removing tiles is not supported by this iterator");
    }
}
