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
package org.illarion.engine.nifty;

import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;

/**
 * This is the render image implementation that uses a texture that is rendered to the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class IgeTextureRenderImage implements IgeRenderImage {
    /**
     * The texture that is rendered with this image.
     */
    @Nonnull
    private final Texture texture;

    /**
     * Create a new instance of this render image that points to a specific image.
     *
     * @param texture the texture that is rendered by this render image
     */
    public IgeTextureRenderImage(@Nonnull Texture texture) {
        this.texture = texture;
    }

    @Override
    public int getWidth() {
        return texture.getWidth();
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    @Nonnull
    public Texture getTexture() {
        return texture;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    @Override
    public void renderImage(
            @Nonnull Graphics g,
            int x,
            int y,
            int width,
            int height,
            @Nonnull Color color,
            float imageScale) {
        int scaledWidth = Math.round(width * imageScale);
        int scaledHeight = Math.round(height * imageScale);
        int fixedX = x + ((width - scaledWidth) / 2);
        int fixedY = y + ((height - scaledHeight) / 2);
        g.drawTexture(texture, fixedX, fixedY, scaledWidth, scaledHeight, color);
    }

    @Override
    public void renderImage(
            @Nonnull Graphics g,
            int x,
            int y,
            int w,
            int h,
            int srcX,
            int srcY,
            int srcW,
            int srcH,
            @Nonnull Color color,
            float scale,
            int centerX,
            int centerY) {
        int scaledWidth = Math.round(w * scale);
        int scaledHeight = Math.round(h * scale);
        int fixedX = (int) Math.round(x + ((w - scaledWidth) * ((double) w / (double) centerX)));
        int fixedY = (int) Math.round(y + ((h - scaledHeight) * ((double) h / (double) centerY)));
        g.drawTexture(texture, fixedX, fixedY, scaledWidth, scaledHeight, srcX, srcY, srcW, srcH, color);
    }
}
