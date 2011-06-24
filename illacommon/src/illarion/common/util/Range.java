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

import java.util.Random;

import javolution.lang.Immutable;

/**
 * Class to handle a range between two values.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class Range {
    /**
     * This class implements a immutable representation of the parent Range
     * class. This class acts in the same way as the parent class with the only
     * difference that it does not allow any changes to the internal values.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class RangeImmutable extends Range implements
        Immutable {
        /**
         * The error message displayed in case a set function is called to
         * change this object.
         */
        @SuppressWarnings("nls")
        private static final String IMMUTABLE_ERROR_MSG =
            "Object is immutable. Changes are not allowed";

        /**
         * The constructor used to create the original instance of this object.
         * 
         * @param org the original range object that is copied with this
         *            immutable representation.
         */
        public RangeImmutable(final Range org) {
            super(org);
        }

        /**
         * Overwritten set function that rejects this operation.
         */
        @Override
        public void set(final Range org) {
            throw new IllegalStateException(IMMUTABLE_ERROR_MSG);
        }

        /**
         * Overwritten set function that rejects this operation.
         */
        @Override
        public void setBorders(final int min, final int max) {
            throw new IllegalStateException(IMMUTABLE_ERROR_MSG);
        }

        /**
         * The human readable string that represents this object.
         */
        @Override
        @SuppressWarnings("nls")
        public String toString() {
            return super.toString() + "i";
        }
    }

    /**
     * The value for the interpolation function that will cause the top border
     * value to be the result of the function.
     */
    public static final int INTERPOLATE_MAX = 100;

    /**
     * This is the same value as interpolate just as float.
     */
    private static final float INTERPOLATE_MAX_F = INTERPOLATE_MAX;

    /**
     * The random value generator used by this class.
     */
    private static final Random RND = new Random();

    /**
     * The format of the string that is returned with the {@link #toString()}
     * function.
     */
    @SuppressWarnings("nls")
    private static final String TO_STRING_FORMAT = "r(%s$1; %s$2)";

    /**
     * The top border of the range.
     */
    private int maxValue;

    /**
     * The bottom border of the range.
     */
    private int minValue;

    /**
     * Default constructor, create a range object with 0 and bottom and top
     * border.
     */
    public Range() {
        minValue = 0;
        maxValue = 0;
    }

    /**
     * Create a range object with a top and a bottom border value.
     * 
     * @param min the bottom border of the range
     * @param max the top border of the range
     */
    public Range(final int min, final int max) {
        minValue = min;
        maxValue = max;
    }

    /**
     * Create a new instance of the range and copy the data of another range
     * object.
     * 
     * @param org the range object that shall be copied
     */
    public Range(final Range org) {
        minValue = org.minValue;
        maxValue = org.maxValue;
    }

    /**
     * Compare this instance of the range with another object. This returns true
     * only in case the object this range instance is compared with is a
     * instance of range and the two border values are exactly the same.
     * 
     * @param o the object this instance of Range shall be compared with
     * @return the result of the comparing
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public final boolean equals(final Object o) {
        if (!(o instanceof Range)) {
            return false;
        }

        final Range compRange = (Range) o;
        return ((compRange.minValue == minValue) && (compRange.maxValue == maxValue));
    }

    /**
     * Get the difference between the maximum and the minimum value.
     * 
     * @return the difference between both values
     */
    public final int getDifference() {
        return maxValue - minValue;
    }

    /**
     * Get a interpolated value. This function returns the same as
     * {@link #getMin()} in case 0 is set for the parameter and it returns
     * {@link #getMax()} in case {@link #INTERPOLATE_MAX} is set for the
     * parameter. For values between 0 and {@link #INTERPOLATE_MAX} it will
     * return a linear interpolated value between the borders.
     * 
     * @param location the interpolation value
     * @return the interpolated value from the range
     */
    public final int getInterpolated(final int location) {
        return (int) (((maxValue - minValue) * location) / INTERPOLATE_MAX_F)
            + minValue;
    }

    /**
     * Get the top border of this range.
     * 
     * @return the top border value
     */
    public final int getMax() {
        return maxValue;
    }

    /**
     * Get the bottom border of this range.
     * 
     * @return the bottom border value
     */
    public final int getMin() {
        return minValue;
    }

    /**
     * Get a random value that is between the top and and bottom border.
     * 
     * @return a generated random value between the top and the bottom border
     */
    public final int getRandomValue() {
        return (RND.nextInt((maxValue - minValue) + 1) + minValue) - 1;
    }

    /**
     * Generate a hash code that identifies this range object. The generated
     * hash codes do not identify the range instance exactly, but in case the
     * hash codes of two objects of the range class are the same its pretty sure
     * that the borders are the same.
     * 
     * @return the generated hashcode
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return (minValue * maxValue) % Integer.MAX_VALUE;
    }

    /**
     * Get a immutable representation of the current state of this range
     * instance. This instance will never change its values and will not allow
     * any changes performed to its values.
     * 
     * @return the immutable instance of this class
     */
    public final Range immutable() {
        return new RangeImmutable(this);
    }

    /**
     * Copy the data of another Range object instance into this instance.
     * 
     * @param org the range object that shall be copied
     */
    public void set(final Range org) {
        minValue = org.minValue;
        maxValue = org.maxValue;
    }

    /**
     * Set the border values of this range object.
     * 
     * @param min the bottom border of the range
     * @param max the top border of the range
     */
    public void setBorders(final int min, final int max) {
        minValue = min;
        maxValue = max;
    }

    /**
     * Create a string representation of this range object. It shows the borders
     * of this range.
     * 
     * @return the generated string
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(TO_STRING_FORMAT, Integer.toString(minValue),
            Integer.toString(maxValue));
    }
}
