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

/**
 * This is the default definition for a optimization task the optimizer is able
 * to execute on a map in order to improve it automatically.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public interface OptimizeTask {
    /**
     * Get the name of this task.
     * 
     * @return a string with the name of this task
     */
    String getName();

    /**
     * Optimize a map stored in this working copy.
     * 
     * @param map the map that shall be optimized
     */
    void optimize(WorkingCopyMap map);
}
