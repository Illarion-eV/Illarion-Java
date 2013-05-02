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

import org.illarion.engine.graphic.effects.GrayScaleEffect;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import javax.annotation.Nonnull;

/**
 * This is the Slick2D implementation of the gray scale effect.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class SlickGrayScaleEffect implements GrayScaleEffect, SlickTextureEffect, SlickSceneEffect {
    /**
     * The shader that is used to change the color.
     */
    @Nonnull
    private final ShaderProgram shader;

    /**
     * Create a new gray scale effect.
     *
     * @throws SlickEngineException in case loading the effect fails
     */
    SlickGrayScaleEffect() throws SlickEngineException {
        try {
            shader = ShaderProgram.loadProgram("org/illarion/engine/backend/slick/shaders/generic.vert",
                    "org/illarion/engine/backend/slick/shaders/grayScale.frag");
        } catch (SlickException e) {
            throw new SlickEngineException(e);
        }
    }

    @Override
    public void activateEffect(@Nonnull final Graphics graphics) {
        shader.bind();
        shader.setUniform1i("tex0", 0);
    }

    @Override
    public void disableEffect(@Nonnull final Graphics graphics) {
        shader.unbind();
    }

    @Override
    public void update(final int delta) {
        // nothing to do
    }

    @Override
    public void activateEffect(final int screenWidth, final int screenHeight, final int textureWidth, final int textureHeight) {
        shader.bind();
        shader.setUniform1i("tex0", 0);
    }

    @Override
    public void disableEffect() {
        shader.unbind();
    }
}
