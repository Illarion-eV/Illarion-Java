/*
 * This file is part of the Illarion Download Utility.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Download Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Download Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Download Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.launcher;

import illarion.common.util.DirectoryManager;
import illarion.common.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The use of this class is to start a independent JVM that runs the chosen application. This class requires calls
 * that are system dependent.
 *
 * @author Martin Karing
 */
public final class JavaLauncher {
    /**
     * This instance of the logger takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaLauncher.class);

    private boolean cancelExecution;

    /**
     * The reader that receives the console output of the launched application.
     */
    @Nullable
    private BufferedReader outputReader;

    /**
     * This text contains the error data in case the launch failed.
     */
    private String errorData;

    private final boolean snapshot;

    @Nonnull
    private final Timer launchTimer;

    /**
     * Construct a new launcher and set the classpath and the class to launch.
     */
    @SuppressWarnings("nls")
    public JavaLauncher(final boolean snapshot) {
        this.snapshot = snapshot;

        launchTimer = new Timer(10000, new Runnable() {
            @Override
            public void run() {
                cancelExecution = true;
                if (outputReader != null) {
                    try {
                        outputReader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        launchTimer.stop();
        launchTimer.setRepeats(false);
    }

    /**
     * Calling this function causes the selected application to launch.
     *
     * @return {@code true} in case launching the application was successful
     */
    @SuppressWarnings("nls")
    public boolean launch(@Nonnull final Collection<File> classpath, @Nonnull final String startupClass) {

        final String classPathString = buildClassPathString(classpath);

        final StringBuilder builder = new StringBuilder();
        final List<String> callList = new ArrayList<>();

        builder.append(System.getProperty("java.home"));
        builder.append(File.separatorChar).append("bin");
        builder.append(File.separatorChar).append("java");
        callList.add(escapePath(builder.toString()));

        callList.add("-classpath");
        callList.add(classPathString);

        if (snapshot) {
            callList.add("-Dillarion.server=devserver");
        }

        callList.add(startupClass);

        printCallList(callList);

        if (!launchCallList(callList)) {
            callList.set(0, "java");
            final String firstError = errorData;

            printCallList(callList);
            if (!launchCallList(callList)) {
                LOGGER.fatal("Error while launching application\n" + firstError);
                if (!firstError.equals(errorData)) {
                    LOGGER.fatal("Error while launching application\n" + errorData);
                    errorData = firstError + '\n' + errorData;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Build the class path string that contain a list of files pointing to each file needed to include to this
     * application.
     *
     * @return the string that represents the class path
     */
    @Nonnull
    private String buildClassPathString(@Nonnull final Collection<File> classpath) {
        if (classpath.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (final File classPathFile : classpath) {
            builder.append(classPathFile.getAbsolutePath());
            builder.append(File.pathSeparatorChar);
        }
        builder.setLength(builder.length() - 1);
        return escapePath(builder.toString());
    }

    /**
     * This small utility function takes care for escaping a path. This operation is platform dependent so the result
     * will differ on different platforms.
     *
     * @param orgPath the original plain path
     * @return the escaped path
     */
    private static String escapePath(@Nonnull final String orgPath) {
        if (OSDetection.isWindows()) {
            if (orgPath.contains(" ")) {
                return '"' + orgPath + '"';
            }
            return orgPath;
        }
        return orgPath.replace(" ", "\\ ");
    }

    /**
     * Print the call list to the logger.
     *
     * @param callList the call list to print
     */
    private static void printCallList(@Nonnull final List<String> callList) {
        if (LOGGER.isDebugEnabled()) {
            final StringBuilder debugBuilder = new StringBuilder();
            debugBuilder.append("Calling: ");
            debugBuilder.append(System.getProperty("line.separator"));

            for (final String aCallList : callList) {
                debugBuilder.append(aCallList).append(' ');
            }
            LOGGER.debug(debugBuilder);
        }
    }

    /**
     * Launch the specified call list.
     *
     * @param callList launch the call list
     * @return {@code true} in case the launch was successful
     */
    private boolean launchCallList(final List<String> callList) {
        try {
            final ProcessBuilder pBuilder = new ProcessBuilder(callList);
            pBuilder.directory(DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User));
            pBuilder.redirectErrorStream(true);
            final Process proc = pBuilder.start();
            proc.getOutputStream().close();

            final StringBuilder outputBuffer = new StringBuilder();
            outputReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            launchTimer.start();
            cancelExecution = false;

            while (true) {
                if (cancelExecution) {
                    throw new IOException("Response Timeout.");
                }
                final String line = outputReader.readLine();
                if (line == null) {
                    errorData = outputBuffer.toString().trim();
                    return false;
                }
                if (line.endsWith("Startup done.")) {
                    outputReader.close();
                    return true;
                }
                outputBuffer.append(line);
                outputBuffer.append('\n');
            }
        } catch (@Nonnull final Exception e) {
            final StringWriter sWriter = new StringWriter();
            final PrintWriter writer = new PrintWriter(sWriter);
            e.printStackTrace(writer);
            writer.flush();
            errorData = sWriter.toString();
            return false;
        } finally {
            if (outputReader != null) {
                try {
                    outputReader.close();
                } catch (@Nonnull final IOException e) {
                    // nothing
                }
            }
            outputReader = null;
        }
    }

    /**
     * Get the information about the launch error.
     *
     * @return the string containing the data about the crash
     */
    public String getErrorData() {
        return errorData;
    }
}
