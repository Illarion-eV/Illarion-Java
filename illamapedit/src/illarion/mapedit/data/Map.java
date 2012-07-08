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

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class represents a whole map, with all tiles, items, and warp points.
 *
 * @author Tim
 */
public class Map {

    /**
     * The logger instance for this class.
     */
    static final Logger LOGGER = Logger.getLogger(Map.class);

    /**
     * The system dependent slash, or backslash.
     */
    static final String SLASH = System.getProperty("file.separator");

    /**
     * The header in the tiles data that marks the level of the map.
     */
    static final String HEADER_LEVEL = "L: ";

    /**
     * The header in the tiles data that marks the x coordinate of the origin of
     * the map.
     */
    static final String HEADER_X = "X: ";

    /**
     * The header in the tiles data that marks the y coordinate of the origin of
     * the map.
     */
    static final String HEADER_Y = "Y: ";

    /**
     * The header in the tiles data that marks the width of the map.
     */
    static final String HEADER_WIDTH = "W: ";

    /**
     * The header in the tiles data that marks the height of the map.
     */
    static final String HEADER_HEIGHT = "H: ";

    /**
     * The char that works as delimiter between the values.
     */
    static final char DM = ';';

    /**
     * The Microsoft Windows newline.
     */
    static final String NL = "\r\n";

    /**
     * The file extension of the items file.
     */
    static final String EXT_ITEM_FILE = ".items.txt";

    /**
     * The file extension of the tiles file.
     */
    static final String EXT_TILE_FILE = ".tiles.txt";

    /**
     * The file extension of the warp fields file.
     */
    private static final String EXT_WARP_FILE = ".warps.txt";

    /**
     * The name of the map.
     */
    private String mapName;

    /**
     * The original path of the map.
     */
    private String path;

    /**
     * The tiles on that map.
     */
    private TileData tileData;

    /**
     * All items on the map.
     */
    private ItemData itemData;

    /**
     * All warp points.
     */
    private WarpData warpData;

    /**
     * Creates a new map instance with a given TileData, ItemData and WarpData.
     *
     * @param tileData
     * @param itemData
     * @param warpData
     * @param path     the folder to store the files in.
     * @param mapName  the name of the files, without extension.
     */
    public Map(final TileData tileData, final ItemData itemData, final WarpData warpData,
               final String path, final String mapName) {

        this.path = path;
        this.mapName = mapName;
        this.tileData = tileData;
        this.itemData = itemData;
        this.warpData = warpData;
    }

    /**
     * Creates a completely new map, with a given path, name and size.
     *
     * @param mapName the name of the files, without extension.
     * @param path    the folder to store the files in.
     * @param size    the size and position of the map.
     */
    public Map(final String mapName, final String path, final MapDimensions size) {
        this.mapName = mapName;
        this.path = path;
        tileData = new TileData(size);
        itemData = new ItemData();
        warpData = new WarpData();
    }

    /**
     * Loads a map from a *.tiles.txt, *.items.txt and a *.warps.txt file.
     *
     * @param basePath the folder to store the files in.
     * @param mapName  the name of the files, without extension.
     * @return the new Map instance.
     * @throws IOException
     */
    public static Map fromBasePath(final String basePath, final String mapName) throws IOException {

        final String path = basePath;


        LOGGER.debug("Load Map from " + path + mapName);
        final TileData tileData =
                TileData.fromInputStream(new FileInputStream(new File(path, mapName + EXT_TILE_FILE)));
        final WarpData warpData =
                WarpData.fromInputStream(new FileInputStream(new File(path, mapName + EXT_WARP_FILE)));
        final ItemData itemData =
                ItemData.fromInputStream(new FileInputStream(new File(path, mapName + EXT_ITEM_FILE)));
        return new Map(tileData, itemData, warpData, path, mapName);
    }

    /**
     * Returns the X position of the map.
     *
     * @return
     */
    public int getX() {
        return tileData.getMapDimensions().getX();
    }

    /**
     * Returns the Y position of the map
     *
     * @return
     */
    public int getY() {
        return tileData.getMapDimensions().getY();
    }

    /**
     * Returns the height of the map.
     *
     * @return
     */
    public int getH() {
        return tileData.getMapDimensions().getH();
    }

    /**
     * Returns the width of the map.
     *
     * @return
     */
    public int getW() {
        return tileData.getMapDimensions().getW();
    }

    /**
     * Returns the level (Z position) of the map.
     *
     * @return
     */
    public int getL() {
        return tileData.getMapDimensions().getL();
    }

    /**
     * Returns the tile data.
     *
     * @return
     */
    public TileData getTileData() {
        return tileData;
    }

    /**
     * Returns the item data
     *
     * @return
     */
    public ItemData getItemData() {
        return itemData;
    }

    /**
     * Returns the warp data.
     *
     * @return
     */
    public WarpData getWarpData() {
        return warpData;
    }

    /**
     * Saves the files to the path, specified in the constructor.
     *
     * @throws IOException
     */
    public void saveToFiles() throws IOException {
        saveToFiles(path, mapName);
    }

    /**
     * Saves the files with the name, specified in the constructor but with another path.
     *
     * @param path the path to save the map in.
     * @throws IOException
     */
    public void saveToFiles(final String path) throws IOException {
        saveToFiles(path, mapName);
    }

    /**
     * Saves the map to a specified directory with a specified name.
     *
     * @param path the folder to save the map in.
     * @param name the name of the map, without extension.
     * @throws IOException
     */
    public void saveToFiles(final String path, final String name) throws IOException {
        tileData.saveToFile(new File(path, name + EXT_TILE_FILE));
        warpData.saveToFile(new File(path, name + EXT_WARP_FILE));
        itemData.saveToFile(new File(path, name + EXT_ITEM_FILE));
    }
}
