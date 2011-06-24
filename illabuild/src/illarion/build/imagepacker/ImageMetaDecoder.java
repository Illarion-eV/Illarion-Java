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

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.imageio.ImageIO;

/**
 * This small class is used to decode the header of images very fast. It does
 * not read the entire image in case its not needed. How ever in case its left
 * without a different chance it will load the entire image to get the data.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ImageMetaDecoder {
    /**
     * This is the singleton instance of this class.
     */
    private static final ImageMetaDecoder INSTANCE = new ImageMetaDecoder();

    /**
     * This variable counts the times how often the legacy method was used to
     * read the image data.
     */
    private static int legacyCount = 0;

    /**
     * This is the header signature of a PNG file.
     */
    private static final ByteBuffer PNG_SIGNATURE = ByteBuffer.wrap(
        new byte[] { (byte) 137, 80, 78, 71, 13, 10, 26, 10 })
        .asReadOnlyBuffer();

    /**
     * Private constructor to ensure that no instances but the singleton
     * instance are created.
     */
    private ImageMetaDecoder() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static ImageMetaDecoder getInstance() {
        return INSTANCE;
    }

    /**
     * Get the amount of legacy calls that were needed to read all image meta
     * data.
     * 
     * @return the count of legacy calls
     */
    public static int getLegacyCount() {
        return legacyCount;
    }

    /**
     * Get the image meta data from a file.
     * 
     * @param inFile get the data from a file
     * @return the load image meta data
     */
    public ImageMetaData getImageData(final File inFile) {
        ImageMetaData metaData = null;
        int run = 0;
        do {
            InputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(inFile));
                run++;
                if (run == 1) {
                    metaData = getImageData(in);
                } else if (run == 2) {
                    metaData = getImageDataLegacy(in);
                } else {
                    break;
                }
            } catch (final IOException ex) {
                // nothing to do
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (final IOException e) {
                        // nothing
                    }
                }
            }
        } while (metaData == null);

        return metaData;
    }

    /**
     * Read the image data.
     * 
     * @param in the stream to read the image data from
     * @return the meta data of the image
     * @throws IOException in case anything goes wrong while reading the image
     */
    public ImageMetaData getImageData(final InputStream in) throws IOException {
        if (in.markSupported()) {
            in.mark(20);
        }

        final ImageMetaData metaData = getImageDataPNG(in);
        if (metaData != null) {
            return metaData;
        }

        if (!in.markSupported()) {
            throw new IOException("mark not supported."); //$NON-NLS-1$
        }
        in.reset();

        return getImageDataLegacy(in);
    }

    /**
     * This function loads the entire image and fetches the required data this
     * way. It shouldn't be used in case its absolutely needed.
     * 
     * @param in the input stream that is the source of the data
     * @return the meta data of the image that was read
     * @throws IOException in case anything goes wrong
     */
    private ImageMetaData getImageDataLegacy(final InputStream in)
        throws IOException {

        legacyCount++;

        final BufferedImage image = ImageIO.read(in);

        final boolean transparancy =
            image.getColorModel().getTransparency() != Transparency.OPAQUE;
        final boolean color =
            image.getColorModel().getColorSpace().getType() != ColorSpace.TYPE_GRAY;

        final int colorCount;
        if (color) {
            colorCount = 3;
        } else {
            colorCount = 1;
        }

        return new ImageMetaData(image.getWidth(), image.getHeight(),
            colorCount, 8, transparancy);
    }

    /**
     * Read the image data from a PNG style image.
     * 
     * @param in to stream to read the image from
     * @return The meta data of the image
     * @throws IOException in case the IOOperations fail
     */
    private ImageMetaData getImageDataPNG(final InputStream in)
        throws IOException {
        final ByteBuffer byteData = ByteBuffer.allocateDirect(14);

        final ReadableByteChannel inChannel = Channels.newChannel(in);

        byteData.limit(8);
        while (byteData.hasRemaining()) {
            inChannel.read(byteData);
        }

        byteData.flip();

        if (!byteData.equals(PNG_SIGNATURE)) {
            return null;
        }

        byteData.clear();

        while (byteData.hasRemaining()) {
            inChannel.read(byteData);
        }

        byteData.flip();

        while (byteData.remaining() >= 4) {
            if (byteData.getInt() == 0x49484452) {
                break;
            }
        }

        byteData.compact();

        byteData.limit(10);
        while (byteData.hasRemaining()) {
            inChannel.read(byteData);
        }

        byteData.flip();

        final int width = byteData.getInt();
        final int height = byteData.getInt();
        final int bitDepth = byteData.get();
        final int colorType = byteData.get();

        int colorCount = 0;
        boolean transparency = false;
        switch (colorType) {
            case 4:
                transparency = true;
                //$FALL-THROUGH$
            case 0:
                colorCount = 1;
                break;
            case 6:
                transparency = true;
                //$FALL-THROUGH$
            case 2:
                colorCount = 3;
                break;
        }

        if (colorCount == 0) {
            return null;
        }

        return new ImageMetaData(width, height, colorCount, bitDepth,
            transparency);
    }
}
