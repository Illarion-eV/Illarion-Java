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
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This is the container that is displaying the game. The implementing class can realize this container in any
 * fitting way. Could be a Swing Window, a native window, a canvas component or a applet.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public interface GameContainer {
    /**
     * Get the height of the container.
     *
     * @return the height of the container
     */
    int getHeight();

    /**
     * Get the width of the container.
     *
     * @return the width of the container
     */
    int getWidth();

    /**
     * Get the backing engine that is used to operate the capabilities of the container.
     *
     * @return the backing engine
     */
    @Nonnull
    Engine getEngine();

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
     * Set the mouse cursor that should be applied to the mouse.
     *
     * @param cursor the cursor to display or {@code null} to revert to the default cursor
     */
    void setMouseCursor(@Nullable MouseCursor cursor);
}
