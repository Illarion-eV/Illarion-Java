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
package org.illarion.engine.backend.shared;

import illarion.common.types.Rectangle;
import illarion.common.util.FastMath;
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
    @Nonnull
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
     * The rectangle that defines the area the sprite is displayed in.
     */
    @Nonnull
    private final Rectangle displayRectangle;

    /**
     * Create a abstract sprite.
     *
     * @param textures the textures that are the frames of this sprite
     * @param offsetX the x offset of the sprite
     * @param offsetY the y offset of the sprite
     * @param centerX the offset of the center point long the x coordinate
     * @param centerY the offset of the center point long the y coordinate
     * @param mirror the mirrored flag
     */
    protected AbstractSprite(
            @Nonnull T[] textures,
            int offsetX,
            int offsetY,
            float centerX,
            float centerY,
            boolean mirror) {
        if (textures.length == 0) {
            throw new IllegalArgumentException("Amount of textures does not fit.");
        }
        int width = textures[0].getWidth();
        int height = textures[0].getHeight();
        for (Texture texture : textures) {
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

        double centerTransX = width * centerX;
        double centerTransY = height * centerY;

        long realOffsetX;
        if (mirror) {
            realOffsetX = Math.round(-centerTransX - offsetX);
        } else {
            realOffsetX = Math.round(-centerTransX + offsetX);
        }
        long realOffsetY = Math.round(-centerTransY - offsetY);
        displayRectangle = new Rectangle((int) realOffsetX, (int) realOffsetY, width, height);
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
    public T getFrame(int frame) {
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

    @Nonnull
    @Override
    public Rectangle getDisplayArea(
            int x, int y, double scale, double rotation, @Nullable Rectangle storage) {
        @Nonnull Rectangle targetRectangle = (storage == null) ? new Rectangle() : storage;

        long displayWidth = FastMath.floor(displayRectangle.getWidth() * scale);
        long displayHeight = FastMath.floor(displayRectangle.getHeight() * scale);
        long displayX;
        if (isMirrored()) {
            displayX = FastMath.floor(x - (displayRectangle.getX() * scale) - displayWidth);
        } else {
            displayX = FastMath.floor(x + (displayRectangle.getX() * scale));
        }
        long displayY = FastMath.floor(y + (displayRectangle.getY() * scale));
        targetRectangle.set((int) displayX, (int) displayY, (int) displayWidth, (int) displayHeight);

        return targetRectangle;
    }
}
