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

import org.illarion.engine.graphic.effects.SceneEffect;

/**
 * This is the interface to a scene effect for the Slick2D backend. It defines the functions required for the backend
 * to interact with the effect.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
interface SlickSceneEffect extends SceneEffect {
    /**
     * The update function is called for each effect applied to a scene to give it some time to calculate the update
     * for the next frame.
     *
     * @param delta the time since the last update call
     */
    void update(int delta);

    /**
     * Activate the effect so it applies to the next rendering operation.
     *
     * @param screenWidth the width of the scene that is rendered
     * @param screenHeight the height of the scene that is rendered
     * @param textureWidth the width of the texture that holds the scene in its current state
     * @param textureHeight the height of the texture that holds the scene in its current state
     */
    void activateEffect(int screenWidth, int screenHeight, int textureWidth, int textureHeight);

    /**
     * Disable the effect so it does not apply anymore
     */
    void disableEffect();
}
