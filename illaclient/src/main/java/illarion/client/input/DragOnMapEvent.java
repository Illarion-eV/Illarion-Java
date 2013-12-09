/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.input;

import org.illarion.engine.input.Button;

import javax.annotation.Nonnull;

/**
 * This event is published in case a dragging operation on the map was noted.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class DragOnMapEvent extends AbstractMouseOnMapEvent {
    /**
     * The X coordinate on the screen where the dragging operation started.
     */
    private final int oldX;

    /**
     * The Y coordinate on the screen where the dragging operation started.
     */
    private final int oldY;

    /**
     * Create and initialize such a event.
     *
     * @param startX     the X coordinate where the dragging starts
     * @param startY     the Y coordinate where the dragging starts
     * @param stopX      the X coordinate where the dragging is currently
     * @param stopY      the Y coordinate where the dragging is currently
     * @param pressedKey the key used for the dragging operation
     */
    public DragOnMapEvent(final int startX, final int startY, final int stopX, final int stopY,
                          final Button pressedKey) {
        super(pressedKey, stopX, stopY);
        oldX = startX;
        oldY = startY;
    }

    public DragOnMapEvent(@Nonnull final DragOnMapEvent org) {
        super(org);
        oldX = org.oldX;
        oldY = org.oldY;
    }

    /**
     * Get the X coordinate of the location there the mouse was located before the dragging operation.
     *
     * @return the X coordinate of the starting location
     */
    public int getOldX() {
        return oldX;
    }

    /**
     * Get the Y coordinate of the location there the mouse was located before the dragging operation.
     *
     * @return the Y coordinate of the starting location
     */
    public int getOldY() {
        return oldY;
    }

    /**
     * Get the X coordinate of the location there the mouse was located after the dragging operation.
     *
     * @return the X coordinate of the current location
     */
    public int getNewX() {
        return getX();
    }

    /**
     * Get the Y coordinate of the location there the mouse was located after the dragging operation.
     *
     * @return the Y coordinate of the current location
     */
    public int getNewY() {
        return getY();
    }
}
