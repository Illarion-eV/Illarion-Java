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
package illarion.mapedit.data.formats;

import illarion.common.graphics.TileInfo;
import illarion.mapedit.crash.exceptions.FormatCorruptedException;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapItem;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.data.MapWarpPoint;

import javax.annotation.Nullable;
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

    private int level = -1;
    private int mapX = -1;
    private int mapY = -1;
    private int width = -1;
    private int height = -1;

    @Nullable
    private Map map;

    private String name;
    private String path;

    @Override
    public void newMap(final String name, final String path) {
        this.name = name;
        this.path = path;
        map = null;
        level = -1;
        mapX = -1;
        mapY = -1;
        width = -1;
        height = -1;
    }

    @Override
    public void decodeItemLine(final String line, final int i) throws FormatCorruptedException {
        final List<String> matches = getItemMatches(line);
        if (matches.size() < 4) {
            throw new FormatCorruptedException(path + ".item.txt", line, i,
                    "<dx>;<dy>;<item ID>;<quality>[;<data value>[;...]]");
        }
        createNewItem(matches);
    }

    private List<String> getItemMatches(final String line) {
        final Matcher regexMatcher = PATTERN_DATA.matcher(line);
        final List<String> matches = new LinkedList<String>();
        while (regexMatcher.find()) {
            final String match = regexMatcher.group();
            if (!match.isEmpty()) {
                matches.add(match);
            }
        }
        return matches;
    }

    private void createNewItem(final List<String> matches) {
        final int itemX = Integer.parseInt(matches.get(0));
        final int itemY = Integer.parseInt(matches.get(1));
        final int itemId = Integer.parseInt(matches.get(2));
        final int itemQuality = Integer.parseInt(matches.get(3));

        final List<String> data = new ArrayList<String>();
        if (matches.size() > 4) {
            for (int index = 4; index < matches.size(); index++) {
                data.add(matches.get(index));
            }
        }

        final MapItem item = new MapItem(itemId, data, itemQuality);
        if (map != null) {
            map.addItemAt(itemX, itemY, item);
        }
    }


    @Override
    public void decodeTileLine(final String line, final int i) throws FormatCorruptedException {
//        <dx>;<dy>;<tileID>;<musicID>

        if (map == null) {
            decodeHeader(line);
            return;
        }
        final String[] sections = DELIMITER.split(line);
        if (sections.length != 4) {
            throw new FormatCorruptedException(path + ".tiles.txt", line, i,
                    "<dx>;<dy>;<tileID>;<musicID>");
        }
        final int tx = Integer.parseInt(sections[0]);
        final int ty = Integer.parseInt(sections[1]);
        final int tid = Integer.parseInt(sections[2]);
        final int tmid = Integer.parseInt(sections[3]);
        final MapTile tile;
        if (TileInfo.hasOverlay(tid)) {
            tile = MapTile.MapTileFactory.createNew(TileInfo.getBaseID(tid), TileInfo.getOverlayID(tid),
                    TileInfo.getShapeId(tid), tmid);
        } else {
            tile = MapTile.MapTileFactory.createNew(tid, 0, 0, tmid);
        }
        map.setTileAt(tx, ty, tile);
    }

    @Override
    public void decodeWarpLine(final String line, final int i) throws FormatCorruptedException {
        // <sx>;<sy>;<tx>;<ty>;<tz>
        final String[] sections = DELIMITER.split(line);
        if (sections.length != 5) {
            throw new FormatCorruptedException(path + ".warps.txt", line, i,
                    "<sx>;<sy>;<tx>;<ty>;<tz>");
        }
        final int sx = Integer.parseInt(sections[0]);
        final int sy = Integer.parseInt(sections[1]);
        final int tx = Integer.parseInt(sections[2]);
        final int ty = Integer.parseInt(sections[3]);
        final int tz = Integer.parseInt(sections[4]);
        final MapWarpPoint warp = new MapWarpPoint(tx, ty, tz);
        if (map != null) {
            map.setWarpAt(sx, sy, warp);
        }
    }

    @Nullable
    @Override
    public Map getDecodedMap() {
        return map;
    }

    private void decodeHeader(final String line) {
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
        if ((level != -1) && (mapX != -1) && (mapY != -1) && (width != -1) && (height != -1)) {
            map = new Map(name, path, width, height, mapX, mapY, level);
        }
    }
}
