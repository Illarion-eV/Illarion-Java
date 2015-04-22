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
package illarion.client.world.interactive;

import illarion.client.graphics.MapDisplayManager;
import illarion.client.world.GameMap;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.types.DisplayCoordinate;
import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    @Nonnull
    private final GameMap parentMap;

    /**
     * Create a interactive map for a single map instance.
     *
     * @param map the map to interact with
     */
    public InteractiveMap(GameMap map) {
        parentMap = map;
    }

    @Nullable
    private static InteractiveMapTile getInteractiveTile(@Nullable MapTile tile) {
        if (tile != null) {
            return tile.getInteractive();
        }
        return null;
    }

    @Nullable
    public InteractiveMapTile getInteractiveTileOnDisplayLoc(int displayX, int displayY) {
        return getInteractiveTile(getTileOnDisplayLoc(displayX, displayY));
    }

    @Nullable
    public InteractiveMapTile getInteractiveTileOnMapLoc(int locX, int locY, int locZ) {
        return getInteractiveTile(getTileOnMapLoc(locX, locY, locZ));
    }

    @Nullable
    public InteractiveMapTile getInteractiveTileOnMapLoc(@Nonnull ServerCoordinate loc) {
        return getInteractiveTile(getTileOnMapLoc(loc));
    }

    @Nullable
    public InteractiveMapTile getInteractiveTileOnScreenLoc(int screenX, int screenY) {
        return getInteractiveTile(getTileOnScreenLoc(screenX, screenY));
    }

    @Nullable
    public MapTile getTileOnDisplayLoc(int displayX, int displayY) {
        int playerBase = World.getPlayer().getBaseLevel();
        int base = playerBase - 2;
        int lowX = DisplayCoordinate.toServerX(displayX, displayY) - (base * TILE_PERSPECTIVE_OFFSET);
        int lowY = DisplayCoordinate.toServerY(displayX, displayY) + (base * TILE_PERSPECTIVE_OFFSET);

        for (int i = 4; i >= 0; --i) {
            int levelOffset = TILE_PERSPECTIVE_OFFSET * i;

            int tilePosX = lowX - levelOffset;
            int tilePosY = lowY + levelOffset;
            int tilePosZ = base + i;

            @Nullable MapTile foundElevatedTile = parentMap.getMapAt(
                    new ServerCoordinate(tilePosX - 1, tilePosY + 1, tilePosZ));
            if ((foundElevatedTile != null) && (foundElevatedTile.getElevation() > 0)) {

                int x = DisplayCoordinate.toServerX(displayX, displayY - foundElevatedTile.getElevation());
                int y = DisplayCoordinate.toServerY(displayX, displayY - foundElevatedTile.getElevation());

                int elevatedX = x - (tilePosZ * TILE_PERSPECTIVE_OFFSET);
                int elevatedY = y + (tilePosZ * TILE_PERSPECTIVE_OFFSET);

                if ((elevatedX == (tilePosX - 1)) && (elevatedY == (tilePosY + 1))) {
                    return foundElevatedTile;
                }
            }

            @Nullable MapTile foundTile = parentMap.getMapAt(tilePosX, tilePosY, tilePosZ);
            if ((foundTile != null) && !foundTile.isHidden()) {
                return foundTile;
            }
        }

        return null;
    }

    @Nullable
    public MapTile getTileOnMapLoc(int locX, int locY, int locZ) {
        return parentMap.getMapAt(locX, locY, locZ);
    }

    @Nullable
    public MapTile getTileOnMapLoc(@Nonnull ServerCoordinate loc) {
        return parentMap.getMapAt(loc);
    }

    @Nullable
    public MapTile getTileOnScreenLoc(int screenX, int screenY) {
        MapDisplayManager displayManager = World.getMapDisplay();

        return getTileOnDisplayLoc(displayManager.getWorldX(screenX), displayManager.getWorldY(screenY));
    }
}
