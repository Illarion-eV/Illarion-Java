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
package illarion.client.util;

import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class stores a list of tasks that are supposed to be executed during the next update cycle of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class UpdateTaskManager {
    /**
     * The task queue.
     */
    @Nonnull
    private final Queue<UpdateTask> taskQueue;

    /**
     * This value is set {@code true} while the updates are executed.
     */
    private boolean isInUpdateCall;

    /**
     * This is the container the current update run is executed for.
     */
    @Nullable
    private GameContainer currentContainer;

    /**
     * The delta value of the current update run.
     */
    private int currentDelta;

    /**
     * The thread that is tasked to execute the updates.
     */
    @Nullable
    private Thread currentThread;

    /**
     * The default constructor that prepares the internal structures.
     */
    public UpdateTaskManager() {
        taskQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * This function is triggered during the update loop of the game and triggers the update tasks.
     *
     * @param container the game container
     * @param delta the time since the last update
     */
    public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
        currentContainer = container;
        currentDelta = delta;
        currentThread = Thread.currentThread();
        isInUpdateCall = true;
        try {
            while (true) {
                @Nullable final UpdateTask task = taskQueue.poll();
                if (task == null) {
                    return;
                }

                task.onUpdateGame(container, delta);
            }
        } finally {
            isInUpdateCall = false;
            currentContainer = null;
        }
    }

    /**
     * Add a task to the list of tasks executed during the update loop. In case the update loop is currently
     * executed from the calling thread, the task is executed instantly.
     *
     * @param task the task to execute
     */
    public void addTask(@Nonnull final UpdateTask task) {
        if (isInUpdateCall && (currentThread == Thread.currentThread()) && (currentContainer != null)) {
            task.onUpdateGame(currentContainer, currentDelta);
        } else {
            taskQueue.offer(task);
        }
    }

    /**
     * Add a task to the list of tasks executed during the next update.
     *
     * @param task the task to execute
     */
    public void addTaskForLater(@Nonnull final UpdateTask task) {
        taskQueue.offer(task);
    }
}
