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
package illarion.input;

/**
 * The mouse manager that handles the mouse inputs and allows to register mouse
 * events that are fired then. Also its allows to check the current position of
 * the mouse and the state of the keys. Its needed to allow this manager to
 * update its state at each render run.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface MouseManager {
    /**
     * Clean up the mouse event manager and remove all mouse handlers by this.
     */
    void clear();

    /**
     * Get the x coordinate of the position of the mouse pointer at the last
     * update of the mouse handler.
     * 
     * @return the x coordinate of the position of the mouse pointer
     */
    int getMousePosX();

    /**
     * Get the y coordinate of the position of the mouse pointer at the last
     * update of the mouse handler.
     * 
     * @return the y coordinate of the position of the mouse pointer
     */
    int getMousePosY();

    /**
     * Check if one key is pressed down. The state of the key at the last update
     * is taken into account.
     * 
     * @param key the id of the key that shall be checked
     * @return <code>true</code> in case the key is pressed down
     */
    boolean isKeyDown(int key);

    /**
     * Check if one key is not pressed down. The state of the key at the last
     * update is taken into account.
     * 
     * @param key the id of the key that shall be checked
     * @return <code>true</code> in case the key is <b>not</b> pressed down
     */
    boolean isKeyUp(int key);

    /**
     * Register a mouse event handler to the manager. At each change of the
     * mouse states this handler will be notified about the change.
     * 
     * @param event the event handler that shall be notified
     */
    void registerEventHandler(MouseEventReceiver event);

    /**
     * Stop the mouse manager. This causes that all mouse handlers are removed
     * and the mouse manager becomes <b>unusable</b>. Only do this in case the
     * mouse is not used anymore at all.
     */
    void shutdown();

    /**
     * Start the mouse manager. This prepares that the mouse handler is prepared
     * for usage. The window in focus is the reference of this mouse handler so
     * ensure this is called while the correct window is active.
     */
    void startManager();
}
