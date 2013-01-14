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

import illarion.mapedit.resource.Overlay;
import javolution.text.TextBuilder;
import javolution.util.FastList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * This object represents a tile with a coordinate, an id and a music id.
 *
 * @author Tim
 */
public class MapTile {

    public static class MapTileFactory {

        private MapTileFactory() {
            //NOTHING TO DO
        }

        @Nonnull
        public static MapTile setMusicId(final int musicID, @Nonnull final MapTile old) {
            return new MapTile(old.id, old.overlayID, old.shapeID, musicID, old.mapItems, old.mapWarpPoint);
        }

        @Nonnull
        public static MapTile setId(final int id, @Nonnull final MapTile old) {
            final int baseId = (Overlay.shapeID(id) == 0) ? id : Overlay.baseID(id);
            final int overlayId = (Overlay.shapeID(id) == 0) ? 0 : Overlay.overlayID(id);
            final int shapeId = Overlay.shapeID(id);
            return new MapTile(baseId, overlayId, shapeId, old.musicID, old.mapItems, old.mapWarpPoint);
        }

        @Nonnull
        public static MapTile setOverlay(final int overlayID, final int shapeID, @Nonnull final MapTile old) {
            return new MapTile(old.id, overlayID, shapeID, old.musicID, old.mapItems, old.mapWarpPoint);
        }

        @Nonnull
        public static MapTile setOverlay(final int baseID, final int overlayID, final int shapeID, @Nonnull MapTile old) {
            return new MapTile(baseID, overlayID, shapeID, old.musicID, old.mapItems, old.mapWarpPoint);
        }

        @Nullable
        public static MapTile createNew(final int id, final int overlayID, final int shapeID, final int musicID) {
            return new MapTile(id, overlayID, shapeID, musicID, null, null);
        }

        @Nonnull
        public static MapTile copy(@Nonnull final MapTile old) {
            return new MapTile(old.id, old.overlayID, old.shapeID, old.musicID, old.mapItems, old.mapWarpPoint);
        }
    }


    /**
     * The tile id.
     */
    private final int id;
    /**
     * The ID of the overlay.
     */
    private final int overlayID;
    /**
     * The id of the shape
     */
    private final int shapeID;
    /**
     * The music id.
     */
    private final int musicID;
    /**
     * The items on top of this tile
     */
    @Nonnull
    private final List<MapItem> mapItems;
    /**
     * The warp point on this tile, may be {@code null}.
     */
    private MapWarpPoint mapWarpPoint;


    public MapTile(final int baseId, final int overlayID, final int shapeID, final int musicID,
                   @Nullable final Collection<MapItem> mapItems, final MapWarpPoint mapWarpPoint) {
        id = baseId;
        this.overlayID = overlayID;
        this.shapeID = shapeID;
        this.musicID = musicID;
        this.mapWarpPoint = mapWarpPoint;
        this.mapItems = new FastList<MapItem>();
        if (mapItems != null) {
            this.mapItems.addAll(mapItems);
        }
    }

    /**
     * Creates a new tile with the coordinates, the id and the music id.
     *
     * @param id
     * @param musicID
     */
    /*public MapTile(final int id, final int overlayID, final int shapeID, final int musicID) {
        this(id, overlayID, shapeID, musicID, null, null);
    }*/

    /**
     * Creates a copy of the other tile.
     *
     * @param old
     */
    /*public MapTile(final MapTile old) {
        this(old.id, old.overlayID, old.shapeID, old.musicID, old.mapItems, old.mapWarpPoint);
    }*/
    /*public MapTile(final int id, final MapTile old) {
        this((Overlay.shapeID(id) == 0) ? id : Overlay.baseID(id),
                (Overlay.shapeID(id) == 0) ? 0 : Overlay.overlayID(id),
                Overlay.shapeID(id),
                old.musicID,
                old.mapItems,
                old.mapWarpPoint);
    }*/
    /*public MapTile(final int overlayID, final int shapeID, MapTile old) {
        this(old.id, overlayID, shapeID, old.musicID, old.mapItems, old.mapWarpPoint);
    }*/

    /*public MapTile(final int baseID, final int overlayID, final int shapeID, MapTile old) {
        this(baseID, overlayID, shapeID, old.musicID, old.mapItems, old.mapWarpPoint);
    }*/

    /**
     * Returns the tile id.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the music id.
     *
     * @return music id
     */
    public int getMusicID() {
        return musicID;
    }

    /**
     * @return The list of items on this tile.
     */
    @Nonnull
    public List<MapItem> getMapItems() {
        return mapItems;
    }

    /**
     * @return The warp point on this tile, may be {@code null}.
     */
    public MapWarpPoint getMapWarpPoint() {
        return mapWarpPoint;
    }

    public int getOverlayID() {
        return overlayID;
    }

    public int getShapeID() {
        return shapeID;
    }

    /**
     * Sets the warp point of this tile.
     *
     * @param mapWarpPoint the new warp, may be {@code null}.
     */
    public void setMapWarpPoint(final MapWarpPoint mapWarpPoint) {
        this.mapWarpPoint = mapWarpPoint;
    }

    /**
     * Serializes the current tile to a string in the following format: <br>
     * {@code <tileID>;<musicID>}
     *
     * @return tileID;musicID
     */
    @Nonnull
    @Override
    public String toString() {
        final TextBuilder builder = TextBuilder.newInstance();

        if (shapeID == 0) {
            builder.append(id);
        } else {
            builder.append(Overlay.generateTileId(id, overlayID, shapeID));
        }
        builder.append(';');
        builder.append(musicID);

        try {
            return builder.toString();
        } finally {
            TextBuilder.recycle(builder);
        }
    }
}
