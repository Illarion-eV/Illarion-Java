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
package illarion.client.resources;

import gnu.trove.map.hash.TIntObjectHashMap;
import illarion.client.util.IdWrapper;
import org.illarion.engine.assets.SoundsManager;
import org.illarion.engine.sound.Sound;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This factory provides access to all known sound files.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SoundFactory implements ResourceFactory<IdWrapper<String>> {
    /**
     * This is the ID of the sound that is played in case the requested sound was not found.
     */
    private static final int DEFAULT_SOUND = 2;

    /**
     * The singleton instance of the sound factory.
     */
    @Nonnull
    private static final SoundFactory INSTANCE = new SoundFactory();

    /**
     * The instance of the logger that is used to write out the data.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(SoundFactory.class);

    /**
     * The path where the sounds are located.
     */
    @Nonnull
    private static final String SOUND_PATH = "sounds/";

    /**
     * The hash map that stores all sound effects available.
     */
    @Nullable
    private TIntObjectHashMap<String> sounds;

    /**
     * Get the singleton instance of the sound factory.
     *
     * @return the singleton instance of the sound factory
     */
    @Nonnull
    @Contract(pure = true)
    public static SoundFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor to ensure that no instances but the singleton instance are created.
     */
    private SoundFactory() {
        // nothing to do
    }

    /**
     * Get the a sound from the factory by the id.
     *
     * @param id the id of the requested sound effect
     * @return the sound effect or {@code null} in case it was not found or if the sound playback is disabled
     */
    @Nullable
    @Contract(pure = true)
    public Sound getSound(int id, @Nonnull SoundsManager manager) {
        if (sounds == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }

        String soundRef = sounds.get(id);
        Sound loadedSound = null;
        if (soundRef != null) {
            loadedSound = manager.getSound(soundRef);
        }
        if (loadedSound == null) {
            log.warn("Requested Sound unknown: {}", id);
            if (id != DEFAULT_SOUND) {
                return getSound(DEFAULT_SOUND, manager);
            }
        }
        return loadedSound;
    }

    /**
     * Prepare the this factory for loading the sounds.
     */
    @Override
    public void init() {
        sounds = new TIntObjectHashMap<>();
    }

    /**
     * Optimize the table for reading operations.
     */
    @Override
    public void loadingFinished() {
        if (sounds == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }

        sounds.compact();
    }

    /**
     * Store a sound definition that was load from the resources in this
     * factory.
     */
    @Override
    public void storeResource(@Nonnull IdWrapper<String> resource) {
        if (sounds == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }
        String sound = resource.getObject();
        sounds.put(resource.getId(), SOUND_PATH + sound);
    }

    /**
     * Get a list of all the sound names present.
     *
     * @return a newly created list that contains the list
     */
    @Nonnull
    @Contract(pure = true)
    public List<String> getSoundNames() {
        if (sounds == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }
        return new ArrayList<>(sounds.valueCollection());
    }

    /**
     * Load a specific sound effect.
     *
     * @param manager the manager used to load the sound
     * @param sound the name of the sound to load
     */
    public void loadSound(@Nonnull SoundsManager manager, @Nonnull String sound) {
        manager.getSound(sound);
    }
}
