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
 * @author Tim
 */
public class MusicIDChangedAction extends HistoryAction {


    private final int x;
    private final int y;
    private final int oldID;
    private final int newID;

    public MusicIDChangedAction(final int x, final int y, final int oldID, final int newID, final Map map) {
        super(map);
        this.x = x;
        this.y = y;
        this.oldID = oldID;
        this.newID = newID;
    }

    @Override
    void redo() {
        map.setTileAt(x, y, MapTile.MapTileFactory.setMusicId(newID, map.getTileAt(x, y)));
    }

    @Override
    void undo() {
        map.setTileAt(x, y, MapTile.MapTileFactory.setMusicId(oldID, map.getTileAt(x, y)));
    }
}
