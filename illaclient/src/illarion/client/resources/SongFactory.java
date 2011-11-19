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
import illarion.common.util.FastMath;

import java.util.List;

import org.newdawn.slick.Music;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;

import javolution.util.FastTable;

/**
 * The song factory, so the main storage for background music. While this sounds
 * like a another child of the RecycleFactory, it is not one. This Factory works
 * independent from the RecycleFactory, because there is only one song at time
 * played anyway.
 */
public final class SongFactory implements ResourceFactory<IdWrapper<Music> > {
    /**
     * The singleton instance of the SongFactory.
     */
    private static final SongFactory INSTANCE = new SongFactory();

    /**
     * Get the singleton instance of the sound factory.
     * 
     * @return the singleton instance
     */
    public static SongFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The storage for the songs and the variations of the songs.
     */
    private TIntObjectHashMap<List<Music>> songs;

    /**
     * Constructor of the factory. Starts to loading of table file containing
     * the songs.
     */
    private SongFactory() {
        // nothing to do
    }

    /**
     * Get a song from a id. This function also selects what variation of a song
     * shall be used.
     * 
     * @param id id of the song that is needed
     * @return null if the song was not found, if there is just one song with
     *         this id, the song is returned, in case there are multiple
     *         variations of this song, one is selected randomly and returned
     */
    public Music getSong(final int id) {
        if ((songs != null) && (songs.contains(id))) {
            // select a variant at random
            final List<Music> clipList = songs.get(id);
            final int variant = FastMath.nextRandomInt(0, clipList.size() - 1);
            return songs.get(id).get(variant);
        }
        return null;
    }

    /**
     * The initialization function prepares this factory to receive data.
     */
    @Override
    @SuppressWarnings("nls")
    public void init() {
        songs = new TIntObjectHashMap<List<Music>>();
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
    public void storeResource(final IdWrapper<Music> resource) {
        final int clipID = resource.getId();
        final Music music = resource.getObject();
        
        if (music instanceof DeferredResource) {
            LoadingList.get().add((DeferredResource) music);
        }

        List<Music> clipList;
        if (!songs.contains(clipID)) {
            clipList = new FastTable<Music>();
            songs.put(clipID, clipList);
        } else {
            clipList = songs.get(clipID);
        }
        clipList.add(music);
    }
}
