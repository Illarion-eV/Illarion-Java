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

import de.lessvoid.nifty.slick2d.input.ForwardingInputSystem;

/**
 * This event is published when a click operation on the map was noted.
 *
 * @author Vilarion &lt;vilarion@illarion.org&gt;
 */
public final class ClickOnMapEvent {
    /**
     * The x coordinate on the screen where the click occurred.
     */
    private final int x;

    /**
     * The y coordinate on the screen where the click occurred.
     */
    private final int y;

    /**
     * The controls used to override the default forwarding behaviour of the Slick renderer.
     */
    private final ForwardingInputSystem forwardingControl;

    /**
     * Create and initialize such an event.
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @param inputForwardingControl the control class to change the forwarding behaviour
     */
    public ClickOnMapEvent(final int x, final int y, final ForwardingInputSystem inputForwardingControl) {
        this.x = x;
        this.y = y;
        forwardingControl = inputForwardingControl;
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

    /**
     * Get the input forwarding control that applies to the input event source this event originates from.
     *
     * @return the forwarding control
     */
    public ForwardingInputSystem getForwardingControl() {
        return forwardingControl;
    }
}
