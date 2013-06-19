/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.backend.shared;

import org.illarion.engine.MouseCursor;
import org.illarion.engine.assets.CursorManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the shared cursor manager implementation that can be used by the different backend implementations in the
 * same way.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractCursorManager implements CursorManager {
    /**
     * This map stores the cursors that were already load.
     */
    private final Map<String, MouseCursor> loadedCursors;

    /**
     * Create a new instance of this manager.
     */
    protected AbstractCursorManager() {
        loadedCursors = new HashMap<String, MouseCursor>();
    }

    @Nullable
    @Override
    public final MouseCursor getCursor(@Nonnull final String ref, final int hotspotX, final int hotspotY) {
        @Nullable final MouseCursor bufferedCursor = loadedCursors.get(ref);
        if (bufferedCursor == null) {
            @Nullable final MouseCursor newCursor = loadCursor(ref, hotspotX, hotspotY);
            if (newCursor != null) {
                loadedCursors.put(ref, newCursor);
            }
            return newCursor;
        }
        return bufferedCursor;
    }

    /**
     * Load the cursor that is referred to with the reference.
     *
     * @param ref      the reference to the cursor
     * @param hotspotX the x coordinate of the hotspot
     * @param hotspotY the y coordinate of the hotspot
     * @return the newly load cursor
     */
    @Nullable
    protected abstract MouseCursor loadCursor(@Nonnull String ref, int hotspotX, int hotspotY);
}
