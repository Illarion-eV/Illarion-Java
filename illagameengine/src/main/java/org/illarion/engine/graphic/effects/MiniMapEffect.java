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
package org.illarion.engine.graphic.effects;

import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;

/**
 * This effect is used to render the mini map properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface MiniMapEffect extends TextureEffect {
    /**
     * Set the center location
     *
     * @param location the center location, this should be the location where the player is located.
     */
    void setCenter(@Nonnull ServerCoordinate location);

    /**
     * Set the radius of the rendered texture.
     *
     * @param x the radius
     */
    void setRadius(int x);
}
