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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLContext;
import javax.media.opengl.glu.GLU;

import illarion.graphics.Graphics;
import illarion.graphics.RenderTask;
import illarion.graphics.generic.AbstractTextureAtlas;

/**
 * TextureAtlas implementation for usage with JOGL.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class TextureAtlasJOGL extends AbstractTextureAtlas {
    /**
     * This render task activates a texture with the specified parameters at the
     * next update of the render system.
     * 
     * @author Martin Karing
     * @since 2.00
     * @version 2.00
     */
    private static final class ActivateTextureTask implements RenderTask {
        /**
         * This parameter stores of the texture is supposed to be compressed
         * when its drawn on the screen.
         */
        private final boolean compress;

        /**
         * This parameter stores if the texture is supposed to be resized when
         * its drawn on the screen.
         */
        private final boolean resize;

        /**
         * This is the reference to the texture atlas that is activated.
         */
        private final TextureAtlasJOGL tex;

        /**
         * The public constructor used to create a instance of this class by the
         * parent class and to setup the task for proper execution.
         * 
         * @param texture the texture that is activated
         * @param resizing the resizing flag that says if the texture is
         *            supposed to be drawn resized on the screen or not
         * @param compression the flag stores if the texture is supposed to be
         *            compressed in case its needed
         */
        public ActivateTextureTask(final TextureAtlasJOGL texture,
            final boolean resizing, final boolean compression) {
            resize = resizing;
            compress = compression;
            tex = texture;
        }

        /**
         * This function is activated upon the loop of the render task. It
         * causes the texture to be updated.
         * 
         * @param delta the time since the last update, irrelevant in this case
         * @return <code>true</code> in case this task is supposed to be
         *         executed again and since this texture is only activated once
         *         in this case always <code>false</code> is returned
         */
        @Override
        public boolean render(final int delta) {
            tex.activateTextureImpl(resize, compress);
            return false;
        }
    }

    /**
     * This is the string for the extension check that sees if the OpenGL 1.3
     * extension is available on this system.
     */
    @SuppressWarnings("nls")
    private static final String GL_EXTENSION_OPENGL13 = "GL_VERSION_1_3";

    /**
     * This is the string for the extension check that sees if the OpenGL 1.4
     * extension is available on this system.
     */
    @SuppressWarnings("nls")
    private static final String GL_EXTENSION_OPENGL14 = "GL_VERSION_1_4";

    /**
     * The buffer that is used to get the generated texture IDs.
     */
    private static final IntBuffer texIDBuffer;

    static {
        texIDBuffer =
            ByteBuffer.allocateDirect((Integer.SIZE / 8) * 4)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
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
    public TextureAtlasJOGL() {
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
        final GL gl = GLU.getCurrentGL();
        gl.glGenTextures(1, texIDBuffer);
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
    public void activateTexture(final boolean resizeable,
        final boolean allowCompression) {
        final Display renderDisplay =
            (Display) Graphics.getInstance().getRenderDisplay()
                .getRenderArea();

        renderDisplay.renderTask(new ActivateTextureTask(this, resizeable,
            allowCompression));
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

        final TextureJOGL tex =
            new TextureJOGL(w, h, getTextureWidth(), getTextureHeight(), x, y);
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

        final GL gl = GLU.getCurrentGL();
        DriverSettingsJOGL.getInstance().enableTexture(gl, getTextureID());
        gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, x, y, w, h, GL.GL_RGBA,
            GL.GL_UNSIGNED_BYTE, imageData);
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
    @SuppressWarnings("nls")
    protected void activateTextureImpl(final boolean resizeable,
        final boolean allowCompression) {

        if (!hasTextureData()) {
            throw new IllegalStateException("No texturedata loaded");
        }

        final int quality = Graphics.getInstance().getQuality();

        boolean releaseContext = false;
        if (GLContext.getCurrent() == null) {
            RenderDisplayJOGL.getGLAutoDrawable().getContext().makeCurrent();
            releaseContext = true;
        }

        if (getTextureID() != 0) {
            removeTexture();
        }

        // generate new texture ID
        final int texID = getNewTextureID();
        setTextureID(texID);

        // bind texture ID
        final GL gl = GLU.getCurrentGL();
        DriverSettingsJOGL.getInstance().enableTexture(gl, texID);
        // prepare texture data
        if (resizeable) { // Textures will be resized -> smoothing would be good
            if (quality <= Graphics.QUALITY_LOW) {
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                    GL.GL_NEAREST);
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                    GL.GL_NEAREST);
            } else if ((quality <= Graphics.QUALITY_NORMAL) || isNoMipMaps()) {
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                    GL.GL_LINEAR);
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                    GL.GL_LINEAR);
            } else {

                if (gl.isGL2ES1()
                    && gl.isExtensionAvailable(GL_EXTENSION_OPENGL14)) {
                    gl.glTexParameteri(GL.GL_TEXTURE_2D,
                        GL2ES1.GL_GENERATE_MIPMAP, GL.GL_TRUE);
                } else {
                    setNoMipMaps(true);
                }
                if (!isNoMipMaps()) {
                    gl.glTexParameteri(GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
                    gl.glTexParameteri(GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                } else {
                    gl.glTexParameteri(GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                    gl.glTexParameteri(GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                }
            }
        } else {
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                GL.GL_NEAREST);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                GL.GL_NEAREST);
        }
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
            GL2.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
            GL2.GL_CLAMP);

        if (gl.isGL2GL3() && gl.isExtensionAvailable(GL_EXTENSION_OPENGL13)) {
            gl.glHint(GL2GL3.GL_TEXTURE_COMPRESSION_HINT, GL.GL_NICEST);
        }

        // setup texture compression
        final boolean activateCompression =
            gl.isExtensionAvailable(GL_EXTENSION_OPENGL13)
                && ((allowCompression && (quality < Graphics.QUALITY_MAX)) || (quality <= Graphics.QUALITY_LOW));
        if (isTextureRGBA()) {
            internalFormat = GL.GL_RGBA;
            sourceFormat = GL.GL_RGBA;
            if (activateCompression && gl.isGL2GL3()) {
                internalFormat = GL2GL3.GL_COMPRESSED_RGBA;
            }
        } else if (isTextureRGB()) {
            internalFormat = GL.GL_RGB;
            sourceFormat = GL.GL_RGB;
            if (activateCompression && gl.isGL2GL3()) {
                internalFormat = GL2GL3.GL_COMPRESSED_RGB;
            }
        } else if (isTextureGrey()) {
            internalFormat = GL.GL_LUMINANCE;
            sourceFormat = GL.GL_LUMINANCE;
            if (activateCompression && gl.isGL2()) {
                internalFormat = GL2.GL_COMPRESSED_LUMINANCE;
            }
        } else if (isTextureGreyAlpha()) {
            internalFormat = GL.GL_LUMINANCE_ALPHA;
            sourceFormat = GL.GL_LUMINANCE_ALPHA;
            if (activateCompression && gl.isGL2()) {
                internalFormat = GL2.GL_COMPRESSED_LUMINANCE_ALPHA;
            }
        }

        final ByteBuffer texData = getTextureData();

        final int texWidth = getTextureWidth();
        final int texHeight = getTextureHeight();

        // produce a texture from the byte buffer
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, internalFormat, texWidth,
            texHeight, 0, sourceFormat, GL.GL_UNSIGNED_BYTE, texData);

        if (activateCompression && gl.isGL2GL3()) {
            texIDBuffer.rewind();
            gl.getGL2GL3().glGetTexLevelParameteriv(GL.GL_TEXTURE_2D, 0,
                GL2GL3.GL_TEXTURE_COMPRESSED, texIDBuffer);

            texData.rewind();
            if (texIDBuffer.get(0) == GL.GL_FALSE) {
                int newInternalFormat = internalFormat;
                if (internalFormat == GL2.GL_COMPRESSED_LUMINANCE_ALPHA) {
                    newInternalFormat = GL2GL3.GL_COMPRESSED_RGBA;
                } else if (internalFormat == GL2.GL_COMPRESSED_LUMINANCE) {
                    newInternalFormat = GL2GL3.GL_COMPRESSED_RGB;
                }
                final int orgSize = texData.remaining();
                if (newInternalFormat != internalFormat) {
                    gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, newInternalFormat,
                        texWidth, texHeight, 0, sourceFormat,
                        GL.GL_UNSIGNED_BYTE, texData);
                    gl.getGL2GL3().glGetTexLevelParameteriv(GL.GL_TEXTURE_2D,
                        0, GL2GL3.GL_TEXTURE_COMPRESSED_IMAGE_SIZE,
                        texIDBuffer);
                    final int newSize = texIDBuffer.get(0);
                    if (newSize > orgSize) {
                        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, internalFormat,
                            texWidth, texHeight, 0, sourceFormat,
                            GL.GL_UNSIGNED_BYTE, texData);
                    }
                }
            }
        }

        if (gl.isGL2GL3()) {
            texIDBuffer.rewind();
            gl.getGL2GL3().glGetTexLevelParameteriv(GL.GL_TEXTURE_2D, 0,
                GL2GL3.GL_TEXTURE_INTERNAL_FORMAT, texIDBuffer);
            internalFormat = texIDBuffer.get();
        }

        discardImageData();

        if (releaseContext) {
            gl.getContext().release();
        }
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
            final GL gl = GLU.getCurrentGL();
            texIDBuffer.flip();

            gl.glDeleteTextures(1, texIDBuffer);
            setTextureID(0);
        }
    }
}
