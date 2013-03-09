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
package org.illarion.engine.graphic;

import org.illarion.engine.Disposable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This class represents a single texture.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@NotThreadSafe
public interface Texture extends Disposable {
    /**
     * Get a new texture object that points to a part of this original texture.
     *
     * @param x      the x coordinate of the new texture
     * @param y      the y coordinate of the new texture
     * @param width  the width of the new texture
     * @param height the height of the new texture
     * @return the new sub-texture instance
     */
    @Nonnull
    Texture getSubTexture(int x, int y, int width, int height);

    /**
     * Get the height of the texture.
     *
     * @return the height of this texture in pixel.
     */
    int getHeight();

    /**
     * Get the width of the texture.
     *
     * @return the width of this texture in pixel.
     */
    int getWidth();
}
