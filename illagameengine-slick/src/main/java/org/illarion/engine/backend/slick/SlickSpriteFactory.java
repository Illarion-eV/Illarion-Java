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
package org.illarion.engine.backend.slick;

import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Sprite;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;

/**
 * The sprite factory implementation of Slick2D.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickSpriteFactory implements SpriteFactory {
    @Nonnull
    @Override
    public Sprite createSprite(
            @Nonnull final Texture[] textures,
            final int offsetX,
            final int offsetY,
            final float centerX,
            final float centerY,
            final boolean mirror) {
        final SlickTexture[] slickTextures = new SlickTexture[textures.length];
        for (int i = 0; i < textures.length; i++) {
            if (textures[i] instanceof SlickTexture) {
                slickTextures[i] = (SlickTexture) textures[i];
            } else {
                throw new IllegalArgumentException("Invalid texture type.");
            }
        }
        return new SlickSprite(slickTextures, offsetX, offsetY, centerX, centerY, mirror);
    }
}
