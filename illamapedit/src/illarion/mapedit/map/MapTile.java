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

import javolution.util.FastTable;

import illarion.mapedit.graphics.ItemStack;
import illarion.mapedit.graphics.Selectable;
import illarion.mapedit.graphics.Tile;
import illarion.mapedit.graphics.TileFactory;
import illarion.mapedit.input.SelectionManager;

import illarion.common.graphics.ItemInfo;
import illarion.common.graphics.Layers;
import illarion.common.graphics.MapConstants;
import illarion.common.util.Location;
import illarion.common.util.Rectangle;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * A map tile represents one tile on the map. It holds links to all items on
 * this tile and allows to import and to export the tile informations.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MapTile implements Selectable {
    /**
     * A small integer array used to decode the tile informations from the map
     * files.
     */
    private static final int[] DECODE = new int[6];

    /**
     * The items located on this tile.
     */
    private FastTable<MapItem> items;

    /**
     * The stack of items that is displayed on the map.
     */
    private ItemStack itemStack;

    /**
     * The location of this tile on the map.
     */
    private final Location loc;

    /**
     * The light on this tile.
     */
    private final SpriteColor localLight = Graphics.getInstance()
        .getSpriteColor();

    /**
     * The map this tile is a part of.
     */
    private final Map parent;

    /**
     * A temporary light value that is used to calculate the tile color before
     * its put in place.
     */
    private final SpriteColor tempLight = Graphics.getInstance()
        .getSpriteColor();

    /**
     * The graphical representation of this tile.
     */
    private Tile tile;

    /**
     * The ID of this tile.
     */
    private int tileId;

    /**
     * This flag is set true in case the tile is currently displayed.
     */
    private boolean tileShown;

    /**
     * Create a new map tile.
     * 
     * @param id the id of the tile
     * @param locX the x coordinate where the tile is located
     * @param locY the y coordinate where the tile is located
     * @param parentMap the parent of this tile
     */
    public MapTile(final int id, final int locX, final int locY,
        final Map parentMap) {
        parent = parentMap;
        tileId = id;
        final Location mapOrigin = parentMap.getOrigin();
        loc = Location.getInstance();
        loc.setSC(locX + mapOrigin.getScX(), locY + mapOrigin.getScY(),
            mapOrigin.getScZ());
        tileShown = false;
    }

    /**
     * Create a new map tile.
     * 
     * @param line the string that is decoded to the tile, source of the string
     *            is a default map file
     * @param parentMap the parent of this tile
     */
    public MapTile(final String line, final Map parentMap) {
        parent = parentMap;
        decodeTile(DECODE, line);
        tileId = DECODE[2];
        final Location mapOrigin = parentMap.getOrigin();
        loc = Location.getInstance();
        loc.setSC(DECODE[0] + mapOrigin.getScX(),
            DECODE[1] + mapOrigin.getScY(), mapOrigin.getScZ());
        tileShown = false;
    }

    /**
     * Function to decode a tile from the map data.
     * 
     * @param ints the array that stores the values
     * @param line the line that is decoded
     * @return the amount of used array entries
     */
    private static int decodeTile(final int[] ints, final String line) {
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

    /**
     * Add a new item to the map.
     * 
     * @param id the id of the item
     * @param quality the quality value of the item
     * @param data the data value of the item
     */
    public void addItem(final int id, final int quality, final int data) {
        addItem(new MapItem(id, loc, quality, data, parent));
    }

    /**
     * Add a new map item to the tile.
     * 
     * @param newItem the new item to add
     */
    public void addItem(final MapItem newItem) {
        if (items == null) {
            items = FastTable.newInstance();
            itemStack = ItemStack.getInstance();
        }
        newItem.setItemStack(itemStack);
        items.add(newItem);
        newItem.setLocation(loc);
        if (tile != null) {
            newItem.showItem(this);
            itemStack.show();
        } else {
            itemStack.hide();
        }
        parent.reportChange();
    }

    /**
     * Add some light to this tile. This prepares a new light value that is
     * transfered when the tile color is rendered.
     * 
     * @param light the new light value of this tile
     */
    public void addLight(final SpriteColor light) {
        tempLight.add(light);
    }

    /**
     * Get the value how much of the view is blocked by this tile. This depends
     * on the items on this tile.
     * 
     * @return <code>0</code> for not blocking the view at all <code>100</code>
     *         for blocking it fully
     */
    public int getBlocksView() {
        int retVal = 0;
        if (items != null) {
            for (int i = 0, n = items.size(); i < n; i++) {
                retVal += items.get(i).getBlocksView();
            }
        }
        return retVal;
    }

    /**
     * Get the face of the map tile. The face is determined by the top item and
     * sets the directions the tile is accepting light from.
     * 
     * @return the face of the tile
     * @see illarion.common.graphics.ItemInfo#FACE_ALL
     * @see illarion.common.graphics.ItemInfo#FACE_S
     * @see illarion.common.graphics.ItemInfo#FACE_SW
     * @see illarion.common.graphics.ItemInfo#FACE_W
     */
    public int getFace() {
        if ((items == null) || items.isEmpty()) {
            return ItemInfo.FACE_ALL;
        }
        return items.get(0).getFace();
    }

    /**
     * Get one item on this map tile.
     * 
     * @param index the index of the item
     * @return the map item
     */
    public MapItem getItem(final int index) {
        if (items == null) {
            throw new IndexOutOfBoundsException();
        }
        return items.get(index);
    }

    /**
     * Get the amount of items placed on that tile.
     * 
     * @return the amount of items on that tile
     */
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    /**
     * Get the location of this map tile.
     * 
     * @return the location of the map tile
     */
    public Location getLocation() {
        return loc;
    }

    /**
     * Get the graphical and data representation of this tile. Its possible that
     * this is the prototype of the tile type from the TileFactory. Use this
     * tile only to read some data. Do not change anything in this instance.
     * 
     * @return the instance of Tile that represents this MapTile
     */
    public Tile getPrototypeTile() {
        if (tile != null) {
            return tile;
        }
        if (tileId == 0) {
            return null;
        }
        return TileFactory.getInstance().getPrototype(Tile.baseID(tileId));
    }

    /**
     * Get the rectangle of the area this tile covers on the screen.
     * 
     * @return the rectangle of this tile on the screen
     */
    public Rectangle getRenderRectangle() {
        if (tile != null) {
            return tile.getBorderRectangle();
        }
        final Rectangle retRect = Rectangle.getInstance();
        retRect.set(loc.getDcX() - (MapConstants.TILE_W / 2), loc.getDcY()
            - (MapConstants.TILE_H / 2), MapConstants.TILE_W,
            MapConstants.TILE_H);
        return retRect;
    }

    /**
     * Get the ID of this tile.
     * 
     * @return the ID of the tile
     */
    public int getTileId() {
        return tileId;
    }

    /**
     * Hide the tile from the display. That function needs to be executed when
     * the map is showed.
     */
    public void hideTile() {
        tileShown = false;

        if (itemStack != null) {
            itemStack.hide();
        }
        if (items != null) {
            for (int i = 0, n = items.size(); i < n; i++) {
                items.get(i).hideItem();
            }
        }

        if (tile != null) {
            tile.recycle();
            tile = null;
        }
    }

    /**
     * Check if this tile is blocked in any way. Either due the tile or due the
     * item located on the tile.
     * 
     * @return <code>true</code> in case the object is blocked
     */
    @Override
    public boolean isBlocked() {
        final Tile mapTile = getPrototypeTile();
        if (mapTile == null) {
            return false;
        }
        if (mapTile.isBlockingTile()) {
            return true;
        }
        if (items != null) {
            final int n = items.size();
            for (int i = 0; i < n; i++) {
                if (items.get(i).isObstracle()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if this tile is on a map that is marked as select able.
     * 
     * @return <code>true</code> in case the map is select able
     */
    @Override
    public boolean isSelectable() {
        return parent.equals(MapStorage.getInstance().getSelectedMap());
    }

    /**
     * Check if this tile is currently selected.
     * 
     * @return <code>true</code> in case the tile is selected
     */
    @Override
    public boolean isSelected() {
        return SelectionManager.getInstance().isSelected(loc);
    }

    /**
     * Remove the item on the top of the list.
     * 
     * @return the item that was removed
     */
    public MapItem removeTopItem() {
        if ((items == null) || items.isEmpty()) {
            return null;
        }
        final MapItem item = items.remove(items.size() - 1);
        item.hideItem();
        if (items.isEmpty()) {
            FastTable.recycle(items);
            itemStack.recycle();
            items = null;
            itemStack = null;
        }
        return item;
    }

    /**
     * Reset the light data of this map tile. This needs to be done before
     * calculating a new light color.
     */
    public void resetLight() {
        tempLight.resetColor();
    }

    /**
     * Export this tile and all the items on to the files for the server.
     * 
     * @param tileWriter the file to write the tile data to
     * @param itemWriter the file to write the item data to
     * @throws IOException thrown in case anything fails during writing the map
     *             data
     */
    public void saveTile(final BufferedWriter tileWriter,
        final BufferedWriter itemWriter) throws IOException {
        tileWriter.write(Integer.toString(loc.getScX()
            - parent.getOrigin().getScX()));
        tileWriter.write(';');
        tileWriter.write(Integer.toString(loc.getScY()
            - parent.getOrigin().getScY()));
        tileWriter.write(';');
        tileWriter.write(Integer.toString(tileId));
        tileWriter.write(';');
        tileWriter.write('0');
        tileWriter.write(';');
        tileWriter.write('0');
        tileWriter.newLine();

        if (tileId == 0) {
            return;
        }

        if (items != null) {
            final int n = items.size();
            for (int i = 0; i < n; i++) {
                items.get(i).saveItem(itemWriter);
            }
        }
    }

    /**
     * Copy the relevant values of another tile.
     * 
     * @param oldTile the other tile to copy the values from
     */
    public void set(final MapTile oldTile) {
        if (items != null) {
            items.clear();
            FastTable.recycle(items);
            itemStack.recycle();
        }

        if (oldTile.items != null) {
            items = FastTable.newInstance();
            itemStack = ItemStack.getInstance();

            MapItem newItem;
            for (final MapItem item : oldTile.items) {
                newItem = new MapItem(item, parent);
                newItem.setItemStack(itemStack);
                items.add(newItem);
                newItem.showItem(this);
            }
        }
        localLight.set(oldTile.localLight);
        tile = oldTile.tile;
        tileId = oldTile.tileId;
    }

    /**
     * Set the ID of this tile.
     * 
     * @param newTileId the new ID of this tile
     */
    public void setTileId(final int newTileId) {
        if (tileId != newTileId) {
            tileId = newTileId;
            parent.reportChange();
            if (tileShown) {
                showTile();
            }
        }
    }

    /**
     * Show the tile on the display.
     */
    public void showTile() {
        tileShown = true;
        if (tile != null) {
            tile.recycle();
        }
        if (tileId == 0) {
            tile = Tile.create(Short.MAX_VALUE, loc);
        } else {
            tile = Tile.create(tileId, loc);
        }
        tile.setScreenPos(loc, Layers.TILE);
        tile.setLight(localLight);
        tile.setSelectable(this);
        tile.show();

        if (items != null) {
            if (tileId == 0) {
                itemStack.hide();
                for (int i = 0, n = items.size(); i < n; i++) {
                    items.get(i).hideItem();
                }
            } else {
                itemStack.show();
                for (int i = 0, n = items.size(); i < n; i++) {
                    items.get(i).setLight(localLight);
                    items.get(i).showItem(this);
                }
            }
        }
    }

    /**
     * Render the light on this tile, using the ambient light of the weather and
     * a factor how much the tile light modifies the ambient light.
     * 
     * @param factor the factor how much the ambient light is modified by the
     *            tile light
     * @param ambientLight the ambient light from the weather
     */
    protected void renderLight(final float factor,
        final SpriteColor ambientLight) {
        tempLight.multiply(factor);
        tempLight.add(ambientLight);
        localLight.set(tempLight);
    }
}
