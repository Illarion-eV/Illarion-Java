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
package illarion.graphics.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import gnu.trove.list.array.TIntArrayList;

import illarion.graphics.TextureAtlas;
import illarion.graphics.generic.AbstractTexture;

/**
 * The implementation of the Texture for usage with JOGL.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class TextureJOGL extends AbstractTexture {
    /**
     * A list of used display list IDs. This is used to clean up the prepared
     * display lists in case its needed.
     */
    private static TIntArrayList usedDisplayLists = new TIntArrayList();

    /**
     * This variable stores if the display list needs to be refreshed.
     */
    private boolean displayListDirty = false;

    /**
     * The ID of the display list used to draw this texture.
     */
    private int displayListID = -1;

    /**
     * Create a empty texture.
     */
    public TextureJOGL() {
        super();
    }

    /**
     * Create a new texture with specified width and height and a texture size.
     * Assumes that the X and Y coordinates of the image are at 0.
     * 
     * @param newWidth the width of the image
     * @param newHeight the height of the image
     * @param newTexWidth the width of the parent texture
     * @param newTexHeight the height of the parent texture
     */
    public TextureJOGL(final int newWidth, final int newHeight,
        final int newTexWidth, final int newTexHeight) {
        super(newWidth, newHeight, newTexWidth, newTexHeight);
    }

    /**
     * Create a new texture with specified width and height and a texture size.
     * Also the position of the texture on the texture map is set.
     * 
     * @param newWidth the width of the image
     * @param newHeight the height of the image
     * @param newTexWidth the width of the parent texture
     * @param newTexHeight the height of the parent texture
     * @param newX the x coordinate of the image on the parent texture
     * @param newY the y coordinate of the image on the parent texture
     */
    public TextureJOGL(final int newWidth, final int newHeight,
        final int newTexWidth, final int newTexHeight, final int newX,
        final int newY) {
        super(newWidth, newHeight, newTexWidth, newTexHeight, newX, newY);
    }

    /**
     * Clean up all textures. After this is done none of the textures can be
     * used anymore.
     */
    @SuppressWarnings("nls")
    public static void dispose() {
        final int n = usedDisplayLists.size();
        if (n == 0) {
            return;
        }
        final GL gl = GLU.getCurrentGL();
        if (!gl.isGL2()) {
            throw new GraphicsJOGLException(
                "There are lists set but display lists are not useable",
                gl.getGLProfile());
        }
        int minDisplayList = Integer.MAX_VALUE;
        int maxDisplayList = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            final int listID = usedDisplayLists.get(i);
            minDisplayList = Math.min(minDisplayList, listID);
            maxDisplayList = Math.max(maxDisplayList, listID);
        }

        gl.getGL2().glDeleteLists(minDisplayList,
            (maxDisplayList - minDisplayList) + 1);
    }

    /**
     * Get the ID of the display list needed to render this texture. In case
     * there is none, the display list is created and compiled automatically.
     * 
     * @return the ID of the display list that needs to be called to render this
     *         list
     */
    @SuppressWarnings("nls")
    public int getDisplayListID() {
        if ((displayListID == -1) || displayListDirty) {
            final GL gl = GLU.getCurrentGL();
            if (!gl.isGL2()) {
                throw new GraphicsJOGLException(
                    "Use of display lists not possible", gl.getGLProfile());
            }
            final GL2 gl2 = GLU.getCurrentGL().getGL2();
            if (displayListID == -1) {
                displayListID = gl2.glGenLists(1);
                usedDisplayLists.add(displayListID);
            }

            gl2.glNewList(displayListID, GL2.GL_COMPILE);
            gl2.glBegin(GL.GL_TRIANGLE_STRIP);

            gl2.glTexCoord2d(getRelX1(), getRelY2());
            gl2.glVertex2f(-0.5f, -0.5f);
            gl2.glTexCoord2d(getRelX1(), getRelY1());
            gl2.glVertex2f(-0.5f, 0.5f);
            gl2.glTexCoord2d(getRelX2(), getRelY2());
            gl2.glVertex2f(0.5f, -0.5f);
            gl2.glTexCoord2d(getRelX2(), getRelY1());
            gl2.glVertex2f(0.5f, 0.5f);

            gl2.glEnd();
            gl2.glEndList();

            displayListDirty = false;
        }

        return displayListID;
    }

    /**
     * Set the parent texture of this texture.
     * 
     * @param parentAtlas the parent texture atlas of this texture
     */
    @Override
    @SuppressWarnings("nls")
    public void setParent(final TextureAtlas parentAtlas) {
        if (!(parentAtlas instanceof TextureAtlasJOGL)) {
            throw new IllegalArgumentException(
                "Invalid implementation of the texture atlas");
        }
        super.setParent(parentAtlas);
    }

    @Override
    protected void textureDataChanged() {
        displayListDirty = true;
    }
}
