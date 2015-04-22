/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import illarion.common.types.ServerCoordinate;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.effects.MiniMapEffect;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxMiniMapEffect implements MiniMapEffect, GdxTextureEffect {
    /**
     * The pixel shader that is required for this effect.
     */
    @Nonnull
    private final ShaderProgram shader;

    /**
     * The radius of the rendered area
     */
    private int radius;

    /**
     * The x coordinate of the center location on the mini map.
     */
    private int centerX;

    /**
     * The y coordinate of the center location on the mini map.
     */
    private int centerY;

    @Nonnull
    private final WorldMap worldMap;

    /**
     * Create a new instance of the mini map effect.
     *
     * @param files the file system handler used to load the effect data
     * @param worldMap the world map that is displayed
     */
    GdxMiniMapEffect(@Nonnull Files files, @Nonnull WorldMap worldMap) {
        //noinspection SpellCheckingInspection
        shader = new ShaderProgram(files.internal("org/illarion/engine/backend/gdx/shaders/generic.vert"),
                                   files.internal("org/illarion/engine/backend/gdx/shaders/minimap.frag"));
        this.worldMap = worldMap;
    }

    @Override
    public void activateEffect(@Nonnull SpriteBatch batch) {
        float miniMapCenterX = (float) centerX / WorldMap.WORLD_MAP_WIDTH;
        float miniMapCenterY = (float) centerY / WorldMap.WORLD_MAP_HEIGHT;

        batch.setShader(shader);
        shader.setUniformf("u_radius", (float) radius / WorldMap.WORLD_MAP_HEIGHT);
        shader.setUniformf("u_markerSize", 2.f / WorldMap.WORLD_MAP_HEIGHT);
        shader.setUniformf("u_center", miniMapCenterX, miniMapCenterY);
    }

    @Override
    public void disableEffect(@Nonnull SpriteBatch batch) {
        batch.setShader(null);
    }

    @Override
    public void setTopLeftCoordinate(float x, float y) {
    }

    @Override
    public void setBottomRightCoordinate(float x, float y) {
    }

    @Override
    public void setCenter(@Nonnull ServerCoordinate location) {
        centerX = location.getX() - worldMap.getMapOrigin().getX();
        centerY = location.getY() - worldMap.getMapOrigin().getY();
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
    }
}
