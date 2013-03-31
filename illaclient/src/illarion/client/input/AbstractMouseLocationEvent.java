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

import org.illarion.engine.graphic.SceneEvent;

import javax.annotation.Nonnull;

/**
 * This event is in general triggered in case the user performs any move or action with the mouse on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractMouseLocationEvent implements SceneEvent {
    /**
     * The x coordinate on the screen where the click occurred.
     */
    private final int x;

    /**
     * The y coordinate on the screen where the click occurred.
     */
    private final int y;

    /**
     * Create and initialize such an event.
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    protected AbstractMouseLocationEvent(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The copy constructor.
     *
     * @param org the original object that is copied
     */
    protected AbstractMouseLocationEvent(@Nonnull final AbstractMouseLocationEvent org) {
        x = org.x;
        y = org.y;
    }

    /**
     * Get the x coordinate on the screen where the click occurred.
     *
     * @return the x coordinate of the click
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinate on the screen where the click occurred.
     *
     * @return the y coordinate of the click
     */
    public int getY() {
        return y;
    }
}
