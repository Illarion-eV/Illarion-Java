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

import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Sprite;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;

/**
 * This factory is used to create sprites that are used by libGDX.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxSpriteFactory implements SpriteFactory {
    @Nonnull
    @Override
    public Sprite createSprite(@Nonnull final Texture[] textures, final int offsetX, final int offsetY,
                               final float centerX, final float centerY, final boolean mirror) {
        final GdxTexture[] gdxTextures = new GdxTexture[textures.length];
        for (int i = 0; i < textures.length; i++) {
            if (textures[i] instanceof GdxTexture) {
                gdxTextures[i] = (GdxTexture) textures[i];
            } else {
                throw new IllegalArgumentException("Invalid texture type.");
            }
        }
        return new GdxSprite(gdxTextures, offsetX, offsetY, centerX, centerY, mirror);
    }

    @Nonnull
    @Override
    public Sprite createSprite(@Nonnull final Texture texture, final int offsetX, final int offsetY,
                               final float centerX, final float centerY, final boolean mirror) {
        return createSprite(new Texture[]{texture}, offsetX, offsetY, centerX, centerY, mirror);
    }
}
