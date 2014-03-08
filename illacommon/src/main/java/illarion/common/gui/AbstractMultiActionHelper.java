/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
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
package illarion.common.gui;

import illarion.common.util.Timer;

import javax.annotation.Nonnull;

/**
 * This class is a helper that enables to GUI to handle things like double clicks.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractMultiActionHelper implements Runnable {
    /**
     * The internal timer that is used to group the events and fire the results.
     */
    @Nonnull
    private final Timer timer;

    /**
     * Amount of actions that were registered since the events were last fired.
     */
    private int actionCount;

    /**
     * The amount of clicks that are allowed at the maximum.
     * <p/>
     * {@code -1} means that there is no limit
     */
    private final int countLimit;

    /**
     * Create a instance of this class and set the timeout that should be used to group events.
     *
     * @param timeoutInMs the timeout value in milliseconds
     */
    protected AbstractMultiActionHelper(final int timeoutInMs) {
        this(timeoutInMs, -1);
    }

    /**
     * Create a instance of this class and set the timeout that should be used to group events.
     *
     * @param timeoutInMs the timeout value in milliseconds
     * @param limit       the amount of clicks allowed at the maximum
     */
    protected AbstractMultiActionHelper(Integer timeoutInMs, final int limit) {
        if (timeoutInMs == null) {
            timeoutInMs = 500;
        }
        timer = new Timer(timeoutInMs, this);
        timer.setRepeats(false);
        countLimit = limit;
        reset();
    }

    /**
     * Reset the helper.
     */
    public final void reset() {
        timer.stop();
        actionCount = 0;
    }

    /**
     * Send one action pulse to the helper.
     */
    public final void pulse() {
        actionCount++;
        if ((actionCount < countLimit) || (countLimit == -1)) {
            timer.restart();
        } else {
            run();
        }
    }

    /**
     * This function is called by the timer once the timeout occurred.
     */
    @Override
    public final void run() {
        timer.stop();
        executeAction(actionCount);
        reset();
    }

    /**
     * This function is called with the amount of registered actions as parameter once the timer times out.
     *
     * @param count the amount of actions since the last timeout. This value is 1 or larger.
     */
    public abstract void executeAction(int count);
}
