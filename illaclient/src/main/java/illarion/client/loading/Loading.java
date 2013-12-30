/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
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

import illarion.common.util.ProgressMonitor;
import org.illarion.engine.Engine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to enlist the required loading tasks and perform the loading operations itself.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public final class Loading {
    /**
     * This variable is set to true in case the elements got enlisted already and this class must not do anything
     * anymore.
     */
    private boolean loadingDone;

    /**
     * The progress monitor that is used to keep track of the loading progress.
     */
    @Nullable
    private ProgressMonitor progressMonitor;

    /**
     * This is the list of loading tasks that need to be handled.
     */
    @Nonnull
    private final List<LoadingTask> tasks;

    /**
     * Create a new instance of this class. This also enlists all the required entries.
     */
    public Loading() {
        tasks = new ArrayList<>();
    }

    /**
     * Enlist all components that are still needed to be loaded.
     *
     * @param gameEngine the game engine
     */
    public void enlistMissingComponents(@Nonnull final Engine gameEngine) {
        progressMonitor = new ProgressMonitor();
        if (!loadingDone) {
            addToTaskList(new TextureLoadingTask(gameEngine));
            addToTaskList(new ResourceTableLoading(gameEngine));
            addToTaskList(new SoundLoadingTask(gameEngine));
            addToTaskList(new GameEnvironmentLoading(gameEngine));
            loadingDone = true;
        }
    }

    /**
     * Perform the text loading step.
     */
    public void load() {
        if (tasks.isEmpty()) {
            return;
        }

        final LoadingTask currentTask = tasks.get(0);
        currentTask.load();
        if (currentTask.isLoadingDone()) {
            tasks.remove(0);
        }
    }

    /**
     * Check if all loading operations are done.
     *
     * @return {@code true} in case the loading is done
     */
    public boolean isLoadingDone() {
        return tasks.isEmpty();
    }

    /**
     * Add a new task to the list of tasks and attach it to the progress monitor.
     *
     * @param task the task to add
     */
    private void addToTaskList(@Nonnull final LoadingTask task) {
        tasks.add(task);

        assert progressMonitor != null;
        progressMonitor.addChild(task.getProgressMonitor());
    }

    /**
     * Get the loading progress.
     *
     * @return the progress of the loading operation as value between {@code 0.f} and {@code 1.f} (finished)
     */
    public float getProgress() {
        if (progressMonitor == null) {
            return 1.f;
        }
        return progressMonitor.getProgress();
    }
}
