/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javolution.context.ObjectFactory;
import javolution.text.TextBuilder;

import illarion.common.graphics.Layers;
import illarion.common.graphics.MapConstants;

/**
 * Storage for the server map and all recalculation function for the Client
 * screen representations.
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public class Location implements Externalizable, Reusable {
    /**
     * This is the factory class that is used to buffer and reuse the location
     * objects that are created.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class LocationFactory extends ObjectFactory<Location> {
        /**
         * Public constructor to allow the parent class to create a proper
         * instance.
         */
        public LocationFactory() {
            super();
        }

        /**
         * Create a new instance of the vector class.
         * 
         * @return the new vector instance
         */
        @Override
        protected Location create() {
            return new Location();
        }
    }

    /**
     * Constant for a move in eastern direction.
     */
    public static final int DIR_EAST = 2;

    /**
     * Directions in the 8 direction system.
     */
    public static final int DIR_MOVE8 = 8;

    /**
     * Constant for a move in northern direction.
     */
    public static final int DIR_NORTH = 0;

    /**
     * Constant for a move in north eastern direction.
     */
    public static final int DIR_NORTHEAST = 1;
    /**
     * Constant for a move in north western direction.
     */
    public static final int DIR_NORTHWEST = 7;

    /**
     * Constant for a move in southern direction.
     */
    public static final int DIR_SOUTH = 4;

    /**
     * Constant for a move in south eastern direction.
     */
    public static final int DIR_SOUTHEAST = 3;

    /**
     * Constant for a move in south western direction.
     */
    public static final int DIR_SOUTHWEST = 5;

    /**
     * Constant for a move in western direction.
     */
    public static final int DIR_WEST = 6;

    /**
     * Constant for a move in no direction. Means there is no real move at all.
     */
    public static final int DIR_ZERO = 0x0A;

    /**
     * Modificator used at the calculation of the display coordinates in case
     * its a tile above or below the level 0.
     */
    public static final int DISPLAY_Z_OFFSET_MOD = 6;

    /**
     * The factory used to buffer and reuse the class instances.
     */
    private static final ObjectFactory<Location> FACTORY =
        new LocationFactory();

    /**
     * Modificator of the X-Coordinate of the server coordinates to calculate a
     * key of this position.
     */
    private static final long KEY_MOD_X = 65536L;

    /**
     * Modificator of the Y-Coordinate of the server coordinates to calculate a
     * key of this position.
     */
    private static final long KEY_MOD_Y = 1L;

    /**
     * Modificator of the Z-Coordinate of the server coordinates to calculate a
     * key of this position.
     */
    private static final long KEY_MOD_Z = 4294967296L;

    /**
     * Offset to all fields that can be accessed by a move in 8 directions.
     */
    private static final int[][] MOVE8 = new int[][] {
        { 0, 1, 1, 1, 0, -1, -1, -1 }, { -1, -1, 0, 1, 1, 1, 0, -1 } };

    /**
     * The serialization UID of this location class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Column of map tiles on the client map.
     */
    private int col;

    /**
     * X-Coordinate of the Display coordinates of the tile. Means on what
     * position of the game window the marked position is shown.
     */
    private int dcX;

    /**
     * Y-Coordinate of the Display coordinates of the tile. Means on what
     * position of the game window the marked position is shown.
     */
    private int dcY;

    /**
     * Z-Coordinate of the Display coordinates of the tile. Means on what
     * position of the game window the marked position is shown.
     */
    private int dcZ;

    /**
     * True if the display coordinates need to be calculated.
     */
    private boolean dirtyDC = false;

    /**
     * True if the map coordinates need to be calculated.
     */
    private boolean dirtyMC = false;

    /**
     * True if the server coordinates need to be calculated.
     */
    private boolean dirtySC = false;

    /**
     * Distance between the tiles. Needed for display coordinate calculation.
     */
    private int gap;

    /**
     * Row of map tiles on the client map.
     */
    private int row;

    /**
     * X-Coordinate on the Server map.
     */
    private int scX;

    /**
     * Y-Coordinate on the Server map.
     */
    private int scY;

    /**
     * Z-Coordinate on the Server map.
     */
    private int scZ;

    /**
     * Constructor of a new location object pointing to 0, 0, 0 on the server
     * coordinates.
     */
    public Location() {
        reset();
    }

    /**
     * Create a new instance of the location that points to a specified location
     * on the map.
     * 
     * @param c the column of the map coordinates
     * @param r the row of the map coordinates
     */
    public Location(final int c, final int r) {
        this();
        setMC(c, r);
    }

    /**
     * Create a new instance of the location that points to a specified location
     * on the map.
     * 
     * @param x the x coordinate of the server coordinates of the target
     *            position
     * @param y the y coordinate of the server coordinates of the target
     *            position
     * @param z the z coordinate of the server coordinates of the target
     *            position
     */
    public Location(final int x, final int y, final int z) {
        this();
        setSC(x, y, z);
    }

    /**
     * Copy constructor. This constructor creates a copy of the location
     * instance set here.
     * 
     * @param org the original Location instance
     */
    public Location(final Location org) {
        this();
        set(org);
    }

    /**
     * Calculate the display coordinates from floating server coordinates. This
     * function returns the X part of the display coordinates where a object
     * with this coordinates needs to be displayed.
     * 
     * @param x the x coordinate of the server location that shall be converted
     * @param y the y coordinate of the server location that shall be converted
     * @param z the z coordinate of the server location that shall be converted
     * @return the x coordinate of the display coordinates where the object
     *         needs to be displayed
     */
    @SuppressWarnings("unused")
    public static int displayCoordinateX(final float x, final float y,
        final float z) {
        return (int) ((x + y) * (MapConstants.STEP_X));
    }

    /**
     * Calculate the display coordinates from floating server coordinates. This
     * function returns the Y part of the display coordinates where a object
     * with this coordinates needs to be displayed.
     * 
     * @param x the x coordinate of the server location that shall be converted
     * @param y the y coordinate of the server location that shall be converted
     * @param z the z coordinate of the server location that shall be converted
     * @return the y coordinate of the display coordinates where the object
     *         needs to be displayed
     */
    public static int displayCoordinateY(final float x, final float y,
        final float z) {
        return (int) (((x - y) * MapConstants.STEP_Y) + (DISPLAY_Z_OFFSET_MOD
            * z * MapConstants.STEP_Y));
    }

    /**
     * Calculate the display coordinates from floating server coordinates. This
     * function returns the Z part of the display coordinates where a object
     * with this coordinates needs to be displayed.
     * 
     * @param x the x coordinate of the server location that shall be converted
     * @param y the y coordinate of the server location that shall be converted
     * @param z the z coordinate of the server location that shall be converted
     * @return the z coordinate of the display coordinates where the object
     *         needs to be displayed
     */
    public static int displayCoordinateZ(final float x, final float y,
        final float z) {
        return (int) ((x - y - (z * Layers.LEVEL)) * Layers.DISTANCE);
    }

    /**
     * Get the X part of a direction vector.
     * 
     * @param direction the direction the x vector is needed from
     * @return the x part of the direction vector
     */
    public static int getDirectionVectorX(final int direction) {
        return MOVE8[0][direction];
    }

    /**
     * Get the Y part of a direction vector.
     * 
     * @param direction the direction the y vector is needed from
     * @return the y part of the direction vector
     */
    public static int getDirectionVectorY(final int direction) {
        return MOVE8[1][direction];
    }

    /**
     * Get a instance from this location class that is currently not in use. Its
     * proposed to use this one over the usual constructors.
     * 
     * @return the unused location instance
     */
    public static Location getInstance() {
        return FACTORY.object();
    }

    /**
     * Create a key that identifies a position exactly. Can be used for
     * collection classes. The key calculated using the server position.
     * 
     * @param x the X-Coordinate of the server coordinates used to calculate the
     *            key
     * @param y the Y-Coordinate of the server coordinates used to calculate the
     *            key
     * @param z the Z-Coordinate of the server coordinates used to calculate the
     *            key
     * @return the key of this position
     */
    public static long getKey(final int x, final int y, final int z) {
        return (z * KEY_MOD_Z) + (x * KEY_MOD_X) + (y * KEY_MOD_Y);
    }

    /**
     * Add an offset to the display location. The calculation to map and server
     * coordinates is triggered automatically.
     * 
     * @param x Value to add to the X-Coordinate of the display location
     * @param y Value to add to the Y-Coordinate of the display location
     * @param z Value to add to the Z-Coordinate of the display location
     */
    public void addDC(final int x, final int y, final int z) {
        if (dirtyDC) {
            toDisplayCoordinates();
        }
        dcX += x;
        dcY += y;
        dcZ += z;

        dirtySC = true;
        dirtyMC = true;
        dirtyDC = false;
    }

    /**
     * Add an offset to the map location. The calculation to Server and Display
     * coordinates is triggered automatically.
     * 
     * @param c Value to add to the column of the map coordinates
     * @param r Value to add to the row of the map coordinates
     */
    public void addMC(final int c, final int r) {
        if (dirtyMC) {
            toMapCoordinates();
        }
        col += c;
        row += r;

        dirtySC = true;
        dirtyMC = false;
        dirtyDC = true;
    }

    /**
     * Add an offset to the server location. The calculation to Map and Display
     * coordinates is triggered automatically.
     * 
     * @param x Value to add to the X-Coordinate of the server location
     * @param y Value to add to the Y-Coordinate of the server location
     * @param z Value to add to the Z-Coordinate of the server location
     */
    public void addSC(final int x, final int y, final int z) {
        if (dirtySC) {
            toServerCoordinates();
        }
        scX += x;
        scY += y;
        scZ += z;

        dirtySC = false;
        dirtyMC = true;
        dirtyDC = true;
    }

    /**
     * Check this location and a second one for equality. Two locations are
     * considered equal in case the server coordinates fit.
     * 
     * @param obj the second location
     * @return true in case the server coordinates of this location and the
     *         second location are the same.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Location)) {
            return false;
        }
        final Location loc = (Location) obj;
        if (dirtySC) {
            toServerCoordinates();
        }
        if (loc.dirtySC) {
            loc.toServerCoordinates();
        }
        return (scX == loc.scX) && (scY == loc.scY) && (scZ == loc.scZ);
    }

    /**
     * Test if the position is identical with the server coordinates handed over
     * to this function.
     * 
     * @param x X-Coordinate of the server coordinates
     * @param y Y-Coordinate of the server coordinates
     * @param z Y-Coordinate of the server coordinates
     * @return true if the coordinates set as parameters of this function are
     *         identical with the current position.
     */
    public boolean equalsSC(final int x, final int y, final int z) {
        if (dirtySC) {
            toServerCoordinates();
        }
        return (scX == x) && (scY == y) && (scZ == z);
    }

    /**
     * Catch the garbage collector and stop it from disposing this object. This
     * function causes that the instance is not disposed by th garbage
     * collector. Instead its put back into its factory.
     */
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        recycle();
        throw new Throwable();
    }

    /**
     * Get the column on the client map.
     * 
     * @return the column on the client map
     */
    public int getCol() {
        if (dirtyMC) {
            toMapCoordinates();
        }
        return col;
    }

    /**
     * Get the X-Coordinate of the Display Coordinates.
     * 
     * @return X-Coordinate of the Display Coordinates
     */
    public int getDcX() {
        if (dirtyDC) {
            toDisplayCoordinates();
        }
        return dcX;
    }

    /**
     * Get the Y-Coordinate of the Display Coordinates.
     * 
     * @return Y-Coordinate of the Display Coordinates
     */
    public int getDcY() {
        if (dirtyDC) {
            toDisplayCoordinates();
        }
        return dcY;
    }

    /**
     * Get the Z-Coordinate of the Display Coordinates.
     * 
     * @return Z-Coordinate of the Display Coordinates
     */
    public int getDcZ() {
        if (dirtyDC) {
            toDisplayCoordinates();
        }
        return dcZ;
    }

    /**
     * Determine the direction to get from the current location to the
     * coordinates handed over to this function, using the 8 directions system.
     * The coordinates used are the server coordinates.
     * 
     * @param x X-Coordinate of the target location
     * @param y Y-Coordinate of the target location
     * @return the direction needed to get from the current location to the
     *         target location
     */
    public int getDirection(final int x, final int y) {
        if (dirtySC) {
            toServerCoordinates();
        }
        // Calculate relative movement
        int dirX = x - scX;
        int dirY = y - scY;
        final int lenX = Math.abs(dirX);
        final int lenY = Math.abs(dirY);

        // normalize distances, just to be sure
        if (dirX != 0) {
            dirX /= lenX;
        }
        if (dirY != 0) {
            dirY /= lenY;
        }

        for (int i = 0; i < MOVE8[0].length; ++i) {
            if ((MOVE8[0][i] == dirX) && (MOVE8[1][i] == dirY)) {
                return i;
            }
        }

        return DIR_ZERO;
    }

    /**
     * Determine the direction needed to change the current location to the
     * location that is handed over to this function using the 8 direction
     * system. The coordinates used are the server coordinates.
     * 
     * @param loc The target location
     * @return the direction needed to get from the current location to the
     *         target location
     */
    public int getDirection(final Location loc) {
        if (loc.dirtySC) {
            loc.toServerCoordinates();
        }
        return getDirection(loc.scX, loc.scY);
    }

    /**
     * Get the distance in needed steps from the current position to the target
     * position.
     * 
     * @param loc the target position
     * @return the amount of steps needed to get from the current position to
     *         the target position in case there are not blocked tiles on the
     *         way
     */
    public int getDistance(final Location loc) {
        if (dirtySC) {
            toServerCoordinates();
        }
        if (loc.dirtySC) {
            loc.toServerCoordinates();
        }
        final int diffX = Math.abs(loc.scX - scX);
        final int diffY = Math.abs(loc.scY - scY);
        return Math.max(diffX, diffY);
    }

    /**
     * Create a key that identifies this position exactly. Can be used for
     * collection classes. The key calculated using the server position.
     * 
     * @return the key of this position
     */
    public long getKey() {
        if (dirtySC) {
            toServerCoordinates();
        }

        return getKey(scX, scY, scZ);
    }

    /**
     * Get the row on the client map.
     * 
     * @return the row on the client map
     */
    public int getRow() {
        if (dirtyMC) {
            toMapCoordinates();
        }
        return row;
    }

    /**
     * Get the X-Coordinate of the Server Coordinates.
     * 
     * @return X-Coordinate of the Server Coordinates
     */
    public int getScX() {
        if (dirtySC) {
            toServerCoordinates();
        }
        return scX;
    }

    /**
     * Get the Y-Coordinate of the Server Coordinates.
     * 
     * @return Y-Coordinate of the Server Coordinates
     */
    public int getScY() {
        if (dirtySC) {
            toServerCoordinates();
        }
        return scY;
    }

    /**
     * Get the Z-Coordinate of the Server Coordinates.
     * 
     * @return Z-Coordinate of the Server Coordinates
     */
    public int getScZ() {
        if (dirtySC) {
            toServerCoordinates();
        }
        return scZ;
    }

    /**
     * Get the square root distance between two locations.
     * 
     * @param loc the target location
     * @return the square root distance between the two locations. So the length
     *         of a straight line between this location and the target location.
     */
    public float getSqrtDistance(final Location loc) {
        if (dirtySC) {
            toServerCoordinates();
        }
        if (loc.dirtySC) {
            loc.toServerCoordinates();
        }
        return FastMath.sqrt(FastMath.pow(loc.scX - scX, 2)
            + FastMath.pow(loc.scY - scY, 2));
    }

    /**
     * Generate the hash code of this location object.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        if (dirtySC) {
            toServerCoordinates();
        }
        return (int) ((scX * KEY_MOD_X) + (scY * KEY_MOD_Y));
    }

    /**
     * Determine if a location is empty or at the origin.
     * 
     * @return true if all 3 components of the server coordinate are 0
     */
    public boolean isEmpty() {
        if (dirtySC) {
            toServerCoordinates();
        }
        return (scX == 0) && (scY == 0) && (scZ == 0);
    }

    /**
     * Determine if this location and a second one are direct neighbours. So
     * they have to touch each other.
     * 
     * @param loc the second location
     * @return true in case this location and the second one are touching each
     *         other
     */
    public boolean isNeighbour(final Location loc) {
        if (dirtySC) {
            toServerCoordinates();
        }
        if (loc.dirtySC) {
            loc.toServerCoordinates();
        }
        return (FastMath.abs(loc.scX - scX) < 2)
            && (FastMath.abs(loc.scY - scY) < 2);
    }

    /**
     * Move location one step into a direction using the 4 direction system.
     * 
     * @param dir The direction the Server coordinates are moved by
     */
    public void moveSC(final int dir) {
        if (dir == DIR_ZERO) {
            return;
        }
        if (dirtySC) {
            toServerCoordinates();
        }

        scX += MOVE8[0][dir];
        scY += MOVE8[1][dir];

        dirtySC = false;
        dirtyMC = true;
        dirtyDC = true;
    }

    /**
     * Move location one step into a direction using the 8 direction system.
     * 
     * @param dir The direction the Server coordinates are moved by
     */
    public void moveSC8(final int dir) {
        if (dir == DIR_ZERO) {
            return;
        }
        if (dirtySC) {
            toServerCoordinates();
        }

        scX += MOVE8[0][dir];
        scY += MOVE8[1][dir];

        dirtySC = false;
        dirtyMC = true;
        dirtyDC = true;
    }

    /**
     * Load the location from an input stream. This method does only load the
     * server coordinates from the input stream.
     */
    @Override
    public void readExternal(final ObjectInput in) throws IOException,
        ClassNotFoundException {
        final long version = in.readLong();
        if (version == 1L) {
            scX = in.readInt();
            scY = in.readInt();
            scZ = in.readInt();
            dirtySC = false;
        }
    }

    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    @Override
    public void reset() {
        dirtyDC = true;
        dirtyMC = true;
        dirtySC = true;
        gap = 0;
    }

    /**
     * Set location from given location.
     * 
     * @param loc The source location that is copied to this location
     */
    public void set(final Location loc) {
        scX = loc.scX;
        scY = loc.scY;
        scZ = loc.scZ;

        col = loc.col;
        row = loc.row;

        dcX = loc.dcX;
        dcY = loc.dcY;
        dcZ = loc.dcZ;

        dirtySC = loc.dirtySC;
        dirtyDC = loc.dirtyDC;
        dirtyMC = loc.dirtyMC;
    }

    /**
     * Set the location to some display coordinates. The calculations to map and
     * server coordinates is done automatically. Z coordinate is used as 0.
     * 
     * @param x X-Coordinate of the display coordinates
     * @param y Y-Coordinate of the display coordinates
     */
    public void setDC(final int x, final int y) {
        setDC(x, y, 0);
    }

    /**
     * Set the location to some display coordinates. The calculations to map and
     * server coordinates is done automatically.
     * 
     * @param x X-Coordinate of the display coordinates
     * @param y Y-Coordinate of the display coordinates
     * @param z Z-Coordinate of the display coordinates
     */
    public void setDC(final int x, final int y, final int z) {
        dcX = x;
        dcY = y;
        dcZ = z;

        dirtySC = true;
        dirtyMC = true;
        dirtyDC = false;
    }

    /**
     * Set distance between tiles on screen. Default is no gap.
     * 
     * @param newGap the new gap value
     */
    public void setGap(final int newGap) {
        gap = newGap;

        dirtyDC = true;
    }

    /**
     * Set the server coordinates over a key that was created by the
     * {@link #getKey()} or the {@link #getKey(int, int, int)} method.
     * 
     * @param key the key used to set the server coordinates of the location
     */
    public void setKey(final long key) {
        setSC(
            (int) (((key % KEY_MOD_Z) / KEY_MOD_X) - (KEY_MOD_X / 2)),
            (int) ((((key % KEY_MOD_Z) % KEY_MOD_X) / KEY_MOD_Y) - (KEY_MOD_Y / 2)),
            (int) ((key / KEY_MOD_Z) - (KEY_MOD_Z / 2)));
    }

    /**
     * Set a location to some map coordinates. The server and display
     * coordinates are calculated automatically.
     * 
     * @param c Column of the map coordinates
     * @param r Row of the map coordinates
     */
    public void setMC(final int c, final int r) {
        col = c;
        row = r;

        dirtySC = true;
        dirtyMC = false;
        dirtyDC = true;
    }

    /**
     * Set the location to some server coordinates. The calculations to map and
     * display coordinates is done automatically. Z is used as 0.
     * 
     * @param x X-Coordinate of the server coordinates
     * @param y Y-Coordinate of the server coordinates
     */
    public void setSC(final int x, final int y) {
        setSC(x, y, 0);
    }

    /**
     * Set the location to some server coordinates. The calculations to map and
     * display coordinates is done automatically.
     * 
     * @param x X-Coordinate of the server coordinates
     * @param y Y-Coordinate of the server coordinates
     * @param z Z-Coordinate of the server coordinates
     */
    public void setSC(final int x, final int y, final int z) {
        scX = x;
        scY = y;
        scZ = z;

        dirtySC = false;
        dirtyMC = true;
        dirtyDC = true;
    }

    /**
     * Create a string with the server coordinates of this position.
     * 
     * @return the string of the server coordinates
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        if (dirtySC) {
            toServerCoordinates();
        }
        final TextBuilder builder = TextBuilder.newInstance();
        builder.append("Location: ");
        builder.append(scX);
        builder.append(',');
        builder.append(scY);
        builder.append(',');
        builder.append(scZ);
        final String retString = builder.toString();
        TextBuilder.recycle(builder);
        return retString;
    }

    /**
     * Write this location to a output stream. This method does only store the
     * server coordinates of this location.
     */
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        toServerCoordinates();
        out.writeInt(scX);
        out.writeInt(scY);
        out.writeInt(scZ);
    }

    /**
     * Use the server coordinates and the map coordinates to calculate the
     * display coordinates.
     */
    private void toDisplayCoordinates() {
        if (!dirtyDC) {
            return;
        }
        if (!dirtySC) {
            dcX = (scX + scY) * (MapConstants.STEP_X + gap);
            dcY =
                ((scX - scY) * (MapConstants.STEP_Y + gap))
                    + (DISPLAY_Z_OFFSET_MOD * scZ * MapConstants.STEP_Y);
            dcZ = (scX - scY - (scZ * Layers.LEVEL)) * Layers.DISTANCE;

            dirtyDC = false;
        } else if (!dirtyMC) {
            dcX = col * (MapConstants.STEP_X + gap);
            dcY = row * (MapConstants.STEP_Y + gap);
            dcZ = row * Layers.DISTANCE;

            dirtyDC = false;
        }
    }

    /**
     * Use the server coordinates to calculate the map coordinates.
     */
    private void toMapCoordinates() {
        if (!dirtyMC) {
            return;
        }
        if (!dirtySC) {
            col = scX + scY;
            row = scX - scY;

            dirtyMC = false;
        } else if (!dirtyDC) {
            col = FastMath.round(dcX / (float) (MapConstants.STEP_X + gap));
            row = FastMath.round(dcY / (float) (MapConstants.STEP_Y + gap));

            dirtyMC = false;
        }
    }

    /**
     * Use the map coordinates to calculate the server coordinates.
     */
    private void toServerCoordinates() {
        if (!dirtySC) {
            return;
        }
        if (!dirtyMC) {
            scX = (row + col) / 2;
            scY = (col - row) / 2;
            scZ = 0;

            dirtySC = false;
        } else if (!dirtyDC) {
            scX =
                FastMath
                    .round(((dcY / (float) (MapConstants.STEP_Y + gap)) + (dcX / (float) (MapConstants.STEP_X + gap))) / 2.f);
            scY =
                FastMath
                    .round(((dcX / (float) (MapConstants.STEP_X + gap)) - (dcY / (float) (MapConstants.STEP_Y + gap))) / 2.f);
            scZ = 0;

            dirtySC = false;
        }
    }
}
