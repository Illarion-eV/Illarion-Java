/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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
package illarion.client.world;

import illarion.client.graphics.MapDisplayManager;
import illarion.common.types.Location;

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
    /**
     * Process a single new tile.
     *
     * @param tile the tile to process
     */
    public static void processTile(final MapTile tile) {
        final Location playerLocation = World.getPlayer().getLocation();

        final MapTile tileAbove = getFirstTileAbove(tile.getLocation(), playerLocation.getScZ() + 2, true);
        final MapTile tileBelow = getFirstTileBelow(tile.getLocation(), playerLocation.getScZ() - 2, true);

        if (tileAbove != null) {
            tile.setObstructingTile(tileAbove);
        }
        if (tileBelow != null) {
            tileBelow.setObstructingTile(tile);
        }

        // check if the tile is at or below the player location. In this case the map groups do not matter.
        if (tile.getLocation().getScZ() <= playerLocation.getScZ()) {
            return;
        }

        final List<MapGroup> groups = getSurroundingMapGroups(tile.getLocation());
        final MapGroup tileGroup;
        if (groups.isEmpty()) {
            tileGroup = new MapGroup();
            tile.setMapGroup(tileGroup);
        } else {
            tileGroup = groups.get(0);
            tile.setMapGroup(tileGroup);
            for (int i = 1; i < groups.size(); i++) {
                groups.get(i).setParent(tileGroup);
            }
        }
        if (tileAbove != null) {
            final MapGroup tileAboveGroup = tileAbove.getMapGroup();
            if (tileAboveGroup != null) {
                tileAboveGroup.addOverwritingGroup(tileGroup);
            }
        }
        if (tileBelow != null) {
            final MapGroup tileBelowGroup = tileBelow.getMapGroup();
            if (tileBelowGroup != null) {
                tileGroup.addOverwritingGroup(tileBelowGroup);
            }
        }
    }

    @Nullable
    private static MapGroup lastInsideGroup;

    public static void checkInside() {
        final Location playerLocation = World.getPlayer().getCharacter().getLocation();

        final MapTile tileAbove = getFirstTileAbove(playerLocation, playerLocation.getScZ() + 2, false);
        final MapGroup realTileAboveGroup = (tileAbove == null) ? null : tileAbove.getMapGroup();
        final MapGroup tileAboveGroup = (realTileAboveGroup == null) ? null : realTileAboveGroup.getRootGroup();

        if (tileAboveGroup == null) {
            if (lastInsideGroup != null) {
                lastInsideGroup.setHidden(false);
                lastInsideGroup = null;
            }
            World.getWeather().setOutside(false);
        } else {
            if (lastInsideGroup != null) {
                if (lastInsideGroup == tileAboveGroup) {
                    return;
                }
                lastInsideGroup.setHidden(false);
            }
            tileAboveGroup.setHidden(true);
            lastInsideGroup = tileAboveGroup;
            World.getWeather().setOutside(true);
        }
    }

    @Nullable
    private static MapTile getFirstTileBelow(final Location startLocation, final int zLimit,
                                             final boolean perceptiveOffset) {
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

            final MapTile tile = World.getMap().getMapAt(currentX, currentY, currentZ);
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }

    private static List<MapTile> getAllTilesAbove(final Location startLocation, final int zLimit,
                                                  final boolean perceptiveOffset) {
        final List<MapTile> tileList = new ArrayList<MapTile>();
        Location currentLocation = startLocation;
        while (true) {
            final MapTile currentTile = getFirstTileAbove(currentLocation, zLimit, perceptiveOffset);
            if (currentTile == null) {
                break;
            }
            tileList.add(currentTile);
            currentLocation = currentTile.getLocation();
        }
        return tileList;
    }

    @Nullable
    private static MapTile getFirstTileAbove(final Location startLocation, final int zLimit,
                                             final boolean perceptiveOffset) {
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

            final MapTile tile = World.getMap().getMapAt(currentX, currentY, currentZ);
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }

    private static List<MapGroup> getSurroundingMapGroups(final Location startLocation) {
        final List<MapGroup> groupList = new ArrayList<MapGroup>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if ((x == 0) && (y == 0)) {
                    continue;
                }
                final MapTile tile = World.getMap().getMapAt(startLocation.getScX() + x, startLocation.getScY() + y,
                        startLocation.getScZ());
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
        }
        return groupList;
    }
}
