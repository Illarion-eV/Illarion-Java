/*
 * This file is part of the Illarion Download Manager.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Download Manager is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Download Manager is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Download Manager. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.tasks.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import illarion.common.util.DirectoryManager;

import illarion.download.install.resources.Resource;
import illarion.download.install.resources.ResourceManager;
import illarion.download.util.OSDetection;

/**
 * The use of this class is to start a independent JVM that runs the chosen
 * application. This class requires calls that are system dependent.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class Launcher {
    /**
     * This set contains all arguments that need to be passed to the program
     * once it was launched.
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
     * The constructor that launches the resource that is selected in the
     * resource manager.
     */
    public Launcher() {
        this(ResourceManager.getInstance().getMainResource());
    }

    /**
     * The constructor and the possibility to select the resource that is
     * supposed to be launched with this.
     * 
     * @param resToLaunch the resource that is expected to be launched
     */
    @SuppressWarnings("nls")
    public Launcher(final Resource resToLaunch) {
        if (resToLaunch == null) {
            throw new IllegalArgumentException("resToLaunch must not be NULL.");
        }
        if (!resToLaunch.isStartable()) {
            throw new IllegalArgumentException(
                "resToLaunch has to be startable.");
        }
        resource = resToLaunch;

        classPath = new HashSet<File>();
        arguments = new HashSet<String>();
        vmArguments = new HashSet<String>();
    }

    /**
     * Calling this function causes the selected application to launch.
     * 
     * @return <code>true</code> in case launching the application was
     *         successful
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

        System.out.println("Calling: ");
        final int entries = callList.size();
        for (int i = 0; i < entries; i++) {
            System.out.print(callList.get(i));
            System.out.print(' ');
        }
        System.out.println();

        final ProcessBuilder pBuilder = new ProcessBuilder(callList);
        pBuilder.directory(DirectoryManager.getInstance().getUserDirectory());
        try {
            final Process proc = pBuilder.start();
            proc.getInputStream().close();
            proc.getOutputStream().close();
            proc.getErrorStream().close();
            // InputStream inStream = proc.getInputStream();
            // int value = 0;
            // while (true) {
            // value = inStream.read();
            // if (value < 0) {
            // System.out.println("EOF");
            // break;
            // }
            // if (value == 0xFF) {
            // System.out.println("READY");
            // break;
            // }
            // System.out.write(value);
            // }
            // inStream.close();
        } catch (final Exception e) {
            System.err.println("Fehler beim starten.");
            e.printStackTrace(System.err);
            return false;
        }

        return true;
    }

    /**
     * Build the class path string that contain a list of files pointing to each
     * file needed to include to this application.
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
     * This function is used to collect the data needed to launch the
     * application properly. The first call of this function needs to be done
     * with the main resource as this resource is the root of the dependency
     * tree.
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

        if (currentRes.getDependencies() != null) {
            for (final Resource nextRes : currentRes.getDependencies()) {
                collectLaunchData(nextRes);
            }
        }
    }

    /**
     * This small utility function takes care for escaping a path. This
     * operation is platform dependent so the result will differ on different
     * platforms.
     * 
     * @param orgPath the original plain path
     * @return the escaped path
     */
    private String escapePath(final String orgPath) {
        if (OSDetection.isWindows()) {
            return "\"".concat(orgPath).concat("\""); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return orgPath.replace(" ", "\\ "); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
