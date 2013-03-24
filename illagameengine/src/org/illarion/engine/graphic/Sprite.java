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

import illarion.common.types.Rectangle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * This class defines a sprite. A sprite is basically a single or multiple textures with additional data.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public interface Sprite {
    /**
     * Get one of the sprites frames.
     *
     * @param index the index of the frame to receive
     * @return the texture assigned to the frame index
     * @throws IndexOutOfBoundsException in case index is less then zero or larger or equal then the amount of frames
     */
    @Nonnull
    Texture getFrame(int index);

    /**
     * Get the amount of frames of the sprite.
     *
     * @return the amount of frames
     */
    int getFrames();

    /**
     * Get the area that is covered on the screen in case the sprite is rendered.
     *
     * @param x        the x coordinate of the location the sprite is rendered to
     * @param y        the y coordinate of the location the sprite is rendered to
     * @param scale    the scale of the sprite
     * @param rotation the rotation applied to the sprite
     * @param storage  the rectangle instance that is supposed to be used to store the data,
     *                 in case this is {@code null} there will be a new instance created and returned
     * @return the rectangle of the covered area
     */
    Rectangle getDisplayArea(int x, int y, float scale, float rotation, @Nullable Rectangle storage);

    /**
     * Get the height of the sprite.
     *
     * @return the height of the sprite
     */
    int getHeight();

    /**
     * Get the width of the sprite.
     *
     * @return the width of the sprite
     */
    int getWidth();
}
