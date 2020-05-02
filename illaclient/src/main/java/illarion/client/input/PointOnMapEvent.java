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
 * This event is published when the mouse is pointing on the map. This means that the event is only invoked in case
 * the mouse rests in one place for some time.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class PointOnMapEvent extends AbstractMouseLocationEvent {
    /**
     * Create and initialize such an event.
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    public PointOnMapEvent(int x, int y) {
        super(x, y);
    }

    /**
     * Create and initialize such an event.
     */
    public PointOnMapEvent() {
        super(0, 0);
    }

    /**
     * Initialize such an event.
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    public void set (int x, int y) {
        super.set(x, y);
    }
}