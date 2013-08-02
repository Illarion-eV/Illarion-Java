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
package illarion.mapedit.events.map;

import illarion.mapedit.data.Map;
import illarion.mapedit.util.MouseButton;

/**
 * @author Tim
 */
public class MapDraggedEvent {

    private final int x;
    private final int y;
    private final int startX;
    private final int startY;
    private final MouseButton button;
    private final Map map;


    public MapDraggedEvent(final int x, final int y, final int startX, final int startY, final MouseButton button,
                           final Map map
    ) {
        this.x = x;
        this.y = y;
        this.startX = startX;
        this.startY = startY;
        this.button = button;
        this.map = map;
    }

    public MouseButton getButton() {
        return button;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Map getMap() {
        return map;
    }
}
