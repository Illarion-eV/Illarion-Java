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
package illarion.common.bug;

import illarion.common.util.AppIdent;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
/**
 * This class is used to wrap the data that is collected about one crash into a
 * single object and prepare the value set for later usage. The object is
 * immutable. So once the values are set, they can't be changed anymore.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CrashData {
    /**
     * The string that is used to introduce a caused part of the crash data.
     */
    private static final String CAUSED = "Caused by: ";

    /**
     * The newline string that is used to create the backtrace.
     */
    private static final String NL = "\n";

    /**
     * The tab string that is used to create the backtrace.
     */
    private static final String TAB = "\t";

    /**
     * The human readable description of the problem
     */
    @Nonnull
    private final String description;

    /**
     * The exception that caused the crash.
     */
    @Nonnull
    private final String exception;

    /**
     * The name of the exception class.
     */
    @Nonnull
    private final String exceptionName;

    /**
     * The application identifier for the application that crashed.
     */
    @Nonnull
    private final AppIdent applicationIdentifier;

    /**
     * The thread the crash happened in.
     */
    @Nonnull
    private final String threadName;

    /**
     * The name of the mantis project this report is supposed to end up in.
     */
    @Nonnull
    private final String mantisProject;

    /**
     * The constructor that collects all data for such a crash data object.
     *
     * @param appIdent the application identifier
     * @param problemDescription the human readable description of the error.
     * This is not send to the server, its just displayed
     * @param crashThread the thread that crashed
     * @param crashException the exception that caused the crash
     */
    public CrashData(
            @Nonnull AppIdent appIdent,
            @Nonnull String mantisProject,
            @Nonnull String problemDescription,
            @Nonnull Thread crashThread,
            @Nonnull Throwable crashException) {
        applicationIdentifier = appIdent;
        threadName = crashThread.getName();
        this.mantisProject = mantisProject;

        StringBuilder builder = new StringBuilder();

        Throwable current = crashException;
        while (current != null) {
            builder.append(current.getClass().getName());
            builder.append('(');
            builder.append('"');
            builder.append(current.getMessage());
            builder.append('"');
            builder.append(')');
            builder.append(NL);
            StackTraceElement[] backtrace = current.getStackTrace();
            for (StackTraceElement element : backtrace) {
                builder.append(TAB);
                builder.append(element);
                builder.append(NL);
            }

            current = current.getCause();
            if (current != null) {
                builder.append(CAUSED);
            }
        }

        exception = builder.toString();
        description = problemDescription;
        exceptionName = crashException.getClass().getSimpleName();
    }

    /**
     * This method creates a string to identify the operation system.
     *
     * @return the string to identify the operation system
     */
    @Nonnull
    static String getOSName() {

        return System.getProperty("os.name") + ' ' + System.getProperty("os.version") + ' ' +
                System.getProperty("os.arch");
    }

    /**
     * Get the identifier of the application.
     *
     * @return the identifier of the application
     */
    @Nonnull
    @Contract(pure = true)
    AppIdent getApplicationIdentifier() {
        return applicationIdentifier;
    }

    /**
     * Get the description of the problem
     *
     * @return the description of the problem
     */
    @Nonnull
    @Contract(pure = true)
    String getDescription() {
        return description;
    }

    /**
     * The simple name of the exception.
     *
     * @return the simple name of the exception
     */
    @Nonnull
    @Contract(pure = true)
    String getExceptionName() {
        return exceptionName;
    }

    /**
     * Get the full stack backtrace of the crash.
     *
     * @return the stack backtrace
     */
    @Nonnull
    @Contract(pure = true)
    String getStackBacktrace() {
        return exception;
    }

    /**
     * Get the name of the thread the crash happened in.
     *
     * @return the name of the thread that crashed
     */
    @Nonnull
    @Contract(pure = true)
    String getThreadName() {
        return threadName;
    }

    @Nonnull
    @Contract(pure = true)
    String getMantisProject() {
        return mantisProject;
    }
}
