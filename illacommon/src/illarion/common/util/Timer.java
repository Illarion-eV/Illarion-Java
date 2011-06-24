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

import java.util.List;

import javolution.util.FastTable;

/**
 * This is a implementation of a restart able timer that is able to operate very
 * fast. It will call any amount of added listeners after given delay times.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Timer {
    /**
     * The delay in milliseconds between two calls of the listeners. This time
     * only applies in case the timer is in repeating mode.
     */
    private int delay;

    /**
     * The time when this timer is supposed to be called next time.
     */
    private long expirationTime;

    /**
     * The delay in milliseconds applied between the call of the
     * {@link #start()} function and the first call of the listeners.
     */
    private int initialDelay;

    /**
     * The list of runnable targets that are executed once this timer is
     * executed.
     */
    private final List<Runnable> listeners;

    /**
     * This is the next timer in the list. This variable is used to implement a
     * linked list the timers are stored with.
     */
    private Timer nextTimer;

    /**
     * The repeat flag. When set to <code>true</code> the timer will call the
     * events more then just once.
     */
    private boolean repeats = true;

    /**
     * The running flag stores if the timer is currently running.
     */
    private boolean running = false;

    /**
     * This creates a new timer with a initial and between delay time. Before
     * this timer can be used properly its needed to add at least one listener
     * in addition to this timer using {@link #addListener(Runnable)}.
     * 
     * @param timerDelay the initial and the between delay time in milliseconds
     */
    public Timer(final int timerDelay) {
        this(timerDelay, null);
    }

    /**
     * This creates a new timer with a separated set initial and between time
     * delay. Also the first listener is added right away using this
     * constructor.
     * 
     * @param initDelay the initial delay of this timer in milliseconds
     * @param betweenDelay the delay between two calls in milliseconds
     * @param listener the first listener that is called using this timer
     */
    public Timer(final int initDelay, final int betweenDelay,
        final Runnable listener) {
        delay = betweenDelay;
        initialDelay = initDelay;
        listeners = FastTable.newInstance();

        if (listener != null) {
            addListener(listener);
        }
    }

    /**
     * This creates a new timer with a initial and a between delay time. Also
     * the first listener is added right away using this constructor.
     * 
     * @param timerDelay the initial and the between delay time in milliseconds
     * @param listener the listener that is added as first listener
     */
    public Timer(final int timerDelay, final Runnable listener) {
        this(timerDelay, timerDelay, listener);
    }

    /**
     * Add a listener to that class that is called every time that timer
     * triggers.
     * 
     * @param listener the listener to add
     */
    public void addListener(final Runnable listener) {
        listeners.add(listener);
    }

    /**
     * Return the time in milliseconds between two calls of the timer listeners.
     * 
     * @return the between-event delay time
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Get the delay that applies before the first call of the timer.
     * 
     * @return the initial delay in milliseconds
     */
    public int getInitialDelay() {
        return initialDelay;
    }

    /**
     * Check if the timer is repeating.
     * 
     * @return <code>true</code> in case the timer is repeating and will send
     *         keep sending events
     */
    public boolean isRepeats() {
        return repeats;
    }

    /**
     * Check if this timer is currently running.
     * 
     * @return <code>true</code> if this timer is currently running and firing
     *         updates
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Remove a listener from that timer. After this call the listener is not
     * notified anymore when the timer triggers.
     * 
     * @param listener the listener to remove
     */
    public void removeListener(final Runnable listener) {
        listeners.remove(listener);
    }

    /**
     * Restart this time. This causes that the timer is stopped and started
     * again right away. The first call to the listeners will be done after the
     * initial delay.
     */
    public void restart() {
        stop();
        start();
    }

    /**
     * Set the delay time that applies between two calls of this timer.
     * 
     * @param timerDelay the time between two calls of the timer in milliseconds
     */
    @SuppressWarnings("nls")
    public void setDelay(final int timerDelay) {
        if (timerDelay < 0) {
            throw new IllegalArgumentException("Invalid delay: " + timerDelay);
        }
        delay = timerDelay;
    }

    /**
     * Set the delay time that applies before the first call of the timer.
     * 
     * @param initDelay the initial delay in milliseconds
     */
    @SuppressWarnings("nls")
    public void setInitialDelay(final int initDelay) {
        if (initDelay < 0) {
            throw new IllegalArgumentException("Invalid initial delay: "
                + initDelay);
        }
        initialDelay = initDelay;
    }

    /**
     * Set if this timer is supposed to repeat events or not. In case this flag
     * is set to <code>false</code> the timer will stop after the first event is
     * send.
     * 
     * @param flag <code>true</code> to have the timer sending more then one
     *            event
     */
    public void setRepeats(final boolean flag) {
        repeats = flag;
    }

    /**
     * Start this timer. This causes the timer to fire the first event after the
     * initial delay time ({@link #getInitialDelay()}.
     */
    public void start() {
        if (!running) {
            TimerQueue.getInstance().addTimer(this,
                System.currentTimeMillis() + getInitialDelay());
            running = true;
        }
    }

    /**
     * Stop this timer. No more calls to the listeners of this timer will be
     * triggered.
     */
    public void stop() {
        if (running) {
            TimerQueue.getInstance().removeTimer(this);
            running = false;
        }
    }

    /**
     * Get the time when this timer is supposed to be called next time.
     * 
     * @return the time when this timer is supposed to be called next time
     */
    long getExpirationTime() {
        return expirationTime;
    }

    /**
     * Get the next timer in the list.
     * 
     * @return the next timer
     */
    Timer getNextTimer() {
        return nextTimer;
    }

    /**
     * Fire a event of this timer. This function is only expected to be called
     * by the timer queue. Calling this will result in all listeners to receive
     * a call.
     */
    synchronized void post() {
        final int count = listeners.size();
        for (int i = 0; i < count; i++) {
            listeners.get(i).run();
        }
    }

    /**
     * Set the time when this timer is supposed to be called next time.
     * 
     * @param time the time when this timer is supposed to be called next time
     */
    void setExpirationTime(final long time) {
        expirationTime = time;
    }

    /**
     * Set the next timer in list of timers.
     * 
     * @param next the next timer in the list
     */
    void setNextTimer(final Timer next) {
        nextTimer = next;
    }
}
