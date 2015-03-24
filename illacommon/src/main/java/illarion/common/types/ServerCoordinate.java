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

import illarion.common.graphics.Layer;
import illarion.common.graphics.MapConstants;
import illarion.common.net.NetCommReader;
import illarion.common.net.NetCommWriter;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;

/**
 * This class represents a server coordinate.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
@SuppressWarnings("InstanceVariableNamingConvention")
public final class ServerCoordinate {
    /**
     * Modifier used at the calculation of the display coordinates in case its a tile above or below the level 0.
     */
    public static final int DISPLAY_Z_OFFSET_MOD = 6;

    private final int x;
    private final int y;
    private final int z;

    public ServerCoordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ServerCoordinate(@Nonnull ServerCoordinate org, @Nonnull Direction dir) {
        this(org, dir.getDirectionVectorX(), dir.getDirectionVectorY(), 0);
    }

    public ServerCoordinate(@Nonnull ServerCoordinate org, int dX, int dY, int dZ) {
        this(org.x + dX, org.y + dY, org.z + dZ);
    }

    public ServerCoordinate(@Nonnull NetCommReader reader) throws IOException {
        x = reader.readShort();
        y = reader.readShort();
        z = reader.readShort();
    }

    @Contract(pure = true)
    public int getX() {
        return x;
    }

    @Contract(pure = true)
    public int getY() {
        return y;
    }

    @Contract(pure = true)
    public int getZ() {
        return z;
    }

    public void encode(@Nonnull NetCommWriter writer) {
        writer.writeShort((short) x);
        writer.writeShort((short) y);
        writer.writeShort((short) z);
    }

    @Nullable
    @Contract(pure = true)
    public Direction getDirection(@Nonnull ServerCoordinate target) {
        return getDirection(this, target);
    }

    @Nullable
    @Contract(pure = true)
    public static Direction getDirection(@Nonnull ServerCoordinate origin, @Nonnull ServerCoordinate target) {
        int dX = origin.getX() - target.getX();
        int dY = origin.getY() - target.getY();

        if ((dX == 0) && (dY == 0)) {
            return null;
        }

        double theta = Math.atan2(dY, dX) + Math.PI;
        double part = Math.PI / 8;

        if (theta < part) {
            return Direction.East;
        } else if (theta < (3 * part)) {
            return Direction.SouthEast;
        } else if (theta < (5 * part)) {
            return Direction.South;
        } else if (theta < (7 * part)) {
            return Direction.SouthWest;
        } else if (theta < (9 * part)) {
            return Direction.West;
        } else if (theta < (11 * part)) {
            return Direction.NorthWest;
        } else if (theta < (13 * part)) {
            return Direction.North;
        } else if (theta < (15 * part)) {
            return Direction.NorthEast;
        } else {
            return Direction.East;
        }
    }

    @Contract(pure = true)
    public int getStepDistance(@Nonnull ServerCoordinate target) {
        return getStepDistance(this, target);
    }

    @Contract(pure = true)
    public static int getStepDistance(@Nonnull ServerCoordinate origin, @Nonnull ServerCoordinate target) {
        int dX = Math.abs(origin.getX() - target.getX());
        int dY = Math.abs(origin.getY() - target.getY());

        return Math.max(dX, dY);
    }

    @Contract(pure = true)
    public double getDistance(@Nonnull ServerCoordinate target) {
        return getDistance(this, target);
    }

    @Contract(pure = true)
    public static double getDistance(@Nonnull ServerCoordinate origin, @Nonnull ServerCoordinate target) {
        int dX = origin.getX() - target.getX();
        int dY = origin.getY() - target.getY();

        return Math.sqrt((dX * dX) + (dY * dY));
    }

    @Contract(pure = true)
    public boolean isNeighbour(@Nonnull ServerCoordinate otherCoordinate) {
        return (z == otherCoordinate.z) && (Math.abs(x - otherCoordinate.x) <= 1) && (Math.abs(y - otherCoordinate.y) <= 1);
    }

    @Nonnull
    @Contract(pure = true)
    public MapCoordinate toMapCoordinate() {
        return toMapCoordinate(x, y);
    }

    @Nonnull
    @Contract(pure = true)
    public static MapCoordinate toMapCoordinate(int x, int y) {
        return new MapCoordinate(toMapColumn(x, y), toMapRow(x, y));
    }

    @Nonnull
    @Contract(pure = true)
    public DisplayCoordinate toDisplayCoordinate(@Nonnull Layer layer) {
        return toDisplayCoordinate(x, y, z, layer);
    }

    @Nonnull
    @Contract(pure = true)
    public static DisplayCoordinate toDisplayCoordinate(int x, int y, int z, @Nonnull Layer layer) {
        return new DisplayCoordinate(toDisplayX(x, y), toDisplayY(x, y, z), toDisplayLayer(x, y, z, layer));
    }

    @Contract(pure = true)
    public int toDisplayX() {
        return toDisplayX(x, y);
    }

    @Contract(pure = true)
    public static int toDisplayX(int scX, int scY) {
        return (scX + scY) * MapConstants.STEP_X;
    }

    @Contract(pure = true)
    public int toDisplayY() {
        return toDisplayY(x, y, z);
    }

    @Contract(pure = true)
    public static int toDisplayY(int scX, int scY, int scZ) {
        return -(((scX - scY) * MapConstants.STEP_Y) + (DISPLAY_Z_OFFSET_MOD * scZ * MapConstants.STEP_Y));
    }

    @Contract(pure = true)
    public int toDisplayLayer(@Nonnull Layer layer) {
        return toDisplayLayer(x, y, z, layer);
    }

    @Contract(pure = true)
    public static int toDisplayLayer(int scX, int scY, int scZ, @Nonnull Layer layer) {
        return ((scX - scY - (scZ * DisplayCoordinate.LEVEL_DISTANCE)) * DisplayCoordinate.ROW_DISTANCE) -
                layer.getLayerOffset();
    }

    @Contract(pure = true)
    public int toMapColumn() {
        return toMapColumn(x, y);
    }

    @Contract(pure = true)
    public static int toMapColumn(int scX, int scY) {
        return scX + scY;
    }

    @Contract(pure = true)
    public int toMapRow() {
        return toMapRow(x, y);
    }

    @Contract(pure = true)
    public static int toMapRow(int scX, int scY) {
        return scX - scY;
    }

    @Override
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof ServerCoordinate) && equals((ServerCoordinate) obj);
    }

    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable ServerCoordinate serverCoordinate) {
        return (serverCoordinate != null) &&
                (serverCoordinate.x == x) && (serverCoordinate.y == y) && (serverCoordinate.z == z);
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return ((((27 + x) * 31) + y) * 31) + z;
    }

    @Override
    @Nonnull
    @Contract(pure = true)
    public String toString() {
        return "Server Coordinate (" + x + ", " + y + ", " + z + ')';
    }
}
