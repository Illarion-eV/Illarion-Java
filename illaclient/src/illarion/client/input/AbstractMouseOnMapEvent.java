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
 * This event is in general triggered in case the user performs any action with the mouse on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractMouseOnMapEvent extends AbstractMouseLocationEvent {
    /**
     * The mouse key that was clicked.
     */
    private final int key;

    /**
     * The controls used to override the default forwarding behaviour of the Slick renderer.
     */
    private final ForwardingInputSystem forwardingControl;

    /**
     * Create and initialize such an event.
     *
     * @param key                    the mouse key that was clicked
     * @param x                      the x coordinate of the click
     * @param y                      the y coordinate of the click
     * @param inputForwardingControl the control class to change the forwarding behaviour
     */
    protected AbstractMouseOnMapEvent(final int key, final int x, final int y, final ForwardingInputSystem
            inputForwardingControl) {
        super(x, y);
        this.key = key;
        forwardingControl = inputForwardingControl;
    }

    /**
     * The copy constructor.
     *
     * @param org the original object to copy
     */
    protected AbstractMouseOnMapEvent(final AbstractMouseOnMapEvent org) {
        super(org);
        key = org.key;
        forwardingControl = org.forwardingControl;
    }

    /**
     * Get the key that was clicked on the mouse.
     *
     * @return the key that was clicked
     */
    public int getKey() {
        return key;
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
