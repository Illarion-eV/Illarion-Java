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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import illarion.common.util.Location;

/**
 * This warp data stores the informations about a single warp point.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class WarpData implements Externalizable {
    /**
     * The serialization UID of this warp data object.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The location of the starting point of the warp.
     */
    private final Location origin;

    /**
     * The map this war originates from.
     */
    private MapData originMap;

    /**
     * The target location of the warp.
     */
    private final Location target;

    /**
     * The map this warp is going to.
     */
    private MapData targetMap;

    /**
     * Create a new instance of this warp data and prepare the internal
     * variables for work.
     */
    public WarpData() {
        origin = new Location();
        target = new Location();
    }

    /**
     * Get the location of this warp point in this map. The location object
     * returned is newly created and can be recylced after the function is done
     * with it
     * 
     * @param map the map the location assigned to is requested
     * @return the location of the warp point in the map, the origin location in
     *         case both target and origin are on the same map or
     *         <code>null</code> in case the warp point is not on this map at
     *         all
     */
    public Location getMapLocation(final MapData map) {
        if (map.equals(originMap)) {
            return getOriginLocation();
        } else if (map.equals(targetMap)) {
            return getTargetLocation();
        }
        return null;
    }

    /**
     * Get the location where the warp starts.
     * 
     * @return a new instance of a location object that contains the location
     *         where the warp origins
     */
    public Location getOriginLocation() {
        return getOriginLocation(Location.getInstance());
    }

    /**
     * Get the location where the warp starts.
     * 
     * @param loc the location instance that is supposed to store the origin
     *            location of this warp field
     * @return the same instance of location that is passed by the
     *         <code>loc</code> parameter
     */
    public Location getOriginLocation(final Location loc) {
        loc.set(origin);
        return loc;
    }

    /**
     * Get the location where the warp ends.
     * 
     * @return a new instance of a location object that contains the location
     *         where the warp ends
     */
    public Location getTargetLocation() {
        return getTargetLocation(Location.getInstance());
    }

    /**
     * Get the location where the warp ends.
     * 
     * @param loc the location instance that is supposed to store the target
     *            location of this warp field
     * @return the same instance of location that is passed by the
     *         <code>loc</code> parameter
     */
    public Location getTargetLocation(final Location loc) {
        loc.set(target);
        return loc;
    }

    /**
     * Read the data from a input stream.
     */
    @SuppressWarnings("nls")
    @Override
    public void readExternal(final ObjectInput in) throws IOException,
        ClassNotFoundException {
        final long id = in.readLong();
        if (id == serialVersionUID) {
            Location readLoc = (Location) in.readObject();
            origin.set(readLoc);
            originMap = (MapData) in.readObject();

            readLoc = (Location) in.readObject();
            target.set(readLoc);
            targetMap = (MapData) in.readObject();
        } else {
            throw new IOException("Illegal MapData ID");
        }
    }

    /**
     * Set the location where the warp points begins. The instance passed as
     * parameter will only be used as data source and can be changed or used
     * otherwise after the call of this function.
     * 
     * @param loc the location where the warp starts
     */
    public void setOriginLocation(final Location loc) {
        origin.set(loc);
    }

    /**
     * Set the map where the warp starts.
     * 
     * @param map the map where the warp starts
     */
    public void setOriginMap(final MapData map) {
        originMap = map;
    }

    /**
     * Set the location where the warp points ends. The instance passed as
     * parameter will only be used as data source and can be changed or used
     * otherwise after the call of this function.
     * 
     * @param loc the location where the warp ends
     */
    public void setTargetLocation(final Location loc) {
        target.set(loc);
    }

    /**
     * Set the map where the warp ends.
     * 
     * @param map the map where the warp ends
     */
    public void setTargetMap(final MapData map) {
        targetMap = map;
    }

    /**
     * Write the current version of the map data to a output stream.
     */
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeObject(origin);
        out.writeObject(originMap);
        out.writeObject(target);
        out.writeObject(targetMap);
    }

}
