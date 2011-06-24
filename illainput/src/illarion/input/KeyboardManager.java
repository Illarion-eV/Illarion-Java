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
 * The keyboard manager that handles the keyboard inputs and allows to register
 * Keyboard events that are fired then. Also its allows to check the current
 * state of each key. Its needed to allow this manager to update its state at
 * each render run.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface KeyboardManager {
    /**
     * Clean up the keyboard event manager and remove all keyboard handlers by
     * this.
     */
    void clear();

    /**
     * Check if one key is pressed down. The state of the key at the last update
     * is taken into account.
     * 
     * @param key the key that shall checked if its down or not
     * @return <code>true</code> in case the key is pressed down
     */
    boolean isKeyDown(int key);

    /**
     * Check if one key is not pressed down. The state of the key at the last
     * update is taken into account.
     * 
     * @param key the key that shall checked if its down or not
     * @return <code>true</code> in case the key is not pressed down
     */
    boolean isKeyUp(int key);

    /**
     * Register a keyboard event handler to the manager. At each change of the
     * keyboard states this handler will be notified about the change.
     * 
     * @param event the event handler that shall be notified
     */
    void registerEventHandler(KeyboardEventReceiver event);

    /**
     * Stop the keyboard manager. This causes that all keyboard handlers are
     * removed and the keyboard manager becomes <b>unusable</b>. Only do this in
     * case the keyboard is not used anymore at all.
     */
    void shutdown();

    /**
     * Start the keyboard manager. This prepares that the keyboard handler is
     * prepared for usage. The window in focus is the reference of this keyboard
     * handler so ensure this is called while the correct window is active.
     */
    void startManager();
}
