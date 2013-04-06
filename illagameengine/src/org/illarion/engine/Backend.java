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

import javax.annotation.Nullable;

/**
 * This enumerator contains the backend definitions that are present in the engine.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum Backend {
    /**
     * The Slick2D backend.
     */
    Slick2D("org.illarion.engine.backend.slick.ApplicationGameContainer"),

    /**
     * The libGDX backend.
     */
    @SuppressWarnings("EnumeratedConstantNamingConvention")
    libGDX("org.illarion.engine.backend.gdx.ApplicationGameContainer");

    /**
     * The class that stores the desktop container.
     */
    @Nullable
    private final String desktopContainerClass;

    /**
     * Create a new backend definition and set the required class references.
     *
     * @param desktopContainerClass the class used to create a desktop game
     */
    Backend(@Nullable final String desktopContainerClass) {
        this.desktopContainerClass = desktopContainerClass;
    }

    /**
     * Get the name of the class used to create desktop games.
     *
     * @return the desktop game class name or {@code null} in case there is no such class for this backend
     */
    @Nullable
    public String getDesktopContainerClass() {
        return desktopContainerClass;
    }
}
