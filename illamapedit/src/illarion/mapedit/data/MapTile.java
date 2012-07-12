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

import javolution.util.FastList;

import java.util.List;

/**
 * This object represents a tile with a coordinate, an id and a music id.
 *
 * @author Tim
 */
public class MapTile {
    /**
     * The tile id.
     */
    private final int id;
    /**
     * The music id.
     */
    private final int musicID;
    /**
     * The items on top of this tile
     */
    private final List<MapItem> mapItems;
    /**
     * The warp point on this tile, may be {@code null}.
     */
    private MapWarpPoint mapWarpPoint;

    /**
     * Creates a new tile with the coordinates, the id and the music id.
     *
     * @param id
     * @param musicID
     */
    public MapTile(final int id, final int musicID) {
        this.id = id;
        this.musicID = musicID;
        mapItems = new FastList<MapItem>();
        mapWarpPoint = null;
    }

    /**
     * Creates a copy of the other tile.
     *
     * @param old
     */
    public MapTile(final MapTile old) {
        id = old.id;
        musicID = old.musicID;
        mapItems = new FastList<MapItem>(old.mapItems);
        mapWarpPoint = old.mapWarpPoint;
    }

    /**
     * Returns the tile id.
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the music id.
     *
     * @return
     */
    public int getMusicID() {
        return musicID;
    }

    /**
     * @return The list of items on this tile.
     */
    public List<MapItem> getMapItems() {
        return mapItems;
    }

    /**
     * @return The warp point on this tile, may be {@code null}.
     */
    public MapWarpPoint getMapWarpPoint() {
        return mapWarpPoint;
    }

    /**
     * Sets the warp point of this tile.
     *
     * @param mapWarpPoint the new warp, may be {@code null}.
     */
    public void setMapWarpPoint(final MapWarpPoint mapWarpPoint) {
        this.mapWarpPoint = mapWarpPoint;
    }
}
