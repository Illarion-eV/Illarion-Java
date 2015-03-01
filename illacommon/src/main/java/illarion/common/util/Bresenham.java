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
package illarion.common.util;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Line calculation by Bresenham. This class is used to calculate a line between
 * 2 points in 2D space on a tiles map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class Bresenham {
    /**
     * The maximum length of the line states how many points one line could contain. If a longer line is calculated,
     * the calculation is canceled.
     */
    private static final int MAX_LINE_LENGTH = 100;

    /**
     * The length of the line that was created latest.
     */
    private int length;

    /**
     * The list of x-coordinates that were calculated due the last line calculation.
     */
    @Nonnull
    private final int[] x = new int[MAX_LINE_LENGTH];

    /**
     * The list of y-coordinates that were calculated due the last line calculation.
     */
    @Nonnull
    private final int[] y = new int[MAX_LINE_LENGTH];

    /**
     * Reverse order of points if they do not start with given point.
     *
     * @param sx x coordinate of the expected starting point
     * @param sy y coordinate of the expected starting point
     */
    public void adjustStart(int sx, int sy) {
        if ((x[0] != sx) || (y[0] != sy)) {
            int i = 0;
            int j = length - 1;
            while (i < j) {
                int tmp = x[i];
                x[i] = x[j];
                x[j] = tmp;

                int tmp2 = y[i];
                y[i] = y[j];
                y[j] = tmp2;

                ++i;
                --j;
            }
        }
    }

    /**
     * Calculate a line between 2 locations using the Bresenham algorithms. The last line that was calculated is
     * overwritten by calling this function. So this line calculation is removed also instantly as the next calculation
     * is performed. So ensure to copy the data of this lines in oder to store them and do not just save the
     * references to the arrays.
     *
     * @param x0 the x coordinate of the start location of the line
     * @param y0 the y coordinate of the start location of the line
     * @param x1 the x coordinate of the target location of the line
     * @param y1 the y coordinate of the target location of the line
     */
    public void calculate(int x0, int y0, int x1, int y1) {
        length = 0;

        int currX = x0;
        int currY = y0;
        int dy = y1 - y0;
        int dx = x1 - x0;
        int stepX, stepY;

        if (dy < 0) {
            dy = -dy;
            stepY = -1;
        } else {
            stepY = 1;
        }
        if (dx < 0) {
            dx = -dx;
            stepX = -1;
        } else {
            stepX = 1;
        }
        dy <<= 1; // dy is now 2*dy
        dx <<= 1; // dx is now 2*dx

        addPoint(x0, y0);
        if (dx > dy) {
            int fraction = dy - (dx >> 1); // same as 2*dy - dx
            while (currX != x1) {
                if (fraction >= 0) {
                    currY += stepY;
                    fraction -= dx; // same as fraction -= 2*dx
                }
                currX += stepX;
                fraction += dy; // same as fraction -= 2*dy
                addPoint(currX, currY);
            }
        } else {
            int fraction = dx - (dy >> 1);
            while (currY != y1) {
                if (fraction >= 0) {
                    currX += stepX;
                    fraction -= dy;
                }
                currY += stepY;
                fraction += dx;
                addPoint(currX, currY);
            }
        }
    }

    /**
     * Get the length of the line that was calculated at the last run.
     *
     * @return the length of the line
     */
    @Contract(pure = true)
    public int getLength() {
        return length;
    }

    /**
     * Get the list of x coordinates that were calculated last time.
     *
     * @return the list of x coordinates
     */
    @Nonnull
    @Contract(pure = true)
    public int[] getX() {
        return Arrays.copyOf(x, length);
    }

    /**
     * Get the list of y coordinates that were calculated last time.
     *
     * @return the list of y coordinates
     */
    @Nonnull
    @Contract(pure = true)
    public int[] getY() {
        return Arrays.copyOf(y, length);
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return "Bresenham Line Tracer";
    }

    /**
     * Add a point to the list of points that were calculated. The length of the line in automatically increased by
     * one after calling this function.
     *
     * @param px the x-coordinate of the point that shall be added
     * @param py the y-coordinate of the point that shall be added
     */
    private void addPoint(int px, int py) {
        if (length > (MAX_LINE_LENGTH - 1)) {
            throw new IllegalStateException("Bresenham line is getting too long.");
        }
        x[length] = px;
        y[length] = py;
        ++length;
    }
}
