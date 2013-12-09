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
package org.illarion.engine.assets;

import org.illarion.engine.MouseCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interfaces defines the mouse cursor manager. Its able to load and store the mouse cursors.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface CursorManager {
    /**
     * Fetch a mouse cursor. This either loads a new cursor or retrieves one that was load before from the internal
     * storage.
     *
     * @param ref      the cursor reference
     * @param hotspotX the X coordinate of the cursor hotspot
     * @param hotspotY the Y coordinate of the cursor hotspot
     * @return the mouse cursor or {@code null} in case loading the required mouse cursor is not possible
     */
    @Nullable
    MouseCursor getCursor(@Nonnull String ref, int hotspotX, int hotspotY);
}
