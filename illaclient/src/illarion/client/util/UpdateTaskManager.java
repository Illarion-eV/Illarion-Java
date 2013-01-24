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

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

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
    private final Queue<UpdateTask> taskQueue;

    /**
     * The default constructor that prepares the internal structures.
     */
    public UpdateTaskManager() {
        taskQueue = new ConcurrentLinkedQueue<UpdateTask>();
    }

    public void onUpdateGame(@Nonnull final GameContainer container, final StateBasedGame game, final int delta) {
        while (true) {
            @Nullable final UpdateTask task = taskQueue.poll();
            if (task == null) {
                return;
            }

            task.onUpdateGame(container, game, delta);
        }
    }

    /**
     * Add a task to the list of tasks executed during the next update.
     *
     * @param task the task to execute
     */
    public void addTask(@Nonnull final UpdateTask task) {
        taskQueue.offer(task);
    }
}
