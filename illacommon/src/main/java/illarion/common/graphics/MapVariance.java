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
package illarion.common.graphics;

import illarion.common.util.FastMath;

import java.util.Random;

/**
 * This is a helper class that is used to calculate the variances of tiles and
 * items on the map regarding the position.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MapVariance {
    /**
     * The random value generator used to generate the random values needed for
     * the variances of tiles and items.
     */
    private static final Random rnd = new Random();

    /**
     * Private constructor to avoid any instances being created.
     */
    private MapVariance() {
        // nothing
    }

    /**
     * Calculate the frame variance of a item regarding its location.
     *
     * @param locX the x coordinate of the item
     * @param locY the y coordinate of the item
     * @param frames the amount of frames of this item
     * @return the frame to be displayed
     */
    public static int getItemFrameVariance(
            int locX, int locY, int frames) {
        rnd.setSeed(((locX * 2876325137L) + (locY * 5979635807L)) * 1853493027);
        rnd.nextInt();
        rnd.nextInt();
        return rnd.nextInt(frames);
    }

    /**
     * Get the scale variance of a item regarding its location.
     *
     * @param locX the x coordinate of the item
     * @param locY the y coordinate of the item
     * @param variance the maximal variance of the item
     * @return the new variance to be used
     */
    public static float getItemScaleVariance(
            int locX, int locY, float variance) {
        rnd.setSeed(((locX * 1586337181L) + (locY * 6110869557L)) * 3251474107L);
        rnd.nextInt();
        rnd.nextInt();
        rnd.nextInt();
        rnd.nextInt();

        return (1.f - variance) + (2 * rnd.nextFloat() * variance);
    }

    /**
     * Get the frame variance of a tile regarding its location.
     *
     * @param locX the x coordinate of the location
     * @param locY the y coordinate of the location
     * @param frames the amount of frames of this tile
     * @return the frame to be displayed
     */
    public static int getTileFrameVariance(
            int locX, int locY, int frames) {
        if ((frames != 4) && (frames != 9) && (frames != 16) && (frames != 25)) {
            rnd.setSeed(((locX * 5133879561L) + (locY * 4154745775L)) * 1256671499);
            rnd.nextInt();
            rnd.nextInt();
            return rnd.nextInt(frames);
        }
        if (frames == 4) {
            return FastMath.abs((locX + 10000) % 2) + (FastMath.abs((locY + 10000) % 2) * 2);
        } else if (frames == 9) {
            return FastMath.abs((locX + 10000) % 3) + (FastMath.abs((locY + 10000) % 3) * 3);
        } else if (frames == 16) {
            return FastMath.abs((locX + 10000) % 4) + (FastMath.abs((locY + 10000) % 4) * 4);
        } else {
            return FastMath.abs((locX + 10000) % 5) + (FastMath.abs((locY + 10000) % 5) * 5);
        }
    }
}
