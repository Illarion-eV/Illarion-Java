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
package org.illarion.engine.backend.gdx;

import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sound;
import org.illarion.engine.sound.Sounds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the sound engine that uses the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxSounds implements Sounds {

    /**
     * The current global music volume.
     */
    private float musicVolume;

    /**
     * The music that is currently played in the background.
     */
    @Nullable
    private com.badlogic.gdx.audio.Music currentBackgroundMusic;

    /**
     * The current sound volume;
     */
    private float soundVolume;

    /**
     * Create a new instance of the libGDX sound system.
     */
    GdxSounds() {
        musicVolume = 1.f;
        soundVolume = 1.f;
    }

    @Override
    public float getMusicVolume() {
        return musicVolume;
    }

    @Override
    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (currentBackgroundMusic != null) {
            currentBackgroundMusic.setVolume(volume);
        }
    }

    @Override
    public float getSoundVolume() {
        return soundVolume;
    }

    @Override
    public void setSoundVolume(float volume) {
        soundVolume = volume;
    }

    @Override
    public float getSoundVolume(@Nonnull Sound sound, int handle) {
        return soundVolume;
    }

    @Override
    public boolean isMusicPlaying(@Nonnull Music music) {
        return (music instanceof GdxMusic) && ((GdxMusic) music).getWrappedMusic().isPlaying();
    }

    @Override
    public boolean isSoundPlaying(@Nonnull Sound sound, int handle) {
        return false;
    }

    @Override
    public void playMusic(@Nonnull Music music, int fadeOutTime, int fadeInTime) {
        if (currentBackgroundMusic != null) {
            currentBackgroundMusic.stop();
        }
        if (music instanceof GdxMusic) {
            currentBackgroundMusic = ((GdxMusic) music).getWrappedMusic();
            currentBackgroundMusic.setLooping(true);
            currentBackgroundMusic.setVolume(getMusicVolume());
            currentBackgroundMusic.play();
        }
    }

    @Override
    public int playSound(@Nonnull Sound sound, float volume) {
        if (sound instanceof GdxSound) {
            return (int) ((GdxSound) sound).getWrappedSound().play(soundVolume * volume);
        }
        return -1;
    }

    @Override
    public int playSound(
            @Nonnull Sound sound, float volume, int offsetX, int offsetY, int offsetZ) {
        if (sound instanceof GdxSound) {
            return (int) ((GdxSound) sound).getWrappedSound().play(soundVolume * volume);
        }
        return -1;
    }

    @Override
    public void poll(int delta) {
        // nothing
    }

    @Override
    public void setSoundVolume(@Nonnull Sound sound, int handle, float volume) {
        if (sound instanceof GdxSound) {
            ((GdxSound) sound).getWrappedSound().setVolume(handle, volume);
        }
    }

    @Override
    public void stopMusic(int fadeOutTime) {
        if (currentBackgroundMusic != null) {
            currentBackgroundMusic.stop();
        }
    }

    @Override
    public void stopSound(@Nonnull Sound sound, int handle) {
        if (sound instanceof GdxSound) {
            ((GdxSound) sound).getWrappedSound().stop(handle);
        }
    }

    @Override
    public void stopSound(@Nonnull Sound sound) {
        if (sound instanceof GdxSound) {
            ((GdxSound) sound).getWrappedSound().stop();
        }
    }
}
