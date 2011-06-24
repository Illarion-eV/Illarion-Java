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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.glsl.fixedfunc.FixedFuncUtil;

import illarion.graphics.SpriteColor;
import illarion.graphics.jogl.DriverSettingsJOGL;
import illarion.graphics.jogl.TextureJOGL;

/**
 * This texture render uses array pointers to render a texture.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class TextureRenderPointer extends AbstractTextureRender {
    /**
     * The singleton instance of this class.
     */
    private static TextureRenderPointer instance;

    /**
     * The buffer that is used to store the texture coordinate data.
     */
    private final FloatBuffer textureBuffer;

    /**
     * The buffer that is used to store the vertex data.
     */
    private final FloatBuffer vertexBuffer;

    /**
     * Private constructor so new instances are only fetched by the
     * {@link #getInstance()} method.
     */
    private TextureRenderPointer() {
        textureBuffer =
            ByteBuffer.allocateDirect((Float.SIZE / 8) * 8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertexBuffer =
            ByteBuffer.allocateDirect((Float.SIZE / 8) * 12)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertexBuffer.put(-0.5f).put(-0.5f);
        vertexBuffer.put(-0.5f).put(0.5f);
        vertexBuffer.put(0.5f).put(-0.5f);
        vertexBuffer.put(0.5f).put(0.5f);
        vertexBuffer.flip();
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of the texture pointer render
     */
    public static TextureRenderPointer getInstance() {
        if (instance == null) {
            final GL gl = GLU.getCurrentGL();
            if (gl.isGL2ES1() || gl.hasGLSL()) {
                instance = new TextureRenderPointer();
            }
        }
        return instance;
    }

    /**
     * Draw a texture using buffer pointers and draw arrays.
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

        final GL2ES1 gl;

        if (GLU.getCurrentGL().hasGLSL()) {
            gl = FixedFuncUtil.getFixedFuncImpl(GLU.getCurrentGL());
        } else {
            gl = GLU.getCurrentGL().getGL2ES1();
        }
        DriverSettingsJOGL.getInstance().enableTexturePointer(gl,
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

        gl.glScalef(width * xmod, height, 1.f);
        gl.glTranslatef(0.5f, 0.5f, 0);

        if (rotation != 0.f) {
            gl.glRotatef(rotation, 0, 0, 1);
        }

        textureBuffer.rewind();
        textureBuffer.put(texture.getRelX1()).put(texture.getRelY2());
        textureBuffer.put(texture.getRelX1()).put(texture.getRelY1());
        textureBuffer.put(texture.getRelX2()).put(texture.getRelY2());
        textureBuffer.put(texture.getRelX2()).put(texture.getRelY1());
        textureBuffer.flip();

        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, textureBuffer);
        gl.glVertexPointer(2, GL.GL_FLOAT, 0, vertexBuffer);

        gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);

        gl.glPopMatrix();
    }

    @Override
    @SuppressWarnings("nls")
    public String toString() {
        return "Array Pointer Texture Render";
    }
}
