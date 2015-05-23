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
package illarion.download.launcher;

import illarion.common.util.DirectoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
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
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(JavaLauncher.class);

    /**
     * This text contains the error data in case the launch failed.
     */
    private String errorData;

    private final boolean snapshot;

    /**
     * Construct a new launcher and set the classpath and the class to launch.
     */
    public JavaLauncher(boolean snapshot) {
        this.snapshot = snapshot;
    }

    /**
     * Calling this function causes the selected application to launch.
     *
     * @return {@code true} in case launching the application was successful
     */
    public boolean launch(@Nonnull Collection<File> classpath, @Nonnull String startupClass) {
        String classPathString = buildClassPathString(classpath);

        Iterable<Path> executablePaths;
        if (OSDetection.isMacOSX()) {
            executablePaths = new MacOsXJavaExecutableIterable();
        } else {
            executablePaths = new JavaExecutableIterable();
        }

        for (Path executable : executablePaths) {
            if (isJavaExecutableWorking(executable)) {
                List<String> callList = new ArrayList<>();
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
                    log.error("Error while launching application: {}", errorData);
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
    private boolean isJavaExecutableWorking(@Nonnull Path executable) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(executable.toString(), "-version");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            //noinspection resource
            process.getOutputStream().close();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("java version")) {
                        Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)_(\\d+)").matcher(line);
                        if (matcher.find()) {
                            int mainVersion = Integer.parseInt(matcher.group(1));
                            int majorVersion = Integer.parseInt(matcher.group(2));
                            int minorVersion = Integer.parseInt(matcher.group(3));
                            int buildNumber = Integer.parseInt(matcher.group(4));

                            log.info("Matched Java version to {}.{}.{}_b{}", mainVersion, majorVersion, minorVersion,
                                    buildNumber);

                            return (mainVersion >= 1) && (majorVersion >= 7) && (buildNumber >= 21);
                        }
                    }
                }
            }
            process.destroy();
        } catch (IOException e) {
            log.error("Launching {} failed.", executable);
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
    private String buildClassPathString(@Nonnull Collection<File> classpath) {
        if (classpath.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (File classPathFile : classpath) {
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
    private static String escapePath(@Nonnull String orgPath) {
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
    private static void printCallList(@Nonnull List<String> callList) {
        if (log.isDebugEnabled()) {
            StringBuilder debugBuilder = new StringBuilder();
            debugBuilder.append("Calling: ");
            debugBuilder.append(System.getProperty("line.separator"));

            for (String aCallList : callList) {
                debugBuilder.append(aCallList).append(' ');
            }
            log.debug(debugBuilder.toString());
        }
    }

    /**
     * Launch the specified call list.
     *
     * @param callList launch the call list
     * @return {@code true} in case the launch was successful
     */
    private boolean launchCallList(@Nonnull List<String> callList) {
        try {
            ProcessBuilder pBuilder = new ProcessBuilder(callList);

            Path workingDirectory = DirectoryManager.getInstance().getWorkingDirectory();
            pBuilder.directory(workingDirectory.toFile());
            pBuilder.redirectErrorStream(true);
            Process proc = pBuilder.start();

            //noinspection EmptyTryBlock
            try (OutputStream ignored = proc.getOutputStream()) {
            }

            StringBuilder outputBuffer = new StringBuilder();
            try (final BufferedReader outputReader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream(), Charset.defaultCharset()))) {

                TimerTask timeoutTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            outputReader.close();
                        } catch (IOException ignored) {
                            // nothing to do
                        }
                    }
                };
                new Timer().schedule(timeoutTask, 10000);

                while (true) {
                    String line = outputReader.readLine();
                    if (line == null) {
                        errorData = outputBuffer.toString().trim();
                        return false;
                    }
                    if (line.endsWith("Startup done.")) {
                        timeoutTask.cancel();
                        outputReader.close();
                        return true;
                    }
                    outputBuffer.append(line);
                    outputBuffer.append('\n');
                }
            }
        } catch (@Nonnull Exception e) {
            StringWriter sWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(sWriter);
            e.printStackTrace(writer);
            writer.flush();
            errorData = sWriter.toString();
            return false;
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
