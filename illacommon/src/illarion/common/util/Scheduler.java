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
package illarion.common.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The scheduler is a wrapper for a java.util.Timer so the whole client handles
 * just a single timer thread.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class Scheduler implements Stoppable {
    /**
     * The singleton instance of the scheduler class.
     */
    private static final Scheduler INSTANCE = new Scheduler();

    /**
     * The timer class that is used to handle the scheduled tasks.
     */
    private Timer timer;

    /**
     * The private constructor that creates the needed final objects and ensures
     * that there are not instances but the singleton instance.
     */
    private Scheduler() {
        // nothing to do
    }

    /**
     * Get the singleton instance of the scheduler class. All actions are
     * handled by this class.
     * 
     * @return the singleton instance
     */
    public static Scheduler getInstance() {
        return INSTANCE;
    }

    /**
     * Shutdown the timer and cancel all tasks. The Scheduler is not working
     * anymore after calling this function.
     */
    @Override
    public void saveShutdown() {
        timer.cancel();
    }

    /**
     * Add a timer task to the scheduler that repeats until the scheduler is
     * stopped.
     * 
     * @param task the timer task that shall be added
     * @param initialDelay the time in milliseconds before the first start of
     *            the task
     * @param periodTime the time the task is run again after a run
     */
    public void schedule(final TimerTask task, final long initialDelay,
        final long periodTime) {
        timer.schedule(task, initialDelay, periodTime);
    }

    /**
     * Start the Scheduler. This will create the thread of the Scheduler.
     */
    @SuppressWarnings("nls")
    public void start() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timer = new Timer("Illarion Scheduler", true);
        StoppableStorage.getInstance().add(this);
    }
}
