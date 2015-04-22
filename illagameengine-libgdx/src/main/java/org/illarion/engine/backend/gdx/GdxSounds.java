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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the sound engine that uses the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxSounds implements Sounds {
    private static final Logger log = LoggerFactory.getLogger(GdxSounds.class);

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
     * Sets default volume to 100%
     */
    GdxSounds() {
        musicVolume = 1.f;
        soundVolume = 1.f;
    }

    @Override
    public float getMusicVolume() {
        return musicVolume;
    }

    public boolean isMusicOn() {
        return getMusicVolume() > 1.e-4;
    }

    @Override
    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (currentBackgroundMusic != null) {
            if (isMusicOn()) {
                currentBackgroundMusic.setVolume(volume);
            } else {
                stopMusic(0);
            }
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

    public boolean isSoundOn() {
        return isSoundOn(getSoundVolume());
    }

    public static boolean isSoundOn(float volume) {
        return volume > 1.e-4;
    }

    /**
     * Check if a specific music track is currently playing.
     *
     * @param music the music track to check
     * @return {@code true} in case this music track is currently played
     */
    @Override
    public boolean isMusicPlaying(@Nonnull Music music) {
        return (music instanceof GdxMusic) && ((GdxMusic) music).getWrappedMusic().isPlaying();
    }

    @Override
    public boolean isSoundPlaying(@Nonnull Sound sound, int handle) {
        return false;
    }

    /**
     * Start playing some background music.
     * Stops current music before starting the given music
     * <p/>
     * The fading effect is NOT currently supported.
     *
     * @param music the music track that is supposed to be played now
     * @param fadeOutTime (UNSUPPORTED)
     * @param fadeInTime (UNSUPPORTED)
     */
    @Override
    public void playMusic(@Nonnull Music music, int fadeOutTime, int fadeInTime) {
        if (!(music instanceof GdxMusic)) {
            throw new IllegalArgumentException("Type of music track is wrong: " + music.getClass());
        }
        if (currentBackgroundMusic != null) {
            currentBackgroundMusic.stop();
        }
        if (isMusicOn()) {
            log.info("Now starting to play: {}", music);
            currentBackgroundMusic = ((GdxMusic) music).getWrappedMusic();
            currentBackgroundMusic.setLooping(true);
            currentBackgroundMusic.setVolume(getMusicVolume());
            currentBackgroundMusic.play();
        }
    }

    @Override
    public int playSound(@Nonnull Sound sound, float volume) {
        if (!(sound instanceof GdxSound)) {
            throw new IllegalArgumentException("Type of sound effect is wrong: " + sound.getClass());
        }
        if (isSoundOn(getSoundVolume() * volume)) {
            return (int) ((GdxSound) sound).getWrappedSound().play(getSoundVolume() * volume);
        }
        return -1;
    }

    /**
     * Calls playSound(sound, volume)
     * <p/>
     * Does NOT support using the offset parameters
     *
     * @param sound the sound to play
     * @param volume the default volume
     * @param offsetX (UNSUPPORTED)
     * @param offsetY (UNSUPPORTED)
     * @param offsetZ (UNSUPPORTED)
     * @return the reference handle to the played sound effect
     */
    @Override
    public int playSound(@Nonnull Sound sound, float volume, int offsetX, int offsetY, int offsetZ) {
        return playSound(sound, volume);
    }

    /**
     * Does nothing, this is an unsupported method
     *
     * @param delta the time since the last call of the poll function
     */
    @Override
    public void poll(int delta) {
        // nothing
    }

    /**
     * Set the volume of a sound effect that is currently playing.
     *
     * @param sound the sound the handle belong to
     * @param handle the handle of the sound effect that is returned by {@link #playSound(Sound, float)}
     * @param volume the volume of the sound effects
     */
    @Override
    public void setSoundVolume(@Nonnull Sound sound, int handle, float volume) {
        if (!(sound instanceof GdxSound)) {
            throw new IllegalArgumentException("Type of sound effect is wrong: " + sound.getClass());
        }

        if (isSoundOn(getSoundVolume() * volume)) {
            ((GdxSound) sound).getWrappedSound().setVolume(handle, getSoundVolume() * volume);
        } else {
            stopSound(sound, handle);
        }
    }

    /**
     * If music is playing, IMMEDIATELY stops the music
     *
     * @param fadeOutTime (UNSUPPORTED)
     */
    @Override
    public void stopMusic(int fadeOutTime) {
        if (currentBackgroundMusic != null) {
            currentBackgroundMusic.stop();
            currentBackgroundMusic = null;
        }
    }

    /**
     * Stops the play of the given sound
     * @param sound the sound that should be stopped
     * @param handle the handle of the sound effect that is returned by {@link #playSound(Sound, float)}
     */
    @Override
    public void stopSound(@Nonnull Sound sound, int handle) {
        if (sound instanceof GdxSound) {
            ((GdxSound) sound).getWrappedSound().stop(handle);
        }
    }

    /**
     * Stops the play of the given sound
     * @param sound the sound that should be stopped
     */
    @Override
    public void stopSound(@Nonnull Sound sound) {
        if (sound instanceof GdxSound) {
            ((GdxSound) sound).getWrappedSound().stop();
        }
    }
}
