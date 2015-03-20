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
package illarion.client.world;

import illarion.client.graphics.MapDisplayManager;
import illarion.common.types.Direction;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This utility class is used to process the map tiles and ensure that they are properly linked and assigned to each
 * other.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GameMapProcessor2 {
    private GameMapProcessor2() {
    }

    /**
     * Process a single new tile.
     *
     * @param tile the tile to process
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static void processTile(@Nonnull MapTile tile) {
        Location playerLocation = World.getPlayer().getLocation();

        MapTile tileAbove = getFirstTileAbove(tile.getLocation(), playerLocation.getScZ() + 2, true);
        MapTile tileBelow = getFirstTileBelow(tile.getLocation(), playerLocation.getScZ() - 2, true);

        if (tileAbove != null) {
            tile.setObstructingTile(tileAbove);
        }
        if (tileBelow != null) {
            tileBelow.setObstructingTile(tile);
        }

        List<MapGroup> groups = getSurroundingMapGroups(tile.getLocation());
        MapGroup tileGroup;
        if (groups.isEmpty()) {
            tileGroup = new MapGroup();
            tile.setMapGroup(tileGroup);
        } else {
            tileGroup = groups.get(0);
            assert tileGroup != null;
            tile.setMapGroup(tileGroup);
            for (int i = 1; i < groups.size(); i++) {
                //noinspection ConstantConditions
                groups.get(i).setParent(tileGroup);
            }
        }
        if (tileAbove != null) {
            MapGroup tileAboveGroup = tileAbove.getMapGroup();
            MapGroup tileAboveGroupRoot = (tileAboveGroup == null) ? null : tileAboveGroup.getRootGroup();
            if (tileAboveGroupRoot != null) {
                tileAboveGroupRoot.addOverwritingGroup(tileGroup);
            }
        }
        if (tileBelow != null) {
            MapGroup tileBelowGroup = tileBelow.getMapGroup();
            MapGroup tileBelowGroupRoot = (tileBelowGroup == null) ? null : tileBelowGroup.getRootGroup();
            if (tileBelowGroupRoot != null) {
                tileGroup.addOverwritingGroup(tileBelowGroupRoot);
            }
        }
    }

    public static boolean isOutsideOfClipping(@Nonnull MapTile tile) {
        if (!World.getPlayer().hasValidLocation()) {
            return false;
        }

        Location playerLoc = World.getPlayer().getLocation();
        Location tileLoc = tile.getLocation();

        /*
         * Start checking the clipping of the tiles. In case a tile is found outside the clipping range, its deleted.
         */
        if ((playerLoc.getScZ() + 2) < tileLoc.getScZ()) {
            return true;
        }

        if ((playerLoc.getScZ() - 2) > tileLoc.getScZ()) {
            return true;
        }

        MapDimensions mapDim = MapDimensions.getInstance();

        if ((playerLoc.getCol() + mapDim.getClippingOffsetLeft()) > tileLoc.getCol()) {
            return true;
        }

        if ((playerLoc.getCol() + mapDim.getClippingOffsetRight()) < tileLoc.getCol()) {
            return true;
        }

        int level = (Math.abs(tileLoc.getScZ() - playerLoc.getScZ()) * 6) + 1;

        if ((playerLoc.getRow() + mapDim.getClippingOffsetTop()) < (tileLoc.getRow() - level)) {
            return true;
        }

        if ((playerLoc.getRow() + mapDim.getClippingOffsetBottom()) > (tileLoc.getRow() + level)) {
            return true;
        }

        return false;
    }

    @Nullable
    private static MapGroup lastInsideGroup;

    public static void checkInside() {
        Location playerLocation = World.getPlayer().getLocation();

        MapTile tileAbove = getFirstTileAbove(playerLocation, playerLocation.getScZ() + 2, false);
        MapGroup realTileAboveGroup = (tileAbove == null) ? null : tileAbove.getMapGroup();
        MapGroup tileAboveGroup = (realTileAboveGroup == null) ? null : realTileAboveGroup.getRootGroup();

        if (tileAboveGroup == null) {
            if (lastInsideGroup != null) {
                lastInsideGroup.setHidden(false);
                lastInsideGroup = null;
            }
            World.getWeather().setOutside(true);
        } else {
            if (lastInsideGroup != null) {
                if (lastInsideGroup == tileAboveGroup) {
                    return;
                }
                lastInsideGroup.setHidden(false);
            }
            tileAboveGroup.setHidden(true);
            lastInsideGroup = tileAboveGroup;
            World.getWeather().setOutside(false);
        }
    }

    @Nullable
    private static MapTile getFirstTileBelow(
            @Nonnull Location startLocation, int zLimit, boolean perceptiveOffset) {
        if (startLocation.getScZ() <= zLimit) {
            return null;
        }

        int currentX = startLocation.getScX();
        int currentY = startLocation.getScY();
        int currentZ = startLocation.getScZ();
        while (currentZ > zLimit) {
            if (perceptiveOffset) {
                currentX += MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
                currentY -= MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
            }
            currentZ--;

            MapTile tile = World.getMap().getMapAt(currentX, currentY, currentZ);
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }

    @Nullable
    private static MapTile getFirstTileAbove(
            @Nonnull Location startLocation, int zLimit, boolean perceptiveOffset) {
        if (startLocation.getScZ() >= zLimit) {
            return null;
        }

        int currentX = startLocation.getScX();
        int currentY = startLocation.getScY();
        int currentZ = startLocation.getScZ();
        while (currentZ < zLimit) {
            if (perceptiveOffset) {
                currentX -= MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
                currentY += MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
            }
            currentZ++;

            MapTile tile = World.getMap().getMapAt(currentX, currentY, currentZ);
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }

    @Nonnull
    private static List<MapGroup> getSurroundingMapGroups(@Nonnull Location startLocation) {
        List<MapGroup> groupList = new ArrayList<>();

        GameMap map = World.getMap();
        //noinspection ConstantConditions
        for (Direction dir : Direction.values()) {
            int locX = startLocation.getScX() + dir.getDirectionVectorX();
            int locY = startLocation.getScY() + dir.getDirectionVectorY();

            MapTile tile = map.getMapAt(locX, locY, startLocation.getScZ());
            if (tile != null) {
                MapGroup group = tile.getMapGroup();
                if (group != null) {
                    group = group.getRootGroup();
                }
                if ((group != null) && !groupList.contains(group)) {
                    groupList.add(group);
                }
            }
        }
        return groupList;
    }
}
