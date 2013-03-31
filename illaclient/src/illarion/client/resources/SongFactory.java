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
import illarion.common.util.FastMath;
import org.illarion.engine.assets.SoundsManager;
import org.illarion.engine.sound.Music;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * The song factory, so the main storage for background music. While this sounds like a another child of the
 * RecycleFactory, it is not one. This Factory works independent from the RecycleFactory,
 * because there is only one song at time played anyway.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SongFactory implements ResourceFactory<IdWrapper<String>> {
    /**
     * The singleton instance of the SongFactory.
     */
    private static final SongFactory INSTANCE = new SongFactory();

    /**
     * The root path to the music track files.
     */
    private static final String SONG_DIR = "data/music/";

    /**
     * Get the singleton instance of the sound factory.
     *
     * @return the singleton instance
     */
    @Nonnull
    public static SongFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The storage for the songs and the variations of the songs.
     */
    private TIntObjectHashMap<List<String>> songs;

    /**
     * Constructor of the factory. Starts to loading of table file containing the songs.
     */
    private SongFactory() {
        // nothing to do
    }

    /**
     * Get a song from a id. This function also selects what variation of a song shall be used.
     *
     * @param id      id of the song that is needed
     * @param manager the manager that actually supplies the music
     * @return {@code null} if the song was not found, if there is just one song with this id, the song is returned,
     *         in case there are multiple variations of this song, one is selected randomly and returned
     */
    @Nullable
    public Music getSong(final int id, @Nonnull final SoundsManager manager) {
        if ((songs != null) && songs.contains(id)) {
            // select a variant at random
            final List<String> clipList = songs.get(id);
            final int variant = FastMath.nextRandomInt(0, clipList.size());
            return manager.getMusic(songs.get(id).get(variant));
        }
        return null;
    }

    /**
     * The initialization function prepares this factory to receive data.
     */
    @Override
    @SuppressWarnings("nls")
    public void init() {
        songs = new TIntObjectHashMap<List<String>>();
    }

    /**
     * Optimize the table after the loading sequence has finished.
     */
    @Override
    public void loadingFinished() {

    }

    /**
     * Add a song to this factory.
     */
    @Override
    public void storeResource(@Nonnull final IdWrapper<String> resource) {
        final int clipID = resource.getId();
        final String music = resource.getObject();

        final List<String> clipList;
        if (songs.contains(clipID)) {
            clipList = songs.get(clipID);
        } else {
            clipList = new ArrayList<String>();
            songs.put(clipID, clipList);
        }
        clipList.add(SONG_DIR + music);
    }
}
