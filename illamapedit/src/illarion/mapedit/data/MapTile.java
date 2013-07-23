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

import illarion.common.graphics.TileInfo;
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
            final int baseId = (TileInfo.hasOverlay(id)) ? id : TileInfo.getBaseID(id);
            final int overlayId = (TileInfo.hasOverlay(id)) ? 0 : TileInfo.getOverlayID(id);
            final int shapeId = TileInfo.getShapeId(id);
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

        @Nonnull
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
    /**
     * If the tile is selected
     */
    private boolean selected;

    public MapTile(final int baseId, final int overlayID, final int shapeID, final int musicID,
                   @Nullable final Collection<MapItem> mapItems, @Nullable final MapWarpPoint mapWarpPoint) {
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
     * Returns the tile id.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    public MapItem getMapItemAt(final int index) {
        return mapItems.get(index);
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

    public boolean isMapItemsDataEmpty() {
        boolean empty = true;
        int index = 0;

        while (empty && index < mapItems.size()) {
            empty = mapItems.get(index).getItemData().isEmpty();
            index++;
        }
        return empty;
    }

    public void removeMapItem(final int index) {
        mapItems.remove(index);
    }

    /**
     * Sets the warp point of this tile.
     *
     * @param mapWarpPoint the new warp, may be {@code null}.
     */
    public void setMapWarpPoint(@Nullable final MapWarpPoint mapWarpPoint) {
        this.mapWarpPoint = mapWarpPoint;
    }

    /**
     * Check if the tile is selected
     * @return True if the tile is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set the selected state if the tile
     * @param selected true if tile is selected otherwise false.
     */
    public void setSelected(final boolean selected) {
        this.selected = selected;
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
