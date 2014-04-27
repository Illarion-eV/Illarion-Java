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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.illarion.engine.graphic.effects.GrayScaleEffect;

import javax.annotation.Nonnull;

/**
 * This is the libGDX implementation of the gray scale effect.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxGrayScaleEffect implements GrayScaleEffect, GdxSceneEffect, GdxTextureEffect {
    /**
     * The pixel shader that is required for this effect.
     */
    @Nonnull
    private final ShaderProgram shader;

    GdxGrayScaleEffect(@Nonnull Files files) {
        //noinspection SpellCheckingInspection
        shader = new ShaderProgram(files.internal("org/illarion/engine/backend/gdx/shaders/generic.vert"),
                                   files.internal("org/illarion/engine/backend/gdx/shaders/grayScale.frag"));
    }

    @Override
    public void update(int delta) {
        // nothing to do
    }

    @Override
    public void activateEffect(
            @Nonnull SpriteBatch batch, int screenWidth, int screenHeight, int textureWidth, int textureHeight) {
        batch.setShader(shader);
    }

    @Override
    public void activateEffect(@Nonnull SpriteBatch batch) {
        batch.setShader(shader);
    }

    @Override
    public void disableEffect(@Nonnull SpriteBatch batch) {
        batch.setShader(null);
    }

    @Override
    public void setTopLeftCoordinate(int x, int y) {
    }

    @Override
    public void setBottomRightCoordinate(int x, int y) {
    }
}
