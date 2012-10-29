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

import illarion.mapedit.crash.exceptions.FormatCorruptedException;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapItem;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.data.MapWarpPoint;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tim
 */
public class Version1Decoder implements Decoder {
    private static final Logger LOGGER = Logger.getLogger(Version1Decoder.class);
    private static final Pattern PATTERN_L = Pattern.compile("L: (-?\\d+)");
    private static final Pattern PATTERN_X = Pattern.compile("X: (-?\\d+)");
    private static final Pattern PATTERN_Y = Pattern.compile("Y: (-?\\d+)");
    private static final Pattern PATTERN_W = Pattern.compile("W: (-?\\d+)");
    private static final Pattern PATTERN_H = Pattern.compile("H: (-?\\d+)");
    private static final Pattern DELIMITER = Pattern.compile(";");

    private int l = -1;
    private int x = -1;
    private int y = -1;
    private int w = -1;
    private int h = -1;

    private Map map;

    private String name;
    private String path;

    @Override
    public void newMap(final String name, final String path) {
        LOGGER.debug("Load new map: " + path);
        this.name = name;
        this.path = path;
        map = null;
        l = -1;
        x = -1;
        y = -1;
        w = -1;
        h = -1;
    }


    @Override
    public void decodeItemLine(final String line, final int i) throws FormatCorruptedException {

        final String[] sections = DELIMITER.split(line);
        if ((sections.length != 5) && (sections.length != 6)) {
            throw new FormatCorruptedException("(Items) Argument length is lower than 5 or bigger than 6", path,
                    line);
        }
        final int ix = Integer.parseInt(sections[0]);
        final int iy = Integer.parseInt(sections[1]);
        final int iid = Integer.parseInt(sections[3]);
        final int idata = Integer.parseInt(sections[4]);
        final int iquality = (sections.length == 6) ? Integer.parseInt(sections[5]) : 0;
        String s = null;
        //TODO: Remove this from here
        if (idata != 0) {
            s = JOptionPane.showInputDialog("Itemdata '" + idata + "' at " + ix + "," + iy + " isn't compatible with the " +
                    "actual " +
                    "fileversion" +
                    "\n enter data in the format key1=value1;key2=value2");
        }
        final MapItem item = new MapItem(iid, (s == null) ? "" : s, iquality);
        map.addItemAt(ix, iy, item);
    }

    @Override
    public void decodeTileLine(final String line, final int i) throws FormatCorruptedException {
        if (map == null) {
            decodeHeader(line);
            return;
        }

        final String[] sections = DELIMITER.split(line);
        if (sections.length != 5) {
            throw new FormatCorruptedException("", path, line);
        }
        final int tx = Integer.parseInt(sections[0]);
        final int ty = Integer.parseInt(sections[1]);
        final int tid = Integer.parseInt(sections[2]);
        final int tmid = Integer.parseInt(sections[3]);
        final MapTile tile = new MapTile(tid, tmid);
        map.setTileAt(tx, ty, tile);
    }

    private void decodeHeader(final String line) {
        Matcher m = PATTERN_L.matcher(line);
        if (m.find()) {

            l = Integer.parseInt(m.group(1));
        }
        m = PATTERN_X.matcher(line);
        if (m.find()) {

            x = Integer.parseInt(m.group(1));
        }
        m = PATTERN_Y.matcher(line);
        if (m.find()) {

            y = Integer.parseInt(m.group(1));
        }
        m = PATTERN_W.matcher(line);
        if (m.find()) {

            w = Integer.parseInt(m.group(1));
        }
        m = PATTERN_H.matcher(line);
        if (m.find()) {

            h = Integer.parseInt(m.group(1));
        }
        if ((l != -1) && (x != -1) && (y != -1) && (w != -1) && (h != -1)) {
            map = new Map(name, path, w, h, x, y, l);
        }
    }

    @Override
    public void decodeWarpLine(final String line, final int i) throws FormatCorruptedException {
        final String[] sections = DELIMITER.split(line);
        if (sections.length != 5) {
            throw new FormatCorruptedException("(Warps) Argument length is too height: " + sections.length, path,
                    line);
        }
        final int sx = Integer.parseInt(sections[0]);
        final int sy = Integer.parseInt(sections[1]);
        final int tx = Integer.parseInt(sections[2]);
        final int ty = Integer.parseInt(sections[3]);
        final int tz = Integer.parseInt(sections[4]);
        final MapWarpPoint warp = new MapWarpPoint(tx, ty, tz);
        map.setWarpAt(sx, sy, warp);
    }

    @Override
    public Map getDecodedMap() {
        return map;
    }
}
