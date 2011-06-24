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
package illarion.client.sound;

import gnu.trove.map.hash.TIntObjectHashMap;

import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

import illarion.sound.SoundClip;
import illarion.sound.SoundManager;

/**
 * This factory provides access to all known sound files.
 */
public final class SoundFactory implements TableLoaderSink {

    /**
     * The singleton instance of the sound factory.
     */
    private static final SoundFactory INSTANCE = new SoundFactory();

    /**
     * Folder containing the sound effects.
     */
    @SuppressWarnings("nls")
    private static final String SOUND_PATH = "data/sounds/";

    /**
     * The index in the table record of the sound id.
     */
    private static final int TB_ID = 0;

    /**
     * The index in the table record of the sound filename.
     */
    private static final int TB_NAME = 1;

    /**
     * The hash map that stores all sound effects avaiable.
     */
    private TIntObjectHashMap<SoundClip> sounds;

    /**
     * Private constructor to ensure that no instances but the singleton
     * instance are created.
     */
    private SoundFactory() {
        // nothing to do
    }

    /**
     * Get the singleton instance of the sound factory.
     * 
     * @return the singleton instance of the sound factory
     */
    public static SoundFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Get the a sound from the factory by the id.
     * 
     * @param id the id of the requested sound effect
     * @return the sound effect or null in case it was not found or if the sound
     *         playback is disabled
     */
    public SoundClip getSound(final int id) {
        if (sounds.contains(id)) {
            return sounds.get(id);
        }
        return sounds.get(2);
    }

    /**
     * The initialization function prepares all prototyped that are needed to
     * work with this function.
     */
    @SuppressWarnings("nls")
    public void init() {
        sounds = new TIntObjectHashMap<SoundClip>();
        new TableLoader("Sounds", this);

        sounds.compact();
    }

    /**
     * Process a record of the table file that stores the sound effects and
     * register each sound effect.
     * 
     * @param line the number of the line that is currently processed
     * @param loader the table loader that processes the table file
     * @return true at all times
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final SoundClip sound = SoundManager.getInstance().getSoundClip();
        final int clipID = loader.getInt(TB_ID);
        sound.setId(clipID);
        sound.loadEffect(SOUND_PATH + loader.getString(TB_NAME));
        sound.setDataMode(SoundClip.MODE_STORE);

        sounds.put(clipID, sound);

        return true;
    }

}
