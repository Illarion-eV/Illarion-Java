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
import com.badlogic.gdx.math.Vector2;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.effects.TileLightEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GdxTileLightEffect implements TileLightEffect, GdxTextureEffect {
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(GdxTileLightEffect.class);

    /**
     * Uniform shader variable name for the top left light.
     */
    @Nonnull
    private static final String UNIFORM_TOP_LEFT = "u_topLeft";

    /**
     * Uniform shader variable name for the top right light.
     */
    @Nonnull
    private static final String UNIFORM_TOP_RIGHT = "u_topRight";

    /**
     * Uniform shader variable name for the bottom left light.
     */
    @Nonnull
    private static final String UNIFORM_BOTTOM_LEFT = "u_bottomLeft";

    /**
     * Uniform shader variable name for the bottom right light.
     */
    @Nonnull
    private static final String UNIFORM_BOTTOM_RIGHT = "u_bottomRight";

    /**
     * Uniform shader variable name for the center light.
     */
    @Nonnull
    private static final String UNIFORM_CENTER = "u_center";

    /**
     * Uniform shader variable name for the top left coordinates.
     */
    @Nonnull
    private static final String UNIFORM_TOP_LEFT_COORDS = "u_topLeftCoords";

    /**
     * Uniform shader variable name for the bottom right coordinates.
     */
    @Nonnull
    private static final String UNIFORM_BOTTOM_RIGHT_COORDS = "u_bottomRightCoords";

    /**
     * The pixel shader that is required for this effect.
     */
    @Nonnull
    private final ShaderProgram shader;

    @Nonnull
    private final com.badlogic.gdx.graphics.Color topLeft;
    @Nonnull
    private final com.badlogic.gdx.graphics.Color topRight;
    @Nonnull
    private final com.badlogic.gdx.graphics.Color bottomLeft;
    @Nonnull
    private final com.badlogic.gdx.graphics.Color bottomRight;
    @Nonnull
    private final com.badlogic.gdx.graphics.Color center;
    private final Vector2 topLeftCoord;
    private final Vector2 bottomRightCoord;

    GdxTileLightEffect(@Nonnull Files files) {
        //noinspection SpellCheckingInspection
        shader = new ShaderProgram(files.internal("org/illarion/engine/backend/gdx/shaders/generic.vert"),
                                   files.internal("org/illarion/engine/backend/gdx/shaders/tileLight.frag"));

        if (!shader.isCompiled()) {
            LOGGER.error("Compiling shader failed: {}", shader.getLog());
        }

        topLeft = new com.badlogic.gdx.graphics.Color();
        topRight = new com.badlogic.gdx.graphics.Color();
        bottomLeft = new com.badlogic.gdx.graphics.Color();
        bottomRight = new com.badlogic.gdx.graphics.Color();
        center = new com.badlogic.gdx.graphics.Color();
        topLeftCoord = new Vector2();
        bottomRightCoord = new Vector2();
    }

    @Override
    public void setTopLeftColor(@Nonnull Color color) {
        GdxGraphics.transferColor(color, topLeft);
    }

    @Override
    public void setTopRightColor(@Nonnull Color color) {
        GdxGraphics.transferColor(color, topRight);
    }

    @Override
    public void setBottomLeftColor(@Nonnull Color color) {
        GdxGraphics.transferColor(color, bottomLeft);
    }

    @Override
    public void setBottomRightColor(@Nonnull Color color) {
        GdxGraphics.transferColor(color, bottomRight);
    }

    @Override
    public void setCenterColor(@Nonnull Color color) {
        GdxGraphics.transferColor(color, center);
    }

    @Override
    public void activateEffect(@Nonnull SpriteBatch batch) {
        if (shader.isCompiled()) {
            batch.setShader(shader);
            setUniform(shader, UNIFORM_TOP_LEFT, topLeft);
            setUniform(shader, UNIFORM_TOP_RIGHT, topRight);
            setUniform(shader, UNIFORM_BOTTOM_LEFT, bottomLeft);
            setUniform(shader, UNIFORM_BOTTOM_RIGHT, bottomRight);
            setUniform(shader, UNIFORM_CENTER, center);
            setUniform(shader, UNIFORM_TOP_LEFT_COORDS, topLeftCoord);
            setUniform(shader, UNIFORM_BOTTOM_RIGHT_COORDS, bottomRightCoord);
        }
    }

    private static void setUniform(
            @Nonnull ShaderProgram shader, @Nonnull String name, @Nonnull com.badlogic.gdx.graphics.Color color) {
        if (shader.hasUniform(name)) {
            shader.setUniformf(name, color);
        }
    }

    private static void setUniform(@Nonnull ShaderProgram shader, @Nonnull String name, @Nonnull Vector2 vector2) {
        if (shader.hasUniform(name)) {
            shader.setUniformf(name, vector2);
        }
    }

    @Override
    public void disableEffect(@Nonnull SpriteBatch batch) {
        batch.setShader(null);
    }

    @Override
    public void setTopLeftCoordinate(float x, float y) {
        topLeftCoord.set(x, y);
    }

    @Override
    public void setBottomRightCoordinate(float x, float y) {
        bottomRightCoord.set(x, y);
    }
}
