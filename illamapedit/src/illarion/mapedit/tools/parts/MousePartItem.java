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

import illarion.mapedit.history.HistoryActionItem;
import illarion.mapedit.history.HistoryEntry;
import illarion.mapedit.map.MapItem;
import illarion.mapedit.map.MapStorage;
import illarion.mapedit.map.MapTile;

import illarion.common.util.Location;

/**
 * A item mouse part describes a item that is bound to on mouse button and is
 * placed on the map upon clicking it.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MousePartItem extends AbstractMousePart {
    /**
     * Item placement mode that causes that items are added to the tile, no
     * matter what.
     */
    public static final int MODE_ADD = 0;

    /**
     * Item placement mode that causes that the that are already placed on the
     * map are replaced by the new tile.
     */
    public static final int MODE_REPLACE = 2;

    /**
     * Item placement mode that causes that items are placed on the tiles only
     * in case the tiles are empty.
     */
    public static final int MODE_UNIQUE = 1;

    /**
     * The currently used mode to place items on the tiles.
     */
    private static int placementMode = MODE_ADD;

    /**
     * Constructor with default access level so the parts manager is able to
     * recycle this objects correctly.
     */
    MousePartItem() {
        // nothing to do
    }

    /**
     * Get the placement mode that is used for placeing items on the map.
     * 
     * @return the mode that is used to place items on the map
     * @see #MODE_ADD
     * @see #MODE_REPLACE
     * @see #MODE_UNIQUE
     */
    public static int getPlacementMode() {
        return placementMode;
    }

    /**
     * Set the new placement mode that is used when placing tiles on the map.
     * 
     * @param newMode the new map mode
     * @see #MODE_ADD
     * @see #MODE_REPLACE
     * @see #MODE_UNIQUE
     */
    @SuppressWarnings("nls")
    public static void setPlacementMode(final int newMode) {
        if ((newMode != MODE_ADD) && (newMode != MODE_UNIQUE)
            && (newMode != MODE_REPLACE)) {
            throw new IllegalArgumentException("Illegal Mode: "
                + Integer.toString(newMode));
        }
        placementMode = newMode;
    }

    /**
     * Get the type of this mouse part.
     * 
     * @return always
     *         {@link illarion.mapedit.tools.parts.AbstractMousePart#TYPE_ITEM}
     */
    @Override
    public int getType() {
        return TYPE_ITEM;
    }

    /**
     * Place the item at one location on the map.
     * 
     * @param loc the location where the item shall be placed
     * @param entry the history entry changes to the items should be saved at
     */
    @Override
    public void placeAt(final Location loc, final HistoryEntry entry) {
        final MapTile tile = MapStorage.getInstance().getMapTile(loc);
        if (tile == null) {
            return;
        }
        if (getId() == 0) {
            final MapItem item = tile.removeTopItem();
            if (item != null) {
                entry.addAction(new HistoryActionItem(item.getItemId(), item
                    .getQuality(), item.getData(), loc,
                    HistoryActionItem.DELETE));
            }
        } else {
            if ((placementMode == MODE_UNIQUE) && (tile.getItemCount() > 0)) {
                return;
            }
            if ((placementMode == MODE_REPLACE) && (tile.getItemCount() > 0)) {
                final MapItem item = tile.removeTopItem();
                entry.addAction(new HistoryActionItem(item.getItemId(), item
                    .getQuality(), item.getData(), loc,
                    HistoryActionItem.DELETE));
            }
            tile.addItem(getId(), 333, 0);
            entry.addAction(new HistoryActionItem(getId(), 333, 0, loc,
                HistoryActionItem.ADD));
        }
    }
}
