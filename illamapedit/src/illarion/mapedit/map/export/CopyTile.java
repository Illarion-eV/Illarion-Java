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
package illarion.mapedit.map.export;

import illarion.common.util.Location;

/**
 * This class is used to store most basic informations about a tile. This can be
 * used to copy or export parts of the map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class CopyTile {
    /**
     * The x coordinate of the position of the tile.
     */
    private final short posX;

    /**
     * The y coordinate of the position of the tile.
     */
    private final short posY;

    /**
     * The z coordinate of the position of the tile.
     */
    private final short posZ;

    /**
     * The ID of the tile.
     */
    private final short tileId;

    /**
     * Create a copied instance of the tile with the supplied data.
     * 
     * @param id the ID of the tile
     * @param tileX the x coordinate of the tile location
     * @param tileY the y coordinate of the tile location
     * @param tileZ the z coordinate of the tile location
     */
    public CopyTile(final int id, final int tileX, final int tileY,
        final int tileZ) {
        tileId = (short) id;
        posX = (short) tileX;
        posY = (short) tileY;
        posZ = (short) tileZ;
    }

    /**
     * Create a copied instance of a tile with the supplied data.
     * 
     * @param id the ID of the tile
     * @param loc the location of the tile
     */
    public CopyTile(final int id, final Location loc) {
        this(id, loc.getScX(), loc.getScY(), loc.getScZ());
    }

    /**
     * Get the location of this tile.
     * 
     * @return the location object with the location data of this tile
     */
    public Location getPos() {
        return getPos(Location.getInstance());
    }

    /**
     * Get the location of this tile.
     * 
     * @param loc the location object that is used to store the location data
     * @return the location object with the location data supplied to the method
     */
    public Location getPos(final Location loc) {
        loc.setSC(posX, posY, posZ);
        return loc;
    }

    /**
     * Get the X coordinate of the location of this tile.
     * 
     * @return the x coordinate of the location
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Get the Y coordinate of the location of this tile.
     * 
     * @return the y coordinate of the location
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Get the Z coordinate of the location of this tile.
     * 
     * @return the z coordinate of the location
     */
    public int getPosZ() {
        return posZ;
    }

    /**
     * Get the ID of this tile.
     * 
     * @return the id of this tile
     */
    public int getTileId() {
        return tileId;
    }
}
