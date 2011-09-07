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

import illarion.input.KeyboardEvent;

/**
 * This interface adds the function to classes to receive keyboard events.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface KeyboardEventReceiverComplex extends KeyboardEventReceiver {
    /**
     * This function is called with the generated event.
     * 
     * @param event the keyboard event that is handed over in this run
     * @return <code>true</code> in case the event was handled and is not
     *         supposed to be send to any further event receivers
     */
    boolean handleKeyboardEvent(KeyboardEvent event);
}
