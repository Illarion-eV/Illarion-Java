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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;

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
     * The file entry of that sprite that is the data source.
     */
    private final TextureConverterNG.FileEntry entry;

    /**
     * The image for the sprite
     */
    private byte[] image;

    /**
     * The general informations about this sprite.
     */
    private final ImageMetaData metaData;

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
     */
    public Sprite(final TextureConverterNG.FileEntry fileEntry) {
        metaData =
            ImageMetaDecoder.getInstance().getImageData(fileEntry.getFile());
        name = stripFileExtension(fileEntry.getFileName());
        entry = fileEntry;
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
        if (xp >= (x + metaData.getWidth())) {
            return false;
        }
        if (yp >= (y + metaData.getHeight())) {
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
        return metaData.getHeight();
    }

    /**
     * Get the image of this sprite
     * 
     * @return The image of this sprite
     */
    public byte[] getImage() {
        try {
            loadImageData();
        } catch (final IOException e) {
            System.err.println("Reading image: " + entry.getFileName() //$NON-NLS-1$
                + " failed."); //$NON-NLS-1$
            e.printStackTrace(System.err);
        }
        return image;
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
        return metaData.getPixelCount();
    }

    /**
     * Get the type of the image.
     * 
     * @return the ID of the image type
     */
    public int getType() {
        return metaData.getTextureType();
    }

    /**
     * Get the width of this sprite within the sheet
     * 
     * @return The width of this sprite within the sheet
     */
    @Override
    public int getWidth() {
        return metaData.getWidth();
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
        image = null;
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

    /**
     * This little function is used to properly convert a short buffer array to
     * a byte buffer. The values are converted in order to remain in the same
     * range.
     * 
     * @param buffer the short buffer that is the source
     * @return the resulting byte buffer
     */
    private byte[] convertBufferArray(final short[] buffer) {
        final byte[] resultArray = new byte[buffer.length];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = (byte) (buffer[i] >> (Short.SIZE - Byte.SIZE));
        }
        return resultArray;
    }

    /**
     * Read the data of the image. The method will try to do so in a very speedy
     * way. In case it fails to do so, it will use a fail save method to read
     * the data.
     * 
     * @throws IOException in case reading the image fails
     */
    private void loadImageData() throws IOException {
        if (image != null) {
            return;
        }

        final ImageInputStream imageInStream =
            ImageIO.createImageInputStream(entry.getFile());
        final Iterator<ImageReader> readersItr =
            ImageIO.getImageReaders(imageInStream);

        if (!readersItr.hasNext()) {
            imageInStream.close();
            loadImageDataFailsave();
            return;
        }

        int bands = metaData.getColorCount();
        if (metaData.hasTransparancy()) {
            bands++;
        }
        final int[] bandOffsets = new int[bands];
        for (int i = 0; i < bands; i++) {
            bandOffsets[i] = i;
        }

        final ColorModel glColorModel =
            ImagePacker.getColorModel(metaData.getTextureType());

        int type = -1;
        if (metaData.getColorDepth() == 8) {
            type = DataBuffer.TYPE_BYTE;
        } else if (metaData.getColorDepth() == 16) {
            type = DataBuffer.TYPE_USHORT;
        } else {
            imageInStream.close();
            loadImageDataFailsave();
            return;
        }

        final ImageTypeSpecifier imageType =
            ImageTypeSpecifier.createInterleaved(glColorModel.getColorSpace(),
                bandOffsets, type, metaData.hasTransparancy(), false);

        final ImageReadParam imageParam = new ImageReadParam();
        imageParam.setDestinationType(imageType);

        Raster imageRaster = null;
        ImageReader currReader;
        ImageReader lastFit = null;
        try {
            while (readersItr.hasNext()) {
                currReader = readersItr.next();
                currReader.setInput(imageInStream);
                if (!currReader.canReadRaster()) {
                    lastFit = currReader;
                    continue;
                }

                imageRaster = currReader.readRaster(0, imageParam);
            }

            if (imageRaster == null) {
                if (lastFit != null) {
                    imageRaster = lastFit.read(0, imageParam).getData();
                }
            }
        } catch (final Exception e) {
            // nothing
        } finally {
            imageInStream.close();
        }

        if (imageRaster == null) {
            loadImageDataFailsave();
            return;
        }

        final DataBuffer dataBuff = imageRaster.getDataBuffer();
        switch (dataBuff.getDataType()) {
            case DataBuffer.TYPE_BYTE:
                // best case, we have a byte buffer -> use is right away
                image = ((DataBufferByte) dataBuff).getData();
                break;
            case DataBuffer.TYPE_SHORT:
                image =
                    convertBufferArray(((DataBufferShort) dataBuff).getData());
                break;
            case DataBuffer.TYPE_USHORT:
                image =
                    convertBufferArray(((DataBufferUShort) dataBuff).getData());
                break;
        }

        if (image == null) {
            loadImageDataFailsave();
            return;
        }
    }

    /**
     * Read the data of the image in a fail save way. While its ensured that
     * this method succeeds in case there is any way, it will be very slow.
     * 
     * @throws IOException in case anything goes wrong
     */
    private void loadImageDataFailsave() throws IOException {
        final ColorModel glColorModel =
            ImagePacker.getColorModel(metaData.getTextureType());
        int bands = metaData.getColorCount();
        if (metaData.hasTransparancy()) {
            bands++;
        }

        System.out.println("Failsave reading method kicked in for: " //$NON-NLS-1$
            + entry.getFileName());

        if (glColorModel == null) {
            return;
        }

        final BufferedImage bufImage = ImageIO.read(entry.getFile());

        final WritableRaster raster =
            Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                metaData.getWidth(), metaData.getHeight(), bands, null);

        final BufferedImage result =
            new BufferedImage(glColorModel, raster, false,
                new Hashtable<Object, Object>());

        image =
            ((DataBufferByte) result.getRaster().getDataBuffer()).getData();

        final Graphics g = result.getGraphics();

        g.drawImage(bufImage, 0, 0, null);
        g.dispose();
        result.flush();
    }
}
