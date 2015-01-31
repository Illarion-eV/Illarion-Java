/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.common.util;

import javolution.util.FastTable;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This is a implementation of a restart able timer that is able to operate very
 * fast. It will call any amount of added listeners after given delay times.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings({"ClassNamingConvention", "UnusedDeclaration"})
public final class Timer {
    /**
     * The delay in milliseconds between two calls of the listeners. This time only applies in case the timer is in
     * repeating mode.
     */
    private int delay;

    /**
     * The time when this timer is supposed to be called next time.
     */
    private long expirationTime;

    /**
     * The delay in milliseconds applied between the call of the {@link #start()} function and the first call of the
     * listeners.
     */
    private int initialDelay;

    /**
     * The list of runnable targets that are executed once this timer is executed.
     */
    @Nonnull
    private final List<Runnable> listeners;

    /**
     * This is the next timer in the list. This variable is used to implement a linked list the timers are stored with.
     */
    @Nullable
    private Timer nextTimer;

    /**
     * The repeat flag. When set to {@code true} the timer will call the events more then just once.
     */
    private boolean repeats = true;

    /**
     * The running flag stores if the timer is currently running.
     */
    private boolean running;

    /**
     * This creates a new timer with a separated set initial and between time delay. Also the first listener is added
     * right away using this constructor.
     *
     * @param initDelay the initial delay of this timer in milliseconds
     * @param betweenDelay the delay between two calls in milliseconds
     * @param listener the first listener that is called using this timer
     */
    public Timer(int initDelay, int betweenDelay, @Nullable Runnable listener) {
        delay = betweenDelay;
        initialDelay = initDelay;
        listeners = new FastTable<>();

        if (listener != null) {
            addListener(listener);
        }
    }

    /**
     * This creates a new timer with a initial and a between delay time. Also the first listener is added right away
     * using this constructor.
     *
     * @param timerDelay the initial and the between delay time in milliseconds
     * @param listener the listener that is added as first listener
     */
    public Timer(int timerDelay, @Nullable Runnable listener) {
        this(timerDelay, timerDelay, listener);
    }

    /**
     * Add a listener to that class that is called every time that timer triggers.
     *
     * @param listener the listener to add
     */
    public void addListener(@Nonnull Runnable listener) {
        listeners.add(listener);
    }

    /**
     * Return the time in milliseconds between two calls of the timer listeners.
     *
     * @return the between-event delay time
     */
    @Contract(pure = true)
    public int getDelay() {
        return delay;
    }

    /**
     * Get the delay that applies before the first call of the timer.
     *
     * @return the initial delay in milliseconds
     */
    @Contract(pure = true)
    public int getInitialDelay() {
        return initialDelay;
    }

    /**
     * Check if the timer is repeating.
     *
     * @return {@code true} in case the timer is repeating and will send keep sending events
     */
    @Contract(pure = true)
    public boolean isRepeats() {
        return repeats;
    }

    /**
     * Check if this timer is currently running.
     *
     * @return {@code true} if this timer is currently running and firing updates
     */
    @Contract(pure = true)
    public boolean isRunning() {
        return running;
    }

    /**
     * Remove a listener from that timer. After this call the listener is not notified anymore when the timer triggers.
     *
     * @param listener the listener to remove
     */
    public void removeListener(@Nonnull Runnable listener) {
        listeners.remove(listener);
    }

    /**
     * Restart this time. This causes that the timer is stopped and started again right away. The first call to the
     * listeners will be done after the initial delay.
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
    public void setDelay(int timerDelay) {
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
    public void setInitialDelay(int initDelay) {
        if (initDelay < 0) {
            throw new IllegalArgumentException("Invalid initial delay: " + initDelay);
        }
        initialDelay = initDelay;
    }

    /**
     * Set if this timer is supposed to repeat events or not. In case this flag is set to {@code false} the timer
     * will stop after the first event is send.
     *
     * @param flag {@code true} to have the timer sending more then one event
     */
    public void setRepeats(boolean flag) {
        repeats = flag;
    }

    /**
     * Start this timer. This causes the timer to fire the first event after the
     * initial delay time ({@link #getInitialDelay()}.
     */
    public void start() {
        if (!running) {
            TimerQueue.getInstance().addTimer(this, System.currentTimeMillis() + getInitialDelay());
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
    @Contract(pure = true)
    long getExpirationTime() {
        return expirationTime;
    }

    /**
     * Get the next timer in the list.
     *
     * @return the next timer
     */
    @Nullable
    @Contract(pure = true)
    Timer getNextTimer() {
        return nextTimer;
    }

    /**
     * Fire a event of this timer. This function is only expected to be called
     * by the timer queue. Calling this will result in all listeners to receive
     * a call.
     */
    synchronized void post() {
        for (@Nonnull Runnable listener : listeners) {
            listener.run();
        }
    }

    /**
     * Set the time when this timer is supposed to be called next time.
     *
     * @param time the time when this timer is supposed to be called next time
     */
    void setExpirationTime(long time) {
        expirationTime = time;
    }

    /**
     * Set the next timer in list of timers.
     *
     * @param next the next timer in the list
     */
    void setNextTimer(@Nullable Timer next) {
        nextTimer = next;
    }
}
