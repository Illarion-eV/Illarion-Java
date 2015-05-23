/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
    @Nonnull
    private final Map<String, Sound> loadedSounds;

    /**
     * The map of music tracks that were already load.
     */
    @Nonnull
    private final Map<String, Music> loadedMusic;

    /**
     * Create a new sound manager and setup the internal structures.
     */
    protected AbstractSoundsManager() {
        loadedMusic = new HashMap<>();
        loadedSounds = new HashMap<>();
    }

    @Nullable
    @Override
    public final Sound getSound(@Nonnull String ref) {
        @Nullable Sound existingSound = loadedSounds.get(ref);
        if (existingSound == null) {
            @Nullable Sound loadedSound = loadSound(ref);
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
    public final Music getMusic(@Nonnull String ref) {
        @Nullable Music existingMusic = loadedMusic.get(ref);
        if (existingMusic == null) {
            @Nullable Music loadedMusicHandle = loadMusic(ref);
            if (loadedMusicHandle != null) {
                loadedMusic.put(ref, loadedMusicHandle);
            }
            return loadedMusicHandle;
        }
        return existingMusic;
    }
}
