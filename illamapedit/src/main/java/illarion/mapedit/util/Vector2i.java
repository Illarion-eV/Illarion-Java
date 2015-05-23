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
package illarion.mapedit.util;

import javax.annotation.Nonnull;

/**
 * @author Tim
 */
public class Vector2i {
    private int x;
    private int y;

    public Vector2i() {
        this(0, 0);
    }

    public Vector2i(int x, int y) {

        this.x = x;
        this.y = y;
    }

    public Vector2i(@Nonnull Vector2i v) {
        x = v.x;
        y = v.y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void set(int x, int y) {

        this.x = x;
        this.y = y;
    }

    private void set(@Nonnull Vector2i v) {
        x = v.x;
        y = v.y;
    }

    private void add(@Nonnull Vector2i v) {
        x += v.x;
        y += v.y;
    }

    private void sub(@Nonnull Vector2i v) {
        x += v.x;
        y += v.y;
    }

    private void times(int i) {
        x *= i;
        y *= i;
    }

    private void div(int i) {
        x /= i;
        y /= i;
    }

    @Nonnull
    @Override
    public String toString() {
        return String.format("( x: %d | y:%d )", x, y);
    }
}
