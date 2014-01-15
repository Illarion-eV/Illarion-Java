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
import illarion.mapedit.data.formats.DataType;
import illarion.mapedit.data.formats.Decoder;
import illarion.mapedit.data.formats.DecoderFactory;
import illarion.mapedit.events.menu.MapLoadErrorEvent;
import illarion.mapedit.events.menu.MapLoadedEvent;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.WRITE;

/**
 * This class takes care of loading and saving maps
 *
 * @author Tim
 */
public class MapIO {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapIO.class);
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
    private static final Pattern VERSION_PATTERN = Pattern.compile("V: (\\d+)");
    private static final CopyrightHeader COPYRIGHT_HEADER = new CopyrightHeader(80, null, null, "# ", null);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final Charset CHARSET = Charset.forName("ISO-8859-1");
    private static final DecoderFactory DECODER_FACTORY = new DecoderFactory();

    private MapIO() {

    }

    /**
     * Loads a map from a specified file
     *
     * @param path the path
     * @param name the map name
     */
    public static void loadMap(final Path path, final String name) {
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

    private static final class LoadFileCallable implements Callable<List<String>> {
        @Nonnull
        private final Path file;

        private LoadFileCallable(@Nonnull final Path file) {
            this.file = file;
        }

        @Override
        public List<String> call() throws Exception {
            try {
                return Files.readAllLines(file, CHARSET);
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }
    }

    private static final class LoadMapDataCallable implements Callable<Void> {
        @Nonnull
        private final DataType type;
        @Nonnull
        private final Decoder decoder;
        @Nonnull
        private final List<String> lines;

        private LoadMapDataCallable(
                @Nonnull final DataType type, @Nonnull final Decoder decoder, @Nonnull final List<String> lines) {
            this.type = type;
            this.decoder = decoder;
            this.lines = lines;
        }

        @Override
        public Void call() throws Exception {
            final int size = lines.size();
            for (int i = 0; i < size; i++) {
                decoder.decodeLine(type, lines.get(i), i);
            }
            return null;
        }
    }

    @Nullable
    public static Map loadMapThread(@Nonnull final Path path, @Nonnull final String name) throws IOException {
        LOGGER.debug("Load map {} at {}", name, path);
        //        Open the streams for all 3 files, containing the map data
        final Path tileFile = path.resolve(name + EXT_TILE);
        final Path itemFile = path.resolve(name + EXT_ITEM);
        final Path warpFile = path.resolve(name + EXT_WARP);
        final Path annoFile = path.resolve(name + EXT_ANNO);

        final Future<List<String>> tileLoadFuture = EXECUTOR_SERVICE.submit(new LoadFileCallable(tileFile));
        final Future<List<String>> itemLoadFuture = EXECUTOR_SERVICE.submit(new LoadFileCallable(itemFile));
        final Future<List<String>> warpLoadFuture = EXECUTOR_SERVICE.submit(new LoadFileCallable(warpFile));
        final Future<List<String>> annoLoadFuture = EXECUTOR_SERVICE.submit(new LoadFileCallable(annoFile));

        try {
            List<String> tileLines = tileLoadFuture.get();
            Iterator<String> tileLinesItr = tileLines.iterator();
            Decoder decoder = null;
            int i = 0;
            while (tileLinesItr.hasNext()) {
                i++;
                String line = tileLinesItr.next();
                if (line.startsWith("# ")) {
                    continue;
                }
                Matcher versionLineMatcher = VERSION_PATTERN.matcher(line);
                if (versionLineMatcher.find()) {
                    String version = versionLineMatcher.group(1);
                    decoder = DECODER_FACTORY.getDecoder(Integer.parseInt(version), name, path);
                    break;
                }
            }
            if (decoder == null) {
                throw new IOException("Failed to find required version number line.");
            }
            while (tileLinesItr.hasNext()) {
                i++;
                String line = tileLinesItr.next();
                decoder.decodeLine(DataType.Tiles, line, i);
            }

            List<Callable<Void>> loadingTasks = new ArrayList<>();
            loadingTasks.add(new LoadMapDataCallable(DataType.Items, decoder, itemLoadFuture.get()));
            loadingTasks.add(new LoadMapDataCallable(DataType.WarpPoints, decoder, warpLoadFuture.get()));
            loadingTasks.add(new LoadMapDataCallable(DataType.Annotations, decoder, annoLoadFuture.get()));
            EXECUTOR_SERVICE.invokeAll(loadingTasks);
            Map m = decoder.getDecodedMap();

            if (m == null) {
                throw new IOException("No map was created by the decoder.");
            }

            LOGGER.debug("W={}; H={}; X={}; Y={}; L={};", m.getWidth(), m.getHeight(), m.getX(), m.getY(), m.getZ());

            return m;
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("Error while loading map.", e);
        }
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
    public static void saveMap(
            @Nonnull final Map map, @Nonnull final String name, @Nonnull final Path path) throws IOException {
        final Path tileFile = path.resolve(name + EXT_TILE);
        final Path itemFile = path.resolve(name + EXT_ITEM);
        final Path warpFile = path.resolve(name + EXT_WARP);
        final Path annoFile = path.resolve(name + EXT_ANNO);

        try (BufferedWriter tileOutput = Files.newBufferedWriter(tileFile, CHARSET, WRITE)) {
            try (BufferedWriter itemOutput = Files.newBufferedWriter(itemFile, CHARSET, WRITE)) {
                try (BufferedWriter warpOutput = Files.newBufferedWriter(warpFile, CHARSET, WRITE)) {
                    try (BufferedWriter annoOutput = Files.newBufferedWriter(annoFile, CHARSET, WRITE)) {
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
                                            writeLine(annoOutput, String.format("%d;%d;%d;%s", x, y, i + 1,
                                                                                items.get(i).getAnnotation()));
                                        }
                                    }
                                }
                                final MapWarpPoint warp = tile.getMapWarpPoint();
                                if (warp != null) {
                                    writeLine(warpOutput, String.format("%d;%d;%s", x, y, warp));
                                }
                            }
                        }
                        tileOutput.flush();
                        itemOutput.flush();
                        warpOutput.flush();
                        annoOutput.flush();
                    }
                }
            }
        }
    }

    private static boolean checkFile(@Nonnull final Path file) {
        if (Files.isDirectory(file)) {
            return false;
        }
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                return false;
            }
            return Files.exists(file);
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
