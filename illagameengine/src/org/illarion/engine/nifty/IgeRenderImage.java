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

import de.lessvoid.nifty.spi.render.RenderImage;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;

/**
 * This is the render image implementation that uses a texture that is rendered to the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class IgeRenderImage implements RenderImage {
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
    IgeRenderImage(@Nonnull final Texture texture) {
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
}
