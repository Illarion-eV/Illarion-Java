/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.input;

import illarion.common.util.Stoppable;

import illarion.input.InputManager;

/**
 * This class takes care for handling all input to the map screen.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class InputHandler implements Stoppable {
    /**
     * The singleton instance of this class.
     */
    private static final InputHandler INSTANCE = new InputHandler();

    /**
     * Private constructor to avoid the creation of any instances but the
     * singleton instance.
     */
    private InputHandler() {
        // nothing to be done
    }

    /**
     * Get the singleton instance of this input handler.
     * 
     * @return the singleton instance of the input handler
     */
    public static InputHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Stop the Input handler correctly ans stop the capturing of the input to
     * the map display.
     */
    @Override
    public void saveShutdown() {
        InputManager.getInstance().getKeyboardManager().shutdown();
        InputManager.getInstance().getMouseManager().shutdown();
    }

    /**
     * Start the input handler. This prepares the capturing of the input to the
     * map display correctly.
     */
    public synchronized void start() {
        InputManager.getInstance().getKeyboardManager().startManager();
        InputManager.getInstance().getMouseManager().startManager();

        InputManager.getInstance().getMouseManager()
            .registerEventHandler(new MouseHandler());
    }
}
