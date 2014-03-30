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

import org.illarion.engine.backend.shared.AbstractSprite;

import javax.annotation.Nonnull;

/**
 * This is the sprite implementation of Slick2D.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickSprite extends AbstractSprite<SlickTexture> {
    SlickSprite(
            @Nonnull final SlickTexture[] textures,
            final int offsetX,
            final int offsetY,
            final float centerX,
            final float centerY,
            final boolean mirror) {
        super(textures, offsetX, offsetY, centerX, centerY, mirror);
    }
}
