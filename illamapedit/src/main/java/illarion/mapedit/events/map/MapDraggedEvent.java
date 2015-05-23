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

    public MapDraggedEvent(
            int x, int y, int startX, int startY, MouseButton button, Map map) {
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
