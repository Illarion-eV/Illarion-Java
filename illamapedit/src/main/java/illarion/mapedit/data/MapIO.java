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

import illarion.common.util.CopyrightHeader;
import illarion.mapedit.Lang;
import illarion.mapedit.crash.exceptions.FormatCorruptedException;
import illarion.mapedit.data.formats.Decoder;
import illarion.mapedit.data.formats.Version2Decoder;
import illarion.mapedit.events.menu.MapLoadErrorEvent;
import illarion.mapedit.events.menu.MapLoadedEvent;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class takes care of loading and saving maps
 *
 * @author Tim
 */
public class MapIO {
    private static final Logger LOGGER = Logger.getLogger(MapIO.class);
    private static final String HEADER_V = "V:";
    private static final String HEADER_L = "L:";
    private static final String HEADER_X = "X:";
    private static final String HEADER_Y = "Y:";
    private static final String HEADER_W = "W:";
    private static final String HEADER_H = "H:";
    public static final String EXT_WARP = ".warps.txt";
    public static final String EXT_ITEM = ".items.txt";
    public static final String EXT_TILE = ".tiles.txt";
    public static final String EXT_ANNO = ".annot.txt";
    private static final char NEWLINE = '\n';
    private static final String VERSION_PATTERN = "V: \\d+";
    private static final java.util.Map<String, Decoder> DECODERS = new HashMap<>();

    private static final CopyrightHeader COPYRIGHT_HEADER = new CopyrightHeader(80, null, null, "# ", null);

    static {
        DECODERS.put("2", new Version2Decoder());
    }

    private MapIO() {

    }

    /**
     * Loads a map from a specified file
     *
     * @param path the path
     * @param name the map name
     */
    public static void loadMap(final String path, final String name) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    EventBus.publish(new MapLoadedEvent(loadMapThread(path, name)));
                } catch (FormatCorruptedException ex) {
                    LOGGER.warn("Format wrong.", ex);
                    EventBus.publish(new MapLoadErrorEvent(ex.getMessage()));
                } catch (IOException ex) {
                    LOGGER.warn("Can't load map", ex);
                    EventBus.publish(new MapLoadErrorEvent(Lang.getMsg("gui.error.LoadMap")));
                }
            }
        }).start();
    }

    @Nullable
    public static Map loadMapThread(final String path, final String name) throws IOException {
        LOGGER.debug("Load map " + name + "  at " + path);
        //        Open the streams for all 3 files, containing the map data
        final File tileFile = new File(path, name + EXT_TILE);
        final File itemFile = new File(path, name + EXT_ITEM);
        final File warpFile = new File(path, name + EXT_WARP);
        final File annoFile = new File(path, name + EXT_ANNO);
        final boolean annotationFileExist = checkFile(annoFile);

        final BufferedReader tileInput = new BufferedReader(new InputStreamReader(new FileInputStream(tileFile)));
        final BufferedReader itemInput = new BufferedReader(new InputStreamReader(new FileInputStream(itemFile)));
        final BufferedReader warpInput = new BufferedReader(new InputStreamReader(new FileInputStream(warpFile)));

        final String version;
        String versionLine = tileInput.readLine();
        while (versionLine.startsWith("# ")) {
            versionLine = tileInput.readLine();
        }
        final Decoder decoder;
        if (Pattern.matches(VERSION_PATTERN, versionLine)) {
            version = versionLine.substring(3).trim();
            LOGGER.debug("Mapfileversion: " + version);
            decoder = DECODERS.get(version);
            decoder.newMap(name, path);
        } else {
            version = "1";
            LOGGER.debug("Mapfileversion: " + version);
            decoder = DECODERS.get(version);
            decoder.newMap(name, path);
            decoder.decodeTileLine(versionLine, 0);
        }

        try {
            String s;
            for (int i = 0; ((s = tileInput.readLine()) != null) && !s.isEmpty(); i++) {
                decoder.decodeTileLine(s, i);
            }

            for (int i = 0; ((s = itemInput.readLine()) != null) && !s.isEmpty(); i++) {
                decoder.decodeItemLine(s, i);
            }

            for (int i = 0; ((s = warpInput.readLine()) != null) && !s.isEmpty(); i++) {
                decoder.decodeWarpLine(s, i);
            }
            if (annotationFileExist) {
                final BufferedReader annoInput = new BufferedReader(
                        new InputStreamReader(new FileInputStream(annoFile)));
                for (int i = 0; ((s = annoInput.readLine()) != null) && !s.isEmpty(); i++) {
                    decoder.decodeAnnoLine(s, i);
                }
            }
        } catch (FormatCorruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Unknown error occurred", e);
        }
        Map m = decoder.getDecodedMap();

        LOGGER.debug(String.format("W=%d; H=%d; X=%d; Y=%d; L=%d;", m.getWidth(), m.getHeight(), m.getX(), m.getY(),
                                   m.getY()));

        return m;
    }

    /**
     * Loads the map, with the map name and path, stored in the map object
     *
     * @param map the map to save
     * @throws IOException
     */
    public static void saveMap(@Nonnull final Map map) throws IOException {
        saveMap(map, map.getName(), map.getPath());
    }

    /**
     * Loads the map, with specified the map name and path.
     *
     * @param map the Map
     * @param name map name
     * @param path path to map files
     * @throws IOException
     */
    public static void saveMap(@Nonnull final Map map, final String name, final String path) throws IOException {
        final File tileFile = new File(path, name + EXT_TILE);
        final File itemFile = new File(path, name + EXT_ITEM);
        final File warpFile = new File(path, name + EXT_WARP);
        final File annoFile = new File(path, name + EXT_ANNO);

        if (!checkFile(tileFile) || !checkFile(itemFile) || !checkFile(warpFile) || !checkFile(annoFile)) {
            throw new IOException("Files are folders or can't be created.");
        }

        Charset charset = Charset.forName("ISO-8859-1");

        final BufferedWriter tileOutput = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(tileFile), charset));
        final BufferedWriter itemOutput = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(itemFile), charset));
        final BufferedWriter warpOutput = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(warpFile), charset));
        final BufferedWriter annoOutput = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(annoFile), charset));

        COPYRIGHT_HEADER.writeTo(tileOutput);
        COPYRIGHT_HEADER.writeTo(itemOutput);
        COPYRIGHT_HEADER.writeTo(warpOutput);
        COPYRIGHT_HEADER.writeTo(annoOutput);

        tileOutput.write(String.format("%s %d%s", HEADER_V, 2, NEWLINE));
        tileOutput.write(String.format("%s %d%s", HEADER_L, map.getZ(), NEWLINE));
        tileOutput.write(String.format("%s %d%s", HEADER_X, map.getX(), NEWLINE));
        tileOutput.write(String.format("%s %d%s", HEADER_Y, map.getY(), NEWLINE));
        tileOutput.write(String.format("%s %d%s", HEADER_W, map.getWidth(), NEWLINE));
        tileOutput.write(String.format("%s %d%s", HEADER_H, map.getHeight(), NEWLINE));
        for (int x = 0; x < map.getWidth(); ++x) {
            for (int y = 0; y < map.getHeight(); ++y) {
                final MapTile tile = map.getTileAt(x, y);

                //        <dx>;<dy>;<tileID>;<musicID>
                writeLine(tileOutput, String.format("%d;%d;%s", x, y, tile));

                if (tile == null) {
                    continue;
                }
                if (tile.hasAnnotation()) {
                    writeLine(annoOutput, String.format("%d;%d;0;%s", x, y, tile.getAnnotation()));
                }

                final List<MapItem> items = tile.getMapItems();
                if (items != null) {
                    for (int i = 0; i < items.size(); i++) {
                        //        <dx>;<dy>;<item ID>;<quality>[;<data value>[;...]]
                        writeLine(itemOutput, String.format("%d;%d;%s", x, y, items.get(i)));
                        if (items.get(i).hasAnnotation()) {
                            writeLine(annoOutput,
                                      String.format("%d;%d;%d;%s", x, y, i + 1, items.get(i).getAnnotation()));
                        }
                    }
                }
                final MapWarpPoint warp = tile.getMapWarpPoint();
                if (warp != null) {
                    writeLine(warpOutput, String.format("%d;%d;%s", x, y, warp));
                }
            }
        }
        tileOutput.close();
        itemOutput.close();
        warpOutput.close();
        annoOutput.close();
    }

    private static boolean checkFile(@Nonnull final File file) {
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

    private static void writeLine(@Nonnull final BufferedWriter writer, @Nonnull Object... args) throws IOException {
        for (int i = 0; i < args.length; ++i) {
            writer.write(args[i].toString());
            if (i < (args.length - 1)) {

                writer.write(';');
            } else {
                writer.write(NEWLINE);
            }
        }
    }

    private static void writeLine(@Nonnull final BufferedWriter writer, @Nonnull String str) throws IOException {
        writer.write(str);
        writer.write(NEWLINE);
    }
}
