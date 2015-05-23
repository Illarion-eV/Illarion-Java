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

import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapItem;

import javax.annotation.Nullable;

/**
 * @author Tim
 */
public class ItemPlacedAction extends HistoryAction {

    private final int x;
    private final int y;
    @Nullable
    private final MapItem old;
    @Nullable
    private final MapItem newt;

    public ItemPlacedAction(int x, int y, @Nullable MapItem newt, Map map) {
        super(map);
        this.x = x;
        this.y = y;
        old = null;
        this.newt = newt;
    }

    public ItemPlacedAction(
            int x, int y, @Nullable MapItem old, @Nullable MapItem newt, Map map) {
        super(map);
        this.x = x;
        this.y = y;
        this.old = old;
        this.newt = newt;
    }

    @Override
    public void redo() {
        if (old != null) {
            map.getTileAt(x, y).removeMapItem(old);
        }
        if (newt != null) {
            map.getTileAt(x, y).addMapItem(newt);
        }
    }

    @Override
    public void undo() {
        if (newt != null) {
            map.getTileAt(x, y).removeMapItem(newt);
        }
        if (old != null) {
            map.getTileAt(x, y).addMapItem(old);
        }
    }
}
