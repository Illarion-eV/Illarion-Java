/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class is used to easily setup the redirect from System.out and System.err to the log4j Loggers.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class StdOutToLog4J extends PrintStream {
    /**
     * The logger used to write the output.
     */
    private final Logger logger = Logger.getLogger(StdOutToLog4J.class);

    /**
     * The priority used.
     */
    private final Priority usedPriority;

    /**
     * Prepare the output.
     */
    public static void setup() {
        System.setOut(new StdOutToLog4J(System.out, Priority.INFO));
        System.setErr(new StdOutToLog4J(System.err, Priority.ERROR));

        System.out.println("Test\n\r");
    }

    /**
     * Creates a new print stream.  This stream will not flush automatically.
     *
     * @param out The output stream to which values and objects will be
     *            printed
     * @see java.io.PrintWriter#PrintWriter(java.io.OutputStream)
     */
    private StdOutToLog4J(final OutputStream out, final Priority logPriority) {
        super(out);
        usedPriority = logPriority;
    }

    @Override
    public void print(final String message) {
        logger.log(usedPriority, message.trim());
        super.print(message);
    }
}
