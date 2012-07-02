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
 * @author Tim
 */
public class Map {
    static final Logger LOGGER = Logger.getLogger(Map.class);
    static final String SLASH = System.getProperty("file.separator");
    static final String HEADER_LEVEL = "L: ";
    static final String HEADER_X = "X: ";
    static final String HEADER_Y = "Y: ";
    static final String HEADER_WIDTH = "W: ";
    static final String HEADER_HEIGHT = "H: ";
    static final char DM = ';';
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

    private final String mapName;
    private final String path;
    private final TileData tileData;
    private final ItemData itemData;
    private final WarpData warpData;

    private Map(final TileData tileData, final ItemData itemData, final WarpData warpData,
                final String path, final String mapName) {

        this.path = path;
        this.mapName = mapName;
        this.tileData = tileData;
        this.itemData = itemData;
        this.warpData = warpData;
    }

    public static Map fromBasePath(final String basePath, final String mapName) throws IOException {

        String path = basePath;
        if (!basePath.endsWith(SLASH)) {
            path += SLASH;
        }
        LOGGER.debug("Load Map from " + path + mapName);
        final TileData tileData = TileData.fromInputStream(new FileInputStream(path + mapName + EXT_TILE_FILE));
        final WarpData warpData = WarpData.fromInputStream(new FileInputStream(path + mapName + EXT_WARP_FILE));
        final ItemData itemData = ItemData.fromInputStream(new FileInputStream(path + mapName + EXT_ITEM_FILE));
        return new Map(tileData, itemData, warpData, path, mapName);
    }

    public int getX() {
        return tileData.getX();
    }

    public int getY() {
        return tileData.getY();
    }

    public int getH() {
        return tileData.getH();
    }

    public int getW() {
        return tileData.getW();
    }

    public int getL() {
        return tileData.getL();
    }

    public TileData getTileData() {
        return tileData;
    }

    public ItemData getItemData() {
        return itemData;
    }

    public WarpData getWarpData() {
        return warpData;
    }

    public void saveToFiles() throws IOException {
        saveToFiles(path, mapName);
    }

    public void saveToFiles(final String path) throws IOException {
        saveToFiles(path, mapName);
    }

    public void saveToFiles(final String path, final String name) throws IOException {
        tileData.saveToFile(new File(path, name + EXT_TILE_FILE));
        warpData.saveToFile(new File(path, name + EXT_WARP_FILE));
        itemData.saveToFile(new File(path, name + EXT_ITEM_FILE));
    }
}
