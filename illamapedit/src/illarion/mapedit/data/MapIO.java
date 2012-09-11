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

import illarion.mapedit.crash.exceptions.FormatCorruptedException;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class takes care of loading and saving maps
 *
 * @author Tim
 */
public class MapIO {
    private static final String HEADER_L = "L:";
    private static final String HEADER_X = "X:";
    private static final String HEADER_Y = "Y:";
    private static final String HEADER_W = "W:";
    private static final String HEADER_H = "H:";
    private static final String FORMAT_TILE = "[X];[Y];[TileID];[MusikID];0";
    private static final String FORMAT_ITEM = "[X];[Y];[Special Flags];[ItemID];[ItemData](;[Qality])";
    private static final String FORMAT_WARP = "[StartX];[StartY];[TargetX];[TargetY];[TargetZ]";
    public static final String EXT_WARP = ".warps.txt";
    public static final String EXT_ITEM = ".items.txt";
    public static final String EXT_TILE = ".tiles.txt";
    private static final String NEWLINE = "\n\r";
    private static final Pattern DELIMITER = Pattern.compile(";");

    private MapIO() {
    }

    /**
     * Loads a map from a specified file
     *
     * @param path the path
     * @param name the map name
     * @return the map
     * @throws IOException if an error occurs
     */
    public static Map loadMap(final String path, final String name) throws IOException {

//        Open the streams for all 3 files, containing the map data
        final File tileFile = new File(path, name + EXT_TILE);
        final File itemFile = new File(path, name + EXT_ITEM);
        final File warpFile = new File(path, name + EXT_WARP);
        final BufferedReader tileInput = new BufferedReader(new InputStreamReader(new FileInputStream(tileFile)));
        final BufferedReader itemInput = new BufferedReader(new InputStreamReader(new FileInputStream(itemFile)));
        final BufferedReader warpInput = new BufferedReader(new InputStreamReader(new FileInputStream(warpFile)));

//        Loads the level
        final int z = loadNextHeader(tileInput);
//        Loads the x position
        final int x = loadNextHeader(tileInput);
//        Loads the y position
        final int y = loadNextHeader(tileInput);
//        Loads the width
        final int w = loadNextHeader(tileInput);
//        Loads the height
        final int h = loadNextHeader(tileInput);

        final Map map = new Map(name, path, w, h, x, y, z);
        int i = 0;
        String s;
//        Load all tiles
        while ((s = tileInput.readLine()) != null) {
            i++;
            final String[] sections = DELIMITER.split(s);
            if (sections.length != 5) {
                throw new FormatCorruptedException(tileFile.getPath(), s, i, FORMAT_TILE);
            }
            final int tx = Integer.parseInt(sections[0]);
            final int ty = Integer.parseInt(sections[1]);
            final int tid = Integer.parseInt(sections[2]);
            final int tmid = Integer.parseInt(sections[3]);
            MapTile tile = new MapTile(tid, tmid);
            map.setTileAt(tx, ty, tile);
        }
        i = 0;
//        Load the items
        while ((s = itemInput.readLine()) != null) {
            i++;
            final String[] sections = DELIMITER.split(s);
            if ((sections.length != 5) && (sections.length != 6)) {
                throw new FormatCorruptedException(itemFile.getPath(), s, i, FORMAT_ITEM);
            }
            final int ix = Integer.parseInt(sections[0]);
            final int iy = Integer.parseInt(sections[1]);
            final int iflag = Integer.parseInt(sections[2]);
            final int iid = Integer.parseInt(sections[3]);
            final int idata = Integer.parseInt(sections[4]);
            final int iquality = (sections.length == 6) ? Integer.parseInt(sections[5]) : 0;
            final MapItem item = new MapItem(iid, idata, iquality);
            map.addItemAt(ix, iy, item);
        }
        i = 0;
//        Load the warp points
        while ((s = warpInput.readLine()) != null) {
            i++;
            final String[] sections = DELIMITER.split(s);
            if (sections.length != 5) {
                throw new FormatCorruptedException(warpFile.getPath(), s, i, FORMAT_WARP);
            }
            final int sx = Integer.parseInt(sections[0]);
            final int sy = Integer.parseInt(sections[1]);
            final int tx = Integer.parseInt(sections[2]);
            final int ty = Integer.parseInt(sections[3]);
            final int tz = Integer.parseInt(sections[4]);
            final MapWarpPoint warp = new MapWarpPoint(tx, ty, tz);
            map.setWarpAt(sx, sy, warp);
        }

        return map;
    }

    /**
     * Reads the number, that begins with the 3th character of the line
     *
     * @param input
     * @return
     * @throws IOException
     */
    private static int loadNextHeader(final BufferedReader input) throws IOException {
        return Integer.parseInt(input.readLine().substring(3));
    }

    /**
     * Loads the map, with the map name and path, stored in the map object
     *
     * @param map the map to save
     * @throws IOException
     */
    public static void saveMap(final Map map) throws IOException {
        saveMap(map, map.getName(), map.getPath());
    }

    /**
     * Loads the map, with specified the map name and path.
     *
     * @param map
     * @param name
     * @param path
     * @throws IOException
     */
    public static void saveMap(final Map map, final String name, final String path) throws IOException {
        final File tileFile = new File(path, name + EXT_TILE);
        final File itemFile = new File(path, name + EXT_ITEM);
        final File warpFile = new File(path, name + EXT_WARP);
        if (!checkFile(tileFile) || !checkFile(itemFile) || !checkFile(warpFile)) {
            throw new IOException("Files are folders or can't be created.");
        }
        final BufferedWriter tileOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tileFile)));
        final BufferedWriter itemOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(itemFile)));
        final BufferedWriter warpOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(warpFile)));

        tileOutput.write(String.format("%s%d%s", HEADER_L, map.getZ(), NEWLINE));
        tileOutput.write(String.format("%s%d%s", HEADER_X, map.getX(), NEWLINE));
        tileOutput.write(String.format("%s%d%s", HEADER_Y, map.getY(), NEWLINE));
        tileOutput.write(String.format("%s%d%s", HEADER_W, map.getWidth(), NEWLINE));
        tileOutput.write(String.format("%s%d%s", HEADER_H, map.getHeight(), NEWLINE));

        for (int y = 0; y < map.getWidth(); ++y) {
            for (int x = 0; x < map.getHeight(); ++x) {
                final MapTile tile = map.getTileAt(x, y);
                tileOutput.write(String.format("%d;%d;%d;%d;0%s", x, y, tile.getId(), tile.getMusicID(), NEWLINE));

                final List<MapItem> items = tile.getMapItems();
                if ((items != null) && !items.isEmpty()) {
                    for (final MapItem item : items) {
                        itemOutput.write(String.format("%d;%d;0;%d;%d;%d%s", x, y, item.getId(), item.getItemData(),
                                item.getQuality(), NEWLINE));
                    }
                }
                final MapWarpPoint warp = tile.getMapWarpPoint();
                if (warp != null) {
                    warpOutput.write(String.format("%d;%d;%d;%d;%d%s", x, y, warp.getXTarget(), warp.getYTarget(),
                            warp.getZTarget(), NEWLINE));
                }
            }
        }
    }

    private static boolean checkFile(final File file) {
        if (file.isDirectory()) {
            return false;
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
            return file.exists();
        }
        return true;
    }

}
