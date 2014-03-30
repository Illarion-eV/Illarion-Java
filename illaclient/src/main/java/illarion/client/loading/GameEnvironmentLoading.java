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
package illarion.client.loading;

import illarion.client.IllaClient;
import illarion.client.world.World;
import illarion.common.data.SkillLoader;
import illarion.common.util.ProgressMonitor;
import org.illarion.engine.Engine;
import org.illarion.engine.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This loading task takes care for loading the components of the game environment that still need to be loaded.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
final class GameEnvironmentLoading implements LoadingTask {
    /**
     * This is set {@code true} once the loading of the game components is done.
     */
    private boolean loadingDone;

    /**
     * The monitor for the loading progress.
     */
    @Nonnull
    private final ProgressMonitor monitor;

    /**
     * The game engine instance that is used.
     */
    @Nonnull
    private final Engine usedEngine;

    /**
     * The logger of this class.
     */
    @Nonnull
    private final Logger logger = LoggerFactory.getLogger(GameEnvironmentLoading.class);

    /**
     * The constructor of this loading task.
     *
     * @param engine the engine that is used to load the game
     */
    GameEnvironmentLoading(@Nonnull final Engine engine) {
        usedEngine = engine;
        monitor = new ProgressMonitor();
    }

    @Override
    public void load() {
        try {
            SkillLoader.load();
            World.initWorldComponents(usedEngine);
        } catch (@Nonnull final EngineException e) {
            logger.error("Failed to init the components of the world.", e);
            IllaClient.errorExit("World init failed!");
        }
        loadingDone = true;
        monitor.setProgress(1.f);
    }

    @Override
    public boolean isLoadingDone() {
        return loadingDone;
    }

    @Nonnull
    @Override
    public ProgressMonitor getProgressMonitor() {
        return monitor;
    }
}
