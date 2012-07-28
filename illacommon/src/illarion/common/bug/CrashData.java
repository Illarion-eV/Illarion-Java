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
package illarion.common.bug;

import javolution.lang.Immutable;
import javolution.text.TextBuilder;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class is used to wrap the data that is collected about one crash into a
 * single object and prepare the value set for later usage. The object is
 * immutable. So once the values are set, they can't be changed anymore.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CrashData implements Immutable, Externalizable {
    /**
     * Serialization UID.
     */
    public static final long serialVersionUID = 1L;

    /**
     * The string that is used to introduce a caused part of the crash data.
     */
    @SuppressWarnings("nls")
    private static final String CAUSED = "Caused by: ";

    /**
     * The newline string that is used to create the backtrace.
     */
    @SuppressWarnings("nls")
    private static final String NL = "\n";

    /**
     * The tab string that is used to create the backtrace.
     */
    @SuppressWarnings("nls")
    private static final String TAB = "\t";

    /**
     * The human readable description of the problem
     */
    private String description;

    /**
     * The exception that caused the crash.
     */
    private String exception;

    /**
     * The name of the exception class.
     */
    private String exceptionName;

    /**
     * The name of the application that crashed.
     */
    private String name;

    /**
     * The thread the crash happened in.
     */
    private String threadName;

    /**
     * The version of the application that crashed
     */
    private String version;

    /**
     * The constructor that collects all data for such a crash data object.
     *
     * @param appName            the name of the application that crashed
     * @param appVersion         the version of the application that crashed
     * @param problemDescription the human readable description of the error.
     *                           This is not send to the server, its just displayed
     * @param crashThread        the thread that crashed
     * @param crashException     the exception that caused the crash
     */
    public CrashData(final String appName, final String appVersion,
                     final String problemDescription, final Thread crashThread,
                     final Throwable crashException) {
        name = appName;
        version = appVersion;
        threadName = crashThread.getName();

        final TextBuilder builder = TextBuilder.newInstance();

        Throwable current = crashException;
        while (current != null) {
            builder.append(current.getClass().getName());
            builder.append('(');
            builder.append('"');
            builder.append(current.getMessage());
            builder.append('"');
            builder.append(')');
            builder.append(NL);
            final StackTraceElement[] backtrace = current.getStackTrace();
            for (final StackTraceElement element : backtrace) {
                builder.append(TAB);
                builder.append(element.toString());
                builder.append(NL);
            }

            current = current.getCause();
            if (current != null) {
                builder.append(CAUSED);
            }
        }

        exception = builder.toString();
        TextBuilder.recycle(builder);
        description = problemDescription;
        exceptionName = crashException.getClass().getSimpleName();
    }

    /**
     * Constructor for deserialization.
     */
    public CrashData() {
    }

    /**
     * This method creates a string to identify the operation system.
     *
     * @return the string to identify the operation system
     */
    @SuppressWarnings("nls")
    static String getOSName() {
        final TextBuilder builder = TextBuilder.newInstance();
        builder.append(System.getProperty("os.name"));
        builder.append(' ');
        builder.append(System.getProperty("os.version"));
        builder.append(' ');
        builder.append(System.getProperty("os.arch"));

        final String result = builder.toString();
        TextBuilder.recycle(builder);
        return result;
    }

    /**
     * Get the name of the application the crash happened in.
     *
     * @return the name of the application
     */
    String getApplicationName() {
        return name;
    }

    /**
     * Get the version of the application the crash happened in.
     *
     * @return the version of the application
     */
    String getApplicationVersion() {
        return version;
    }

    /**
     * Get the description of the problem
     *
     * @return the description of the problem
     */
    String getDescription() {
        return description;
    }

    /**
     * The simple name of the exception.
     *
     * @return the simple name of the exception
     */
    String getExceptionName() {
        return exceptionName;
    }

    /**
     * Get the full stack backtrace of the crash.
     *
     * @return the stack backtrace
     */
    String getStackBacktrace() {
        return exception;
    }

    /**
     * Get the name of the thread the crash happened in.
     *
     * @return the name of the thread that crashed
     */
    String getThreadName() {
        return threadName;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeObject(name);
        out.writeObject(version);
        out.writeObject(threadName);
        out.writeObject(exception);
        out.writeObject(description);
        out.writeObject(exceptionName);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final long fileVersion = in.readLong();
        if (fileVersion == 1L) {
            name = (String) in.readObject();
            version = (String) in.readObject();
            threadName = (String) in.readObject();
            exception = (String) in.readObject();
            description = (String) in.readObject();
            exceptionName = (String) in.readObject();
        } else {
            throw new ClassNotFoundException("Class version invalid. Found: " + Long.toString(fileVersion) +
                    " expected: 1");
        }
    }
}
