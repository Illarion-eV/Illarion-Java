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
package illarion.mapedit.data;

import illarion.mapedit.events.HistoryPasteCutEvent;
import illarion.mapedit.history.CopyPasteAction;
import illarion.mapedit.history.GroupAction;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Fredrik K
 */
public class SelectionManager {
    @Nonnull
    private final Set<MapPosition> selection;

    public SelectionManager() {
        selection = new HashSet<>();
    }

    @Nonnull
    public MapSelection copy(@Nonnull final Map map) {
        final MapSelection mapSelection = new MapSelection();
        for (final MapPosition pos : selection) {
            final MapTile tile = map.getTileAt(pos.getX(), pos.getY());
            if (tile != null) {
                mapSelection.addSelectedTile(pos, MapTile.MapTileFactory.copy(tile));
            }
        }
        return mapSelection;
    }

    @Nonnull
    public MapSelection cut(@Nonnull final Map map) {
        final MapSelection mapSelection = new MapSelection();
        final GroupAction action = new GroupAction();
        for (final MapPosition pos : selection) {
            final MapTile tile = map.getTileAt(pos.getX(), pos.getY());
            if (tile != null) {
                mapSelection.addSelectedTile(pos, MapTile.MapTileFactory.copy(tile));
                final MapTile tileNew = MapTile.MapTileFactory.createNew(0, 0, 0, 0);
                action.addAction(new CopyPasteAction(pos.getX(), pos.getY(), tile, tileNew, map));
                map.setTileAt(pos.getX(), pos.getY(), tileNew);
            }
        }

        if (!action.isEmpty()) {
            EventBus.publish(new HistoryPasteCutEvent(action));
        }

        selection.clear();
        return mapSelection;
    }

    @Nonnull
    public Set<MapPosition> getSelection() {
        return selection;
    }

    public void select(final int x, final int y) {
        selection.add(new MapPosition(x, y));
    }

    public void deselect(final int x, final int y) {
        final MapPosition pos = new MapPosition(x, y);
        if (selection.contains(pos)) {
            selection.remove(pos);
        }
    }

    public boolean isSelected(final int x, final int y) {
        return selection.contains(new MapPosition(x, y));
    }
}
