/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
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
import java.io.Writer;
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

/**
 * This class takes care of loading and saving maps
 *
 * @author Tim
 */
public final class MapIO {
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(MapIO.class);
    @Nonnull
    private static final String HEADER_V = "V:";
    @Nonnull
    private static final String HEADER_L = "L:";
    @Nonnull
    private static final String HEADER_X = "X:";
    @Nonnull
    private static final String HEADER_Y = "Y:";
    @Nonnull
    private static final String HEADER_W = "W:";
    @Nonnull
    private static final String HEADER_H = "H:";
    @Nonnull
    public static final String EXT_WARP = ".warps.txt";
    @Nonnull
    public static final String EXT_ITEM = ".items.txt";
    @Nonnull
    public static final String EXT_TILE = ".tiles.txt";
    @Nonnull
    public static final String EXT_ANNO = ".annot.txt";
    private static final char NEWLINE = '\n';
    @Nonnull
    private static final Pattern VERSION_PATTERN = Pattern.compile("V: (\\d+)");
    @Nonnull
    private static final CopyrightHeader COPYRIGHT_HEADER = new CopyrightHeader(80, null, null, "# ", null);
    @Nonnull
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    @Nonnull
    private static final Charset CHARSET = Charset.forName("ISO-8859-1");
    @Nonnull
    private static final DecoderFactory DECODER_FACTORY = new DecoderFactory();

    private MapIO() {

    }

    /**
     * Loads a map from a specified file
     *
     * @param path the path
     * @param name the map name
     */
    public static void loadMap(Path path, String name) {
        new Thread(() -> {
            try {
                EventBus.publish(new MapLoadedEvent(loadMapThread(path, name)));
            } catch (FormatCorruptedException ex) {
                LOGGER.warn("Format wrong.", ex);
                EventBus.publish(new MapLoadErrorEvent(ex.getMessage()));
            } catch (IOException ex) {
                LOGGER.warn("Can't load map", ex);
                EventBus.publish(new MapLoadErrorEvent(Lang.getMsg("gui.error.LoadMap")));
            }
        }).start();
    }

    private static final class LoadFileCallable implements Callable<List<String>> {
        @Nonnull
        private final Path file;

        private LoadFileCallable(@Nonnull Path file) {
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
                @Nonnull DataType type, @Nonnull Decoder decoder, @Nonnull List<String> lines) {
            this.type = type;
            this.decoder = decoder;
            this.lines = lines;
        }

        @Nullable
        @Override
        public Void call() throws Exception {
            int size = lines.size();
            for (int i = 0; i < size; i++) {
                decoder.decodeLine(type, lines.get(i), i);
            }
            return null;
        }
    }

    @Nullable
    public static Map loadMapThread(@Nonnull Path path, @Nonnull String name) throws IOException {
        LOGGER.debug("Load map {} at {}", name, path);
        //        Open the streams for all 3 files, containing the map data
        Path tileFile = path.resolve(name + EXT_TILE);
        Path itemFile = path.resolve(name + EXT_ITEM);
        Path warpFile = path.resolve(name + EXT_WARP);
        Path annoFile = path.resolve(name + EXT_ANNO);

        Future<List<String>> tileLoadFuture = EXECUTOR_SERVICE.submit(new LoadFileCallable(tileFile));
        Future<List<String>> itemLoadFuture = EXECUTOR_SERVICE.submit(new LoadFileCallable(itemFile));
        Future<List<String>> warpLoadFuture = EXECUTOR_SERVICE.submit(new LoadFileCallable(warpFile));
        Future<List<String>> annoLoadFuture = EXECUTOR_SERVICE.submit(new LoadFileCallable(annoFile));

        try {
            List<String> tileLines = tileLoadFuture.get();
            Iterator<String> tileLinesItr = tileLines.iterator();
            Decoder decoder = null;
            int i = 0;
            while (tileLinesItr.hasNext()) {
                i++;
                String line = tileLinesItr.next();
                if ((line == null) || line.startsWith("#")) {
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
        } catch (@Nonnull InterruptedException | ExecutionException e) {
            throw new IOException("Error while loading map.", e);
        }
    }

    /**
     * Loads the map, with the map name and path, stored in the map object
     *
     * @param map the map to save
     * @throws IOException
     */
    public static void saveMap(@Nonnull Map map) throws IOException {
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
            @Nonnull Map map, @Nonnull String name, @Nonnull Path path) throws IOException {
        Path tileFile = path.resolve(name + EXT_TILE);
        Path itemFile = path.resolve(name + EXT_ITEM);
        Path warpFile = path.resolve(name + EXT_WARP);
        Path annoFile = path.resolve(name + EXT_ANNO);

        try (BufferedWriter tileOutput = Files.newBufferedWriter(tileFile, CHARSET)) {
            try (BufferedWriter itemOutput = Files.newBufferedWriter(itemFile, CHARSET)) {
                try (BufferedWriter warpOutput = Files.newBufferedWriter(warpFile, CHARSET)) {
                    try (BufferedWriter annoOutput = Files.newBufferedWriter(annoFile, CHARSET)) {
                        COPYRIGHT_HEADER.writeTo(tileOutput);
                        COPYRIGHT_HEADER.writeTo(itemOutput);
                        COPYRIGHT_HEADER.writeTo(warpOutput);
                        COPYRIGHT_HEADER.writeTo(annoOutput);

                        writeHeader(tileOutput, HEADER_V, 2);
                        writeHeader(tileOutput, HEADER_L, map.getZ());
                        writeHeader(tileOutput, HEADER_X, map.getX());
                        writeHeader(tileOutput, HEADER_Y, map.getY());
                        writeHeader(tileOutput, HEADER_W, map.getWidth());
                        writeHeader(tileOutput, HEADER_H, map.getHeight());

                        MapIterator itr = map.iterator();
                        while (itr.hasNext()) {
                            MapTile tile = itr.next();
                            int x = itr.getCurrentX();
                            int y = itr.getCurrentY();

                            //        <dx>;<dy>;<tileID>;<musicID>
                            writeLine(tileOutput, String.format("%d;%d;%s", x, y, tile));
                            if (tile.hasAnnotation()) {
                                writeLine(annoOutput, String.format("%d;%d;0;%s", x, y, tile.getAnnotation()));
                            }

                            List<MapItem> items = tile.getMapItems();
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
                            MapWarpPoint warp = tile.getMapWarpPoint();
                            if (warp != null) {
                                writeLine(warpOutput, String.format("%d;%d;%s", x, y, warp));
                            }
                        }
                    }
                }
            }
        }
    }

    private static void writeHeader(@Nonnull Writer writer, @Nonnull String header, int value) throws IOException {
        writer.write(header);
        writer.write(' ');
        writer.write(Integer.toString(value));
        writer.write(NEWLINE);
    }

    private static void writeLine(@Nonnull BufferedWriter writer, @Nonnull Object... args) throws IOException {
        for (int i = 0; i < args.length; ++i) {
            writer.write(args[i].toString());
            if (i < (args.length - 1)) {

                writer.write(';');
            } else {
                writer.write(NEWLINE);
            }
        }
    }

    private static void writeLine(@Nonnull BufferedWriter writer, @Nonnull String str) throws IOException {
        writer.write(str);
        writer.write(NEWLINE);
    }
}
