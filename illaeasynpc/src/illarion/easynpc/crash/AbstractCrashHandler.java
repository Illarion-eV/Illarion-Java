/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.crash;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

import illarion.easynpc.Lang;
import illarion.easynpc.Parser;
import illarion.easynpc.gui.MainFrame;

import illarion.common.bug.CrashData;
import illarion.common.bug.CrashReporter;

/**
 * This abstract class takes care for fetching uncaught exceptions and tries to
 * keep the editor alive just in the way it supposed to be.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
abstract class AbstractCrashHandler implements UncaughtExceptionHandler {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(AbstractCrashHandler.class);

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
    public final void uncaughtException(final Thread t, final Throwable e) {
        LOGGER.error("Fetched uncaught exception: " + getCrashMessage(), e);
        if (currentlyCrashing) {
            return;
        }
        currentlyCrashing = true;
        final long oldLastCrash = lastCrash;
        lastCrash = System.currentTimeMillis();
        if ((lastCrash - oldLastCrash) < TIME_SINCE_LAST_CRASH) {
            crashEditor();
            return;
        }

        reportError(t, e);

        restart();
        currentlyCrashing = false;
    }

    /**
     * Calling this function results in crashing the entire editor. Call it only
     * in case there is no chance in keeping the client running.
     */
    @SuppressWarnings("nls")
    protected final void crashEditor() {
        MainFrame.crashEditor(Lang.getMsg(getCrashMessage()) + "\n"
            + Lang.getMsg("crash.fixfailed"));

        currentlyCrashing = false;
    }

    /**
     * Get the message that describes the problem that caused this crash
     * readable for a common player.
     * 
     * @return the error message for this problem
     */
    protected abstract String getCrashMessage();

    /**
     * Restart the crashed thread and try to keep the client alive this way.
     * After this function is called the CrashHandler requests a reconnect.
     */
    protected abstract void restart();

    /**
     * Send the data about a crash to the Illarion server so some developer is
     * able to look over it.
     * 
     * @param t the thread that crashed
     * @param e the reason of the crash
     */
    private void reportError(final Thread t, final Throwable e) {
        CrashReporter.getInstance().reportCrash(
            new CrashData(Parser.APPLICATION, Parser.VERSION,
                getCrashMessage(), t, e));
    }
}
