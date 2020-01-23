/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.mapedit.data;

import illarion.common.graphics.TileInfo;
import illarion.mapedit.resource.Overlay;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This object represents a tile with a coordinate, an tileId and a music tileId.
 *
 * @author Tim
 */
public class MapTile {

    public static final class MapTileFactory {

        private MapTileFactory() {
            //NOTHING TO DO
        }

        @Nonnull
        public static MapTile setMusicId(int musicID, @Nonnull MapTile old) {
            return new MapTile(old.tileId, old.overlayID, old.shapeID, musicID, old.mapItems, old.mapWarpPoint);
        }

        @Nonnull
        public static MapTile setId(int id, @Nonnull MapTile old) {
            int baseId = (TileInfo.hasOverlay(id)) ? id : TileInfo.getBaseID(id);
            int overlayId = (TileInfo.hasOverlay(id)) ? 0 : TileInfo.getOverlayID(id);
            int shapeId = TileInfo.getShapeId(id);
            MapTile tile = new MapTile(baseId, overlayId, shapeId, old.musicID, old.mapItems, old.mapWarpPoint);
            tile.setAnnotation(old.getAnnotation());
            return tile;
        }

        @Nonnull
        public static MapTile setOverlay(int overlayID, int shapeID, @Nonnull MapTile old) {
            return new MapTile(old.tileId, overlayID, shapeID, old.musicID, old.mapItems, old.mapWarpPoint);
        }

        @Nonnull
        public static MapTile setOverlay(
                int baseID, int overlayID, int shapeID, @Nonnull MapTile old) {
            return new MapTile(baseID, overlayID, shapeID, old.musicID, old.mapItems, old.mapWarpPoint);
        }

        @Nonnull
        public static MapTile createNew(int id, int overlayID, int shapeID, int musicID) {
            return new MapTile(id, overlayID, shapeID, musicID, null, null);
        }

        @Nonnull
        public static MapTile copy(@Nonnull MapTile old) {
            return new MapTile(old);
        }

        @Nonnull
        public static MapTile copyAll(@Nonnull MapTile old) {
            List<MapItem> items = new ArrayList<>();
            if (old.mapItems != null) {
                for (MapItem item : old.mapItems) {
                    List<String> itemData = null;
                    if (item.getItemData() != null) {
                        itemData = item.getItemData();
                    }
                    MapItem newItem = new MapItem(item.getId(), itemData, item.getQualityDurability());
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
    @Nullable
    private String annotation;
    /**
     * The warp point on this tile, may be {@code null}.
     */
    @Nullable
    private MapWarpPoint mapWarpPoint;

    public MapTile(
            int baseId,
            int overlayID,
            int shapeID,
            int musicID,
            @Nullable Collection<MapItem> mapItems,
            @Nullable MapWarpPoint mapWarpPoint) {
        tileId = baseId;
        this.overlayID = overlayID;
        this.shapeID = shapeID;
        this.musicID = musicID;
        this.mapWarpPoint = mapWarpPoint;
        if (mapItems != null) {
            this.mapItems = new ArrayList<>();
            this.mapItems.addAll(mapItems);
        }
    }

    public MapTile(@Nonnull MapTile org) {
        tileId = org.tileId;
        overlayID = org.overlayID;
        shapeID = org.shapeID;
        musicID = org.musicID;
        mapWarpPoint = org.mapWarpPoint;
        if (org.mapItems != null) {
            mapItems = new ArrayList<>();
            mapItems.addAll(org.mapItems.stream().map(MapItem::new).collect(Collectors.toList()));
        }
    }

    @Nullable
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
    public MapItem getMapItemAt(int index) {
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

    public void addMapItem(MapItem item) {
        if (mapItems == null) {
            mapItems = new ArrayList<>();
        }
        mapItems.add(item);
    }

    public void removeMapItem(MapItem item) {
        if (mapItems != null) {
            mapItems.remove(item);
        }
    }

    public void removeMapItem(int index) {
        if (mapItems != null) {
            mapItems.remove(index);
        }
    }

    /**
     * @return The warp point on this tile, may be {@code null}.
     */
    @Nullable
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

    public void setAnnotation(@Nullable String annotation) {
        this.annotation = annotation;
    }

    /**
     * Sets the warp point of this tile.
     *
     * @param mapWarpPoint the new warp, may be {@code null}.
     */
    public void setMapWarpPoint(@Nullable MapWarpPoint mapWarpPoint) {
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
        StringBuilder builder = new StringBuilder();

        if (shapeID == 0) {
            builder.append(tileId);
        } else {
            builder.append(Overlay.generateTileId(tileId, overlayID, shapeID));
        }
        builder.append(';');
        builder.append(musicID);

        return builder.toString();
    }
}
