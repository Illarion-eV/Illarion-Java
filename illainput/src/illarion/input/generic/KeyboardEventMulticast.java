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

import illarion.input.KeyboardEvent;
import illarion.input.KeyboardEventReceiver;

/**
 * This class is a multicast class for Keyboard events. It allows multiple
 * receiver to be handled.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class KeyboardEventMulticast implements KeyboardEventReceiver {
    /**
     * The first receiver to receive the event.
     */
    private final KeyboardEventReceiver receiver1;
    
    /**
     * The second receiver to receive the event.
     */
    private final KeyboardEventReceiver receiver2;
    
    /**
     * Create a new event multicast.
     * 
     * @param r1 the receiver to receive the event first
     * @param r2 the receiver to receive the event second
     */
    public KeyboardEventMulticast(final KeyboardEventReceiver r1, final KeyboardEventReceiver r2) {
        receiver1 = r1;
        receiver2 = r2;
    }

    @Override
    public boolean handleKeyboardEvent(KeyboardEvent event) {
        boolean result;
        result = receiver1.handleKeyboardEvent(event);
        if (!result) {
            result = receiver2.handleKeyboardEvent(event);
        }
        return result;
    }

}
