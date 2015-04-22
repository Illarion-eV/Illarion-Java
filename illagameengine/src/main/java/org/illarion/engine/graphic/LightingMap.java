/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package org.illarion.engine.graphic;

import illarion.common.types.ServerCoordinate;

/**
 * The light map interface is used to handle the light effects on the map. It allows to set, render and reset light
 * and allows to check if a tile blocks the line of sight or accepts no light.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface LightingMap {
    /**
     * This value is returned by {@link #blocksView(ServerCoordinate)} in case the view
     * is fully blocked.
     */
    int BLOCKED_VIEW = 1000;

    /**
     * Determines whether a map location accepts the light from a specific
     * direction.
     *
     * @param coordinate the location on that is checked
     * @param dx x part of the direction of the light ray
     * @param dy y part of the direction of the light ray
     * @return true if location accepts from this direction
     */
    boolean acceptsLight(ServerCoordinate coordinate, int dx, int dy);

    /**
     * Determines whether a map location blocks the flow of light.
     *
     * @param coordinate the location on the map
     * @return obscurity, 0 is for free view, {@link #BLOCKED_VIEW} for fully
     * blocked
     */
    int blocksView(ServerCoordinate coordinate);

    /**
     * Start rendering lights after calculations are finished.
     */
    void renderLights();

    /**
     * Assign the cumulative light value to a map tile.
     *
     * @param coordinate the location on the map the light is assigned to
     * @param color the color that is assigned to the tile
     */
    void setLight(ServerCoordinate coordinate, Color color);
}
