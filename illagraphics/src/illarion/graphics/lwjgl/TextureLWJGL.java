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
package illarion.graphics.lwjgl;

import org.lwjgl.opengl.GL11;

import illarion.graphics.TextureAtlas;
import illarion.graphics.generic.AbstractTexture;

/**
 * The implementation of the Texture for usage with LWJGL.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class TextureLWJGL extends AbstractTexture {

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
    public TextureLWJGL() {
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
    public TextureLWJGL(final int newWidth, final int newHeight,
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
    public TextureLWJGL(final int newWidth, final int newHeight,
        final int newTexWidth, final int newTexHeight, final int newX,
        final int newY) {
        super(newWidth, newHeight, newTexWidth, newTexHeight, newX, newY);
    }

    /**
     * Generate the display list in case its needed and return the ID of the
     * list needed to display this texture.
     * 
     * @return the ID of the display list
     */
    public int getDisplayListID() {
        if ((displayListID == -1) || displayListDirty) {
            if (displayListID == -1) {
                displayListID = GL11.glGenLists(1);
            }

            GL11.glNewList(displayListID, GL11.GL_COMPILE);
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

            GL11.glTexCoord2f(getRelX1(), getRelY2());
            GL11.glVertex2f(-0.5f, -0.5f);
            GL11.glTexCoord2f(getRelX1(), getRelY1());
            GL11.glVertex2f(-0.5f, 0.5f);
            GL11.glTexCoord2f(getRelX2(), getRelY2());
            GL11.glVertex2f(0.5f, -0.5f);
            GL11.glTexCoord2f(getRelX2(), getRelY1());
            GL11.glVertex2f(0.5f, 0.5f);

            GL11.glEnd();
            GL11.glEndList();

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
        if (!(parentAtlas instanceof TextureAtlasLWJGL)) {
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
