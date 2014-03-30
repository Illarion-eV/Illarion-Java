/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.build.imagepacker

import de.matthiasmann.twl.utils.PNGDecoder
import de.matthiasmann.twl.utils.PNGDecoder.Format
import org.gradle.api.logging.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
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

    @Nullable
    private InputStream decoderStream;

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
     * The used logging instance.
     */
    private final Logger logger;

    /**
     * Create a sprite based on a file
     *
     * @param fileEntry The file entry containing the sprite image
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Sprite(@Nonnull final File fileEntry, final Logger logger) throws IOException {
        this.logger = logger;
        file = fileEntry
        name = stripFileExtension(fileEntry.absolutePath)

        setupData()

        if (decoder == null) {
            throw new IOException("Failed to read the image")
        }

        height = decoder.height
        width = decoder.width

        type = detectImageType()
        releaseData()
    }

    private void setupData() throws IOException {
        if (decoder != null) {
            return
        }

        try {
            decoderStream = new BufferedInputStream(new FileInputStream(file))
            decoder = new PNGDecoder(decoderStream)
        } catch (ignored) {
            decoderStream?.close()
            decoderStream = null;
            decoder = null;
        }

        if (decoder == null) {
            BufferedImage image = ImageIO.read(file)
            def height = image.height
            def width = image.width

            BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
            tempImage.graphics.drawImage(image, 0, 0, null)

            def outStream = new ByteArrayOutputStream(1024)
            ImageIO.write(tempImage, "PNG", outStream)
            try {
                decoderStream = new ByteArrayInputStream(outStream.toByteArray())
                decoder = new PNGDecoder(decoderStream)
            } catch (IOException e) {
                throw e
            }
        }
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

        setupData()

        if (decoder == null) {
            throw new IOException("Failed to read the image")
        }

        final Format format = decoder.decideTextureFormat(Format.LUMINANCE)
        imageData = ByteBuffer.allocateDirect(format.numComponents * width * height)
        try {
            decoder.decode(imageData, width * format.numComponents, format)
        } catch (e) {
            logger.error("Decoding the image failed!", e)
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
            logger.warn("Detecting the image type while there is no decoder set will lead to illegal results.")
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
        decoderStream?.close()
        decoderStream = null
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
