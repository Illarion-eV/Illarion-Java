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
package illarion.download.install.resources.libs;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import illarion.common.util.DirectoryManager;

import illarion.download.install.resources.Resource;
import illarion.download.util.Lang;
import illarion.download.util.OSDetection;

/**
 * This resource contains the Gluegen library.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class Gluegen implements LibraryResource {
    /**
     * The singleton instance of this class.
     */
    private static final Gluegen INSTANCE = new Gluegen();

    /**
     * The files that are needed to be added to the class path for this
     * resource.
     */
    private Collection<File> classpath;

    /**
     * The resources that are needed to be downloaded for this class.
     */
    private Collection<URL> resources;

    /**
     * The arguments that are passed to the virtual machine in case this
     * resource is part of the application.
     */
    private Collection<String> vmArguments;

    /**
     * Private constructor to avoid instances but the singleton instance.
     */
    private Gluegen() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    public static Resource getInstance() {
        return INSTANCE;
    }

    /**
     * Generate and return the files needed to be added to the class path for
     * this resource.
     */
    @Override
    public Collection<File> getClassPath() {
        if (classpath == null) {
            final Collection<File> cp = new ArrayList<File>();
            cp.add(new File(LibraryDirectory.getInstance().getDirectory(),
                "gluegen-rt.jar")); //$NON-NLS-1$

            classpath = cp;
        }
        return classpath;
    }

    /**
     * This resource does not depend on anything else. So this function returns
     * <code>null</code> at all times
     */
    @Override
    public Collection<Resource> getDependencies() {
        return null;
    }

    /**
     * As this resource is not start able this function will throw a exception
     * upon a call.
     */
    @Override
    public String getLaunchClass() {
        throw new IllegalStateException();
    }

    @Override
    public String getName() {
        return Lang.getMsg(Gluegen.class.getName());
    }

    /**
     * This resource does not require and program arguments. So this function
     * will return <code>null</code> in any case.
     */
    @Override
    public Collection<String> getProgramArgument() {
        return null;
    }

    /**
     * Generates and returns the list of files that need to be downloaded to get
     * this resource working.
     */
    @Override
    public Collection<URL> getRequiredRessources() {
        if (resources == null) {
            final Collection<URL> res = new ArrayList<URL>();

            if (OSDetection.isOsUnknown() || OSDetection.isSolaris()) {
                return null;
            }

            final StringBuilder builder = new StringBuilder();
            builder.append(ONLINE_PATH);
            builder.append("gluegen-"); //$NON-NLS-1$
            builder.append(OSDetection.getOsValue());
            builder.append('-');

            if (OSDetection.isMacOSX()) {
                builder.append("universal"); //$NON-NLS-1$
            } else if (OSDetection.isArchUnknown()) {
                return null;
            } else {
                builder.append(OSDetection.getArchValue());
            }
            builder.append(RESSOURCE_FILE_EXT);

            try {
                res.add(new URL(builder.toString()));
            } catch (final Exception e) {
                // Catch everything and do nothing!
            }
            resources = res;
        }
        return resources;
    }

    /**
     * The name of the directory the downloaded files are supposed to be
     * extracted to.
     */
    @Override
    public String getSubDirectory() {
        return LOCAL_LIB_PATH;
    }

    /**
     * Generate and return the list of virtual machine arguments that are passed
     * to java when the function is called.
     */
    @Override
    public Collection<String> getVMArguments() {
        if (vmArguments == null) {
            if (OSDetection.isOsUnknown() || OSDetection.isSolaris()) {
                return null;
            }
            if (!OSDetection.isMacOSX() && OSDetection.isArchUnknown()) {
                return null;
            }

            final Collection<String> vmArgs = new ArrayList<String>();
            vmArgs.add("-Dillarion.components.avaiable.gluegen=true"); //$NON-NLS-1$
            vmArgs.add("-Djava.library.path=" //$NON-NLS-1$
                + DirectoryManager.getInstance().getDataDirectory()
                + File.separator + LOCAL_LIB_PATH + ""); //$NON-NLS-1$

            vmArguments = vmArgs;
        }

        return vmArguments;
    }

    /**
     * This is a supporting library, so its not start able.
     */
    @Override
    public boolean isStartable() {
        return false;
    }
}
