/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is used to execute tasks in the background.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class TaskExecutor {
    private static final TaskExecutor INSTANCE = new TaskExecutor();
    public static TaskExecutor getInstance() {
        return INSTANCE;
    }
    private TaskExecutor() {
        executor = Executors.newCachedThreadPool();
    }
    
    private ExecutorService executor;
    
    public void executeTask(final Task task) {
        executor.execute(task);
    }
}
