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
package illarion.client.loading;

import illarion.client.resources.SongFactory;
import illarion.client.resources.SoundFactory;
import illarion.common.util.ProgressMonitor;
import org.illarion.engine.Engine;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * This loading task is used to load all songs and sounds.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SoundLoadingTask implements LoadingTask {
    /**
     * The progress monitor used to keep track of the loading progress.
     */
    @Nonnull
    private final ProgressMonitor monitor;

    /**
     * The game engine instance used to load the sounds.
     */
    @Nonnull
    private final Engine engine;

    /**
     * The list of songs that need to be loaded.
     */
    @Nonnull
    private final Queue<String> songsToLoad;

    /**
     * The list of sounds that need to be loaded.
     */
    @Nonnull
    private final Queue<String> soundsToLoad;

    /**
     * The amount of resources to load before the loading started.
     */
    private final int initialAmount;

    /**
     * The amount of entries that still need loading.
     */
    private int remaining;

    SoundLoadingTask(@Nonnull Engine engine) {
        this.engine = engine;
        monitor = new ProgressMonitor(3.f);

        songsToLoad = new LinkedList<>(SongFactory.getInstance().getSongNames());
        soundsToLoad = new LinkedList<>(SoundFactory.getInstance().getSoundNames());
        initialAmount = songsToLoad.size() + soundsToLoad.size();
        remaining = initialAmount;
    }

    @Override
    public void load() {
        if (!songsToLoad.isEmpty()) {
            SongFactory.getInstance().loadSong(engine.getAssets().getSoundsManager(), songsToLoad.poll());
        } else if (!soundsToLoad.isEmpty()) {
            SoundFactory.getInstance().loadSound(engine.getAssets().getSoundsManager(), soundsToLoad.poll());
        }
        remaining--;

        monitor.setProgress(1.f - (remaining / (float) initialAmount));
    }

    @Override
    public boolean isLoadingDone() {
        return songsToLoad.isEmpty() && soundsToLoad.isEmpty();
    }

    @Nonnull
    @Override
    public ProgressMonitor getProgressMonitor() {
        return monitor;
    }
}
