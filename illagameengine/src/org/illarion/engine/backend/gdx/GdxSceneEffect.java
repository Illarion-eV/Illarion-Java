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

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.illarion.engine.graphic.effects.SceneEffect;

import javax.annotation.Nonnull;

/**
 * This is the extended interface of scene effects that are applied to the libGDX scene renderer.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
interface GdxSceneEffect extends SceneEffect {
    /**
     * Update the state of the effect. This should be used to implement animated effects.
     *
     * @param delta the time since the last update call in milliseconds
     */
    void update(int delta);

    /**
     * Activate the effect so it applies to the next rendering operation.
     *
     * @param batch         the batch used for the drawing operation
     * @param screenWidth   the width of the scene that is rendered
     * @param screenHeight  the height of the scene that is rendered
     * @param textureWidth  the width of the texture that holds the scene in its current state
     * @param textureHeight the height of the texture that holds the scene in its current state
     */
    void activateEffect(@Nonnull SpriteBatch batch, int screenWidth, int screenHeight, int textureWidth,
                        int textureHeight);

    /**
     * Disable the effect so it does not apply anymore
     *
     * @param batch the batch used for the drawing operation
     */
    void disableEffect(@Nonnull SpriteBatch batch);
}
