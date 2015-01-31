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
package illarion.common.util;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

/**
 * This implementation of the table loader is used to load the sound table.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class TableLoaderSound extends TableLoader {
    /**
     * The index in the table record of the sound id.
     */
    private static final int TB_ID = 0;

    /**
     * The index in the table record of the sound filename.
     */
    private static final int TB_NAME = 1;

    /**
     * Create a new music table loader that loads the sound data from the default file.
     *
     * @param callback the callback sink that receives the data
     * @param <T> the type of the table loader used
     */
    public <T extends TableLoader> TableLoaderSound(@Nonnull TableLoaderSink<T> callback) {
        super("Sounds", callback);
    }

    /**
     * Get the ID of the sound.
     *
     * @return the ID of the sound
     */
    @Contract(pure = true)
    public int getSoundId() {
        return getInt(TB_ID);
    }

    /**
     * The name of the file that contains the audio data of this sound.
     *
     * @return the sound file
     */
    @Nonnull
    @Contract(pure = true)
    public String getSoundFile() {
        return getString(TB_NAME);
    }
}
