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
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Class to handle a range between two values.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
@Immutable
public final class Range {
    /**
     * The value for the interpolation function that will cause the top border value to be the result of the function.
     */
    public static final int INTERPOLATE_MAX = 100;

    /**
     * This is the same value as interpolate just as float.
     */
    private static final float INTERPOLATE_MAX_F = INTERPOLATE_MAX;

    /**
     * The format of the string that is returned with the {@link #toString()} function.
     */
    @Nonnull
    @SuppressWarnings("nls")
    private static final String TO_STRING_FORMAT = "r(%s$1; %s$2)";

    /**
     * The top border of the range.
     */
    private final int maxValue;

    /**
     * The bottom border of the range.
     */
    private final int minValue;

    /**
     * Default constructor, create a range object with 0 and bottom and top border.
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
     * Create a new instance of the range and copy the data of another range object.
     *
     * @param org the range object that shall be copied
     */
    public Range(@Nonnull final Range org) {
        minValue = org.minValue;
        maxValue = org.maxValue;
    }

    /**
     * Compare this instance of the range with another object. This returns true only in case the object this range
     * instance is compared with is a instance of range and the two border values are exactly the same.
     *
     * @param o the object this instance of Range shall be compared with
     * @return the result of the comparing
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(@Nullable final Object o) {
        if (super.equals(o)) {
            return true;
        }

        if (!(o instanceof Range)) {
            return false;
        }

        final Range compRange = (Range) o;
        return (compRange.minValue == minValue) && (compRange.maxValue == maxValue);
    }

    /**
     * Get the difference between the maximum and the minimum value.
     *
     * @return the difference between both values
     */
    public int getDifference() {
        return maxValue - minValue;
    }

    /**
     * Get a interpolated value. This function returns the same as {@link #getMin()} in case 0 is set for the parameter
     * and it returns {@link #getMax()} in case {@link #INTERPOLATE_MAX} is set for the parameter. For values between 0
     * and {@link #INTERPOLATE_MAX} it will return a linear interpolated value between the borders.
     *
     * @param location the interpolation value
     * @return the interpolated value from the range
     */
    public int getInterpolated(final int location) {
        return (int) (((maxValue - minValue) * location) / INTERPOLATE_MAX_F) + minValue;
    }

    /**
     * Get the top border of this range.
     *
     * @return the top border value
     */
    public int getMax() {
        return maxValue;
    }

    /**
     * Get the bottom border of this range.
     *
     * @return the bottom border value
     */
    public int getMin() {
        return minValue;
    }

    /**
     * Generate a hash code that identifies this range object. The generated hash codes do not identify the range
     * instance exactly, but in case the hash codes of two objects of the range class are the same its pretty sure that
     * the borders are the same.
     *
     * @return the generated hashcode
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (minValue * maxValue) % Integer.MAX_VALUE;
    }

    /**
     * Create a string representation of this range object. It shows the borders of this range.
     *
     * @return the generated string
     * @see Object#toString()
     */
    @Nonnull
    @Override
    public String toString() {
        return String.format(TO_STRING_FORMAT, Integer.toString(minValue), Integer.toString(maxValue));
    }
}
