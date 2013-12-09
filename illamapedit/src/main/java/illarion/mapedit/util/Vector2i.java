/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
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

    public Vector2i(final int x, final int y) {

        this.x = x;
        this.y = y;
    }

    public Vector2i(@Nonnull final Vector2i v) {
        x = v.x;
        y = v.y;
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public void set(final int x, final int y) {

        this.x = x;
        this.y = y;
    }

    private void set(@Nonnull final Vector2i v) {
        x = v.x;
        y = v.y;
    }

    private void add(@Nonnull final Vector2i v) {
        x += v.x;
        y += v.y;
    }

    private void sub(@Nonnull final Vector2i v) {
        x += v.x;
        y += v.y;
    }

    private void times(final int i) {
        x *= i;
        y *= i;
    }

    private void div(final int i) {
        x /= i;
        y /= i;
    }

    @Override
    public String toString() {
        return String.format("( x: %d | y:%d )", x, y);
    }
}
