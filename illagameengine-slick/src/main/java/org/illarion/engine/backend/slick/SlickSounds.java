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
    /**
     * The default value for the pitch of the music.
     */
    private static final float DEFAULT_PITCH = 1.f;

    /**
     * The map of active sounds.
     */
    @Nonnull
    private final Map<Integer, SlickSound> activeSoundsMap;

    /**
     * The last handle of the sound that was triggered.
     */
    private int lastHandle = Integer.MIN_VALUE;

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

    /**
     * Create the implementation of the slick sounds and setup the internal values.
     */
    SlickSounds() {
        activeSoundsMap = new HashMap<Integer, SlickSound>();
    }

    @Override
    public float getMusicVolume() {
        return SoundStore.get().getMusicVolume();
    }

    @Override
    public void setMusicVolume(final float volume) {
        if (volume > 0.001f) {
            SoundStore.get().setMusicOn(true);
            SoundStore.get().setMusicVolume(volume);
        } else {
            SoundStore.get().setMusicOn(false);
        }
    }

    @Override
    public float getSoundVolume() {
        return SoundStore.get().getSoundVolume();
    }

    @Override
    public void setSoundVolume(final float volume) {
        if (volume > 0.001f) {
            SoundStore.get().setSoundsOn(true);
            SoundStore.get().setSoundVolume(volume);
        } else {
            SoundStore.get().setSoundsOn(false);
        }
    }

    @Override
    public float getSoundVolume(@Nonnull final Sound sound, final int handle) {
        return getSoundVolume();
    }

    @Override
    public boolean isMusicPlaying(@Nonnull final Music music) {
        if (music instanceof SlickMusic) {
            return ((SlickMusic) music).getInternalMusic().playing();
        }
        return false;
    }

    @Override
    public boolean isSoundPlaying(@Nonnull final Sound sound, final int handle) {
        if (!(sound instanceof SlickSound)) {
            return false;
        }
        @Nullable final SlickSound checkedSound = activeSoundsMap.get(handle);
        if ((checkedSound == null) || !checkedSound.equals(sound)) {
            return false;
        }
        if (checkedSound.getInternalSound().playing()) {
            return true;
        }
        activeSoundsMap.remove(handle);
        return false;
    }

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
        currentMusic.getInternalMusic().loop(DEFAULT_PITCH, 0.f);
        currentMusic.getInternalMusic().fade(fadeInTime, getMusicVolume(), false);
    }

    @Override
    public int playSound(@Nonnull final Sound sound, final float volume) {
        if (sound instanceof SlickSound) {
            @Nonnull final org.newdawn.slick.Sound slickSound = ((SlickSound) sound).getInternalSound();
            slickSound.play(DEFAULT_PITCH, SoundStore.get().getSoundVolume() * volume);
            lastHandle++;
            activeSoundsMap.put(lastHandle, (SlickSound) sound);
            return lastHandle;
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public int playSound(@Nonnull final Sound sound, final float volume, final int offsetX, final int offsetY, final int offsetZ) {
        if (sound instanceof SlickSound) {
            @Nonnull final org.newdawn.slick.Sound slickSound = ((SlickSound) sound).getInternalSound();
            slickSound.playAt(1.f, SoundStore.get().getSoundVolume() * volume, offsetX, offsetY, offsetZ);
            lastHandle++;
            activeSoundsMap.put(lastHandle, (SlickSound) sound);
            return lastHandle;
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public void poll(final int delta) {
        SoundStore.get().poll(delta);
    }

    @Override
    public void setSoundVolume(@Nonnull final Sound sound, final int handle, final float volume) {
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
    public void stopSound(@Nonnull final Sound sound, final int handle) {
        if (!(sound instanceof SlickSound)) {
            return;
        }
        @Nullable final SlickSound checkedSound = activeSoundsMap.get(handle);
        if ((checkedSound != null) && checkedSound.equals(sound)) {
            checkedSound.getInternalSound().stop();
            activeSoundsMap.remove(handle);
        }
    }

    @Override
    public void stopSound(@Nonnull final Sound sound) {
        ((SlickSound) sound).getInternalSound().stop();
    }
}
