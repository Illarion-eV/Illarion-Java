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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.effects.HighlightEffect;

import javax.annotation.Nonnull;

/**
 * This is the implementation of the highlight effect that is used by libGDX.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxHighlightEffect implements HighlightEffect, GdxTextureEffect {
    /**
     * The pixel shader that is required for this effect.
     */
    @Nonnull
    private final ShaderProgram shader;

    /**
     * The color that is applied to highlight the rendered object.
     */
    @Nonnull
    private final com.badlogic.gdx.graphics.Color highlightColor;

    /**
     * Create a new instance of the highlight effect.
     *
     * @param files the file system handler used to load the effect data
     */
    GdxHighlightEffect(@Nonnull final Files files) {
        //noinspection SpellCheckingInspection
        shader = new ShaderProgram(files.internal("org/illarion/engine/backend/gdx/shaders/highlight.vert"),
                files.internal("org/illarion/engine/backend/gdx/shaders/highlight.frag"));
        highlightColor = new com.badlogic.gdx.graphics.Color();
    }

    @Override
    public void activateEffect(@Nonnull final SpriteBatch batch) {
        batch.setShader(shader);
        shader.setUniformf("highlight", highlightColor);
    }

    @Override
    public void disableEffect(@Nonnull final SpriteBatch batch) {
        batch.setShader(null);
    }

    @Override
    public void setHighlightColor(@Nonnull final Color color) {
        GdxGraphics.transferColor(color, highlightColor);
    }
}
