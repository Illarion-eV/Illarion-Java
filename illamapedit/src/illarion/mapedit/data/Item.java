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
 * @author Tim
 */
public class Item {
    public static final int QUALITY_NONE = -1;
    public static final int QUALITY_DEFAULT = 333;
    public static final int DATA_NONE = 0;

    private int x, y;
    private int id;
    private int itemData;
    private int quality = QUALITY_NONE;

    public Item(final int x, final int y, final int id, final int itemData, final int quality) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.itemData = itemData;
        this.quality = quality;
    }

    public Item(final Item old) {
        this.x = old.x;
        this.y = old.y;
        this.id = old.id;
        this.itemData = itemData;
        this.quality = old.quality;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public int getQuality() {
        return quality;
    }

    public int getItemData() {
        return itemData;
    }

    public static Item fromString(final String line) {
        String[] sections = line.split(";");
        if (sections.length < 5 || sections.length > 6) {
            throw new IllegalArgumentException("Item can only hava 5-6 sections: " + line);
        }
        return new Item(
                Integer.parseInt(sections[0]),
                Integer.parseInt(sections[1]),
                Integer.parseInt(sections[3]),
                Integer.parseInt(sections[4]),
                sections.length == 6 ? Integer.parseInt(sections[5]) : Item.QUALITY_NONE
        );

    }
}
