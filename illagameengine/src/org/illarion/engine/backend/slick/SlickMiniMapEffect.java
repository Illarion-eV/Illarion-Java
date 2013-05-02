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

import illarion.common.types.Location;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.effects.MiniMapEffect;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import javax.annotation.Nonnull;

/**
 * This is the Slick2D implementation of the mini map effect.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickMiniMapEffect implements MiniMapEffect, SlickTextureEffect {
    /**
     * The world map that supplies this effect with the required data.
     */
    @Nonnull
    private final SlickWorldMap worldMap;

    /**
     * The shader handles drawing the mini map.
     */
    @Nonnull
    private final ShaderProgram miniMapShader;

    /**
     * Create a new instance of the mini map effect.
     *
     * @throws SlickEngineException in case loading the effect fails
     */
    SlickMiniMapEffect(@Nonnull final WorldMap worldMap) throws SlickEngineException {
        try {
            miniMapShader = ShaderProgram.loadProgram("org/illarion/engine/backend/slick/shaders/generic.vert",
                    "org/illarion/engine/backend/slick/shaders/minimap.frag");
        } catch (@Nonnull final SlickException e) {
            throw new SlickEngineException(e);
        }
        if (worldMap instanceof SlickWorldMap) {
            this.worldMap = (SlickWorldMap) worldMap;
        } else {
            throw new IllegalArgumentException("worldMap has incorrect type: " + worldMap.getClass().toString());
        }

    }

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

    @Override
    public void activateEffect(@Nonnull final Graphics graphics) {
        final float miniMapCenterX = (float) centerX / (float) WorldMap.WORLD_MAP_WIDTH;
        final float miniMapCenterY = (float) centerY / (float) WorldMap.WORLD_MAP_HEIGHT;

        miniMapShader.bind();
        miniMapShader.setUniform2f("center", miniMapCenterX, miniMapCenterY);
        miniMapShader.setUniform1f("radius", (float) radius / (float) WorldMap.WORLD_MAP_HEIGHT);
        miniMapShader.setUniform1f("markerSize", 2.f / (float) WorldMap.WORLD_MAP_HEIGHT);
        miniMapShader.setUniform1i("tex0", 0);
    }

    @Override
    public void disableEffect(@Nonnull final Graphics graphics) {
        miniMapShader.unbind();
    }

    @Override
    public void setCenter(@Nonnull final Location location) {
        centerX = location.getScX() - worldMap.getMapOrigin().getScX();
        centerY = location.getScY() - worldMap.getMapOrigin().getScY();
    }

    @Override
    public void setRadius(final int radius) {
        this.radius = radius;
    }
}
