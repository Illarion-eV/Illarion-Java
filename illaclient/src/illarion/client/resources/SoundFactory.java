/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.resources;

import gnu.trove.map.hash.TIntObjectHashMap;
import illarion.client.util.IdWrapper;
import org.apache.log4j.Logger;
import org.illarion.engine.assets.SoundsManager;
import org.illarion.engine.sound.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    private static final SoundFactory INSTANCE = new SoundFactory();

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(SoundFactory.class);

    /**
     * Get the singleton instance of the sound factory.
     *
     * @return the singleton instance of the sound factory
     */
    @Nonnull
    public static SoundFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The hash map that stores all sound effects available.
     */
    private TIntObjectHashMap<String> sounds;

    /**
     * The path where the sounds are located.
     */
    private static final String SOUND_PATH = "data/sounds/";

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
    public Sound getSound(final int id, @Nonnull final SoundsManager manager) {
        Sound loadedSound = null;
        if (sounds.contains(id)) {
            loadedSound = manager.getSound(sounds.get(id));
        }
        if (loadedSound == null) {
            LOGGER.warn("Requested Sound unknown: " + id);
            return manager.getSound(sounds.get(DEFAULT_SOUND));
        }
        return loadedSound;
    }

    /**
     * Prepare the this factory for loading the sounds.
     */
    @Override
    @SuppressWarnings("nls")
    public void init() {
        sounds = new TIntObjectHashMap<String>();
    }

    /**
     * Optimize the table for reading operations.
     */
    @Override
    public void loadingFinished() {
        sounds.compact();
    }

    /**
     * Store a sound definition that was load from the resources in this
     * factory.
     */
    @Override
    public void storeResource(@Nonnull final IdWrapper<String> resource) {
        final String sound = resource.getObject();
        sounds.put(resource.getId(), SOUND_PATH + sound);
    }
}
