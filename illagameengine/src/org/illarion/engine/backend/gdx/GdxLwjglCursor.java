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
package org.illarion.engine.backend.gdx;

import org.illarion.engine.MouseCursor;
import org.lwjgl.input.Cursor;

import javax.annotation.Nonnull;

/**
 * This is the mouse cursor implementation for libGDX.
 * <p/>
 * Since libGDX is not able to handle the native mouse cursor this class uses LWJGL directly to show the cursor. This
 * only works properly in case the libGDX backend is used.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxLwjglCursor implements MouseCursor {
    /**
     * The LWJGL implementation of the mouse cursor.
     */
    @Nonnull
    private final Cursor lwjglCursor;

    /**
     * Create a new engine mouse cursor and set the LWJGL mouse cursor that is wrapped by this class.
     *
     * @param lwjglCursor the lwjgl mouse cursor
     */
    GdxLwjglCursor(@Nonnull final Cursor lwjglCursor) {
        this.lwjglCursor = lwjglCursor;
    }

    @Override
    public void dispose() {
        lwjglCursor.destroy();
    }

    /**
     * Get the internal LWJGL cursor.
     *
     * @return the LWJGL mouse cursor
     */
    @Nonnull
    Cursor getLwjglCursor() {
        return lwjglCursor;
    }
}
