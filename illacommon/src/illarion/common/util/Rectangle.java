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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javolution.context.ObjectFactory;

/**
 * The rectangle class is a helper class that allows to define rectangles and
 * check them for intersection.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Rectangle implements Cloneable, Reusable, Externalizable {
    /**
     * This factory class is used to create and store the instances of this
     * Rectangles.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class RectangleFactory extends
        ObjectFactory<Rectangle> {
        /**
         * Public constructor to allow the parent class the proper creation of
         * instances.
         */
        public RectangleFactory() {
            super();
        }

        /**
         * Create a new object of the maintained objects. In this case a new
         * Rectangle object is created.
         * 
         * @return the new rectangle object
         */
        @Override
        protected Rectangle create() {
            return new Rectangle();
        }
    }

    /**
     * The factory that takes care to create and recycle the instances of this
     * class.
     */
    private static final RectangleFactory FACTORY = new RectangleFactory();

    /**
     * Serialization UID of this rectangle class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The first x coordinate of this rectangle. This is the left border of the
     * rectangle.
     */
    private int x0;
    /**
     * The second x coordinate of this rectangle. This is the right border of
     * the rectangle.
     */
    private int x1;

    /**
     * The first y coordinate of this rectangle. This is the bottom border of
     * the rectangle.
     */
    private int y0;

    /**
     * The second y coordinate of this rectangle. This is the top border of the
     * rectangle.
     */
    private int y1;

    /**
     * The private constructor to ensure that new instances are only created by
     * the get method.
     */
    Rectangle() {
        reset();
    }

    /**
     * Get a instance of the rectangle class. Either a new one or one from the
     * buffer.
     * 
     * @return the rectangle method that is free for use.
     */
    public static Rectangle getInstance() {
        return FACTORY.object();
    }

    /**
     * Add another rectangle to this one. This basically creates a union of both
     * rectangles.
     * 
     * @param other the rectangle that shall be added to the current instance
     */
    public void add(final Rectangle other) {
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
     * Create a copy of the object.
     */
    @Override
    public Rectangle clone() {
        try {
            return (Rectangle) super.clone();
        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Test this rectangle and another object for being equal.
     * 
     * @param o the object this Rectangle shall be compared with
     * @return <code>true</code> in case this rectangle and the other one
     *         descripe the same rectangle
     */
    @Override
    public boolean equals(final Object o) {
        if (super.equals(o)) {
            return true;
        }
        if (o instanceof Rectangle) {
            final Rectangle oRect = (Rectangle) o;
            return ((oRect.x0 == x0) && (oRect.x1 == x1) && (oRect.y0 == y0) && (oRect.y1 == y1));
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
        int retVal = (x0 & 0xFF);
        retVal <<= Byte.SIZE;
        retVal += (x1 & 0xFF);
        retVal <<= Byte.SIZE;
        retVal += (y0 & 0xFF);
        retVal <<= Byte.SIZE;
        retVal += (y1 & 0xFF);
        return retVal;
    }

    /**
     * Ensure that the current rectangle describes only a area that is also
     * described by another rectangle. So if a area if this rectangle is outside
     * the other rectangle it will be cut off.
     * 
     * @param other the other rectangle
     */
    public void intersect(final Rectangle other) {
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
     * @return <code>true</code> in case there is an intersection
     */
    public boolean intersects(final Rectangle other) {
        return (((other.x1 < other.x0) || (other.x1 > x0))
            && ((other.y1 < other.y0) || (other.y1 > y0))
            && ((x1 < x0) || (x1 > other.x0)) && ((y1 < y0) || (y1 > other.y0)));
    }

    /**
     * Check if the rectangle covers no area.
     * 
     * @return <code>true</code> if the rectangle covers no area
     */
    public boolean isEmpty() {
        return ((x0 == x1) || (y0 == y1));
    }

    /**
     * Check if a coordinate is inside of the rectangle.
     * 
     * @param x the x coordinate to check
     * @param y the y coordinate of check
     * @return <code>true</code> in case the coordinates are inside the the
     *         rectangle
     */
    public boolean isInside(final int x, final int y) {
        return (x >= x0) && (y >= y0) && (x < x1) && (y < y1);
    }

    /**
     * This function is used to read the rectangle from a input stream.
     */
    @SuppressWarnings("nls")
    @Override
    public void readExternal(final ObjectInput in) throws IOException,
        ClassNotFoundException {
        final long serial = in.readLong();

        if (serial == serialVersionUID) {
            x0 = in.readInt();
            x1 = in.readInt();
            y0 = in.readInt();
            y1 = in.readInt();
        } else {
            throw new IOException("Illegal version number");
        }
    }

    /**
     * Put a rectangle that is not any longer needed back into the factory.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Set the values of this instance back to its original values.
     */
    @Override
    public void reset() {
        x0 = 0;
        x1 = 0;
        y0 = 0;
        y1 = 0;
    }

    /**
     * Set the properties of this rectangle.
     * 
     * @param x the new x coordinate of this rectangle
     * @param y the new y coordinate of this rectangle
     * @param width the new width of this rectangle
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
    public void set(final Rectangle org) {
        x0 = org.x0;
        x1 = org.x1;
        y0 = org.y0;
        y1 = org.y1;
    }

    /**
     * Get the same rectangle as the native implementation of rectangle.
     * 
     * @return the java.awt.Rectangle that represents the same rectangle as this
     *         one
     */
    public java.awt.Rectangle toNative() {
        return new java.awt.Rectangle(x0, y0, x1 - x0, y1 - y0);
    }

    /**
     * This function is used to write the rectangle to a output stream.
     */
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeInt(x0);
        out.writeInt(x1);
        out.writeInt(y0);
        out.writeInt(y1);
    }
}
