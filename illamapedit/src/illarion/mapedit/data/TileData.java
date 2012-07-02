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
 * @author Tim
 */
public class TileData {



    private final int w, h, x, y, l;
    private final Tile[][] tileData;

    public TileData(final int l, final int x, final int y, final int w, final int h) {
        this.l = l;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        tileData = new Tile[w][h];
    }

    public TileData(final int w, final int h, TileData old) {
        this.w = w;
        this.h = h;
        this.l = old.l;
        this.x = old.x;
        this.y = old.y;

        tileData = new Tile[w][h];
        int minWidth = (w < old.tileData.length) ? w : old.tileData.length;
        int minHeight = (h < old.tileData[0].length) ? h : old.tileData[0].length;

        for (int x = 0; x < minWidth; ++x) {
            for (int y = 0; y < minHeight; ++y) {
                tileData[x][y] = old.tileData[x][y];
            }
        }
    }

    public void setTileAt(final Tile tile, final int x, final int y) {
        tileData[x][y] = tile;
    }

    public Tile getTileAt(final int x, final int y) {
        return tileData[x][y];
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getL() {
        return l;
    }

    public static TileData fromInputStream(final InputStream is) throws IOException {
        Scanner scanner = new Scanner(is);
        int l = Integer.parseInt(scanner.nextLine().substring(3));
        int x = Integer.parseInt(scanner.nextLine().substring(3));
        int y = Integer.parseInt(scanner.nextLine().substring(3));
        int w = Integer.parseInt(scanner.nextLine().substring(3));
        int h = Integer.parseInt(scanner.nextLine().substring(3));
        TileData data = new TileData(l,x,y,w,h);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Tile t = Tile.fromString(line);
            data.tileData[t.getX()][t.getY()] = t;
        }

        return data;
    }

    public void saveToFile(final File file) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        TextBuilder builder = TextBuilder.newInstance();
        builder.append(Map.HEADER_LEVEL).append(l).append(Map.NL);
        builder.append(Map.HEADER_X).append(x).append(Map.NL);
        builder.append(Map.HEADER_Y).append(y).append(Map.NL);
        builder.append(Map.HEADER_WIDTH).append(w).append(Map.NL);
        builder.append(Map.HEADER_HEIGHT).append(h).append(Map.NL);

        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                builder.append(x).append(Map.DM);
                builder.append(y).append(Map.DM);
                builder.append(tileData[x][y].getId()).append(Map.DM);
                builder.append(tileData[x][y].getMusicID()).append(Map.DM);
                builder.append(0).append(Map.NL);
            }
        }
        writer.write(builder.toString());
        writer.close();
        TextBuilder.recycle(builder);

    }
}
