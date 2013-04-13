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
package org.illarion.engine.nifty;

import de.lessvoid.nifty.spi.render.MouseCursor;

import javax.annotation.Nonnull;

/**
 * This is the mouse cursor implementation that uses the mouse cursors of the game engine.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class IgeMouseCursor implements MouseCursor {
    /**
     * This is the cursor that is wrapped by this mouse cursor implementation.
     */
    @Nonnull
    private final org.illarion.engine.MouseCursor cursor;

    /**
     * Create a new mouse cursor that wraps the mouse cursor of the engine.
     *
     * @param cursor the wrapped cursor
     */
    IgeMouseCursor(@Nonnull final org.illarion.engine.MouseCursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Get the mouse cursor that is wrapped in this class.
     *
     * @return the wrapped mouse cursor
     */
    @Nonnull
    public org.illarion.engine.MouseCursor getCursor() {
        return cursor;
    }

    @Override
    public void dispose() {
        cursor.dispose();
    }
}
