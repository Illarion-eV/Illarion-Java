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
package illarion.easynpc.crash;

import illarion.common.bug.CrashData;
import illarion.common.bug.CrashReporter;
import illarion.easynpc.Parser;

import javax.annotation.Nonnull;

/**
 * This crash handler differs from the rest of the crash handlers. It will bind
 * to the AWT system and get notified in case the AWT event queue fetches a
 * exception. To do so this class needs to be initialized. When doing so it
 * changes the system properties in order to bind to the AWT event queue.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AWTCrashHandler {
    /**
     * Prepare the crash handler to work properly. This causes that the system
     * property needed for this handler to work is set to this class.
     */
    public static void init() {
        System.setProperty("sun.awt.exception.handler", AWTCrashHandler.class.getName());
    }

    /**
     * This function is called by the AWT event queue in case a crash is
     * fetched. It will trigger the CrashReporter so the crash information is
     * send to the Illarion server.
     *
     * @param e the crash information
     */
    public void handle(@Nonnull Throwable e) {
        CrashData data = new CrashData(Parser.APPLICATION, "easyNPC Editor", "crash.awt", Thread.currentThread(), e);
        CrashReporter.getInstance().reportCrash(data, true);
    }
}
