/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Build Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build.imagepacker

import de.matthiasmann.twl.utils.PNGDecoder
import de.matthiasmann.twl.utils.PNGDecoder.Format

import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.*
import java.nio.ByteBuffer

/**
 * A simple sprite holder that allows the tool to name images
 *
 * @author Martin Karing &ltnitram@illarion.org&gt
 */
final class Sprite extends TextureElement {
    /**
     * The decoder that is used to read the image data.
     */
    @Nullable
    private PNGDecoder decoder

    /**
     * The name of the sprite
     */
    final def String name

    /**
     * The file this sprite was load from.
     */
    @Nonnull
    private final File file

    /**
     * Create a sprite based on a file
     *
     * @param fileEntry The file entry containing the sprite image
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Sprite(@Nonnull final File fileEntry) throws IOException {
        def PNGDecoder decoder = null
        try {
            decoder = fileEntry.withInputStream {is -> new PNGDecoder(is)} as PNGDecoder
        } catch (ignored) {}

        if (decoder != null) {
            height = decoder.getHeight()
            width = decoder.getWidth()
        } else {
            BufferedImage image = ImageIO.read(fileEntry)
            height = image.getHeight()
            width = image.getWidth()

            BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
            tempImage.getGraphics().drawImage(image, 0, 0, null)

            def outStream = new ByteArrayOutputStream(1024)
            ImageIO.write(tempImage, "PNG", outStream)
            try {
                decoder = new PNGDecoder(new ByteArrayInputStream(outStream.toByteArray()))
            } catch (IOException e) {
                throw e
            }
        }
        this.decoder = decoder
        detectImageType()
        name = stripFileExtension(fileEntry.absolutePath)
        file = fileEntry
    }

    /**
     * This function is used to strip the file extension of a file name.
     *
     * @param fileName the full file name
     * @return the file name without extension
     */
    private static String stripFileExtension(@Nonnull final String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'))
    }

    /**
     * Check if this sprite location contains the given x,y position
     *
     * @param xp The x position of the sprite
     * @param yp The y position of the sprite
     * @return True if the sprite contains the point
     */
    public boolean contains(final int xp, final int yp) {
        if (xp < x) {
            return false
        }
        if (yp < y) {
            return false
        }
        if (xp >= (x + width)) {
            return false
        }
        if (yp >= (y + height)) {
            return false
        }

        return true
    }

    /**
     * This buffer stores the data of this image.
     */
    @Nullable
    private ByteBuffer imageData

    /**
     * Get the image of this sprite
     *
     * @return The image of this sprite
     */
    @Nullable
    public ByteBuffer getImage() {
        if (imageData != null) {
            return imageData
        }
        
        if (decoder == null) {
            try {
                file.withInputStream {is -> decoder = new PNGDecoder(is)}
            } catch (ignored) {}
        }

        if (decoder == null) {
            imageData = ByteBuffer.allocateDirect(height * width * 2)
            while (imageData.hasRemaining()) {
                imageData.put((byte) 0)
            }
            imageData.flip()
        } else {
            final Format format = decoder.decideTextureFormat(Format.LUMINANCE)
            imageData = ByteBuffer.allocateDirect(format.getNumComponents() * width * height)
            try {
                decoder.decode(imageData, width * format.getNumComponents(), format)
            } catch (ignored) {}

            decoder = null
        }

        return imageData
    }

    /**
     * Get the amount of pixels this image has.
     *
     * @return the amount of pixels of this image
     */
    public long getPixelCount() {
        return height * width
    }

    /**
     * The type of the image that was generated.
     */
    private def int type = -1

    /**
     * Get the type of the image.
     *
     * @return the ID of the image type
     */
    public int getType() {
        if (type == -1) {
            type = detectImageType()
        }
        return type
    }

    /**
     * Get the type of the image.
     *
     * @return the ID of the image type
     */
    private int detectImageType() {
        if (decoder == null) {
            return ImagePacker.TYPE_GREY_ALPHA
        }
        final Format format = decoder.decideTextureFormat(Format.LUMINANCE)
        switch (format) {
            case Format.RGBA:
                return ImagePacker.TYPE_RGBA
            case Format.RGB:
                return ImagePacker.TYPE_RGB
            case Format.LUMINANCE_ALPHA:
                return ImagePacker.TYPE_GREY_ALPHA
            case Format.LUMINANCE:
                return ImagePacker.TYPE_GREY
        }
        return ImagePacker.TYPE_RGBA
    }

    /**
     * Clear all data stored in this sprite.
     */
    public void releaseData() {
        imageData = null
        decoder = null
    }

    /**
     * Set the position within the sheet of this sprite
     *
     * @param posX The x position of the sprite
     * @param posY The y position of the sprite
     */
    public void setPosition(final int posX, final int posY) {
        x = posX
        y = posY
    }
}
