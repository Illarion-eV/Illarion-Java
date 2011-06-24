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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Util;

import illarion.graphics.Graphics;
import illarion.graphics.generic.AbstractTextureAtlas;

/**
 * TextureAtlas implementation for usage with LWJGL.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class TextureAtlasLWJGL extends AbstractTextureAtlas {
    /**
     * The buffer that is used to get the generated texture IDs.
     */
    private static final IntBuffer texIDBuffer;

    static {
        texIDBuffer = BufferUtils.createIntBuffer(4);
    }

    /**
     * The internal format of this texture atlas.
     */
    private int internalFormat = 0;

    /**
     * The format of the original source data of this texture.
     */
    private int sourceFormat = 0;

    /**
     * Constructor to create a empty texture Atlas.
     */
    public TextureAtlasLWJGL() {
        super();
    }

    /**
     * Get a new texture ID from the OpenGL system. This also registers the
     * texture in the openGL environment, so it can be used right away.
     * 
     * @return the generated openGL texture ID
     */
    private static int getNewTextureID() {
        texIDBuffer.rewind();
        GL11.glGenTextures(texIDBuffer);
        return texIDBuffer.get(0);
    }

    /**
     * Activate the texture and prepare it for usage by OpenGL.
     * 
     * @param resizeable true in case the texture shall be loaded with advanced
     *            rescaling methods, that are more calculation intensive but
     *            look better then the normal ones
     * @param allowCompression true if the texture is compressed at default
     *            settings, false if not. Best disallow compression for static
     *            images such as tiles, since the effects of the compression
     *            will be quite visible there
     */
    @Override
    @SuppressWarnings("nls")
    public void activateTexture(final boolean resizeable,
        final boolean allowCompression) {

        if (!hasTextureData()) {
            throw new IllegalStateException("No texturedata loaded");
        }

        final int quality = Graphics.getInstance().getQuality();

        if (getTextureID() != 0) {
            removeTexture();
        }

        // generate new texture ID
        final int texID = getNewTextureID();
        setTextureID(texID);

        // bind texture ID
        DriverSettingsLWJGL.getInstance().enableTexture(texID);

        // prepare texture data
        if (resizeable) { // Textures will be resized -> smoothing would be good
            if (quality <= Graphics.QUALITY_LOW) {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            } else if ((quality <= Graphics.QUALITY_NORMAL) || isNoMipMaps()) {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            } else {
                if (GLContext.getCapabilities().OpenGL14) {
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                        GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
                } else {
                    setNoMipMaps(true);
                }
                if (!isNoMipMaps()) {
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                        GL11.GL_TEXTURE_MIN_FILTER,
                        GL11.GL_LINEAR_MIPMAP_LINEAR);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                        GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                } else {
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                        GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                        GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                }
            }
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
            GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
            GL11.GL_CLAMP);

        if (GLContext.getCapabilities().OpenGL13) {
            GL11.glHint(GL13.GL_TEXTURE_COMPRESSION_HINT, GL11.GL_NICEST);
        }

        // setup texture compression
        final boolean activateCompression =
            GLContext.getCapabilities().OpenGL13
                && ((allowCompression && (quality < Graphics.QUALITY_MAX)) || (quality <= Graphics.QUALITY_LOW));
        if (isTextureRGBA()) {
            internalFormat = GL11.GL_RGBA;
            sourceFormat = GL11.GL_RGBA;
            if (activateCompression) {
                internalFormat = GL13.GL_COMPRESSED_RGBA;
            }
        } else if (isTextureRGB()) {
            internalFormat = GL11.GL_RGB;
            sourceFormat = GL11.GL_RGB;
            if (activateCompression) {
                internalFormat = GL13.GL_COMPRESSED_RGB;
            }
        } else if (isTextureGrey()) {
            internalFormat = GL11.GL_LUMINANCE;
            sourceFormat = GL11.GL_LUMINANCE;
            if (activateCompression) {
                internalFormat = GL13.GL_COMPRESSED_LUMINANCE;
            }
        } else if (isTextureGreyAlpha()) {
            internalFormat = GL11.GL_LUMINANCE_ALPHA;
            sourceFormat = GL11.GL_LUMINANCE_ALPHA;
            if (activateCompression) {
                internalFormat = GL13.GL_COMPRESSED_LUMINANCE_ALPHA;
            }
        }

        final ByteBuffer texData = getTextureData();

        final int texWidth = getTextureWidth();
        final int texHeight = getTextureHeight();

        // produce a texture from the byte buffer
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, texWidth,
            texHeight, 0, sourceFormat, GL11.GL_UNSIGNED_BYTE, texData);

        if (quality < Graphics.QUALITY_MAX) {
            texIDBuffer.rewind();
            GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0,
                GL13.GL_TEXTURE_COMPRESSED, texIDBuffer);

            texData.rewind();
            if (texIDBuffer.get(0) == GL11.GL_FALSE) {
                int newInternalFormat = internalFormat;
                if (internalFormat == GL13.GL_COMPRESSED_LUMINANCE_ALPHA) {
                    newInternalFormat = GL13.GL_COMPRESSED_RGBA;
                } else if (internalFormat == GL13.GL_COMPRESSED_LUMINANCE) {
                    newInternalFormat = GL13.GL_COMPRESSED_RGB;
                }
                final int orgSize = texData.remaining();
                if (newInternalFormat != internalFormat) {
                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0,
                        newInternalFormat, texWidth, texHeight, 0,
                        sourceFormat, GL11.GL_UNSIGNED_BYTE, texData);
                    GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0,
                        GL13.GL_TEXTURE_COMPRESSED_IMAGE_SIZE, texIDBuffer);
                    final int newSize = texIDBuffer.get(0);
                    if (newSize > orgSize) {
                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0,
                            internalFormat, texWidth, texHeight, 0,
                            sourceFormat, GL11.GL_UNSIGNED_BYTE, texData);
                    }
                }
            }
        }

        texIDBuffer.rewind();
        GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0,
            GL11.GL_TEXTURE_INTERNAL_FORMAT, texIDBuffer);
        internalFormat = texIDBuffer.get();

        discardImageData();
    }

    /**
     * Add a image definition to the storage that marks the locations of the
     * image on the texture.
     * 
     * @param fileName the name of the image that works as the reference to the
     *            image file
     * @param x the x coordinate of the location of the image on the texture map
     * @param y the y coordinate of the location of the image on the texture map
     * @param w the width of the image
     * @param h the height of the image
     */
    @Override
    public void addImage(final String fileName, final int x, final int y,
        final int w, final int h) {

        final TextureLWJGL tex =
            new TextureLWJGL(w, h, getTextureWidth(), getTextureHeight(), x, y);
        tex.setParent(this);

        addTextureToBuffer(fileName, tex);
    }

    /**
     * Generate a identifier for this texture as human readable version.
     * 
     * @return human readable identifier for this texture
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "Texture: " + getFileName();
    }

    /**
     * Change a area of the texture.
     * 
     * @param x the x coordinate of the origin of the area that is changed
     * @param y the y coordinate of the origin of the area that is changed
     * @param w the width of the area that is changed
     * @param h the height of the area that is changed
     * @param image the image that is drawn in the area
     */
    @Override
    public void updateTextureArea(final int x, final int y, final int w,
        final int h, final BufferedImage image) {

        final byte[] imageByteData =
            ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        final ByteBuffer imageBuffer = getByteBuffer(imageByteData.length);
        imageBuffer.clear();
        imageBuffer.put(imageByteData, 0, imageByteData.length);
        imageBuffer.flip();

        updateTextureArea(x, y, w, h, imageBuffer);
    }

    /**
     * Change a area of the texture.
     * 
     * @param x the x coordinate of the origin of the area that is changed
     * @param y the y coordinate of the origin of the area that is changed
     * @param w the width of the area that is changed
     * @param h the height of the area that is changed
     * @param imageData the image data that shall be updated
     */
    @Override
    public void updateTextureArea(final int x, final int y, final int w,
        final int h, final ByteBuffer imageData) {
        DriverSettingsLWJGL.getInstance().enableTexture(getTextureID());
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, x, y, w, h, GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE, imageData);
        Util.checkGLError();
    }

    /**
     * Remove the texture from the video ram of the graphic card.
     */
    @Override
    protected void removeFromVRam() {
        final int texID = getTextureID();
        if (texID != 0) {
            texIDBuffer.rewind();
            texIDBuffer.put(texID);

            GL11.glDeleteTextures(texIDBuffer);
            setTextureID(0);
        }
    }
}
