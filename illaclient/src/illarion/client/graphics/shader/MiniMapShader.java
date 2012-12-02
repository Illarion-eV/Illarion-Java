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
 * This is the mini map shader that takes care for showing the mini map in shape of a circle.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MiniMapShader extends AbstractShader {
    /**
     * Default constructor.
     *
     * @throws SlickException in case loading the shader fails
     */
    public MiniMapShader() throws SlickException {
        super("minimap.vert", "minimap.frag");
    }

    /**
     * The center of the circle that is supposed to be cut out.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setCenter(final float x, final float y) {
        getShader().setUniform2f("center", x, y);
    }

    /**
     * Set the radius of the circle to cut.
     *
     * @param radius the circle radius
     */
    public void setRadius(final float radius) {
        getShader().setUniform1f("radius", radius);
    }

    /**
     * Set the texture reference.
     *
     * @param textureIndex the index of the bound texture
     */
    public void setTexture(final int textureIndex) {
        getShader().setUniform1i("tex0", textureIndex);
    }
}
