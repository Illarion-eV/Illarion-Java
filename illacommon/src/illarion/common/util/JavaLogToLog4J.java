/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class JavaLogToLog4J extends Handler {

    public static void setup() {
        applyLoggingHandlers(java.util.logging.Logger.getGlobal());
        applyLoggingHandlers(java.util.logging.Logger.getAnonymousLogger());
        applyLoggingHandlers(java.util.logging.Logger.getLogger(JavaLogToLog4J.class.getName()));
    }

    private static void applyLoggingHandlers(final java.util.logging.Logger logger) {
        final Handler[] handlers = logger.getHandlers();
        if (handlers.length > 0) {
            final Handler handler = handlers[0];
            if (handler instanceof JavaLogToLog4J) {
                return;
            }
            logger.removeHandler(handler);

            if (handlers.length == 1) {
                logger.addHandler(new JavaLogToLog4J());
            } else {
                applyLoggingHandlers(logger);
            }
        }

        final java.util.logging.Logger parent = logger.getParent();
        if ((parent == null) || parent.equals(logger)) {
            return;
        }
        applyLoggingHandlers(parent);
    }

    @Override
    public void publish(final LogRecord record) {
        final Logger log4j = getLogger(record.getLoggerName());
        final Priority priority = toLog4j(record.getLevel());
        log4j.log(priority, toLog4jMessage(record), record.getThrown());
    }

    static Logger getLogger(final String loggerName) {
        return Logger.getLogger(loggerName);
    }

    private static String toLog4jMessage(final LogRecord record) {
        String message = record.getMessage();
        try {
            final Object[] parameters = record.getParameters();
            if ((parameters != null) && (parameters.length != 0)) {
                if (message.contains("{0}") ||
                        message.contains("{1}") ||
                        message.contains("{2}") ||
                        message.contains("{3}")) {
                    message = MessageFormat.format(message, parameters);
                }
            }
        } catch (Exception ignored) {
        }
        return message;
    }

    private static org.apache.log4j.Level toLog4j(final Level level) {//converts levels
        if (Level.SEVERE == level) {
            return org.apache.log4j.Level.ERROR;
        } else if (Level.WARNING == level) {
            return org.apache.log4j.Level.WARN;
        } else if (Level.INFO == level) {
            return org.apache.log4j.Level.INFO;
        } else if (Level.OFF == level) {
            return org.apache.log4j.Level.OFF;
        }
        return org.apache.log4j.Level.OFF;
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }
}
