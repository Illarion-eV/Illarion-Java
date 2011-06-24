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
package illarion.mapedit.graphics;

/**
 * This interface allows the entities to fetch informations about selected
 * objects on the display.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public interface Selectable {
    /**
     * Check if this object is blocked.
     * 
     * @return <code>true</code> in case the object is blocked
     */
    boolean isBlocked();

    /**
     * Check if this object is marked as selectable. In case its not selectable
     * the object should be faded out some how.
     * 
     * @return <code>true</code> in case the object is selectable
     */
    boolean isSelectable();

    /**
     * Check if the object is selected.
     * 
     * @return <code>true</code> in case the object is selected right now
     */
    boolean isSelected();
}
