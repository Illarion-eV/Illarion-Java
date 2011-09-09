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

import de.lessvoid.nifty.sound.SoundSystem;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import de.lessvoid.nifty.spi.sound.SoundHandle;

/**
 * The sound system that is used for the implementation into the NiftyGUI.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class NiftySoundDevice implements SoundDevice {
    /**
     * Load a background music that can be played by nifty.
     */
    @Override
    public SoundHandle loadMusic(final SoundSystem soundSystem,
        final String filename) {
        return new NiftyBackgroundMusicHandle(Integer.parseInt(filename));
    }

    /**
     * Load a new sound that can be played by nifty.
     */
    @Override
    public SoundHandle loadSound(final SoundSystem soundSystem,
        final String filename) {
        return new NiftySoundEffectHandle(Integer.parseInt(filename));
    }

    /**
     * Update the sound, we do this some way else.
     */
    @Override
    public void update(final int delta) {
    }

}
