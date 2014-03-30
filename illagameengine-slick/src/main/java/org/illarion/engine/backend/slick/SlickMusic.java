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

import org.illarion.engine.sound.Music;
import org.newdawn.slick.SlickException;

import javax.annotation.Nonnull;

/**
 * This is the implementation of a background music track for Slick2D.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickMusic implements Music {
    /**
     * The slick music track.
     */
    @Nonnull
    private final org.newdawn.slick.Music slickMusic;

    /**
     * Create a new instance of a slick music track.
     *
     * @param ref the reference to the slick music track
     * @throws SlickException in case loading the track fails
     */
    SlickMusic(@Nonnull final String ref) throws SlickException {
        slickMusic = new org.newdawn.slick.Music(ref, true);
    }

    /**
     * Get the internal instance of the slick music.
     *
     * @return the internal slick music instance
     */
    @Nonnull
    public org.newdawn.slick.Music getInternalMusic() {
        return slickMusic;
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
