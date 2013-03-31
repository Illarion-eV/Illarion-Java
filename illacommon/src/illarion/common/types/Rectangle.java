/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
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
package illarion.common.types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;

/**
 * The rectangle class is a helper class that allows to define rectangles and check them for intersection.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public final class Rectangle implements Serializable {
    /**
     * Serialization UID of this rectangle class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The first x coordinate of this rectangle. This is the left border of the rectangle.
     */
    private int x0;
    /**
     * The second x coordinate of this rectangle. This is the right border of the rectangle.
     */
    private int x1;

    /**
     * The first y coordinate of this rectangle. This is the bottom border of the rectangle.
     */
    private int y0;

    /**
     * The second y coordinate of this rectangle. This is the top border of the rectangle.
     */
    private int y1;

    /**
     * The private constructor to ensure that new instances are only created by the get method.
     */
    public Rectangle() {
        reset();
    }

    public Rectangle(final int x, final int y, final int width, final int height) {
        set(x, y, width, height);
    }

    public Rectangle(@Nonnull final Rectangle other) {
        x0 = other.x0;
        x1 = other.x1;
        y0 = other.y0;
        y1 = other.y1;
    }

    /**
     * Get a instance of the rectangle class. Either a new one or one from the buffer.
     *
     * @return the rectangle method that is free for use.
     */
    @Nonnull
    @Deprecated
    public static Rectangle getInstance() {
        return new Rectangle();
    }

    /**
     * Add another rectangle to this one. This basically creates a union of both rectangles.
     *
     * @param other the rectangle that shall be added to the current instance
     */
    public void add(@Nonnull final Rectangle other) {
        if (isEmpty()) {
            set(other);
            return;
        }
        if (other.isEmpty()) {
            return;
        }
        x0 = Math.min(x0, other.x0);
        y0 = Math.min(y0, other.y0);
        x1 = Math.max(x1, other.x1);
        y1 = Math.max(y1, other.y1);
    }

    /**
     * Test this rectangle and another object for being equal.
     *
     * @param o the object this Rectangle shall be compared with
     * @return {@code true} in case this rectangle and the other one descripe the same rectangle
     */
    @Override
    public boolean equals(@Nullable final Object o) {
        if (super.equals(o)) {
            return true;
        }
        if (o instanceof Rectangle) {
            final Rectangle oRect = (Rectangle) o;
            return (oRect.x0 == x0) && (oRect.x1 == x1) && (oRect.y0 == y0) && (oRect.y1 == y1);
        }
        return false;
    }

    /**
     * Get the y coordinate (bottom border) of the rectangle.
     *
     * @return the coordinate of the bottom border
     */
    public int getBottom() {
        return y0;
    }

    /**
     * Get the center x coordinate of the rectangle.
     *
     * @return the center x coordinate of the rectangle
     */
    public int getCenterX() {
        return (x0 + x1) / 2;
    }

    /**
     * Get the center y coordinate of the rectangle.
     *
     * @return the center y coordinate of the rectangle
     */
    public int getCenterY() {
        return (y0 + y1) / 2;
    }

    /**
     * Get the height of the rectangle.
     *
     * @return the height of the rectangle
     */
    public int getHeight() {
        return y1 - y0;
    }

    /**
     * Get the x coordinate (left border) of the rectangle.
     *
     * @return the coordinate of the left border
     */
    public int getLeft() {
        return x0;
    }

    /**
     * Get the x coordinate (right border) of the rectangle.
     *
     * @return the coordinate of the right border
     */
    public int getRight() {
        return x1;
    }

    /**
     * Get the y coordinate (top border) of the rectangle.
     *
     * @return the coordinate of the top border
     */
    public int getTop() {
        return y1;
    }

    /**
     * Get the width of the rectangle.
     *
     * @return the width of the rectangle
     */
    public int getWidth() {
        return x1 - x0;
    }

    /**
     * Get the x coordinate (left border) of the rectangle.
     *
     * @return the coordinate of the left border
     */
    public int getX() {
        return x0;
    }

    /**
     * Get the y coordinate (bottom border) of the rectangle.
     *
     * @return the coordinate of the top border
     */
    public int getY() {
        return y0;
    }

    /**
     * Generate a hash code to identify the object.
     *
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        int retVal = x0 & 0xFF;
        retVal <<= Byte.SIZE;
        retVal += x1 & 0xFF;
        retVal <<= Byte.SIZE;
        retVal += y0 & 0xFF;
        retVal <<= Byte.SIZE;
        retVal += y1 & 0xFF;
        return retVal;
    }

    /**
     * Move the current location of the rectangle without changing its height and width.
     *
     * @param x the change value for the x coordinate
     * @param y the change value for the y coordinate
     */
    public void move(final int x, final int y) {
        x0 += x;
        x1 += x;
        y0 += y;
        y1 += y;
    }

    public void expand(final int left, final int top, final int right, final int bottom) {
        x0 -= left;
        x1 += right;
        y0 -= bottom;
        y1 += top;
    }

    /**
     * Ensure that the current rectangle describes only a area that is also described by another rectangle. So if a
     * area if this rectangle is outside the other rectangle it will be cut off.
     *
     * @param other the other rectangle
     */
    public void intersect(@Nonnull final Rectangle other) {
        x0 = Math.max(x0, other.x0);
        y0 = Math.max(y0, other.y0);
        x1 = Math.min(x1, other.x1);
        y1 = Math.min(y1, other.y1);
        if ((x1 < x0) || (y1 < y0)) {
            x1 = x0;
            y1 = y0;
        }
    }

    /**
     * Check the two rectangles for intersection.
     *
     * @param other the second rectangle
     * @return {@code true} in case there is an intersection
     */
    public boolean intersects(@Nonnull final Rectangle other) {
        if ((x0 > other.x1) || (x1 < other.x0)) {
            return false;
        }
        if ((y0 > other.y1) || (y1 < other.y0)) {
            return false;
        }
        return true;
    }

    /**
     * Check if the rectangle covers no area.
     *
     * @return {@code true} if the rectangle covers no area
     */
    public boolean isEmpty() {
        return (x0 == x1) || (y0 == y1);
    }

    /**
     * Check if a coordinate is inside of the rectangle.
     *
     * @param x the x coordinate to check
     * @param y the y coordinate of check
     * @return {@code true} in case the coordinates are inside the the rectangle
     */
    public boolean isInside(final int x, final int y) {
        return (x >= x0) && (y >= y0) && (x < x1) && (y < y1);
    }

    /**
     * Put a rectangle that is not any longer needed back into the factory.
     */
    @Deprecated
    public void recycle() {
    }

    /**
     * Set the values of this instance back to its original values.
     */
    public void reset() {
        x0 = 0;
        x1 = 0;
        y0 = 0;
        y1 = 0;
    }

    /**
     * Set the properties of this rectangle.
     *
     * @param x      the new x coordinate of this rectangle
     * @param y      the new y coordinate of this rectangle
     * @param width  the new width of this rectangle
     * @param height the new height of this rectangle
     */
    public void set(final int x, final int y, final int width, final int height) {
        x0 = x;
        y0 = y;
        x1 = x + Math.max(0, width);
        y1 = y + Math.max(0, height);
    }

    /**
     * Set the properties of this rectangle to the values of another rectangle.
     *
     * @param org the rectangle that shall be copied
     */
    public void set(@Nonnull final Rectangle org) {
        x0 = org.x0;
        x1 = org.x1;
        y0 = org.y0;
        y1 = org.y1;
    }

    /**
     * Get the same rectangle as the native implementation of rectangle.
     *
     * @return the java.awt.Rectangle that represents the same rectangle as this one
     */
    @Nonnull
    public java.awt.Rectangle toNative() {
        return new java.awt.Rectangle(x0, y0, x1 - x0, y1 - y0);
    }

    /**
     * Get the size of the area covered by this rectangle.
     *
     * @return the size of the area
     */
    public int getArea() {
        return getWidth() * getHeight();
    }
}
