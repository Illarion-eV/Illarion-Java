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

import org.illarion.engine.backend.shared.AbstractTextureManager;
import org.illarion.engine.graphic.Texture;
import org.newdawn.slick.SlickException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The texture manager that takes care for loading and providing the texture data for the Slick 2D backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class SlickTextureManager extends AbstractTextureManager {
    @Nullable
    @Override
    protected Texture loadTexture(@Nonnull final String resource) {
        try {
            return new SlickTexture(resource);
        } catch (@Nonnull final SlickException ignored) {
            return null;
        }
    }
}
