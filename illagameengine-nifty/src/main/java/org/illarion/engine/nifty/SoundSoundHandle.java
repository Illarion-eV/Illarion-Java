/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package org.illarion.engine.nifty;

import de.lessvoid.nifty.spi.sound.SoundHandle;
import org.illarion.engine.sound.Sound;
import org.illarion.engine.sound.Sounds;

import javax.annotation.Nonnull;

/**
 * This is the sound handle used to reference to the sound effect.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SoundSoundHandle implements SoundHandle {
    /**
     * The sound effect that is wrapped by this sound handle.
     */
    @Nonnull
    private final Sound sound;

    /**
     * The sound engine used to play the music.
     */
    @Nonnull
    private final Sounds soundsEngine;

    /**
     * This variable is set true in case the handle of the sound effect is valid.
     */
    private boolean validHandle;

    /**
     * The handle to the sound effect
     */
    private int handle;

    /**
     * Create a new sound handle for playing a sound effect.
     *
     * @param soundsEngine the sound engine used to play the music
     * @param sound the sound effect that is played
     */
    SoundSoundHandle(@Nonnull Sounds soundsEngine, @Nonnull Sound sound) {
        this.sound = sound;
        this.soundsEngine = soundsEngine;
    }

    @Override
    public void play() {
        handle = soundsEngine.playSound(sound, 1.f);
        validHandle = true;
    }

    @Override
    public void stop() {
        if (validHandle) {
            soundsEngine.stopSound(sound, handle);
            validHandle = false;
        }
    }

    @Override
    public void setVolume(float volume) {
        if (validHandle) {
            soundsEngine.setSoundVolume(sound, handle, volume);
        }
    }

    @Override
    public float getVolume() {
        if (validHandle) {
            return soundsEngine.getSoundVolume(sound, handle);
        }
        return 1.f;
    }

    @Override
    public boolean isPlaying() {
        if (validHandle) {
            return soundsEngine.isSoundPlaying(sound, handle);
        }
        return false;
    }

    @Override
    public void dispose() {
        sound.dispose();
    }
}
