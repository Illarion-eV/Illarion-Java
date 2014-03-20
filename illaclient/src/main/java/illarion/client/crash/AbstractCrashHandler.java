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
package illarion.client.crash;

import illarion.client.IllaClient;
import illarion.client.util.Lang;
import illarion.common.bug.CrashData;
import illarion.common.bug.CrashReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * This abstract class takes care for fetching uncaught exceptions and tries to
 * keep the client alive just in the way it supposed to be.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
abstract class AbstractCrashHandler implements UncaughtExceptionHandler {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCrashHandler.class);

    /**
     * The time since the last crash in milliseconds that need to have passed to
     * trigger a restart attempt. In case the time since the last crash is
     * shorter then this, the client will be shut down.
     */
    private static final int TIME_SINCE_LAST_CRASH = 60000;

    /**
     * This stores if there is currently a crash handled. In this case all other
     * crashes are ignored for now.
     */
    private boolean currentlyCrashing = false;

    /**
     * The time stored when this crash occurred last time. In case the same part
     * of the client crashes too frequent the entire client is shutdown.
     */
    private long lastCrash = 0;

    /**
     * Fetch a uncaught exception that was thrown and try restart the crashed
     * part of the client correctly.
     *
     * @param t the thread that crashed
     * @param e the error message it crashed with
     */
    @Override
    @SuppressWarnings("nls")
    public final void uncaughtException(@Nonnull final Thread t, @Nonnull final Throwable e) {
        LOGGER.error("Fetched uncaught exception: " + getCrashMessage(), e);
        if (currentlyCrashing) {
            return;
        }
        currentlyCrashing = true;
        final long oldLastCrash = lastCrash;
        lastCrash = System.currentTimeMillis();
        if ((lastCrash - oldLastCrash) < TIME_SINCE_LAST_CRASH) {
            crashClient();
            return;
        }

        reportError(t, e);

        if (restart()) {
            IllaClient.sendDisconnectEvent(getCrashMessage());
        }
        currentlyCrashing = false;
    }

    /**
     * Calling this function results in crashing the entire client. Call it only
     * in case there is no chance in keeping the client running.
     */
    @SuppressWarnings("nls")
    protected final void crashClient() {
        IllaClient.errorExit(Lang.getMsg(getCrashMessage()) + "\n"
                + Lang.getMsg("crash.fixfailed"));

        currentlyCrashing = false;
    }

    /**
     * Get the message that describes the problem that caused this crash
     * readable for a common player.
     *
     * @return the error message for this problem
     */
    @Nonnull
    protected abstract String getCrashMessage();

    /**
     * Restart the crashed thread and try to keep the client alive this way.
     * After this function is called the CrashHandler requests a reconnect.
     *
     * @return <code>true</code> in case reconnecting the client is needed.
     */
    protected abstract boolean restart();

    /**
     * Send the data about a crash to the Illarion server so some developer is
     * able to look over it.
     *
     * @param t the thread that crashed
     * @param e the reason of the crash
     */
    private void reportError(@Nonnull final Thread t, @Nonnull final Throwable e) {
        CrashReporter.getInstance().reportCrash(new CrashData(IllaClient.APPLICATION, getCrashMessage(), t, e));
    }
}
