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
package illarion.download.install.resources.db;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the database of the resources and its purpose is to keep track
 * of the files and resources already downloaded and ensure that they are
 * consistent.
 * 
 * @author Martin Karing
 * @version 1.01
 * @since 1.01
 */
public final class ResourceDatabase implements Externalizable {
    /**
     * The current version for the serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The map of resources stored in this database.
     */
    private Map<URL, DBResource> resources;

    /**
     * The constructor for this database that prepares it for proper operation.
     */
    public ResourceDatabase() {
        resources = new HashMap<URL, DBResource>();
    }

    /**
     * Add a file to a resource that is determined using a URL.
     * 
     * @param resourceURL the URL of the resource
     * @param file the file to add to this resource
     */
    public void addFile(final URL resourceURL, final File file) {
        if (resources.containsKey(resourceURL)) {
            resources.get(resourceURL).addFile(file);
        }
    }

    /**
     * Add a new resource to the database. This causes that any old resource
     * that was stored with the same URL is removed.
     * 
     * @param url the URL of the resource
     * @param lastChange the timestamp when this resource was last changed
     */
    public void addResource(final URL url, final long lastChange) {
        if (resources.containsKey(url)) {
            resources.remove(url);
        }
        resources.put(url, new DBResource(url, lastChange));
    }

    /**
     * Check if a URL is bound to a resource in the database.
     * 
     * @param url the URL to check
     * @return <code>true</code> in case the URL is assigned to a resource in
     *         this database
     */
    public boolean containsResource(final URL url) {
        return resources.containsKey(url);
    }

    /**
     * Get a resource bound to a URL.
     * 
     * @param url the URL
     * @return the resource connected to the URL or <code>null</code> in case
     *         the resource is not set
     */
    public DBResource getResource(final URL url) {
        return resources.get(url);
    }

    /**
     * Read the database from a input stream.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(final ObjectInput in) throws IOException,
        ClassNotFoundException {
        final long version = in.readLong();
        if (version == 1L) {
            resources = (Map<URL, DBResource>) in.readObject();
        }
    }

    /**
     * Write the database to a output stream.
     */
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeObject(resources);
    }

}
