/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.map.optimize;

import java.awt.Dimension;

import javolution.util.FastTable;

import illarion.mapedit.map.Map;
import illarion.mapedit.map.MapTile;

import illarion.common.util.Location;

/**
 * This optimization task splits maps that are technical on one map but are
 * visual split in two. Means at least one row or column of empty tiles has to
 * span over the whole map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class SplitSeperatedMaps implements OptimizeTask {
    /**
     * Get the human readable name of this task.
     */
    @SuppressWarnings("nls")
    @Override
    public String getName() {
        return "Split up map";
    }

    /**
     * Optimize the current working maps.
     */
    @SuppressWarnings("nls")
    @Override
    public void optimize(final WorkingCopyMap map) {
        final int count = map.getMapCount();
        final FastTable<Map> remList = FastTable.newInstance();
        final FastTable<Map> newList = FastTable.newInstance();

        for (int mapIndex = 0; mapIndex < count; mapIndex++) {
            final Map workingMap = map.getMap(mapIndex);
            final MapTile[] mapData = workingMap.getRawMapData();

            final Dimension mapDim = workingMap.getDimension();
            int index;
            boolean foundFreeStripe = false;
            int stripePos = 0;
            for (int h = mapDim.height - 1; h >= 0; h--) {
                foundFreeStripe = true;
                for (int w = mapDim.width - 1; w >= 0; w--) {
                    index = (h * mapDim.width) + w;

                    if ((mapData[index].getTileId() == 0)
                        && (mapData[index].getItemCount() == 0)) {
                        continue;
                    }

                    foundFreeStripe = false;
                    break;
                }

                if (foundFreeStripe) {
                    stripePos = h;
                    break;
                }
            }
            if (foundFreeStripe) {
                final Location map1Origin = workingMap.getOrigin();
                final Dimension map1Dim =
                    new Dimension(mapDim.width, stripePos - 1);

                final Location map2Origin = Location.getInstance();
                map2Origin.setSC(map1Origin.getScX(), map1Origin.getScY()
                    + map1Dim.height + 1, map1Origin.getScZ());
                final Dimension map2Dim =
                    new Dimension(mapDim.width, mapDim.height - stripePos);

                final Map newMap1 =
                    new Map(workingMap.getMapName(), map1Origin, map1Dim);
                final Map newMap2 =
                    new Map(workingMap.getMapName(), map2Origin, map2Dim);

                final Location tempLoc = Location.getInstance();
                for (int x = 0; x < map1Dim.width; x++) {
                    for (int y = 0; y < map1Dim.height; y++) {
                        tempLoc.set(map1Origin);
                        tempLoc.addSC(x, y, 0);
                        newMap1.getTile(tempLoc).set(
                            workingMap.getTile(tempLoc));
                    }
                }

                for (int x = 0; x < map2Dim.width; x++) {
                    for (int y = 0; y < map2Dim.height; y++) {
                        tempLoc.set(map2Origin);
                        tempLoc.addSC(x, y, 0);
                        newMap2.getTile(tempLoc).set(
                            workingMap.getTile(tempLoc));
                    }
                }
                tempLoc.recycle();

                newList.add(newMap1);
                newList.add(newMap2);
                remList.add(workingMap);
                continue;
            }

            foundFreeStripe = false;
            stripePos = 0;

            for (int w = mapDim.width - 1; w >= 0; w--) {
                foundFreeStripe = true;
                for (int h = mapDim.height - 1; h >= 0; h--) {
                    index = (h * mapDim.width) + w;

                    if ((mapData[index].getTileId() == 0)
                        && (mapData[index].getItemCount() == 0)) {
                        continue;
                    }

                    foundFreeStripe = false;
                    break;
                }

                if (foundFreeStripe) {
                    stripePos = w;
                    break;
                }
            }

            if (foundFreeStripe) {
                final Location map1Origin = workingMap.getOrigin();
                final Dimension map1Dim =
                    new Dimension(stripePos - 1, mapDim.height);

                final Location map2Origin = Location.getInstance();
                map2Origin.setSC(map1Origin.getScX() + map1Dim.width + 1,
                    map1Origin.getScY(), map1Origin.getScZ());
                final Dimension map2Dim =
                    new Dimension(mapDim.width - stripePos, mapDim.height);

                final Location tempLoc = Location.getInstance();

                if ((map1Dim.width > 0) && (map1Dim.height > 0)) {
                    final Map newMap1 =
                        new Map(workingMap.getMapName(), map1Origin, map1Dim);
                    for (int x = 0; x < map1Dim.width; x++) {
                        for (int y = 0; y < map1Dim.height; y++) {
                            tempLoc.set(map1Origin);
                            tempLoc.addSC(x, y, 0);
                            newMap1.getTile(tempLoc).set(
                                workingMap.getTile(tempLoc));
                        }
                    }
                    newList.add(newMap1);
                }

                if ((map2Dim.width > 0) && (map2Dim.height > 0)) {
                    final Map newMap2 =
                        new Map(workingMap.getMapName(), map2Origin, map2Dim);
                    for (int x = 0; x < map2Dim.width; x++) {
                        for (int y = 0; y < map2Dim.height; y++) {
                            tempLoc.set(map2Origin);
                            tempLoc.addSC(x, y, 0);
                            newMap2.getTile(tempLoc).set(
                                workingMap.getTile(tempLoc));
                        }
                    }
                    newList.add(newMap2);
                }
                tempLoc.recycle();

                remList.add(workingMap);
            }
        }

        final int remCount = remList.size();
        for (int i = 0; i < remCount; i++) {
            map.removeMap(remList.get(i));
        }
        remList.clear();

        final int newCount = newList.size();
        for (int i = 0; i < newCount; i++) {
            map.addMap(newList.get(i));
        }
        newList.clear();

        FastTable.recycle(remList);
        FastTable.recycle(newList);
    }
}
