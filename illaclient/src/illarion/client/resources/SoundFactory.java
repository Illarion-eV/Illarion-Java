/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.resources;

import gnu.trove.map.hash.TIntObjectHashMap;

import illarion.client.util.IdWrapper;

import java.util.logging.Logger;

import org.newdawn.slick.Sound;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;

/**
 * This factory provides access to all known sound files.
 */
public final class SoundFactory implements ResourceFactory<IdWrapper<Sound> > {

    /**
     * This is the ID of the sound that is played in case the requested sound
     * was not found.
     */
    private static final int DEFAULT_SOUND = 2;

    /**
     * The singleton instance of the sound factory.
     */
    private static final SoundFactory INSTANCE = new SoundFactory();

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(SoundFactory.class
        .getCanonicalName());

    /**
     * Get the singleton instance of the sound factory.
     * 
     * @return the singleton instance of the sound factory
     */
    public static SoundFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The hash map that stores all sound effects available.
     */
    private TIntObjectHashMap<Sound> sounds;

    /**
     * Private constructor to ensure that no instances but the singleton
     * instance are created.
     */
    private SoundFactory() {
        // nothing to do
    }

    /**
     * Get the a sound from the factory by the id.
     * 
     * @param id the id of the requested sound effect
     * @return the sound effect or null in case it was not found or if the sound
     *         playback is disabled
     */
    public Sound getSound(final int id) {
        if (sounds.contains(id)) {
            return sounds.get(id);
        }

        LOGGER.warning("Requested Sound unknown: " + Integer.toString(id));
        return sounds.get(DEFAULT_SOUND);
    }

    /**
     * Prepare the this factory for loading the sounds.
     */
    @Override
    @SuppressWarnings("nls")
    public void init() {
        sounds = new TIntObjectHashMap<Sound>();
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
    public void storeResource(final IdWrapper<Sound> resource) {
        final Sound sound = resource.getObject();
        
        if (sound instanceof DeferredResource) {
            LoadingList.get().add((DeferredResource) sound);
        }
        sounds.put(resource.getId(), sound);
    }

}
