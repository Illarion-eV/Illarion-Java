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

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Interface of a generic texture atlas that is able to save, load and generate
 * texture files and to instantiate the texture to offer them to the rest of the
 * software.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface TextureAtlas {
    /**
     * The type constant for the texture using grey scale without alpha.
     */
    int TYPE_GREY = 3;

    /**
     * The type constant for the texture using grey scale and alpha.
     */
    int TYPE_GREY_ALPHA = 1;

    /**
     * The type constant for the texture using the RGB color space without
     * alpha.
     */
    int TYPE_RGB = 2;

    /**
     * The type constant for the texture using the RGBA color space.
     */
    int TYPE_RGBA = 0;

    /**
     * Activate the texture and prepare it for usage.
     * 
     * @param resizeable true in case the texture shall be prepared to be
     *            resized
     * @param allowCompression true in case the system can compress the image in
     *            order to save some storage space
     */
    void activateTexture(boolean resizeable, boolean allowCompression);

    /**
     * Add a image definition to the storage that marks the locations of the
     * image on the texture.
     * 
     * @param fileName the name of the image
     * @param x the x coordinate of the location of the picture
     * @param y the y coordinate of the location of the picture
     * @param w the width of the picture
     * @param h the height of the picture
     */
    void addImage(String fileName, int x, int y, int w, int h);

    /**
     * Check if the texture is still in use, if not report this using the
     * listener.
     */
    void checkUsed();

    /**
     * Remove the load texture data. That is only needed in case the auto remove
     * after activating the texture is disabled.
     */
    void discardImageData();

    /**
     * Finalize the texture. This causes that no more textures can be obtained
     * from the texture atlas. Call this for optimizing reasons after the
     * loading of the textures is done.
     */
    void finish();

    /**
     * Copy all textures stored in this texture atlas into one hash map.
     * 
     * @param target the hash map to receive all textures
     */
    void getAllTextures(final Map<String, Texture> target);

    /**
     * Get the file name that atlas was loaded from.
     * 
     * @return the FileName
     */
    String getFileName();

    /**
     * Get a texture instance pointing on a specified image within this texture
     * atlas.
     * 
     * @param name the name of the image within the texture atlas.
     * @return the instance of texture or null in case the image was not found
     */
    Texture getTexture(String name);

    /**
     * Get the byte data of the texture. The required format depends on the type
     * of the texture. So its one, two, three or four byte per pixel. The image
     * data needs to be uncompressed.
     * 
     * @return the byte data of the texture
     */
    ByteBuffer getTextureData();

    /**
     * Get the height of the atlas texture.
     * 
     * @return the height of the atlas texture
     */
    int getTextureHeight();

    /**
     * Get a unique identifier of the texture.
     * 
     * @return the ID number of the texture
     */
    int getTextureID();

    /**
     * Get the texture as a buffered image. This will only work before the image
     * data is discarded. Make sure to disable the discarding before its
     * activated or call this function before its activated.
     * <p>
     * Every call of this function will create a new instance of this image.
     * Calling it multiple times for the same image will waste alot of space.
     * </p>
     * 
     * @return the buffered image of this texture
     */
    BufferedImage getTextureImage();

    /**
     * Get the definitions of all textures defined in this atlas.
     * 
     * @return a set of texture definitions, the entry key holds the name of the
     *         texture, the value holds the texture itself
     */
    Set<Entry<String, Texture>> getTextures();

    /**
     * Get the texture type of this texture.
     * 
     * @return one of the type constants
     * @see #TYPE_RGB
     * @see #TYPE_RGBA
     * @see #TYPE_GREY
     * @see #TYPE_GREY_ALPHA
     */
    int getTextureType();

    /**
     * Get the width of the atlas texture.
     * 
     * @return the width of the texture
     */
    int getTextureWidth();

    /**
     * Get the transparency mask that describes the exact shape of the displayed
     * objects.
     * 
     * @return the transparency mask or <code>null</code> in case no such mask
     *         is defined
     */
    ByteBuffer getTransparencyMask();

    /**
     * Check if this texture atlas has a transparency mask that can be used to
     * determine the exact visual shape of the objects that are visible on this
     * texture.
     * 
     * @return <code>true</code> in case there is a transparency mask
     */
    boolean hasTransparencyMask();

    /**
     * Check if a pixel at the specified location is transparent. This only
     * works in case the texture has additional transparency informations. To
     * check if this informations are available use the
     * {@link #hasTransparencyMask()} function.
     * 
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     * @return <code>true</code> if the transparency informations are available
     *         and say that this pixel is fully transparent
     * @throws IllegalArgumentException in case one of the coordinates is
     *             outside the area of this texture
     */
    boolean isPixelTransparent(int x, int y);

    /**
     * Remove the texture from the system. After this function call the texture
     * is not available anymore.
     */
    void removeTexture();

    /**
     * Set the dimensions of the texture atlas. The normal client should not use
     * this, it needed to build the texture atlas by the configuration tool.
     * 
     * @param newWidth the new width value for this texture atlas
     * @param newHeight the new height value for this texture atlas
     */
    void setDimensions(int newWidth, int newHeight);

    /**
     * Set the name of the file this atlas was original loaded from.
     * 
     * @param newFileName the ownFileName to set
     */
    void setFileName(String newFileName);

    /**
     * Set the flag that makes the texture not discarding the texture data after
     * activating the texture.
     * 
     * @param newKeepTextureData the new state of the flag
     */
    void setKeepTextureData(final boolean newKeepTextureData);

    /**
     * Set the listener of the texture atlas instance in case its needed. The
     * texture atlas will report its own death to this listener in case it
     * happens.
     * 
     * @param listener the listener function that shall be used
     */
    void setListener(TextureAtlasListener listener);

    /**
     * Set the texture image from a external source. This has to fit to the
     * definitions of the files that were stored in this class or are about to
     * be stored in this class. This function also has to trigger a conversion
     * of the format to the internal needed format if needed.
     * 
     * @param imageData the image that shall be used as texture
     */
    void setTextureImage(BufferedImage imageData);

    /**
     * Set the texture image from a external source. This has to fit to the
     * definitions of the files that were stored in this class or are about to
     * be stored in this class. This function also has to trigger a conversion
     * of the format to the internal needed format if needed.
     * 
     * @param imageData the image that shall be used as texture
     */
    void setTextureImage(ByteBuffer imageData);

    /**
     * Set the type of this texture. That could the default RGBA image, but also
     * a gray scale image with alpha or a image without alpha.
     * 
     * @param type the new type of this texture atlas graphic
     */
    void setTextureType(int type);

    /**
     * Set the transparency mask of this texture. This function is only supposed
     * to be used while compiling this texture.
     * 
     * @param mask the transparency mask of this texture
     * @throws NullPointerException in case the mask argument is
     *             <code>null</code>
     * @throws IllegalArgumentException if the remaining bytes of the mask do
     *             not equal the expected size for this mask
     */
    void setTransparencyMask(ByteBuffer mask);

    /**
     * Change a area of the texture.
     * 
     * @param x the x coordinate of the origin of the area that is changed
     * @param y the y coordinate of the origin of the area that is changed
     * @param w the width of the area that is changed
     * @param h the height of the area that is changed
     * @param image the image that is drawn in the area
     */
    void updateTextureArea(int x, int y, int w, int h, BufferedImage image);

    /**
     * Change a area of the texture.
     * 
     * @param x the x coordinate of the origin of the area that is changed
     * @param y the y coordinate of the origin of the area that is changed
     * @param w the width of the area that is changed
     * @param h the height of the area that is changed
     * @param imageData the byte data of the image
     */
    void updateTextureArea(int x, int y, int w, int h, ByteBuffer imageData);
}
