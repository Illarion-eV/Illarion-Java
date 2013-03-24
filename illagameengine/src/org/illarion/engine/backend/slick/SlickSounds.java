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
package org.illarion.engine.backend.slick;

import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sound;
import org.illarion.engine.sound.Sounds;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.openal.SoundStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the implementation of the sounds engine for the Slick2D backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickSounds implements MusicListener, Sounds {
    @Override
    public float getMusicVolume() {
        return SoundStore.get().getMusicVolume();
    }

    @Override
    public void setMusicVolume(final float volume) {
        SoundStore.get().setMusicVolume(volume);
    }

    @Override
    public float getSoundVolume() {
        return SoundStore.get().getSoundVolume();
    }

    @Override
    public void setSoundVolume(final float volume) {
        SoundStore.get().setSoundVolume(volume);
    }

    @Override
    public float getSoundVolume(final int handle) {
        return getSoundVolume();
    }

    @Override
    public boolean isMusicPlaying(@Nonnull final Music music) {
        if (music instanceof SlickMusic) {
            return ((SlickMusic) music).getInternalMusic().playing();
        }
        return false;
    }

    /**
     * Create the implementation of the slick sounds and setup the internal values.
     */
    SlickSounds() {
        activeSoundsMap = new HashMap<Integer, SlickSound>();
    }

    @Nonnull
    private final Map<Integer, SlickSound> activeSoundsMap;

    private int lastHandle = Integer.MIN_VALUE;

    @Override
    public boolean isSoundPlaying(final int handle) {
        @Nullable final SlickSound checkedSound = activeSoundsMap.get(handle);
        if (checkedSound == null) {
            return false;
        }
        if (checkedSound.getInternalSound().playing()) {
            return true;
        }
        activeSoundsMap.remove(handle);
        return false;
    }

    /**
     * The currently played music track.
     */
    @Nullable
    private SlickMusic currentMusic;

    /**
     * The next music track that is supposed to launch once the current track finished playing.
     */
    @Nullable
    private SlickMusic nextMusic;

    /**
     * The fade in time of the next track.
     */
    private int nextFadeInTime;

    @Override
    public void playMusic(@Nonnull final Music music, final int fadeOutTime, final int fadeInTime) {
        if (currentMusic == null) {
            if (music instanceof SlickMusic) {
                startMusic((SlickMusic) music, fadeInTime);
            }
            nextMusic = null;
            nextFadeInTime = 0;
        } else {
            stopMusic(fadeOutTime);
            if (music instanceof SlickMusic) {
                nextMusic = (SlickMusic) music;
                nextFadeInTime = fadeInTime;
            } else {
                nextMusic = null;
            }
        }
    }

    private void startMusic(@Nonnull final SlickMusic music, final int fadeInTime) {
        currentMusic = music;
        currentMusic.getInternalMusic().addListener(this);
        currentMusic.getInternalMusic().play(0.f, 0.f);
        currentMusic.getInternalMusic().fade(fadeInTime, getMusicVolume(), false);
    }

    @Override
    public int playSound(@Nonnull final Sound sound, final float volume, final float pitch) {
        if (sound instanceof SlickSound) {
            @Nonnull final org.newdawn.slick.Sound slickSound = ((SlickSound) sound).getInternalSound();
            slickSound.play();
            lastHandle++;
            activeSoundsMap.put(lastHandle, (SlickSound) sound);
            return lastHandle;
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public void poll(final int delta) {
        // polling is not needed
    }

    @Override
    public void setSoundVolume(final int handle, final float volume) {
        // not suppoted
    }

    @Override
    public void stopMusic(final int fadeOutTime) {
        if (currentMusic != null) {
            currentMusic.getInternalMusic().fade(fadeOutTime, 0.f, true);
        }
    }

    @Override
    public void musicEnded(final org.newdawn.slick.Music music) {
        music.removeListener(this);
        if (nextMusic != null) {
            startMusic(nextMusic, nextFadeInTime);
        }

    }

    @Override
    public void musicSwapped(final org.newdawn.slick.Music music, final org.newdawn.slick.Music music2) {
        // does not happen
    }

    @Override
    public void stopSound(final int handle) {
        @Nullable final SlickSound checkedSound = activeSoundsMap.get(handle);
        if (checkedSound != null) {
            checkedSound.getInternalSound().stop();
            activeSoundsMap.remove(handle);
        }
    }
}
