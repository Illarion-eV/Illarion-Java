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
package org.illarion.engine.sound;

import javax.annotation.Nonnull;

/**
 * This interface provides the required access to play sound effects and background music.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Sounds {
    /**
     * Get the currently applied global music volume.
     *
     * @return the music volume
     */
    float getMusicVolume();

    /**
     * Set the global volume of the background music.
     *
     * @param volume the volume of the music
     */
    void setMusicVolume(float volume);

    /**
     * Get the global sound effects volume.
     *
     * @return the global sound effects volume
     */
    float getSoundVolume();

    /**
     * Set the global volume of the sound effects.
     *
     * @param volume the volume of the sound effects
     */
    void setSoundVolume(float volume);

    /**
     * Get the global sound effects volume.
     *
     * @return the global sound effects volume
     */
    float getAlertVolume();

    /**
     * Set the global volume of the sound effects.
     *
     * @param volume the volume of the sound effects
     */
    void setAlertVolume(float volume);

    /**
     * Get the sound effects volume of a sound that is currently playing.
     *
     * @param sound the sound the handle belong to
     * @param handle the handle of the sound effect that is returned by {@link #playSound(Sound, float)}
     * @return the volume of the sound effect
     */
    float getSoundVolume(@Nonnull Sound sound, int handle);

    /**
     * Check if a specific music track is currently playing.
     *
     * @param music the music track to check
     * @return {@code true} in case this music track is currently played
     */
    boolean isMusicPlaying(@Nonnull Music music);

    /**
     * Check if a sound effect is currently playing.
     *
     * @param sound the sound that should be checked
     * @param handle the handle of the sound effect that is returned by {@link #playSound(Sound, float)}
     * @return {@code true} in case this sound effect is currently played
     */
    boolean isSoundPlaying(@Nonnull Sound sound, int handle);

    /**
     * Start playing some background music.
     * <p/>
     * The implementation of the fading effect depends on the used backend and does not have to work by all means.
     *
     * @param music the music track that is supposed to be played now
     * @param fadeOutTime the time to fade the track that is currently playing out in milliseconds,
     * this parameter is ignored in case there is not music playing currently.
     * @param fadeInTime the time to fade the new track in
     */
    void playMusic(@Nonnull Music music, int fadeOutTime, int fadeInTime);

    /**
     * Play an alert
     *
     * @param sound the sound effect to play for the alert
     * @param volume the volume of the alert, this volume is multiplied with the global alert volume
     * @return the reference handle to the played alert
     */
    int playAlert(@Nonnull Sound sound, float volume);

    /**
     * Play a sound effect.
     *
     * @param sound the sound effect to play
     * @param volume the volume of the sound effect, this volume is multiplied with the global sound effect volume
     * @return the reference handle to the played sound effect
     */
    int playSound(@Nonnull Sound sound, float volume);

    /**
     * Play a sound with a offset to the listener in 3D space.
     *
     * @param sound the sound to play
     * @param volume the default volume
     * @param offsetX the X offset
     * @param offsetY the Y offset
     * @param offsetZ the Z offset
     * @return the reference handle to the played sound effect
     */
    int playSound(@Nonnull Sound sound, float volume, int offsetX, int offsetY, int offsetZ);

    /**
     * This function should be called once during the update run of the game, to allow the sound engine to process
     * their internal tasks.
     * <p/>
     * This is especially important for streaming music.
     *
     * @param delta the time since the last call of the poll function
     */
    void poll(int delta);

    /**
     * Set the volume of a sound effect that is currently playing.
     *
     * @param sound the sound the handle belong to
     * @param handle the handle of the sound effect that is returned by {@link #playSound(Sound, float)}
     * @param volume the volume of the sound effects
     */
    void setSoundVolume(@Nonnull Sound sound, int handle, float volume);

    /**
     * Stop any currently played music.
     * <p/>
     * The implementation of the fading effect depends on the used backend and does not have to work by all means.
     * <p/>
     * This function has no effect what so ever in case there is currently no music track played.
     *
     * @param fadeOutTime the time in milliseconds it should take to fade out the current music
     */
    void stopMusic(int fadeOutTime);

    /**
     * Stop the playback of a sound effect
     *
     * @param sound the sound that should be stopped
     * @param handle the handle of the sound effect that is returned by {@link #playSound(Sound, float)}
     */
    void stopSound(@Nonnull Sound sound, int handle);

    /**
     * Stop the playback of all instances of a sound effect.
     *
     * @param sound the sound that should be stopped
     */
    void stopSound(@Nonnull Sound sound);
}
