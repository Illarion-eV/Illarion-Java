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

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALLwjgl3Audio;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALMusic;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This hack class resolves the libGDX sound problem.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SaveGdxOpenALMusic extends OpenALMusic {
    private static final Logger log = LoggerFactory.getLogger(SaveGdxOpenALMusic.class);
    @Nonnull
    private final OpenALMusic internalMusic;

    public SaveGdxOpenALMusic(@Nonnull Audio audio, @Nonnull FileHandle ref) {
        super((OpenALLwjgl3Audio) audio, ref);
        internalMusic = (OpenALMusic) audio.newMusic(ref);
    }

    @Override
    public void play() {
        internalMusic.play();
    }

    @Override
    public void stop() {
        internalMusic.stop();
    }

    @Override
    public void pause() {
        internalMusic.pause();
    }

    @Override
    public boolean isPlaying() {
        return internalMusic.isPlaying();
    }

    @Override
    public void setLooping(boolean isLooping) {
        internalMusic.setLooping(isLooping);
    }

    @Override
    public boolean isLooping() {
        return internalMusic.isLooping();
    }

    @Override
    public void setVolume(float volume) {
        internalMusic.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return internalMusic.getVolume();
    }

    @Override
    public void setPan(float pan, float volume) {
        internalMusic.setPan(pan, volume);
    }

    @Override
    public float getPosition() {
        return internalMusic.getPosition();
    }

    @Override
    public int read(byte[] buffer) {
        return internalMusic.read(buffer);
    }

    @Override
    public void reset() {
        internalMusic.reset();
    }

    @Override
    public void update() {
        try {
            internalMusic.update();
        } catch (GdxRuntimeException ex) {
            log.error("Failure while updating the music track. There is a problem with the sound track.");
            internalMusic.stop();
        }
    }

    @Override
    public int getChannels() {
        return internalMusic.getChannels();
    }

    @Override
    public int getRate() {
        return internalMusic.getRate();
    }

    @Override
    public void dispose() {
        internalMusic.dispose();
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        internalMusic.setOnCompletionListener(listener);
    }

    @Override
    public int getSourceId() {
        return internalMusic.getSourceId();
    }
}
