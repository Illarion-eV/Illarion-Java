/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.history;

import illarion.common.types.Location;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.processing.MapTransitions;

/**
 * @author Fredrik K
 */
public class CopyPasteAction extends HistoryAction {
    private final MapTile newTile;
    private final MapTile oldTile;
    private final int x;
    private final int y;

    public CopyPasteAction(final int x, final int y, final MapTile oldTile, final MapTile newTile, final Map map) {
        super(map);
        this.x = x;
        this.y = y;
        this.oldTile = oldTile;
        this.newTile = newTile;
    }

    @Override
    void redo() {
        map.setTileAt(x, y, MapTile.MapTileFactory.copy(newTile));
        MapTransitions.getInstance().checkTileAndSurround(map, new Location(x, y, 0));
    }

    @Override
    void undo() {
        map.setTileAt(x, y, MapTile.MapTileFactory.copy(newTile));
        MapTransitions.getInstance().checkTileAndSurround(map, new Location(x, y, 0));
    }
}
