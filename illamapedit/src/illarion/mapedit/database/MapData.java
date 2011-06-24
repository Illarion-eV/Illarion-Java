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

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javolution.util.FastMap;

import illarion.common.util.Location;
import illarion.common.util.Rectangle;

/**
 * This is a entry of one map in the map database.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class MapData implements Externalizable {
    /**
     * A empty string.
     */
    static final String EMPTY_STR = "".intern(); //$NON-NLS-1$

    /**
     * The file extension of the items file.
     */
    static final String EXT_ITEM_FILE = ".items.txt"; //$NON-NLS-1$

    /**
     * The file extension of the meta data file.
     */
    static final String EXT_META_FILE = ".meta.txt"; //$NON-NLS-1$

    /**
     * The file extension of the tiles file.
     */
    static final String EXT_TILE_FILE = ".tiles.txt"; //$NON-NLS-1$

    /**
     * The header in the tiles data that marks the height of the map.
     */
    private static final String DATA_HEADER_HEIGHT = "H:"; //$NON-NLS-1$

    /**
     * The header in the tiles data that marks the level of the map.
     */
    private static final String DATA_HEADER_LEVEL = "L:"; //$NON-NLS-1$

    /**
     * The header in the tiles data that marks the width of the map.
     */
    private static final String DATA_HEADER_WIDTH = "W:"; //$NON-NLS-1$

    /**
     * The header in the tiles data that marks the x coordinate of the origin of
     * the map.
     */
    private static final String DATA_HEADER_X = "X:"; //$NON-NLS-1$

    /**
     * The header in the tiles data that marks the y coordinate of the origin of
     * the map.
     */
    private static final String DATA_HEADER_Y = "Y:"; //$NON-NLS-1$

    /**
     * The file extension of the warp fields file.
     */
    private static final String EXT_WARP_FILE = ".warps.txt"; //$NON-NLS-1$

    /**
     * The serialization UID of this map data object.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The description text of this map.
     */
    private String description;

    /**
     * The base filename of this map.
     */
    private String fileName;

    /**
     * The name of the group this map is a part of.
     */
    private String groupName;

    /**
     * The name of the user who performed the last change to this map.
     */
    private String lastAuthor;

    /**
     * The date when this map was last changed.
     */
    private Date lastChangedDate;

    /**
     * The bounding box of this map.
     */
    private Rectangle mapBounds;

    /**
     * The level this map is located on.
     */
    private int mapLevel;

    /**
     * The name of this map.
     */
    private String mapName;

    /**
     * The list of warp fields that have something to do with this map data.
     */
    private Map<Location, WarpData> warpFields;

    /**
     * Add a warp field to this map.
     * 
     * @param field the field to add
     */
    public void addWarpField(final WarpData field) {
        if (warpFields == null) {
            warpFields = FastMap.newInstance();
        }

        final Location loc = field.getMapLocation(this);
        if (loc != null) {
            warpFields.put(loc, field);
        } else {
            throw new IllegalArgumentException(
                "The warpfield supplied is not prepared properly."); //$NON-NLS-1$
        }
    }

    /**
     * Get the base file name of this map.
     * 
     * @return the base file name
     */
    public String getBaseFileName() {
        if (fileName == null) {
            return EMPTY_STR;
        }
        return fileName;
    }

    /**
     * Get the bounding box of this map. This operation will fetch a new
     * instance of the rectangle object that can be handled any way wanted.
     * 
     * @return the bounding box stored in a new rectangle object
     */
    public Rectangle getBoundingBox() {
        return getBoundingBox(Rectangle.getInstance());
    }

    /**
     * Get the bounding box of this map.
     * 
     * @param storage the rectangle object that is used to store the values in
     * @return the rectangle instance that was set as parameter
     */
    public Rectangle getBoundingBox(final Rectangle storage) {
        if (mapBounds == null) {
            storage.reset();
        } else {
            storage.set(mapBounds);
        }
        return storage;
    }

    /**
     * Get the description of this map.
     * 
     * @return the description of this map
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the name of the group this map is a part of.
     * 
     * @return the group name of this map
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Get the name of the file that stores the items of this map.
     * 
     * @return the file that stores the items
     */
    public String getItemFileName() {
        return getBaseFileName().concat(EXT_ITEM_FILE);
    }

    /**
     * Get the name of the last person to changed this map.
     * 
     * @return the name of the last author
     */
    public String getLastAuthor() {
        return lastAuthor;
    }

    /**
     * Get the data of the last change of this map.
     * 
     * @return the last change data of this map
     */
    public Date getLastChangeDate() {
        if (lastChangedDate == null) {
            lastChangedDate = new Date();
        }
        return lastChangedDate;
    }

    /**
     * Get the height of the map.
     * 
     * @return the height of the map
     */
    public int getMapHeight() {
        if (mapBounds == null) {
            return 0;
        }
        return mapBounds.getHeight();
    }

    /**
     * Get the level this map is located on.
     * 
     * @return the level the map is located on
     */
    public int getMapLevel() {
        return mapLevel;
    }

    /**
     * Get the name of this map.
     * 
     * @return the name of this map
     */
    public String getMapName() {
        if (mapName == null) {
            return getBaseFileName();
        }
        return mapName;
    }

    /**
     * Get the width of the map.
     * 
     * @return the width of the map
     */
    public int getMapWidth() {
        if (mapBounds == null) {
            return 0;
        }
        return mapBounds.getWidth();
    }

    /**
     * Get the name of the file that stores the meta data of this map.
     * 
     * @return the file that stores the meta data
     */
    public String getMetaFileName() {
        return getBaseFileName().concat(EXT_META_FILE);
    }

    /**
     * Get the origin location of this map. This will fetch a new instance of
     * the location object and return it.
     * 
     * @return the origin of the map
     */
    public Location getOrigin() {
        return getOrigin(Location.getInstance());
    }

    /**
     * Get the origin location of this map.
     * 
     * @param storage the location object that is used to store the origin
     * @return the same instance of the location object that is hand over as
     *         parameter
     */
    public Location getOrigin(final Location storage) {
        if (mapBounds == null) {
            storage.setSC(0, 0, mapLevel);
        } else {
            storage.setSC(mapBounds.getX(), mapBounds.getY(), mapLevel);
        }
        return storage;
    }

    /**
     * Get the x coordinate of the origin of this map.
     * 
     * @return the x coordinate of the maps origin
     */
    public int getOriginX() {
        if (mapBounds == null) {
            return 0;
        }
        return mapBounds.getX();
    }

    /**
     * Get the y coordinate of the maps origin.
     * 
     * @return the y coordinate of the maps origin
     */
    public int getOriginY() {
        if (mapBounds == null) {
            return 0;
        }
        return mapBounds.getY();
    }

    /**
     * Get the name of the file that stores the tiles of this map.
     * 
     * @return the file that stores the tiles
     */
    public String getTileFileName() {
        return getBaseFileName().concat(EXT_TILE_FILE);
    }

    /**
     * Get the name of the file that stores the warp fields of this map.
     * 
     * @return the file that stores the warp fields
     */
    public String getWarpFileName() {
        return getBaseFileName().concat(EXT_WARP_FILE);
    }

    /**
     * Check if a location is on that map.
     * 
     * @param pos the position to check
     * @return <code>true</code> in case the location is part of this map
     */
    public boolean isOnMap(final Location pos) {
        if (mapBounds == null) {
            return false;
        }
        if (pos == null) {
            return false;
        }
        if (mapLevel != pos.getScZ()) {
            return false;
        }
        return mapBounds.isInside(pos.getScX(), pos.getScY());
    }

    /**
     * Read or update all required data from the files stored in the named
     * directory.
     * 
     * @param directory the directory to read the data from
     */
    public void readData(final File directory) {
        readTilesData(directory);
        readMetaData(directory);
        readWarpData(directory);
    }

    /**
     * Read the data of this map from a external stream.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(final ObjectInput in) throws IOException,
        ClassNotFoundException {

        final long serial = in.readLong();
        if (serial != serialVersionUID) {
            throw new IOException("Illegal data in the input stream."); //$NON-NLS-1$
        }

        fileName = (String) in.readObject();
        mapName = (String) in.readObject();
        description = (String) in.readObject();
        groupName = (String) in.readObject();
        mapBounds = (Rectangle) in.readObject();
        mapLevel = in.readInt();
        lastAuthor = (String) in.readObject();

        warpFields = (Map<Location, WarpData>) in.readObject();

        lastChangedDate = new Date(in.readLong());
    }

    /**
     * Remove a warp field at a specified location.
     * 
     * @param loc remove the warp field at a location
     */
    public void removeWarpField(final Location loc) {
        warpFields.remove(loc);
        if (warpFields.isEmpty()) {
            if (warpFields instanceof FastMap) {
                FastMap.recycle((FastMap<?, ?>) warpFields);
            }
            warpFields = null;
        }
    }

    /**
     * Remove a warp field.
     * 
     * @param field the warp field to remove
     */
    public void removeWarpField(final WarpData field) {
        removeWarpField(field.getMapLocation(this));
    }

    /**
     * Report a update to this map by a specified user. This will alter the last
     * author and the last change date.
     * 
     * @param newAuthor the name of the author who changed this map
     */
    public void reportUpdate(final String newAuthor) {
        getLastChangeDate().setTime(System.currentTimeMillis());
        lastAuthor = newAuthor;
    }

    /**
     * Set the base filename of this map.
     * 
     * @param newFileName the new base filename
     */
    public void setBaseFileName(final String newFileName) {
        fileName = newFileName;
    }

    /**
     * The new description of this map.
     * 
     * @param newDescription the new description of this map
     */
    public void setDescription(final String newDescription) {
        description = newDescription;
    }

    /**
     * Set the name of the group this map is a part of to a new value.
     * 
     * @param newGroupName the new name of the group this map is a part of
     */
    public void setGroupName(final String newGroupName) {
        groupName = newGroupName;
    }

    /**
     * Set the level of this map to a new value.
     * 
     * @param newMapLevel the new level of this map
     */
    public void setMapLevel(final int newMapLevel) {
        mapLevel = newMapLevel;
    }

    /**
     * Set the name of this map.
     * 
     * @param newMapName the new name of this map
     */
    public void setMapName(final String newMapName) {
        mapName = newMapName;
    }

    /**
     * Set the new origin of this map.
     * 
     * @param x the x coordinate of the new origin
     * @param y the y coordinate of the new origin
     */
    public void setOrigin(final int x, final int y) {
        if (mapBounds == null) {
            mapBounds = Rectangle.getInstance();
        }
        mapBounds.set(x, y, mapBounds.getWidth(), mapBounds.getHeight());
    }

    /**
     * Set the new origin of this map.
     * 
     * @param x the x coordinate of the new origin
     * @param y the y coordinate of the new origin
     * @param z the z coordinate of the new origin
     */
    public void setOrigin(final int x, final int y, final int z) {
        setOrigin(x, y);
        setMapLevel(z);
    }

    /**
     * Set the size of the map to a new value.
     * 
     * @param newWidth the new width of the map
     * @param newHeight the new height of the map
     */
    public void setSize(final int newWidth, final int newHeight) {
        if (mapBounds == null) {
            mapBounds = Rectangle.getInstance();
        }

        mapBounds.set(mapBounds.getX(), mapBounds.getY(), newWidth, newHeight);
    }

    /**
     * Write the data of this map to a external stream.
     */
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeObject(fileName);
        out.writeObject(mapName);
        out.writeObject(description);
        out.writeObject(groupName);
        out.writeObject(mapBounds);
        out.writeInt(mapLevel);
        out.writeObject(lastAuthor);

        out.writeObject(warpFields);

        if (lastChangedDate == null) {
            out.writeLong(System.currentTimeMillis());
        } else {
            out.writeLong(lastChangedDate.getTime());
        }
    }

    /**
     * This function is used to write the meta data of a file to any output
     * stream.
     * 
     * @param out the stream that is supposed to receive the meta data
     * @throws IOException thrown in case the writing operations fail
     */
    public void writeMetaData(final OutputStream out) throws IOException {
        final Properties props = new Properties();
        props.setProperty("name", mapName); //$NON-NLS-1$
        props.setProperty("group", groupName); //$NON-NLS-1$
        props.setProperty("description", description); //$NON-NLS-1$
        props.setProperty("author", lastAuthor); //$NON-NLS-1$
        props.setProperty("lastChange", //$NON-NLS-1$
            Long.toString(lastChangedDate.getTime(), Character.MAX_RADIX));

        props.store(out, "Map Metadata - DO NOT CHANGE!"); //$NON-NLS-1$
    }

    /**
     * This is a small helper function that extracts the value of the header of
     * the tiles file.
     * 
     * @param line the line to progress
     * @param header the header that needs to be cut of
     * @return the value stored in this header line
     */
    private int getTilesHeaderValue(final String line, final String header) {
        return Integer.parseInt(line.substring(header.length()).trim());
    }

    /**
     * Read the data of the meta file that is needed for the map data.
     * 
     * @param directory the directory the file is located in
     */
    private void readMetaData(final File directory) {
        final File metaFile = new File(directory, getMetaFileName());

        if (!metaFile.exists() || !metaFile.isFile() || !metaFile.canRead()) {
            // File for meta data is optional
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(metaFile));
            final Properties props = new Properties();

            props.load(reader);

            mapName = props.getProperty("name", null); //$NON-NLS-1$
            groupName = props.getProperty("group", null); //$NON-NLS-1$
            description = props.getProperty("description", null); //$NON-NLS-1$
            lastAuthor = props.getProperty("author", null); //$NON-NLS-1$
            lastChangedDate =
                new Date(Long.parseLong(
                    props.getProperty("lastChange", Long.toString( //$NON-NLS-1$
                        System.currentTimeMillis(), Character.MAX_RADIX)),
                    Character.MAX_RADIX));
        } catch (final IOException e) {
            // error while reading -> ignore that
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    // nothing to do
                }
            }
        }
    }

    /**
     * Read the data of the tiles file that is needed for the map data.
     * 
     * @param directory the directory the file is located in
     */
    private void readTilesData(final File directory) {
        final File tilesFile = new File(directory, getTileFileName());

        if (!tilesFile.exists() || !tilesFile.isFile() || !tilesFile.canRead()) {
            throw new IllegalStateException("Can't find required tile file."); //$NON-NLS-1$
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(tilesFile));

            String line = null;

            int mapPosX = getOriginX();
            int mapPosY = getOriginY();
            int mapWidth = getMapWidth();
            int mapHeight = getMapHeight();

            boolean foundLevel = false;
            boolean foundX = false;
            boolean foundY = false;
            boolean foundWidth = false;
            boolean foundHeight = false;

            while ((line = reader.readLine()) != null) {
                try {
                    if (line.startsWith(DATA_HEADER_LEVEL)) {
                        mapLevel =
                            getTilesHeaderValue(line, DATA_HEADER_LEVEL);
                        foundLevel = true;
                    } else if (line.startsWith(DATA_HEADER_WIDTH)) {
                        mapWidth =
                            getTilesHeaderValue(line, DATA_HEADER_WIDTH);
                        foundWidth = true;
                    } else if (line.startsWith(DATA_HEADER_HEIGHT)) {
                        mapHeight =
                            getTilesHeaderValue(line, DATA_HEADER_HEIGHT);
                        foundHeight = true;
                    } else if (line.startsWith(DATA_HEADER_X)) {
                        mapPosX = getTilesHeaderValue(line, DATA_HEADER_X);
                        foundX = true;
                    } else if (line.startsWith(DATA_HEADER_Y)) {
                        mapPosY = getTilesHeaderValue(line, DATA_HEADER_Y);
                        foundY = true;
                    }
                } catch (final NumberFormatException e) {
                    // nothing
                }

                if (foundLevel && foundWidth && foundHeight && foundX
                    && foundY) {
                    break;
                }
            }

            if (mapBounds == null) {
                mapBounds = Rectangle.getInstance();
            }
            mapBounds.set(mapPosX, mapPosY, mapWidth, mapHeight);
        } catch (final IOException e) {
            // error while reading -> ignore that
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    // nothing to do
                }
            }
        }
    }

    /**
     * This function is used to read the warp points of this map file.
     * 
     * @param directory the directory that contains the map files
     */
    private void readWarpData(final File directory) {
        final File warpFile = new File(directory, getWarpFileName());

        if (!warpFile.exists() || !warpFile.isFile() || !warpFile.canRead()) {
            // File for meta data is optional
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(warpFile));

            final WarpLoader warpLoader = WarpLoader.getInstance();
            final Location origin = Location.getInstance();
            getOrigin(origin);
            String line;
            while ((line = reader.readLine()) != null) {
                warpLoader.decodeWarpFieldLine(line, origin);
            }
            origin.recycle();
        } catch (final IOException e) {
            // error while reading -> ignore that
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    // nothing to do
                }
            }
        }
    }
}
