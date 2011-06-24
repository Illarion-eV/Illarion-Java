/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.jogl.render;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import illarion.graphics.SpriteColor;
import illarion.graphics.jogl.DriverSettingsJOGL;
import illarion.graphics.jogl.TextureJOGL;

/**
 * This texture render is drawing the graphics by using display lists.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class TextureRenderDisplaylist extends AbstractTextureRender {
    /**
     * The singleton instance of this texture render that is returned by the get
     * instance method all time.
     */
    private static TextureRenderDisplaylist instance;

    /**
     * Private constructor so new instances are only fetched by the
     * {@link #getInstance()} method.
     */
    private TextureRenderDisplaylist() {
        // nothing to do
    }

    /**
     * Get a new instance of this class.
     * 
     * @return get a new instance of the display list render
     */
    public static TextureRenderDisplaylist getInstance() {
        if (instance == null) {
            final GL gl = GLU.getCurrentGL();
            if (gl.isGL2()) {
                instance = new TextureRenderDisplaylist();
            }
        }
        return instance;
    }

    /**
     * Draw a texture using display lists at a specified location.
     * 
     * @param x the x coordinate of the texture
     * @param y the y coordinate of the texture
     * @param z the z coordinate (so the layer) of the texture
     * @param width the width of the area the texture shall be rendered on
     * @param height the height of the area the texture shall be rendered on
     * @param texture the texture that shall be drawn
     * @param color the color that is supposed to be used with that texture
     * @param mirror mirror the texture horizontal
     * @param rotation the value the texture is rotated by
     */
    @Override
    public void drawTexture(final float x, final float y, final float z,
        final float width, final float height, final TextureJOGL texture,
        final SpriteColor color, final boolean mirror, final float rotation) {

        final GL2 gl = GLU.getCurrentGL().getGL2();

        DriverSettingsJOGL.getInstance().enableTexture(gl,
            texture.getTextureID());
        color.setActiveColor();
        gl.glPushMatrix();

        int xmod = 1;
        if (mirror) {
            xmod = -1;
            gl.glTranslatef(x + width, y, z);
        } else {
            gl.glTranslatef(x, y, z);
        }
        if (rotation != 0.f) {
            gl.glRotatef(rotation, 0, 0, 1);
        }
        gl.glScalef(width * xmod, height, 1.f);
        gl.glTranslatef(0.5f, 0.5f, 0);

        gl.glCallList(texture.getDisplayListID());

        gl.glPopMatrix();
    }

    @Override
    @SuppressWarnings("nls")
    public String toString() {
        return "DisplayList Texture Render";
    }
}
