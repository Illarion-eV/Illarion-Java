/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
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

import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;

/**
 * This history action is used when a tile of the map is changed to another tile.
 *
 * @author Tim
 */
public class TileChangedAction implements HistoryAction {

    private final int x;
    private final int y;
    /**
     * The new tile.
     */
    private final MapTile newTile;
    /**
     * The old tile.
     */
    private final MapTile oldTile;
    /**
     * The map, on which these changes happened.
     */
    private final Map map;

    public TileChangedAction(final int x, final int y, final MapTile newTile, final MapTile oldTile, final Map map) {
        this.x = x;
        this.y = y;
        this.newTile = newTile;
        this.oldTile = oldTile;
        this.map = map;

    }

    /**
     * Undoes the action.
     */
    @Override
    public void redo() {
        map.setTileAt(x, y, newTile);
    }

    /**
     * Redoes the action.
     */
    @Override
    public void undo() {
        map.setTileAt(x, y, oldTile);
    }
}
