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
package org.illarion.engine.backend.slick;

import org.illarion.engine.graphic.Texture;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import javax.annotation.Nonnull;

/**
 * This is the implementation of the texture interface used by the Slick2D backend. These textures can be used to be
 * drawn using this backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickTexture implements Texture {
    /**
     * The image that is actually rendered as this picture.
     */
    @Nonnull
    private final Image backingImage;

    /**
     * Create a new texture instance and set the reference string to the resource that is load to receive the texture.
     *
     * @param texture the reference string to the image resource
     * @throws SlickException in case loading the texture fails
     */
    SlickTexture(@Nonnull final String texture) throws SlickException {
        backingImage = new Image(texture);
    }

    /**
     * Create a new texture that wraps a specific image instance that is drawn on the screen.
     *
     * @param image the image that is drawn
     */
    SlickTexture(@Nonnull final Image image) {
        backingImage = image;
    }

    /**
     * Get the image wrapped by this texture.
     *
     * @return the backing image
     */
    @Nonnull
    public Image getBackingImage() {
        return backingImage;
    }

    @Override
    public void dispose() {
        // nothing
    }

    @Nonnull
    @Override
    public Texture getSubTexture(final int x, final int y, final int width, final int height) {
        return new SlickTexture(backingImage.getSubImage(x, y, width, height));
    }

    @Override
    public int getHeight() {
        return backingImage.getHeight();
    }

    @Override
    public int getWidth() {
        return backingImage.getWidth();
    }
}
