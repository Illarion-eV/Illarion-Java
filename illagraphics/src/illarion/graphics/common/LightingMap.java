/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.common;

import illarion.common.util.Location;

import illarion.graphics.SpriteColor;

/**
 * The lightmap interface is used to handle the light effects on the map. It
 * allows to set, render and reset light and allows to check if a tile blocks
 * the line of sight or accepts no light.
 * 
 * @author Nop
 * @author Martin Karing
 * @version 1.21
 * @since 2.00
 */
public interface LightingMap {
    /**
     * This value is returned by {@link #blocksView(Location)} in case the view
     * is fully blocked.
     */
    int BLOCKED_VIEW = 1000;

    /**
     * Determines whether a map location accepts the light from a specific
     * direction.
     * 
     * @param loc the location on that is checked
     * @param dx x part of the direction of the light ray
     * @param dy y part of the direction of the light ray
     * @return true if location accepts from this direction
     */
    boolean acceptsLight(Location loc, int dx, int dy);

    /**
     * Determines whether a map location blocks the flow of light.
     * 
     * @param loc the location on the map
     * @return obscurity, 0 is for free view, {@link #BLOCKED_VIEW} for fully
     *         blocked
     */
    int blocksView(Location loc);

    /**
     * Start rendering lights after calculations are finished.
     */
    void renderLights();

    /**
     * Reset all lights in the map to zero.
     */
    void resetLights();

    /**
     * Assign the cumulative light value to a map tile.
     * 
     * @param loc the location on the map the light is assigned to
     * @param color the color that is assigned to the tile
     */
    void setLight(Location loc, SpriteColor color);
}
