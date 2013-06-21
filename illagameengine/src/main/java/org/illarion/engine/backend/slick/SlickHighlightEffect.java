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

import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.effects.HighlightEffect;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import javax.annotation.Nonnull;

/**
 * This is the Slick2D implementation of the highlighting effect.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickHighlightEffect implements HighlightEffect, SlickTextureEffect {
    /**
     * The shader handles drawing the highlighted object.
     */
    @Nonnull
    private final ShaderProgram highlightShader;

    /**
     * The color that is applied to the texture as highlight.
     */
    @Nonnull
    private final org.newdawn.slick.Color highlightColor;

    /**
     * Create a new instance of the highlight effect.
     *
     * @throws SlickEngineException
     */
    SlickHighlightEffect() throws SlickEngineException {
        try {
            highlightShader = ShaderProgram.loadProgram("org/illarion/engine/backend/slick/shaders/generic.vert",
                    "org/illarion/engine/backend/slick/shaders/highlight.frag");
        } catch (@Nonnull final SlickException e) {
            throw new SlickEngineException(e);
        }
        highlightColor = new org.newdawn.slick.Color(org.newdawn.slick.Color.white);
    }

    @Override
    public void activateEffect(@Nonnull final Graphics graphics) {
        highlightShader.bind();
        highlightShader.setUniform1i("tex0", 0);
        highlightShader.setUniform4f("highlight", highlightColor);
    }

    @Override
    public void disableEffect(@Nonnull final Graphics graphics) {
        highlightShader.unbind();
    }

    @Override
    public void setHighlightColor(@Nonnull final Color color) {
        SlickGraphics.transferColor(color, highlightColor);
    }
}
