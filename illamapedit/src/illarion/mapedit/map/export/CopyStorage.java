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
package illarion.mapedit.map.export;

import illarion.mapedit.MapEditor;

/**
 * The copy storage stores copied data for later insert operations.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class CopyStorage {
    /**
     * The singleton instance of this class.
     */
    private static final CopyStorage INSTANCE = new CopyStorage();

    /**
     * The map that was copied last, ready to be insert.
     */
    private CopyMap copiedMap;

    /**
     * The private constructor of this class to avoid any instances but the
     * singleton instance.
     */
    private CopyStorage() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static CopyStorage getInstance() {
        return INSTANCE;
    }

    /**
     * Get the map data that shall be insert at the next insert operation.
     * 
     * @return the copied map data
     */
    public CopyMap getCopiedMap() {
        return copiedMap;
    }

    /**
     * Set the copied map.
     * 
     * @param newCopiedMap the copied map that is avaiable from now on for
     *            insert operations
     */
    public void setCopiedMap(final CopyMap newCopiedMap) {
        copiedMap = newCopiedMap;
//        MapEditor.getMainFrame().getMenubar().validateInsert();
    }
}
