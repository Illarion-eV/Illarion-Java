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

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.illarion.engine.graphic.effects.TextureEffect;

import javax.annotation.Nonnull;

/**
 * This interface extends the default texture effects for libGDX.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
interface GdxTextureEffect extends TextureEffect {
    /**
     * Apply the effect to a sprite batch.
     *
     * @param batch the batch that is supposed to render this effect from now on
     */
    void activateEffect(@Nonnull SpriteBatch batch);

    /**
     * Remove the effect from a sprite batch.
     *
     * @param batch the batch that is now not supposed to render the effect anymore
     */
    void disableEffect(@Nonnull SpriteBatch batch);

    /**
     * Set the top left coordinate of the texture that is rendered.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    void setTopLeftCoordinate(float x, float y);

    /**
     * Set the bottom right coordinate of the texture that is rendered.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    void setBottomRightCoordinate(float x, float y);
}
