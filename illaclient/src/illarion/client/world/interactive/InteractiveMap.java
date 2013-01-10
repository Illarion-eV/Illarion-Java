/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world.interactive;

import illarion.client.graphics.MapDisplayManager;
import illarion.client.world.GameMap;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.types.Location;

import static illarion.client.graphics.MapDisplayManager.TILE_PERSPECTIVE_OFFSET;

/**
 * This interactive map class is used for the user interaction with the game map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InteractiveMap {
    /**
     * The actual map that supplies this class with data.
     */
    private final GameMap parentMap;

    /**
     * Create a interactive map for a single map instance.
     *
     * @param map the map to interact with
     */
    public InteractiveMap(final GameMap map) {
        parentMap = map;
    }

    private static InteractiveMapTile getInteractiveTile(final MapTile tile) {
        if (tile != null) {
            return tile.getInteractive();
        }
        return null;
    }

    public InteractiveMapTile getInteractiveTileOnDisplayLoc(final int displayX, final int displayY) {
        return getInteractiveTile(getTileOnDisplayLoc(displayX, displayY));
    }

    public InteractiveMapTile getInteractiveTileOnMapLoc(final int locX, final int locY, final int locZ) {
        return getInteractiveTile(getTileOnMapLoc(locX, locY, locZ));
    }

    public InteractiveMapTile getInteractiveTileOnMapLoc(final Location loc) {
        return getInteractiveTile(getTileOnMapLoc(loc));
    }

    public InteractiveMapTile getInteractiveTileOnScreenLoc(final int screenX, final int screenY) {
        return getInteractiveTile(getTileOnScreenLoc(screenX, screenY));
    }

    public MapTile getTileOnDisplayLoc(final int displayX, final int displayY) {
        final Location helpLoc = new Location();
        helpLoc.setDC(displayX, displayY);

        final int playerBase = World.getPlayer().getBaseLevel();
        final int base = playerBase - 2;
        final int lowX = helpLoc.getScX() + ((2 - playerBase) * TILE_PERSPECTIVE_OFFSET);
        final int lowY = helpLoc.getScY() - ((2 - playerBase) * TILE_PERSPECTIVE_OFFSET);

        MapTile foundTile;
        int levelOffset;
        for (int i = 4; i >= 0; --i) {
            levelOffset = TILE_PERSPECTIVE_OFFSET * i;
            foundTile = parentMap.getMapAt(lowX - levelOffset, lowY + levelOffset, base + i);
            if ((foundTile != null) && !foundTile.isHidden()) {
                return foundTile;
            }
        }

        return null;
    }

    public MapTile getTileOnMapLoc(final int locX, final int locY, final int locZ) {
        return parentMap.getMapAt(locX, locY, locZ);
    }

    public MapTile getTileOnMapLoc(final Location loc) {
        return parentMap.getMapAt(loc);
    }

    public MapTile getTileOnScreenLoc(final int screenX, final int screenY) {
        final MapDisplayManager displayManager = World.getMapDisplay();

        return getTileOnDisplayLoc(displayManager.getWorldX(screenX), displayManager.getWorldY(screenY));
    }
}
