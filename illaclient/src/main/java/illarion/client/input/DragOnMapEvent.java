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

    @Nonnull
    private final InputReceiver inputReceiver;

    private final boolean startDragging;

    /**
     * Create and initialize such a event.
     *
     * @param startX the X coordinate where the dragging starts
     * @param startY the Y coordinate where the dragging starts
     * @param stopX the X coordinate where the dragging is currently
     * @param stopY the Y coordinate where the dragging is currently
     * @param pressedKey the key used for the dragging operation
     */
    public DragOnMapEvent(
            int startX, int startY, int stopX, int stopY, @Nonnull Button pressedKey, boolean firstEvent,
            @Nonnull InputReceiver receiver) {
        super(pressedKey, stopX, stopY);
        oldX = startX;
        oldY = startY;
        inputReceiver = receiver;
        startDragging = firstEvent;
    }

    public DragOnMapEvent(@Nonnull DragOnMapEvent org) {
        super(org);
        oldX = org.oldX;
        oldY = org.oldY;
        inputReceiver = org.inputReceiver;
        startDragging = org.isStartDragging();

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

    @Nonnull
    public InputReceiver getInputReceiver() {
        return inputReceiver;
    }

    /**
     * This is set {@code true} in case this is the first event of this drag.
     */
    public boolean isStartDragging() {
        return startDragging;
    }
}
