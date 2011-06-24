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

import java.util.ArrayList;
import java.util.List;

import illarion.mapedit.MapEditor;

/**
 * This class is supposed to optimize a map to the minimal storage usage.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MapOptimizer {
    /**
     * The default text that is displayed for every optimization step.
     */
    @SuppressWarnings("nls")
    private static final String OPT_MSG = "Optimize Map - Step %1$d: %2$s";

    /**
     * The list of optimization tasks that shall be done during the optimization
     * of the map.
     */
    private final List<OptimizeTask> tasks;

    /**
     * The constructor that prepares the tasks needed to be done during a
     * optimization.
     */
    public MapOptimizer() {
        tasks = new ArrayList<OptimizeTask>();
        tasks.add(new CutToPerfectFit());
        tasks.add(new SplitSeperatedMaps());
    }

    /**
     * Optimize a map with all available tasks that are registered to this
     * optimizer.
     * 
     * @param map the map that shall be optimized
     */
    public void optimize(final WorkingCopyMap map) {
        final int count = tasks.size();

        String msg;
        int stepCnt = 0;
        int oldCount = 0;
        while (map.getMapCount() != oldCount) {
            oldCount = map.getMapCount();
            for (int i = 0; i < count; i++) {
                stepCnt++;
                msg =
                    String.format(OPT_MSG, Integer.valueOf(stepCnt), tasks
                        .get(i).getName());

//                MapEditor.getMainFrame().getMessageLine().addMessage(msg);
                tasks.get(i).optimize(map);
//                MapEditor.getMainFrame().getMessageLine().removeMessage(msg);
            }
        }

        map.nameAllNewMaps();
    }
}
