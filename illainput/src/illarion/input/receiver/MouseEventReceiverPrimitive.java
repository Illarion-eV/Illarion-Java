/*
 * This file is part of the Illarion Input Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Input Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Input Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Input Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.input.receiver;

/**
 * This mouse event receiver is used to receive mouse events. The events that
 * get transfered are limited to a very limited level.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface MouseEventReceiverPrimitive extends MouseEventReceiver {
    /**
     * This function receives the primitive mouse events.
     * 
     * @param mouseX the x coordinate where the mouse is currently
     * @param mouseY the y coordinate where the mouse is currently
     * @param wheelDelta the delta the wheel got changed by
     * @param button the button of the mouse that was used
     * @param buttonDown <code>true</code> in case 
     * @return <code>true</code> in case the event got consumed
     */
    boolean handleMouseEvent(int mouseX, int mouseY, int wheelDelta, int button, boolean buttonDown);
}
