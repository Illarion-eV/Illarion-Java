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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This object represents a tile with a coordinate, an tileId and a music tileId.
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
            return new MapTile(old.tileId, old.overlayID, old.shapeID, musicID, old.mapItems, old.mapWarpPoint);
        }

        @Nonnull
        public static MapTile setId(final int id, @Nonnull final MapTile old) {
            final int baseId = (TileInfo.hasOverlay(id)) ? id : TileInfo.getBaseID(id);
            final int overlayId = (TileInfo.hasOverlay(id)) ? 0 : TileInfo.getOverlayID(id);
            final int shapeId = TileInfo.getShapeId(id);
            final MapTile tile = new MapTile(baseId, overlayId, shapeId, old.musicID, old.mapItems, old.mapWarpPoint);
            tile.setAnnotation(old.getAnnotation());
            return tile;
        }

        @Nonnull
        public static MapTile setOverlay(final int overlayID, final int shapeID, @Nonnull final MapTile old) {
            return new MapTile(old.tileId, overlayID, shapeID, old.musicID, old.mapItems, old.mapWarpPoint);
        }

        @Nonnull
        public static MapTile setOverlay(final int baseID, final int overlayID, final int shapeID, @Nonnull final MapTile old) {
            return new MapTile(baseID, overlayID, shapeID, old.musicID, old.mapItems, old.mapWarpPoint);
        }

        @Nonnull
        public static MapTile createNew(final int id, final int overlayID, final int shapeID, final int musicID) {
            return new MapTile(id, overlayID, shapeID, musicID, null, null);
        }

        @Nonnull
        public static MapTile copy(@Nonnull final MapTile old) {
            final List<MapItem> items = new FastList<MapItem>();
            if (old.mapItems != null) {
                for (final MapItem item : old.mapItems) {
                    items.add(new MapItem(item.getId(), new ArrayList<String>(), MapItem.QUALITY_NONE));
                }
            }
            return new MapTile(old.tileId, old.overlayID, old.shapeID, old.musicID, items, old.mapWarpPoint);
        }

        @Nonnull
        public static MapTile copyAll(@Nonnull final MapTile old) {
            final List<MapItem> items = new FastList<MapItem>();
            if (old.mapItems != null) {
                for (final MapItem item : old.mapItems) {
                    List<String> itemData = null;
                    if (item.getItemData() != null) {
                        itemData = item.getItemData();
                    }
                    final MapItem newItem = new MapItem(item.getId(), itemData, item.getQuality());
                    newItem.setAnnotation(item.getAnnotation());
                    items.add(newItem);
                }
            }
            return new MapTile(old.tileId, old.overlayID, old.shapeID, old.musicID, items, old.mapWarpPoint);
        }
    }

    /**
     * The tile tileId.
     */
    private final int tileId;
    /**
     * The ID of the overlay.
     */
    private final int overlayID;
    /**
     * The tileId of the shape
     */
    private final int shapeID;
    /**
     * The music tileId.
     */
    private final int musicID;
    /**
     * The items on top of this tile
     */
    @Nullable
    private List<MapItem> mapItems;
    private String annotation;
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
        tileId = baseId;
        this.overlayID = overlayID;
        this.shapeID = shapeID;
        this.musicID = musicID;
        this.mapWarpPoint = mapWarpPoint;
        if (mapItems != null) {
            this.mapItems = new FastList<MapItem>();
            this.mapItems.addAll(mapItems);
        }
    }

    public String getAnnotation() {
        return annotation;
    }

    /**
     * Returns the tile tileId.
     *
     * @return tileId
     */
    public int getId() {
        return tileId;
    }

    @Nullable
    public MapItem getMapItemAt(final int index) {
        if (mapItems == null) {
            return null;
        }
        return mapItems.get(index);
    }

    /**
     * Returns the music tileId.
     *
     * @return music tileId
     */
    public int getMusicID() {
        return musicID;
    }

    /**
     * @return The list of items on this tile.
     */
    @Nullable
    public List<MapItem> getMapItems() {
        return mapItems;
    }

    public void addMapItem(final MapItem item) {
        if (mapItems == null) {
            mapItems = new FastList<MapItem>();
        }
        mapItems.add(item);
    }

    public void removeMapItem(final MapItem item) {
        if (mapItems != null) {
            mapItems.add(item);
        }
    }

    public void removeMapItem(final int index) {
        if (mapItems != null) {
            mapItems.remove(index);
        }
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

    public boolean hasAnnotation() {
        return (annotation != null) && !annotation.isEmpty();
    }

    public boolean hasItemAnnotation() {
        boolean hasAnnotation = false;
        if ((mapItems != null) && !mapItems.isEmpty()) {
            int index = 0;
            while (!hasAnnotation && (mapItems.size() > index)) {
                hasAnnotation = mapItems.get(index).hasAnnotation();
                index++;
            }
        }
        return hasAnnotation;
    }

    public boolean isMapItemsDataEmpty() {
        if (mapItems == null) {
            return true;
        }
        boolean empty = true;
        int index = 0;

        while (empty && (index < mapItems.size())) {
            empty = mapItems.get(index).isItemDataNullOrEmpty();
            index++;
        }
        return empty;
    }

    public void setAnnotation(@Nullable final String annotation) {
        this.annotation = annotation;
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
            builder.append(tileId);
        } else {
            builder.append(Overlay.generateTileId(tileId, overlayID, shapeID));
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
