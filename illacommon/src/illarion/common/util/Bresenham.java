/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import java.awt.Point;

/**
 * Line calculation by Bresenham. This class is used to calculate a line between
 * 2 points in 2D space on a tiles map.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class Bresenham {
    /**
     * The maximum length of the line states how many points one line could
     * contain. If a longer line is calculated, the calculation is canceled.
     */
    protected static final int MAX_LINE_LENGTH = 100;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    // private static final Logger LOGGER = Logger.getLogger(Bresenham.class);

    /**
     * The singleton instance of this class.
     */
    private static final Bresenham INSTANCE = new Bresenham();

    /**
     * The length of the line that was created latest.
     */
    protected int length = 0;

    /**
     * The list of x-coordinates that were calculated due the last line
     * calculation.
     */
    protected final int[] x = new int[MAX_LINE_LENGTH];

    /**
     * The list of y-coordinates that were calculated due the last line
     * calculation.
     */
    protected final int[] y = new int[MAX_LINE_LENGTH];

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    public static Bresenham getInstance() {
        return INSTANCE;
    }

    /**
     * Reverse order of points if they do not start with given point.
     * 
     * @param sx x coordinate of the expected starting point
     * @param sy y coordinate of the expected starting point
     */
    public void adjustStart(final int sx, final int sy) {
        if ((x[0] != sx) || (y[0] != sy)) {
            int i = 0;
            int j = length - 1;
            int tmp;
            while (i < j) {
                tmp = x[i];
                x[i] = x[j];
                x[j] = tmp;

                tmp = y[i];
                y[i] = y[j];
                y[j] = tmp;

                ++i;
                --j;
            }
        }
    }

    /**
     * Calculate a line between 2 locations using the Bresenham algorithms. The
     * last line that was calculated is overwritten by calling this function. So
     * this line calculation is removed also instantly as the next calculation
     * is performed. So ensure to copy the data of this lines in oder to store
     * them and do not just save the references to the arrays.
     * 
     * @param x0 the x coordinate of the start location of the line
     * @param y0 the y coordinate of the start location of the line
     * @param x1 the x coordinate of the target location of the line
     * @param y1 the y coordinate of the target location of the line
     */
    public void calculate(final int x0, final int y0, final int x1,
        final int y1) {
        length = 0;

        int currX = x0;
        int currY = y0;
        int dy = y1 - y0;
        int dx = x1 - x0;
        int stepx, stepy;

        if (dy < 0) {
            dy = -dy;
            stepy = -1;
        } else {
            stepy = 1;
        }
        if (dx < 0) {
            dx = -dx;
            stepx = -1;
        } else {
            stepx = 1;
        }
        dy <<= 1; // dy is now 2*dy
        dx <<= 1; // dx is now 2*dx

        addPoint(x0, y0);
        if (dx > dy) {
            int fraction = dy - (dx >> 1); // same as 2*dy - dx
            while (currX != x1) {
                if (fraction >= 0) {
                    currY += stepy;
                    fraction -= dx; // same as fraction -= 2*dx
                }
                currX += stepx;
                fraction += dy; // same as fraction -= 2*dy
                addPoint(currX, currY);
            }
        } else {
            int fraction = dx - (dy >> 1);
            while (currY != y1) {
                if (fraction >= 0) {
                    currX += stepx;
                    fraction -= dy;
                }
                currY += stepy;
                fraction += dx;
                addPoint(currX, currY);
            }
        }
    }

    /**
     * Calculate a line between the two locations.
     * 
     * @param loc0 the start location of the calculation
     * @param loc1 the target location of the calculation
     * @see #calculate(int, int, int, int)
     */
    @SuppressWarnings("nls")
    public void calculate(final Location loc0, final Location loc1) {
        if (loc0 == null) {
            throw new IllegalArgumentException(
                "Start location (loc0) must not be null.");
        }
        if (loc1 == null) {
            throw new IllegalArgumentException(
                "Start location (loc1) must not be null.");
        }
        calculate(loc0.getScX(), loc0.getScY(), loc1.getScX(), loc1.getScY());
    }

    /**
     * Get the length of the line that was calculated at the last run.
     * 
     * @return the length of the line
     */
    public int getLength() {
        return length;
    }

    /**
     * Get a point out of the list of points that were calculated at the last
     * run of this function.
     * 
     * @param index the index of the point in the list of points
     * @param point the object the point data is stored in
     * @return true in case the index was valid and the point data got stored in
     *         the object, false if the index was smaller then 0 or larger then
     *         the length of the calculated line
     */
    @SuppressWarnings("nls")
    public boolean getPoint(final int index, final Point point) {
        if (point == null) {
            throw new IllegalArgumentException("Point must not be NULL");
        }
        if ((index < 0) || (index >= length)) {
            return false;
        }

        point.x = x[index];
        point.y = y[index];
        return true;
    }

    /**
     * Get the list of x coordinates that were calculated last time. This list
     * is only valid for the line until a new line is calculated. Its
     * overwritten then. Do also not perform any writing actions on this list
     * from outside of this class in order to prevent maleforming this list.
     * 
     * @return the list of x coordinates
     */
    public int[] getX() {
        return x;
    }

    /**
     * Get the list of y coordinates that were calculated last time. This list
     * is only valid for the line until a new line is calculated. Its
     * overwritten then. Do also not perform any writing actions on this list
     * from outside of this class in order to prevent maleforming this list.
     * 
     * @return the list of y coordinates
     */
    public int[] getY() {
        return y;
    }

    /**
     * Get the name of this class. This is used to identify the Bresenham
     * implementation human readable.
     */
    @Override
    @SuppressWarnings("nls")
    public String toString() {
        return "Bresenham Linetracer";
    }

    /**
     * Add a point to the list of points that were calculated. The length of the
     * line in automatically increased by one after calling this function.
     * 
     * @param px the x-coordinate of the point that shall be added
     * @param py the y-coordinate of the point that shall be added
     */
    @SuppressWarnings("nls")
    private void addPoint(final int px, final int py) {
        if (length > (MAX_LINE_LENGTH - 1)) {
            throw new IllegalStateException(
                "Bresenham line is getting too long.");
        }
        x[length] = px;
        y[length] = py;
        ++length;
    }
}
