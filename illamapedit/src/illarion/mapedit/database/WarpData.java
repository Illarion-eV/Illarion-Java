/*
 * This file is part of the Illarion Mapeditor.
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

    public Location getMapLocation(final MapData map) {
        if (map.equals(originMap)) {
            return origin;
        } else if (map.equals(targetMap)) {
            return target;
        }
        return null;
    }

    public Location getOriginLocation() {
        return getOriginLocation(Location.getInstance());
    }

    public Location getOriginLocation(final Location loc) {
        loc.set(origin);
        return loc;
    }

    public Location getTargetLocation() {
        return getTargetLocation(Location.getInstance());
    }

    public Location getTargetLocation(final Location loc) {
        loc.set(target);
        return loc;
    }

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

    public void setOriginLocation(final Location loc) {
        origin.set(loc);
    }

    public void setOriginMap(final MapData map) {
        originMap = map;
    }

    public void setTargetLocation(final Location loc) {
        target.set(loc);
    }

    public void setTargetMap(final MapData map) {
        targetMap = map;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeObject(origin);
        out.writeObject(originMap);
        out.writeObject(target);
        out.writeObject(targetMap);
    }

}
