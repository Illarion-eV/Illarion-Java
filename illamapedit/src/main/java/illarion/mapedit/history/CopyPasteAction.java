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
package illarion.mapedit.history;

import illarion.common.types.ServerCoordinate;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.data.MapTile.MapTileFactory;
import illarion.mapedit.processing.MapTransitions;

import javax.annotation.Nullable;

/**
 * @author Fredrik K
 */
public class CopyPasteAction extends HistoryAction {
    private final MapTile newTile;
    @Nullable
    private final MapTile oldTile;
    private final int x;
    private final int y;

    public CopyPasteAction(
            int x,
            int y,
            @Nullable MapTile oldTile,
            MapTile newTile,
            Map map) {
        super(map);
        this.x = x;
        this.y = y;
        this.oldTile = oldTile;
        this.newTile = newTile;
    }

    @Override
    void redo() {
        map.setTileAt(x, y, MapTileFactory.copyAll(newTile));
        MapTransitions.getInstance().checkTileAndSurround(map, new ServerCoordinate(x, y, 0));
        map.getTileAt(x, y).setAnnotation(newTile.getAnnotation());
    }

    @Override
    void undo() {
        map.setTileAt(x, y, MapTileFactory.copyAll(oldTile));
        MapTransitions.getInstance().checkTileAndSurround(map, new ServerCoordinate(x, y, 0));
        map.getTileAt(x, y).setAnnotation(oldTile.getAnnotation());
    }
}
