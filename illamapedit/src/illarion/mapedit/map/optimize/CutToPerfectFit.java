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
 * This is a task that cuts the map it has to work on to its perfect fit. This
 * means empty rows and columns of tiles of the map are removed.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class CutToPerfectFit implements OptimizeTask {
    /**
     * Get the name of this optimization task.
     */
    @Override
    public String getName() {
        return "Cut to perfect fit"; //$NON-NLS-1$
    }

    /**
     * Execute the task and optimize the map.
     */
    @Override
    public void optimize(final WorkingCopyMap map) {
        final int count = map.getMapCount();
        final FastTable<Map> remList = new FastTable<Map>();
        final FastTable<Map> newList = new FastTable<Map>();

        for (int mapIndex = 0; mapIndex < count; mapIndex++) {
            final Map workingMap = map.getMap(mapIndex);

            final Location newOrigin = Location.getInstance();
            final Dimension newDimension = new Dimension();
            newOrigin.set(workingMap.getOrigin());
            newDimension.setSize(workingMap.getDimension());

            final MapTile[] mapData = workingMap.getRawMapData();

            final int originalHeight = newDimension.height;
            final int originalWidth = newDimension.width;

            for (int mapPos = 0; mapPos < mapData.length; mapPos++) {
                if ((mapData[mapPos].getTileId() == 0)
                    && (mapData[mapPos].getItemCount() == 0)) {
                    continue;
                }
                mapPos--;

                final int removableLines = (mapPos + 1) / originalWidth;
                newOrigin.addSC(0, removableLines, 0);
                newDimension.height -= removableLines;
                break;
            }

            for (int mapPos = mapData.length - 1; mapPos >= 0; mapPos--) {
                if ((mapData[mapPos].getTileId() == 0)
                    && (mapData[mapPos].getItemCount() == 0)) {
                    continue;
                }
                mapPos++;

                final int removableLines =
                    (mapData.length - mapPos) / originalWidth;
                newDimension.height -= removableLines;
                break;
            }

            for (int mapPos = 0; mapPos < mapData.length; mapPos++) {
                final int realMapPos =
                    ((mapPos % originalHeight) * originalWidth)
                        + (mapPos / originalHeight);
                if ((mapData[realMapPos].getTileId() == 0)
                    && (mapData[realMapPos].getItemCount() == 0)) {
                    continue;
                }
                mapPos--;

                final int removableLines = (mapPos + 1) / originalHeight;
                newOrigin.addSC(removableLines, 0, 0);
                newDimension.width -= removableLines;
                break;
            }

            for (int mapPos = mapData.length - 1; mapPos >= 0; mapPos--) {
                final int realMapPos =
                    ((mapPos % originalHeight) * originalWidth)
                        + (mapPos / originalHeight);
                if ((mapData[realMapPos].getTileId() == 0)
                    && (mapData[realMapPos].getItemCount() == 0)) {
                    continue;
                }
                mapPos++;

                final int removableLines =
                    (mapData.length - mapPos) / originalHeight;
                newDimension.width -= removableLines;
                break;
            }

            if ((newDimension.width == originalWidth)
                && (newDimension.height == originalHeight)) {
                continue;
            }

            remList.add(workingMap);

            if ((newDimension.width == 0) || (newDimension.height == 0)) {
                continue;
            }

            final Map newMap =
                new Map(workingMap.getMapName(), newOrigin, newDimension);

            final Location tempLoc = Location.getInstance();
            for (int x = 0; x < newDimension.width; x++) {
                for (int y = 0; y < newDimension.height; y++) {
                    tempLoc.set(newOrigin);
                    tempLoc.addSC(x, y, 0);
                    newMap.getTile(tempLoc).set(workingMap.getTile(tempLoc));
                }
            }
            tempLoc.recycle();
            newOrigin.recycle();
            newList.add(newMap);
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
