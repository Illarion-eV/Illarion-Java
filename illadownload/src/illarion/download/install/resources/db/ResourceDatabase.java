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
package illarion.download.install.resources.db;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the database of the resources and its purpose is to keep track of the files and resources already
 * downloaded and ensure that they are consistent.
 *
 * @author Martin Karing
 * @version 1.01
 * @since 1.01
 */
public final class ResourceDatabase
        implements Serializable {
    /**
     * The current version for the serialization.
     */
    private static final long serialVersionUID = 2L;

    /**
     * The map of resources stored in this database.
     */
    private final Map<String, DBResource> resources;

    /**
     * The constructor for this database that prepares it for proper operation.
     */
    public ResourceDatabase() {
        resources = new HashMap<String, DBResource>();
    }

    /**
     * Add a file to a resource that is determined using a URL.
     *
     * @param resourceURL the URL of the resource
     * @param file        the file to add to this resource
     */
    public void addFile(final URL resourceURL, final File file) {
        final String stringURL = resourceURL.toString();
        if (resources.containsKey(stringURL)) {
            resources.get(stringURL).addFile(file);
        }
    }

    /**
     * Add a new resource to the database. This causes that any old resource that was stored with the same URL is
     * removed.
     *
     * @param url        the URL of the resource
     * @param lastChange the timestamp when this resource was last changed
     */
    public void addResource(final URL url, final long lastChange) {
        final String stringURL = url.toString();
        if (resources.containsKey(stringURL)) {
            resources.remove(stringURL);
        }
        resources.put(stringURL, new DBResource(url, lastChange));
    }

    /**
     * Check if a URL is bound to a resource in the database.
     *
     * @param url the URL to check
     * @return <code>true</code> in case the URL is assigned to a resource in this database
     */
    public boolean containsResource(final URL url) {
        return resources.containsKey(url.toString());
    }

    /**
     * Get a resource bound to a URL.
     *
     * @param url the URL
     * @return the resource connected to the URL or {@code null} in case the resource is not set
     */
    public DBResource getResource(final URL url) {
        return resources.get(url.toString());
    }

    /**
     * This function clears the database. Use with care.
     */
    public void clear() {
        resources.clear();
    }
}
