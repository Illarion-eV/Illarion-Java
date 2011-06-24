/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Build Utility. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build.imagepacker;

import java.util.ArrayList;

/**
 * This class is used to define the empty space on a texture atlas.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
final class Space implements TextureElement {
    /**
     * This is the buffer used to store the unused instances of the space
     * objects.
     */
    private static final ArrayList<Space> BUFFER = new ArrayList<Space>();

    /**
     * The height of the space.
     */
    private int height;

    /**
     * The width of the space.
     */
    private int width;

    /**
     * The X coordinate of the space.
     */
    private int x;

    /**
     * The y coordinate of the space.
     */
    private int y;

    /**
     * Private constructor to ensure that the only instances fetched are the
     * once created by the {@link #getSpace(int, int, int, int)} function.
     */
    private Space() {
        // nothing to do
    }

    /**
     * Get a space with a set size and location. This function will either
     * create a new instance or reuse a old one.
     * 
     * @param x the x coordinate of this space
     * @param y the y coordinate of this space
     * @param height the height of this space
     * @param width the width of this space
     * @return the space instance that is filled with the required values
     */
    public static Space getSpace(final int x, final int y, final int height,
        final int width) {
        Space retSpace = null;
        if (BUFFER.isEmpty()) {
            retSpace = new Space();
        } else {
            retSpace = BUFFER.remove(BUFFER.size() - 1);
        }
        retSpace.setDim(x, y, height, width);

        return retSpace;
    }

    /**
     * Check if a sprite fits into the space.
     * 
     * @param s the sprite to test
     * @return <code>true</code> in case the sprite fits into the space
     */
    public boolean fitsInside(final TextureElement s) {
        return ((s.getHeight() <= height) && (s.getWidth() <= width));
    }

    /**
     * Get the height of this space.
     * 
     * @return the height of this space
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Get the size of this space in pixels.
     * 
     * @return the size of this space
     */
    public long getSize() {
        return height * width;
    }

    /**
     * Get the width of this space.
     * 
     * @return the width of this space
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Get the X coordinate of the origin of this space.
     * 
     * @return the x coordinate of this space
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Get the Y coordinate of the origin of this space.
     * 
     * @return the y coordinate of this space
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Recycle this instance, so it can be reused later.
     */
    public void recycle() {
        BUFFER.add(this);
    }

    /**
     * Set the size of this space. This function is used to prepare the values
     * that are required for this space.
     * 
     * @param posX the x coordinate of the origin of the space
     * @param posY the y coordinate of the origin of the space
     * @param spaceHeight the height of the space
     * @param spaceWidth the width of the space
     */
    private void setDim(final int posX, final int posY, final int spaceHeight,
        final int spaceWidth) {
        x = posX;
        y = posY;
        height = spaceHeight;
        width = spaceWidth;
    }
}
