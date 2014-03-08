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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        Iterable<Path> executablePaths;
        if (OSDetection.isMacOSX()) {
            executablePaths = new MacOsXJavaExecutableIterable();
        } else {
            executablePaths = new JavaExecutableIterable();
        }

        for (Path executable : executablePaths) {
            if (isJavaExecutableWorking(executable)) {
                final List<String> callList = new ArrayList<>();
                callList.add(escapePath(executable.toString()));
                callList.add("-classpath");
                callList.add(classPathString);
                if (snapshot) {
                    callList.add("-Dillarion.server=devserver");
                }
                callList.add(startupClass);
                printCallList(callList);
                if (launchCallList(callList)) {
                    return true;
                } else {
                    LOGGER.error("Error while launching application: {}" + errorData);
                }
            }
        }
        return false;
    }

    /**
     * This function is used to check if the java executable has the proper version.
     *
     * @param executable the path to the executable
     * @return {@code true} in case java meets the required specifications
     */
    private boolean isJavaExecutableWorking(@Nonnull final Path executable) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(executable.toString(), "-version");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.getOutputStream().close();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("java version")) {
                        Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)_(\\d+)").matcher(line);
                        if (matcher.find()) {
                            int mainVersion = Integer.parseInt(matcher.group(1));
                            int majorVersion = Integer.parseInt(matcher.group(2));
                            int minorVersion = Integer.parseInt(matcher.group(3));
                            int buildNumber = Integer.parseInt(matcher.group(4));

                            LOGGER.info("Matched Java version to {}.{}.{}_b{}", mainVersion, majorVersion, minorVersion,
                                        buildNumber);

                            return mainVersion >= 1 && majorVersion >= 7 && buildNumber >= 21;
                        }
                    }
                }
            }
            process.destroy();
        } catch (IOException e) {
            LOGGER.error("Launching {} failed.", executable);
        }
        return false;
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
            LOGGER.debug(debugBuilder.toString());
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

            File workingDirectory = DirectoryManager.getInstance().getWorkingDirectory();
            if (workingDirectory != null) {
                pBuilder.directory(workingDirectory);
            }
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
