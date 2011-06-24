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
package illarion.mapedit.map;

import java.io.BufferedWriter;
import java.io.IOException;

import illarion.mapedit.graphics.Item;
import illarion.mapedit.graphics.ItemFactory;
import illarion.mapedit.graphics.ItemStack;
import illarion.mapedit.graphics.Selectable;

import illarion.common.graphics.Layers;
import illarion.common.util.Location;

import illarion.graphics.SpriteColor;
import illarion.graphics.common.LightSource;

/**
 * This class defines a item displayed on the map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MapItem {
    /**
     * A small integer array used to decode the item informations from the map
     * files.
     */
    private static final int[] DECODE = new int[6];

    /**
     * The data value of this item.
     */
    private final int data;

    /**
     * The graphical entity of this item.
     */
    private Item item;

    /**
     * The ID of this item.
     */
    private final int itemid;

    private LightSource itemLight;

    /**
     * The item stack this item is currently a part of.
     */
    private ItemStack itemStack;

    /**
     * The location of this item on the map.
     */
    private Location loc;

    private SpriteColor localLight;

    /**
     * The map this tile is located on.
     */
    private final Map parent;

    /**
     * The quality value of this item.
     */
    private final int quality;

    /**
     * Create a new map item by setting all the values.
     * 
     * @param id the ID of the item
     * @param locX the x coordinate of the item
     * @param locY the y coordinate of the item
     * @param newData the data value of the item
     * @param newQuality the quality value of the item
     * @param parentMap the map this item is located on
     */
    public MapItem(final int id, final Location newLoc, final int newData,
        final int newQuality, final Map parentMap) {
        parent = parentMap;
        itemid = id;
        loc = newLoc;
        data = newData;
        quality = newQuality;
    }

    /**
     * Copy constructor that copies the data of another item object.
     * 
     * @param org the source of the copy operation
     * @param parentMap the new parent map of this item on the map
     */
    public MapItem(final MapItem org, final Map parentMap) {
        parent = parentMap;
        loc = Location.getInstance();
        loc = org.loc;
        itemid = org.itemid;
        data = org.data;
        quality = org.quality;
    }

    /**
     * Create a map item by decoding one line from a map file.
     * 
     * @param line the line to decode
     * @param parentMap the map this item is located on
     */
    public MapItem(final String line, final Map parentMap) {
        parent = parentMap;
        decodeItem(DECODE, line);

        final Location mapOrigin = parentMap.getOrigin();
        loc = Location.getInstance();
        loc.setSC(DECODE[0] + mapOrigin.getScX(),
            DECODE[1] + mapOrigin.getScY(), mapOrigin.getScZ());
        itemid = DECODE[3];
        data = DECODE[4];
        quality = DECODE[5];
    }

    /**
     * Function to decode a item from the map data.
     * 
     * @param ints the array that stores the values
     * @param line the line that is decoded
     * @return the amount of used array entries
     */
    private static int decodeItem(final int[] ints, final String line) {
        int start = 0;
        int end;
        int count = 0;
        boolean more = true;

        while (more) {
            end = line.indexOf(';', start);
            if (end < start) {
                more = false;
                end = line.length();
            }

            ints[count++] = Integer.parseInt(line.substring(start, end));
            start = end + 1;
        }

        return count;
    }

    public int getBlocksView() {
        Item usedItem;
        if (item != null) {
            usedItem = item;
        } else {
            usedItem = ItemFactory.getInstance().getPrototype(itemid);
        }
        return usedItem.getInfos().getOpacity();
    }

    /**
     * Get the data value.
     * 
     * @return the data value
     */
    public int getData() {
        return data;
    }

    public int getFace() {
        Item usedItem;
        if (item != null) {
            usedItem = item;
        } else {
            usedItem = ItemFactory.getInstance().getPrototype(itemid);
        }
        return usedItem.getInfos().getFace();
    }

    /**
     * Get the ID of the item.
     * 
     * @return the ID of the item
     */
    public int getItemId() {
        return itemid;
    }

    /**
     * Get the location of the tile.
     * 
     * @return the location of the tile
     */
    public Location getLocation() {
        return loc;
    }

    /**
     * Get the quality value.
     * 
     * @return the quality
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Hide the item from the display.
     */
    public void hideItem() {
        if (item != null) {
            itemStack.remove(item);
            item.recycle();
            item = null;

            if (itemLight != null) {
                MapStorage.getInstance().getLightTracer().remove(itemLight);
            }
        }
    }

    public boolean isObstracle() {
        Item usedItem;
        if (item != null) {
            usedItem = item;
        } else {
            usedItem = ItemFactory.getInstance().getPrototype(itemid);
        }
        return usedItem.getInfos().isObstacle();
    }

    public void saveItem(final BufferedWriter itemWriter) throws IOException {
        itemWriter.write(Integer.toString(loc.getScX()
            - parent.getOrigin().getScX()));
        itemWriter.write(';');
        itemWriter.write(Integer.toString(loc.getScY()
            - parent.getOrigin().getScY()));
        itemWriter.write(';');
        itemWriter.write('0');
        itemWriter.write(';');
        itemWriter.write(Integer.toString(itemid));
        itemWriter.write(';');
        itemWriter.write(Integer.toString(data));
        itemWriter.write(';');
        itemWriter.write(Integer.toString(quality));
        itemWriter.newLine();
    }

    public void setItemStack(final ItemStack stack) {
        itemStack = stack;
    }

    /**
     * Set the light this item is colored in.
     * 
     * @param newLight the new instance of light this entity is colored with
     */
    public void setLight(final SpriteColor newLight) {
        localLight = newLight;
    }

    public void setLocation(final Location newLoc) {
        loc = newLoc;
    }

    /**
     * Show the item on the screen.
     */
    public void showItem(final Selectable newSelectable) {
        if (item != null) {
            itemStack.remove(item);
            item.recycle();
        }
        item = Item.create(itemid, loc);
        item.setScreenPos(loc, Layers.ITEM);
        item.setLight(localLight);
        item.setSelectable(newSelectable);
        itemStack.add(item);

        if (item.getInfos().getLight() > 0) {
            itemLight =
                LightSource.createLight(loc, item.getInfos().getLight());
            MapStorage.getInstance().getLightTracer().add(itemLight);
        }
    }
}
