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
package illarion.graphics;

/**
 * Interface that handles a single graphic of a graphic atlas. Pointing always
 * only to one of the texture graphics.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface Texture {
    /**
     * Get the height of the original image.
     * 
     * @return The height of the original image.
     */
    int getImageHeight();

    /**
     * Get the width of the original image.
     * 
     * @return The width of the original image.
     */
    int getImageWidth();

    /**
     * Get the x coordinate of the position of the image on the texture atlas.
     * 
     * @return the x coordinate of the image position
     */
    int getImageX();

    /**
     * Get the y coordinate of the position of the image on the texture atlas.
     * 
     * @return the y coordinate of the image position
     */
    int getImageY();

    /**
     * Get the parent texture atlas assigned to this texture
     * 
     * @return the parent texture atlas
     */
    TextureAtlas getParent();

    /**
     * Get the ID of the atlas the texture this instance is pointing at is a
     * part of.
     * 
     * @return the ID of the atlas
     */
    int getTextureID();

    /**
     * Report that this texture is now in use.
     */
    void reportUsed();

    /**
     * Set the dimension of the image this texture instance defines.
     * 
     * @param newWidth The width of the image
     * @param newHeight The height of the image
     */
    void setImageDimension(int newWidth, int newHeight);

    /**
     * Set the location of the image on the texture atlas the image is located
     * on.
     * 
     * @param newX the x coordinate of the image on the parent texture
     * @param newY the y coordinate of the image on the parent texture
     */
    void setImageLocation(int newX, int newY);

    /**
     * Set the parent texture that is used as source of this texture definition.
     * 
     * @param parentAtlas the parent texture atlas
     */
    void setParent(TextureAtlas parentAtlas);
}
