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
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Build Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build.imagepacker;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import illarion.build.TextureConverterNG;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * A simple sprite holder that allows the tool to name images
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Sprite implements TextureElement {
    /**
     * The decoder that is used to read the image data.
     */
    private PNGDecoder decoder;

    /**
     * The name of the sprite
     */
    private final String name;

    /**
     * The x position of the sprite
     */
    private int x;

    /**
     * The y position of the sprite.
     */
    private int y;

    /**
     * The file this sprite was load from.
     */
    private final TextureConverterNG.FileEntry file;

    /**
     * Create a sprite based on a file
     *
     * @param fileEntry The file entry containing the sprite image
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Sprite(final TextureConverterNG.FileEntry fileEntry)
            throws FileNotFoundException, IOException {
        PNGDecoder tempDecoder = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileEntry.getFile());
            tempDecoder = new PNGDecoder(inputStream);
        } catch (IOException e) {
            System.err.println("Error for image: " + fileEntry.getFileName() + ": " + e.getLocalizedMessage());
        }

        if (tempDecoder != null) {
            height = tempDecoder.getHeight();
            width = tempDecoder.getWidth();
        } else {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            BufferedImage image = ImageIO.read(fileEntry.getFile());
            height = image.getHeight();
            width = image.getWidth();

            BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            tempImage.getGraphics().drawImage(image, 0, 0, null);
            ImageIO.write(tempImage, "PNG", fileEntry.getFile());
            try {
                inputStream = new FileInputStream(fileEntry.getFile());
                tempDecoder = new PNGDecoder(inputStream);
            } catch (IOException e) {
                System.err.println("Can't fix image: " + fileEntry.getFileName());
                throw e;
            }
        }
        decoder = tempDecoder;
        getType();
        name = stripFileExtension(fileEntry.getFileName());
        file = fileEntry;

        decoder = null;
        try {
            inputStream.close();
        } catch (IOException e) {
            // closing stream failed -> it does not matter
        }
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
     * The height of the sprite.
     */
    private final int height;

    /**
     * The width of the sprite.
     */
    private final int width;

    /**
     * Get the height of this sprite within the sheet
     *
     * @return The height of this sprite within the sheet
     */
    @Override
    public int getHeight() {
        return height;
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

        InputStream stream = null;
        try {
            stream = new FileInputStream(file.getFile());
            decoder = new PNGDecoder(stream);
        } catch (IOException e) {
        }

        if (decoder == null) {
            imageData = ByteBuffer.allocateDirect(getHeight() * getWidth() * 2);
            while (imageData.hasRemaining()) {
                imageData.put((byte) 0);
            }
            imageData.flip();
        } else {
            Format format = decoder.decideTextureFormat(Format.LUMINANCE);
            imageData =
                    ByteBuffer.allocateDirect(
                            format.getNumComponents() * getWidth()
                                    * getHeight());
            try {
                decoder.decode(imageData, getWidth() *
                        format.getNumComponents(), format);
            } catch (IOException e) {
                System.err.println("Failed reading the image data.");
            }

            decoder = null;
        }

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // ignored
            }
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
     * The type of the image that was generated.
     */
    private int imageType = -1;

    /**
     * Get the type of the image.
     *
     * @return the ID of the image type
     */
    public int getType() {
        if (imageType == -1) {
            imageType = getTypeImpl();
        }
        return imageType;
    }

    /**
     * Get the type of the image.
     *
     * @return the ID of the image type
     */
    private int getTypeImpl() {
        if (decoder == null) {
            return ImagePacker.TYPE_GREY_ALPHA;
        }
        Format format = decoder.decideTextureFormat(Format.LUMINANCE);
        switch (format) {
            case RGBA:
                return ImagePacker.TYPE_RGBA;
            case RGB:
                return ImagePacker.TYPE_RGB;
            case LUMINANCE_ALPHA:
                return ImagePacker.TYPE_GREY_ALPHA;
            case LUMINANCE:
                return ImagePacker.TYPE_GREY;
        }
        return ImagePacker.TYPE_RGBA;
    }

    /**
     * Get the width of this sprite within the sheet
     *
     * @return The width of this sprite within the sheet
     */
    @Override
    public int getWidth() {
        return width;
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
        decoder = null;
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
