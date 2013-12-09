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
package org.illarion.engine.graphic.effects;

import org.illarion.engine.graphic.Color;

import javax.annotation.Nonnull;

/**
 * This interface defines the highlighting effect.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface HighlightEffect extends TextureEffect {
    /**
     * The highlight color that is applied. The alpha component determines how much of the textures original color is
     * covered. For the actual render operation only the alpha value of the original texture is used to preserve the
     * shape of the resulting image.
     *
     * @param color the color to apply
     */
    void setHighlightColor(@Nonnull Color color);
}
