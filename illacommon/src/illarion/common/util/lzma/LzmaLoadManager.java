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
package illarion.common.util.lzma;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This is the load manager that takes care that the load created by the LZMA
 * streams remains within limits. It will manage the required threads to execute
 * the compression and decompression tasks.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
final class LzmaLoadManager {
    /**
     * The singleton instance of this class.
     */
    private static final LzmaLoadManager INSTANCE = new LzmaLoadManager();

    /**
     * The executor service that manages the tasks.
     */
    private final ExecutorService exec;

    /**
     * Private constructor to prepare this class and to ensure that there is
     * only one instance.
     */
    private LzmaLoadManager() {
        exec = Executors.newCachedThreadPool();
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static final LzmaLoadManager getInstance() {
        return INSTANCE;
    }

    /**
     * Add a task to this manager that will be executed.
     * 
     * @param task the task to execute
     * @return the future of this task
     */
    public Future<Boolean> addTask(final Callable<Boolean> task) {
        return exec.submit(task);
    }
}
