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
package illarion.graphics.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Set;

import javolution.context.ArrayFactory;

import illarion.common.util.FastMath;

import illarion.graphics.Graphics;
import illarion.graphics.Texture;
import illarion.graphics.TextureAtlas;

/**
 * A utility class for reading and writing the data from textures. This class
 * allows all implementations of the graphic port to read and write the textures
 * in exactly the same way.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public final class TextureIO {
    /**
     * The character set that is used to encode the filenames of the textures.
     */
    @SuppressWarnings("nls")
    private static final Charset FILENAME_CHARSET = Charset
        .forName("ISO-8859-1");

    /**
     * Size of the header of a texture in byte.
     */
    private static final int TEXTURE_HEADER_SIZE = 18;

    /**
     * Version number of the texture files required.
     */
    private static final byte TEXTURE_VERSION_NUMBER = 2;

    /**
     * The size in byte that is used for the byte buffer when writing a texture
     * to the file system.
     */
    private static final int TEXTURE_WRITING_BUFFER_SIZE = 1000000;

    /**
     * Private constructor to avoid any instances created from this utility
     * class.
     */
    private TextureIO() {
        // nothing to be done
    }

    /**
     * Read a texture atlas from a byte channel. This method does only check the
     * texture version to be valid. Everything else remains unchecked in order
     * not to slow down the loading of the texture.
     * 
     * @param inChannel the channel that is the source of the data
     * @return the texture created from the data delivered by the input channel
     * @throws IOException in case anything goes wrong while reading the input
     *             channel
     */
    @SuppressWarnings("nls")
    public static TextureAtlas readTexture(final ReadableByteChannel inChannel)
        throws IOException {
        final ByteBuffer headerBuffer =
            ByteBuffer.allocateDirect(TEXTURE_HEADER_SIZE).order(
                ByteOrder.LITTLE_ENDIAN);

        // read header into buffer
        while (headerBuffer.hasRemaining()) {
            if (inChannel.read(headerBuffer) == -1) {
                break;
            }
        }
        headerBuffer.flip();

        // read and check version
        final byte version = headerBuffer.get();
        if (version != TEXTURE_VERSION_NUMBER) {
            throw new IllegalStateException("Invalid Texture version: "
                + Integer.toString(version));
        }

        final TextureAtlas texAtlas = Graphics.getInstance().getTextureAtlas();

        // read and set texture dimension
        final int width = headerBuffer.getShort();
        final int height = headerBuffer.getShort();
        texAtlas.setDimensions(width, height);

        // read and set texture type
        final int type = headerBuffer.get();
        texAtlas.setTextureType(type);

        // get the size of the definitions of the textures for this atlas
        final int texDefSize = headerBuffer.getInt();

        // get the size of the texture data for this atlas
        final int texDataSize = headerBuffer.getInt();
        final int transparencyMaskSize = headerBuffer.getInt();

        final ByteBuffer texDefBuffer =
            ByteBuffer.allocateDirect(texDefSize).order(
                ByteOrder.LITTLE_ENDIAN);
        while (texDefBuffer.hasRemaining()) {
            if (inChannel.read(texDefBuffer) == -1) {
                break;
            }
        }
        texDefBuffer.flip();

        int strLen = 0;
        String filename = null;
        int x;
        int y;
        int w;
        int h;
        while (texDefBuffer.hasRemaining()) {
            // length of the filename first
            strLen = texDefBuffer.getShort();

            final byte[] filenameByte =
                ArrayFactory.BYTES_FACTORY.array(Math.max(5, strLen));
            texDefBuffer.get(filenameByte, 0, strLen);
            filename = new String(filenameByte, 0, strLen, FILENAME_CHARSET);
            ArrayFactory.BYTES_FACTORY.recycle(filenameByte);

            // first the x coordinate of the image
            x = texDefBuffer.getShort();

            // now the y coordinate of the image
            y = texDefBuffer.getShort();

            // now the width of the image
            w = texDefBuffer.getShort();

            // now the height of the image
            h = texDefBuffer.getShort();

            // store the definition data in this class
            texAtlas.addImage(filename, x, y, w, h);
        }

        final ByteBuffer textureData =
            ByteBuffer.allocateDirect(texDataSize).order(
                ByteOrder.LITTLE_ENDIAN);
        while (textureData.hasRemaining()) {
            if (inChannel.read(textureData) == -1) {
                break;
            }
        }
        textureData.flip();

        texAtlas.setTextureImage(textureData);

        if (transparencyMaskSize > 0) {
            final ByteBuffer transparencyData =
                ByteBuffer.allocateDirect(texDataSize).order(
                    ByteOrder.LITTLE_ENDIAN);

            while (transparencyData.hasRemaining()) {
                if (inChannel.read(transparencyData) == -1) {
                    break;
                }
            }
            transparencyData.flip();

            texAtlas.setTransparencyMask(transparencyData);
        }

        return texAtlas;
    }

    /**
     * Write the texture data to a output channel.
     * 
     * @param outChannel the channel that shall receive the texture data
     * @param tex the texture atlas that shall be written to the channel
     * @throws IOException in case the writing to the channel caused any problem
     * @throws IllegalStateException in case any validation check of the texture
     *             data failed
     */
    @SuppressWarnings("nls")
    public static void writeTexture(final WritableByteChannel outChannel,
        final TextureAtlas tex) throws IOException {
        final ByteBuffer writeBuffer =
            ByteBuffer.allocateDirect(TEXTURE_WRITING_BUFFER_SIZE).order(
                ByteOrder.LITTLE_ENDIAN);

        // writing the version of the texture
        writeBuffer.put(TEXTURE_VERSION_NUMBER);

        // get width and height
        final int width = tex.getTextureWidth();
        final int height = tex.getTextureHeight();

        // validate width and height
        if (width <= 0) {
            throw new IllegalStateException("Width is smaller or equal to 0");
        }
        if (!FastMath.isPowerOfTwo(width)) {
            throw new IllegalStateException("Width is not power of two.");
        }
        if (height <= 0) {
            throw new IllegalStateException("Height is smaller or equal to 0");
        }
        if (!FastMath.isPowerOfTwo(height)) {
            throw new IllegalStateException("Height is not power of two.");
        }

        // write width and height
        writeBuffer.putShort((short) width);
        writeBuffer.putShort((short) height);

        // get the type of the texture
        final int type = tex.getTextureType();

        // validate type
        if ((type != TextureAtlas.TYPE_RGB)
            && (type != TextureAtlas.TYPE_RGBA)
            && (type != TextureAtlas.TYPE_GREY)
            && (type != TextureAtlas.TYPE_GREY_ALPHA)) {
            throw new IllegalStateException("Illegal texture type");
        }

        // write type
        writeBuffer.put((byte) type);

        // store the position where the size of the header needs to be stored
        final int headerPos = writeBuffer.position();
        writeBuffer.putInt(0);

        int expectedTextureSize = width * height;
        if (type == TextureAtlas.TYPE_RGB) {
            expectedTextureSize *= 3;
        } else if (type == TextureAtlas.TYPE_RGBA) {
            expectedTextureSize *= 4;
        } else if (type == TextureAtlas.TYPE_GREY) {
            expectedTextureSize *= 1;
        } else if (type == TextureAtlas.TYPE_GREY_ALPHA) {
            expectedTextureSize *= 2;
        }

        // store texture size
        writeBuffer.putInt(expectedTextureSize);

        int expectedTransparencySize = 0;
        if (tex.hasTransparencyMask()) {
            expectedTransparencySize = (width * height) / 8;
            if ((expectedTransparencySize * 8) < (width * height)) {
                expectedTransparencySize++;
            }
        }

        // Store transparency mask size
        writeBuffer.putInt(expectedTransparencySize);

        // storing the position where the texture writing started
        final int textureDefStart = writeBuffer.position();

        final Set<Entry<String, Texture>> textures = tex.getTextures();
        for (final Entry<String, Texture> texture : textures) {
            // getting filename as byte data
            final byte[] filename =
                texture.getKey().getBytes(FILENAME_CHARSET);

            // validating texture name
            if (filename.length == 0) {
                throw new IllegalStateException("Illegal name for a texture");
            }

            // put the name of the texture
            writeBuffer.putShort((short) filename.length);
            writeBuffer.put(filename);

            // get image size and location
            final int texX = texture.getValue().getImageX();
            final int texY = texture.getValue().getImageY();
            final int texW = texture.getValue().getImageWidth();
            final int texH = texture.getValue().getImageHeight();

            // validate size and location
            if (texX < 0) {
                throw new IllegalStateException(
                    "Illegal value for image x coordinate");
            }
            if (texY < 0) {
                throw new IllegalStateException(
                    "Illegal value for image y coordinate");
            }
            if (texW < 0) {
                throw new IllegalStateException(
                    "Illegal value for image width");
            }
            if (texH < 0) {
                throw new IllegalStateException(
                    "Illegal value for image height");
            }
            if ((texX + texW) > width) {
                throw new IllegalStateException(
                    "Illegal value for image position and width");
            }
            if ((texY + texH) > height) {
                throw new IllegalStateException(
                    "Illegal value for image position and height");
            }

            // writing image size and location
            writeBuffer.putShort((short) texX);
            writeBuffer.putShort((short) texY);
            writeBuffer.putShort((short) texW);
            writeBuffer.putShort((short) texH);
        }

        final int texDefSize = writeBuffer.position() - textureDefStart;
        final int oldPos = writeBuffer.position();
        writeBuffer.position(headerPos);
        writeBuffer.putInt(texDefSize);
        writeBuffer.position(oldPos);
        writeBuffer.flip();

        final ByteBuffer texData = tex.getTextureData();
        if (texData.remaining() != expectedTextureSize) {
            throw new IllegalStateException(String.format(
                "Texture data has the invalid size. %s$1 - %s$2",
                Integer.toString(texData.remaining()),
                Integer.toString(expectedTextureSize)));
        }

        ByteBuffer transparencyData = null;
        if (tex.hasTransparencyMask()) {
            transparencyData = tex.getTransparencyMask();
            if (transparencyData.remaining() != expectedTransparencySize) {
                throw new IllegalStateException(
                    String
                        .format(
                            "Texture transparency data has the invalid size. %s$1 - %s$2",
                            Integer.toString(transparencyData.remaining()),
                            Integer.toString(expectedTransparencySize)));
            }
        }

        while (writeBuffer.hasRemaining()) {
            outChannel.write(writeBuffer);
        }
        while (texData.hasRemaining()) {
            outChannel.write(texData);
        }

        if (transparencyData != null) {
            while (transparencyData.hasRemaining()) {
                outChannel.write(transparencyData);
            }
        }
    }
}
