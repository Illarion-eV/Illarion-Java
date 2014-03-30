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
package org.illarion.engine.backend.gdx;

import org.illarion.engine.sound.Music;

import javax.annotation.Nonnull;

/**
 * The wrapper for a background music track of the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxMusic implements Music {

    /**
     * The music track that is wrapped in this class.
     */
    @Nonnull
    private final com.badlogic.gdx.audio.Music wrappedMusic;

    /**
     * Create a new wrapper for a music track.
     *
     * @param wrappedMusic the new music wrapper
     */
    GdxMusic(@Nonnull final com.badlogic.gdx.audio.Music wrappedMusic) {
        this.wrappedMusic = wrappedMusic;
    }

    @Override
    public void dispose() {
        wrappedMusic.dispose();
    }

    /**
     * Get the wrapped music object.
     *
     * @return the wrapped music object
     */
    @Nonnull
    public com.badlogic.gdx.audio.Music getWrappedMusic() {
        return wrappedMusic;
    }
}
