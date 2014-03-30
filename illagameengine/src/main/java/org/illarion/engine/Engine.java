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
package org.illarion.engine;

import org.illarion.engine.assets.Assets;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.input.Input;
import org.illarion.engine.sound.Sounds;

import javax.annotation.Nonnull;

/**
 * This interfaces defines the access to the actual game engine elements. This interface is implemented by the
 * different library dependant implementations, providing unified access to all the implementations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Engine {
    /**
     * Get the graphics component of the engine.
     *
     * @return the graphics component
     */
    @Nonnull
    Graphics getGraphics();

    /**
     * Get the sounds component of the engine.
     *
     * @return the sounds component
     */
    @Nonnull
    Sounds getSounds();

    /**
     * Get the assets that are maintained by this engine.
     *
     * @return the asset component of the engine
     */
    @Nonnull
    Assets getAssets();

    /**
     * Get the input component of the engine.
     *
     * @return the input component
     */
    @Nonnull
    Input getInput();
}
