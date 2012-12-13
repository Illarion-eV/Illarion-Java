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
package illarion.download.tasks.launch;

import illarion.common.util.DirectoryManager;
import illarion.download.install.resources.Resource;
import illarion.download.install.resources.ResourceManager;
import illarion.download.util.OSDetection;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The use of this class is to start a independent JVM that runs the chosen
 * application. This class requires calls that are system dependent.
 *
 * @author Martin Karing
 * @version 1.00
 * @since 1.00
 */
public final class Launcher implements ActionListener {
    /**
     * This set contains all arguments that need to be passed to the program once it was launched.
     */
    private final Set<String> arguments;

    /**
     * The list of files that need to be added to the class path.
     */
    private final Set<File> classPath;

    /**
     * This variable contains the resource to launch.
     */
    private final Resource resource;

    /**
     * This set contains all arguments that are passed to the virtual machine.
     */
    private final Set<String> vmArguments;

    /**
     * This instance of the logger takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Launcher.class);

    private final Timer launchTimer;

    private boolean cancelExecution;

    /**
     * The constructor that launches the resource that is selected in the resource manager.
     */
    public Launcher() {
        this(ResourceManager.getInstance().getMainResource());
    }

    /**
     * The constructor and the possibility to select the resource that is supposed to be launched with this.
     *
     * @param resToLaunch the resource that is expected to be launched
     */
    @SuppressWarnings("nls")
    public Launcher(final Resource resToLaunch) {
        if (resToLaunch == null) {
            throw new IllegalArgumentException("resToLaunch must not be NULL.");
        }
        if (!resToLaunch.isStartable()) {
            throw new IllegalArgumentException("resToLaunch has to be startable.");
        }
        resource = resToLaunch;

        classPath = new HashSet<File>();
        arguments = new HashSet<String>();
        vmArguments = new HashSet<String>();

        launchTimer = new Timer(10000, this);
        launchTimer.stop();
    }

    /**
     * Calling this function causes the selected application to launch.
     *
     * @return {@code true} in case launching the application was successful
     */
    @SuppressWarnings("nls")
    public boolean launch() {
        collectLaunchData(resource);

        final String classPathString = buildClassPathString();

        final StringBuilder builder = new StringBuilder();
        final List<String> callList = new ArrayList<String>();

        builder.append(System.getProperty("java.home"));
        builder.append(File.separatorChar).append("bin");
        builder.append(File.separatorChar).append("java");
        callList.add(escapePath(builder.toString()));
        builder.setLength(0);

        callList.add("-cp");
        callList.add(classPathString);

        callList.addAll(vmArguments);
        callList.add(resource.getLaunchClass());
        callList.addAll(arguments);

        if (LOGGER.isDebugEnabled()) {
            final StringBuilder debugBuilder = new StringBuilder();
            debugBuilder.append("Calling: ");
            debugBuilder.append(System.getProperty("line.separator"));

            for (final String aCallList : callList) {
                debugBuilder.append(aCallList).append(' ');
            }
            LOGGER.debug(debugBuilder);
        }

        final ProcessBuilder pBuilder = new ProcessBuilder(callList);
        pBuilder.directory(DirectoryManager.getInstance().getUserDirectory());
        pBuilder.redirectErrorStream(true);
        try {
            final Process proc = pBuilder.start();
            proc.getOutputStream().close();

            final StringBuilder outputBuffer = new StringBuilder();
            final BufferedReader outputReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            launchTimer.start();
            cancelExecution = false;

            while (true) {
                if (cancelExecution) {
                    throw new Exception("Response Timeout.");
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
        } catch (final Exception e) {
            LOGGER.fatal("Error while launching application", e);

            final PrintWriter writer = new PrintWriter(new StringWriter());
            e.printStackTrace(writer);
            errorData = writer.toString();
            return false;
        }
    }

    /**
     * This text contains the error data in case the launch failed.
     */
    private String errorData;

    /**
     * Get the information about the launch error.
     *
     * @return the string containing the data about the crash
     */
    public String getErrorData() {
        return errorData;
    }

    /**
     * Build the class path string that contain a list of files pointing to each file needed to include to this
     * application.
     *
     * @return the string that represents the class path
     */
    private String buildClassPathString() {
        if (classPath.isEmpty()) {
            return ""; //$NON-NLS-1$
        }
        final StringBuilder builder = new StringBuilder();
        for (final File classPathFile : classPath) {
            builder.append(escapePath(classPathFile.getAbsolutePath()));
            builder.append(File.pathSeparatorChar);
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    /**
     * This function is used to collect the data needed to launch the application properly. The first call of this
     * function needs to be done with the main resource as this resource is the root of the dependency tree.
     *
     * @param currentRes the currently handled resource
     */
    private void collectLaunchData(final Resource currentRes) {
        if (currentRes.getClassPath() != null) {
            classPath.addAll(currentRes.getClassPath());
        }
        if (currentRes.getProgramArgument() != null) {
            arguments.addAll(currentRes.getProgramArgument());
        }
        if (currentRes.getVMArguments() != null) {
            vmArguments.addAll(currentRes.getVMArguments());
        }
        vmArguments.add("-Djava.net.preferIPv4Stack=true");

        if (currentRes.getDependencies() != null) {
            for (final Resource nextRes : currentRes.getDependencies()) {
                collectLaunchData(nextRes);
            }
        }
    }

    /**
     * This small utility function takes care for escaping a path. This operation is platform dependent so the result
     * will differ on different platforms.
     *
     * @param orgPath the original plain path
     * @return the escaped path
     */
    private static String escapePath(final String orgPath) {
        if (OSDetection.isWindows()) {
            return '"' + orgPath + '"'; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return orgPath.replace(" ", "\\ "); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        cancelExecution = true;
        launchTimer.stop();
    }
}
