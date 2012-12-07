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

import org.apache.log4j.Logger;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This file is a single resource of that is stored in the database. It contains the references files that are part of
 * this resource and the information that were acquired from the server for this resource.
 *
 * @author Martin Karing
 * @version 1.01
 * @since 1.01
 */
public final class DBResource
        implements Serializable {
    /**
     * The current version for the serialization.
     */
    private static final long serialVersionUID = 2L;

    /**
     * The list of files that were extracted from this database resource.
     */
    private List<DBFile> files;

    /**
     * The time when the resource was last modified. This contains the value that is reported by the server upon
     * downloading the resource.
     */
    private long lastModified;

    /**
     * The URL that is the origin of this resource.
     */
    private URL sourceURL;

    /**
     * The logger that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(DBResource.class);

    /**
     * This constructor is needed for the de-serialization and for nothing else.
     */
    public DBResource() {
        // nothing to do here
    }

    /**
     * Create a new database resource.
     *
     * @param url     the URL that is the source of this resource
     * @param lastMod the time when the resource was last change, this date has to be reported by the server
     */
    public DBResource(final URL url, final long lastMod) {
        sourceURL = url;
        lastModified = lastMod;
        files = new ArrayList<DBFile>();
    }

    /**
     * Add a file to this resource. In case a detailed check is performed those files will be validated.
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
     * @return {@code true} if and only if all files passed the detail check
     */
    @SuppressWarnings("nls")
    public boolean checkFiles(final ResourceCheckLevel level) {
        final long time = System.currentTimeMillis();
        boolean result = true;
        int fileCount = 0;
        for (final DBFile file : files) {
            switch (level) {
                case simpleCheck:
                    result = file.exists();
                    break;
                case detailedCheck:
                    result = file.exists() && file.isUpToDate();
                    break;
                case fullCheck:
                    result = file.exists() && file.isUpToDate() && file.hasValidChecksum();
            }
            fileCount++;
            if (!result) {
                break;
            }
        }

        if (LOGGER.isInfoEnabled()) {
            final StringBuilder builder = new StringBuilder();
            builder.append("Check of ");
            builder.append(sourceURL.getFile());
            builder.append(" took: ");
            builder.append(System.currentTimeMillis() - time);
            builder.append("ms (").append(fileCount).append(" files): ");
            if (result) {
                builder.append("SUCCESSFULL");
            } else {
                builder.append("FAILURE");
            }

            LOGGER.info(builder);
        }
        return result;
    }

    /**
     * Get the time when this resource was last modified on the server. This value can be used to determine if its
     * needed to download the resource again or not.
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
}
