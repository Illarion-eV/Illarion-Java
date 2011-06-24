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
package illarion.common.debug;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This class is able to run a thread that detects deadlocked threads.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ThreadDeadlockDetector {
    /**
     * This is called whenever a problem with threads is detected.
     */
    public interface Listener {
        /**
         * This function is called in case a deadlock is detected in case with a
         * list of threads that are locked.
         * 
         * @param deadlockedThreads the locked threads
         */
        void deadlockDetected(Thread[] deadlockedThreads);
    }

    /**
     * The number of milliseconds between checking for deadlocks. It may be
     * expensive to check for deadlocks, and it is not critical to know so
     * quickly.
     */
    private static final int DEFAULT_DEADLOCK_CHECK_PERIOD = 10000;

    /**
     * The listeners that are notified in case a deadlocked thread is detected.
     */
    private final Collection<Listener> listeners =
        new CopyOnWriteArraySet<Listener>();

    /**
     * The thread management bean.
     */
    private final ThreadMXBean mbean = ManagementFactory.getThreadMXBean();

    /**
     * The timer that calls the dead lock detector from time to time.
     */
    @SuppressWarnings("nls")
    private final Timer threadCheck =
        new Timer("ThreadDeadlockDetector", true);

    /**
     * Create a new dead lock detector that checks for deadlocks using the
     * default time interval.
     */
    public ThreadDeadlockDetector() {
        this(DEFAULT_DEADLOCK_CHECK_PERIOD);
    }

    /**
     * Create a new dead lock detector that checks for deadlocks.
     * 
     * @param deadlockCheckPeriod the time in milliseconds between two checks
     */
    public ThreadDeadlockDetector(final int deadlockCheckPeriod) {
        threadCheck.schedule(new TimerTask() {
            @Override
            @SuppressWarnings("synthetic-access")
            public void run() {
                checkForDeadlocks();
            }
        }, 10, deadlockCheckPeriod);
    }

    /**
     * Add a listener to the deadlock detector that is notified in case a
     * deadlock is found.
     * 
     * @param l the listener that shall be added
     * @return <code>true</code> in case the listener got added
     */
    public boolean addListener(final Listener l) {
        return listeners.add(l);
    }

    /**
     * Remove a listener from the deadlock detector. This listener won't be
     * informed about deadlocks anymore.
     * 
     * @param l the listener that is supposed to be removed
     * @return <code>true</code> in case the listener got removed
     */
    public boolean removeListener(final Listener l) {
        return listeners.remove(l);
    }

    /**
     * Check all threads and look for dead locks.
     */
    private void checkForDeadlocks() {
        final long[] ids = findDeadlockedThreads();
        if ((ids != null) && (ids.length > 0)) {
            final Thread[] threads = new Thread[ids.length];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = findMatchingThread(mbean.getThreadInfo(ids[i]));
            }
            fireDeadlockDetected(threads);
        }
    }

    /**
     * Find and return dead locked threads.
     * 
     * @return a list of IDs of the threads that got deadlocked
     */
    private long[] findDeadlockedThreads() {
        if (mbean.isSynchronizerUsageSupported()) {
            return mbean.findDeadlockedThreads();
        }
        return mbean.findMonitorDeadlockedThreads();
    }

    /**
     * Find the thread fitting to some thread informations.
     * 
     * @param inf the thread informations that are used as search condition
     * @return the thread matching the thread informations
     */
    @SuppressWarnings("nls")
    private Thread findMatchingThread(final ThreadInfo inf) {
        for (final Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getId() == inf.getThreadId()) {
                return thread;
            }
        }
        throw new IllegalStateException("Deadlocked Thread not found");
    }

    /**
     * Notify all listeners about a found deadlock.
     * 
     * @param threads the list of threads that got deadlocked
     */
    private void fireDeadlockDetected(final Thread[] threads) {
        for (final Listener l : listeners) {
            l.deadlockDetected(threads);
        }
    }
}
