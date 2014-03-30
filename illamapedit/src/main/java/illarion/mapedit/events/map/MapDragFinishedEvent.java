/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import javax.annotation.Nonnull;

/**
 * @author Tim
 */
public class MapDragFinishedEvent {
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    private final Map map;

    public MapDragFinishedEvent(final int startX, final int startY, final int endX, final int endY, final Map map) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.map = map;
    }

    public int getEndY() {
        return endY;
    }

    public int getEndX() {
        return endX;
    }

    public Map getMap() {
        return map;
    }

    public int getStartY() {
        return startY;
    }

    public int getStartX() {
        return startX;
    }

    @Nonnull
    @Override
    public String toString() {
        return "MapDragFinishedEvent{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                '}';
    }
}
