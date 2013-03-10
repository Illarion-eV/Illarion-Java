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
package org.illarion.engine;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This is a special implementation of the game container that targets desktop application. It offers a few
 * additional values to set.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public interface DesktopGameContainer extends GameContainer {
    /**
     * Set the application icons of this game container. This function has no effect in case the game container does
     * not support icons.
     *
     * @param icons the icons (in different sizes) to load as application icons
     */
    void setIcons(@Nonnull String[] icons);

    /**
     * Set the title of the game. This text is displayed in the title bar of the application.
     *
     * @param title the title of the game
     */
    void setTitle(@Nonnull String title);

    /**
     * Set the size of the window.
     *
     * @param width  the width of the window
     * @param height the height of the window
     * @throws EngineException in case setting the size of the window fails
     */
    void setSize(int width, int height) throws EngineException;

    /**
     * Check if the user is allowed to change the size of the window.
     *
     * @return {@code true} in case the window is resizeable
     */
    boolean isResizeable();

    /**
     * Set the resizeable flag of the window.
     *
     * @param resizeable {@code true} to allow the user to change the size of the window
     * @throws EngineException in case switching the resizeable value fails
     */
    void setResizeable(boolean resizeable) throws EngineException;

    /**
     * Check if this container is currently running in full screen mode.
     *
     * @return {@code true} in case the window is currently running in full screen mode
     */
    boolean isFullScreen();

    /**
     * Set the full screen mode of this window.
     *
     * @param fullScreen {@code true} to enter the full screen mode
     * @throws EngineException in case switching the full screen mode fails
     */
    void setFullScreen(boolean fullScreen) throws EngineException;
}
