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
package illarion.download.install.resources.dev;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import illarion.download.install.resources.Resource;
import illarion.download.install.resources.libs.Javolution;
import illarion.download.install.resources.libs.LWJGL;
import illarion.download.install.resources.libs.Log4j;
import illarion.download.install.resources.libs.Trove;
import illarion.download.install.resources.libs.VorbisSPI;
import illarion.download.util.Lang;

/**
 * This resource contains the Illarion Sound Engine.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class Sound implements DevelopmentResource {
    /**
     * The singleton instance of this class.
     */
    private static final Sound INSTANCE = new Sound();

    /**
     * The files that are needed to be added to the class path for this
     * resource.
     */
    private Collection<File> classpath;

    /**
     * The dependencies of this resource.
     */
    private Collection<Resource> dependencies;

    /**
     * The resources that are needed to be downloaded for this class.
     */
    private Collection<URL> resources;

    /**
     * Private constructor to avoid instances but the singleton instance.
     */
    private Sound() {
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
            cp.add(new File(DevelopmentDirectory.getInstance().getDirectory(),
                "illarion_sound.jar")); //$NON-NLS-1$

            classpath = cp;
        }
        return classpath;
    }

    /**
     * Get the dependencies of this resource.
     */
    @Override
    public Collection<Resource> getDependencies() {
        if (dependencies == null) {
            final Collection<Resource> dep = new ArrayList<Resource>();
            dep.add(LWJGL.getInstance());
            dep.add(Log4j.getInstance());
            dep.add(Trove.getInstance());
            dep.add(Javolution.getInstance());
            dep.add(VorbisSPI.getInstance());
            dep.add(Common.getInstance());

            dependencies = dep;
        }
        return dependencies;
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
        return Lang.getMsg(Sound.class.getName());
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
            try {
                res.add(new URL(ONLINE_PATH
                    + "illarion_sound" + RESSOURCE_FILE_EXT)); //$NON-NLS-1$
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
        return null;
    }

    /**
     * This is a supporting library, so its not start able.
     */
    @Override
    public boolean isStartable() {
        return false;
    }
}
