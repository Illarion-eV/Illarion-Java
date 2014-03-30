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

import org.illarion.engine.sound.Sound;
import org.newdawn.slick.SlickException;

import javax.annotation.Nonnull;

/**
 * This is the implementation of a sound effect of the Slick2D backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickSound implements Sound {
    /**
     * The slick sound implementation.
     */
    @Nonnull
    private final org.newdawn.slick.Sound slickSound;

    /**
     * Create a new sound implementation for Slick.
     *
     * @param ref the path used to load the sound
     * @throws SlickException in case loading fails
     */
    SlickSound(@Nonnull final String ref) throws SlickException {
        slickSound = new org.newdawn.slick.Sound(ref);
    }

    /**
     * Get the internal instance of the Slick2D sound.
     *
     * @return the slick sound instance
     */
    @Nonnull
    public org.newdawn.slick.Sound getInternalSound() {
        return slickSound;
    }

    @Override
    public void dispose() {
        slickSound.release();
    }
}
