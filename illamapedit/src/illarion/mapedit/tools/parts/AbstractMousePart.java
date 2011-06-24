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
package illarion.mapedit.tools.parts;

import illarion.mapedit.history.HistoryEntry;

import illarion.common.util.Location;

/**
 * A abstract mouse part is a tile or a item that is connected to a mouse cursor
 * and can be placed on the position the mouse cursor is currently pointing at.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public abstract class AbstractMousePart {
    /**
     * Constant that is returned in case this object defines a item.
     */
    public static final int TYPE_ITEM = 2;

    /**
     * Constant that is returned in case this object defines a tile.
     */
    public static final int TYPE_TILE = 1;

    /**
     * The ID of this object.
     */
    private int id;

    /**
     * The listener that is informed in case the mouse part is disabled.
     */
    private DisableListener listener;

    public void finishExecution() {

    }

    /**
     * Get the ID of this object.
     * 
     * @return the ID of this object
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the type of this object.
     * 
     * @return the type of the object
     * @see #TYPE_ITEM
     * @see #TYPE_TILE
     */
    public abstract int getType();

    /**
     * Place the object at some location on the map.
     * 
     * @param loc the location where the object needs to be placed
     * @param entry the history entry the changes done by this mouse part shall
     *            be added to
     */
    public abstract void placeAt(Location loc, HistoryEntry entry);

    /**
     * Set the new ID of this object.
     * 
     * @param newId the new id
     */
    public final void setId(final int newId) {
        id = newId;
    }

    public void startExecution() {

    }

    /**
     * Report to the listener of this mouse part that it is disabled. After this
     * is done the link to the listener is released.
     */
    final void reportDisabled() {
        if (listener != null) {
            listener.reportDisable();
            listener = null;
        }
    }

    /**
     * Set the disable listener that is informed in case the mouse part is
     * disabled.
     * 
     * @param list the listener object
     */
    final void setDisableListener(final DisableListener list) {
        listener = list;
    }
}
