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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.illarion.engine.backend.shared.AbstractTextureManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the texture manager that takes care of loading and storing textures that were created for libGDX.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxTextureManager extends AbstractTextureManager<Pixmap> {
    @Override
    protected Pixmap loadTextureData(@Nonnull String textureName) {
        try {
            return new Pixmap(Gdx.files.internal(textureName));
        } catch (@Nonnull Exception ignored) {
            return null;
        }
    }

    @Nullable
    @Override
    protected GdxTexture loadTexture(@Nonnull String resource, Pixmap preLoadData) {
        try {
            Texture tex = new Texture(preLoadData, false);
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            TextureRegion region = new TextureRegion(tex);
            region.flip(false, false);
            return new GdxTexture(region);
        } catch (@Nonnull GdxRuntimeException e) {
            return null;
        }
    }
}
