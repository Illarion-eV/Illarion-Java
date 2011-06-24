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

import org.apache.log4j.Logger;

/**
 * The default listener for the deadlock detector that prints the results to the
 * logging system.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class DefaultDeadlockListener implements
    ThreadDeadlockDetector.Listener {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(DefaultDeadlockListener.class);

    /**
     * Output the results of the deadlock detection.
     * 
     * @param threads the deadlocked threads
     */
    @Override
    @SuppressWarnings("nls")
    public void deadlockDetected(final Thread[] threads) {
        LOGGER.error("Deadlocked Threads:");
        LOGGER.error("-------------------");
        for (final Thread thread : threads) {
            LOGGER.error(thread);
            for (final StackTraceElement ste : thread.getStackTrace()) {
                LOGGER.error("\t" + ste);
            }
        }
    }
}
