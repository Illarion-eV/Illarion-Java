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
package illarion.download.install.resources.dev;

import illarion.download.install.resources.Resource;
import illarion.download.install.resources.libs.*;
import illarion.download.util.Lang;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This resource contains the Illarion Mapeditor.
 *
 * @author Martin Karing
 * @version 1.00
 * @since 1.00
 */
public final class Mapeditor implements DevelopmentResource {
    /**
     * The singleton instance of this class.
     */
    private static final Mapeditor INSTANCE = new Mapeditor();

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
     * The arguments that are passed to the virtual machine in case this
     * resource is part of the application.
     */
    private Collection<String> vmArguments;

    /**
     * Private constructor to avoid instances but the singleton instance.
     */
    private Mapeditor() {
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
                    "illarion_mapeditor.jar")); //$NON-NLS-1$

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
            dep.add(Javolution.getInstance());
            dep.add(Log4j.getInstance());
            dep.add(Trove.getInstance());
            dep.add(Substance.getInstance());
            dep.add(Flamingo.getInstance());
            dep.add(SubstanceFlamingo.getInstance());
            dep.add(EventBus.getInstance());
            dep.add(Common.getInstance());
            dep.add(SwingX.getInstance());
            dep.add(SubstanceSwingX.getInstance());
            dep.add(VorbisSPI.getInstance());

            dep.add(Items.getInstance());
            dep.add(Tables.getInstance());
            dep.add(Tiles.getInstance());
            dep.add(Sounds.getInstance());

            dependencies = dep;
        }
        return dependencies;
    }

    /**
     * As this resource is not start able this function will throw a exception
     * upon a call.
     */
    @SuppressWarnings("nls")
    @Override
    public String getLaunchClass() {
        return "illarion.mapedit.MapEditor";
    }

    @Override
    public String getName() {
        return Lang.getMsg(Mapeditor.class.getName());
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
                        + "illarion_mapeditor" + RESSOURCE_FILE_EXT)); //$NON-NLS-1$
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
            final Collection<String> vmArgs = new ArrayList<String>();
            //vmArgs.add("-Xmx1024m"); //$NON-NLS-1$
            //vmArgs.add("-Xms800m"); //$NON-NLS-1$

            vmArguments = vmArgs;
        }

        return vmArguments;
    }

    /**
     * This is a supporting library, so its not start able.
     */
    @Override
    public boolean isStartable() {
        return true;
    }
}
