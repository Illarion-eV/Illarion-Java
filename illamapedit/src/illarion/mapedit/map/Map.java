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

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import illarion.mapedit.MapEditor;
import illarion.mapedit.history.History;

import illarion.common.util.Location;
import illarion.common.util.Rectangle;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * This class defines a loaded map with all its tiles.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class Map {
    /**
     * The color instance that is used for calculations of the light values.
     */
    private static final SpriteColor CALC_COLOR = Graphics.getInstance()
        .getSpriteColor();

    /**
     * The header introduction text for the width of the map.
     */
    @SuppressWarnings("nls")
    private static final String HEADER_HEIGHT = "H: ";

    /**
     * The header introduction text for the level of the map.
     */
    @SuppressWarnings("nls")
    private static final String HEADER_LEVEL = "L: ";

    /**
     * The header introduction text for the x coordinate of the origin of the
     * map.
     */
    @SuppressWarnings("nls")
    private static final String HEADER_LOCX = "X: ";

    /**
     * The header introduction text for the y coordinate of the origin of the
     * map.
     */
    @SuppressWarnings("nls")
    private static final String HEADER_LOCY = "Y: ";

    /**
     * The header introduction text for the width of the map.
     */
    @SuppressWarnings("nls")
    private static final String HEADER_WIDTH = "W: ";

    /**
     * Flag that stores of the map was changed since the last save or not.
     */
    private boolean changed;

    /**
     * The history of that map that saves all changes done to it.
     */
    private final History history;

    /**
     * The dimension of the map in tiles.
     */
    private final Dimension mapDim;

    /**
     * The name of the map.
     */
    private String name;

    /**
     * The origin of the map.
     */
    private final Location origin;

    /**
     * The tiles of this map.
     */
    private final MapTile[] tiles;

    /**
     * Constructor of the map that takes the filename of the map file to decode
     * the map.
     * 
     * @param mapName the name of the map
     */
    @SuppressWarnings("nls")
    public Map(final String mapName) {
        final File baseFileName = MapEditor.getConfig().getFile("mapDir");
        final String filenameTiles =
            baseFileName + File.separator + mapName + ".tiles.txt";
        final String filenameItems =
            baseFileName + File.separator + mapName + ".items.txt";

        final File mapFile = new File(filenameTiles);
        if (!mapFile.exists() || !mapFile.isFile()) {
            throw new IllegalArgumentException("Mapfile " + filenameTiles
                + " not found");
        }
        if (!mapFile.canRead()) {
            throw new IllegalArgumentException("Can't read mapfile "
                + filenameTiles);
        }

        BufferedReader reader = null;
        int level = 0;
        int locX = 0;
        int locY = 0;
        int width = 0;
        int height = 0;
        try {
            reader = new BufferedReader(new FileReader(mapFile));

            int headerLinesDone = 0;
            String line;

            while (headerLinesDone < 5) {
                line = reader.readLine();
                if (line == null) {
                    throw new IOException(
                        "Error while decoding header, map corrupted");
                }
                if (line.startsWith(HEADER_LEVEL)) {
                    level =
                        Integer
                            .parseInt(line.substring(HEADER_LEVEL.length()));
                    headerLinesDone++;
                } else if (line.startsWith(HEADER_LOCX)) {
                    locX =
                        Integer.parseInt(line.substring(HEADER_LOCX.length()));
                    headerLinesDone++;
                } else if (line.startsWith(HEADER_LOCY)) {
                    locY =
                        Integer.parseInt(line.substring(HEADER_LOCY.length()));
                    headerLinesDone++;
                } else if (line.startsWith(HEADER_WIDTH)) {
                    width =
                        Integer
                            .parseInt(line.substring(HEADER_WIDTH.length()));
                    headerLinesDone++;
                } else if (line.startsWith(HEADER_HEIGHT)) {
                    height =
                        Integer
                            .parseInt(line.substring(HEADER_HEIGHT.length()));
                    headerLinesDone++;
                }
            }
        } catch (final FileNotFoundException ex) {
            throw new IllegalArgumentException("Can't read mapfile "
                + filenameTiles);
        } catch (final IOException ex) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException ex1) {
                    // nothing
                }
            }
            throw new IllegalArgumentException("Can't read mapfile "
                + filenameTiles);
        }

        origin = Location.getInstance();
        origin.setSC(locX, locY, level);
        mapDim = new Dimension(width, height);

        tiles = new MapTile[width * height];

        try {

            int decodedTiles = 0;
            MapTile currentTile;
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                decodedTiles++;

                currentTile = new MapTile(line, this);
                tiles[locationToIndex(currentTile.getLocation())] =
                    currentTile;
            }

            if (decodedTiles != (width * height)) {
                throw new IOException(
                    "Error while decoding map data, map corrupted");
            }
        } catch (final IOException ex) {
            try {
                reader.close();
            } catch (final IOException ex1) {
                // nothing
            }
            throw new IllegalArgumentException("Can't read mapfile "
                + filenameTiles);
        } finally {
            try {
                reader.close();
            } catch (final IOException ex1) {
                // nothing
            }
        }

        final File itemsFile = new File(filenameItems);
        if (!itemsFile.exists() || !itemsFile.isFile()) {
            throw new IllegalArgumentException("Item file " + filenameItems
                + " not found");
        }
        if (!itemsFile.canRead()) {
            throw new IllegalArgumentException("Can't read mapfile "
                + filenameItems);
        }

        BufferedReader itemReader = null;
        try {
            itemReader = new BufferedReader(new FileReader(itemsFile));

            String line;
            MapItem currentItem;
            while (true) {
                line = itemReader.readLine();
                if (line == null) {
                    break;
                }

                currentItem = new MapItem(line, this);
                tiles[locationToIndex(currentItem.getLocation())]
                    .addItem(currentItem);
            }
        } catch (final FileNotFoundException ex) {
            throw new IllegalArgumentException("Can't read mapfile "
                + filenameItems);
        } catch (final IOException ex) {
            if (itemReader != null) {
                try {
                    itemReader.close();
                } catch (final IOException ex1) {
                    // nothing
                }
            }
            throw new IllegalArgumentException("Can't read mapfile "
                + filenameItems);
        } finally {
            try {
                if (itemReader != null) {
                    itemReader.close();
                }
            } catch (final IOException ex1) {
                // nothing
            }
        }

        history = History.getInstance();

        name = mapName;
        changed = false;
        System.gc();
    }

    /**
     * Create a empty map.
     * 
     * @param mapName the name of the map
     * @param newOrigin the origin location of the map
     * @param newDim the dimension of the map
     */
    public Map(final String mapName, final Location newOrigin,
        final Dimension newDim) {
        name = mapName;

        origin = Location.getInstance();
        origin.set(newOrigin);
        mapDim = new Dimension();
        mapDim.setSize(newDim);

        tiles = new MapTile[mapDim.width * mapDim.height];

        for (int x = 0; x < mapDim.width; x++) {
            for (int y = 0; y < mapDim.height; y++) {
                final MapTile tile = new MapTile(0, x, y, this);
                tiles[locationToIndex(tile.getLocation())] = tile;
            }
        }

        changed = true;
        history = History.getInstance();
    }

    /**
     * Save the map even if its not needed.
     */
    @SuppressWarnings("nls")
    public void forceSave() {
        if (name == null) {
            return;
        }

        final File baseFileName = MapEditor.getConfig().getFile("mapDir");
        final String filenameTiles =
            baseFileName + File.separator + name + ".tiles.txt";
        final String filenameItems =
            baseFileName + File.separator + name + ".items.txt";

        final File tilesFile = new File(filenameTiles);
        final File itemsFile = new File(filenameItems);

        final File tilesFileBackup = new File(filenameTiles + ".bak");
        final File itemsFileBackup = new File(filenameItems + ".bak");

        if (tilesFileBackup.exists()) {
            tilesFileBackup.delete();
        }
        if (itemsFileBackup.exists()) {
            itemsFileBackup.delete();
        }
        if (tilesFile.exists()) {
            tilesFile.renameTo(tilesFileBackup);
        }
        if (itemsFile.exists()) {
            itemsFile.renameTo(itemsFileBackup);
        }

        BufferedWriter tilesWriter = null;
        BufferedWriter itemsWriter = null;

        try {
            tilesFile.createNewFile();
            itemsFile.createNewFile();

            tilesWriter = new BufferedWriter(new FileWriter(tilesFile));
            itemsWriter = new BufferedWriter(new FileWriter(itemsFile));

            tilesWriter.write(HEADER_LEVEL);
            tilesWriter.write(Integer.toString(origin.getScZ()));
            tilesWriter.newLine();

            tilesWriter.write(HEADER_LOCX);
            tilesWriter.write(Integer.toString(origin.getScX()));
            tilesWriter.newLine();

            tilesWriter.write(HEADER_LOCY);
            tilesWriter.write(Integer.toString(origin.getScY()));
            tilesWriter.newLine();

            tilesWriter.write(HEADER_WIDTH);
            tilesWriter.write(Integer.toString(mapDim.width));
            tilesWriter.newLine();

            tilesWriter.write(HEADER_HEIGHT);
            tilesWriter.write(Integer.toString(mapDim.height));
            tilesWriter.newLine();

            final int tilesCnt = tiles.length;
            try {
                for (int i = 0; i < tilesCnt; i++) {
                    tiles[i].saveTile(tilesWriter, itemsWriter);
                }
            } catch (final IOException ex) {
                throw new IllegalStateException("Failed writing tiles", ex);
            }

            tilesWriter.flush();
            itemsWriter.flush();
        } catch (final Exception ex) {
            if (tilesWriter != null) {
                try {
                    tilesWriter.close();
                } catch (final IOException ex1) {
                    // nothing to be done
                }
            }
            if (itemsWriter != null) {
                try {
                    itemsWriter.close();
                } catch (final IOException ex1) {
                    // nothing to be done
                }
            }
            tilesFile.delete();
            if (tilesFileBackup.exists()) {
                tilesFileBackup.renameTo(tilesFile);
            }
            itemsFile.delete();
            if (itemsFileBackup.exists()) {
                itemsFileBackup.renameTo(itemsFile);
            }
            return;
        }
        try {
            tilesWriter.close();
        } catch (final IOException ex1) {
            // nothing to be done
        }
        try {
            itemsWriter.close();
        } catch (final IOException ex1) {
            // nothing to be done
        }
        if (tilesFileBackup.exists()) {
            tilesFileBackup.delete();
        }
        if (itemsFileBackup.exists()) {
            itemsFileBackup.delete();
        }
    }

    /**
     * Get the dimension of this map.
     * 
     * @return the dimension of the map
     */
    public Dimension getDimension() {
        return mapDim;
    }

    /**
     * Get the history of this map.
     * 
     * @return the history of this map
     */
    public History getHistory() {
        return history;
    }

    /**
     * Get the name of the map.
     * 
     * @return the name of the map
     */
    public String getMapName() {
        return name;
    }

    /**
     * Get the origin location of this map.
     * 
     * @return the origin location
     */
    public Location getOrigin() {
        return origin;
    }

    /**
     * This function returns the raw map data stored in this map. This data can
     * be used for reading operations, how ever it must not ever be changed.
     * 
     * @return the raw data of this map
     */
    public MapTile[] getRawMapData() {
        return tiles;
    }

    /**
     * Get the rectangle that defines the area that is covered by this map.
     * 
     * @return the rectangle of the area that is covered by this map
     */
    public Rectangle getRenderRectangle() {
        final Rectangle renderRect = Rectangle.getInstance();
        renderRect.set(0, 0, 0, 0);

        Rectangle tempRect;
        tempRect = tiles[0].getRenderRectangle();
        renderRect.add(tempRect);
        tempRect.recycle();

        tempRect = tiles[mapDim.width - 1].getRenderRectangle();
        renderRect.add(tempRect);
        tempRect.recycle();

        tempRect =
            tiles[mapDim.width * (mapDim.height - 1)].getRenderRectangle();
        renderRect.add(tempRect);
        tempRect.recycle();

        tempRect =
            tiles[(mapDim.width * mapDim.height) - 1].getRenderRectangle();
        renderRect.add(tempRect);
        tempRect.recycle();

        renderRect.set(renderRect.getX() - 10, renderRect.getY() - 10,
            renderRect.getWidth() + 20, renderRect.getHeight() + 20);

        return renderRect;
    }

    /**
     * Get a tile from a specified location on the map. In case there is no tile
     * on this location, return <code>null</code>.
     * 
     * @param tileLoc the location to search the tile
     * @return the found tile or <code>null</code> in case there is none
     */
    public MapTile getTile(final Location tileLoc) {
        final int index = locationToIndex(tileLoc);
        if (index == -1) {
            return null;
        }
        return tiles[index];
    }

    /**
     * Hide all tiles from the active screen.
     */
    public void hideMap() {
        final int tilesCnt = tiles.length;
        for (int i = 0; i < tilesCnt; i++) {
            tiles[i].hideTile();
        }
    }

    /**
     * Check if the map was changed since the last save.
     * 
     * @return <code>true</code> in case the map was changed
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Recalculate all overlays on this map.
     */
    public void recalculateOverlays() {
        final int tilesCnt = tiles.length;
        for (int i = 0; i < tilesCnt; i++) {
            MapTransitions.getInstance().checkTile(tiles[i].getLocation());
        }
    }

    /**
     * Render the prepared lights of all tiles on this map.
     */
    public void renderLights() {
        CALC_COLOR.set(0.2f, 0.2f, 0.4f);
        final float factor = 1.f - CALC_COLOR.getLuminationf();

        final int tilesCnt = tiles.length;
        for (int i = 0; i < tilesCnt; i++) {
            tiles[i].renderLight(factor, CALC_COLOR);
        }
    }

    /**
     * Report that the map was changed. After this the map will be saved at the
     * automatic save cycle or when saved by hand.
     */
    public void reportChange() {
        changed = true;
    }

    /**
     * Reset the light value of all tiles on this map.
     */
    public void resetLights() {
        final int tilesCnt = tiles.length;
        for (int i = 0; i < tilesCnt; i++) {
            tiles[i].resetLight();
        }
    }

    /**
     * Save the map in case its needed.
     */
    public void save() {
        if (changed) {
            forceSave();
        }
    }

    /**
     * Set the map name of this map.
     * 
     * @param newName the new map name
     */
    public void setMapName(final String newName) {
        name = newName;
    }

    /**
     * Show all tiles on the active screen.
     */
    public void showMap() {
        final int tilesCnt = tiles.length;
        for (int i = 0; i < tilesCnt; i++) {
            tiles[i].showTile();
        }
    }

    /**
     * Convert a location to the index value in the array that stores the tiles
     * in this class.
     * 
     * @param loc the location
     * @return the index in the tiles array or -1 in case the position is not on
     *         the map
     */
    private int locationToIndex(final Location loc) {
        if (loc.getScZ() != origin.getScZ()) {
            return -1;
        }
        final int x = loc.getScX() - origin.getScX();
        final int y = loc.getScY() - origin.getScY();

        if ((x < 0) || (y < 0) || (y >= mapDim.height) || (x >= mapDim.width)) {
            return -1;
        }
        return x + (y * mapDim.width);
    }
}
