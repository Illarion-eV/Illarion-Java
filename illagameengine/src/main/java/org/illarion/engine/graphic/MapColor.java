/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package org.illarion.engine.graphic;

import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static final String[] COLOR_NAMES = {"Black", "Green", "Brown", "Gray", "Blue", "Yellow", "Red", "White",
                                                "Dark green",};

    /**
     * Amount of color value, excluding the alpha value, in a color.
     */
    public static final int COLOR_VALUES = 3;

    /**
     * The RGBA Color values that are used on the map.
     */
    private static final Color[] COLORS = {new ImmutableColor(0, 0, 0), // black
                                           new ImmutableColor(182, 214, 158), // green
                                           new ImmutableColor(155, 120, 90), // brown
                                           new ImmutableColor(175, 183, 165), // gray
                                           new ImmutableColor(126, 193, 238), // blue
                                           new ImmutableColor(255, 255, 204), // yellow
                                           new ImmutableColor(205, 101, 101), // red
                                           new ImmutableColor(255, 255, 255), // white
                                           new ImmutableColor(140, 160, 100), // dark green
    };

    /**
     * The error and debug logger of the client.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MapColor.class);

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
    @Nonnull
    @Contract(pure = true)
    public static Color getColor(int color) {
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
    public static void writeColor(
            int color, int alpha, @Nonnull ByteBuffer map, int pos) {
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
}
