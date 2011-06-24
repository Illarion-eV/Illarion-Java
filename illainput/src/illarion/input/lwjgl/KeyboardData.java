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
package illarion.input.lwjgl;

import javolution.util.FastList;

/**
 * This class is able to fetch the data received from the keyboard until its
 * processed by the keyboard manager. That is done to avoid that the keyboard
 * manager thread needs the global lock of LWJGL.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
final class KeyboardData {
    /**
     * The lock that secures the buffer against access by multiple threads that
     * access.
     */
    public static final Object LOCK = new Object();

    /**
     * The buffer that stores all unused instances of this class.
     */
    private static final FastList<KeyboardData> BUFFER =
        new FastList<KeyboardData>();

    /**
     * The character assigned to this keyboard event.
     */
    private char character;

    /**
     * The code of the key that was pressed at this keyboard event.
     */
    private int key;

    /**
     * <code>true</code> in case this event is a repeated one.
     */
    private boolean repeated;

    /**
     * The state of the key during this event (pressed or not pressed).
     */
    private boolean state;

    /**
     * Private constructor. New instances only by the get method.
     */
    private KeyboardData() {
        // nothing to do
    }

    /**
     * Get a new instance of this object. This method is NOT thread save. Get
     * the needed lock using the lock method first.
     * 
     * @return a new instance of the mouse data or a old one from the buffer.
     */
    public static KeyboardData get() {
        if (BUFFER.isEmpty()) {
            return new KeyboardData();
        }
        return BUFFER.removeFirst();
    }

    /**
     * Get the character of this keyboard event.
     * 
     * @return the character of this keyboard event
     */
    public char getCharacter() {
        return character;
    }

    /**
     * Get the key of this keyboard event.
     * 
     * @return the code of the key of this keyboard event
     */
    public int getKey() {
        return key;
    }

    /**
     * Get the state of the key at this event.
     * 
     * @return <code>true</code> if the button was pressed down
     */
    public boolean getState() {
        return state;
    }

    /**
     * Get if the event was a repeated one.
     * 
     * @return <code>true</code> if this element got fired at least twice
     */
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * Put the element back into the buffer.
     */
    public void recycle() {
        synchronized (LOCK) {
            BUFFER.addLast(this);
        }
    }

    /**
     * Set this data element so it contains all needed data for further
     * operations.
     * 
     * @param newKey the code of the key used at this keyboard event
     * @param newState the state of the key (pressed or not)
     * @param newRepeated the repeated flag of the keyboard event
     * @param newChar the character assigned to this keyboard event
     */
    public void set(final int newKey, final boolean newState,
        final boolean newRepeated, final char newChar) {
        key = newKey;
        state = newState;
        repeated = newRepeated;
        character = newChar;
    }
}
