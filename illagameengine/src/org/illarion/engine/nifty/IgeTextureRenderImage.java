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
    public IgeTextureRenderImage(@Nonnull final Texture texture) {
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

    public void renderImage(@Nonnull final Graphics g, final int x, final int y, final int width, final int height,
                            @Nonnull final Color color, final float imageScale) {
        final int scaledWidth = Math.round(width * imageScale);
        final int scaledHeight = Math.round(height * imageScale);
        final int fixedX = x + ((width - scaledWidth) / 2);
        final int fixedY = y + ((height - scaledHeight) / 2);
        g.drawTexture(texture, fixedX, fixedY, scaledWidth, scaledHeight, color);
    }

    public void renderImage(@Nonnull final Graphics g, final int x, final int y, final int w, final int h,
                            final int srcX, final int srcY, final int srcW, final int srcH, @Nonnull final Color color,
                            final float scale, final int centerX, final int centerY) {
        final int scaledWidth = Math.round(w * scale);
        final int scaledHeight = Math.round(h * scale);
        final int fixedX = Math.round(x + ((w - scaledWidth) * ((float) centerX / (float) w)));
        final int fixedY = Math.round(y + ((h - scaledHeight) * ((float) centerY / (float) h)));
        g.drawTexture(texture, fixedX, fixedY, scaledWidth, scaledHeight, srcX, srcY, srcW, srcH, color);
    }
}
