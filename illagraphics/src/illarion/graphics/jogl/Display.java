/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.jogl;

import javax.media.opengl.GLAutoDrawable;

import illarion.graphics.RenderTask;

/**
 * This display is the interface for the different display implementations.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public interface Display {
    /**
     * Add a input listener to this display.
     * 
     * @param listener the listener to add to this display
     */
    void addInputListener(Object listener);

    /**
     * Get the openGL drawable to render on this display.
     * 
     * @return the GLAutoDrawable instance
     */
    GLAutoDrawable getDrawable();

    /**
     * Check if a input listener is valid to be used on this display.
     * 
     * @param listener the listener to test
     * @return <code>true</code> in case this listener is valid to be used
     */
    boolean isInputListenerSupported(Object listener);

    /**
     * Remove a input listener to this display.
     * 
     * @param listener the listener to remove to this display
     */
    void removeInputListener(Object listener);

    /**
     * Render a task in the fitting thread.
     * 
     * @param task the task to render
     */
    void renderTask(RenderTask task);

    /**
     * Set the frames per second the display should be shown with.
     * 
     * @param fps the target frames per second
     */
    void setFPS(int fps);

    /**
     * This function is supposed to shutdown the display so all operations of
     * this display stop and the resources are freed. This is required to
     * shutdown a render context properly.
     */
    void shutdown();
}
