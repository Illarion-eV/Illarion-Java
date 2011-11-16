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
package illarion.common.graphics;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.newdawn.slick.Color;

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
    private static final Color[] COLORS = new Color[] {
        new Color(0, 0, 0), // black
        new Color(182, 214, 158), // green
        new Color(155, 120, 90), // brown
        new Color(175, 183, 165), // gray
        new Color(126, 193, 238), // blue
        new Color(255, 255, 204), // yellow
        new Color(205, 101, 101), // red
        new Color(255, 255, 255), // white
        new Color(140, 160, 100) // dark green
    };

    /**
     * The error and debug logger of the client.
     */
    private static final Logger LOGGER = Logger.getLogger(MapColor.class);

    /**
     * Get the values of a color that is defined.
     * 
     * @param color the index of the color value
     * @return a array with the red, green and blue color value
     */
    public static Color getColor(final int color) {
        return COLORS[color];
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
        map.position(pos);
        map.put((byte) COLORS[color].getRedByte());
        map.put((byte) COLORS[color].getGreenByte());
        map.put((byte) COLORS[color].getBlueByte());
        map.put((byte) alpha);
    }

    /**
     * Private constructor so nothing can create a instance of this utility
     * class.
     */
    private MapColor() {
        // nothing is allowed to create a instance of this class
    }
}
