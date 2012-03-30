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
package illarion.client.gui.util;

import illarion.common.util.Timer;

/**
 * Created by IntelliJ IDEA. User: Martin Karing Date: 28.03.12 Time: 23:06 To change this template use File | Settings
 * | File Templates.
 */
public abstract class AbstractMultiActionHelper
        implements Runnable {
    private final Timer timer;

    private int actionCount;

    protected AbstractMultiActionHelper(final int timeoutInMs) {
        timer = new Timer(timeoutInMs, this);
        timer.setRepeats(false);
        reset();
    }

    public final void reset() {
        timer.stop();
        actionCount = 0;
    }

    public final void pulse() {
        timer.restart();
        actionCount++;
    }

    @Override
    public void run() {
        timer.stop();
        executeAction(actionCount);
        reset();
    }

    public abstract void executeAction(final int count);
}
