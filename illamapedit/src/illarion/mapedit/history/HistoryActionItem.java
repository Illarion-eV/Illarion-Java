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
package illarion.mapedit.history;

import org.apache.log4j.Logger;

import illarion.mapedit.map.MapStorage;
import illarion.mapedit.map.MapTile;

import illarion.common.util.Location;

/**
 * A history action entry that contains a change done to the items on the map.
 * This either includes a delete or a adding operation.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class HistoryActionItem extends AbstractHistoryAction {
    /**
     * Constant for a add operation. Means the a tile with the data supplied to
     * this class was added from the map.
     */
    public static final int ADD = 1;

    /**
     * Constant for a delete operation. Means the a tile with the data supplied
     * to this class was deleted from the map.
     */
    public static final int DELETE = 0;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(HistoryActionItem.class);

    /**
     * The serialization UID of the class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The mode of the action.
     */
    private final int actionMode;

    /**
     * The data of the item that got changed.
     */
    private final int itemData;

    /*
     * + The ID of the item that got changed.
     */
    private final int itemId;

    /**
     * The location of the item that got changed.
     */
    private final Location itemLoc;

    /**
     * The quality of the item that got changed.
     */
    private final int itemQual;

    /**
     * Create a new action for a history entry that effects the items on the
     * map.
     * 
     * @param id the ID of the item effected
     * @param quality the quality of the item effected
     * @param data the data of the item effected
     * @param loc the location of the item effected
     * @param mode the mode how the item is changed, {@link #DELETE} or
     *            {@link #ADD}
     */
    public HistoryActionItem(final int id, final int quality, final int data,
        final Location loc, final int mode) {
        itemId = id;
        itemQual = quality;
        itemData = data;
        itemLoc = Location.getInstance();
        itemLoc.set(loc);
        actionMode = mode;
    }

    /**
     * Perform the action stored in this object again. This should only be done
     * in case the action was undone before.
     */
    @Override
    @SuppressWarnings("nls")
    public void redo() {
        if (actionMode == ADD) {
            final MapTile tile = MapStorage.getInstance().getMapTile(itemLoc);
            if (tile == null) {
                LOGGER.warn("Redo operation pointed to non-existant tile");
                return;
            }
            tile.addItem(itemId, itemQual, itemData);
        } else if (actionMode == DELETE) {
            final MapTile tile = MapStorage.getInstance().getMapTile(itemLoc);
            if (tile == null) {
                LOGGER.warn("Redo operation pointed to non-existant tile");
                return;
            }
            tile.removeTopItem();
        }
    }

    /**
     * Revert the action defined in this object.
     */
    @Override
    @SuppressWarnings("nls")
    public void undo() {
        if (actionMode == DELETE) {
            final MapTile tile = MapStorage.getInstance().getMapTile(itemLoc);
            if (tile == null) {
                LOGGER.warn("Redo operation pointed to non-existant tile");
                return;
            }
            tile.addItem(itemId, itemQual, itemData);
        } else if (actionMode == ADD) {
            final MapTile tile = MapStorage.getInstance().getMapTile(itemLoc);
            if (tile == null) {
                LOGGER.warn("Redo operation pointed to non-existant tile");
                return;
            }
            tile.removeTopItem();
        }
    }

}
