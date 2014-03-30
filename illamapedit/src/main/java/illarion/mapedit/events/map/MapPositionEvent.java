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

/**
 * This event is fired every time the mouse is moved on the screen. Its used to update the coordinates the mouse is
 * pointing at.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MapPositionEvent {
    private final int mapX;
    private final int mapY;

    private final int worldX;
    private final int worldY;
    private final int worldZ;

    public MapPositionEvent(final int mapX, final int mapY, final int worldX, final int worldY, final int worldZ) {
        this.mapX = mapX;
        this.mapY = mapY;
        this.worldX = worldX;
        this.worldY = worldY;
        this.worldZ = worldZ;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public int getWorldX() {
        return worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public int getWorldZ() {
        return worldZ;
    }
}
