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
import illarion.mapedit.data.MapWarpPoint;

/**
 * @author Tim
 */
public class WarpPointAction extends HistoryAction {

    private final int x;
    private final int y;
    private final MapWarpPoint oldWP;
    private final MapWarpPoint newWP;

    protected WarpPointAction(final int x, final int y, final MapWarpPoint oldWP,
                              final MapWarpPoint newWP, final Map map) {
        super(map);
        this.x = x;
        this.y = y;
        this.oldWP = oldWP;
        this.newWP = newWP;
    }

    @Override
    void redo() {
        map.getTileAt(x, y).setMapWarpPoint(newWP);
    }

    @Override
    void undo() {
        map.getTileAt(x, y).setMapWarpPoint(oldWP);
    }
}
