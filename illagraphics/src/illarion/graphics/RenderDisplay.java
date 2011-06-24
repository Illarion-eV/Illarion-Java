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
package illarion.graphics;

import java.awt.Component;

/**
 * A render display controls the Display related settings and checks the
 * possible options of the display.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface RenderDisplay {
    /**
     * Add a listener the active display.
     * 
     * @param listener the listener to add to this display
     */
    void addInputListener(Object listener);

    /**
     * Apply a global offset to all following render actions.
     * 
     * @param offX the global x offset
     * @param offY the global y offset
     */
    void applyOffset(float offX, float offY);

    /**
     * Add a scaling factor to the display. All graphics are scaled with this
     * value.
     * 
     * @param scale new scaling value
     */
    void applyScaling(float scale);

    /**
     * Get the height of the render display.
     * 
     * @return the height of the render display in pixel
     */
    int getHeight();

    /**
     * Get the amount of objects drawn since the last call of that function.
     * 
     * @return the amount of objects drawn
     */
    int getObjects();

    /**
     * Get a list of resolutions that are supported by the render display.
     * 
     * @return a list of strings describing the display mode
     */
    GraphicResolution[] getPossibleResolutions();

    /**
     * Get the render area that can be included to a display window.
     * 
     * @return the {@link java.awt.Component} that can be included into a java
     *         gui window and is the render target in windowed mode.
     */
    Component getRenderArea();

    /**
     * Get the width of the render display.
     * 
     * @return the width of the render display in pixel
     */
    int getWidth();

    /**
     * Hide the system default cursor while its over the display component.
     */
    void hideCursor();

    /**
     * Check if the display is currently in fullscreen mode.
     * 
     * @return true in case the display is currently fullscreen
     */
    boolean isFullscreen();

    /**
     * Check if a listener is supported by this render display.
     * 
     * @param listener the listener to test
     * @return <code>true</code> if its fine to use this input listener
     */
    boolean isInputListenerSupported(Object listener);

    /**
     * Remove a listener the active display.
     * 
     * @param listener the listener to add to this display
     */
    void removeInputListener(Object listener);

    /**
     * Reset the last applied global offset.
     */
    void resetOffset();

    /**
     * Reset a scaling value that was applied before.
     */
    void resetScaling();

    /**
     * Limit the render area to a specified area. After this function is called
     * the render origin 0 0 is located in the lower left edge of this area.
     * Rendering outside of this area is not possible.
     * <p>
     * <b>Important:</b> Calling this function twice results that the second
     * rectangle is reduced to the intersection of the current and the new
     * rectangle. Disabling the area limit using {@link #unsetAreaLimit()} will
     * cause a rollback to the last limit then and will not remove the limit
     * entirely.
     * 
     * @param x the x coordinate of the location of the rectangle defining the
     *            new render area
     * @param y the y coordinate of the location of the rectangle defining the
     *            new render area
     * @param width the width of the render area
     * @param height the height of the render area
     */
    void setAreaLimit(int x, int y, int width, int height);

    /**
     * Set the used screen resolution.
     * 
     * @param resolution the display settings to be used to render the screen.
     */
    void setDisplayMode(GraphicResolution resolution);

    /**
     * Enable a display mode. The index is the index within the list that is
     * received by {@link #getPossibleResolutions()}.
     * 
     * @param index the index of the display mode in the list
     */
    void setDisplayMode(int index);

    /**
     * Set a option value to this render display. This will effect the way the
     * render display operates.
     * 
     * @param key the key of the settings
     * @param value the value of the settings
     */
    void setDisplayOptions(String key, String value);

    /**
     * Display the system default cursor while its over the display component.
     */
    void showCursor();

    /**
     * Shutting down the display results in disposing the OpenGL render context.
     * Only do this in case you are sure that you don't need that anymore.
     */
    void shutdown();

    /**
     * This function needs to be called right before the rendering starts.
     */
    void startRendering();

    /**
     * This function needs to be called to stop the rendering. After calling
     * this function no rendering is possible anymore.
     */
    void stopRendering();

    /**
     * Check if the Display implementation supports fullscreen or not.
     * 
     * @return true if fullscreen is supported
     */
    boolean supportsFullscreen();

    /**
     * Switch between fullscreen mode and windowed mode. That works only in case
     * {@link #supportsFullscreen()} returns true.
     */
    void toogleFullscreen();

    /**
     * Remove a previous set limit of a render area.
     */
    void unsetAreaLimit();
}
