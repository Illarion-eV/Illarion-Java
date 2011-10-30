package illarion.client.world;

import illarion.common.graphics.MapConstants;
import illarion.common.util.FastMath;

/**
 * This class is used to store and calculate the dimensions of the map. It
 * requires the size of the screen as information to offer the proper values.
 * This class requires updates upon changes of the screen size.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class MapDimensions {
    /**
     * This is the amount of rows and columns that are requested from the server
     * in addition to the tiles needed to fill the screen size. If this value is
     * chosen too high the result is that large items and light sources are
     * known to the client too late and just "pop" in.
     */
    private static final int ADD_MAP_RANGE = 4;

    /**
     * This is the additional map range that is attached to the bottom of the
     * clipping ranges.
     */
    private static final int ADD_MAP_RANGE_BOTTOM = 9;

    /**
     * The singleton instance of this class.
     */
    private static final MapDimensions INSTANCE = new MapDimensions();

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class.
     */
    public static MapDimensions getInstance() {
        return INSTANCE;
    }

    /**
     * The private constructor to ensure that no further instances are created.
     */
    private MapDimensions() {
        // nothing
    }

    private int onScreenWidth;
    private int onScreenHeight;
    private int offScreenWidth;
    private int offScreenHeight;
    private int stripesWidth;
    private int stripesHeight;
    private int clippingOffsetTop;
    private int clippingOffsetBottom;
    private int clippingOffsetLeft;
    private int clippingOffsetRight;

    /**
     * Report a new screen size to the application. This needs to be done upon
     * every update of the screen size. This function will cause all internal
     * values to be recalculated in case new values are reported.
     * 
     * @param width the width of the screen in pixels
     * @param height the height of the screen in pixels
     */
    public void reportScreenSize(final int width, final int height) {
        if (onScreenHeight == height && onScreenWidth == width) {
            return;
        }

        onScreenWidth = width;
        onScreenHeight = height;

        final int heightInTiles =
            FastMath.ceil((float) height / (float) MapConstants.TILE_H * 2.f);
        final int widthInTiles =
            FastMath.ceil((float) width / (float) MapConstants.TILE_W * 2.f);

        clippingOffsetTop =
            FastMath.ceil(heightInTiles / 2.f) + ADD_MAP_RANGE + 2;
        clippingOffsetBottom =
            -FastMath.ceil(heightInTiles / 2.f) - ADD_MAP_RANGE_BOTTOM - 2;
        clippingOffsetLeft =
            -FastMath.ceil(widthInTiles / 2.f) - ADD_MAP_RANGE - 2;
        clippingOffsetRight =
            FastMath.ceil(widthInTiles / 2.f) + ADD_MAP_RANGE + 2;

        stripesWidth = widthInTiles + ADD_MAP_RANGE + ADD_MAP_RANGE;
        stripesHeight = heightInTiles + ADD_MAP_RANGE + ADD_MAP_RANGE;

        offScreenWidth = stripesWidth * MapConstants.TILE_W / 2;
        offScreenHeight = stripesHeight * MapConstants.TILE_H / 2;
    }

    /**
     * Get the width of the visible screen in pixels.
     * 
     * @return the width of the visible screen
     */
    public int getOnScreenWidth() {
        return onScreenWidth;
    }

    /**
     * Get the height of the visible screen in pixels.
     * 
     * @return the height of the visible screen
     */
    public int getOnScreenHeight() {
        return onScreenHeight;
    }

    /**
     * The width in pixels of the screen that needs to be rendered. This is
     * usually slightly larger then the visible screen. It represents the size
     * of the map in pixels that is requested from the server.
     * 
     * @return the width of the off screen
     */
    public int getOffScreenWidth() {
        return offScreenWidth;
    }

    /**
     * The height in pixels of the screen that needs to be rendered. This is
     * usually slightly larger then the visible screen. It represents the size
     * of the map in pixels that is requested from the server.
     * 
     * @return the height of the off screen
     */
    public int getOffScreenHeight() {
        return offScreenHeight;
    }

    /**
     * The width of the map in stripes.
     * 
     * @return the width in stripes
     */
    public int getStripesWidth() {
        return stripesWidth;
    }

    /**
     * The height of the map in stripes.
     * 
     * @return the height in stripes
     */
    public int getStripesHeight() {
        return stripesHeight;
    }

    /**
     * The offset from the center of the screen towards the top in tile stripes.
     * Any tile beyond this offset is allowed to be clipped away.
     * 
     * @return the clipping distance from the center towards the top
     */
    public int getClippingOffsetTop() {
        return clippingOffsetTop;
    }

    /**
     * The offset from the center of the screen towards the bottom in tile
     * stripes. Any tile beyond this offset is allowed to be clipped away.
     * 
     * @return the clipping distance from the center towards the bottm
     */
    public int getClippingOffsetBottom() {
        return clippingOffsetBottom;
    }

    /**
     * The offset from the center of the screen towards the left in tile
     * stripes. Any tile beyond this offset is allowed to be clipped away.
     * 
     * @return the clipping distance from the center towards the left
     */
    public int getClippingOffsetLeft() {
        return clippingOffsetLeft;
    }

    /**
     * The offset from the center of the screen towards the right in tile
     * stripes. Any tile beyond this offset is allowed to be clipped away.
     * 
     * @return the clipping distance from the center towards the right
     */
    public int getClippingOffsetRight() {
        return clippingOffsetRight;
    }
}
