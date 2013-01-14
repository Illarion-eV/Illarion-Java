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

import org.newdawn.slick.Input;

import javax.annotation.Nonnull;

/**
 * Create a mouse event that is marking the current mouse location.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CurrentMouseLocationEvent extends AbstractMouseLocationEvent {
    /**
     * Create and initialize such an event.
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    public CurrentMouseLocationEvent(final int x, final int y) {
        super(x, y);
    }

    /**
     * Create and initialize such an event.
     *
     * @param input the input handler supplying the data
     */
    public CurrentMouseLocationEvent(@Nonnull final Input input) {
        super(input.getMouseX(), input.getMouseY());
    }
}
