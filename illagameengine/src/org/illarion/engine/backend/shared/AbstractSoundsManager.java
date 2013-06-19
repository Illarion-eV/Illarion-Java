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
package org.illarion.engine.backend.shared;

import org.illarion.engine.assets.SoundsManager;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the shared implementation for the sounds manager as its used by some backends.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractSoundsManager implements SoundsManager {
    /**
     * The map of sounds that were already load.
     */
    private final Map<String, Sound> loadedSounds;

    /**
     * The map of music tracks that were already load.
     */
    private final Map<String, Music> loadedMusic;

    /**
     * Create a new sound manager and setup the internal structures.
     */
    protected AbstractSoundsManager() {
        loadedMusic = new HashMap<String, Music>();
        loadedSounds = new HashMap<String, Sound>();
    }

    @Nullable
    @Override
    public final Sound getSound(@Nonnull final String ref) {
        @Nullable final Sound existingSound = loadedSounds.get(ref);
        if (existingSound == null) {
            @Nullable final Sound loadedSound = loadSound(ref);
            if (loadedSound != null) {
                loadedSounds.put(ref, loadedSound);
            }
            return loadedSound;
        }
        return existingSound;
    }

    /**
     * Load the sound from the resources.
     *
     * @param ref the reference to the sound
     * @return the sound or {@code null} in case loading the sound failed
     */
    @Nullable
    protected abstract Sound loadSound(@Nonnull String ref);

    /**
     * Load the music from the resources.
     *
     * @param ref the reference to the music
     * @return the music or {@code null} in case loading the music failed
     */
    @Nullable
    protected abstract Music loadMusic(@Nonnull String ref);

    @Nullable
    @Override
    public final Music getMusic(@Nonnull final String ref) {
        @Nullable final Music existingMusic = loadedMusic.get(ref);
        if (existingMusic == null) {
            @Nullable final Music loadedMusicHandle = loadMusic(ref);
            if (loadedMusicHandle != null) {
                loadedMusic.put(ref, loadedMusicHandle);
            }
            return loadedMusicHandle;
        }
        return existingMusic;
    }
}
