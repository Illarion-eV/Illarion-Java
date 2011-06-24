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

import java.util.ArrayList;

import illarion.mapedit.history.HistoryActionItem;
import illarion.mapedit.history.HistoryActionTile;
import illarion.mapedit.history.HistoryEntry;
import illarion.mapedit.map.MapItem;
import illarion.mapedit.map.MapStorage;
import illarion.mapedit.map.MapTile;

import illarion.common.util.Location;

import illarion.graphics.Graphics;

/**
 * This class is able to store basic data of a copied area of the map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class CopyMap {
    /**
     * The list of items stored in this copy of a map.
     */
    private final ArrayList<CopyItem> items;

    /**
     * The maximal x coordinate of the data stored in this copy of a map.
     */
    private short maxX;

    /**
     * The maximal y coordinate of the data stored in this copy of a map.
     */
    private short maxY;

    /**
     * The minimal x coordinate of the data stored in this copy of a map.
     */
    private short minX;

    /**
     * The minimal y coordinate of the data stored in this copy of a map.
     */
    private short minY;

    /**
     * The list of tiles stored in this copy of a map.
     */
    private final ArrayList<CopyTile> tiles;

    /**
     * Create a new instance of a copied map that is able to store informations
     * about tiles and items.
     */
    public CopyMap() {
        tiles = new ArrayList<CopyTile>();
        items = new ArrayList<CopyItem>();
        minX = Short.MAX_VALUE;
        minY = Short.MAX_VALUE;
        maxX = Short.MIN_VALUE;
        maxY = Short.MIN_VALUE;
    }

    /**
     * Add a item to this copied map.
     * 
     * @param item the item data
     */
    public void addItem(final CopyItem item) {
        minX = (short) Math.min(minX, item.getPosX());
        minY = (short) Math.min(minY, item.getPosY());
        maxX = (short) Math.max(maxX, item.getPosX());
        maxY = (short) Math.max(maxY, item.getPosY());
        items.add(item);
    }

    /**
     * Add a tile to this map. This will alter the origin and the size of the
     * map and in case this map is insert at one location this tile is added to
     * the real map as well.
     * 
     * @param tile the tile that shall be a part of this copied map
     */
    public void addTile(final CopyTile tile) {
        minX = (short) Math.min(minX, tile.getPosX());
        minY = (short) Math.min(minY, tile.getPosY());
        maxX = (short) Math.max(maxX, tile.getPosX());
        maxY = (short) Math.max(maxY, tile.getPosY());
        tiles.add(tile);
    }

    /**
     * Get the height of this map in tiles.
     * 
     * @return the height of this map
     */
    public int getHeight() {
        return maxY - minY;
    }

    /**
     * Get the width of this map in tiles.
     * 
     * @return the width of this map
     */
    public int getWidth() {
        return maxX - minX;
    }

    /**
     * Insert this map at a location on the map.
     * 
     * @param posX the x coordinate of the location on the map
     * @param posY the y coordinate of the location on the map
     * @param posZ the z coordinate of the location on the map
     * @param entry the history entry that is supposed to save the changes done
     *            to this map in order to undo them
     */
    public void insertAt(final int posX, final int posY, final int posZ,
        final HistoryEntry entry) {
        final int xCorrect = posX - minX;
        final int yCorrect = posY - minY;
        final Location calcLoc = Location.getInstance();

        final int tileCnt = tiles.size();
        for (int i = 0; i < tileCnt; i++) {
            final CopyTile tile = tiles.get(i);
            calcLoc.setSC(tile.getPosX() + xCorrect,
                tile.getPosY() + yCorrect, posZ);
            final MapTile mapTile =
                MapStorage.getInstance().getMapTile(calcLoc);
            if (mapTile != null) {
                while (mapTile.getItemCount() > 0) {
                    final MapItem remItem = mapTile.removeTopItem();
                    entry.addAction(new HistoryActionItem(remItem.getItemId(),
                        remItem.getQuality(), remItem.getData(), calcLoc,
                        HistoryActionItem.DELETE));
                }
                final int oldId = mapTile.getTileId();
                mapTile.setTileId(tile.getTileId());
                entry.addAction(new HistoryActionTile(oldId, mapTile
                    .getTileId(), calcLoc));
            }
        }

        final int itemCnt = items.size();
        for (int i = 0; i < itemCnt; i++) {
            final CopyItem item = items.get(i);
            calcLoc.setSC(item.getPosX() + xCorrect,
                item.getPosY() + yCorrect, posZ);
            final MapTile mapTile =
                MapStorage.getInstance().getMapTile(calcLoc);
            if (mapTile != null) {
                mapTile.addItem(item.getItemId(), item.getQuality(),
                    item.getData());
                entry.addAction(new HistoryActionItem(item.getItemId(), item
                    .getQuality(), item.getData(), calcLoc,
                    HistoryActionItem.ADD));
            }
        }
        calcLoc.recycle();
        Graphics.getInstance().getRenderDisplay().getRenderArea().repaint();
    }
}
