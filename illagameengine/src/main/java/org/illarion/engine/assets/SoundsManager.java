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
package org.illarion.engine.assets;

import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This manager loads and maintains references to the sound and music objects that were load.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface SoundsManager {
    /**
     * Get a sound effect.
     *
     * @param ref the reference to this sound effect that is used to load it
     * @return the sound effect or {@code null} if this sound is unknown
     */
    @Nullable
    Sound getSound(@Nonnull String ref);

    /**
     * Get a background music track.
     *
     * @param ref the identifier of this track
     * @return the track or {@code null} in case the track is unknown
     */
    @Nullable
    Music getMusic(@Nonnull String ref);
}
