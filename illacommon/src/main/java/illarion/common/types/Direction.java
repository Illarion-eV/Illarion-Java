/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import illarion.common.net.NetCommReader;
import illarion.common.net.NetCommWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum Direction {
    North(0, 0, -1),
    NorthEast(1, 1, -1),
    East(2, 1, 0),
    SouthEast(3, 1, 1),
    South(4, 0, 1),
    SouthWest(5, -1, 1),
    West(6, -1, 0),
    NorthWest(7, -1, -1);

    private final int serverId;
    private final int xVec;
    private final int yVec;

    Direction(int serverId, int xVec, int yVec) {
        this.serverId = serverId;
        this.xVec = xVec;
        this.yVec = yVec;
    }

    public int getServerId() {
        return serverId;
    }

    public int getDirectionVectorX() {
        return xVec;
    }

    public int getDirectionVectorY() {
        return yVec;
    }

    public boolean isDiagonal() {
        return (xVec != 0) && (yVec != 0);
    }

    @Nonnull
    public static Direction getReverse(@Nonnull Direction direction) {
        switch (direction) {
            case North:
                return South;
            case NorthEast:
                return SouthWest;
            case East:
                return West;
            case SouthEast:
                return NorthWest;
            case South:
                return North;
            case SouthWest:
                return NorthEast;
            case West:
                return East;
            case NorthWest:
                return SouthEast;
        }
        throw new IllegalArgumentException("Invalid direction");
    }

    public void encode(@Nonnull NetCommWriter writer) {
        writer.writeUByte((short) serverId);
    }

    public static void encode(@Nullable Direction dir, @Nonnull NetCommWriter writer) {
        if (dir == null) {
            writer.writeUByte((short) 0x0A);
        } else {
            dir.encode(writer);
        }
    }

    public static Direction decode(@Nonnull NetCommReader reader) throws IOException {
        int dirId = reader.readUByte();
        for (Direction dir : values()) {
            if (dir.serverId == dirId) {
                return dir;
            }
        }
        return null;
    }
}
