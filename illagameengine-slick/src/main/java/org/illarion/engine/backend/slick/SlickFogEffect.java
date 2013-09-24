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

import org.illarion.engine.graphic.effects.FogEffect;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import javax.annotation.Nonnull;

/**
 * This is the implementation of the fog effect for Slick2D.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickFogEffect implements FogEffect, SlickSceneEffect {
    /**
     * The density of the fog.
     */
    private float density;

    /**
     * The shader that is used to render the fog.
     */
    @Nonnull
    private final ShaderProgram fogShader;

    /**
     * Create a new fog effect.
     *
     * @throws SlickEngineException in case loading the effect fails
     */
    SlickFogEffect() throws SlickEngineException {
        try {
            fogShader = ShaderProgram.loadProgram("org/illarion/engine/backend/slick/shaders/generic.vert",
                    "org/illarion/engine/backend/slick/shaders/fog.frag");
        } catch (SlickException e) {
            throw new SlickEngineException(e);
        }
    }

    @Override
    public void setDensity(final float density) {
        this.density = density;
    }

    @Override
    public void update(final int delta) {
        // no update needed
    }

    @Override
    public void activateEffect(final int screenWidth, final int screenHeight, final int textureWidth, final int textureHeight) {
        fogShader.bind();
        fogShader.setUniform1i("tex0", 0);
        fogShader.setUniform1f("density", density);
        fogShader.setUniform2f("center", (float) screenWidth / 2.f / (float) textureWidth,
                (float) screenHeight / 2.f / (float) textureHeight);
    }

    @Override
    public void disableEffect() {
        fogShader.unbind();
    }
}
