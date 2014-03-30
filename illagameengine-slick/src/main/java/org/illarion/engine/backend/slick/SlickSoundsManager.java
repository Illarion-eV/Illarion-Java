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
package org.illarion.engine.backend.slick;

import org.illarion.engine.backend.shared.AbstractSoundsManager;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sound;
import org.newdawn.slick.SlickException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the manager used to load sound and background music.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickSoundsManager extends AbstractSoundsManager {
    @Nullable
    @Override
    protected Sound loadSound(@Nonnull final String ref) {
        try {
            return new SlickSound(ref);
        } catch (@Nonnull final SlickException e) {
            return null;
        }
    }

    @Nullable
    @Override
    protected Music loadMusic(@Nonnull final String ref) {
        try {
            return new SlickMusic(ref);
        } catch (SlickException e) {
            return null;
        }
    }
}
