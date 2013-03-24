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
 * This event is in general triggered in case the user performs any action with the mouse on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractMouseOnMapEvent extends AbstractMouseLocationEvent {
    /**
     * The mouse key that was clicked.
     */
    private final Button key;

    /**
     * Create and initialize such an event.
     *
     * @param key the mouse key that was clicked
     * @param x   the x coordinate of the click
     * @param y   the y coordinate of the click
     */
    protected AbstractMouseOnMapEvent(final Button key, final int x, final int y) {
        super(x, y);
        this.key = key;
    }

    /**
     * The copy constructor.
     *
     * @param org the original object to copy
     */
    protected AbstractMouseOnMapEvent(@Nonnull final AbstractMouseOnMapEvent org) {
        super(org);
        key = org.key;
    }

    /**
     * Get the key that was clicked on the mouse.
     *
     * @return the key that was clicked
     */
    public Button getKey() {
        return key;
    }
}
