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
package org.illarion.engine.backend.slick;

import org.illarion.engine.Engine;
import org.newdawn.slick.GameContainer;

import javax.annotation.Nonnull;

/**
 * This is the central engine for the Slick2D backend. This class provides access to the most important components of
 * the engine.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickEngine implements Engine {
    /**
     * The graphics component of the Slick engine.
     */
    @Nonnull
    private final SlickGraphics graphics;
    @Nonnull
    private final SlickAssets assets;
    @Nonnull
    private final SlickSounds sounds;
    @Nonnull
    private final SlickInput input;

    SlickEngine(@Nonnull final GameContainer container) {
        graphics = new SlickGraphics();
        assets = new SlickAssets(container);
        sounds = new SlickSounds();
        input = new SlickInput();
    }

    @Nonnull
    @Override
    public SlickGraphics getGraphics() {
        return graphics;
    }

    @Nonnull
    @Override
    public SlickSounds getSounds() {
        return sounds;
    }

    @Nonnull
    @Override
    public SlickAssets getAssets() {
        return assets;
    }

    @Nonnull
    @Override
    public SlickInput getInput() {
        return input;
    }
}
