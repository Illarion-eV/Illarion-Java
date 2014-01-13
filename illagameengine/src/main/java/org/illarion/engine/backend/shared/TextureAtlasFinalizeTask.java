/*
 * This file is part of the engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.backend.shared;

import illarion.common.util.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class TextureAtlasFinalizeTask<T> implements Runnable, TextureAtlasTask {
    /**
     * The logger that provides the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TextureAtlasFinalizeTask.class);

    private static final class SpriteData {
        String spriteName;
        int posX;
        int posY;
        int width;
        int height;
    }

    @Nonnull
    private final FutureTask<T> preLoadTask;
    @Nonnull
    private final AbstractTextureManager<T> textureManager;
    @Nonnull
    private final String atlasName;
    @Nonnull
    private final List<SpriteData> spriteList;
    @Nonnull
    private final ProgressMonitor monitor;
    private final float progressToAdd;
    private boolean done;

    public TextureAtlasFinalizeTask(
            @Nonnull final FutureTask<T> preLoadTask,
            @Nonnull final String atlasName,
            @Nonnull final AbstractTextureManager<T> textureManager,
            @Nonnull final ProgressMonitor monitor,
            final float progressToAdd) {
        this.preLoadTask = preLoadTask;
        this.atlasName = atlasName;
        this.textureManager = textureManager;
        this.monitor = monitor;
        this.progressToAdd = progressToAdd;
        spriteList = new ArrayList<>();
        done = false;
    }

    public void addSprite(
            @Nonnull final String name, final int posX, final int posY, final int width, final int height) {
        final SpriteData data = new SpriteData();
        data.spriteName = name;
        data.posX = posX;
        data.posY = posY;
        data.width = width;
        data.height = height;
        spriteList.add(data);
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void run() {
        try {
            final T preLoadData = preLoadTask.get();
            final Texture atlasTexture = textureManager.loadTexture(atlasName, preLoadData);
            if (atlasTexture != null) {
                textureManager.addTexture(atlasName, atlasTexture);
                for (@Nonnull final SpriteData data : spriteList) {
                    final Texture spriteTexture = atlasTexture
                            .getSubTexture(data.posX, data.posY, data.width, data.height);
                    textureManager.addTexture(data.spriteName, spriteTexture);
                }
            }
            monitor.setProgress(monitor.getProgress() + progressToAdd);
        } catch (@Nonnull final InterruptedException e) {
            LOGGER.error("Loading thread got interrupted.", e);
        } catch (@Nonnull final ExecutionException e) {
            LOGGER.error("Failure while loading texture data.", e);
        } finally {
            done = true;
        }
    }
}
