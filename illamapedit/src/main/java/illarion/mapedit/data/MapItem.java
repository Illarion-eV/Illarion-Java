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

import javolution.text.TextBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single item, with a position, an id, a qualityDurability, and data.
 *
 * @author Tim
 * @author Fredrik K
 */
public class MapItem {
    /**
     * Represents the default qualityDurability
     */
    public static final int QUALITY_DEFAULT = 333;

    /**
     * The item id.
     */
    private final int itemId;
    /**
     * The data of this item.
     */
    @Nullable
    private List<String> itemData;
    /**
     * The qualityDurability of this item.
     */
    private int qualityDurability;
    private String annotation;

    /**
     * Creates a new Item
     *
     * @param itemId The item id.
     * @param itemData The data of this item.
     * @param qualityDurability The qualityDurability of this item.
     */
    public MapItem(final int itemId, @Nullable final List<String> itemData, final int qualityDurability) {
        this.itemId = itemId;
        if ((itemData != null) && !itemData.isEmpty()) {
            this.itemData = new ArrayList<>(itemData);
        }
        this.qualityDurability = qualityDurability;
    }

    /**
     * Creates a new copy of an existing item.
     *
     * @param old the old instance.
     */
    public MapItem(@Nonnull final MapItem old) {
        itemId = old.itemId;
        itemData = new ArrayList<>(old.itemData);
        qualityDurability = old.qualityDurability;
    }

    /**
     * Creates a new Item
     *
     * @param itemId The item id.
     *
     */
    public MapItem(final int itemId) {
        this(itemId, new ArrayList<String>(), QUALITY_DEFAULT);
    }

    public String getAnnotation() {
        return annotation;
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
     * Returns the qualityDurability of the item.
     *
     * @return the qualityDurability
     */
    public int getQualityDurability() {
        return qualityDurability;
    }

    /**
     * Returns the quality of the item.
     *
     * @return the quality
     */
    public int getQuality() {
        return qualityDurability / 100;
    }

    /**
     * Returns the durability of the item.
     *
     * @return the durability
     */
    public int getDurability() {
        return qualityDurability % 100;
    }

    /**
     * Sets the quality of the item.
     *
     * @param quality the quality to set
     */
    public void setQuality(int quality) {
        qualityDurability = (quality * 100) + getDurability();
    }

    /**
     * Sets the durability of the item.
     *
     * @param durability the durability to set
     */
    public void setDurability(int durability) {
        qualityDurability = (getQuality() * 100 + durability);
    }

    /**
     * Returns the data-value of this item.
     *
     * @return the data-value.
     */
    @Nullable
    public List<String> getItemData() {
        return itemData;
    }

    public boolean isItemDataNullOrEmpty() {
        return (itemData == null) || itemData.isEmpty();
    }

    public void addItemData(final String data) {
        if (itemData == null) {
            itemData = new ArrayList<>();
        }
        itemData.add(data);
    }

    public void addItemData(final int index, final String data) {
        if (itemData == null) {
            itemData = new ArrayList<>();
        }
        itemData.set(index, data);
    }

    public void removeItemData(final int index) {
        if (itemData != null) {
            itemData.remove(index);
        }
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (super.equals(obj)) {
            return true;
        }

        if (obj instanceof MapItem) {
            return ((MapItem) obj).itemId == itemId;
        }
        return false;
    }

    public boolean hasAnnotation() {
        return (annotation != null) && !annotation.isEmpty();
    }

    @Override
    public int hashCode() {
        return itemId;
    }

    public void setAnnotation(final String annotation) {
        this.annotation = annotation;
    }

    /**
     * Serializes this MapItem object into a string in the format:<br>
     * {@code <item ID>;<qualityDurability>[;<data value>[;...]]}
     *
     * @return the serialized String
     */
    @Nonnull
    @Override
    public String toString() {
        final TextBuilder builder = new TextBuilder();
        builder.append(itemId).append(';');
        builder.append(qualityDurability);

        if ((itemData != null) && !itemData.isEmpty()) {
            builder.append(';').append(join(itemData, ";"));
        }

        return builder.toString();
    }

    public static String join(@Nonnull final List<String> itemData, final String joinWith) {
        String retVal = "";
        boolean firstRun = true;
        for (final String s : itemData) {
            if (firstRun) {
                firstRun = false;
            } else {
                retVal += joinWith;
            }
            retVal += s;
        }
        return retVal;
    }
}
