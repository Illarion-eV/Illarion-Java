/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.nifty;

import de.lessvoid.nifty.sound.SoundSystem;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import de.lessvoid.nifty.spi.sound.SoundHandle;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;
import org.illarion.engine.Engine;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the sound device for the Nifty-GUI that uses the Illarion Game Engine to play background music and sound
 * effects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class IgeSoundDevice implements SoundDevice {
    /**
     * The sounds playback engine.
     */
    @Nonnull
    private final Engine engine;

    /**
     * Create a new sound device.
     *
     * @param engine the engine that is used by this device
     */
    public IgeSoundDevice(@Nonnull final Engine engine) {
        this.engine = engine;
    }


    @Override
    public void setResourceLoader(final NiftyResourceLoader niftyResourceLoader) {
        // nothing
    }

    @Nullable
    @Override
    public SoundHandle loadSound(final SoundSystem soundSystem, @Nonnull final String filename) {
        final Sound sound = engine.getAssets().getSoundsManager().getSound(filename);
        if (sound == null) {
            return null;
        }
        return new SoundSoundHandle(engine.getSounds(), sound);
    }

    @Nullable
    @Override
    public SoundHandle loadMusic(final SoundSystem soundSystem, @Nonnull final String filename) {
        final Music music = engine.getAssets().getSoundsManager().getMusic(filename);
        if (music == null) {
            return null;
        }
        return new MusicSoundHandle(engine.getSounds(), music);
    }

    @Override
    public void update(final int delta) {
        // nothing to do
    }
}
