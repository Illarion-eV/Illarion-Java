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
 * A history action that stores the change of one tile.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class HistoryActionTile extends AbstractHistoryAction {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(HistoryActionTile.class);

    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The ID of the tile after the change was done
     */
    private final int newTileId;

    /**
     * The ID of the tile before the change was done.
     */
    private final int oldTileId;

    /**
     * The location of the tile.
     */
    private final Location tileLocation;

    /**
     * Create a history action that contains a tile change.
     * 
     * @param oldId the ID of the tile before the change
     * @param newId the ID of the tile after the change
     * @param loc the location where the tile is located
     */
    public HistoryActionTile(final int oldId, final int newId,
        final Location loc) {
        oldTileId = oldId;
        newTileId = newId;
        tileLocation = Location.getInstance();
        tileLocation.set(loc);
    }

    /**
     * Perform the action stored in this object again. This should only be done
     * in case the action was undone before.
     */
    @SuppressWarnings("nls")
    @Override
    public void redo() {
        final MapTile tile = MapStorage.getInstance().getMapTile(tileLocation);
        if (tile == null) {
            LOGGER.warn("Redo operation pointed to non-existant tile");
            return;
        }
        if (tile.getTileId() != oldTileId) {
            LOGGER.warn("Repo operation is not consistent.");
            return;
        }
        tile.setTileId(newTileId);
    }

    /**
     * Revert the action defined in this object.
     */
    @SuppressWarnings("nls")
    @Override
    public void undo() {
        final MapTile tile = MapStorage.getInstance().getMapTile(tileLocation);
        if (tile == null) {
            LOGGER.warn("Undo operation pointed to non-existant tile");
            return;
        }
        if (tile.getTileId() != newTileId) {
            LOGGER.warn("Undo operation is not consistent.");
            return;
        }
        tile.setTileId(oldTileId);
    }

}
