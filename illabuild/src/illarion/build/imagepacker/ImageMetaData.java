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

import illarion.graphics.TextureAtlas;

/**
 * This little helper class is used to store the meta data of a image. Those
 * meta data are the size of the image along with the bit depth.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ImageMetaData {
    /**
     * The amount of colors this image has. Excluding the alpha channel.
     */
    private final int colorCount;

    /**
     * The color depth of the image in bit.
     */
    private final int colorDepth;

    /**
     * The height of the image in pixels.
     */
    private final int height;

    /**
     * <code>true</code> in case this image has a transparency channel.
     */
    private final boolean transparency;

    /**
     * The width of the image in pixels.
     */
    private final int width;

    /**
     * Create a new instance of the image meta data and store the data of one
     * image in it. Once set this data can't change anymore.
     * 
     * @param imageWidth the width of the image in pixels
     * @param imageHeight the height of the image in pixels
     * @param imageColorCount the amount of colors of this image, excluding the
     *            transparency color
     * @param imageColorDepth the amount of bit that is used to store the value
     *            of each color
     * @param imageTransparency <code>true</code> in case this image contains
     *            transparency data
     */
    public ImageMetaData(final int imageWidth, final int imageHeight,
        final int imageColorCount, final int imageColorDepth,
        final boolean imageTransparency) {
        width = imageWidth;
        height = imageHeight;
        colorCount = imageColorCount;
        colorDepth = imageColorDepth;
        transparency = imageTransparency;
    }

    /**
     * The amount of bit needed to store one pixel of this image uncompressed.
     * 
     * @return the amount of bit used to store one pixel of this image
     */
    public int getBitPerPixel() {
        if (transparency) {
            return (colorCount + 1) * colorDepth;
        }
        return colorCount * colorDepth;
    }

    /**
     * The amount of bytes needed to store one pixel of this image.
     * 
     * @return the amount of byte required to store one pixel
     */
    public int getBytePerPixel() {
        return (getBitPerPixel() >> 3);
    }

    /**
     * Get the amount of colors stored in this image. This does not include the
     * transparency "color".
     * 
     * @return the amount of colors, usually its 1 or 3
     */
    public int getColorCount() {
        return colorCount;
    }

    /**
     * Get the color depth of this image.
     * 
     * @return the amount of bit each color of this image is stored in
     */
    public int getColorDepth() {
        return colorDepth;
    }

    /**
     * Get the height of this image in pixels.
     * 
     * @return the height of this image
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the amount of pixels this image contains.
     * 
     * @return the amount of pixels in this image
     */
    public int getPixelCount() {
        return width * height;
    }

    /**
     * The amount of bytes needed to store this full image uncompressed.
     * 
     * @return the amount of bytes needed to store this image
     */
    public int getSizeInByte() {
        return getBytePerPixel() * getPixelCount();
    }

    /**
     * Get the texture type that fits to this image.
     * 
     * @return the fitting texture type or -1 in case no texture type was found
     */
    public int getTextureType() {
        if (colorCount == 1) {
            if (transparency) {
                return TextureAtlas.TYPE_GREY_ALPHA;
            }
            return TextureAtlas.TYPE_GREY;
        }
        if (colorCount == 3) {
            if (transparency) {
                return TextureAtlas.TYPE_RGBA;
            }
            return TextureAtlas.TYPE_RGB;
        }
        return -1;
    }

    /**
     * Get the width of this image in pixels.
     * 
     * @return the width of this image
     */
    public int getWidth() {
        return width;
    }

    /**
     * Check if this image contains transparent areas.
     * 
     * @return <code>true</code> in case this image contains transparency data
     */
    public boolean hasTransparancy() {
        return transparency;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ImageMetaData - "); //$NON-NLS-1$
        builder.append("Width: ").append(width); //$NON-NLS-1$
        builder.append(", "); //$NON-NLS-1$
        builder.append("Height: ").append(height); //$NON-NLS-1$
        builder.append(", "); //$NON-NLS-1$
        builder.append("Colors: ").append(colorCount); //$NON-NLS-1$
        builder.append(", "); //$NON-NLS-1$
        builder.append("Transparency: ").append(transparency); //$NON-NLS-1$
        builder.append(", "); //$NON-NLS-1$
        builder.append("Bit per Pixel: ").append(getBitPerPixel()); //$NON-NLS-1$
        return builder.toString();
    }
}
