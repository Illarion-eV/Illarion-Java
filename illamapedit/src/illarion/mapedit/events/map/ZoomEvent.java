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
package illarion.mapedit.events.map;

import illarion.mapedit.util.Vector2i;

import javax.annotation.Nullable;

/**
 * @author Tim
 */
public class ZoomEvent {

    private final boolean original;

    private final float value;
    @Nullable
    private final Vector2i pos;

    public ZoomEvent(final float value, @Nullable final Vector2i pos) {
        this.pos = pos;
        original = false;
        this.value = value;
    }

    public ZoomEvent() {
        pos = null;
        original = true;
        value = 0;
    }

    public float getValue() {
        if (original) {
            throw new IllegalStateException("ZoomEvent has no value if it's 'original'," +
                    " check that first.");
        }
        return value;
    }

    public boolean isOriginal() {
        return original;
    }

    @Nullable
    public Vector2i getPos() {
        return pos;
    }
}
