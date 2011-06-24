/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.guiNG.references;

import illarion.client.net.NetCommWriter;
import illarion.client.world.MapTile;

import illarion.common.util.Location;

/**
 * This is the reference to a map coordinate. Such references are used to handle
 * the dragging effects correctly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class MapReference extends AbstractReference {
    /**
     * The location this reference currently refers to.
     */
    private final Location loc;

    /**
     * Constructor to create a new instance of a map reference.
     */
    public MapReference() {
        super(AbstractReference.MAP);
        loc = Location.getInstance();
    }

    /**
     * Encode the needed data for a use on this tile.
     * 
     * @param writer the interface to write data on the network
     */
    @Override
    public void encodeUse(final NetCommWriter writer) {
        encodeID(writer);
        writer.writeLocation(loc);
    }

    /**
     * Called by the garbage collector in case the object is disposed.
     */
    @Override
    public void finalize() {
        loc.recycle();
    }

    /**
     * Get the location this reference refers to.
     * 
     * @return the location this reference refers to
     */
    public Location getReferringLocation() {
        return loc;
    }

    /**
     * Set the tile this map reference refers to.
     * 
     * @param tile the map tile this reference refers to
     */
    public void setReferringTile(final MapTile tile) {
        loc.set(tile.getLocation());
    }
}
