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
package illarion.input.generic;

import illarion.input.MouseEvent;
import illarion.input.receiver.MouseEventReceiver;
import illarion.input.receiver.MouseEventReceiverComplex;
import illarion.input.receiver.MouseEventReceiverPrimitive;

/**
 * This class is a multicast class for mouse events. It allows multiple receiver
 * to be handled.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class MouseEventMulticast implements MouseEventReceiverComplex, MouseEventReceiverPrimitive {
    /**
     * The first receiver to receive the event.
     */
    private final MouseEventReceiver receiver1;

    /**
     * The second receiver to receive the event.
     */
    private final MouseEventReceiver receiver2;

    /**
     * Create a new event multicast.
     * 
     * @param r1 the receiver to receive the event first
     * @param r2 the receiver to receive the event second
     */
    public MouseEventMulticast(final MouseEventReceiver r1,
        final MouseEventReceiver r2) {
        receiver1 = r1;
        receiver2 = r2;
    }

    @Override
    public boolean handleMouseEvent(final MouseEvent event) {
        boolean result;
        result = handleComplexEvent(receiver1, event);
        if (!result) {
            result = handleComplexEvent(receiver2, event);
        }
        return result;
    }
    
    private static boolean handleComplexEvent(final MouseEventReceiver receiver, final MouseEvent event) {
        if (receiver instanceof MouseEventReceiverComplex) {
            return ((MouseEventReceiverComplex) receiver).handleMouseEvent(event);
        }
        return false;
    }

    @Override
    public boolean handleMouseEvent(final int mouseX, final int mouseY, final int wheelDelta,
        final int button, final boolean buttonDown) {
        boolean result;
        result = handlePrimitiveEvent(receiver1, mouseX, mouseY, wheelDelta, button, buttonDown);
        if (!result) {
            result = handlePrimitiveEvent(receiver2, mouseX, mouseY, wheelDelta, button, buttonDown);
        }
        return result;
    }

    private static boolean handlePrimitiveEvent(final MouseEventReceiver receiver, final int mouseX, final int mouseY, final int wheelDelta,
        final int button, final boolean buttonDown) {
        if (receiver instanceof MouseEventReceiverPrimitive) {
            return ((MouseEventReceiverPrimitive) receiver).handleMouseEvent(mouseX, mouseY, wheelDelta, button, buttonDown);
        }
        return false;
    }
}
