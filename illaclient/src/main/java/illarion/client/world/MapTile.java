/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import illarion.client.graphics.*;
import illarion.client.net.server.TileUpdate;
import illarion.client.world.interactive.InteractiveMapTile;
import illarion.common.graphics.Layers;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Location;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.LightSource;
import org.illarion.engine.graphic.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A tile on the map. Contains the tile graphics and items.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("ClassNamingConvention")
@NotThreadSafe
public final class MapTile implements AlphaChangeListener {
    /**
     * Default Tile ID for no tile at this position.
     */
    public static final int ID_NONE = -1;

    /**
     * The instance of the logger that is used to write out the data.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(MapTile.class);

    /**
     * Highest elevation caused by an item on this tile.
     */
    private int elevation;

    /**
     * Index of the tile with the highest elevation in the item array.
     */
    private int elevationIndex;

    /**
     * This value contains the value the quest marker is elevated by.
     */
    private int questMarkerElevation;

    /**
     * List of items on the tile.
     */
    @Nullable
    @GuardedBy("itemsLock")
    private List<Item> items;

    /**
     * The lock used to guard the items table.
     */
    @Nonnull
    private final ReadWriteLock itemsLock = new ReentrantReadWriteLock();

    /**
     * The calculated light in the center of the tile.
     */
    @Nonnull
    private final Color targetCenterColor;

    /**
     * The storage of the color values.
     */
    @Nonnull
    private final List<AnimatedColor> colors;

    /**
     * Light Source that is on the tile.
     */
    @Nullable
    private LightSource lightSrc;

    /**
     * Light value of the light on this tile.
     */
    private int lightValue;

    /**
     * Location of the tile.
     */
    @Nonnull
    private final Location tileLocation;

    /**
     * Flag if there is still work to do for the LOS calculation on this tile.
     */
    private boolean losDirty;

    /**
     * The ID of the sound track that is played while the player is standing on this tile.
     */
    private int musicId;

    /**
     * Value for partial obstruction.
     */
    private int obstruction;

    /**
     * Graphical representation of the tile.
     */
    @Nullable
    private Tile tile;

    /**
     * ID of the tile.
     */
    private int tileId;

    /**
     * The movement cost of this tile.
     */
    private int movementCost;

    /**
     * The temporary light instance that is used for the calculations before its applied to the actual light.
     */
    private final Color tmpLight = new Color(Color.WHITE);

    /**
     * The reference to the tile that is obstructing this tile.
     */
    @Nullable
    private Reference<MapTile> obstructingTileRef;

    /**
     * The reference to the interactive map tile that was buffered for later usage.
     */
    @Nullable
    private Reference<InteractiveMapTile> interactiveMapTileRef;

    /**
     * The map group this tile is assigned to.
     */
    @Nullable
    private MapGroup group;

    /**
     * Get the item that causes the elevation.
     *
     * @return get the item that causes a elevation in case there is any
     */
    @Nullable
    public Item getElevatingItem() {
        if (elevation == 0) {
            return null;
        }
        itemsLock.readLock().lock();
        try {
            if ((items == null) || (items.size() <= elevationIndex)) {
                return null;
            }
            return items.get(elevationIndex);
        } finally {
            itemsLock.readLock().unlock();
        }
    }

    public int getQuestMarkerElevation() {
        return questMarkerElevation;
    }

    public void setObstructingTile(@Nonnull MapTile tile) {
        obstructingTileRef = new WeakReference<>(tile);
    }

    @Nullable
    public MapTile getObstructingTile() {
        if (obstructingTileRef == null) {
            return null;
        }
        MapTile obstructingTile = obstructingTileRef.get();
        if (obstructingTile == null) {
            obstructingTileRef = null;
            return null;
        }

        if (obstructingTile.removedTile) {
            obstructingTileRef = null;
            return null;
        }
        return obstructingTile;
    }

    public void setMapGroup(@Nonnull MapGroup group) {
        this.group = group;
    }

    @Nullable
    public MapGroup getMapGroup() {
        return group;
    }

    /**
     * Create a new instance of the map and assign its location.
     *
     * @param location the location of this tile
     */
    @SuppressWarnings("nls")
    public MapTile(@Nonnull Location location) {
        tileLocation = new Location(location);
        tileId = ID_NONE;
        tile = null;
        lightSrc = null;
        losDirty = true;
        targetCenterColor = new Color(Color.WHITE);
        colors = Arrays.asList(new AnimatedColor[Location.DIR_MOVE8 + 1]);
        colors.set(Location.DIR_MOVE8, new AnimatedColor(targetCenterColor));
    }

    @Nonnull
    public Color getLight() {
        if (removedTile) {
            LOGGER.warn("Fetching light of a removed tile.");
        }
        return getLight(Location.DIR_MOVE8);
    }

    public Color getLight(int direction) {
        if ((direction < 0) || (direction > Location.DIR_MOVE8)) {
            throw new IllegalArgumentException("Direction argument is out of range. Has to be between 0 and " +
                                                       Location.DIR_MOVE8 + " (including)");
        }
        @Nullable AnimatedColor color = colors.get(direction);
        return (color == null) ? Color.WHITE : color.getCurrentColor();
    }

    public boolean hasLightGradient() {
        @Nullable Color lastColor = null;
        for (@Nullable AnimatedColor testAnimatedColor : colors) {
            Color testColor = (testAnimatedColor == null) ? Color.WHITE : testAnimatedColor.getCurrentColor();
            if (lastColor == null) {
                lastColor = testColor;
            } else if (!lastColor.equals(testColor)) {
                return true;
            }
        }
        return false;
    }

    public void updateColor(int delta) {
        colors.get(Location.DIR_MOVE8).update(delta);
    }

    void linkColors(@Nonnull MapTile otherTile, int direction) {
        if ((direction < 0) || (direction >= Location.DIR_MOVE8)) {
            throw new IllegalArgumentException("Direction is not a valid direction value: " + direction);
        }

        int reverseDirection = (direction + (Location.DIR_SOUTH - Location.DIR_NORTH)) % Location.DIR_MOVE8;
        otherTile.colors.set(reverseDirection, colors.get(Location.DIR_MOVE8));
        colors.set(direction, otherTile.colors.get(Location.DIR_MOVE8));
    }

    /**
     * Get the item on the top of this tile.
     *
     * @return the top tile or {@code null} in case there is none
     */
    @Nullable
    public Item getTopItem() {
        if (removedTile) {
            LOGGER.warn("Requested top item of a removed tile.");
            return null;
        }

        itemsLock.readLock().lock();
        try {
            if ((items == null) || items.isEmpty()) {
                return null;
            }
            return items.get(items.size() - 1);
        } finally {
            itemsLock.readLock().unlock();
        }
    }

    /**
     * This function receives updates in case the alpha value of the tile changes. Its possible that this happens in
     * case the character is walking past this tile. The effect of this is that the map processor checks the tile again
     * and displays it, so the tile below the tile that got faded out is visible properly.
     */
    @Override
    public void alphaChanged(int from, int to) {
        if (removedTile) {
            LOGGER.warn("Updating alpha of a changed tile");
            return;
        }
    }

    /**
     * Once this value is set {@code true} the tile is assumed to be removed.
     */
    private boolean removedTile;

    /**
     * Remove the light source of this tile in case there is any.
     */
    public void markAsRemoved() {
        removedTile = true;
        if (lightSrc != null) {
            World.getLights().remove(lightSrc);
            LightSource.releaseLight(lightSrc);
        }
        clampItems(0);
        if (tile != null) {
            tile.markAsRemoved();
            tile = null;
        }
    }

    /**
     * Create a string that identifies the tile and its current state.
     *
     * @return the generated string
     */
    @SuppressWarnings("nls")
    @Override
    @Nonnull
    public String toString() {
        itemsLock.readLock().lock();
        try {
            return "MapTile " + tileLocation + " tile=" + tileId + " items=" +
                    ((items != null) ? items.size() : 0);
        } finally {
            itemsLock.readLock().unlock();
        }
    }

    /**
     * Update the item at the top position of the stack of items.
     *
     * @param oldItemId the ID of the item that is currently on the top position
     * @param itemId the new ID that shall be set on the item
     * @param count the new count value of the item in top position
     */
    @SuppressWarnings("nls")
    public void changeTopItem(
            @Nonnull ItemId oldItemId, @Nonnull ItemId itemId, @Nonnull ItemCount count) {
        if (removedTile) {
            LOGGER.warn("Changing top item of removed tile requested.");
            return;
        }
        itemsLock.writeLock().lock();
        try {
            if (items == null) {
                LOGGER.warn("There are no items on this field. Change top impossible.");
                return;
            }
            int pos = items.size() - 1;
            if (pos < 0) {
                LOGGER.warn("error: change top item on empty field");
                return;
            }

            if (items.get(pos).getItemId().equals(oldItemId)) {
                setItem(pos, itemId, count);
            } else {
                LOGGER.warn("change top item mismatch. Expected {} found {}", oldItemId,
                            items.get(pos).getItemId().getValue());
            }
        } finally {
            itemsLock.writeLock().unlock();
        }
        itemChanged();
    }

    /**
     * Remove the item at the top position of the item stack.
     */
    @SuppressWarnings("nls")
    public void removeTopItem() {
        if (removedTile) {
            LOGGER.warn("Remove top item of removed tile requested.");
            return;
        }

        itemsLock.writeLock().lock();
        try {
            if (items == null) {
                LOGGER.warn("Remove top item on empty field");
                return;
            }
            int pos = items.size() - 1;
            if (pos < 0) {
                LOGGER.warn("Remove top item on empty field");
                return;
            }

            // supporting item was removed
            if ((elevation > 0) && (elevationIndex == pos)) {
                elevation = 0;
                elevationIndex = 0;
            }

            Item removedItem = items.remove(pos);
            removedItem.markAsRemoved();
            // enable numbers for next top item
            if ((pos - 1) >= 0) {
                items.get(pos - 1).enableNumbers(true);
            } else {
                items = null;
            }
        } finally {
            itemsLock.writeLock().unlock();
        }
        itemChanged();
    }

    /**
     * Add a single item to the item stack. The new item is placed at the last position and is shown on top this way.
     *
     * @param itemId the ID of the item that is created
     * @param count the count value of the item that is created
     */
    public void addItem(@Nonnull ItemId itemId, @Nonnull ItemCount count) {
        if (removedTile) {
            LOGGER.warn("Trying to add a item to a removed tile.");
            return;
        }
        itemsLock.writeLock().lock();
        try {
            int pos = 0;
            if (items != null) {
                pos = items.size();
                // disable numbers for old top item
                if (pos > 0) {
                    items.get(pos - 1).enableNumbers(false);
                }
            }
            setItem(pos, itemId, count);
            // enable numbers for new top item
            items.get(pos).enableNumbers(true);
        } finally {
            itemsLock.writeLock().unlock();
        }
        itemChanged();
    }

    /**
     * Set a item at a special position of the item stack on this tile.
     *
     * @param index The index within the item list of this tile
     * @param itemId The new item ID of the item
     * @param itemCount The new count value of this item
     */
    @SuppressWarnings("nls")
    private void setItem(int index, @Nonnull ItemId itemId, @Nonnull ItemCount itemCount) {
        @Nullable Item item = null;
        // look for present item in map tile
        itemsLock.writeLock().lock();
        try {
            if (items != null) {
                if (index < items.size()) {
                    item = items.get(index);
                    // just an update of present item
                    if (item.getItemId().equals(itemId)) {
                        updateItem(item, itemCount, index);
                    } else {
                        // different item: clear old item
                        item.markAsRemoved();
                        item = null;

                        // carrying item was removed
                        if (index == elevationIndex) {
                            elevation = 0;
                            elevationIndex = 0;
                        }
                    }
                }
            } else {
                items = new ArrayList<>();
            }
            // add a new item
            if (item == null) {
                // create new item
                item = Item.create(itemId, tileLocation, this);

                updateItem(item, itemCount, index);
                // display on screen

                item.show();

                // add it to list
                if (index < items.size()) {
                    items.set(index, item);
                } else if (index == items.size()) { // extend list by 1 row
                    items.add(item);
                } else { // index mismatch
                    throw new IllegalArgumentException("update behind end of items list");
                }
            }
        } finally {
            itemsLock.writeLock().unlock();
        }
        // temporarily disable all numbers
        item.enableNumbers(false);
    }

    /**
     * Update a single item with new data.
     *
     * @param item the item that shall be updated
     * @param itemCount the count value of the new item
     * @param index the index of the item within the stack of items on this tile
     */
    private void updateItem(@Nonnull Item item, @Nonnull ItemCount itemCount, int index) {
        // set number
        item.setCount(itemCount);
        // calculate offset from items carrying other items
        int objectOffset = 0;
        if ((elevation > 0) && (index > elevationIndex)) {
            objectOffset = elevation;
        }
        // position on tile with increasing z-order
        item.setScreenPos(tileLocation.getDcX(), tileLocation.getDcY() - objectOffset, tileLocation.getDcZ() - index,
                          Layers.ITEM);

        // set the elevation for items that can carry
        int level = item.getTemplate().getItemInfo().getLevel();
        if (level > 0) {
            // Set elevation only for first suitable item
            if (((elevation == 0) || (elevationIndex == index)) && (elevation != level)) {
                elevation = level;
                elevationIndex = index;

                Char charOnTile = World.getPeople().getCharacterAt(tileLocation);
                if (charOnTile != null) {
                    if (World.getPlayer().isPlayer(charOnTile.getCharId())) {
                        World.getMapDisplay().updateElevation();
                    }
                    charOnTile.updateElevation(elevation);
                }
            }
        }
    }

    /**
     * Notify the tile that the items got changed. That makes a recalculation of the lights and the line of sight
     * needed.
     */
    private void itemChanged() {
        updateQuestMarkerElevation();

        // invalidate LOS data
        losDirty = true;
        // report a change of shadow
        World.getLights().notifyChange(tileLocation);
        // check for a light source
        checkLight();
    }

    /**
     * Update the elevation of the quest markers. This needs to be called when ever the items or the character on
     * this tile change.
     */
    public void updateQuestMarkerElevation() {
        Char character = World.getPeople().getCharacterAt(tileLocation);
        @Nullable Avatar avatar = ((character == null) || !character.isVisible()) ? null : character.getAvatar();

        questMarkerElevation = elevation;
        if (avatar == null) {
            Item topItem = getTopItem();
            if (elevationIndex == 0) {
                questMarkerElevation = 0;
            }
            if (topItem != null) {
                Sprite topItemSprite = topItem.getTemplate().getSprite();

                questMarkerElevation += Math
                        .round((topItemSprite.getOffsetY() + topItemSprite.getHeight()) * topItem.getScale());
            }
        } else {
            questMarkerElevation += Math.round(avatar.getTemplate().getSprite().getHeight() * avatar.getScale());
        }
    }

    /**
     * Determine whether the top item is a light source and needs to be registered. Also removes previous or changed
     * light sources.
     */
    private void checkLight() {
        int newLightValue = 0;
        itemsLock.readLock().lock();
        try {
            if (items != null) {
                for (Item item : items) {
                    if (item.getTemplate().getItemInfo().isLight()) {
                        newLightValue = item.getTemplate().getItemInfo().getLight();
                        break;
                    }
                }
            }
        } finally {
            itemsLock.readLock().unlock();
        }

        if (lightValue == newLightValue) {
            return;
        }

        if (lightSrc != null) {
            World.getLights().remove(lightSrc);
            LightSource.releaseLight(lightSrc);
            lightSrc = null;
        }

        if (newLightValue > 0) {
            lightSrc = LightSource.createLight(tileLocation, newLightValue);
            World.getLights().add(lightSrc);
        }

        lightValue = newLightValue;
    }

    /**
     * Add some light influence to this tile. This is added to the already existing light on this tile
     *
     * @param color the light that shall be added
     */
    public void addLight(@Nonnull Color color) {
        if (removedTile) {
            LOGGER.warn("Adding light to a removed tile.");
            return;
        }
        tmpLight.add(color);
    }

    /**
     * Check if the player can move the top item on this tile.
     *
     * @return true if the player can move the item around
     */
    public boolean canMoveItem() {
        if (removedTile) {
            LOGGER.warn("Checking a removed tile for a movable item.");
            return false;
        }
        if (!World.getPlayer().getLocation().isNeighbour(tileLocation)) {
            return false;
        }

        Item topItem = getTopItem();
        return (topItem != null) && topItem.getTemplate().getItemInfo().isMovable();
    }

    /**
     * Determine how much of the tile is hidden due items on it. Needed for LOS calculation.
     *
     * @return identifier how much of the tile is hidden
     */
    public int getCoverage() {
        if (removedTile) {
            LOGGER.warn("Checking the coverage of a removed tile");
            return 0;
        }
        if (losDirty) {
            obstruction = 0;
            itemsLock.readLock().lock();
            try {
                if (items != null) {
                    for (Item item : items) {
                        obstruction += item.getTemplate().getItemInfo().getOpacity();
                    }
                }
            } finally {
                itemsLock.readLock().unlock();
            }
            losDirty = false;
        }

        return obstruction;
    }

    /**
     * Get the elevation of the item with the highest elevation value.
     *
     * @return the elevation value
     */
    public int getElevation() {
        if (removedTile) {
            LOGGER.warn("Checking the elevation of a removed tile.");
        }
        return elevation;
    }

    /**
     * Check from what sides the tile accepts light.
     *
     * @return the identifier for the side the tile accepts light from
     */
    public int getFace() {
        if (removedTile) {
            LOGGER.warn("Checking the facing flag of a removed tile.");
            return 0;
        }
        // empty tile accept all light
        itemsLock.readLock().lock();
        try {
            if ((items == null) || items.isEmpty()) {
                return 0;
            }

            // non-movable items are only lit from the front
            return items.get(0).getTemplate().getItemInfo().getFace();
        } finally {
            itemsLock.readLock().unlock();
        }
    }

    /**
     * Get the the interactive instance used for interaction with this tile.
     *
     * @return the interactive tile referring to this map tile
     */
    @Nonnull
    public InteractiveMapTile getInteractive() {
        if (removedTile) {
            LOGGER.warn("Request a interactive reference to a removed tile.");
        }
        if (interactiveMapTileRef != null) {
            @Nullable InteractiveMapTile interactiveMapTile = interactiveMapTileRef.get();
            if (interactiveMapTile != null) {
                return interactiveMapTile;
            }
        }

        InteractiveMapTile interactiveMapTile = new InteractiveMapTile(this);
        interactiveMapTileRef = new SoftReference<>(interactiveMapTile);
        return interactiveMapTile;
    }

    /**
     * Get the cost of moving over this item. Needed was walking patch calculations.
     *
     * @return the costs for moving over this tile
     */
    public int getMovementCost() {
        if (removedTile) {
            LOGGER.warn("Checking the movement costs on a removed tile.");
            return Integer.MAX_VALUE;
        }
        Tile localTile = tile;
        if ((localTile == null) || (movementCost == -1)) {
            return Integer.MAX_VALUE;
        }
        return movementCost;
    }

    /**
     * Get the ID of the background music track that is supposed to be played on this tile.
     *
     * @return the background music track for this tile
     */
    public int getTileMusic() {
        if (removedTile) {
            LOGGER.warn("Requested the music ID of a removed tile.");
            return 0;
        }
        return musicId;
    }

    /**
     * Check if this tile is at the same level as the player.
     *
     * @return {@code true} in case the tile is on the same level as the player
     */
    public boolean isAtPlayerLevel() {
        if (removedTile) {
            LOGGER.warn("Checking if a removed tile is at the level of the player.");
            return false;
        }
        return World.getPlayer().isBaseLevel(getLocation());
    }

    /**
     * Get the location of the tile.
     *
     * @return the location of the tile
     */
    @Nonnull
    public Location getLocation() {
        if (removedTile) {
            LOGGER.warn("Requesting the location of a removed tile");
        }
        return tileLocation;
    }

    /**
     * Check if there is anything on the tile that blocks moving over the tile.
     *
     * @return true in case it is not possible to walk over this tile
     */
    public boolean isBlocked() {
        if (removedTile) {
            LOGGER.warn("Checking a removed tile if its blocked.");
            return true;
        }
        if (isObstacle()) {
            return true;
        }

        // check for chars
        return World.getPeople().getCharacterAt(tileLocation) != null;
    }

    /**
     * Check if the tile is obstacle or if there is a item that blocks the movement over this tile.
     *
     * @return true if the tile is obstacle
     */
    public boolean isObstacle() {
        if (removedTile) {
            LOGGER.warn("Checking a removed tile if its a obstacle.");
            return true;
        }
        return getMovementCost() == Integer.MAX_VALUE;
    }

    /**
     * Check if the tile is fully opaque and everything below can be hidden.
     *
     * @return {@code true} in case the tile is opaque and everything below is hidden entirely.
     */
    public boolean isOpaque() {
        if (removedTile) {
            LOGGER.warn("Checking opaque value of a removed tile.");
        }
        Tile localTile = tile;
        if (tile == null) {
            return false;
        }

        boolean opaqueFlag = localTile.getTemplate().getTileInfo().isOpaque();
        boolean transparentFlag = localTile.isTransparent();
        return opaqueFlag && !transparentFlag;
    }

    /**
     * Render the light on this tile, using the ambient light of the weather and a factor how much the tile light
     * modifies the ambient light.
     *
     * @param ambientLight the ambient light from the weather
     */
    public void renderLight(@Nonnull Color ambientLight) {
        if (removedTile) {
            LOGGER.warn("Render light of a removed tile.");
            return;
        }
        targetCenterColor.setColor(tmpLight);
        targetCenterColor.multiply(1.f - ambientLight.getLuminancef());
        targetCenterColor.add(ambientLight);
        targetCenterColor.setAlpha(Color.MAX_INT_VALUE);
    }

    /**
     * Show a graphical effect on the tile.
     *
     * @param effectId the ID of the effect
     */
    public void showEffect(int effectId) {
        if (removedTile) {
            LOGGER.warn("Show a graphics effect on a removed tile.");
            return;
        }
        Effect effect = Effect.create(effectId);
        effect.show(tileLocation);
    }

    /**
     * Update a map tile using the update data the server send.
     *
     * @param update the update data the server send
     */
    public void update(@Nonnull TileUpdate update) {
        if (removedTile) {
            LOGGER.warn("Process update of a removed tile.");
            return;
        }
        // update tile
        setTileId(update.getTileId());

        movementCost = update.isBlocked() ? -1 : update.getMovementCost();
        musicId = update.getTileMusic();

        // update items
        updateItemList(update.getItemNumber(), update.getItemId(), update.getItemCount());

        itemChanged();
    }

    /**
     * Set the ID of the tile and change the type of the tile this way. This function also sets up a new tile at this
     * position if there was no. Furthermore all calculations that are needed for a new tile are triggered by this
     * function.
     *
     * @param id the new ID if the tile
     */
    public void setTileId(int id) {
        if (removedTile) {
            LOGGER.warn("Change the ID of a removed tile.");
            return;
        }
        losDirty = true;
        resetLight();

        // no replacement necessary
        if ((tileId == id) && (tile != null)) {
            tile.setScreenPos(tileLocation, Layers.TILE);
            return;
        }

        tileId = id;

        // free old tile to factory
        if (tile != null) {
            tile.markAsRemoved();
            tile = null;
        }

        // get a new tile to display
        if (id >= 0) {
            // create a tile, possibly with variants
            tile = new Tile(id, this);

            tile.addAlphaChangeListener(this);
            tile.setScreenPos(tileLocation, Layers.TILE);
            tile.show();
        }
    }

    @Nullable
    public Tile getTile() {
        return tile;
    }

    /**
     * Reset the light value back to 0.
     */
    public void resetLight() {
        if (removedTile) {
            LOGGER.warn("Resetting the light of a removed tile.");
        }
        tmpLight.setColor(Color.BLACK);
    }

    /**
     * Update all items on the stack of this tile at once.
     *
     * @param number the new amount of items on this tile
     * @param itemId the list of item ids for the items on this tile
     * @param itemCount the list of count values for the items on this tile
     */
    private void updateItemList(int number, @Nonnull List<ItemId> itemId, @Nonnull List<ItemCount> itemCount) {
        Lock lock = itemsLock.writeLock();
        lock.lock();
        try {
            try {
                clampItems(number);
                for (int i = 0; i < number; i++) {
                    setItem(i, itemId.get(i), itemCount.get(i));
                }
            } finally {
                Lock readLock = itemsLock.readLock();
                //noinspection LockAcquiredButNotSafelyReleased
                readLock.lock();
                try {
                    lock.unlock();
                } finally {
                    lock = readLock;
                }
            }

            // enable numbers for top item
            if (items != null) {
                int pos = items.size() - 1;
                if (pos >= 0) {
                    items.get(pos).enableNumbers(true);
                }
            }
        } finally {
            lock.unlock();
        }
        itemChanged();
    }

    /**
     * Delete any surplus items in an update.
     *
     * @param itemNumber the maximum amount of items that shall remain
     */
    private void clampItems(int itemNumber) {
        // reset elevation data when items are updated
        elevation = 0;
        elevationIndex = -1;

        itemsLock.writeLock().lock();
        try {
            if (items == null) {
                return;
            }

            int amount = items.size() - itemNumber;
            for (int i = 0; i < amount; i++) {
                // recycle the removed items
                Item item = items.get(itemNumber);
                item.markAsRemoved();

                // keep deleting in the same place as the list becomes shorter
                items.remove(itemNumber);
            }

            if (items.isEmpty()) {
                items = null;
            }
        } finally {
            itemsLock.writeLock().unlock();
        }
    }

    /**
     * Update the entire item list. This function is called by the net interface in case the server sends a full set of
     * new item data to the client.
     *
     * @param itemNumber Amount of items within the list of items
     * @param itemId List of the item IDs for all items that shall be created
     * @param itemCount List of count values for all items
     */
    public void updateItems(
            int itemNumber, @Nonnull List<ItemId> itemId, @Nonnull List<ItemCount> itemCount) {
        if (removedTile) {
            LOGGER.warn("Update items of a removed tile requested.");
            return;
        }
        updateItemList(itemNumber, itemId, itemCount);
        itemChanged();
    }

    public boolean isHidden() {
        return (group != null) && group.isHidden();
    }
}
