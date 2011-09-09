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

import de.lessvoid.nifty.spi.sound.SoundHandle;

/**
 * The handle for sound effects.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class NiftySoundEffectHandle implements SoundHandle {
    /**
     * The ID of the sound effect.
     */
    private final int effectId;

    /**
     * Create a new sound effect handle with the ID of the sound effect that is
     * supposed to be played.
     * 
     * @param id the ID of the sound effect
     */
    public NiftySoundEffectHandle(final int id) {
        effectId = id;
    }

    /**
     * Remove the sound effect from the memory.
     */
    @Override
    public void dispose() {
    }

    /**
     * Get the volume of this effect.
     */
    @Override
    public float getVolume() {
        return SoundManager.getInstance().getEffectVolume();
    }

    /**
     * Check if the effect is currently playing.
     */
    @Override
    public boolean isPlaying() {
        return false;
    }

    /**
     * Play the sound effect.
     */
    @Override
    public void play() {
        SoundManager.getInstance().playEffect(effectId, null);
    }

    /**
     * Set the volume of this sound effect.
     */
    @Override
    public void setVolume(final float volume) {
        SoundManager.getInstance().setEffectVolume(volume);
    }

    /**
     * Stop the sound effect (does not work).
     */
    @Override
    public void stop() {
    }
}
