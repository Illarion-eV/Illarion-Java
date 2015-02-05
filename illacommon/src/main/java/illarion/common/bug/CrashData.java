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

import javax.annotation.Nonnull;
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
public final class CrashData implements Externalizable {
    /**
     * Serialization UID.
     */
    public static final long serialVersionUID = 2L;

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
    private String description;

    /**
     * The exception that caused the crash.
     */
    @Nonnull
    private String exception;

    /**
     * The name of the exception class.
     */
    @Nonnull
    private String exceptionName;

    /**
     * The application identifier for the application that crashed.
     */
    @Nonnull
    private AppIdent applicationIdentifier;

    /**
     * The thread the crash happened in.
     */
    @Nonnull
    private String threadName;

    /**
     * The name of the mantis project this report is supposed to end up in.
     */
    @Nonnull
    private String mantisProject;

    /* Used only for deserialization. */
    public CrashData() {
    }

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
    AppIdent getApplicationIdentifier() {
        return applicationIdentifier;
    }

    /**
     * Get the description of the problem
     *
     * @return the description of the problem
     */
    @Nonnull
    String getDescription() {
        return description;
    }

    /**
     * The simple name of the exception.
     *
     * @return the simple name of the exception
     */
    @Nonnull
    String getExceptionName() {
        return exceptionName;
    }

    /**
     * Get the full stack backtrace of the crash.
     *
     * @return the stack backtrace
     */
    @Nonnull
    String getStackBacktrace() {
        return exception;
    }

    /**
     * Get the name of the thread the crash happened in.
     *
     * @return the name of the thread that crashed
     */
    @Nonnull
    String getThreadName() {
        return threadName;
    }

    @Nonnull
    String getMantisProject() {
        return mantisProject;
    }

    @Override
    public void writeExternal(@Nonnull ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeObject(applicationIdentifier);
        out.writeObject(threadName);
        out.writeObject(exception);
        out.writeObject(description);
        out.writeObject(exceptionName);
    }

    @Override
    public void readExternal(@Nonnull ObjectInput in) throws IOException, ClassNotFoundException {
        long fileVersion = in.readLong();
        if (fileVersion == 1L) {
            String name = (String) in.readObject();
            String version = (String) in.readObject();
            applicationIdentifier = new AppIdent(name, version);
            threadName = (String) in.readObject();
            exception = (String) in.readObject();
            description = (String) in.readObject();
            exceptionName = (String) in.readObject();
        } else if (fileVersion == 2L) {
            applicationIdentifier = (AppIdent) in.readObject();
            threadName = (String) in.readObject();
            exception = (String) in.readObject();
            description = (String) in.readObject();
            exceptionName = (String) in.readObject();
        } else {
            throw new ClassNotFoundException("Class version invalid. Found: " + Long.toString(fileVersion) +
                                                     " expected: 1 or 2");
        }
    }
}
