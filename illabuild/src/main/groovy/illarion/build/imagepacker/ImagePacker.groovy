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

import groovy.xml.MarkupBuilder
import illarion.common.util.FastMath
import org.gradle.api.logging.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.awt.color.ColorSpace
import java.awt.image.*
import java.lang.ref.SoftReference
import java.nio.ByteBuffer
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import static java.awt.Transparency.OPAQUE
import static java.awt.Transparency.TRANSLUCENT
import static java.awt.image.DataBuffer.TYPE_BYTE

/**
 * A image packer that sorts the images by its types and places them in a good way on a larger sprite in order to
 * waste as less as possible video ram.
 *
 * @author Martin Karing &ltnitram@illarion.org&gt
 */
public final class ImagePacker implements Comparator<TextureElement> {
    /**
     * The color models that are used for the different kinds of textures.
     */
    @Nonnull
    private static final ComponentColorModel[] COLOR_MODES

    /**
     * Counter for the dumbed images.
     */
    private static int imageCountDumb = 0

    /**
     * Maximal texture size in both directions in pixel.
     */
    private static final int MAX_SIZE = 1024

    /**
     * The index for the RGB images in the different image format related arrays.
     */
    static final int TYPE_RGB = 0

    /**
     * The index for the RGB images with alpha in the different image format related arrays.
     */
    static final int TYPE_RGBA = 1

    /**
     * The index for the grey scale images in the different image format related arrays.
     */
    static final int TYPE_GREY = 2

    /**
     * The index for the grey scale images with alpha in the different image format related arrays.
     */
    static final int TYPE_GREY_ALPHA = 3

    /**
     * The executor service that runs the reading operation of the sprites concurrent.
     */
    private def ExecutorService execService

    /**
     * This variable is used to synchronize the access to the analysis state
     * variables in order to ensure the data integrity when processing the image
     * files concurrent.
     */
    private final Object imageDataLock = new Object()

    /**
     * The counter that is used to monitor the amount of images in the storage.
     */
    private int imageCount = 0

    /**
     * A list of the minimal height of all images of each of the four image
     * groups. In case spaces are created with a lower height, they are not
     * needed to be stored.
     */
    private final int[] imageMinHeight =  [MAX_SIZE, MAX_SIZE, MAX_SIZE, MAX_SIZE]

    /**
     * A list of the minimal width of all images of each of the four image
     * groups. In case spaces are created with a lower width, they are not
     * needed to be stored.
     */
    private final int[] imageMinWidth = [MAX_SIZE, MAX_SIZE, MAX_SIZE, MAX_SIZE]

    /**
     * The list of sprite images that are loaded into the Image packer already.
     */
    @Nonnull
    private final List<Sprite>[] images

    /**
     * The amount of pixels all used pictures together will use.
     */
    private final long[] pixelCount = new long[4]

    /**
     * The list of buffered rasters that were created already and are now
     * supposed to be reused.
     */
    private final List<java.lang.ref.Reference<WritableRaster>> rasterBuffer = []

    /**
     * The flag if a list needs to be sorted or not.
     */
    private final boolean[] sortNeeded = new boolean[4]

    /**
     * The list of spaces that were used to fill up the height of the texture.
     */
    private final List<Space> spacesHeight = new ArrayList<Space>()

    /**
     * The list of spaces that were used to fill up the width of the texture.
     */
    private final List<Space> spacesWidth = new ArrayList<Space>()

    /**
     * The list of images that were used in this texture.
     */
    private final List<Sprite> usedImages = new LinkedList<Sprite>()

    /**
     * The source directory
     */
    private final def File srcDir;

    /**
     * The used logging instance.
     */
    @Nonnull
    private final Logger logger;

    /**
     * Constructor for a image packer. Sets up all needed lists to handle the
     * packing of the images
     */
    @SuppressWarnings("unchecked")
    public ImagePacker(final File srcDir, @Nonnull final Logger logger) {
        this.logger = logger;
        images = new List[4]
        for (i in 0..<images.size()) {
            images[i] = new ArrayList<Sprite>()
        }

        this.srcDir = srcDir;
    }

    static {
        def csRgb = ColorSpace.getInstance(ColorSpace.CS_sRGB)
        def csGray = ColorSpace.getInstance(ColorSpace.CS_GRAY)
        COLOR_MODES = new ComponentColorModel[4]
        COLOR_MODES[TYPE_RGBA] = new ComponentColorModel(csRgb, true, false, TRANSLUCENT, TYPE_BYTE)
        COLOR_MODES[TYPE_RGB] = new ComponentColorModel(csRgb, false, false, OPAQUE, TYPE_BYTE)
        COLOR_MODES[TYPE_GREY] = new ComponentColorModel(csGray, false, false, OPAQUE, TYPE_BYTE)
        COLOR_MODES[TYPE_GREY_ALPHA] = new ComponentColorModel(csGray, true, false, TRANSLUCENT, TYPE_BYTE)
    }

    /**
     * Add a image to the image packer. This function will not analyse the image
     * right away, it will rather schedule it for analysing it in future.
     *
     * @param file the entry that defines the location of the source file
     */
    public void addImages(final Collection<File> files) {
        def defer = { c -> getExecService().submit(c as Callable)}
        files.each {file ->
            defer{processAddImage(file)}
        }
    }

    @Override
    public int compare(@Nonnull final TextureElement o1, @Nonnull final TextureElement o2) {
        return FastMath.sign(o2.height - o1.height)
    }

    /**
     * Check if there are still images left that need to get packed, or if
     * everything is done.
     *
     * @return true if we are all done, false if not
     */
    public boolean isEverythingDone() {
        !images.any { imgCollection -> !imgCollection?.empty }
    }

    /**
     * Pack the images provided
     *
     * @param targetDoc       the XML document reference that is supposed to store the definition where the images are
     *                        located
     * @param spriteDefTarget the node inside the document where the sprite definition are supposed to be stored
     * @return The generated sprite sheet
     * @throws IOException Indicates a failure to write out files
     */
    @Nullable
    @SuppressWarnings("nls")
    public BufferedImage packImages(@Nonnull final MarkupBuilder defBuilder) throws IOException {
        logger.info("Packing images")
        shutdownExecutionService()

        def targetType = -1
        for (type in [TYPE_RGBA, TYPE_RGB, TYPE_GREY_ALPHA, TYPE_GREY]) {
            if (images[type] != null && !images[type].empty) {
                targetType = type;
                break;
            }
        }

        if (targetType == -1) {
            logger.info("All images done.")
            return null;
        }

        def currType = targetType

        logger.info("Selected Texture Type ${typeToName(currType)} with ${images[currType].size()} remaining images.")

        final def dimensions = getOptimalDimensions(imageMinWidth[currType], imageMinHeight[currType], targetType)
        final def width = dimensions[0]
        final def height = dimensions[1]

        logger.info("Selected atlas dimensions are ${width}px width and ${height}px height for " +
                "${pixelCount[currType]} remaining pixels.")

        spacesWidth.clear()
        spacesHeight.clear()
        spacesWidth.add(new Space(0, 0, height, width))

        final def glColorModel = COLOR_MODES[targetType]
        final def raster = getRaster(width, height, glColorModel.numComponents)
        final def result = new BufferedImage(glColorModel, raster, false, null)

        final def imageByteData = ((DataBufferByte) result.raster.dataBuffer).data

        Arrays.fill(imageByteData, (byte) 0)

        def minHeight = height
        def minWidth = width

        while (true) {
            final def curImages = images[currType]

            logger.info("Processing images of type ${typeToName(currType)} with ${images[currType].size()} remaining images.")

            def imageCnt = 0

            if (curImages != null) {
                imageCnt = curImages.size()

                if (sortNeeded[currType]) {
                    Collections.sort(images[currType], this)
                    sortNeeded[currType] = false
                }
            }
            for (i in 0..<imageCnt) {
                if (curImages == null) {
                    break
                }
                final def currentImage = curImages.get(i)
                def imageUnused = true
                for (spaces in [spacesWidth, spacesHeight]) {
                    if (spaces.empty) {
                        continue
                    }

                    for (s in 0..<spaces.size()) {
                        final def currentSpace = spaces.get(s)
                        if (!currentSpace.isFittingInside(currentImage)) {
                            continue
                        }

                        imageUnused = false
                        currentImage.setPosition(currentSpace.x, currentSpace.y)
                        usedImages.add(currentImage)
                        pixelCount[currType] -= currentImage.pixelCount
                        transferPixel(currentImage.image, currentImage.width, currentImage.height,
                                currType, imageByteData,
                                currentImage.x, currentImage.y, width, height, targetType)

                        currentImage.releaseData()

                        if ((currentSpace.width - currentImage.width) > 0) {
                            final def spaceX = currentSpace.x + currentImage.width
                            final def spaceY = currentSpace.y
                            final def spaceHeight = currentImage.height
                            final def spaceWidth = currentSpace.width - currentImage.width

                            spacesWidth.add(new Space(spaceX, spaceY, spaceHeight, spaceWidth))
                            reorderSpaces(spacesWidth)
                        }
                        if ((currentSpace.height - currentImage.height) > 0) {
                            final int spaceX = currentSpace.x
                            final int spaceY = currentSpace.y + currentImage.height
                            final int spaceHeight = currentSpace.height - currentImage.height
                            final int spaceWidth = currentSpace.width

                            spacesHeight.add(new Space(spaceX, spaceY, spaceHeight, spaceWidth))
                            reorderSpaces(spacesHeight)
                        }
                        spaces.remove(currentSpace)
                        break
                    }
                    if (!imageUnused) {
                        break
                    }
                }

                if (imageUnused) {
                    minHeight = Math.min(currentImage.height, minHeight)
                    minWidth = Math.min(currentImage.width, minWidth)
                }
            }

            imageMinHeight[currType] = minHeight
            imageMinWidth[currType] = minWidth

            logger.info("Transferred ${usedImages.size()} of type ${typeToName(currType)} to the texture atlas.")

            images[currType].removeAll(usedImages)

            usedImages.each {image ->
                String imageName = image.name;

                if (imageName.startsWith(srcDir.absolutePath)) {
                    imageName = imageName.replace(srcDir.absolutePath, "")
                }
                imageName = imageName.replace('\\', '/')
                if (imageName.startsWith("/")) {
                    imageName = imageName.substring(1)
                }
                defBuilder.sprite(name: imageName,
                        x: image.x, y: image.y,
                        height: image.height, width: image.width) {}
            }
            usedImages.clear()

            if (![spacesWidth, spacesHeight].any {spaceList -> !spaceList.empty}) {
                break;
            }

            if (targetType == TYPE_RGBA) {
                if (currType == TYPE_RGBA) {
                    currType = TYPE_RGB
                } else if (currType == TYPE_RGB) {
                    currType = TYPE_GREY_ALPHA
                } else if (currType == TYPE_GREY_ALPHA) {
                    currType = TYPE_GREY
                } else {
                    break
                }
            } else if (targetType == TYPE_RGB) {
                if (currType == TYPE_RGB) {
                    currType = TYPE_GREY
                } else {
                    break
                }
            } else if (targetType == TYPE_GREY_ALPHA) {
                if (currType == TYPE_GREY_ALPHA) {
                    currType = TYPE_GREY
                } else {
                    break
                }
            } else {
                break
            }
        }

        result.flush()

        logger.info("Texture Map ${imageCountDumb++} of type ${typeToName(targetType)} done")

        spacesWidth.clear()
        spacesHeight.clear()

        return result
    }

    /**
     * Search the optimal base 2 dimensions for the sprite and return them. The
     * dimension will keep within the limit set with {@link #MAX_SIZE}.
     *
     * @param minWidth  the minimal width value that is needed
     * @param minHeight the minimal height value that is needed
     * @param currType  the current type that is handled, so the index of the per
     *                  type arrays
     * @return integer array with 2 values, first is the width, second the
     *         height
     */
    @Nonnull
    private int[] getOptimalDimensions(final int minWidth, final int minHeight, final int currType) {
        final int quadSideLength = (int) FastMath.sqrt(pixelCount[currType])

        if (quadSideLength > MAX_SIZE) {
            return [MAX_SIZE, MAX_SIZE]
        }

        int width = 1
        while ((width < quadSideLength) || (width < minWidth)) {
            width <<= 1
        }

        int height = 1
        while ((height < minHeight) || ((width * height) < pixelCount[currType])) {
            height <<= 1
        }

        return [width, height]
    }

    /**
     * Get a raster with the given specifications. Either create a new one or
     * get a buffered one.
     *
     * @param width      the width of the raster
     * @param height     the height of the raster
     * @param components the samples per pixel of the raster
     * @return the raster, either a new one or a buffered one
     */
    private WritableRaster getRaster(final int width, final int height, final int components) {
        if (!rasterBuffer.empty) {
            rasterBuffer.retainAll {it.get() == null}
            for (rasterRef in rasterBuffer) {
                def raster = rasterRef.get()
                if (raster != null) {
                    if ((raster.height == height) && (raster.width == width) && (raster.numBands == components)) {
                        return raster
                    }
                }
            }
        }

        def raster = Raster.createInterleavedRaster(TYPE_BYTE, width, height, components, null)
        rasterBuffer.add(new SoftReference(raster))
        logger.info("Created new raster of size ${width} x ${height} with ${components} components.")
        return raster
    }

    /**
     * Transfer the pixels of one image to the larger image map one by one. This
     * transfers the pixels directly. Even transparent pixels would overwrite
     * everything below them.
     *
     * @param sourceImage  the byte data of the source image
     * @param sourceWidth  the width of the source image
     * @param sourceHeight the height of the source image
     * @param sourceType   the type of the source image, based on that type the bits per pixel are set up
     * @param targetImage  the byte data of the target image
     * @param targetX      the x location of the source image on the target image
     * @param targetY      the y location of the source image on the target image
     * @param targetWidth  the width of the target image
     * @param targetHeight the height of the target image
     * @param targetType   the type of the target image, based on that type the bits per pixel are set up
     */
    @SuppressWarnings("nls")
    private void transferPixel(@Nonnull final ByteBuffer sourceImage,
                                      final int sourceWidth, final int sourceHeight, final int sourceType,
                                      @Nonnull final byte[] targetImage, final int targetX, final int targetY,
                                      final int targetWidth, final int targetHeight, final int targetType) {
        if ((targetX + sourceWidth) > targetWidth) {
            throw new IllegalArgumentException("Image outside of legal range (width).")
        }
        if ((targetY + sourceHeight) > targetHeight) {
            throw new IllegalArgumentException("Image outside of legal range (height).")
        }

        int targetBitsPerPixel
        switch (targetType) {
            case TYPE_RGBA:
                targetBitsPerPixel = 4
                break
            case TYPE_RGB:
                targetBitsPerPixel = 3
                break
            case TYPE_GREY_ALPHA:
                targetBitsPerPixel = 2
                break
            case TYPE_GREY:
                targetBitsPerPixel = 1
                break
            default:
                throw new IllegalArgumentException("Illegal target type: ${targetType}")
        }

        int sourceBitsPerPixel
        switch (sourceType) {
            case TYPE_RGBA:
                sourceBitsPerPixel = 4
                break
            case TYPE_RGB:
                sourceBitsPerPixel = 3
                break
            case TYPE_GREY_ALPHA:
                sourceBitsPerPixel = 2
                break
            case TYPE_GREY:
                sourceBitsPerPixel = 1
                break
            default:
                throw new IllegalArgumentException("Illegal source type: ${sourceType}")
        }

        if (sourceBitsPerPixel > targetBitsPerPixel) {
            throw new IllegalArgumentException("Incompatible image types")
        }

        for (x in 0..<sourceWidth) {
            for (y in 0..<sourceHeight) {
                final int locTarget = ((x + targetX) * targetBitsPerPixel) + ((y + targetY) * targetWidth *
                        targetBitsPerPixel)
                final int locSource = (x * sourceBitsPerPixel) + (y * sourceWidth * sourceBitsPerPixel)

                if (targetType == TYPE_RGBA) {
                    if (sourceType == TYPE_RGBA) {
                        sourceImage.position(locSource)
                        sourceImage.get(targetImage, locTarget, 4)
                    } else if (sourceType == TYPE_RGB) {
                        sourceImage.position(locSource)
                        sourceImage.get(targetImage, locTarget, 3)
                        targetImage[locTarget + 3] = -1
                    } else if (sourceType == TYPE_GREY_ALPHA) {
                        Arrays.fill(targetImage, locTarget, locTarget + 3, sourceImage.get(locSource))
                        targetImage[locTarget + 3] = sourceImage.get(locSource + 1)
                    } else if (sourceType == TYPE_GREY) {
                        Arrays.fill(targetImage, locTarget, locTarget + 3, sourceImage.get(locSource))
                        targetImage[locTarget + 3] = -1
                    } else {
                        logger.error("Illegal source type (${sourceType}) or target type (${targetType})")
                    }
                } else if (targetType == TYPE_RGB) {
                    if (sourceType == TYPE_RGB) {
                        sourceImage.position(locSource)
                        sourceImage.get(targetImage, locTarget, 3)
                    } else if (sourceType == TYPE_GREY) {
                        Arrays.fill(targetImage, locTarget, locTarget + 3, sourceImage.get(locSource))
                    } else {
                        logger.error("Illegal source type (${sourceType}) or target type (${targetType})")
                    }
                } else if (targetType == TYPE_GREY_ALPHA) {
                    if (sourceType == TYPE_GREY_ALPHA) {
                        sourceImage.position(locSource)
                        sourceImage.get(targetImage, locTarget, 2)
                    } else if (sourceType == TYPE_GREY) {
                        targetImage[locTarget] = sourceImage.get(locSource)
                        targetImage[locTarget + 1] = -1
                    } else {
                        logger.error("Illegal source type (${sourceType}) or target type (${targetType})")
                    }
                } else {
                    if (sourceType == TYPE_GREY) {
                        targetImage[locTarget] = sourceImage.get(locSource)
                    } else {
                        logger.error("Illegal source type (${sourceType}) or target type (${targetType})")
                    }
                }
            }
        }
    }

    /**
     * Resort the list of spaces after usage, so its always the smallest one
     * that is used in the next turn.
     *
     * @param spaceList the list of spaces
     */
    private void reorderSpaces(@Nonnull final List<Space> spaceList) {
        Collections.sort(spaceList, this)
    }

    /**
     * Convert a type ID to a human readable string.
     *
     * @param type the type ID
     * @return the readable string
     */
    @Nonnull
    @SuppressWarnings("nls")
    private static String typeToName(final int type) {
        switch (type) {
            case TYPE_GREY:
                return "Gray"
            case TYPE_GREY_ALPHA:
                return "Gray+Alpha"
            case TYPE_RGB:
                return "RGB"
            case TYPE_RGBA:
                return "RGBA"
            default:
                return "unknown"
        }
    }

    private void shutdownExecutionService() {
        if (execService != null) {
            execService.shutdown()
            while (true) {
                try {
                    if (execService.awaitTermination(2, TimeUnit.HOURS)) {
                        break;
                    }
                } catch (ignored) {}
            }
            if (imageCount > 0) {
                logger.info("${imageCount} images loaded.")
                imageCount = 0
            }
        }
    }

    /**
     * Print out information about the detected texture groupings.
     */
    @SuppressWarnings("nls")
    public void printTypeCounts() {
        shutdownExecutionService()

        images.eachWithIndex{ entry, index -> logger.info("${typeToName(index)} textures: ${entry.size()}")};
    }

    /**
     * This function actually creates the required sprite and puts it into the
     * list of files to process.
     *
     * @param fileEntry the file entry to process
     */
    void processAddImage(@Nonnull final File fileEntry) {
        try {
            final Sprite sprite = new Sprite(fileEntry, logger)

            if (sprite.pixelCount == 0) {
                return
            }

            if ((sprite.width > MAX_SIZE) || (sprite.height > MAX_SIZE)) {
                logger.warn("Image ${fileEntry.name} is too large!")
                return
            }

            final int spriteType = sprite.type

            synchronized (imageDataLock) {
                pixelCount[spriteType] += sprite.pixelCount
                images[spriteType].add(sprite)
                sortNeeded[spriteType] = true
                imageMinWidth[spriteType] = Math.min(sprite.width, imageMinWidth[spriteType])
                imageMinHeight[spriteType] = Math.min(sprite.height, imageMinHeight[spriteType])
                imageCount++
                if ((imageCount % 1000) == 0) {
                    logger.info("${imageCount} images loaded.")
                }
            }
        } catch (ex) {
            logger.warn("Reading image ${fileEntry.name} failed: ${ex.message}")
        }
    }

    @Nonnull
    private def getExecService() {
        if (execService == null) {
            execService = Executors.newFixedThreadPool(Runtime.runtime.availableProcessors())
        }
        execService
    }
}
