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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Gdx;
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
class GdxTextureManager extends AbstractTextureManager {
    @Nullable
    @Override
    protected GdxTexture loadTexture(@Nonnull final String resource) {
        try {
            final Texture tex = new Texture(Gdx.files.internal(resource), true);
            final TextureRegion region = new TextureRegion(tex);
            region.flip(false, false);
            return new GdxTexture(region);
        } catch (@Nonnull final GdxRuntimeException e) {
            return null;
        }
    }
}
