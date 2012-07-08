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
public class Item {
    /**
     * Represents a not existing qualtity
     */
    public static final int QUALITY_NONE = -1;

    /**
     * Represents the default quality, if the {@link Item#quality} is {@link Item#QUALITY_NONE}
     * time of serialisation
     */
    public static final int QUALITY_DEFAULT = 333;
    /**
     * Represents no data.
     */
    public static final int DATA_NONE = 0;

    /**
     * The x coordinate of the item.
     */
    private final int x;
    /**
     * The y coordinate of the item.
     */
    private final int y;
    /**
     * The item id.
     */
    private final int id;
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
     * @param x        The x coordinate of the item.
     * @param y        The y coordinate of the item.
     * @param id       The item id.
     * @param itemData The data of this item.
     * @param quality  The quality of this item.
     */
    public Item(final int x, final int y, final int id, final int itemData, final int quality) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.itemData = itemData;
        this.quality = quality;
    }

    /**
     * Creates a new copy of an existing item.
     *
     * @param old the old instance.
     */
    public Item(final Item old) {
        x = old.x;
        y = old.y;
        id = old.id;
        itemData = old.itemData;
        quality = old.quality;
    }

    /**
     * Returns the x coordinate of the item relative to the map.
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the item relative to the map.
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the id of the item.
     *
     * @return the item id
     */
    public int getId() {
        return id;
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

    /**
     * Creates a new item from a line of a map file with the following syntax: <br/>
     * {@code [X];[Y];[Special Flags];[ItemID];[ItemData](;[Qality])}
     *
     * @param line a line of a item map file.
     * @return the generated item.
     */
    public static Item fromString(final String line) {
        final String[] sections = line.split(";");
        if ((sections.length < 5) || (sections.length > 6)) {
            throw new IllegalArgumentException("Item can only hava 5-6 sections: " + line);
        }
        return new Item(
                Integer.parseInt(sections[0]),
                Integer.parseInt(sections[1]),
                Integer.parseInt(sections[3]),
                Integer.parseInt(sections[4]),
                (sections.length == 6) ? Integer.parseInt(sections[5]) : QUALITY_NONE
        );

    }
}
