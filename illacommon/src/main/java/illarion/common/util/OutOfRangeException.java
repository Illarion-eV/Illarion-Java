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

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class OutOfRangeException extends IllegalArgumentException {
    public OutOfRangeException(@Nonnull String parameterName, int value, int min, int max) {
        this(parameterName, Integer.toString(value), Integer.toString(min), Integer.toString(max));
    }

    public OutOfRangeException(@Nonnull String parameterName,
                               @Nonnull String value,
                               @Nonnull String min,
                               @Nonnull String max) {
        super("Argument \"" + parameterName + "\" is out of range. " + value + " should be between " + min + " and " +
                max);
    }

    public OutOfRangeException(@Nonnull String parameterName, long value, long min, long max) {
        this(parameterName, Long.toString(value), Long.toString(min), Long.toString(max));
    }

    public OutOfRangeException(@Nonnull String parameterName, float value, float min, float max) {
        this(parameterName, Float.toString(value), Float.toString(min), Float.toString(max));
    }

    public OutOfRangeException(@Nonnull String parameterName, double value, double min, double max) {
        this(parameterName, Double.toString(value), Double.toString(min), Double.toString(max));
    }
}