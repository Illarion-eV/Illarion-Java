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

import illarion.mapedit.data.MapTile.MapTileFactory;
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
    public MapSelection copy(@Nonnull Map map) {
        MapSelection mapSelection = new MapSelection();
        for (MapPosition pos : selection) {
            MapTile tile = map.getTileAt(pos.getX(), pos.getY());
            if (tile != null) {
                mapSelection.addSelectedTile(pos, MapTileFactory.copy(tile));
            }
        }
        return mapSelection;
    }

    @Nonnull
    public MapSelection cut(@Nonnull Map map) {
        MapSelection mapSelection = new MapSelection();
        GroupAction action = new GroupAction();
        for (MapPosition pos : selection) {
            MapTile tile = map.getTileAt(pos.getX(), pos.getY());
            if (tile != null) {
                mapSelection.addSelectedTile(pos, MapTileFactory.copy(tile));
                MapTile tileNew = MapTileFactory.createNew(0, 0, 0, 0);
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

    public void select(int x, int y) {
        selection.add(new MapPosition(x, y));
    }

    public void deselect(int x, int y) {
        MapPosition pos = new MapPosition(x, y);
        if (selection.contains(pos)) {
            selection.remove(pos);
        }
    }

    public boolean isSelected(int x, int y) {
        return selection.contains(new MapPosition(x, y));
    }
}
