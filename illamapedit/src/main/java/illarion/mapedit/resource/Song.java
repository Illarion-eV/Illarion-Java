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
package illarion.mapedit.resource;


import illarion.mapedit.util.OggPlayer;
import org.apache.log4j.Logger;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Song implements Comparable<Song> {
    /**
     * This logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(OggPlayer.class);
    /**
     * Path to where songs are stored.
     */
    private static final String SONG_DIR = "data/music/";

    private final int clipID;
    private final String fileName;

    /**
     * Default constructor
     *
     * @param clipID   id of the clip
     * @param fileName fileName of the file
     */
    public Song(final int clipID, final String fileName) {
        this.clipID = clipID;
        this.fileName = fileName;
    }

    /**
     * Get the id of the clip
     *
     * @return clipID
     */
    public int getClipID() {
        return clipID;
    }

    /**
     * Get the filename of the sound file.
     *
     * @return file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Play the sound file
     */
    public void play() {
        try {
            OggPlayer.play(SONG_DIR + fileName);
        } catch (IOException e) {
            LOGGER.error("Failed to play Ogg file.", e);
        } catch (UnsupportedAudioFileException e) {
            LOGGER.error("Unsupported audio file file.", e);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (super.equals(o)) {
            return true;
        }
        if (o instanceof Song) {
            return ((Song) o).clipID == clipID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return clipID;
    }

    @Override
    public int compareTo(final Song o) {
        return clipID - o.getClipID();
    }
}
