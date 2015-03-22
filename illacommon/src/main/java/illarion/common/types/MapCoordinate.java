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
public final class MapCoordinate {
    private final int column;
    private final int row;

    public MapCoordinate(int column, int row) {
        this.column = column;
        this.row = row;
    }

    @Contract(pure = true)
    public int getColumn() {
        return column;
    }

    @Contract(pure = true)
    public int getRow() {
        return row;
    }

    @Contract(pure = true)
    public int toDisplayX() {
        return toDisplayX(column);
    }

    @Contract(pure = true)
    public static int toDisplayX(int column) {
        return column * MapConstants.STEP_X;
    }

    @Contract(pure = true)
    public int toDisplayY() {
        return toDisplayY(row);
    }

    @Contract(pure = true)
    public static int toDisplayY(int row) {
        return -row * MapConstants.STEP_Y;
    }

    @Contract(pure = true)
    public int toDisplayZ() {
        return toDisplayZ(row);
    }

    @Contract(pure = true)
    public static int toDisplayZ(int row) {
        return row * DisplayCoordinate.ROW_DISTANCE;
    }

    @Contract(pure = true)
    public int toServerX() {
        return toServerX(column, row);
    }

    @Contract(pure = true)
    public static int toServerX(int column, int row) {
        return (column + row) / 2;
    }

    @Contract(pure = true)
    public int toServerY() {
        return toServerY(column, row);
    }

    @Contract(pure = true)
    public static int toServerY(int column, int row) {
        return (column - row) / 2;
    }

    @Override
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof MapCoordinate) && equals((MapCoordinate) obj);
    }

    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable MapCoordinate mapCoordinate) {
        return (mapCoordinate != null) && (mapCoordinate.column == column) && (mapCoordinate.row == row);
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return ((27 + column) * 31) + row;
    }

    @Override
    @Nonnull
    @Contract(pure = true)
    public String toString() {
        return "Map Coordinate (Column: " + column + ", Row: " + row + ')';
    }
}
