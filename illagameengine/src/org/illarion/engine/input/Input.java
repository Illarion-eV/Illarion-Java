/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.input;

import javax.annotation.Nonnull;

/**
 * This is the interface of the input implementation.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Input {
    /**
     * This function should be called at least once during the updates to forward the input events to the application.
     */
    void poll();

    /**
     * Set the listener that is supposed to receive the input events.
     *
     * @param listener the input event listener
     */
    void setListener(@Nonnull InputListener listener);

    /**
     * Check if the specified button is currently pressed down.
     *
     * @param button the button
     * @return {@code true} if the button is currently down
     */
    boolean isButtonDown(@Nonnull Button button);

    /**
     * Check if the key is currently pressed down.
     *
     * @param key the key
     * @return {@code true} in case the key is pressed
     */
    boolean isKeyDown(@Nonnull Key key);

    /**
     * Get the X coordinate of the current mouse location.
     *
     * @return the X coordinate of the current mouse location
     */
    int getMouseX();

    /**
     * Get the Y coordinate of the current mouse location.
     *
     * @return the Y coordinate of the current mouse location
     */
    int getMouseY();

    /**
     * Set the location of the mouse cursor.
     *
     * @param x the x coordinate of the mouse cursor
     * @param y the y coordinate of the mouse cursor
     */
    void setMouseLocation(int x, int y);
}
