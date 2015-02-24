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
package illarion.client.world;

import illarion.client.IllaClient;
import illarion.client.graphics.QuestMarker;
import illarion.client.graphics.QuestMarker.QuestMarkerAvailability;
import illarion.client.graphics.QuestMarker.QuestMarkerType;
import illarion.client.gui.MiniMapGui;
import illarion.client.gui.MiniMapGui.Pointer;
import illarion.client.net.server.TileUpdate;
import illarion.client.world.interactive.InteractiveMap;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.graphics.ItemInfo;
import illarion.common.types.Direction;
import illarion.common.types.Location;
import illarion.common.util.Stoppable;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicPatternSubscriber;
import org.illarion.engine.Engine;
import org.illarion.engine.EngineException;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.LightingMap;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This handler stores all map data and ensures the updates of the map. This
 * class is fully thread save for all actions. Clipping, hiding effects and map
 * optimization is done by the GameMapProcessor.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Andreas Grob &lt;vilarion@illarion.org&gt;
 */
@ThreadSafe
public final class GameMap implements LightingMap, Stoppable {
    private static final class QuestMarkerCarrier {
        @Nullable
        private final QuestMarker mapMarker;

        @Nullable
        private final Pointer guiMarker;

        private QuestMarkerCarrier(@Nullable QuestMarker mapMarker, @Nullable Pointer guiMarker) {
            this.mapMarker = mapMarker;
            this.guiMarker = guiMarker;
        }

        @Nullable
        private Pointer getGuiMarker() {
            return guiMarker;
        }

        @Nullable
        private QuestMarker getMapMarker() {
            return mapMarker;
        }

        void setActiveQuest(boolean activeQuest) {
            if (guiMarker != null) {
                guiMarker.setCurrentQuest(activeQuest);
            }
        }

        boolean isRemoved() {
            return (mapMarker == null) || mapMarker.isMarkedAsRemoved();
        }

        void removeMarker() {
            if (guiMarker != null) {
                World.getGameGui().getMiniMapGui().releasePointer(guiMarker);
            }
            if (mapMarker != null) {
                mapMarker.markAsRemoved();
            }
        }
    }

    /**
     * The lock that is hold in case the light is currently rendered. If that is done right now the map must not be
     * rendered.
     */
    @Nonnull
    public static final Object LIGHT_LOCK = new Object();

    /**
     * The interactive map that is used to handle the player interaction with the map.
     */
    @Nonnull
    private final InteractiveMap interactive;

    /**
     * The lock that secures the map tiles.
     */
    @Nonnull
    private final ReadWriteLock mapLock;

    /**
     * The handler for the overview map.
     */
    @Nonnull
    private final GameMiniMap miniMap;

    /**
     * The tiles of the map. The key of the hash map is the location key of the tiles location.
     */
    @Nonnull
    @GuardedBy("mapLock")
    private final Map<Long, MapTile> tiles;

    /**
     * This is the list of active quest markers that show where a quest starts.
     */
    @Nonnull
    private final Map<Location, QuestMarkerCarrier> activeQuestStartMarkers;

    /**
     * This is the list of active quest markers that show the target of a quest.
     */
    @Nonnull
    private final Map<Location, QuestMarkerCarrier> activeQuestTargetMarkers;

    /**
     * This is the list of quest markers that show the target of the quest, that are not visible on the map yet.
     */
    @Nonnull
    private final Map<Location, QuestMarkerCarrier> inactiveQuestTargetLocations;

    /**
     * This is set {@code true} in case the quest markers are supposed to be displayed on the mini map.
     */
    private boolean showQuestsOnMiniMap;

    /**
     * This is set {@code true} in case the quest markers are supposed to be displayed on the game map.
     */
    private boolean showQuestsOnGameMap;

    /**
     * Default constructor of the map handler.
     */
    public GameMap(@Nonnull Engine engine) throws EngineException {
        tiles = new HashMap<>();
        interactive = new InteractiveMap(this);

        activeQuestStartMarkers = new HashMap<>();
        activeQuestTargetMarkers = new HashMap<>();
        inactiveQuestTargetLocations = new HashMap<>();

        mapLock = new ReentrantReadWriteLock();

        miniMap = new GameMiniMap(engine);

        showQuestsOnMiniMap = IllaClient.getCfg().getBoolean("showQuestsOnMiniMap");
        showQuestsOnGameMap = IllaClient.getCfg().getBoolean("showQuestsOnGameMap");

        AnnotationProcessor.process(this);
    }

    @EventTopicPatternSubscriber(topicPattern = "showQuestsOn.+Map")
    public void onQuestMarkerSettingsChanged(@Nonnull String topic, @Nonnull ConfigChangedEvent event) {
        if ("showQuestsOnMiniMap".equals(topic)) {
            showQuestsOnMiniMap = event.getConfig().getBoolean("showQuestsOnMiniMap");
        } else if ("showQuestsOnGameMap".equals(topic)) {
            showQuestsOnGameMap = event.getConfig().getBoolean("showQuestsOnGameMap");
        }
    }

    public void applyQuestTargetLocations(@Nonnull Iterable<Location> targets) {
        //removeAllQuestMarkers();
        Collection<Location> oldMarkers = new HashSet<>(activeQuestTargetMarkers.keySet());
        oldMarkers.addAll(inactiveQuestTargetLocations.keySet());

        for (@Nonnull Location markerLocation : targets) {
            if (oldMarkers.contains(markerLocation)) {
                oldMarkers.remove(markerLocation);
            } else {
                MapTile tile = getMapAt(markerLocation);
                @Nullable Pointer targetPointer;
                if (showQuestsOnMiniMap) {
                    targetPointer = World.getGameGui().getMiniMapGui().createTargetPointer();
                    targetPointer.setTarget(markerLocation);
                    targetPointer.setCurrentQuest(true);
                    World.getGameGui().getMiniMapGui().addPointer(targetPointer);
                } else {
                    targetPointer = null;
                }
                if ((tile != null) && showQuestsOnGameMap) {
                    QuestMarker newMarker = new QuestMarker(QuestMarkerType.Target, tile);
                    newMarker.setAvailability(QuestMarkerAvailability.Available);
                    activeQuestTargetMarkers.put(markerLocation, new QuestMarkerCarrier(newMarker, targetPointer));
                    newMarker.show();
                } else if (targetPointer != null) {
                    inactiveQuestTargetLocations.put(markerLocation, new QuestMarkerCarrier(null, targetPointer));
                }
            }
        }
        for (@Nonnull Location markerLocation : oldMarkers) {

            QuestMarkerCarrier activeCarrier = activeQuestTargetMarkers.get(markerLocation);
            if (activeCarrier != null) {
                activeCarrier.setActiveQuest(false);
                //activeCarrier.removeMarker();
            }

            QuestMarkerCarrier inactiveCarrier = inactiveQuestTargetLocations.remove(markerLocation);
            if (inactiveCarrier != null) {
                inactiveCarrier.removeMarker();
            }
        }
    }

    public void removeQuestMarkers(@Nonnull Iterable<Location> targets) {
        Collection<Location> currentMarkers = new HashSet<>(activeQuestTargetMarkers.keySet());
        currentMarkers.addAll(inactiveQuestTargetLocations.keySet());
        currentMarkers.addAll(activeQuestStartMarkers.keySet());

        for (@Nonnull Location markerLocation : targets) {
            QuestMarkerCarrier activeStartCarrier = activeQuestStartMarkers.remove(markerLocation);
            if (activeStartCarrier != null) {
                activeStartCarrier.removeMarker();
            }

            QuestMarkerCarrier activeCarrier = activeQuestTargetMarkers.remove(markerLocation);
            if (activeCarrier != null) {
                activeCarrier.removeMarker();
            }

            QuestMarkerCarrier inactiveCarrier = inactiveQuestTargetLocations.remove(markerLocation);
            if (inactiveCarrier != null) {
                inactiveCarrier.removeMarker();
            }
        }
    }

    /**
     * Apply the new quest source locations.
     *
     * @param available the list of available quests
     * @param availableSoon the list of quests that will become available soon
     */
    public void applyQuestStartLocations(
            @Nonnull Iterable<Location> available, @Nonnull Iterable<Location> availableSoon) {
        Collection<Location> currentMarkers = new HashSet<>(activeQuestStartMarkers.keySet());

        for (int i = 0; i < 2; i++) {
            QuestMarkerAvailability availability;
            Iterable<Location> collection;
            switch (i) {
                case 0:
                    availability = QuestMarkerAvailability.Available;
                    collection = available;
                    break;
                case 1:
                    availability = QuestMarkerAvailability.AvailableSoon;
                    collection = availableSoon;
                    break;
                default:
                    continue;
            }
            for (@Nonnull Location markerLocation : collection) {
                if (currentMarkers.contains(markerLocation)) {
                    QuestMarkerCarrier carrier = activeQuestStartMarkers.get(markerLocation);
                    QuestMarker mapMarker = carrier.getMapMarker();
                    if ((mapMarker != null) && (mapMarker.getAvailability() != availability)) {
                        MiniMapGui gui = World.getGameGui().getMiniMapGui();
                        gui.releasePointer(carrier.getGuiMarker());
                        mapMarker.setAvailability(availability);

                        Pointer pointer = gui
                                .createStartPointer(availability == QuestMarkerAvailability.Available);
                        pointer.setTarget(markerLocation);
                        activeQuestStartMarkers.put(markerLocation, new QuestMarkerCarrier(mapMarker, pointer));
                        gui.addPointer(pointer);
                    }
                    currentMarkers.remove(markerLocation);
                } else {
                    MapTile tile = getMapAt(markerLocation);
                    if (tile != null) {
                        MiniMapGui gui = World.getGameGui().getMiniMapGui();
                        @Nullable QuestMarker newMarker;
                        if (showQuestsOnGameMap) {
                            newMarker = new QuestMarker(QuestMarkerType.Start, tile);
                            newMarker.setAvailability(availability);
                            newMarker.show();
                        } else {
                            newMarker = null;
                        }

                        @Nullable Pointer pointer;
                        if (showQuestsOnMiniMap) {
                            pointer = gui
                                    .createStartPointer(availability == QuestMarkerAvailability.Available);
                            pointer.setTarget(markerLocation);
                            gui.addPointer(pointer);
                        } else {
                            pointer = null;
                        }

                        activeQuestStartMarkers.put(markerLocation, new QuestMarkerCarrier(newMarker, pointer));
                    }
                }
            }
        }

        for (@Nonnull Location markerLocation : currentMarkers) {
            activeQuestStartMarkers.remove(markerLocation).removeMarker();
        }
    }

    /**
     * Determines whether a map location accepts the light from a specific direction.
     *
     * @param loc the location of the tile
     * @param dx the X-Delta of the light ray direction
     * @param dy the Y-Delta of the light ray direction
     * @return {@code true} if the position accepts the light, false if not
     */
    @Override
    public boolean acceptsLight(@Nonnull Location loc, int dx, int dy) {
        MapTile tile = getMapAt(loc);
        if (tile != null) {
            switch (tile.getFace()) {
                case ItemInfo.FACE_ALL:
                    return true;

                case ItemInfo.FACE_W:
                    return dx >= 0;

                case ItemInfo.FACE_SW:
                    return (dy - dx) < 0;

                case ItemInfo.FACE_S:
                    return dy <= 0;

                default:
                    return true;
            }
        }

        return false;
    }

    /**
     * Determines how much the tile blocks the view.
     *
     * @param loc the location of the tile
     * @return obscurity of the tile, 0 for clear view {@link LightingMap#BLOCKED_VIEW} for fully blocked
     */
    @Override
    @Contract(pure = true)
    public int blocksView(@Nonnull Location loc) {
        MapTile tile = getMapAt(loc);
        if (tile == null) {
            return 0;
        }
        return tile.getCoverage();
    }

    /**
     * Make the map processor checking if the player is inside a building or a
     * cave or something else and start fading out that tiles.
     */
    public void checkInside() {
        GameMapProcessor2.checkInside();
    }

    /**
     * Clear the entire map. This will cause all the tiles and items to be removed. It does not touch the characters.
     */
    public void clear() {
        mapLock.writeLock().lock();
        try {
            for (MapTile tile : tiles.values()) {
                tile.markAsRemoved();
            }
            tiles.clear();
        } finally {
            mapLock.writeLock().unlock();
        }
        for (@Nonnull Entry<Location, QuestMarkerCarrier> markers : activeQuestTargetMarkers.entrySet()) {
            QuestMarker questMarker = markers.getValue().getMapMarker();
            if (questMarker != null) {
                questMarker.markAsRemoved();
            }
            inactiveQuestTargetLocations
                    .put(markers.getKey(), new QuestMarkerCarrier(null, markers.getValue().getGuiMarker()));
        }
        activeQuestTargetMarkers.clear();

        for (@Nonnull Entry<Location, QuestMarkerCarrier> markers : activeQuestStartMarkers.entrySet()) {
            markers.getValue().removeMarker();
        }
        activeQuestStartMarkers.clear();
    }

    /**
     * Check if the map is currently empty.
     *
     * @return {@code true} in case the map is empty
     */
    @Contract(pure = true)
    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    /**
     * Get the amount of tiles currently stored in the map.
     *
     * @return the amount of tiles
     */
    @Contract(pure = true)
    public int getTileCount() {
        return tiles.size();
    }

    /**
     * Finish a tile update of the game map.
     */
    public void finishTileUpdate() {
        GameMapProcessor2.checkInside();
    }

    /**
     * Get item elevation on a special position.
     *
     * @param loc the location that shall be checked
     * @return the elevation value
     */
    @Contract(pure = true)
    public int getElevationAt(@Nonnull Location loc) {
        MapTile ground = getMapAt(loc);
        if (ground != null) {
            return ground.getElevation();
        }
        return 0;
    }

    /**
     * Get the interactive map that is used to interact with this map.
     *
     * @return the map used to interact with this map
     */
    @Nonnull
    @Contract(pure = true)
    public InteractiveMap getInteractive() {
        return interactive;
    }

    /**
     * Get a map tile at a specified location.
     *
     * @param posX the x coordinate of the location of the searched tile
     * @param posY the y coordinate of the location of the searched tile
     * @param posZ the z coordinate of the location of the searched tile
     * @return the map tile at the location or {@code null}
     */
    @Nullable
    @Contract(pure = true)
    public MapTile getMapAt(int posX, int posY, int posZ) {
        return getMapAt(Location.getKey(posX, posY, posZ));
    }

    /**
     * Get a map tile at a specified location.
     *
     * @param loc the location on the map
     * @return the map tile at the location or {@code null}
     */
    @Nullable
    @Contract(pure = true)
    public MapTile getMapAt(@Nonnull Location loc) {
        return getMapAt(loc.getKey());
    }

    /**
     * Get a map tile at a specified location.
     *
     * @param key the key of the location
     * @return the map tile at the location or {@code null}
     */
    @Nullable
    @Contract(pure = true)
    public MapTile getMapAt(long key) {
        mapLock.readLock().lock();
        try {
            return tiles.get(key);
        } finally {
            mapLock.readLock().unlock();
        }
    }

    /**
     * Get the overview map handler that is used currently.
     *
     * @return the object that handles the overview map
     */
    @Nonnull
    public GameMiniMap getMiniMap() {
        return miniMap;
    }

    /**
     * Remove a tile by its key from the map.
     *
     * @param key the key of the tile that is to be removed
     */
    public void removeTile(long key) {
        @Nullable MapTile removedTile = null;
        mapLock.writeLock().lock();
        try {
            removedTile = tiles.remove(key);
        } finally {
            mapLock.writeLock().unlock();
        }

        if (removedTile != null) {
            @Nullable QuestMarkerCarrier marker = activeQuestTargetMarkers.remove(removedTile.getLocation());
            if (marker != null) {
                QuestMarker questMarker = marker.getMapMarker();
                if (questMarker != null) {
                    questMarker.markAsRemoved();
                }

                Pointer guiMarker = marker.getGuiMarker();
                if (guiMarker != null) {
                    inactiveQuestTargetLocations
                            .put(removedTile.getLocation(), new QuestMarkerCarrier(null, guiMarker));
                }
            }

            @Nullable QuestMarkerCarrier startMarker = activeQuestStartMarkers.remove(removedTile.getLocation());
            if (startMarker != null) {
                startMarker.removeMarker();
            }

            removedTile.markAsRemoved();
        }
    }

    /**
     * Render lights based on the tile light and the ambient light generated by the current IG time and the weather.
     */
    @Override
    public void renderLights() {
        synchronized (LIGHT_LOCK) {
            mapLock.readLock().lock();
            try {
                Color ambientLight = World.getWeather().getAmbientLight();
                for (MapTile tile : tiles.values()) {
                    tile.renderLight(ambientLight);
                }
            } finally {
                mapLock.readLock().unlock();
            }
        }

        World.getPeople().updateLight();
    }

    public void updateAmbientLight() {
        synchronized (LIGHT_LOCK) {
            mapLock.readLock().lock();
            try {
                Color ambientLight = World.getWeather().getAmbientLight();
                for (MapTile tile : tiles.values()) {
                    tile.applyAmbientLight(ambientLight);
                }
            } finally {
                mapLock.readLock().unlock();
            }
        }

        World.getPeople().updateLight();
    }

    @Override
    public void saveShutdown() {
        clear();
        miniMap.saveShutdown();
    }

    /**
     * Set a light color on a tile.
     *
     * @param loc the location of the map tile on the server map
     * @param color the color that shall be set for this tile
     */
    @Override
    public void setLight(@Nonnull Location loc, @Nonnull Color color) {
        MapTile tile = getMapAt(loc);
        if (tile != null) {
            tile.addLight(color);
        }
    }

    /**
     * This function sends all tiles to the map processor and causes it to check the tiles again.
     */
    public void updateAllTiles() {
        Collection<Long> tilesToDelete = new HashSet<>();
        mapLock.readLock().lock();
        try {
            for (MapTile tile : tiles.values()) {
                if (GameMapProcessor2.isOutsideOfClipping(tile)) {
                    tilesToDelete.add(tile.getLocation().getKey());
                }
            }
        } finally {
            mapLock.readLock().unlock();
        }
        if (!tilesToDelete.isEmpty()) {
            mapLock.writeLock().lock();
            try {
                for (long key : tilesToDelete) {
                    removeTile(key);
                }
            } finally {
                mapLock.writeLock().unlock();
            }
        }
    }

    public void updateTiles(@Nonnull Iterable<TileUpdate> updateDataList) {
        mapLock.writeLock().lock();
        try {
            for (@Nonnull TileUpdate updateData : updateDataList) {
                updateTile(updateData);
            }
        } finally {
            mapLock.writeLock().unlock();
        }
    }

    /**
     * Perform a update of a single map tile regarding the update information. This can add a new tile,
     * update a old one or delete one tile.
     *
     * @param updateData the data of the update
     */
    @SuppressWarnings("nls")
    public void updateTile(@Nonnull TileUpdate updateData) {
        long locKey = updateData.getLocation().getKey();

        if (updateData.getTileId() == MapTile.ID_NONE) {
            removeTile(locKey);
        } else {
            MapTile tile = getMapAt(locKey);
            boolean newTile = tile == null;

            // create a tile for this location if none was found
            if (newTile) {
                //noinspection ReuseOfLocalVariable
                tile = new MapTile(updateData.getLocation());
            }

            // update tile from update info
            tile.update(updateData);

            if (newTile) {
                mapLock.writeLock().lock();
                try {
                    tiles.put(locKey, tile);
                    setColorLinks(tile);
                    GameMapProcessor2.processTile(tile);
                    if (inactiveQuestTargetLocations.containsKey(updateData.getLocation())) {
                        Pointer pointer = inactiveQuestTargetLocations.remove(updateData.getLocation())
                                .getGuiMarker();

                        QuestMarker newMarker = new QuestMarker(QuestMarkerType.Target, tile);
                        newMarker.setAvailability(QuestMarkerAvailability.Available);
                        activeQuestTargetMarkers
                                .put(updateData.getLocation(), new QuestMarkerCarrier(newMarker, pointer));
                        newMarker.show();
                    }
                } finally {
                    mapLock.writeLock().unlock();
                }
            }
            tile.applyAmbientLight(World.getWeather().getAmbientLight());

            if (World.getMapDisplay().isActive()) {
                World.getLights().notifyChange(updateData.getLocation());
            }

            if (World.getPlayer().getLocation().equals(tile.getLocation())) {
                World.getMusicBox().updatePlayerLocation();
            }
        }
        miniMap.update(updateData);
    }

    @Nullable
    @Contract(pure = true)
    private MapTile getMapAt(@Nonnull Location origin, @Nonnull Direction direction) {
        int offsetX = direction.getDirectionVectorX();
        int offsetY = direction.getDirectionVectorY();

        return getMapAt(origin.getScX() + offsetX, origin.getScY() + offsetY, origin.getScZ());
    }

    /**
     * This function is used to establish the links between the color values of the different tiles.
     *
     * @param tile the new tile that is added
     */
    private void setColorLinks(@Nonnull MapTile tile) {
        Location tileLocation = tile.getLocation();

        //noinspection ConstantConditions
        for (Direction dir : Direction.values()) {
            MapTile offsetTile = getMapAt(tileLocation, dir);
            if (offsetTile != null) {
                tile.linkColors(offsetTile, dir);
            }
        }
    }
}
