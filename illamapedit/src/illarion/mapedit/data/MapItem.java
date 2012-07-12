/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.data;

/**
 * Represents a single item, with a position, an id, a quality, and data.
 *
 * @author Tim
 */
public class MapItem {
    /**
     * Represents a not existing qualtity
     */
    public static final int QUALITY_NONE = -1;

    /**
     * Represents the default quality, if the {@link MapItem#quality} is {@link MapItem#QUALITY_NONE}
     * time of serialisation
     */
    public static final int QUALITY_DEFAULT = 333;
    /**
     * Represents no data.
     */
    public static final int DATA_NONE = 0;

    /**
     * The item id.
     */
    private final int itemId;
    /**
     * The data of this item.
     */
    private final int itemData;
    /**
     * The quality of this item.
     */
    private final int quality;

    /**
     * Creates a new Item
     *
     * @param itemId   The item id.
     * @param itemData The data of this item.
     * @param quality  The quality of this item.
     */
    public MapItem(final int itemId, final int itemData, final int quality) {
        this.itemId = itemId;
        this.itemData = itemData;
        this.quality = quality;
    }

    /**
     * Creates a new copy of an existing item.
     *
     * @param old the old instance.
     */
    public MapItem(final MapItem old) {
        itemId = old.itemId;
        itemData = old.itemData;
        quality = old.quality;
    }


    /**
     * Returns the id of the item.
     *
     * @return the item id
     */
    public int getId() {
        return itemId;
    }

    /**
     * Returns the quality of the item.
     *
     * @return the quality
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Returns the data-value of this item.
     *
     * @return the data-value.
     */
    public int getItemData() {
        return itemData;
    }
}
