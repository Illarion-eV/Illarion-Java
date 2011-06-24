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
 * This class is able to store the data that is fetched from the mouse and yet
 * not processed. This is done to avoid that the mouse manager needs the LWJGL
 * global lock.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
final class MouseData {
    /**
     * The lock that secures the buffer against access by multiple threads that
     * access.
     */
    public static final Object LOCK = new Object();

    /**
     * The buffer that stores all unused instances of this class.
     */
    private static final FastList<MouseData> BUFFER =
        new FastList<MouseData>();

    /**
     * The button that was used.
     */
    private int button;

    /**
     * The delta value of the mouse wheel.
     */
    private int delta;

    /**
     * The state if the button was pressed down or not.
     */
    private boolean pressed;

    /**
     * The x coordinate of this mouse event.
     */
    private int x;

    /**
     * The y coordinate of this mouse event.
     */
    private int y;

    /**
     * Private constructor. New instances only by the get method.
     */
    private MouseData() {
        // nothing to do
    }

    /**
     * Get a new instance of this object. This method is NOT thread save. Get
     * the needed lock using the lock method first.
     * 
     * @return a new instance of the mouse data or a old one from the buffer.
     */
    public static MouseData get() {
        if (BUFFER.isEmpty()) {
            return new MouseData();
        }
        return BUFFER.removeFirst();
    }

    /**
     * Get the button used at this mouse event.
     * 
     * @return the code of the button used at this mouse event
     */
    public int getButton() {
        return button;
    }

    /**
     * Get the delta of the mouse wheel of this mouse event.
     * 
     * @return the delta of this mouse wheel of the mouse event
     */
    public int getDelta() {
        return delta;
    }

    /**
     * Get the x coordinate of this mouse event.
     * 
     * @return the x coordinate of the mouse event
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinate of this mouse event.
     * 
     * @return the y coordinate of the mouse event
     */
    public int getY() {
        return y;
    }

    /**
     * Check if the button was pressed or released.
     * 
     * @return <code>true</code> in case it was pressed down
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     * Put the data object back into the recycler.
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
     * @param newX the x coordinate of the mouse event stored
     * @param newY the y coordinate of the mouse event stored
     * @param newDelta the delta of the mouse wheel of the mouse event stored
     * @param newButton the button pressed during this mouse event
     * @param newPressed <code>true</code> in case the button was pressed down
     */
    public void setData(final int newX, final int newY, final int newDelta,
        final int newButton, final boolean newPressed) {
        x = newX;
        y = newY;
        delta = newDelta;
        button = newButton;
        pressed = newPressed;
    }
}
