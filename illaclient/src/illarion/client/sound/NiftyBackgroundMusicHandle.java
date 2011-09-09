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

import illarion.client.world.Game;
import de.lessvoid.nifty.spi.sound.SoundHandle;

/**
 * The handle of a background music that can be played with Nifty.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class NiftyBackgroundMusicHandle implements SoundHandle {
    /**
     * The ID of the music.
     */
    private final int musicId;

    /**
     * Create a new handle for the background music.
     * 
     * @param id the Id of the music
     */
    public NiftyBackgroundMusicHandle(final int id) {
        musicId = id;
    }

    /**
     * Remove that track from the memory.
     */
    @Override
    public void dispose() {
    }

    /**
     * Get the volume of the background music.
     */
    @Override
    public float getVolume() {
        return SoundManager.getInstance().getBackgroundVolume();
    }

    /**
     * Check if that track is currently already playing.
     */
    @Override
    public boolean isPlaying() {
        return Game.getMusicBox().isPlaying(musicId);
    }

    /**
     * Play the background music track.
     */
    @Override
    public void play() {
        Game.getMusicBox().playMusicTrack(musicId);
    }

    /**
     * Set the volume of the background music playback.
     */
    @Override
    public void setVolume(final float volume) {
        SoundManager.getInstance().setBackgroundVolume(volume);
    }

    /**
     * Stop the playback of that music.
     */
    @Override
    public void stop() {
        Game.getMusicBox().playDefaultMusic();
    }
}
