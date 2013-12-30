/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.loading;

import illarion.client.resources.SongFactory;
import illarion.client.resources.SoundFactory;
import illarion.common.util.ProgressMonitor;
import org.illarion.engine.Engine;

import javax.annotation.Nonnull;
import java.util.List;

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
    private final List<String> songsToLoad;

    /**
     * The list of sounds that need to be loaded.
     */
    @Nonnull
    private final List<String> soundsToLoad;

    /**
     * The amount of resources to load before the loading started.
     */
    private final int initialAmount;

    SoundLoadingTask(@Nonnull final Engine engine) {
        this.engine = engine;
        monitor = new ProgressMonitor(3.f);

        songsToLoad = SongFactory.getInstance().getSongNames();
        soundsToLoad = SoundFactory.getInstance().getSoundNames();
        initialAmount = songsToLoad.size() + soundsToLoad.size();
    }

    @Override
    public void load() {
        if (!songsToLoad.isEmpty()) {
            SongFactory.getInstance().loadSong(engine.getAssets().getSoundsManager(),
                    songsToLoad.remove(songsToLoad.size() - 1));
        } else if (!soundsToLoad.isEmpty()) {
            SoundFactory.getInstance().loadSound(engine.getAssets().getSoundsManager(),
                    soundsToLoad.remove(soundsToLoad.size() - 1));
        }

        monitor.setProgress(1.f - ((float) (songsToLoad.size() + soundsToLoad.size()) / (float) initialAmount));
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
