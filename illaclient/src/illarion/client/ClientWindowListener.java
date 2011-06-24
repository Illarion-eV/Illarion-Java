/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Listener of the Client window that fetches all events the Client window gets.
 * 
 * @author Martin Karing
 * @since 1.22
 */
final class ClientWindowListener implements WindowListener {
    /**
     * Default constructor of this Window Listener.
     */
    public ClientWindowListener() {
        super();
    }

    /**
     * Invoked when the Window is set to be the active Window.
     * 
     * @param e the window event that that occurred that caused the calling of
     *            this function
     */
    @Override
    public void windowActivated(final WindowEvent e) {
        ClientWindow.getInstance().focus();
    }

    /**
     * Invoked when a window has been closed.
     * 
     * @param e the window event that that occurred that caused the calling of
     *            this function
     */
    @Override
    public void windowClosed(final WindowEvent e) {
        // window is closed, nothing more to do.
    }

    /**
     * Invoked when the user attempts to close the window.
     * 
     * @param e the window event that that occurred that caused the calling of
     *            this function
     */
    @Override
    public void windowClosing(final WindowEvent e) {
        IllaClient.ensureExit();
    }

    /**
     * Invoked when a Window is no longer the active Window.
     * 
     * @param e the window event that that occurred that caused the calling of
     *            this function
     */
    @Override
    public void windowDeactivated(final WindowEvent e) {
        // user chose to disable the window, do nothing
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     * 
     * @param e the window event that that occurred that caused the calling of
     *            this function
     */
    @Override
    public void windowDeiconified(final WindowEvent e) {
        ClientWindow.getInstance().focus();
    }

    /**
     * Invoked when a window is changed from a normal to a minimized state.
     * 
     * @param e the window event that that occurred that caused the calling of
     *            this function
     */
    @Override
    public void windowIconified(final WindowEvent e) {
        // user minimized the window, do nothing
    }

    /**
     * Invoked the first time a window is made visible.
     * 
     * @param e the window event that that occurred that caused the calling of
     *            this function
     */
    @Override
    public void windowOpened(final WindowEvent e) {
        // window got created, the other application parts handle all needed
        // things for this event
    }

}
