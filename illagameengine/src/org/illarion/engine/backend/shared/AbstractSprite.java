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
package org.illarion.engine.backend.shared;

import org.illarion.engine.graphic.Sprite;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * This is the shared implementation of a sprite. It does only implement the required functions to hold the default
 * data of the sprites across the different backend implementations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractSprite<T extends Texture> implements Sprite {
    private final T[] textures;
    private final int offsetX;
    private final int offsetY;
    private final float centerX;
    private final float centerY;
    private final boolean mirror;

    protected AbstractSprite(@Nonnull final T[] textures, final int offsetX, final int offsetY,
                             final float centerX, final float centerY, final boolean mirror) {
        if (textures.length == 0) {
            throw new IllegalArgumentException("Amount of textures does not fit.");
        }
        final int width = textures[0].getWidth();
        final int height = textures[0].getHeight();
        for (final Texture texture : textures) {
            if ((texture.getWidth() != width) || (texture.getHeight() != height)) {
                throw new IllegalArgumentException("Sizes of textures do not match.");
            }
        }

        this.textures = Arrays.copyOf(textures, textures.length);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.centerX = centerX;
        this.centerY = centerY;
        this.mirror = mirror;
    }

    public int getFrames() {
        return textures.length;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public boolean isMirrored() {
        return mirror;
    }
}
