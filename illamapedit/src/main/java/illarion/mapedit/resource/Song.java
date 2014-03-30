/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.mapedit.resource;

import illarion.mapedit.util.OggPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Song implements Comparable<Song> {
    /**
     * This logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OggPlayer.class);
    /**
     * Path to where songs are stored.
     */
    private static final String SONG_DIR = "music/";

    private final int clipID;
    private final String fileName;

    /**
     * Default constructor
     *
     * @param clipID id of the clip
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
    public int compareTo(@Nonnull final Song o) {
        return clipID - o.getClipID();
    }
}
