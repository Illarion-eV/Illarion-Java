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

import illarion.common.util.Location;

/**
 * Encapsulates all position and size data of a map, and helps with converting from global coordinate system to the
 * local one.
 *
 * @author Tim
 */
public class MapDimensions {
    /**
     * The x position of the map.
     */
    private int x;
    /**
     * The y position of the map.
     */
    private int y;
    /**
     * The level (z position) of the map.
     */
    private int l;
    /**
     * The width of the map.
     */
    private int w;
    /**
     * The height of the map.
     */
    private int h;

    /**
     * Generates a new instance from all given values.
     *
     * @param x The x position of the map.
     * @param y The y position of the map.
     * @param l The level (z position) of the map.
     * @param w The width of the map.
     * @param h The height of the map.
     */
    public MapDimensions(final int x, final int y, final int l, final int w, final int h) {
        this.x = x;
        this.y = y;
        this.l = l;
        this.w = w;
        this.h = h;
    }

    /**
     * Copies the old MapDimension instance.
     *
     * @param old
     */
    public MapDimensions(final MapDimensions old) {
        this.x = old.x;
        this.y = old.y;
        this.l = old.l;
        this.w = old.w;
        this.h = old.h;
    }

    /**
     * Creates a new instance with all values 0.
     */
    public MapDimensions() {

    }

    /**
     * Returns the x position of the map.
     *
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x position of the map.
     *
     * @param x
     */
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * Returns the y position of the map.
     *
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y position of the map.
     *
     * @param y
     */
    public void setY(final int y) {
        this.y = y;
    }

    /**
     * Returns the level (z coordinate) of the map.
     *
     * @return
     */
    public int getL() {
        return l;
    }

    /**
     * Sets the level (z coordinate) of the map
     *
     * @param l
     */
    public void setL(final int l) {
        this.l = l;
    }

    /**
     * Returns the width of the  Map.
     *
     * @return
     */
    public int getW() {
        return w;
    }

    /**
     * Sets the width of the map.
     *
     * @param w
     */
    public void setW(final int w) {
        this.w = w;
    }

    /**
     * Returns the height of the map.
     *
     * @return
     */
    public int getH() {
        return h;
    }

    /**
     * Sets the height of the map.
     *
     * @param h
     */
    public void setH(final int h) {
        this.h = h;
    }

    /**
     * Returns {@code true} if the given MapDimension is equal.
     *
     * @param o the given MapDimension.
     * @return
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapDimensions)) {
            return false;
        }

        MapDimensions that = (MapDimensions) o;
        if (h != that.h) {
            return false;
        }
        if (l != that.l) {
            return false;
        }
        if (w != that.w) {
            return false;
        }
        if (x != that.x) {
            return false;
        }
        if (y != that.y) {
            return false;
        }
        return true;
    }

    /**
     * Converts a map coordinate into a world coordinate.
     *
     * @param mapLocation the map coordinate
     * @return the world coordinate
     */
    public Location getAsWorldLocation(Location mapLocation) {
        if (mapLocation.getScZ() != l) throw new IllegalArgumentException("Can't calculate the world location if the " +
                "y value is not equal to the level of the map.");
        return new Location(x + mapLocation.getScX(), y + mapLocation.getScY(), l);
    }

    /**
     * Converts a world coordinate into a map coordinate.
     *
     * @param worldLocation the world coordinate
     * @return the map coordinate
     */
    public Location getAsMapLocation(Location worldLocation) {
        if (worldLocation.getScZ() != l) throw new IllegalArgumentException("Can't calculate the map location if the " +
                "y value is not equal to the level of the map.");

        return new Location(worldLocation.getScX() - x, worldLocation.getScY() - y, l);
    }
}
