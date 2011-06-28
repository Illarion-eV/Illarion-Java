/*
 * This file is part of the Illarion Mapeditor.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javolution.util.FastMap;

import illarion.common.util.Location;
import illarion.common.util.tasks.TaskCancelException;
import illarion.common.util.tasks.TaskListener;

/**
 * This class is the main map database that maintains the data of all maps.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class MapDatabase implements Externalizable {
    /**
     * This is the filename of the file storing the database.
     */
    private static final String DB_FILENAME = ".mapdb"; //$NON-NLS-1$

    /**
     * The serialization UID of this database.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The directory the database is located in.
     */
    private File dir;

    /**
     * This map stores all maps known to this database related to their file
     * name.
     */
    private final Map<String, MapData> storage;

    /**
     * The standard constructor that that prepares this class for proper
     * operation.
     */
    private MapDatabase() {
        storage = new FastMap<String, MapData>();
    }

    /**
     * This class is able to load the map database from a directory. This
     * function may or may not load a previously stored database object. This
     * depends on if there is a valid database file stored in the directory.
     * 
     * @param dbDir the directory of the database
     * @return the created map database object
     */
    public static MapDatabase loadDatabase(final File dbDir) {
        final File dbFile = new File(dbDir, DB_FILENAME);

        if (!dbFile.exists() || !dbFile.isFile() || !dbFile.canRead()) {
            final MapDatabase retDb = new MapDatabase();
            retDb.setDirectory(dbDir);
            try {
                retDb.refreshFull(null);
            } catch (TaskCancelException e) {
                // ignore
            }
            return retDb;
        }

        ObjectInputStream oIn = null;
        MapDatabase retDb = null;
        try {
            oIn =
                new ObjectInputStream(new BufferedInputStream(
                    new GZIPInputStream(new FileInputStream(dbFile))));
            retDb = (MapDatabase) oIn.readObject();
        } catch (final FileNotFoundException e) {
            // nothing to do
        } catch (final IOException e) {
            // nothing to do
        } catch (final ClassNotFoundException e) {
            // nothing to do
        } finally {
            if (oIn != null) {
                try {
                    oIn.close();
                } catch (final IOException e) {
                    // nothing to do
                }
            }
        }

        if (retDb == null) {
            retDb = new MapDatabase();
            retDb.setDirectory(dbDir);
            try {
                retDb.refreshFull(null);
            } catch (TaskCancelException e) {
                // ignore
            }
            return retDb;
        }
        return retDb;
    }

    /**
     * Get the map that covers a specified location.
     * 
     * @param pos the position that shall be covered by the map
     * @return the map that covers the location or <code>null</code> in case
     *         none was found
     */
    public MapData getMapAt(final Location pos) {
        for (final MapData testMap : storage.values()) {
            if (testMap.isOnMap(pos)) {
                return testMap;
            }
        }
        return null;
    }

    /**
     * Get the map that is stored with a specified name.
     * 
     * @param name the name of the map
     * @return the map assigned to this name or <code>null</code> in case no map
     *         is assigned to this name
     */
    public MapData getMap(final String name) {
        return storage.get(name);
    }
    
    /**
     * Get a collection of all maps stored in this database. The returned
     * collection is read-only.
     * 
     * @return the view-collection of all maps stored in this database
     */
    public Collection<MapData> getAllMaps() {
        return Collections.unmodifiableCollection(storage.values());
    }
    
    /**
     * Get the path to the directory this database was load from.
     * 
     * @return the path of the directory of the database
     */
    public String getDirectory() {
        return dir.getAbsolutePath();
    }

    /**
     * Mark this database as the active one. Once this is done the database
     * manager will inform all parts of the editor that there is a new database
     * to maintain.
     */
    public void activate() {
        MapDatabaseManager.getInstance().activateDb(this);
    }

    /**
     * Read the contents of a map database from a file.
     */
    @Override
    public void readExternal(final ObjectInput in) throws IOException,
        ClassNotFoundException {
        final long version = in.readLong();

        if (version == 1L) {
            final int count = in.readInt();

            MapData data;
            for (int i = 0; i < count; i++) {
                data = (MapData) in.readObject();
                storage.put(data.getBaseFileName(), data);
            }
        } else {
            throw new IOException("Illegal database version"); //$NON-NLS-1$
        }
    }

    /**
     * Refresh the entire directory by reading the data of every single map
     * again.
     * 
     * @param listener The listener that receives updates of the progress of
     *            this function, its possible to set this to <code>null</code>
     *            in case no updates are needed
     * @throws TaskCancelException thrown in case this task is canceled
     */
    public void refreshFull(final TaskListener listener)
        throws TaskCancelException {
        if (listener != null) {
            listener.taskStarted();
        }

        if (!dir.isDirectory() && !dir.mkdirs()) {
            if (listener != null) {
                listener.taskCanceled();
            }
            return;
        }

        final File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File directory, final String name) {
                return name.endsWith(MapData.EXT_TILE_FILE);
            }
        });

        final FastMap<String, MapData> tempMap = FastMap.newInstance();

        final int totalProgress = files.length + 2;

        if (listener != null) {
            listener.taskProgress(0, totalProgress);
        }

        for (int cnt = 0; cnt < files.length; cnt++) {
            String fileName = files[cnt].getName();
            fileName =
                fileName.substring(0, fileName.length()
                    - MapData.EXT_TILE_FILE.length());

            MapData data;
            if (storage.containsKey(fileName)) {
                data = storage.get(fileName);
            } else {
                data = new MapData();
                data.setBaseFileName(fileName);
            }

            data.readData(dir);

            tempMap.put(fileName, data);

            if (listener != null) {
                listener.taskProgress(cnt, totalProgress);
            }
            if ((listener != null) && listener.cancelTask()) {
                throw new TaskCancelException();
            }
        }

        WarpLoader.getInstance().readGlobalWarpFile(dir);
        if (listener != null) {
            listener.taskProgress(totalProgress - 1, totalProgress);
        }

        storage.clear();
        storage.putAll(tempMap);
        FastMap.recycle(tempMap);

        WarpLoader.getInstance().layoutWarpPoints(this);
        if (listener != null) {
            listener.taskProgress(totalProgress, totalProgress);
        }

        if (listener != null) {
            listener.taskFinished();
        }
    }

    /**
     * Save the database to the filesystem.
     */
    public void save() {
        ObjectOutputStream oOut = null;
        try {
            oOut =
                new ObjectOutputStream(new BufferedOutputStream(
                    new GZIPOutputStream(new FileOutputStream(new File(dir,
                        DB_FILENAME)))));
            oOut.writeObject(this);
            oOut.flush();
        } catch (final IOException e) {
            // nothing
        } finally {
            if (oOut != null) {
                try {
                    oOut.close();
                } catch (final IOException e) {
                    // nothing
                }
            }
        }
    }

    /**
     * Write the contents of this database to a file.
     */
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeInt(storage.size());

        for (final MapData data : storage.values()) {
            out.writeObject(data);
        }
    }

    /**
     * Set the directory this map database is located in.
     * 
     * @param dbDir the directory of this database
     */
    private void setDirectory(final File dbDir) {
        if ((dbDir == null) || !dbDir.isDirectory()) {
            if ((dbDir != null) && !dbDir.mkdirs()) {
                throw new IllegalArgumentException(
                    "Not a valid directory for the database."); //$NON-NLS-1$
            }
        }
        dir = dbDir;
    }
}
