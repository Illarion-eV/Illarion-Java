/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.graphic;

import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * Provides the color table for the small and the overview map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MapColor {
    /**
     * The names of the colors for the display in the configuration tool. They
     * are not used in the client.
     */
    @SuppressWarnings("nls")
    public static final String[] COLOR_NAMES = {"Black",
            "Green", "Brown", "Gray", "Blue", "Yellow", "Red", "White",
            "Dark green"};

    /**
     * Amount of color value, excluding the alpha value, in a color.
     */
    public static final int COLOR_VALUES = 3;

    /**
     * The RGBA Color values that are used on the map.
     */
    private static final Color[] COLORS = {
            new ImmutableColor(0, 0, 0), // black
            new ImmutableColor(182, 214, 158), // green
            new ImmutableColor(155, 120, 90), // brown
            new ImmutableColor(175, 183, 165), // gray
            new ImmutableColor(126, 193, 238), // blue
            new ImmutableColor(255, 255, 204), // yellow
            new ImmutableColor(205, 101, 101), // red
            new ImmutableColor(255, 255, 255), // white
            new ImmutableColor(140, 160, 100) // dark green
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
     * @param map   the byte buffer that is going to get the new color value
     * @param pos   the position in the byte buffer the color shall be located at
     */
    @SuppressWarnings("nls")
    public static void writeColor(final int color, final int alpha,
                                  @Nonnull final ByteBuffer map, final int pos) {
        // check color
        if (color > COLORS.length) {
            LOGGER.error("minimap color out of range - ignoring");
            return;
        }

        // draw pixel
        map.position(pos);
        map.put((byte) COLORS[color].getRed());
        map.put((byte) COLORS[color].getGreen());
        map.put((byte) COLORS[color].getBlue());
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
