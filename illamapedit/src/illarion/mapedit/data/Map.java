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

import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class represents a whole map, including name, path, dimensions, and data.
 *
 * @author Tim
 */
public class Map {
    /**
     * The map name
     */
    private final String name;
    /**
     * The path to save this map
     */
    private final String path;
    /**
     * The width of the map
     */
    private final int width;
    /**
     * The height of the map
     */
    private final int height;
    /**
     * The x coordinate of the origin of the map
     */
    private final int x;
    /**
     * The y coordinate
     */
    private final int y;
    /**
     * The map level
     */
    private final int z;

    /**
     * The tiles.
     */
    @Nonnull
    private final MapTile[] mapTileData;

    /**
     * Creates a new map
     *
     * @param name the map name
     * @param path the map path
     * @param w    the with of the map
     * @param h    the height of the map
     * @param x    the x coordinate of the origin of the map
     * @param y    the y coordinate of the origin of the map
     * @param z    the map level (= z coordinate)
     */
    public Map(final String name, final String path, final int w, final int h,
               final int x, final int y, final int z) {
        this.name = name;
        this.path = path;
        width = w;
        height = h;
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
        final int i = (y * width) + x;
        mapTileData[i] = mapTile;
    }

    public void setTileAt(@Nonnull final Location loc, final MapTile mapTile) {
        setTileAt(loc.getScX(), loc.getScY(), mapTile);
    }

    /**
     * Adds an item to a specified position.
     *
     * @param x       the x coordinate relative to the origin
     * @param y       the y coordinate relative to the origin
     * @param mapItem the item  <- u don't sayy ;)
     */
    public void addItemAt(final int x, final int y, final MapItem mapItem) {
        final int i = (y * width) + x;
        mapTileData[i].getMapItems().add(mapItem);
    }

    /**
     * Sets a warp point at to specified tile
     *
     * @param x         the x coordinate of the warp point
     * @param y         the y coordinate of the warp point
     * @param warpPoint the warp point <- u don't sayy ;)
     */
    public void setWarpAt(final int x, final int y, final MapWarpPoint warpPoint) {
        final int i = (y * width) + x;
        mapTileData[i].setMapWarpPoint(warpPoint);
    }

    /**
     * Return a tile at a specified position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the tile
     */
    @Nullable
    public MapTile getTileAt(final int x, final int y) {
        if (!contains(x, y)) {
            return null;
        }
        final int i = (y * width) + x;
        if (mapTileData[i] != null) {
            return mapTileData[i];
        }
        setTileAt(x, y, MapTile.MapTileFactory.createNew(0, 0, 0, 0));
        return getTileAt(x, y);

    }

    @Nullable
    public MapTile getTileAt(@Nonnull final Location loc) {
        return getTileAt(loc.getScX(), loc.getScY());
    }

    /**
     * @return the map width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the map height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the x coordinate of the origin in global coordinates
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y coordinate of the origin in gobal coordinates
     */
    public int getY() {
        return y;
    }

    /**
     * @return the map level
     */
    public int getZ() {
        return z;
    }

    /**
     * @return the path to save the map
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the map name
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the map contains the following coordinates
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return {@code true} if the map contains x and y
     */
    public boolean contains(final int x, final int y) {
        return (x >= 0) && (y >= 0) && (x < getWidth()) && (y < getHeight());
    }

    @Override
    public boolean equals(@Nonnull final Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Map map = (Map) obj;
        return name.equals(map.name) && path.equals(map.path);
    }


}
