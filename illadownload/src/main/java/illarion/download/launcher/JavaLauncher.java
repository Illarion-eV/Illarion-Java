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

import illarion.common.config.Config;
import illarion.common.util.DirectoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    @Nullable
    private String errorData;

    private final boolean snapshot;
    @Nonnull
    private final Config cfg;

    /**
     * Construct a new launcher and set the classpath and the class to launch.
     */
    public JavaLauncher(@Nonnull Config cfg, boolean snapshot) {
        this.snapshot = snapshot;
        this.cfg = cfg;
    }

    /**
     * Calling this function causes the selected application to launch.
     *
     * @return {@code true} in case launching the application was successful
     */
    public boolean launch(@Nonnull Collection<File> classpath, @Nonnull String startupClass) {
        String classPathString = buildClassPathString(classpath);

        Iterable<Path> executablePaths;
        executablePaths = OSDetection.isMacOSX() ? new MacOsXJavaExecutableIterable() : new JavaExecutableIterable();

        for (Path executable : executablePaths) {
            if (isJavaExecutableWorking(executable)) {
                List<String> callList = new ArrayList<>();
                callList.add(escapePath(executable.toString()));
                callList.add("-classpath");
                callList.add(classPathString);
                if (snapshot) {
                    callList.add("-Dillarion.server=devserver");
                }
                if (cfg.getBoolean("launchAggressive")) {
                    callList.add("-XX:+AggressiveOpts");
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
    private static boolean isJavaExecutableWorking(@Nonnull Path executable) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(executable.toString(), "-version");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {

                Pattern versionRegex = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)_(\\d+)");
                Optional<Matcher> versionMatcher = reader.lines()
                        .filter(s -> s.startsWith("java version"))
                        .map(versionRegex::matcher)
                        .filter(Matcher::find)
                        .findFirst();

                if (versionMatcher.isPresent()) {
                    Matcher matcher = versionMatcher.get();
                    int mainVersion = Integer.parseInt(matcher.group(1));
                    int majorVersion = Integer.parseInt(matcher.group(2));
                    int minorVersion = Integer.parseInt(matcher.group(3));
                    int buildNumber = Integer.parseInt(matcher.group(4));

                    log.info("Matched Java version to {}.{}.{}_b{}",
                            mainVersion, majorVersion, minorVersion, buildNumber);

                    return (mainVersion >= 1) && (majorVersion >= 8) && (buildNumber >= 0);
                }
            } finally {
                process.destroy();
            }
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
    private static String buildClassPathString(@Nonnull Collection<File> classpath) {
        if (classpath.isEmpty()) {
            return "";
        }

        String cp = classpath.stream().map(File::getAbsolutePath).collect(Collectors.joining(File.pathSeparator));
        return (cp == null) ? "" : escapePath(cp);
    }

    /**
     * This small utility function takes care for escaping a path. This operation is platform dependent so the result
     * will differ on different platforms.
     *
     * @param orgPath the original plain path
     * @return the escaped path
     */
    @Nonnull
    private static String escapePath(@Nonnull String orgPath) {
        if (OSDetection.isWindows()) {
            if (orgPath.contains(" ")) {
                return '"' + orgPath + '"';
            }
            return orgPath;
        }
        //noinspection DynamicRegexReplaceableByCompiledPattern
        return orgPath.replace(" ", "\\ ");
    }

    /**
     * Print the call list to the logger.
     *
     * @param callList the call list to print
     */
    private static void printCallList(@Nonnull Collection<String> callList) {
        if (log.isDebugEnabled()) {
            String prefix = "Calling: " + System.getProperty("line.separator");
            log.debug(callList.stream().collect(Collectors.joining(" ", prefix, "")));
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
    @Nullable
    public String getErrorData() {
        return errorData;
    }
}
