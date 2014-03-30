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
package illarion.mapedit.history;

import illarion.common.types.Location;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.processing.MapTransitions;

/**
 * @author Tim
 */
public class TileIDChangedAction extends HistoryAction {

    private final int x;
    private final int y;
    private final MapTile old;
    private final MapTile newt;

    public TileIDChangedAction(final int x, final int y, final MapTile old, final MapTile newt, final Map map) {
        super(map);
        this.x = x;
        this.y = y;
        this.old = old;
        this.newt = newt;
    }

    @Override
    public void redo() {
        map.setTileAt(x, y, MapTile.MapTileFactory.setId(newt.getId(), map.getTileAt(x, y)));
        MapTransitions.getInstance().checkTileAndSurround(map, new Location(x, y, 0));
    }

    @Override
    public void undo() {
        map.setTileAt(x, y, MapTile.MapTileFactory.setId(old.getId(), map.getTileAt(x, y)));
        MapTransitions.getInstance().checkTileAndSurround(map, new Location(x, y, 0));
    }
}
