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


/**
 * @author Tim
 */
public class Map {

    private final String name;
    private final String path;
    private final int w;
    private final int h;
    private final int x;
    private final int y;
    private final int z;

    /**
     * The tiles.
     */
    private final MapTile[] mapTileData;

    public Map(final String name, final String path, final int w, final int h,
               final int x, final int y, final int z) {
        this.name = name;
        this.path = path;
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
        this.z = z;
        mapTileData = new MapTile[w * h];
    }

    /**
     * Sets a tile at a specified position.
     *
     * @param mapTile the tile to add.
     */
    public void setTileAt(final int x, final int y, final MapTile mapTile) {
        final int i = (y * w) + x;
        mapTileData[i] = mapTile;
    }

    public void addItemAt(final int x, final int y, final MapItem mapItem) {
        final int i = (y * w) + x;
        mapTileData[i].getMapItems().add(mapItem);
    }

    public void setWarptAt(final int x, final int y, final MapWarpPoint warpPoint) {
        final int i = (y * w) + x;
        mapTileData[i].setMapWarpPoint(warpPoint);
    }

    /**
     * Return a tile at a specified position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the tile
     */
    public MapTile getTileAt(final int x, final int y) {
        final int i = (y * w) + x;
        if (mapTileData[i] != null) {
            return mapTileData[i];
        } else {
            setTileAt(x, y, new MapTile(0, 0));
            return getTileAt(x, y);
        }

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

    public int getZ() {
        return z;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public boolean contains(final int x, final int y) {
        return x >= 0 && y >= 0 && x < getW() && y < getH();
    }
}
