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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

import illarion.build.TextureConverterNG;

/**
 * A simple sprite holder that allows the tool to name images
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Sprite implements TextureElement {
    /**
     * The decoder that is used to read the image data.
     */
    private final PNGDecoder decoder;

    /**
     * The name of the sprite
     */
    private final String name;

    /**
     * The x position of the sprite
     */
    private int x;

    /**
     * The y position of the sprite
     */
    private int y;

    /**
     * Create a sprite based on a file
     * 
     * @param fileEntry The file entry containing the sprite image
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Sprite(final TextureConverterNG.FileEntry fileEntry)
        throws FileNotFoundException, IOException {
        decoder = new PNGDecoder(new FileInputStream(fileEntry.getFile()));
        name = stripFileExtension(fileEntry.getFileName());;
    }

    /**
     * This function is used to strip the file extension of a file name.
     * 
     * @param fileName the full file name
     * @return the file name without extension
     */
    private static String stripFileExtension(final String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
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
            return false;
        }
        if (yp < y) {
            return false;
        }
        if (xp >= (x + getWidth())) {
            return false;
        }
        if (yp >= (y + getHeight())) {
            return false;
        }

        return true;
    }

    /**
     * Get the height of this sprite within the sheet
     * 
     * @return The height of this sprite within the sheet
     */
    @Override
    public int getHeight() {
        return decoder.getHeight();
    }

    /**
     * This buffer stores the data of this image.
     */
    private ByteBuffer imageData;

    /**
     * Get the image of this sprite
     * 
     * @return The image of this sprite
     */
    public ByteBuffer getImage() {
        if (imageData != null) {
            return imageData;
        }
        int bits;
        Format format;
        if (decoder.isRGB()) {
            if (decoder.hasAlphaChannel()) {
                bits = 4;
                format = Format.RGBA;
            } else {
                bits = 3;
                format = Format.RGB;
            }
        } else {
            if (decoder.hasAlphaChannel()) {
                bits = 2;
                format = Format.LUMINANCE_ALPHA;
            } else {
                bits = 1;
                format = Format.LUMINANCE;
            }
        }
        imageData =
            ByteBuffer.allocateDirect(bits * decoder.getWidth()
                * decoder.getHeight());
        try {
            decoder.decode(imageData, decoder.getWidth() * bits, format);
        } catch (IOException e) {
            System.err.println("Failed reading the image data.");
        }
        return imageData;
    }

    /**
     * Get the name of this sprite
     * 
     * @return The name of this sprite
     */
    public String getName() {
        return name;
    }

    /**
     * Get the amount of pixels this image has.
     * 
     * @return the amount of pixels of this image
     */
    public long getPixelCount() {
        return getHeight() * getWidth();
    }

    /**
     * Get the type of the image.
     * 
     * @return the ID of the image type
     */
    public int getType() {
        if (decoder.isRGB()) {
            if (decoder.hasAlphaChannel()) {
                return ImagePacker.TYPE_RGBA;
            } else {
                return ImagePacker.TYPE_RGB;
            }
        } else {
            if (decoder.hasAlphaChannel()) {
                return ImagePacker.TYPE_GREY_ALPHA;
            } else {
                return ImagePacker.TYPE_GREY;
            }
        }
    }

    /**
     * Get the width of this sprite within the sheet
     * 
     * @return The width of this sprite within the sheet
     */
    @Override
    public int getWidth() {
        return decoder.getWidth();
    }

    /**
     * Get the x position of this sprite within the sheet
     * 
     * @return The x position of this sprite within the sheet
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Get the y position of this sprite within the sheet
     * 
     * @return The y position of this sprite within the sheet
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Clear all data stored in this sprite.
     */
    public void releaseData() {
        imageData = null;
    }

    /**
     * Set the position within the sheet of this sprite
     * 
     * @param posX The x position of the sprite
     * @param posY The y position of the sprite
     */
    public void setPosition(final int posX, final int posY) {
        x = posX;
        y = posY;
    }
}
