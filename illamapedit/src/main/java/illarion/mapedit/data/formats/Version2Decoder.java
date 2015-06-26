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
package illarion.mapedit.data.formats;

import illarion.common.graphics.TileInfo;
import illarion.mapedit.crash.exceptions.FormatCorruptedException;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapItem;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.data.MapTile.MapTileFactory;
import illarion.mapedit.data.MapWarpPoint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tim
 * @author Fredrik K
 */
public class Version2Decoder implements Decoder {
    private static final Pattern PATTERN_L = Pattern.compile("L: (-?\\d+)");
    private static final Pattern PATTERN_X = Pattern.compile("X: (-?\\d+)");
    private static final Pattern PATTERN_Y = Pattern.compile("Y: (-?\\d+)");
    private static final Pattern PATTERN_W = Pattern.compile("W: (-?\\d+)");
    private static final Pattern PATTERN_H = Pattern.compile("H: (-?\\d+)");
    private static final Pattern DELIMITER = Pattern.compile(";");
    private static final Pattern PATTERN_DATA = Pattern.compile("(?:\\\\.|[^;\\\\]++)*");

    @Nullable
    private Integer level;
    @Nullable
    private Integer mapX;
    @Nullable
    private Integer mapY;
    @Nullable
    private Integer width;
    @Nullable
    private Integer height;

    @Nullable
    private Map map;

    private final String name;
    private final Path path;

    Version2Decoder(@Nonnull String name, @Nonnull Path path) {
        this.name = name;
        this.path = path;
        map = null;
        level = null;
        mapX = null;
        mapY = null;
        width = null;
        height = null;
    }

    public void decodeItemLine(@Nonnull String line, int i) throws FormatCorruptedException {
        if (line.startsWith("# ")) {
            return;
        }
        List<String> matches = getItemMatches(line);
        if (matches.size() < 4) {
            throw new FormatCorruptedException(path + ".item.txt", line, i,
                                               "<dx>;<dy>;<item ID>;<quality>[;<data value>[;...]]");
        }
        createNewItem(matches);
    }

    @Nonnull
    private List<String> getItemMatches(String line) {
        Matcher regexMatcher = PATTERN_DATA.matcher(line);
        List<String> matches = new LinkedList<>();
        while (regexMatcher.find()) {
            String match = regexMatcher.group();
            if (!match.isEmpty()) {
                matches.add(match);
            }
        }
        return matches;
    }

    private void createNewItem(@Nonnull List<String> matches) {
        int itemX = Integer.parseInt(matches.get(0));
        int itemY = Integer.parseInt(matches.get(1));
        int itemId = Integer.parseInt(matches.get(2));
        int itemQuality = Integer.parseInt(matches.get(3));

        List<String> data = new ArrayList<>();
        if (matches.size() > 4) {
            for (int index = 4; index < matches.size(); index++) {
                data.add(matches.get(index));
            }
        }

        MapItem item = new MapItem(itemId, data, itemQuality);
        if (map != null) {
            map.addItemAt(itemX, itemY, item);
        }
    }

    public void decodeTileLine(@Nonnull String line, int i) throws FormatCorruptedException {
        if (line.startsWith("# ")) {
            return;
        }
        //        <dx>;<dy>;<tileID>;<musicID>

        if (map == null) {
            decodeHeader(line);
            return;
        }
        String[] sections = DELIMITER.split(line);
        if (sections.length != 4) {
            throw new FormatCorruptedException(path + ".tiles.txt", line, i, "<dx>;<dy>;<tileID>;<musicID>");
        }
        int tx = Integer.parseInt(sections[0]);
        int ty = Integer.parseInt(sections[1]);
        int tid = Integer.parseInt(sections[2]);
        int tmid = Integer.parseInt(sections[3]);
        final MapTile tile;
        if (TileInfo.hasOverlay(tid)) {
            tile = MapTileFactory
                    .createNew(TileInfo.getBaseID(tid), TileInfo.getOverlayID(tid), TileInfo.getShapeId(tid), tmid);
        } else {
            tile = MapTileFactory.createNew(tid, 0, 0, tmid);
        }
        map.setTileAt(tx, ty, tile);
    }

    public void decodeAnnoLine(String line, int i) throws FormatCorruptedException {
        if (line.startsWith("# ")) {
            return;
        }
        String[] sections = DELIMITER.split(line);
        if (sections.length != 4) {
            throw new FormatCorruptedException(path + ".annot.txt", line, i, "<sx>;<sy>;<type>;<annotation>");
        }
        int sx = Integer.parseInt(sections[0]);
        int sy = Integer.parseInt(sections[1]);
        int index = Integer.parseInt(sections[2]);
        String annotation = sections[3];

        if (map != null) {
            MapTile tile = map.getTileAt(sx, sy);
            if ((tile != null) && (index == 0)) {
                tile.setAnnotation(annotation);
            }
            if ((tile != null) && (index > 0)) {
                tile.getMapItems().get(index - 1).setAnnotation(annotation);
            }
        }
    }

    public void decodeWarpLine(@Nonnull String line, int i) throws FormatCorruptedException {
        if (line.startsWith("# ")) {
            return;
        }
        // <sx>;<sy>;<tx>;<ty>;<tz>
        String[] sections = DELIMITER.split(line);
        if (sections.length != 5) {
            throw new FormatCorruptedException(path + ".warps.txt", line, i, "<sx>;<sy>;<tx>;<ty>;<tz>");
        }
        int sx = Integer.parseInt(sections[0]);
        int sy = Integer.parseInt(sections[1]);
        int tx = Integer.parseInt(sections[2]);
        int ty = Integer.parseInt(sections[3]);
        int tz = Integer.parseInt(sections[4]);
        MapWarpPoint warp = new MapWarpPoint(tx, ty, tz);
        if (map != null) {
            map.setWarpAt(sx, sy, warp);
        }
    }

    @Override
    public void decodeLine(@Nonnull DataType type, String line, int i) throws FormatCorruptedException {
        switch (type) {
            case Tiles:
                decodeTileLine(line, i);
                break;
            case Items:
                decodeItemLine(line, i);
                break;
            case WarpPoints:
                decodeWarpLine(line, i);
                break;
            case Annotations:
                decodeAnnoLine(line, i);
                break;
        }
    }

    @Nullable
    @Override
    public Map getDecodedMap() {
        return map;
    }

    private void decodeHeader(String line) {
        Matcher matcher = PATTERN_L.matcher(line);
        if (matcher.find()) {
            level = Integer.parseInt(matcher.group(1));
        }
        matcher = PATTERN_X.matcher(line);
        if (matcher.find()) {
            mapX = Integer.parseInt(matcher.group(1));
        }
        matcher = PATTERN_Y.matcher(line);
        if (matcher.find()) {
            mapY = Integer.parseInt(matcher.group(1));
        }
        matcher = PATTERN_W.matcher(line);
        if (matcher.find()) {
            width = Integer.parseInt(matcher.group(1));
        }
        matcher = PATTERN_H.matcher(line);
        if (matcher.find()) {
            height = Integer.parseInt(matcher.group(1));
        }
        if ((level != null) && (mapX != null) && (mapY != null) && (width != null) && (height != null)) {
            map = new Map(name, path, width, height, mapX, mapY, level);
        }
    }
}
