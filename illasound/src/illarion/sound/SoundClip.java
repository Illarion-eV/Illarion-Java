/*
 * This file is part of the Illarion Sound Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Sound Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Sound Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Sound Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.sound;

/**
 * A sound clip in general holds the data that is playable by a sound source
 * later.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface SoundClip {
    /**
     * Setting this constant as clip data mode orders the sound clip to store
     * the decoded data. This leads to a pretty high usage of memory to store
     * the audio data. But this way one clip can serve multiple sound sources
     * without the need for additional resources.
     */
    int MODE_STORE = 2;

    /**
     * Setting this constant as clip data mode orders the sound clip to stream
     * its data. This leads to a very low usage of memory to send in the data,
     * but on the other hand side it causes that every sound clip is only able
     * to serve one sound source. So in case streamed clips are needed for more
     * then one sound instance the clip objects have to be cloned.
     */
    int MODE_STREAM = 1;

    /**
     * Get the ID of the sound effect.
     * 
     * @return the ID of the sound effect
     */
    int getId();

    /**
     * Load the effect from the resources. The data is loaded from the classpath
     * using the named filename.
     * 
     * @param filename the path and the filename to the source of the effect
     */
    void loadEffect(String filename);

    /**
     * Set the data mode this clip is working with.
     * 
     * @param mode the constant of the data mode
     * @see #MODE_STORE
     * @see #MODE_STREAM
     */
    void setDataMode(int mode);

    /**
     * Set the ID of the sound effect.
     * 
     * @param id the id of this sound effect
     */
    void setId(int id);

    /**
     * Remove the data of the sound effect from the memory and free the
     * resources this way.
     */
    void unloadEffect();
}
