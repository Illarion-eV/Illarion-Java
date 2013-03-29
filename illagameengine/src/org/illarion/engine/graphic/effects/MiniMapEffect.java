/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.graphic.effects;

import illarion.common.types.Location;

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
    void setCenter(@Nonnull Location location);

    /**
     * Set the radius of the rendered texture.
     *
     * @param x the radius
     */
    void setRadius(int x);
}
