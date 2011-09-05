/*
 * This file is part of the Illarion Nifty-GUI binding.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Nifty-GUI binding is free software: you can redistribute i
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * The Illarion Nifty-GUI binding is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Nifty-GUI binding. If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.renderer.render;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import de.lessvoid.nifty.spi.render.MouseCursor;

/**
 * This is the Illarion implementation of a mouse cursor.
 * 
 * @author Martin Karing
 * @since 1.22/1.3
 * @version 1.22/1.3
 */
public final class IllarionMouseCursor implements MouseCursor {
    /**
     * The internal mouse cursor that is used to actually display the mouse
     * cursor.
     */
    private final illarion.graphics.MouseCursor internalCursor;

    /**
     * Construct the mouse cursor.
     * 
     * @param sprite the sprite that is used to display the mouse cursor
     * @param hotspotX the x location of the hotspot of the mouse cursor
     * @param hotspotY the y location of the hotspot of the mouse cursor
     */
    public IllarionMouseCursor(final Sprite sprite, final int hotspotX,
        final int hotspotY) {
        internalCursor =
            Graphics.getInstance().getMouseCursor(sprite, hotspotX, hotspotY);
        
        if (internalCursor == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Disable the mouse cursor.
     */
    public void disable() {
        internalCursor.disableCursor();
    }

    /**
     * Free the resources used by this mouse cursor.
     */
    @Override
    public void dispose() {
        // nothing to do for now
    }

    /**
     * Active the mouse cursor and prepare it for rendering.
     */
    public void enable() {
        internalCursor.enableCursor();
    }

    /**
     * Update the mouse cursor. That will cause the mouse cursor to be drawn and
     * should be done at the very end of the frame display.
     */
    public void update() {
        internalCursor.update();
    }

}
