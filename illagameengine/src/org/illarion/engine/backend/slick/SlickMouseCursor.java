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
package org.illarion.engine.backend.slick;

import org.illarion.engine.MouseCursor;
import org.lwjgl.input.Cursor;

import javax.annotation.Nonnull;

/**
 * The mouse cursor implementation of Slick.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickMouseCursor implements MouseCursor {
    /**
     * The LWJGL mouse cursor that is activated once this mouse cursor is enabled.
     */
    @Nonnull
    private final Cursor cursor;

    /**
     * The X coordinate of the hotspot on the cursor.
     */
    private final int hotspotX;

    /**
     * The Y coordinate of the hotspot on the cursor.
     */
    private final int hotspotY;

    /**
     * Create a new instance of this mouse cursor implementation for Slick2D.
     *
     * @param cursor   the LWJGL cursor that is wrapped by this class
     * @param hotspotX the X coordinate of the cursor hotspot
     * @param hotspotY the Y coordinate of the cursor hotspot
     */
    SlickMouseCursor(@Nonnull final Cursor cursor, final int hotspotX, final int hotspotY) {
        this.cursor = cursor;
        this.hotspotX = hotspotX;
        this.hotspotY = hotspotY;
    }

    /**
     * The the mouse cursor that is wrapped in this class.
     *
     * @return the LWJGL mouse cursor
     */
    @Nonnull
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * Get the X coordinate of the cursors hotspot.
     *
     * @return the x coordinate of the cursor hotspot
     */
    public int getHotspotX() {
        return hotspotX;
    }

    /**
     * Get the Y coordinate of the cursors hotspot.
     *
     * @return the Y coordinate of the cursor hotspot
     */
    public int getHotspotY() {
        return hotspotY;
    }

    @Override
    public void dispose() {
        // nothing to do
    }
}
