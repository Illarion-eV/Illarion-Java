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

/**
 * This event is published when the mouse is moved on the map.
 *
 * @author Vilarion &lt;vilarion@illarion.org&gt;
 */
public final class MoveOnMapEvent extends AbstractMouseLocationEvent {
    /**
     * Create and initialize such an event.
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    public MoveOnMapEvent(int x, int y) {
        super(x, y);
    }

    /**
     * Create and initialize such an event.
     */
    public MoveOnMapEvent() {
        super(0, 0);
    }

    /**
     * Initialize such an event.
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    @Override
    public void set (int x, int y) {
        super.set(x, y);
    }

}