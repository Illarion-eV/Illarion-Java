/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;

/**
 * This is the implementation of a texture that stores a libGDX texture.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxTexture implements Texture {
    /**
     * The internal texture that is wrapped by this engine texture.
     */
    @Nonnull
    private final TextureRegion backingTexture;

    GdxTexture(@Nonnull final TextureRegion backingTexture) {
        this.backingTexture = backingTexture;
    }

    @Override
    public void dispose() {
        // nothing to do
    }

    @Nonnull
    @Override
    public Texture getSubTexture(final int x, final int y, final int width, final int height) {
        return new GdxTexture(new TextureRegion(backingTexture, x, y, width, height));
    }

    @Override
    public int getHeight() {
        return backingTexture.getRegionHeight();
    }

    @Override
    public int getWidth() {
        return backingTexture.getRegionWidth();
    }

    @Nonnull
    public TextureRegion getTextureRegion() {
        return backingTexture;
    }
}
