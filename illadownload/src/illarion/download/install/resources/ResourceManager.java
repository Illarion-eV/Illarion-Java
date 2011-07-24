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
package illarion.download.install.resources;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import illarion.common.util.DirectoryManager;

import illarion.download.install.resources.db.DBResource;
import illarion.download.install.resources.db.ResourceCheckLevel;
import illarion.download.install.resources.db.ResourceDatabase;
import illarion.download.tasks.download.DownloadManager;

/**
 * The resource manager is one of the key parts of the entire download system.
 * It keeps track of the state of all resources, compiles the list of required
 * resources from a starting resource and builds the java call.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.01
 */
public final class ResourceManager {
    /**
     * This is the singleton instance of this class.
     */
    private static final ResourceManager INSTANCE = new ResourceManager();

    /**
     * This is the filename of the file that stores the resources that are
     * already downloaded.
     */
    @SuppressWarnings("nls")
    private static final String RES_DB_FILE = "resources.dbz";

    /**
     * The list of resources that are required to execute the main resource.
     */
    private final List<Resource> dependingResources;

    /**
     * This variable contains the set resource of the application to launch.
     */
    private Resource mainResource;

    /**
     * This database contains all resources that are already downloaded and
     * present in the database.
     */
    private ResourceDatabase resourceDatabase;

    /**
     * This variable is used to store if the resource informations are dirty and
     * need to be written again.
     */
    private boolean resourcesDirty;

    /**
     * Private constructor to ensure that no instances but the singleton
     * instance are created.
     */
    private ResourceManager() {
        dependingResources = new ArrayList<Resource>();
        resourceDatabase = new ResourceDatabase();
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    /**
     * Find all required dependencies that are needed to execute the resource.
     */
    @SuppressWarnings("nls")
    public void discoverDependencies() {
        if (mainResource == null) {
            throw new IllegalStateException("No main resource set");
        }

        dependingResources.clear();
        discoverDependencies(mainResource);
    }

    /**
     * Get the selected main resource.
     * 
     * @return the main resource that was selected
     */
    public Resource getMainResource() {
        return mainResource;
    }

    /**
     * Report that a file was installed.
     * 
     * @param resourceURL the URL of the resource this file is assigned to
     * @param file the file itself
     */
    public void reportFileInstalled(final URL resourceURL, final File file) {
        resourceDatabase.addFile(resourceURL, file);
        resourcesDirty = true;
    }

    /**
     * Report that a new resource is fully installed and note that information
     * in the resource database.
     * 
     * @param url the URL that was the source of this resource
     * @param lastChanged the time when this resource was last changed
     */
    public void reportResourceInstalled(final URL url, final long lastChanged) {
        resourceDatabase.addResource(url, lastChanged);
        resourcesDirty = true;
    }

    /**
     * This function saves the current state of the resource manager to the file
     * on the hard disk. This function only has any effect in case the resource
     * database got actually changed.
     */
    public void saveResourceDatabase() {
        if (!resourcesDirty) {
            return;
        }

        final File dbFile =
            new File(DirectoryManager.getInstance().getDataDirectory(),
                RES_DB_FILE);

        if (dbFile.exists() && !dbFile.delete()) {
            /* Can't clear old database file. */
            return;
        }

        ObjectOutputStream out = null;
        try {
            out =
                new ObjectOutputStream(new BufferedOutputStream(
                    new FileOutputStream(dbFile)));
            out.writeObject(resourceDatabase);
            out.flush();
            resourcesDirty = false;
        } catch (final FileNotFoundException e) {
            // file not found, should not happen
        } catch (final IOException e) {
            // failed, writing the file, its sad, but nothing to be done about
            // that
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    // closing the file failed... ignore that.
                }
                out = null;
            }
        }
    }

    /**
     * This function places all resources that are needed in a download manager.
     * 
     * @param manager the manager that is supposed to receive the data
     */
    public void scheduleDownloads(final DownloadManager manager) {
        loadResourceDatabase();
        for (final URL url : mainResource.getRequiredRessources()) {
            scheduleDownloadImpl(mainResource.getName(),
                mainResource.getSubDirectory(), url, manager);
        }
        for (final Resource res : dependingResources) {
            if (res.getRequiredRessources() == null) {
                return;
            }
            for (final URL url : res.getRequiredRessources()) {
                scheduleDownloadImpl(res.getName(), res.getSubDirectory(),
                    url, manager);
            }
        }
    }

    /**
     * Set the main resource that is used by the application to start.
     * 
     * @param main the main resource
     * @throws IllegalArgumentException in case the parameter is
     *             <code>null</code>
     * @throws IllegalArgumentException in case the resource is not start able
     */
    @SuppressWarnings("nls")
    public void setMainResource(final Resource main) {
        if (main == null) {
            throw new IllegalArgumentException("resource is null");
        }
        if (!main.isStartable()) {
            throw new IllegalArgumentException(
                "Supplied resource is not startable");
        }

        mainResource = main;
    }

    /**
     * Discover the dependencies of the resource set as parameter. That function
     * is required to recursive search for the dependencies of all dependencies.
     * 
     * @param res the resource thats dependencies are discovered
     */
    private void discoverDependencies(final Resource res) {
        if (res.getDependencies() == null) {
            return;
        }

        for (final Resource depRes : res.getDependencies()) {
            if (!dependingResources.contains(depRes)) {
                dependingResources.add(depRes);
                discoverDependencies(depRes);
            }
        }
    }

    /**
     * This function is used to load the resource database.
     */
    private void loadResourceDatabase() {
        final File dbFile =
            new File(DirectoryManager.getInstance().getDataDirectory(),
                RES_DB_FILE);

        resourcesDirty = false;
        if (!dbFile.exists()) {
            /* Database yet not created, so there are no files in there. */
            return;
        }

        ObjectInputStream in = null;
        try {
            in =
                new ObjectInputStream(new BufferedInputStream(
                    new FileInputStream(dbFile)));
            resourceDatabase = (ResourceDatabase) in.readObject();
        } catch (final FileNotFoundException e) {
            // file not found, should not happen, but if, it does not matter
            e.printStackTrace();
        } catch (final IOException e) {
            // reading error, nothing to be done about
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            // class not found, could be caused by a invalid version of the DB
            // ignore it
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException e) {
                    // closing the stream failed, ignore that
                }
            }
        }
    }

    /**
     * This private function is used to add new downloads to a download manager.
     * It is only able to add one file at a time.
     * 
     * @param name the name of the download
     * @param dir the directory the files downloaded need to be extracted to
     * @param url the URL that is supposed to be downloaded
     * @param manager the download manager that maintains the download
     */
    private void scheduleDownloadImpl(final String name, final String dir,
        final URL url, final DownloadManager manager) {
        long timeout = 0L;
        if (resourceDatabase.containsResource(url)) {
            final DBResource res = resourceDatabase.getResource(url);
            if (res.checkFiles(ResourceCheckLevel.simpleCheck)) {
                timeout = res.getLastModified();
            }
        }

        final String fileName = url.getFile();
        manager.scheduleDownload(name, dir, url,
            new File(DirectoryManager.getInstance().getDataDirectory(),
                fileName.substring(fileName.lastIndexOf('/'))), timeout);
    }
}
