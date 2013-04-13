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

import org.illarion.engine.sound.Sound;

import javax.annotation.Nonnull;

/**
 * This is the wrapper class for a libGDX sound so it can be used properly by the game engine.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxSound implements Sound {
    /**
     * The sound instance that is wrapped by this class.
     */
    @Nonnull
    private final com.badlogic.gdx.audio.Sound wrappedSound;

    /**
     * Create a new sound effect wrapper.
     *
     * @param wrappedSound the sound that is wrapped
     */
    GdxSound(@Nonnull final com.badlogic.gdx.audio.Sound wrappedSound) {
        this.wrappedSound = wrappedSound;
    }

    @Override
    public void dispose() {
        wrappedSound.dispose();
    }

    /**
     * Get the internal sound that is wrapped by this class.
     *
     * @return the internal sound
     */
    @Nonnull
    public com.badlogic.gdx.audio.Sound getWrappedSound() {
        return wrappedSound;
    }
}
