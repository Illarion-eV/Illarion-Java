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

import java.io.*;
import java.util.Scanner;

/**
 * Stores all tiles of a map, as well as it's dimensions and position.
 *
 * @author Tim
 */
public class TileData {

    /**
     * The dimension, and position of the map.
     */
    private final MapDimensions mapDimensions;

    /**
     * The tiles.
     */
    private final Tile[] tileData;

    /**
     * Creates a new tiledata object with zero tiles and with the dimension.
     *
     * @param mapDimensions
     */
    public TileData(final MapDimensions mapDimensions) {
        this.mapDimensions = mapDimensions;
        tileData = new Tile[mapDimensions.getW() * mapDimensions.getH()];
    }

    /**
     * Copies an old tiledata object, but adapt the size.
     *
     * @param mapDimensions the new dimension.
     * @param old           the old tiledata object.
     */
    public TileData(final MapDimensions mapDimensions, final TileData old) {
        this.mapDimensions = mapDimensions;

        tileData = new Tile[mapDimensions.getW() * mapDimensions.getH()];
        final int minWidth;
        if (mapDimensions.getW() < old.getMapDimensions().getW()) {
            minWidth = mapDimensions.getW();
        } else {
            minWidth = old.getMapDimensions().getW();
        }
        final int minHeight;
        if (mapDimensions.getH() < old.mapDimensions.getH()) {
            minHeight = mapDimensions.getH();
        } else {
            minHeight = old.getMapDimensions().getH();
        }

        System.arraycopy(old.tileData, 0, tileData, 0, minWidth * minHeight);
    }

    /**
     * Sets a tile at a specified position.
     *
     * @param tile the tile to add.
     */
    public void setTileAt(final Tile tile) {
        final int i = (tile.getY() * mapDimensions.getW()) + tile.getX();
        tileData[i] = tile;
    }

    /**
     * Return a tile at a specified position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the tile
     */
    public Tile getTileAt(final int x, final int y) {
        final int i = (y * mapDimensions.getW()) + x;
        return tileData[i];
    }

    /**
     * @return the map dimension.
     */
    public MapDimensions getMapDimensions() {
        return mapDimensions;
    }

    /**
     * Reads the map dimension, the map size and the tiles from the input stream.
     *
     * @param is the input stream
     * @return
     */
    public static TileData fromInputStream(final InputStream is) {
        final Scanner scanner = new Scanner(is);
        final MapDimensions dimensions = new MapDimensions();

        dimensions.setL(Integer.parseInt(scanner.nextLine().substring(3)));
        dimensions.setX(Integer.parseInt(scanner.nextLine().substring(3)));
        dimensions.setY(Integer.parseInt(scanner.nextLine().substring(3)));
        dimensions.setW(Integer.parseInt(scanner.nextLine().substring(3)));
        dimensions.setH(Integer.parseInt(scanner.nextLine().substring(3)));

        final TileData data = new TileData(dimensions);
        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            final Tile t = Tile.fromString(line);
            final int i = (t.getY() * dimensions.getW()) + t.getX();
            data.tileData[i] = t;
        }

        return data;
    }

    /**
     * Saves the map dimension, the map size and the tiles to a given file.
     *
     * @param file the file to save the data in.
     * @throws IOException if a error occurs
     */
    public void saveToFile(final File file) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        final TextBuilder builder = TextBuilder.newInstance();
        builder.append(Map.HEADER_LEVEL).append(mapDimensions.getL()).append(Map.NL);
        builder.append(Map.HEADER_X).append(mapDimensions.getX()).append(Map.NL);
        builder.append(Map.HEADER_Y).append(mapDimensions.getY()).append(Map.NL);
        builder.append(Map.HEADER_WIDTH).append(mapDimensions.getW()).append(Map.NL);
        builder.append(Map.HEADER_HEIGHT).append(mapDimensions.getH()).append(Map.NL);

        for (int y = 0; y < mapDimensions.getH(); ++y) {
            for (int x = 0; x < mapDimensions.getW(); ++x) {
                final int i = (y * mapDimensions.getW()) + x;
                builder.append(x).append(Map.DM);
                builder.append(y).append(Map.DM);
                builder.append(getID(tileData[i])).append(Map.DM);
                builder.append(getMusicID(tileData[i])).append(Map.DM);
                builder.append(0).append(Map.NL);
            }
        }
        writer.write(builder.toString());
        writer.close();
        TextBuilder.recycle(builder);

    }

    private static int getID(final Tile t) {
        return (t == null) ? 0 : t.getId();
    }

    private static int getMusicID(final Tile t) {
        return (t == null) ? 0 : t.getMusicID();
    }
}
