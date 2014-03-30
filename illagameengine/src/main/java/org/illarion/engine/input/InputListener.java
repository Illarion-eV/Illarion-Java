/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package org.illarion.engine.input;

import javax.annotation.Nonnull;

/**
 * This interface defines a input listener that should be attached to the input interface. It will receive the
 * updates of the keys the input system received.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface InputListener {
    /**
     * This function is called for key down events. In case a button is pressed such a event is generated.
     *
     * @param key the key that is pressed
     */
    void keyDown(@Nonnull Key key);

    /**
     * This function is called for key up events. In case a button that was pressed down is released,
     * such a event is generated.
     *
     * @param key the key that is released
     */
    void keyUp(@Nonnull Key key);

    /**
     * This even is generated in case a button is typed. This is only triggered for buttons that generate a valid
     * character upon typing. These events are used to handle text input.
     *
     * @param character the character assigned to the key that was typed
     */
    void keyTyped(char character);

    /**
     * This event is generated in case a mouse button is pressed down.
     *
     * @param mouseX the X coordinate of the mouse cursor when this event occurred
     * @param mouseY the y coordinate of the mouse cursor when this event occurred
     * @param button the button that is pressed
     */
    void buttonDown(int mouseX, int mouseY, @Nonnull Button button);

    /**
     * This event is generated in case a mouse button is released.
     *
     * @param mouseX the X coordinate of the mouse cursor when this event occurred
     * @param mouseY the y coordinate of the mouse cursor when this event occurred
     * @param button the button that is released
     */
    void buttonUp(int mouseX, int mouseY, @Nonnull Button button);

    /**
     * This event is generated in case the mouse button is clicked (so pressed and released right after).
     * <p/>
     * Be aware that even double clicks fire first a single click and after this the double click.
     *
     * @param mouseX the X coordinate of the mouse cursor when this event occurred
     * @param mouseY the y coordinate of the mouse cursor when this event occurred
     * @param button the button that was clicked
     * @param count the amount of clicks that occurred
     */
    void buttonClicked(int mouseX, int mouseY, @Nonnull Button button, int count);

    /**
     * This event is generated in case the mouse is moved without any buttons pressed down.
     *
     * @param mouseX the X coordinate of the new mouse location
     * @param mouseY the Y coordinate of the new mouse location
     */
    void mouseMoved(int mouseX, int mouseY);

    /**
     * This event is generated in case the mouse is moved a button pressed down.
     *
     * @param button the button that was down during the dragging operation
     * @param fromX the X coordinate of the mouse where the drag started
     * @param fromY the Y coordinate of the mouse where the drag started
     * @param toX the X coordinate of the new mouse location
     * @param toY the Y coordinate of the new mouse location
     */
    void mouseDragged(@Nonnull Button button, int fromX, int fromY, int toX, int toY);

    /**
     * This event is generated in case the mouse wheel was moved.
     *
     * @param mouseX the current X coordinate of the mouse location
     * @param mouseY the current Y coordinate of the mouse location
     * @param delta the delta of the wheel movement
     */
    void mouseWheelMoved(int mouseX, int mouseY, int delta);
}
