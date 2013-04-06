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
import illarion.common.types.Location;
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

    /**
     * Create a new instance of the mini map effect.
     *
     * @param files the file system handler used to load the effect data
     */
    GdxMiniMapEffect(@Nonnull final Files files) {
        //noinspection SpellCheckingInspection
        shader = new ShaderProgram(files.internal("org/illarion/engine/backend/gdx/shaders/highlight.vert"),
                files.internal("org/illarion/engine/backend/gdx/shaders/highlight.frag"));
    }


    @Override
    public void activateEffect(@Nonnull final SpriteBatch batch) {
        final float miniMapCenterX = (float) centerX / (float) WorldMap.WORLD_MAP_WIDTH;
        final float miniMapCenterY = (float) centerY / (float) WorldMap.WORLD_MAP_HEIGHT;

        batch.setShader(shader);
        shader.setUniformf("center", miniMapCenterX, miniMapCenterY);
        shader.setUniformf("radius", (float) radius / (float) WorldMap.WORLD_MAP_HEIGHT);
        shader.setUniformf("markerSize", 2.f / (float) WorldMap.WORLD_MAP_HEIGHT);
        shader.setUniformi("tex0", 0);
    }

    @Override
    public void disableEffect(@Nonnull final SpriteBatch batch) {
        batch.setShader(null);
    }

    @Override
    public void setCenter(@Nonnull final Location location) {
        centerX = location.getScX();
        centerY = location.getScY();
    }

    @Override
    public void setRadius(final int radius) {
        this.radius = radius;
    }
}
