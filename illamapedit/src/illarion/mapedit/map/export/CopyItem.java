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
 * This class is used to store basic informations about items. This can be used
 * to copy parts of the map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class CopyItem {
    /**
     * The data value of this item.
     */
    private final int data;

    /**
     * The ID of this item.
     */
    private final short itemId;

    /**
     * The x coordinate of the position of the item.
     */
    private final short posX;

    /**
     * The y coordinate of the position of the item.
     */
    private final short posY;

    /**
     * The z coordinate of the position of the item.
     */
    private final short posZ;

    /**
     * The quality value of this item.
     */
    private final short quality;

    public CopyItem(final int id, final int qual, final int itemData,
        final int itemPosX, final int itemPosY, final int itemPosZ) {
        itemId = (short) id;
        quality = (short) qual;
        data = (short) itemData;
        posX = (short) itemPosX;
        posY = (short) itemPosY;
        posZ = (short) itemPosZ;
    }

    public CopyItem(final int id, final int qual, final int itemData,
        final Location loc) {
        this(id, qual, itemData, loc.getScX(), loc.getScY(), loc.getScZ());
    }

    /**
     * Get the data value of this item
     * 
     * @return the data value of this item
     */
    public int getData() {
        return data;
    }

    /**
     * Get the ID of this item.
     * 
     * @return the item id
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * Get the location of this item.
     * 
     * @return the location object with the location data of this item
     */
    public Location getPos() {
        return getPos(Location.getInstance());
    }

    /**
     * Get the location of this item.
     * 
     * @param loc the location object that is used to store the location data
     * @return the location object with the location data supplied to the method
     */
    public Location getPos(final Location loc) {
        loc.setSC(posX, posY, posZ);
        return loc;
    }

    /**
     * Get the X coordinate of the location of this item.
     * 
     * @return the x coordinate of the location
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Get the Y coordinate of the location of this item.
     * 
     * @return the y coordinate of the location
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Get the Z coordinate of the location of this item.
     * 
     * @return the z coordinate of the location
     */
    public int getPosZ() {
        return posZ;
    }

    /**
     * Get the quality of this item.
     * 
     * @return the quality of this item
     */
    public int getQuality() {
        return quality;
    }
}
