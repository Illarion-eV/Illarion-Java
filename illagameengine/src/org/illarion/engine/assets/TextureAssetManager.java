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
package org.illarion.engine.assets;

import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This interface provides access to the textures loaded by the backend. The references to those textures are used to
 * draw the respective images on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public interface TextureAssetManager {
    /**
     * Add a directory that provides texture data.
     *
     * @param directory the directory providing the texture data
     */
    void addTextureDirectory(@Nonnull String directory);

    /**
     * Get a specified texture.
     *
     * @param directory the directory to read the data from
     * @param name      the name of the texture required
     * @return the loaded texture or {@code null} in case the texture requested does not exist
     */
    @Nullable
    Texture getTexture(@Nonnull String directory, @Nonnull String name);

    /**
     * Get a specified texture.
     *
     * @param name the name of the texture required
     * @return the loaded texture or {@code null} in case the texture requested does not exist
     */
    @Nullable
    Texture getTexture(@Nonnull String name);

    /**
     * This function loads the remaining texture atlas files within the set directories.
     *
     * @return the progress of the load, {@code 0.f} in case nothing was load yet,
     *         {@code 1.f} once the loading progress is done
     */
    float loadRemaining();
}
