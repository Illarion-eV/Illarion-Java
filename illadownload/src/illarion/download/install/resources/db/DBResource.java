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
import java.util.ArrayList;
import java.util.List;

/**
 * This file is a single resource of that is stored in the database. It contains
 * the references files that are part of this resource and the informations
 * that were acquired from the server for this resource.
 * 
 * @author Martin Karing
 * @version 1.01
 * @since 1.01
 */
public final class DBResource implements Externalizable {
    /**
     * The current version for the serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The list of files that were extracted from this database resource.
     */
    private List<DBFile> files;

    /**
     * The time when the resource was last modified. This contains the value
     * that is reported by the server upon downloading the resource.
     */
    private long lastModified;

    /**
     * The URL that is the origin of this resource.
     */
    private URL sourceURL;

    /**
     * This constructor is needed for the de-serialization and for nothing else.
     */
    public DBResource() {
        // nothing to do here
    }

    /**
     * Create a new database resource.
     * 
     * @param url the URL that is the source of this resource
     * @param lastMod the time when the resource was last change, this date has
     *            to be reported by the server
     */
    public DBResource(final URL url, final long lastMod) {
        sourceURL = url;
        lastModified = lastMod;
        files = new ArrayList<DBFile>();
    }

    /**
     * Add a file to this resource. In case a detailed check is performed those
     * files will be validated.
     * 
     * @param file the file to check
     */
    public void addFile(final File file) {
        final DBFile fileEntry = new DBFile(file);
        if (!files.contains(fileEntry)) {
            files.add(fileEntry);
        }
    }

    /**
     * Check the files on different detail levels.
     * 
     * @param level the level to the check
     * @return <code>true</code> if and only if all files passed the detail
     *         check
     */
    @SuppressWarnings("nls")
    public boolean checkFiles(final ResourceCheckLevel level) {
        final long time = System.currentTimeMillis();
        boolean result = true;
        int fileCount = 0;
        for (final DBFile file : files) {
            switch (level) {
                case simpleCheck:
                    result &= file.exists();
                    break;
                case detailedCheck:
                    result &= (file.exists() && file.upToDate());
                    break;
                case fullCheck:
                    result &=
                        (file.exists() && file.upToDate() && file
                            .validateChecksum());
            }
            fileCount++;
            if (!result) {
                break;
            }
        }
        System.out.println("Check or " + sourceURL.getFile() + " took: "
            + Long.toString(System.currentTimeMillis() - time) + "ms" + " ("
            + Integer.toString(fileCount) + " files): "
            + (result ? "successfull" : "failed"));
        return result;
    }

    /**
     * Get the time when this resource was last modified on the server. This
     * value can be used to determine if its needed to download the resource
     * again or not.
     * 
     * @return the timestamp of the last change of the resource
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * Get the URL that is the source of this resource.
     * 
     * @return the URL that is the source of this resource
     */
    public URL getSource() {
        return sourceURL;
    }

    /**
     * Read the object from a input stream.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(final ObjectInput in) throws IOException,
        ClassNotFoundException {
        final long version = in.readLong();
        if (version == 1L) {
            sourceURL = (URL) in.readObject();
            lastModified = in.readLong();
            files = (List<DBFile>) in.readObject();
        }
    }

    /**
     * Write the object to an output stream.
     */
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeObject(sourceURL);
        out.writeLong(lastModified);
        out.writeObject(files);
    }

}
