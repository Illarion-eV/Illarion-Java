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

import illarion.common.util.Location;

/**
 * A sound source is in general something that sends out sounds. Its possible
 * that this a sound effect with a special location, but its also possible that
 * this is just a general sound. Also background music could be a sound source.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface SoundSource {
    /**
     * Default volume boost value.
     */
    float DEFAULT_BOOST = -20.f;

    /**
     * Restart playing the sound.
     */
    int OP_LOOP = 1;

    /**
     * Recycle the played sound effect and free the taken resources.
     */
    int OP_RECYCLE = 2;

    /**
     * Stop playing the sound and wait for further operations.
     */
    int OP_STOP = 0;

    /**
     * This constant is returned by {@link #getType()} in case the sound source
     * is a sound effect.
     */
    int TYPE_EFFECT = 0;

    /**
     * This constant is returned by {@link #getType()} in case the sound source
     * is background music.
     */
    int TYPE_MUSIC = 1;

    /**
     * Get the sound clip loaded into this sound source.
     * 
     * @return the loaded sound clip or <code>null</code> in case none is load.
     */
    SoundClip getSoundClip();

    /**
     * Get the type of the sound source.
     * 
     * @return the type of the sound source
     * @see #TYPE_EFFECT
     * @see #TYPE_MUSIC
     */
    int getType();

    /**
     * Check if the sound source is currently playing some effect.
     * 
     * @return <code>true</code> in case there is a sound clip played currently
     */
    boolean isPlaying();

    /**
     * Define the operation that is triggered when the sound has played
     * completely.
     * 
     * @param op the operation by this sound source from now on.
     * @see #OP_LOOP
     * @see #OP_RECYCLE
     * @see #OP_STOP
     */
    void setEndOperation(int op);

    /**
     * This function causes that the sound source has no location anymore. Its
     * played as it should be right on top of the character. No chance of volume
     * and balance.
     */
    void setNoPosition();

    /**
     * Set the location of the source source. This is only a legal operation in
     * case the sound source is not used as background music player.
     * 
     * @param posx the x coordinate of the sound source
     * @param posy the y coordinate of the sound source
     * @param posz the z coordinate of the sound source
     * @throws IllegalStateException in case the sound type is set to
     *             {@link #TYPE_MUSIC}
     */
    void setPosition(float posx, float posy, float posz);

    /**
     * Set the location of the source source. This is only a legal operation in
     * case the sound source is not used as background music player.
     * 
     * @param loc the location where the sound is played
     * @throws IllegalStateException in case the sound type is set to
     *             {@link #TYPE_MUSIC}
     */
    void setPosition(Location loc);

    /**
     * Set the sound clip that shall be played with this source. To remove the
     * clip from the sound source just send in <code>null</code> as clip.
     * 
     * @param clip the new sound effect to play or null
     */
    void setSoundClip(SoundClip clip);

    /**
     * Set the type of this clip. Its either a music clip or a effect clip.
     * 
     * @param type the type of that sound source
     * @see #TYPE_EFFECT
     * @see #TYPE_MUSIC
     */
    void setType(int type);

    /**
     * Start playing the sound effect.
     */
    void start();

    /**
     * Stop playing the sound effect.
     */
    void stop();
}
