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
package org.illarion.engine.backend.shared;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * The texture pre-loading task is used to load the texture data as far as possible outside of the graphics context.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class TextureAtlasPreLoadTask<V> implements Callable<V> {
    /**
     * The texture manager used to load the texture data.
     */
    @Nonnull
    private final AbstractTextureManager<V> textureManager;

    /**
     * The name of the texture to load.
     */
    @Nonnull
    private final String textureName;

    /**
     * Create a new pre-load texture atlas task.
     *
     * @param textureManager the task manager used to load the texture data
     * @param textureName the name of the texture atlas
     */
    public TextureAtlasPreLoadTask(
            @Nonnull AbstractTextureManager<V> textureManager, @Nonnull String textureName) {
        this.textureManager = textureManager;
        this.textureName = textureName;
    }

    @Nullable
    @Override
    public V call() throws Exception {
        return textureManager.loadTextureData(textureName + ".png");
    }
}
