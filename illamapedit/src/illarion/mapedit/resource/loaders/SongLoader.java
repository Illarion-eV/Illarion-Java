/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.resource.loaders;


import gnu.trove.map.hash.TIntObjectHashMap;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;
import illarion.mapedit.resource.Resource;
import illarion.mapedit.resource.Song;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Fredrik K
 */
public class SongLoader implements TableLoaderSink<TableLoader>, Resource {
    private static final Logger LOGGER = Logger.getLogger(SongLoader.class);
    /**
     * The index in the table record of the sound id.
     */
    private static final int TB_ID = 0;

    /**
     * The index in the table record of the sound filename.
     */
    private static final int TB_NAME = 1;

    private final TIntObjectHashMap<Song> songs = new TIntObjectHashMap<Song>();

    private static final SongLoader INSTANCE = new SongLoader();;

    /**
     * Creates a new TableLoader
     * @throws IOException
     */
    @Override
    public void load() throws IOException {
        new TableLoader("Songs", this);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Songs";
    }

    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final int clipID = loader.getInt(TB_ID);
        final String filename = loader.getString(TB_NAME);
        Song song = new Song(clipID, filename);
        songs.put(clipID, song);
        return true;
    }

    /**
     * Get the instance of SongLoader
     * @return
     */
    @Nonnull
    public static SongLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Get the songs sorted by the songs clipID
     * @return Array of songs
     */
    public Song[] getSongs() {
        final Song[] s = songs.values(new Song[songs.size()]);
        Arrays.sort(s);
        return s;
    }
}
