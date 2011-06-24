/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.guiNG;

import illarion.client.graphics.MapDisplayManager;

import illarion.common.graphics.MapConstants;

/**
 * This is a helper class that contains functions that are able to detect on
 * what parts of the game screen some screen location is. The main usage is to
 * detect on what part of the GUI the mouse cursor is currently pointing in
 * order to optimize the searched parts of the GUI.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class ScreenLocation {
    /**
     * The left border of the area the player can use item on the map in.
     */
    private static final int USE_RANGE_LEFT =
        (int) (MapDisplayManager.MAP_CENTER_X - (MapConstants.TILE_W * 1.5f));

    /**
     * The lower border of the area the player can use item on the map in.
     */
    private static final int USE_RANGE_LOWER =
        (int) (MapDisplayManager.MAP_CENTER_Y - (MapConstants.TILE_H * 1.5f));

    /**
     * The right border of the area the player can use item on the map in.
     */
    private static final int USE_RANGE_RIGHT =
        (int) (MapDisplayManager.MAP_CENTER_X + (MapConstants.TILE_W * 1.5f));

    /**
     * The upper border of the area the player can use item on the map in.
     */
    private static final int USE_RANGE_UPPER =
        (int) (MapDisplayManager.MAP_CENTER_Y + (MapConstants.TILE_H * 1.5f));

    /**
     * The private constructor is used to avoid that anything creates a instance
     * of this helper function collection.
     */
    private ScreenLocation() {
        // nothing to do here
    }

    /**
     * Check if the screen coordinate is within the use range on the map.
     * 
     * @param screenX the x coordinate of the location
     * @param screenY the y coordinate of the location
     * @return <code>true</code> in case the coordinate are within the use range
     *         on the game map, this how ever does not fit exactly to the use
     *         range and a check if it is on the correct tile is still needed
     */
    public static boolean isInUseRange(final int screenX, final int screenY) {
        return ((screenY >= USE_RANGE_LOWER) && (screenY <= USE_RANGE_UPPER)
            && (screenX >= USE_RANGE_LEFT) && (screenX <= USE_RANGE_RIGHT));
    }
}
