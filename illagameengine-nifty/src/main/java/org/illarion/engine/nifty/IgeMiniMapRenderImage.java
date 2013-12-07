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
package org.illarion.engine.nifty;

import illarion.common.types.Location;
import org.illarion.engine.Engine;
import org.illarion.engine.EngineException;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.effects.MiniMapEffect;

import javax.annotation.Nonnull;

/**
 * This implementation of the render image is used in special to render the mini map of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class IgeMiniMapRenderImage implements IgeRenderImage {
    /**
     * The world map that supplies the data to this render image.
     */
    @Nonnull
    private final WorldMap map;

    /**
     * The radius of the mini map.
     */
    private final int radius;

    /**
     * The effect that is applied ot make the mini map round.
     */
    @Nonnull
    private final MiniMapEffect effect;

    /**
     * Create a new render engine that shows the mini map.
     *
     * @param engine the used instance of the game engine
     * @param map    the world map that supplied the data
     * @param radius the radius of the mini map
     * @throws EngineException in case the creation of this image fails for any reason
     */
    public IgeMiniMapRenderImage(@Nonnull final Engine engine, @Nonnull final WorldMap map,
                                 final int radius) throws EngineException {
        this.map = map;
        this.radius = radius;
        effect = engine.getAssets().getEffectManager().getMiniMapEffect(map, false);
    }

    @Override
    public void renderImage(@Nonnull final Graphics g, final int x, final int y, final int width, final int height,
                            @Nonnull final Color color, final float imageScale) {
        renderImage(g, x, y, width, height, 0, 0, getWidth(), getHeight(), color, imageScale, radius, radius);
    }

    @Override
    public void renderImage(@Nonnull final Graphics g, final int x, final int y, final int w, final int h, final int srcX,
                            final int srcY, final int srcW, final int srcH, @Nonnull final Color color, final float scale,
                            final int centerX, final int centerY) {
        setupEffect();

        final Location playerLoc = map.getPlayerLocation();
        final Location origin = map.getMapOrigin();
        final int miniMapOriginX = playerLoc.getScX() - origin.getScX() - radius;
        final int miniMapOriginY = playerLoc.getScY() - origin.getScY() - radius;

        final int scaledWidth = Math.round(w * scale);
        final int scaledHeight = Math.round(h * scale);
        final int fixedX = (int) Math.round(x + ((w - scaledWidth) * ((double) centerX / (double) w)));
        final int fixedY = (int) Math.round(y + ((h - scaledHeight) * ((double) centerY / (double) h)));
        g.drawTexture(map.getWorldMap(), fixedX, fixedY, scaledWidth, scaledHeight, srcX + miniMapOriginX,
                srcY + miniMapOriginY, srcW, srcH, centerX - fixedX, centerY - fixedY, -45.f, color, effect);
    }

    /**
     * Setup the parameters of the effect required to render the graphic.
     */
    private void setupEffect() {
        effect.setRadius(radius);
        effect.setCenter(map.getPlayerLocation());
    }

    @Override
    public int getWidth() {
        return radius * 2;
    }

    @Override
    public int getHeight() {
        return radius * 2;
    }

    @Override
    public void dispose() {
        // nothing to do
    }
}
