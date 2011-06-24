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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javolution.util.FastList;

import illarion.mapedit.map.Map;

/**
 * This class stores the working copy of the map for the optimizer. It will will
 * not cause any change to the orignal map.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class WorkingCopyMap {
    /**
     * The replace pattern used to name the maps properly.
     */
    @SuppressWarnings("nls")
    private static final Pattern REPLACE_PATTERN = Pattern
        .compile("(.+)(_[-0-9]+)");

    /**
     * The map that is the original stores in this working copy.
     */
    private final Map originalMap;

    /**
     * The list of maps that were created from the original map due the
     * optimization.
     */
    private final List<Map> resultMaps;

    /**
     * Constructor for a working copy of the map for the optimizer. This
     * constructor needs the map that is wrapped in this working copy.
     * 
     * @param map the original map
     */
    public WorkingCopyMap(final Map map) {
        originalMap = map;
        resultMaps = new FastList<Map>();
    }

    /**
     * Add a map to the internal storage.
     * 
     * @param map the map that is supposed to be added
     */
    public void addMap(final Map map) {
        resultMaps.add(map);
    }

    /**
     * Return the map at a given index. This working copy is able to store
     * multiple maps created during the optimization. The first index is 0.
     * 
     * @param index the index of the map that is requested
     * @return the map that is requested
     */
    public Map getMap(final int index) {
        if (resultMaps.isEmpty()) {
            return originalMap;
        }
        return resultMaps.get(index);
    }

    /**
     * Get the count of maps that are stored in this working copy.
     * 
     * @return the amount of maps to process
     */
    public int getMapCount() {
        return Math.max(1, resultMaps.size());
    }

    /**
     * Give all maps a unique name to work with in in future.
     */
    public void nameAllNewMaps() {
        if (resultMaps.isEmpty()) {
            return;
        }

        int current = 0;
        Matcher match;
        for (final Map map : resultMaps) {
            match = REPLACE_PATTERN.matcher(map.getMapName());
            map.setMapName(match.replaceAll("$1" + Integer.toString(current)
                + "$2"));
            current++;
        }
    }

    /**
     * Remove a map stored at a given index. This map won't be accessible
     * anymore.
     * 
     * @param index the index of the map to remove
     */
    public void removeMap(final int index) {
        if (resultMaps.isEmpty()) {
            return;
        }
        resultMaps.remove(index);
    }

    /**
     * Remove a map object from the list of result maps.
     * 
     * @param map the map to remove
     */
    public void removeMap(final Map map) {
        if (resultMaps.isEmpty()) {
            return;
        }
        resultMaps.remove(map);
    }
}
