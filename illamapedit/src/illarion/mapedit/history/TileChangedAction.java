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
import illarion.mapedit.data.Tile;

/**
 * This history action is used when a tile of the map is changed to another tile.
 *
 * @author Tim
 */
public class TileChangedAction implements HistoryAction {

    private final boolean ignore;
    private final Tile newTile;
    private final Tile oldTile;
    private final Map map;

    public TileChangedAction(final Tile newTile, final Tile oldTile, final Map map) {
        if (newTile.getX() != oldTile.getX() || newTile.getY() != newTile.getY()) {
            throw new IllegalArgumentException("The tiles must be on the same location on the map.");
        }
        if (newTile.getId() == oldTile.getId() && newTile.getMusicID() == oldTile.getMusicID()) {
            ignore = true;
            this.newTile = null;
            this.oldTile = null;
            this.map = null;
        } else {
            ignore = false;
            this.newTile = newTile;
            this.oldTile = oldTile;
            this.map = map;
        }
    }

    @Override
    public void redo() {
        if (ignore) return;
        map.getTileData().setTileAt(newTile);
    }

    @Override
    public void undo() {
        if (ignore) return;
        map.getTileData().setTileAt(oldTile);
    }
}
