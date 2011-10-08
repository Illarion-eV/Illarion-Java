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

/**
 * This class is used to store the informations about a sound effect that are
 * needed to play it.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Sound {
    /**
     * Folder containing the sound effects.
     */
    @SuppressWarnings("nls")
    private static final String SOUND_PATH = "data/sounds/";

    /**
     * The ID of the sound.
     */
    private final int id;
    
    /**
     * The path to the file that stores this sound.
     */
    private final String soundFile;
    
    /**
     * The constructor that defines all values needed to identify this sound.
     * 
     * @param soundId the id of the sound
     * @param name the name of the sound
     */
    public Sound(final int soundId, final String fileName) {
        id = soundId;
        soundFile = SOUND_PATH + fileName;
    }

    /**
     * Get the ID of this sound.
     * 
     * @return the ID of the sound
     */
    public int getId() {
        return id;
    }

    /**
     * Get the path to this sound file.
     * 
     * @return the path to this sound file
     */
    public String getSoundFile() {
        return soundFile;
    }
}
