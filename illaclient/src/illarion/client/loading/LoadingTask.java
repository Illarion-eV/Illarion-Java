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

import illarion.common.util.ProgressMonitor;

import javax.annotation.Nonnull;

/**
 * This interface defines a task that need to be loaded.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface LoadingTask {
    /**
     * Perform the next step of the loading operation.
     */
    void load();

    /**
     * Check if the loading of this task is done.
     *
     * @return {@code true} if the loading is done
     */
    boolean isLoadingDone();

    /**
     * Get the progress monitor that reports the progress of this task.
     *
     * @return the progress monitor for this task
     */
    @Nonnull
    ProgressMonitor getProgressMonitor();
}
