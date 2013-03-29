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

import illarion.common.types.Rectangle;
import org.illarion.engine.graphic.Sprite;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;

/**
 * This is the shared implementation of a sprite. It does only implement the required functions to hold the default
 * data of the sprites across the different backend implementations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public abstract class AbstractSprite<T extends Texture> implements Sprite {
    /**
     * The textures assigned to this sprite.
     */
    private final T[] textures;

    /**
     * The X offset applied to the texture when rendering.
     */
    private final int offsetX;

    /**
     * The Y offset applied to the sprite when rendering.
     */
    private final int offsetY;

    /**
     * The X coordinate of the center coordinate of the sprite.
     */
    private final float centerX;

    /**
     * The Y coordinate of the center coordinate of the sprite.
     */
    private final float centerY;

    /**
     * This mirror flag. In case its set {@code true} the sprite will be rendered vertically mirrored.
     */
    private final boolean mirror;

    /**
     * Create a abstract sprite.
     *
     * @param textures the textures that are the frames of this sprite
     * @param offsetX  the x offset of the sprite
     * @param offsetY  the y offset of the sprite
     * @param centerX  the offset of the center point long the x coordinate
     * @param centerY  the offset of the center point long the y coordinate
     * @param mirror   the mirrored flag
     */
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

    @Override
    public int getWidth() {
        return textures[0].getWidth();
    }

    @Override
    public int getHeight() {
        return textures[0].getHeight();
    }

    @Override
    public int getFrames() {
        return textures.length;
    }

    @Override
    @Nonnull
    public T getFrame(final int frame) {
        if ((frame < 0) || (frame >= textures.length)) {
            throw new IndexOutOfBoundsException("Frame out of bounds: " + frame);
        }
        return textures[frame];
    }

    @Override
    public int getOffsetX() {
        return offsetX;
    }

    @Override
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

    @Override
    public Rectangle getDisplayArea(final int x, final int y, final float scale, final float rotation,
                                    @Nullable final Rectangle storage) {
        @Nonnull final Rectangle targetRectangle;
        if (storage == null) {
            targetRectangle = new Rectangle();
        } else {
            targetRectangle = storage;
        }

        final int displayX = (int) (x + ((offsetX + (getWidth() * centerX)) * scale));
        final int displayY = (int) (y + ((offsetY + (getHeight() * centerY)) * scale));
        final int displayWidth = (int) (getWidth() * scale);
        final int displayHeight = (int) (getHeight() * scale);
        targetRectangle.set(displayX, displayY, displayWidth, displayHeight);

        return targetRectangle;
    }
}
