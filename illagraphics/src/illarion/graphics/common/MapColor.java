/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.common;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * Provides the color table for the small and the overview map.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class MapColor {
    /**
     * The names of the colors for the display in the configuration tool. They
     * are not used in the client.
     */
    @SuppressWarnings("nls")
    public static final String[] COLOR_NAMES = new String[] { "Black",
        "Green", "Brown", "Gray", "Blue", "Yellow", "Red", "White",
        "Dark green" };

    /**
     * Amount of color value, excluding the alpha value, in a color.
     */
    public static final int COLOR_VALUES = 3;

    /**
     * The RGBA Color values that are used on the map.
     */
    private static final int[][] COLORS = new int[][] { { 0, 0, 0 }, // black
        { 182, 214, 158 }, // green
        { 155, 120, 90 }, // brown
        { 175, 183, 165 }, // gray
        { 126, 193, 238 }, // blue
        { 255, 255, 204 }, // yellow
        { 205, 101, 101 }, // red
        { 255, 255, 255 }, // white
        { 140, 160, 100 } // dark green
        };

    /**
     * The RGBA Color values in the openGL compatible format.
     */
    private static final SpriteColor[] GLCOLORS =
        new SpriteColor[COLORS.length];

    /**
     * The error and debug logger of the client.
     */
    private static final Logger LOGGER = Logger.getLogger(MapColor.class);

    static {
        for (int i = 0; i < COLORS.length; ++i) {
            GLCOLORS[i] = Graphics.getInstance().getSpriteColor();
            GLCOLORS[i].set(COLORS[i][0], COLORS[i][1], COLORS[i][2]);
            GLCOLORS[i].setAlpha(SpriteColor.COLOR_MAX);
        }
    }

    /**
     * Private constructor so nothing can create a instance of this utility
     * class.
     */
    private MapColor() {
        // nothing is allowed to create a instance of this class
    }

    /**
     * Get the values of a color that is defined.
     * 
     * @param color the index of the color value
     * @return a array with the red, green and blue color value
     */
    public static int[] getColor(final int color) {
        return COLORS[color];
    }

    /**
     * Get the sprite color that is defined under that index.
     * 
     * @param color the index of the requested color
     * @return the sprite color object of this color
     */
    public static SpriteColor getSpriteColor(final int color) {
        return GLCOLORS[color];
    }

    /**
     * Set the currently used openGL color to one of the colors defined in this
     * function.
     * 
     * @param color the index of the color value in the list of colors
     */
    @SuppressWarnings("nls")
    public static void setColor(final int color) {
        if (color > COLORS.length) {
            LOGGER.error("minimap color out of range - ignoring");
            final SpriteColor temp =
                Graphics.getInstance().getSprite(1).getDefaultLight();
            temp.setAlpha(SpriteColor.COLOR_MAX);
            temp.setActiveColor();
            return;
        }

        GLCOLORS[color].setActiveColor();
    }

    /**
     * Write color value to a byte buffer on the map.
     * 
     * @param color the index of the color value
     * @param alpha the intended alpha value of the pixel on the byte buffer
     * @param map the byte buffer that is going to get the new color value
     * @param pos the position in the byte buffer the color shall be located at
     */
    @SuppressWarnings("nls")
    public static void writeColor(final int color, final int alpha,
        final ByteBuffer map, final int pos) {
        // check color
        if (color > COLORS.length) {
            LOGGER.error("minimap color out of range - ignoring");
            return;
        }

        // draw pixel
        for (int i = 0; i < COLOR_VALUES; ++i) {
            map.put(pos + i, (byte) COLORS[color][i]);
        }
        // use calculated alpha values
        map.put(pos + COLOR_VALUES, (byte) alpha);
    }
}
