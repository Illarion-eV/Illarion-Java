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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Application;
import org.illarion.engine.Engine;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;

/**
 * The main engine that uses libGDX for the handling of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxEngine implements Engine {
    /**
     * The asset manager of the engine.
     */
    @Nonnull
    private final GdxAssets assets;

    /**
     * The sound system of the engine.
     */
    @Nonnull
    private final GdxSounds sounds;

    /**
     * The graphics system of the engine.
     */
    @Nonnull
    private final GdxGraphics graphics;

    /**
     * The input system of the engine.
     */
    @Nonnull
    private final GdxInput input;

    /**
     * Create a new instance of the engine along with the reference to the libGDX application that is used.
     *
     * @param gdxApplication the active libGDX application
     * @param container      the game container that shows the application
     */
    GdxEngine(@Nonnull final Application gdxApplication, @Nonnull final GameContainer container) {
        assets = new GdxAssets(gdxApplication, container);
        sounds = new GdxSounds();
        graphics = new GdxGraphics(this, gdxApplication.getGraphics());
        input = new GdxInput(gdxApplication.getInput());
    }

    @Nonnull
    @Override
    public GdxGraphics getGraphics() {
        return graphics;
    }

    @Nonnull
    @Override
    public GdxSounds getSounds() {
        return sounds;
    }

    @Nonnull
    @Override
    public GdxAssets getAssets() {
        return assets;
    }

    @Nonnull
    @Override
    public GdxInput getInput() {
        return input;
    }
}
