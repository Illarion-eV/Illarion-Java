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
import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This utility class is used to process the map tiles and ensure that they are properly linked and assigned to each
 * other.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GameMapProcessor2 {
    @Nullable
    private static MapGroup lastInsideGroup;

    private GameMapProcessor2() {
    }

    /**
     * Process a single new tile.
     *
     * @param tile the tile to process
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static void processTile(@Nonnull MapTile tile) {
        ServerCoordinate playerLocation = World.getPlayer().getLocation();

        MapTile tileAbove = getFirstTileAbove(tile.getCoordinates(), playerLocation.getZ() + 2, true);
        MapTile tileBelow = getFirstTileBelow(tile.getCoordinates(), playerLocation.getZ() - 2, true);

        if (tileAbove != null) {
            tile.setObstructingTile(tileAbove);
        }
        if (tileBelow != null) {
            tileBelow.setObstructingTile(tile);
        }

        List<MapGroup> groups = getSurroundingMapGroups(tile.getCoordinates());
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

        ServerCoordinate playerLoc = World.getPlayer().getLocation();
        ServerCoordinate tileLoc = tile.getCoordinates();

        /*
         * Start checking the clipping of the tiles. In case a tile is found outside the clipping range, its deleted.
         */
        if ((playerLoc.getZ() + 2) < tileLoc.getZ()) {
            return true;
        }

        if ((playerLoc.getZ() - 2) > tileLoc.getZ()) {
            return true;
        }

        MapDimensions mapDim = MapDimensions.getInstance();

        if ((playerLoc.toMapColumn() + mapDim.getClippingOffsetLeft()) > tileLoc.toMapColumn()) {
            return true;
        }

        if ((playerLoc.toMapColumn() + mapDim.getClippingOffsetRight()) < tileLoc.toMapColumn()) {
            return true;
        }

        int level = (Math.abs(tileLoc.getZ() - playerLoc.getZ()) * 6) + 1;

        if ((playerLoc.toMapRow() + mapDim.getClippingOffsetTop()) < (tileLoc.toMapRow() - level)) {
            return true;
        }

        return (playerLoc.toMapRow() + mapDim.getClippingOffsetBottom()) > (tileLoc.toMapRow() + level);

    }

    public static void checkInside() {
        ServerCoordinate playerLocation = World.getPlayer().getLocation();

        MapTile tileAbove = getFirstTileAbove(playerLocation, playerLocation.getZ() + 2, false);
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
                if (Objects.equals(lastInsideGroup, tileAboveGroup)) {
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
            @Nonnull ServerCoordinate startLocation, int zLimit, boolean perceptiveOffset) {
        if (startLocation.getZ() <= zLimit) {
            return null;
        }

        int currentX = startLocation.getX();
        int currentY = startLocation.getY();
        int currentZ = startLocation.getZ();
        while (currentZ > zLimit) {
            if (perceptiveOffset) {
                currentX += MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
                currentY -= MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
            }
            currentZ--;

            MapTile tile = World.getMap().getMapAt(new ServerCoordinate(currentX, currentY, currentZ));
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }

    @Nullable
    private static MapTile getFirstTileAbove(
            @Nonnull ServerCoordinate startLocation, int zLimit, boolean perceptiveOffset) {
        if (startLocation.getZ() >= zLimit) {
            return null;
        }

        int currentX = startLocation.getX();
        int currentY = startLocation.getY();
        int currentZ = startLocation.getZ();
        while (currentZ < zLimit) {
            if (perceptiveOffset) {
                currentX -= MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
                currentY += MapDisplayManager.TILE_PERSPECTIVE_OFFSET;
            }
            currentZ++;

            MapTile tile = World.getMap().getMapAt(new ServerCoordinate(currentX, currentY, currentZ));
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }

    @Nonnull
    private static List<MapGroup> getSurroundingMapGroups(@Nonnull ServerCoordinate startLocation) {
        List<MapGroup> groupList = new ArrayList<>();

        GameMap map = World.getMap();
        //noinspection ConstantConditions
        for (Direction dir : Direction.values()) {
            ServerCoordinate newLoc = new ServerCoordinate(startLocation, dir);
            MapTile tile = map.getMapAt(newLoc);
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
