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

import org.apache.log4j.Logger;

/**
 * Debug function used to write time stamps to the relative to the start time of
 * the client into the log file.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class DebugTimer {
    /**
     * The error and debug logger of the client.
     */
    private static final Logger LOGGER = Logger.getLogger(DebugTimer.class);

    /**
     * The string used to write the line.
     */
    @SuppressWarnings("nls")
    private static final String MARK = "%1$s: %2$sms";

    /**
     * Storage of the starting time of the client.
     */
    private static long ms;

    /**
     * Private constructor so nothing creates a instance of this utility class.
     */
    private DebugTimer() {
        // avoid that any instance is created
    }

    /**
     * Mark a text entry in the log file with a time stamp.
     * 
     * @param txt the actual log entry, the time stamp is added
     */
    @SuppressWarnings("nls")
    public static synchronized void mark(final String txt) {
        if (ms == 0L) {
            throw new IllegalStateException(
                "Can't use the timer before it was started");
        }
        LOGGER.debug(String.format(MARK, txt,
            Long.toString(System.currentTimeMillis() - ms)));
    }

    /**
     * Mark the start time of the relative time calculation. This function needs
     * to be called once before any entries are written into the file using
     * {@link #mark(String)}.
     */
    public static void start() {
        ms = System.currentTimeMillis();

        if (ms == 0) {
            ms = 1;
        }
    }

    /**
     * Check if the DebugTimer was started already.
     * 
     * @return <code>true</code> in case the timer is started
     */
    public boolean isRunning() {
        return (ms != 0);
    }
}
