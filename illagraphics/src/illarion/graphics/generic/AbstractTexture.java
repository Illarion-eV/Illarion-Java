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
package illarion.graphics.generic;

import illarion.graphics.Texture;
import illarion.graphics.TextureAtlas;

/**
 * Generic texture implementation that implements the parts of the texture that
 * is shared by all library specific implementations.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public abstract class AbstractTexture implements Texture {
    /**
     * This variable stores the next unique ID a newly created texture will get.
     */
    private static int nextUID = 1;

    /**
     * The height of the image itself.
     */
    private int height = 0;

    /**
     * The parent texture atlas this texture was created from.
     */
    private AbstractTextureAtlas parent = null;

    /**
     * The x coordinate of the image relative to the size of the parent texture.
     */
    private float relX1 = 0;

    /**
     * The width plus the y coordinate of the image relative to the size of the
     * parent texture.
     */
    private float relX2 = 0;

    /**
     * The y coordinate of the image relative to the size of the parent texture.
     */
    private float relY1 = 0;

    /**
     * The height plus the y coordinate of the image relative to the size of the
     * parent texture.
     */
    private float relY2 = 0;

    /**
     * The height of the texture atlas that is the parent of this image.
     */
    private int texHeight = 0;

    /**
     * The width of the texture atlas that is the parent of this image.
     */
    private int texWidth = 0;

    /**
     * The unique texture ID of this texture.
     */
    private final int uid;

    /**
     * The width of the image itself.
     */
    private int width = 0;
    /**
     * The x coordinate of the location of the image.
     */
    private int x = 0;

    /**
     * The y coordinate of the location of the image.
     */
    private int y = 0;

    /**
     * Create a empty texture.
     */
    protected AbstractTexture() {
        super();

        uid = nextUID++;
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
    protected AbstractTexture(final int newWidth, final int newHeight,
        final int newTexWidth, final int newTexHeight) {
        this(newWidth, newHeight, newTexWidth, newTexHeight, 0, 0);
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
    protected AbstractTexture(final int newWidth, final int newHeight,
        final int newTexWidth, final int newTexHeight, final int newX,
        final int newY) {
        this();

        x = newX;
        y = newY;

        height = newHeight;
        width = newWidth;
        texWidth = newTexWidth;
        texHeight = newTexHeight;

        calculateRatio();
    }

    /**
     * Get the height of the original image.
     * 
     * @return The height of the original image.
     */
    @Override
    public final int getImageHeight() {
        return height;
    }

    /**
     * Get the width of the original image.
     * 
     * @return The width of the original image.
     */
    @Override
    public final int getImageWidth() {
        return width;
    }

    /**
     * Get the x coordinate of the position of the image on the texture atlas.
     * 
     * @return the x coordinate of the image position
     */
    @Override
    public final int getImageX() {
        return x;
    }

    /**
     * Get the y coordinate of the position of the image on the texture atlas.
     * 
     * @return the y coordinate of the image position
     */
    @Override
    public final int getImageY() {
        return y;
    }

    /**
     * Get the texture atlas assigned to this texture.
     * 
     * @return the texture atlas assigned to this texture
     */
    @Override
    public final TextureAtlas getParent() {
        return parent;
    }

    /**
     * Get the x coordinate of the texture relative to the size of the texture
     * the image is located on.
     * 
     * @return the relative x coordinate.
     */
    public final float getRelX1() {
        return relX1;
    }

    /**
     * Get the X coordinate plus the width of the texture as relative value to
     * the image it is located on.
     * 
     * @return the relative x coordinate plus the relative width
     */
    public final float getRelX2() {
        return relX2;
    }

    /**
     * Get the y coordinate of the texture relative to the size of the texture
     * the image is located on.
     * 
     * @return the relative y coordinate.
     */
    public final float getRelY1() {
        return relY1;
    }

    /**
     * Get the Y coordinate plus the height of the texture as relative value to
     * the image it is located on.
     * 
     * @return the relative y coordinate plus the relative height
     */
    public final float getRelY2() {
        return relY2;
    }

    /**
     * Get the height of the parent texture this image is located on.
     * 
     * @return the height of the texture atlas
     */
    public final int getTextureHeight() {
        return texHeight;
    }

    /**
     * Get the OpenGL ID of the parent texture of this image.
     * 
     * @return The GL texture ID
     */
    @Override
    public final int getTextureID() {
        return parent.getTextureID();
    }

    /**
     * Get the width of the parent texture this image is located on.
     * 
     * @return the width of the texture atlas
     */
    public final int getTextureWidth() {
        return texWidth;
    }

    /**
     * Get the unique ID of this texture.
     * 
     * @return the unique ID of this texture.
     */
    public final int getUID() {
        return uid;
    }

    /**
     * Call this function in case this texture is not needed anymore and needs
     * to be removed from the system.
     */
    public final void remove() {
        if (parent != null) {
            parent.decreaseLoadCounter();
            parent.checkUsed();
        }
    }

    @Override
    public final void reportUsed() {
        if (parent != null) {
            parent.increaseLoadCounter();
        }
    }

    /**
     * Set the dimension of the image this texture instance defines.
     * 
     * @param newWidth The width of the image
     * @param newHeight The height of the image
     */
    @Override
    public final void setImageDimension(final int newWidth, final int newHeight) {
        final boolean calculateNeeded =
            ((width != newWidth) || (height != newHeight));
        width = newWidth;
        height = newHeight;

        if (calculateNeeded) {
            calculateRatio();
        }
    }

    /**
     * Set the location of the image on the texture atlas the image is located
     * on.
     * 
     * @param newX the x coordinate of the image on the parent texture
     * @param newY the y coordinate of the image on the parent texture
     */
    @Override
    public final void setImageLocation(final int newX, final int newY) {
        final boolean calculateNeeded = ((x != newX) || (y != newY));
        x = newX;
        y = newY;

        if (calculateNeeded) {
            calculateRatio();
        }
    }

    /**
     * Set the parent texture of this texture.
     * 
     * @param parentAtlas the parent texture atlas of this texture
     */
    @Override
    @SuppressWarnings("nls")
    public void setParent(final TextureAtlas parentAtlas) {
        if (!(parentAtlas instanceof AbstractTextureAtlas)) {
            throw new IllegalArgumentException(
                "Invalid implementation of the texture atlas");
        }
        parent = (AbstractTextureAtlas) parentAtlas;
    }

    /**
     * Set the dimension of the parent texture this image is located on.
     * 
     * @param newTexWidth the width of the parent texture
     * @param newTexHeight the height of the parent texture
     */
    public final void setTextureDimension(final int newTexWidth,
        final int newTexHeight) {
        final boolean calculateNeeded =
            ((texHeight != newTexHeight) || (texWidth != newTexWidth));

        texHeight = newTexHeight;
        texWidth = newTexWidth;

        if (calculateNeeded) {
            calculateRatio();
        }
    }

    /**
     * This function is called in case the informations of this textures change.
     */
    protected abstract void textureDataChanged();

    /**
     * Calculate the relative values in this class. After calling this function
     * all values that are relative to the size of the atlas are valid. Has to
     * be called for sure before this values are read.
     */
    private void calculateRatio() {
        if (texHeight != 0) {
            relY2 = ((float) (y + height)) / texHeight;
            relY1 = ((float) y) / texHeight;
        }
        if (texWidth != 0) {
            relX2 = ((float) (x + width)) / texWidth;
            relX1 = ((float) x) / texWidth;
        }

        textureDataChanged();
    }
}
