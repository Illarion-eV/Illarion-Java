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

/**
 * This class is the timer queue that stores all instances of timers and
 * regularly calls this instances from a single thread.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class TimerQueue implements Runnable {
    /**
     * The singleton instance of this timer queue.
     */
    private static final TimerQueue INSTANCE = new TimerQueue();

    /**
     * The list of timers handled by this queue.
     */
    private Timer firstTimer;

    /**
     * The last timer in the list. This entry is needed to perform a high speed
     * check if a timer is part of this queue.
     */
    private Timer lastTimer;

    /**
     * The running flag. This is set to <code>true</code> in case the thread
     * that updates this queue is set in place and running.
     */
    private boolean running;

    /**
     * Constructor for TimerQueue.
     */
    private TimerQueue() {
        super();
        running = false;

        start();
    }

    /**
     * Get the instance of this timer queue.
     * 
     * @return the instance of this timer queue that is supposed to be used for
     *         all timers
     */
    static TimerQueue getInstance() {
        return INSTANCE;
    }

    /**
     * The main loop of the timer queue. That functions constantly calls the
     * timers based upon their settings.
     */
    @Override
    public void run() {
        long timeToWait;

        try {
            while (running) {
                timeToWait = postExpiredTimers();
                synchronized (this) {
                    try {
                        wait(timeToWait);
                    } catch (final InterruptedException ie) {
                        // nothing to do
                    }
                }
            }
        } catch (final ThreadDeath td) {
            running = false;

            // remove all queued timers.
            while (firstTimer != null) {
                removeTimer(firstTimer);
            }
            throw td;
        }
    }

    /**
     * Add a timer to the queue of timers.
     * 
     * @param timer the timer to add
     * @param expirationTime the time stamp of the next time this timer is
     *            supposed to be called
     */
    void addTimer(final Timer timer, final long expirationTime) {
        Timer previousTimer;
        Timer nextTimer;

        synchronized (this) {
            // If the Timer is already in the queue, then ignore the add.
            if (containsTimer(timer)) {
                return;
            }

            previousTimer = null;
            nextTimer = firstTimer;

            while (nextTimer != null) {
                if (nextTimer.getExpirationTime() > expirationTime) {
                    break;
                }

                previousTimer = nextTimer;
                nextTimer = nextTimer.getNextTimer();
            }

            if (previousTimer == null) {
                firstTimer = timer;
            } else {
                previousTimer.setNextTimer(timer);
            }

            timer.setExpirationTime(expirationTime);
            timer.setNextTimer(nextTimer);

            if (nextTimer == null) {
                lastTimer = timer;
            }

            notify();
        }
    }

    /**
     * Check if a timer is in the queue.
     * 
     * @param timer the timer to check
     * @return <code>true</code> in case the timer is in this timer queue
     */
    boolean containsTimer(final Timer timer) {
        return (timer.equals(firstTimer) || timer.equals(lastTimer) || (timer
            .getNextTimer() != null));
    }

    /**
     * Send a update to all timers that are now supposed to be updated.
     * 
     * @return the time in milliseconds to wait until the next call is needed
     */
    long postExpiredTimers() {
        Timer timer;
        long currentTime;
        long timeToWait;

        do {
            synchronized (this) {
                timer = firstTimer;
                try {
                    this.wait(1);
                } catch (final InterruptedException e) {
                    // nothing to do
                }
            }

            if (timer == null) {
                return 0;
            }

            currentTime = System.currentTimeMillis();
            timeToWait = timer.getExpirationTime() - currentTime;

            if (timeToWait <= 0) {
                timer.post();

                // Remove the timer from the queue
                removeTimer(timer);

                // This tries to keep the interval uniform at
                // the cost of drift.
                if (timer.isRepeats()) {
                    addTimer(timer, currentTime + timer.getDelay());
                }

            }
        } while (timeToWait <= 0);

        return timeToWait;
    }

    /**
     * Remove a timer from the queue. AFter this the timer is not invoked
     * anymore.
     * 
     * @param timer the timer to remove from the list
     */
    void removeTimer(final Timer timer) {
        Timer previousTimer;
        Timer nextTimer;
        boolean found;

        synchronized (this) {
            if (!containsTimer(timer)) {
                return;
            }

            previousTimer = null;
            nextTimer = firstTimer;
            found = false;

            while (nextTimer != null) {
                if (nextTimer == timer) {
                    found = true;
                    break;
                }

                previousTimer = nextTimer;
                nextTimer = nextTimer.getNextTimer();
            }

            if (found) {
                if (previousTimer == null) {
                    firstTimer = timer.getNextTimer();
                } else {
                    previousTimer.setNextTimer(timer.getNextTimer());
                }

                if (timer.getNextTimer() == null) {
                    lastTimer = previousTimer;
                }
            }

            timer.setExpirationTime(0L);
            timer.setNextTimer(null);
        }
    }

    /**
     * Start the thread that manages this queue and all the timers stored in it.
     */
    @SuppressWarnings("nls")
    private synchronized void start() {
        if (running) {
            throw new RuntimeException("Can't start a TimerQueue "
                + "that is already running");
        }

        final Thread timerThread = new Thread(null, this, "TimerQueue");
        timerThread.setDaemon(true);
        timerThread.setPriority(Thread.NORM_PRIORITY);
        timerThread.start();
        running = true;
    }
}
