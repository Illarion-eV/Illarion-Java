/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics.shader;

import org.newdawn.slick.SlickException;

/**
 * This shader takes care for rendering the rain on top of the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class RainShader extends AbstractShader {
    /**
     * Default constructor.
     *
     * @throws org.newdawn.slick.SlickException
     *          in case loading the shader fails
     */
    public RainShader() throws SlickException {
        super("rain.vert", "rain.frag");
    }

    /**
     * The offset of the rain texture.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setRainTextureOffset(final float x, final float y) {
        getShader().setUniform2f("texRainOffset", x, y);
    }

    /**
     * The size of the rain texture.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setRainTextureSize(final float x, final float y) {
        getShader().setUniform2f("texRainSize", x, y);
    }

    /**
     * The scaling value of the rain texture
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setRainTextureScale(final float x, final float y) {
        getShader().setUniform2f("texRainScale", x, y);
    }

    /**
     * Set the texture reference for the background texture.
     *
     * @param textureIndex the index of the bound texture
     */
    public void setBackgroundTexture(final int textureIndex) {
        getShader().setUniform1i("texBack", textureIndex);
    }

    /**
     * Set the texture reference for the rain texture.
     *
     * @param textureIndex the index of the bound texture
     */
    public void setRainTexture(final int textureIndex) {
        getShader().setUniform1i("texRain", textureIndex);
    }

    /**
     * Set the animation state.
     *
     * @param offset the dropping offset between {@code 0.f} and {@code 1.f}
     */
    public void setAnimation(final float offset) {
        getShader().setUniform1f("animation", offset);
    }

    /**
     * Set the animation of the wind gusts.
     *
     * @param offset the dropping offset between {@code 0.f} and {@code 1.f}
     */
    public void setGustAnimation(final float offset) {
        getShader().setUniform1f("gustAnimation", offset);
    }

    /**
     * Set the intensity of the rain.
     *
     * @param intensity the rain intensity
     */
    public void setIntensity(final float intensity) {
        getShader().setUniform1f("intensity", intensity);
    }

    /**
     * Set the strength and the direction of the wind.
     *
     * @param dir the wind speed and direction
     */
    public void setWindDirection(final float dir) {
        getShader().setUniform1f("windDir", dir);
    }

    /**
     * Set the strength of wind gusts.
     *
     * @param gust the strength of wind gusts
     */
    public void setGustStrength(final float gust) {
        getShader().setUniform1f("gustStrength", gust);
    }
}
