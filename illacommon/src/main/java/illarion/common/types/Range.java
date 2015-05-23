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
package illarion.common.types;

import org.jetbrains.annotations.Contract;

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
     * The format of the string that is returned with the {@link #toString()} function.
     */
    @Nonnull
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
    public Range(int min, int max) {
        minValue = min;
        maxValue = max;
    }

    /**
     * Create a new instance of the range and copy the data of another range object.
     *
     * @param org the range object that shall be copied
     */
    public Range(@Nonnull Range org) {
        minValue = org.minValue;
        maxValue = org.maxValue;
    }

    /**
     * Compare this instance of the range with another object. This returns true only in case the object this range
     * instance is compared with is a instance of range and the two border values are exactly the same.
     *
     * @param obj the object this instance of Range shall be compared with
     * @return the result of the comparing
     */
    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(@Nullable Object obj) {
        if (super.equals(obj)) {
            return true;
        }

        if (!(obj instanceof Range)) {
            return false;
        }

        Range compRange = (Range) obj;
        return (compRange.minValue == minValue) && (compRange.maxValue == maxValue);
    }

    /**
     * Get the top border of this range.
     *
     * @return the top border value
     */
    @Contract(pure = true)
    public int getMax() {
        return maxValue;
    }

    /**
     * Get the bottom border of this range.
     *
     * @return the bottom border value
     */
    @Contract(pure = true)
    public int getMin() {
        return minValue;
    }

    /**
     * Generate a hash code that identifies this range object. The generated hash codes do not identify the range
     * instance exactly, but in case the hash codes of two objects of the range class are the same its pretty sure that
     * the borders are the same.
     *
     * @return the generated hashcode
     */
    @Override
    @Contract(pure = true)
    public int hashCode() {
        return (minValue * maxValue) % Integer.MAX_VALUE;
    }

    /**
     * Create a string representation of this range object. It shows the borders of this range.
     *
     * @return the generated string
     */
    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return String.format(TO_STRING_FORMAT, Integer.toString(minValue), Integer.toString(maxValue));
    }
}
