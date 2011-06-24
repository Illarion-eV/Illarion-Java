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

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastTable;

import illarion.graphics.Texture;
import illarion.graphics.TextureAtlas;
import illarion.graphics.TextureAtlasListener;

/**
 * Generic texture atlas implementation that contains the implementation that is
 * shared by all library specific implementations.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public abstract class AbstractTextureAtlas implements TextureAtlas {
    /**
     * A list of all known texture atlas objects. Used to optimize the texture
     * usage.
     */
    private static final List<AbstractTextureAtlas> existingAtlasObject =
        new FastTable<AbstractTextureAtlas>();

    /**
     * Flag that makes the texture atlas not removing the texture data after the
     * texture is activated.
     */
    private boolean keepTextureData;

    /**
     * The listener class the death of the texture atlas is reported to.
     */
    private TextureAtlasListener listener;

    /**
     * A counter to keep track how many instances of this texture are around.
     * This is needed to find out if the texture can be deleted entirely.
     */
    private int loadedCount;

    /**
     * In some cases the texture atlas will create mip maps. This flag ensures
     * that none are generated.
     */
    private boolean noMipMaps = false;

    /**
     * The file name this atlas was original loaded from.
     */
    private String ownFileName;

    /**
     * A buffer of the already loaded textures to avoid that a single graphic is
     * loaded into multiple textures.
     */
    private Map<String, Texture> textureBuffer;

    /**
     * The image data of the texture made for openGL.
     */
    private ByteBuffer textureData;

    /**
     * The height of the texture atlas that is loaded in this instance.
     */
    private int textureHeight;

    /**
     * The OpenGL ID of this texture atlas. When using this texture this ID must
     * be the active openGL texture ID.
     */
    private int textureID = 0;

    /**
     * The type of the texture that is used.
     */
    private int textureType;

    /**
     * The width of the texture atlas that is loaded in this instance.
     */
    private int textureWidth;

    /**
     * This byte buffer, in case its set, contains the bit mask that stores if a
     * pixel of the texture is entirely transparent or not.
     */
    private ByteBuffer transparencyMask = null;

    /**
     * The constructor for this texture atlas that prepares the required
     * variables for proper usage.
     */
    protected AbstractTextureAtlas() {
        textureBuffer = new FastMap<String, Texture>();
        textureType = TYPE_RGBA;
        existingAtlasObject.add(this);
        keepTextureData = false;
    }

    /**
     * This removes all texture data from the video memory. After calling this
     * function, no texture can be rendered anymore.
     */
    public static void dispose() {
        while (!existingAtlasObject.isEmpty()) {
            existingAtlasObject.get(0).removeTexture();
        }
    }

    /**
     * Get a byte buffer that was buffered before in order to avoid that it
     * needs to be recreated. Don't forget to clear the returned buffer.
     * 
     * @param size the size of the buffer needed
     * @return the byte buffer, either newly created or from the buffered ones
     */
    protected static ByteBuffer getByteBuffer(final int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    /**
     * Check if the texture atlas is still in use and if its not in use, report
     * this to its listener.
     */
    @Override
    public final void checkUsed() {
        if ((loadedCount == 0) && (listener != null)) {
            listener.reportDeath(this);
        }
    }

    /**
     * Decrease the counter that keeps track how many objects loaded this
     * texture. Always run this when any texture referring to this texture atlas
     * is deleted.
     */
    public final void decreaseLoadCounter() {
        --loadedCount;
    }

    /**
     * Remove the image data from the system.
     */
    @Override
    public final void discardImageData() {
        if (!keepTextureData) {
            textureData = null;
        }
    }

    /**
     * Finalize the texture. This causes that no more textures can be obtained
     * from the texture atlas. Call this for optimizing reasons after the
     * loading of the textures is done.
     */
    @Override
    public final void finish() {
        textureBuffer = null;
    }

    /**
     * Copy all textures stored in this texture atlas into one hash map.
     * 
     * @param target the hash map to receive all textures
     */
    @Override
    public final void getAllTextures(final Map<String, Texture> target) {
        target.putAll(textureBuffer);
    }

    /**
     * Get the file name that atlas was loaded from.
     * 
     * @return the filename of the file this texture was loaded from.
     * @see illarion.graphics.TextureAtlas#getFileName()
     */
    @Override
    public final String getFileName() {
        return ownFileName;
    }

    /**
     * Get one of the textures that is stored in this texture atlas. The texture
     * instance contains all data to render the image with the sprite class of
     * this implementation.
     * 
     * @param name the name of the texture that is required
     * @return null in case the searched image is not included in this texture-
     *         atlas, a instance of Texture in case it is included that will
     *         contain the needed data about this texture.
     * @see illarion.graphics.TextureAtlas#getTexture(String)
     */
    @Override
    public final Texture getTexture(final String name) {
        final Texture returnVal = textureBuffer.get(name);
        if (returnVal != null) {
            increaseLoadCounter();
        }

        return returnVal;
    }

    /**
     * Return the data of the texture.
     * 
     * @return the byte buffer holding the texture data
     */
    @Override
    public final ByteBuffer getTextureData() {
        return textureData.asReadOnlyBuffer();
    }

    /**
     * Get the height of the texture that was loaded.
     * 
     * @return the height of the texture that was loaded
     */
    @Override
    public final int getTextureHeight() {
        return textureHeight;
    }

    /**
     * Get the OpenGL texture ID for this texture that is unique for sure.
     * 
     * @return the OpenGL texture ID of this atlas
     * @see illarion.graphics.TextureAtlas#getTextureID()
     */
    @Override
    public final int getTextureID() {
        return textureID;
    }

    /**
     * Get the load texture data as a image.
     * 
     * @return the load texture data as image
     */
    @Override
    @SuppressWarnings("nls")
    public final BufferedImage getTextureImage() {
        final ByteBuffer texData = getTextureData();
        if (texData == null) {
            throw new IllegalStateException(
                "Can't get Image after textureData was discarded");
        }
        ComponentColorModel colorModel;
        WritableRaster raster;
        if (isTextureRGBA()) {
            colorModel =
                new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8,
                        8, 8, 8 }, true, false, Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE);
            raster =
                Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                    getTextureWidth(), getTextureHeight(), 4, null);
        } else if (isTextureRGB()) {
            colorModel =
                new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8,
                        8, 8 }, false, false, Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE);
            raster =
                Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                    getTextureWidth(), getTextureHeight(), 3, null);
        } else if (isTextureGreyAlpha()) {
            colorModel =
                new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[] { 8,
                        8 }, true, false, Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE);
            raster =
                Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                    getTextureWidth(), getTextureHeight(), 2, null);
        } else if (isTextureGrey()) {
            colorModel =
                new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_GRAY),
                    new int[] { 8 }, false, false, Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE);
            raster =
                Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                    getTextureWidth(), getTextureHeight(), 1, null);
        } else {
            return null;
        }

        texData.rewind();
        texData.get(((DataBufferByte) raster.getDataBuffer()).getData());

        return new BufferedImage(colorModel, raster, false,
            new Hashtable<Object, Object>());
    }

    /**
     * Get a set containing all texture names and definitions
     * 
     * @return all texture names and definitions
     */
    @Override
    public final Set<Entry<String, Texture>> getTextures() {
        return textureBuffer.entrySet();
    }

    /**
     * Get the type of the texture.
     * 
     * @return the type of the texture
     */
    @Override
    public final int getTextureType() {
        return textureType;
    }

    /**
     * Get the width of the texture that was loaded.
     * 
     * @return the width of the texture that was loaded
     */
    @Override
    public final int getTextureWidth() {
        return textureWidth;
    }

    /**
     * This function returns the stored transparency mask or <code>null</code>
     * in case the mask was not set.
     */
    @Override
    public final ByteBuffer getTransparencyMask() {
        return transparencyMask;
    }

    /**
     * This function returns <code>true</code> in case a transparency mask is
     * defined.
     */
    @Override
    public final boolean hasTransparencyMask() {
        return (transparencyMask != null);
    }

    /**
     * Increase the counter that keeps track how many objects loaded this
     * texture. Always run this when any texture referring to this texture atlas
     * is created.
     */
    public final void increaseLoadCounter() {
        ++loadedCount;
    }

    /**
     * Check if a pixel at a specified location is transparent or not.
     */
    @Override
    @SuppressWarnings("nls")
    public final boolean isPixelTransparent(final int x, final int y) {
        if (transparencyMask == null) {
            return false;
        }

        if ((x < 0) || (y < 0) || (x >= textureWidth) || (y >= textureHeight)) {
            throw new IllegalArgumentException("Coordinates out of range");
        }

        final int pixelIndex = x + (y * textureHeight);

        final int bufferIndex = pixelIndex / 8;
        final int pixelMask = 1 << (pixelIndex % 8);

        return ((transparencyMask.get(bufferIndex) & pixelMask) == pixelMask);
    }

    /**
     * Remove the texture from the openGL system. After this function call the
     * texture atlas is not usable anymore.
     */
    @Override
    public final void removeTexture() {
        existingAtlasObject.remove(this);
        setKeepTextureData(false);
        discardImageData();
        removeFromVRam();
    }

    /**
     * Set the dimensions of the texture atlas. The normal client should not use
     * this, it needed to build the texture atlas by the config tool. If a
     * texture is read from a file this values are set automatically.
     * 
     * @param newWidth the new value for the width of the texture
     * @param newHeight the new value for the height of the texture
     */
    @Override
    public final void setDimensions(final int newWidth, final int newHeight) {
        textureWidth = newWidth;
        textureHeight = newHeight;
    }

    /**
     * Set the name of the file the Texture Atlas was loaded from.
     * 
     * @param newFileName the filename that shall be stored in this instance of
     *            the texture atlas.
     */
    @Override
    public final void setFileName(final String newFileName) {
        ownFileName = newFileName;
    }

    /**
     * Set the flag that makes the texture not discarding the texture data after
     * activating the texture.
     * 
     * @param newKeepTextureData the new state of the flag
     */
    @Override
    public final void setKeepTextureData(final boolean newKeepTextureData) {
        keepTextureData = newKeepTextureData;
    }

    /**
     * Set the listener class for this texture atlas that shall receive the
     * notify of the death of this texture atlas in case it occurs.
     * 
     * @param newListener the listener class
     */
    @Override
    public final void setListener(final TextureAtlasListener newListener) {
        listener = newListener;
    }

    /**
     * Set the image data of the texture. This is only needed in case the
     * texture receives its data not from a texture file. The BufferedImage
     * loaded into this function is automatically converted in the best format
     * for rendering it using OpenGL.
     * 
     * @param imageData the image that shall be stored in this texture
     */
    @Override
    public final void setTextureImage(final BufferedImage imageData) {
        noMipMaps = true;

        final byte[] imageByteData =
            ((DataBufferByte) imageData.getRaster().getDataBuffer()).getData();

        final ByteBuffer imageBuffer = getByteBuffer(imageByteData.length);
        imageBuffer.put(imageByteData, 0, imageByteData.length);

        imageBuffer.flip();

        setTextureImage(imageBuffer);
    }

    /**
     * Set the image data of the texture. This is only needed in case the
     * texture receives its data not from a texture file. The BufferedImage
     * loaded into this function is automatically converted in the best format
     * for rendering it using OpenGL.
     * 
     * @param imageData the image that shall be stored in this texture
     */
    @SuppressWarnings("nls")
    @Override
    public final void setTextureImage(final ByteBuffer imageData) {
        if (!imageData.hasRemaining()) {
            throw new IllegalArgumentException(
                "There is no data remaining in the buffer!");
        }
        textureData = imageData;
    }

    /**
     * Set the texture type that shall be used.
     * 
     * @param type the new type that shall be used
     */
    @Override
    public final void setTextureType(final int type) {
        textureType = type;
    }

    /**
     * This function set the transparency mask. It also performs the required
     * checks to ensure that the transparency has the expected specifications.
     */
    @SuppressWarnings("nls")
    @Override
    public final void setTransparencyMask(final ByteBuffer mask) {
        if (mask == null) {
            throw new NullPointerException("mask must not be NULL");
        }

        int expectedSize = (textureHeight * textureWidth) / 8;
        if ((expectedSize * 8) < (textureHeight * textureWidth)) {
            expectedSize++;
        }

        if (mask.remaining() != expectedSize) {
            throw new IllegalArgumentException("Size of the mask is: "
                + Integer.toString(mask.remaining())
                + " Bytes but was expected to be "
                + Integer.toString(expectedSize) + " Bytes.");
        }

        transparencyMask = mask;
    }

    /**
     * Add a texture to the buffer of textures.
     * 
     * @param name the name of the texture
     * @param tex the texture itself
     */
    protected final void addTextureToBuffer(final String name,
        final Texture tex) {
        textureBuffer.put(name, tex);
    }

    /**
     * Check if this texture has some stored texture data that is ready to be
     * activated.
     * 
     * @return <code>true</code> if there is texture data in the memory that is
     *         ready to be transfered to the video memory
     */
    protected final boolean hasTextureData() {
        return (textureData != null);
    }

    /**
     * Check if mipMaps are supposed to be generated.
     * 
     * @return <code>true</code> to generate mip maps
     */
    protected boolean isNoMipMaps() {
        return noMipMaps;
    }

    /**
     * Check if this texture is a grey scale texture.
     * 
     * @return <code>true</code> if this texture is a grey scale texture
     */
    protected final boolean isTextureGrey() {
        return (textureType == TYPE_GREY);
    }

    /**
     * Check if this texture is a grey scale texture with alpha.
     * 
     * @return <code>true</code> if this texture is a grey scale texture with
     *         alpha
     */
    protected final boolean isTextureGreyAlpha() {
        return (textureType == TYPE_GREY_ALPHA);
    }

    /**
     * Check if this texture is a RGB texture.
     * 
     * @return <code>true</code> if this texture is a RGB texture
     */
    protected final boolean isTextureRGB() {
        return (textureType == TYPE_RGB);
    }

    /**
     * Check if this texture is a RGB texture with alpha.
     * 
     * @return <code>true</code> if this texture is a RGB texture with alpha
     */
    protected final boolean isTextureRGBA() {
        return (textureType == TYPE_RGBA);
    }

    /**
     * Remove the texture from the video ram of the graphic card.
     */
    protected abstract void removeFromVRam();

    /**
     * Set the new value of the noMipMaps flag. That flag will ensure that no
     * mipMaps are created.
     * 
     * @param value <code>false</code> to ensure that no mipMaps are generated
     */
    protected void setNoMipMaps(final boolean value) {
        noMipMaps = value;
    }

    /**
     * Set the texture ID to a new value.
     * 
     * @param id the new value of the texture ID
     */
    protected final void setTextureID(final int id) {
        textureID = id;
    }
}
