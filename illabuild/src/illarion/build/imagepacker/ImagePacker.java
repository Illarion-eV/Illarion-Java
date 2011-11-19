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
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import illarion.build.TextureConverterNG;

import illarion.common.util.FastMath;

/**
 * A image packer that sorts the images by its types and places them in a good
 * way on a larger sprite in order to waste as less as possible video ram.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ImagePacker implements Comparator<TextureElement> {
    /**
     * This is a helper function that is used as task for loading the images
     * into the image packer.
     * 
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class AnalyseFileTask implements Runnable {
        /**
         * The file that will be analyzed once this task is executed.
         */
        private final TextureConverterNG.FileEntry file;

        /**
         * The image packer that is the parent to this class.
         */
        private final ImagePacker parent;

        /**
         * Create a new instance of this class. The public constructor is needed
         * to allow the parent class to create instances of this class. Also
         * this constructor takes the data needed to execute this task properly.
         * 
         * @param par the image packer that is the parent to this class
         * @param entry the entry that is analyzed when this task is running
         */
        public AnalyseFileTask(final ImagePacker par,
            final TextureConverterNG.FileEntry entry) {
            file = entry;
            parent = par;
        }

        @Override
        public void run() {
            parent.processAddImage(file);
        }

    }

    /**
     * The color models that are used for the different kinds of textures.
     */
    private static final ComponentColorModel[] colorModels;

    /**
     * Counter for the dumbed images.
     */
    private static int imageCountDumb = 0;

    /**
     * Maximal texture size in both directions in pixel.
     */
    private static final int MAX_SIZE = 1024;

    /**
     * The format for the first line of the informations printed for each
     * prepared texture atlas.
     */
    @SuppressWarnings("nls")
    private static final String TEXTURE_INFO_LINE1_FORMAT =
        "Texture Map #%1$s Type %2$s done";

    /**
     * The format for the second line of the informations printed for each
     * prepared texture atlas.
     */
    @SuppressWarnings("nls")
    private static final String TEXTURE_INFO_LINE2_FORMAT =
        "Remaining Images: %1$s";

    /**
     * The format for the third line of the informations printed for each
     * prepared texture atlas.
     */
    @SuppressWarnings("nls")
    private static final String TEXTURE_INFO_LINE3_FORMAT =
        "Wasted %1$s of %2$s Pixels. (%3$s%% - %4$s Bytes)";

     static final int TYPE_RGB = 0;
     static final int TYPE_RGBA = 1;
     static final int TYPE_GREY = 2;
    static final int TYPE_GREY_ALPHA = 3;

    static {
        colorModels = new ComponentColorModel[4];
        colorModels[TYPE_RGBA] =
            new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
                    8, 8 }, true, false, Transparency.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);
        colorModels[TYPE_RGB] =
            new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
                    8 }, false, false, Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        colorModels[TYPE_GREY] =
            new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[] { 8 },
                false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        colorModels[TYPE_GREY_ALPHA] =
            new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[] { 8, 8 }, true, false, Transparency.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);
    }

    /**
     * The executor service that runs the reading operation of the sprites
     * concurrent.
     */
    private ExecutorService execService;

    /**
     * This variable is used to synchronize the access to the analysis state
     * variables in order to ensure the data integrity when processing the image
     * files concurrent.
     */
    private final Object IMAGE_DATA_LOCK = new Object();

    /**
     * The counter that is used to monitor the amount of images in the storage.
     */
    private int imageCount = 0;

    /**
     * A list of the minimal height of all images of each of the four image
     * groups. In case spaces are created with a lower height, they are not
     * needed to be stored.
     */
    private final int[] imageMinHeight = new int[] { MAX_SIZE, MAX_SIZE,
        MAX_SIZE, MAX_SIZE };

    /**
     * A list of the minimal width of all images of each of the four image
     * groups. In case spaces are created with a lower width, they are not
     * needed to be stored.
     */
    private final int[] imageMinWidth = new int[] { MAX_SIZE, MAX_SIZE,
        MAX_SIZE, MAX_SIZE };

    /**
     * The list of sprite images that are loaded into the Image packer already.
     */
    private final List<Sprite>[] images;

    /**
     * The amount of pixels all used pictures together will use.
     */
    private final long[] pixelCount = new long[4];

    /**
     * The list of buffered rasters that were created already and are now
     * supposed to be reused.
     */
    private final ArrayList<WritableRaster> rasterBuffer =
        new ArrayList<WritableRaster>();

    /**
     * The flag if a list needs to be sorted or not.
     */
    private final boolean[] sortNeeded = new boolean[4];

    /**
     * The list of spaces that were used to fill up the height of the texture.
     */
    private final List<Space> spacesHeight = new ArrayList<Space>();

    /**
     * The list of spaces that were used to fill up the width of the texture.
     */
    private final List<Space> spacesWidth = new ArrayList<Space>();

    /**
     * The list of images that were used in this texture.
     */
    private final List<Sprite> usedImages = new LinkedList<Sprite>();

    /**
     * Constructor for a image packer. Sets up all needed lists to handle the
     * packing of the images
     */
    @SuppressWarnings("unchecked")
    public ImagePacker() {
        images = new List[4];
        images[TYPE_RGB] = new ArrayList<Sprite>();
        images[TYPE_RGBA] = new ArrayList<Sprite>();
        images[TYPE_GREY] = new ArrayList<Sprite>();
        images[TYPE_GREY_ALPHA] = new ArrayList<Sprite>();
    }

    /**
     * Get the color model for a specified texture type.
     * 
     * @param colorSpace the texture type
     * @return the needed color model
     */
    public static ComponentColorModel getColorModel(final int colorSpace) {
        if ((colorSpace < 0) || (colorSpace >= colorModels.length)) {
            return null;
        }
        return colorModels[colorSpace];
    }

    /**
     * Transfer the pixels of one image to the larger image map one by one. This
     * transfers the pixels directly. Even transparent pixels would overwrite
     * everything below them.
     * 
     * @param sourceImage the byte data of the source image
     * @param sourceWidth the width of the source image
     * @param sourceHeight the height of the source image
     * @param sourceType the type of the source image, based on that type the
     *            bits per pixel are set up
     * @param targetImage the byte data of the target image
     * @param targetX the x location of the source image on the target image
     * @param targetY the y location of the source image on the target image
     * @param targetWidth the width of the target image
     * @param targetHeight the height of the target image
     * @param targetType the type of the target image, based on that type the
     *            bits per pixel are set up
     */
    @SuppressWarnings("nls")
    private static void transferPixel(final ByteBuffer sourceImage,
        final int sourceWidth, final int sourceHeight, final int sourceType,
        final byte[] targetImage, final int targetX, final int targetY,
        final int targetWidth, final int targetHeight, final int targetType) {
        
        if ((targetX + sourceWidth) > targetWidth) {
            throw new IllegalArgumentException(
                "Image outside of legal range (width).");
        }
        if ((targetY + sourceHeight) > targetHeight) {
            throw new IllegalArgumentException(
                "Image outside of legal range (height).");
        }

        int targetBitsPerPixel = 4;
        switch (targetType) {
            case TYPE_RGBA:
                targetBitsPerPixel = 4;
                break;
            case TYPE_RGB:
                targetBitsPerPixel = 3;
                break;
            case TYPE_GREY_ALPHA:
                targetBitsPerPixel = 2;
                break;
            case TYPE_GREY:
                targetBitsPerPixel = 1;
                break;
        }

        int sourceBitsPerPixel = 4;
        switch (sourceType) {
            case TYPE_RGBA:
                sourceBitsPerPixel = 4;
                break;
            case TYPE_RGB:
                sourceBitsPerPixel = 3;
                break;
            case TYPE_GREY_ALPHA:
                sourceBitsPerPixel = 2;
                break;
            case TYPE_GREY:
                sourceBitsPerPixel = 1;
                break;
        }

        if ((sourceBitsPerPixel > targetBitsPerPixel)
            || ((sourceType == TYPE_RGB) && (targetType == TYPE_GREY_ALPHA))) {
            throw new IllegalArgumentException("Incompatible image types");
        }

        for (int x = 0; x < sourceWidth; ++x) {
            for (int y = 0; y < sourceHeight; ++y) {
                final int locTarget =
                    ((x + targetX) * targetBitsPerPixel)
                        + ((y + targetY) * targetWidth * targetBitsPerPixel);
                final int locSource =
                    (x * sourceBitsPerPixel)
                        + (y * sourceWidth * sourceBitsPerPixel);

                if (targetType == TYPE_RGBA) {
                    if (sourceType == TYPE_RGBA) {
                        sourceImage.position(locSource);
                        sourceImage.get(targetImage, locTarget, 4);
                    } else if (sourceType == TYPE_RGB) {
                        sourceImage.position(locSource);
                        sourceImage.get(targetImage, locTarget, 3);
                        targetImage[locTarget + 3] = -1;
                    } else if (sourceType == TYPE_GREY_ALPHA) {
                        Arrays.fill(targetImage, locTarget, locTarget + 3, sourceImage.get(locSource));
                        targetImage[locTarget + 3] = sourceImage.get(locSource + 1);
                    } 
                    else if (sourceType == TYPE_GREY) {
                        Arrays.fill(targetImage, locTarget, locTarget + 3, sourceImage.get(locSource));
                        targetImage[locTarget + 3] = -1;
                    }
                } else if (targetType == TYPE_RGB) {
                    if (sourceType == TYPE_RGB) {
                        sourceImage.position(locSource);
                        sourceImage.get(targetImage, locTarget, 3);
                    }
                    else if (sourceType == TYPE_GREY) {
                        Arrays.fill(targetImage, locTarget, locTarget + 3, sourceImage.get(locSource));
                    }
                } else if (targetType == TYPE_GREY_ALPHA) {
                    if (sourceType == TYPE_GREY_ALPHA) {
                        sourceImage.position(locSource);
                        sourceImage.get(targetImage, locTarget, 2);
                    } 
                    else if (sourceType == TYPE_GREY) {
                        targetImage[locTarget] = sourceImage.get(locSource);
                        targetImage[locTarget + 1] = -1;
                    }
                }
                else if (targetType == TYPE_GREY) {
                    if (sourceType == TYPE_GREY) {
                        targetImage[locTarget] = sourceImage.get(locSource);
                    }
                }
            }
        }
    }

    /**
     * Convert a type ID to a human readable string.
     * 
     * @param type the type ID
     * @return the readable string
     */
    @SuppressWarnings("nls")
    private static String typeToName(final int type) {
        switch (type) {
            case TYPE_GREY:
                return "Gray";
            case TYPE_GREY_ALPHA:
                return "Gray+Alpha";
            case TYPE_RGB:
                return "RGB";
            case TYPE_RGBA:
                return "RGBA";
            default:
                return "unknown";
        }
    }

    /**
     * Add a image to the image packer. This function will not analyse the image
     * right away, it will rather shedule it for analysing it in future.
     * 
     * @param fileEntry the entry that defines the location of the source file
     */
    public void addImage(final TextureConverterNG.FileEntry fileEntry) {
        getExecService().execute(new AnalyseFileTask(this, fileEntry));
    }

    /**
     * Check if there are still images left that need to get packed, or if
     * everything is done.
     * 
     * @return true if we are all done, false if not
     */
    public boolean allDone() {
        if ((images[TYPE_RGBA] != null) && !images[TYPE_RGBA].isEmpty()) {
            return false;
        }
        if ((images[TYPE_RGB] != null) && !images[TYPE_RGB].isEmpty()) {
            return false;
        }
        if ((images[TYPE_GREY_ALPHA] != null)
            && !images[TYPE_GREY_ALPHA].isEmpty()) {
            return false;
        }
        if ((images[TYPE_GREY] != null) && !images[TYPE_GREY].isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public int compare(final TextureElement o1, final TextureElement o2) {
        final int asize = o1.getHeight();
        final int bsize = o2.getHeight();
        return bsize - asize;
    }

    /**
     * Pack the images provided
     * 
     * @param coordList
     * @param texture
     * @return The generated sprite sheet
     * @throws IOException Indicates a failure to write out files
     */
    @SuppressWarnings("nls")
    public BufferedImage packImages(final Document targetDoc, final Node spriteDefTarget) throws IOException {
        System.out.println("Generating new Texture");
        if (execService != null) {
            execService.shutdown();
            try {
                execService.awaitTermination(2, TimeUnit.HOURS);
            } catch (final InterruptedException e) {
                // error while waiting!
                throw new BuildException(e);
            }
            execService = null;
        }
        if (imageCount > 0) {
            System.out.println(imageCount + " images read.");
            imageCount = 0;
        }

        int targetType = TYPE_RGBA;
        if ((images[targetType] == null) || (images[targetType].size() == 0)) {
            images[targetType] = null;
            targetType = TYPE_RGB;
        }
        if ((images[targetType] == null) || (images[targetType].size() == 0)) {
            images[targetType] = null;
            targetType = TYPE_GREY_ALPHA;
        }
        if ((images[targetType] == null) || (images[targetType].size() == 0)) {
            images[targetType] = null;
            targetType = TYPE_GREY;
        }
        if ((images[targetType] == null) || (images[targetType].size() == 0)) {
            images[targetType] = null;
            System.out.println("No more textures ... dropping out.");
            return null;
        }
        if (sortNeeded[targetType]) {
            Collections.sort(images[targetType], this);
            sortNeeded[targetType] = false;
        }

        int currType = targetType;

        System.out.println("Selected Texture Type: " + currType);

        final Sprite firstImage = images[currType].get(0);
        final int dimensions[] =
            getOptimalDimensions(firstImage.getWidth(),
                firstImage.getHeight(), targetType);
        final int width = dimensions[0];
        final int height = dimensions[1];

        spacesWidth.add(Space.getSpace(0, 0, height, width));

        ComponentColorModel glColorModel = null;
        int components = 0;
        if (targetType == TYPE_RGBA) {
            glColorModel = colorModels[TYPE_RGBA];
            components = 4;
        } else if (targetType == TYPE_RGB) {
            glColorModel = colorModels[TYPE_RGB];
            components = 3;
        } else if (targetType == TYPE_GREY) {
            glColorModel = colorModels[TYPE_GREY];
            components = 1;
        } else if (targetType == TYPE_GREY_ALPHA) {
            glColorModel = colorModels[TYPE_GREY_ALPHA];
            components = 2;
        }

        WritableRaster raster = getRaster(width, height, components);

        BufferedImage result =
            new BufferedImage(glColorModel, raster, false,
                new Hashtable<Object, Object>());

        final byte[] imageByteData =
            ((DataBufferByte) result.getRaster().getDataBuffer()).getData();

        Arrays.fill(imageByteData, (byte) 0);

        int minHeight = height;
        int minWidth = width;

        long wastedPixel = 0;

        while (true) {
            final List<Sprite> curImages = images[currType];

            int imageCnt = 0;

            if (curImages != null) {
                imageCnt = curImages.size();

                if (sortNeeded[currType]) {
                    Collections.sort(images[currType], this);
                    sortNeeded[currType] = false;
                }
            }
            for (int i = 0; i < imageCnt; i++) {
                if (curImages == null) {
                    break;
                }
                final Sprite currentImage = curImages.get(i);
                boolean image_used = false;
                int j = 0;
                while ((j < 2) && !image_used) {
                    List<Space> spaces;
                    if (j == 0) {
                        spaces = spacesWidth;
                    } else if (j == 1) {
                        spaces = spacesHeight;
                    } else {
                        System.err.println("ERROR selecting spaces!!!");
                        return null;
                    }
                    j++;
                    if (spaces.isEmpty()) {
                        continue;
                    }
                    for (int s = 0; s < spaces.size(); ++s) {
                        final Space currentSpace = spaces.get(s);
                        if (currentSpace.fitsInside(currentImage)) {
                            image_used = true;
                            currentImage.setPosition(currentSpace.getX(),
                                currentSpace.getY());
                            usedImages.add(currentImage);
                            pixelCount[currType] -=
                                currentImage.getPixelCount();
                            transferPixel(currentImage.getImage(),
                                currentImage.getWidth(),
                                currentImage.getHeight(), currType,
                                imageByteData, currentImage.getX(),
                                currentImage.getY(), width, height, targetType);

                            currentImage.releaseData();

                            if ((currentSpace.getWidth() - currentImage
                                .getWidth()) > 0) {
                                final int spaceX =
                                    currentSpace.getX()
                                        + currentImage.getWidth();
                                final int spaceY = currentSpace.getY();
                                final int spaceHeight =
                                    currentImage.getHeight();
                                final int spaceWidth =
                                    currentSpace.getWidth()
                                        - currentImage.getWidth();

                                spacesWidth.add(Space.getSpace(spaceX, spaceY,
                                    spaceHeight, spaceWidth));
                                reorderSpaces(spacesWidth);
                            }
                            if ((currentSpace.getHeight() - currentImage
                                .getHeight()) > 0) {
                                final int spaceX = currentSpace.getX();
                                final int spaceY =
                                    currentSpace.getY()
                                        + currentImage.getHeight();
                                final int spaceHeight =
                                    currentSpace.getHeight()
                                        - currentImage.getHeight();
                                final int spaceWidth = currentSpace.getWidth();

                                spacesHeight.add(Space.getSpace(spaceX,
                                    spaceY, spaceHeight, spaceWidth));
                                reorderSpaces(spacesHeight);
                            }
                            spaces.remove(currentSpace);
                            currentSpace.recycle();
                            break;
                        }
                    }
                }

                if (!image_used) {
                    minHeight = Math.min(currentImage.getHeight(), minHeight);
                    minWidth = Math.min(currentImage.getWidth(), minWidth);
                }
            }

            imageMinHeight[currType] = minHeight;
            imageMinWidth[currType] = minWidth;

            images[currType].removeAll(usedImages);

            for (final Sprite currentImg : usedImages) {
                final Element entryNode = targetDoc.createElement("sprite");
                
                entryNode.setAttribute("name", currentImg.getName());
                entryNode.setAttribute("x", Integer.toString(currentImg.getX()));
                entryNode.setAttribute("y", Integer.toString(currentImg.getY()));
                entryNode.setAttribute("height", Integer.toString(currentImg.getHeight()));
                entryNode.setAttribute("width", Integer.toString(currentImg.getWidth()));
                
                spriteDefTarget.appendChild(entryNode);
            }
            usedImages.clear();

            if (targetType == TYPE_RGBA) {
                if (currType == TYPE_RGBA) {
                    currType = TYPE_RGB;
                } else if (currType == TYPE_RGB) {
                    currType = TYPE_GREY_ALPHA;
                } else if (currType == TYPE_GREY_ALPHA) {
                    currType = TYPE_GREY;
                } else {
                    break;
                }
            } else if (targetType == TYPE_RGB) {
                if (currType == TYPE_RGB) {
                    currType = TYPE_GREY;
                } else {
                    break;
                }
            } else if (targetType == TYPE_GREY_ALPHA) {
                if (currType == TYPE_GREY_ALPHA) {
                    currType = TYPE_GREY;
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        for (final Space currSpace : spacesWidth) {
            wastedPixel += currSpace.getSize();
        }
        for (final Space currSpace : spacesHeight) {
            wastedPixel += currSpace.getSize();
        }

        result.flush();

        System.out.println(String.format(TEXTURE_INFO_LINE1_FORMAT,
            Integer.toString(imageCountDumb), typeToName(targetType)));
        System.out.println(String.format(TEXTURE_INFO_LINE2_FORMAT,
            Integer.toString(images[currType].size())));
        System.out.println(String.format(TEXTURE_INFO_LINE3_FORMAT,
            Long.toString(wastedPixel), Long.toString(width * height),
            Long.toString((wastedPixel * 100) / (width * height)),
            Long.toString(wastedPixel * components)));

        for (int i = 0; i < spacesWidth.size(); ++i) {
            spacesWidth.get(i).recycle();
        }
        for (int i = 0; i < spacesHeight.size(); ++i) {
            spacesHeight.get(i).recycle();
        }
        spacesWidth.clear();
        spacesHeight.clear();

        return result;
    }

    /**
     * Print out informations about the detected texture groupings.
     */
    @SuppressWarnings("nls")
    public void printTypeCounts() {
        if (execService != null) {
            execService.shutdown();
            try {
                execService.awaitTermination(2, TimeUnit.MINUTES);
            } catch (final InterruptedException e) {
                e.printStackTrace();
                throw new BuildException(e);
            }
            execService = null;
        }

        if (imageCount > 0) {
            System.out.println(Integer.toString(imageCount) + " images read.");
            imageCount = 0;
        }

        System.out.println("RGBA Textures: "
            + images[TYPE_RGBA].size());
        System.out.println("RGB Textures: "
            + images[TYPE_RGB].size());
        System.out.println("Gray Textures: "
            + images[TYPE_GREY].size());
        System.out.println("Gray+Alpha Textures: "
            + images[TYPE_GREY_ALPHA].size());
    }

    /**
     * This function actually creates the required sprite and puts it into the
     * list of files to process.
     * 
     * @param fileEntry the file entry to process
     */
    protected void processAddImage(final TextureConverterNG.FileEntry fileEntry) {
        try {
            final Sprite sprite = new Sprite(fileEntry);
            
            if (sprite.getPixelCount() == 0) {
                return;
            }

            if ((sprite.getWidth() > MAX_SIZE)
                || (sprite.getHeight() > MAX_SIZE)) {
                System.out.println("Image " + fileEntry.getFileName() //$NON-NLS-1$
                    + " ignored - too large"); //$NON-NLS-1$
                return;
            }

            int spriteType = sprite.getType();

            synchronized (IMAGE_DATA_LOCK) {
                pixelCount[spriteType] += sprite.getPixelCount();
                images[spriteType].add(sprite);
                sortNeeded[spriteType] = true;
                imageMinWidth[spriteType] =
                    Math.min(sprite.getWidth(), imageMinWidth[spriteType]);
                imageMinHeight[spriteType] =
                    Math.min(sprite.getHeight(), imageMinHeight[spriteType]);
                imageCount++;
                if ((imageCount % 1000) == 0) {
                    System.out.println(imageCount + " images read."); //$NON-NLS-1$
                }
            }
        } catch (final Exception e) {
            System.out.println("Failed reading image " //$NON-NLS-1$
                + fileEntry.getFileName());
            e.printStackTrace();
        }
    }

    /**
     * Get the executor service and create a new one in case there is none.
     * 
     * @return the executor service
     */
    private ExecutorService getExecService() {
        if (execService == null) {
            execService =
                Executors.newFixedThreadPool(Runtime.getRuntime()
                    .availableProcessors() * 2);
        }
        return execService;
    }

    /**
     * Search the optimal base 2 dimensions for the sprite and return them. The
     * dimension will keep within the limit set with {@link #MAX_SIZE}.
     * 
     * @param minWidth the minimal width value that is needed
     * @param minHeight the minimal height value that is needed
     * @param currType the current type that is handled, so the index of the per
     *            type arrays
     * @return integer array with 2 values, first is the width, second the
     *         height
     */
    private int[] getOptimalDimensions(final int minWidth,
        final int minHeight, final int currType) {
        final int quadSideLength = (int) FastMath.sqrt(pixelCount[currType]);

        if (quadSideLength > MAX_SIZE) {
            return new int[] { MAX_SIZE, MAX_SIZE };
        }

        int width = 1;
        while ((width < quadSideLength) || (width < minWidth)) {
            width <<= 1;
        }

        int height = 1;
        while ((height < minHeight)
            || ((width * height) < pixelCount[currType])) {
            height <<= 1;
        }

        return new int[] { width, height };
    }

    /**
     * Get a raster with the given specifications. Either create a new one or
     * get a buffered one.
     * 
     * @param width the width of the raster
     * @param height the height of the raster
     * @param components the samples per pixel of the raster
     * @return the raster, either a new one or a buffered one
     */
    private WritableRaster getRaster(final int width, final int height,
        final int components) {
        if (rasterBuffer.size() > 0) {
            for (int i = 0; i < rasterBuffer.size(); ++i) {
                final WritableRaster testRaster = rasterBuffer.get(i);
                if ((testRaster.getHeight() == height)
                    && (testRaster.getWidth() == width)
                    && (testRaster.getNumBands() == components)) {
                    return testRaster;
                }
            }
        }

        final WritableRaster raster =
            Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width,
                height, components, null);
        rasterBuffer.add(raster);
        return raster;
    }

    /**
     * Resort the list of spaces after usage, so its always the smallest one
     * that is used in the next turn.
     * 
     * @param spaceList the list of spaces
     */
    private void reorderSpaces(final List<Space> spaceList) {
        Collections.sort(spaceList, this);
    }
}
