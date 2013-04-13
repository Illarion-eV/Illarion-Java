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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.illarion.engine.backend.shared.AbstractSoundsManager;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the sounds manager implementation for the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxSoundsManager extends AbstractSoundsManager {
    /**
     * The file system handler that is used to load the sound data.
     */
    @Nonnull
    private final Files files;

    /**
     * The sound interface of libGDX that is used to prepare the sound for playback.
     */
    @Nonnull
    private final Audio audio;

    /**
     * Create a new instance of the sound manager.
     *
     * @param files the file system handler used to load the data
     * @param audio the audio interface of libGDX that is supposed to be used
     */
    GdxSoundsManager(@Nonnull final Files files, @Nonnull final Audio audio) {
        this.files = files;
        this.audio = audio;
    }

    @Nullable
    @Override
    protected Sound loadSound(@Nonnull final String ref) {
        try {
            return new GdxSound(audio.newSound(files.internal(ref)));
        } catch (@Nonnull final GdxRuntimeException e) {
            return null;
        }
    }

    @Nullable
    @Override
    protected Music loadMusic(@Nonnull final String ref) {
        try {
            return new GdxMusic(audio.newMusic(files.internal(ref)));
        } catch (@Nonnull final GdxRuntimeException e) {
            return null;
        }
    }
}
