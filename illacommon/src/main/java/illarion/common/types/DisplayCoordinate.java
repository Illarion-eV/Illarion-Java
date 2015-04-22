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

import illarion.common.graphics.MapConstants;
import illarion.common.util.FastMath;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
@SuppressWarnings("InstanceVariableNamingConvention")
public class DisplayCoordinate {
    /**
     * The layer distance from one row to the next.
     */
    public static final int ROW_DISTANCE = 50;
    /**
     * The layer distance from one level to the next.
     */
    public static final int LEVEL_DISTANCE = 500;

    private final int x;
    private final int y;
    private final int layer;

    public DisplayCoordinate(int x, int y, int layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    public DisplayCoordinate(@Nonnull DisplayCoordinate org, int dX, int dY, int dLayer) {
        this(org.getX() + dX, org.getY() + dY, org.getLayer() - dLayer);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLayer() {
        return layer;
    }

    @Nonnull
    @Contract(pure = true)
    public MapCoordinate toMapCoordinate() {
        return toMapCoordinate(x, y);
    }

    @Nonnull
    @Contract(pure = true)
    public static MapCoordinate toMapCoordinate(int x, int y) {
        return new MapCoordinate(toMapColumn(x), toMapRow(y));
    }

    @Contract(pure = true)
    public int toMapColumn() {
        return toMapColumn(x);
    }

    @Contract(pure = true)
    public static int toMapColumn(int x) {
        return FastMath.round(x / (float) MapConstants.STEP_X);
    }

    @Contract(pure = true)
    public int toMapRow() {
        return toMapRow(y);
    }

    @Contract(pure = true)
    public static int toMapRow(int y) {
        return FastMath.round(-y / (float) MapConstants.STEP_Y);
    }

    @Contract(pure = true)
    public int toServerX() {
        return toServerX(x, y);
    }

    @Contract(pure = true)
    public static int toServerX(int x, int y) {
        return FastMath.round(((-y / (float) MapConstants.STEP_Y) + (x / (float) MapConstants.STEP_X)) / 2.f);
    }

    @Contract(pure = true)
    public int toServerY() {
        return toServerY(x, y);
    }

    @Contract(pure = true)
    public static int toServerY(int x, int y) {
        return FastMath.round(((x / (float) MapConstants.STEP_X) - (-y / (float) MapConstants.STEP_Y)) / 2.f);
    }

    @Override
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof DisplayCoordinate) && equals((DisplayCoordinate) obj);
    }

    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable DisplayCoordinate displayCoordinate) {
        return (displayCoordinate != null) &&
                (displayCoordinate.x == x) && (displayCoordinate.y == y) && (displayCoordinate.layer == layer);
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return ((((27 + x) * 31) + y) * 31) + layer;
    }

    @Override
    @Nonnull
    @Contract(pure = true)
    public String toString() {
        return "Display Coordinate (" + x + ", " + y + ", Layer: " + layer + ')';
    }
}
