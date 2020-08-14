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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableListMultimap;
import illarion.client.util.IdWrapper;
import illarion.common.util.FastMath;
import org.illarion.engine.assets.SoundsManager;
import org.illarion.engine.sound.Music;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nonnull
    private static final SongFactory INSTANCE = new SongFactory();

    /**
     * The root path to the music track files.
     */
    @Nonnull
    private static final String SONG_DIR = "music/";

    /**
     * Get the singleton instance of the sound factory.
     *
     * @return the singleton instance
     */
    @Nonnull
    @Contract(pure = true)
    public static SongFactory getInstance() {
        return INSTANCE;
    }

    /**
     * This is the builder of the storage collection. This variable is only used during the initialization and disposed
     * of after.
     */
    @Nullable
    private ImmutableListMultimap.Builder<Integer, String> songsBuilder;

    /**
     * The storage for the songs and the variations of the songs.
     */
    @Nullable
    private ImmutableListMultimap<Integer, String> songs;

    /**
     * Constructor of the factory. Starts to loading of table file containing the songs.
     */
    private SongFactory() {
        // nothing to do
    }

    /**
     * Get a song from a id. This function also selects what variation of a song shall be used.
     *
     * @param id id of the song that is needed
     * @param manager the manager that actually supplies the music
     * @return {@code null} if the song was not found, if there is just one song with this id, the song is returned,
     * in case there are multiple variations of this song, one is selected randomly and returned
     */
    @Nullable
    @Contract(pure = true)
    public Music getSong(int id, @Nonnull SoundsManager manager) {
        if (songs != null) {
            // select a variant at random
            List<String> clipList = songs.get(id);
            if (clipList != null) {
                int variant = FastMath.nextRandomInt(0, clipList.size());
                String variantRef = clipList.get(variant);
                if (variantRef != null) {
                    return manager.getMusic(variantRef);
                }
            }
        }
        return null;
    }

    /**
     * The initialization function prepares this factory to receive data.
     */
    @Override
    public void init() {
        songsBuilder = new ImmutableListMultimap.Builder<>();
    }

    /**
     * Optimize the table after the loading sequence has finished.
     */
    @Override
    public void loadingFinished() {
        if (songsBuilder == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }

        songs = songsBuilder.build();
        songsBuilder = null;
    }

    /**
     * Add a song to this factory.
     */
    @Override
    public void storeResource(@Nonnull IdWrapper<String> resource) {
        if (songsBuilder == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }

        int clipID = resource.getId();
        String music = resource.getObject();

        songsBuilder.put(clipID, SONG_DIR + music);
    }

    /**
     * Get a list of all the song names present.
     *
     * @return a newly created list that contains the list
     */
    @Nonnull
    @Contract(pure = true)
    public ImmutableCollection<String> getSongNames() {
        if (songs == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }

        return songs.values();
    }

    /**
     * Load a specific music track.
     *
     * @param manager the manager used to load the track
     * @param song the name of the song to load
     */
    public void loadSong(@Nonnull SoundsManager manager, @Nonnull String song) {
        manager.getMusic(song);
    }
}
