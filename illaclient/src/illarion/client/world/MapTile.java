/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world;

import illarion.client.graphics.AlphaChangeListener;
import illarion.client.graphics.Effect;
import illarion.client.graphics.Item;
import illarion.client.graphics.Tile;
import illarion.client.net.server.TileUpdate;
import illarion.client.world.interactive.InteractiveMapTile;
import illarion.common.graphics.Layers;
import illarion.common.graphics.LightSource;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Location;
import org.apache.log4j.Logger;
import org.newdawn.slick.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
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
    private static final Logger LOGGER = Logger.getLogger(MapTile.class);

    /**
     * Highest elevation caused by an item on this tile.
     */
    private int elevation;

    /**
     * Index of the tile with the highest elevation in the item array.
     */
    private int elevationIndex;

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
    private ReadWriteLock itemsLock = new ReentrantReadWriteLock();

    /**
     * rendered light value on this tile.
     */
    @Nonnull
    private final Color light = new Color(0);

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
     * The temporary light instance that is used for the calculations before its applied to the actual light.
     */
    private final Color tmpLight = new Color(0);

    /**
     * The reference to the tile that is obstructing this tile.
     */
    @Nullable
    private Reference<MapTile> obstructingTileRef;

    /**
     * The map group this tile is assigned to.
     */
    @Nullable
    private MapGroup group;

    public void setObstructingTile(@Nonnull final MapTile tile) {
        obstructingTileRef = new WeakReference<MapTile>(tile);
    }

    @Nullable
    public MapTile getObstructingTile() {
        if (obstructingTileRef == null) {
            return null;
        }
        final MapTile obstructingTile = obstructingTileRef.get();
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

    public void setMapGroup(@Nonnull final MapGroup group) {
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
    public MapTile(@Nonnull final Location location) {
        tileLocation = new Location(location);
        tileId = ID_NONE;
        tile = null;
        lightSrc = null;
        losDirty = true;
    }

    /**
     * Get the current rendered light.
     *
     * @return the light color on this tile
     */
    @Nonnull
    public Color getLight() {
        if (removedTile) {
            LOGGER.warn("Fetching light of a removed tile.");
        }
        return light;
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
    public void alphaChanged(final int from, final int to) {
        if (removedTile) {
            LOGGER.warn("Updating alpha of a changed tile");
            return;
        }
        if (((from == 255) && (to < from)) || ((from < to) && (to == 255))) {
            World.getMap().updateTile(this);
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
            return "MapTile " + tileLocation.toString() + " tile=" + tileId + " items=" +
                    ((items != null) ? items.size() : 0);
        } finally {
            itemsLock.readLock().unlock();
        }
    }

    /**
     * Update the item at the top position of the stack of items.
     *
     * @param oldItemId the ID of the item that is currently on the top position
     * @param itemId    the new ID that shall be set on the item
     * @param count     the new count value of the item in top position
     */
    @SuppressWarnings("nls")
    public void changeTopItem(@Nonnull final ItemId oldItemId, @Nonnull final ItemId itemId,
                              @Nonnull final ItemCount count) {
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
            final int pos = items.size() - 1;
            if (pos < 0) {
                LOGGER.warn("error: change top item on empty field");
                return;
            }

            if (items.get(pos).getItemId().equals(oldItemId)) {
                setItem(pos, itemId, count);
            } else {
                LOGGER.warn("change top item mismatch. Expected " + oldItemId + " found " + items.get(pos).getItemId().getValue());
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
            final int pos = items.size() - 1;
            if (pos < 0) {
                LOGGER.warn("Remove top item on empty field");
                return;
            }

            // supporting item was removed
            if ((elevation > 0) && (elevationIndex == pos)) {
                elevation = 0;
                elevationIndex = 0;
            }

            final Item removedItem = items.remove(pos);
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
     * @param count  the count value of the item that is created
     */
    public void addItem(@Nonnull final ItemId itemId, @Nonnull final ItemCount count) {
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
     * @param index     The index within the item list of this tile
     * @param itemId    The new item ID of the item
     * @param itemCount The new count value of this item
     */
    @SuppressWarnings("nls")
    private void setItem(final int index, @Nonnull final ItemId itemId, @Nonnull final ItemCount itemCount) {
        Item item = null;
        // look for present item in map tile
        itemsLock.writeLock().lock();
        try {
            if (items != null) {
                if (index < items.size()) {
                    item = items.get(index);
                    // just an update of present item
                    if (item.getItemId() == itemId) {
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
                items = new ArrayList<Item>();
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
     * @param item      the item that shall be updated
     * @param itemCount the count value of the new item
     * @param index     the index of the item within the stack of items on this tile
     */
    private void updateItem(@Nonnull final Item item, @Nonnull final ItemCount itemCount, final int index) {
        // set number
        item.setCount(itemCount);
        // calculate offset from items carrying other items
        int objectOffset = 0;
        if ((elevation > 0) && (index > elevationIndex)) {
            objectOffset = elevation;
        }
        // position on tile with increasing z-order
        item.setScreenPos(tileLocation.getDcX(), tileLocation.getDcY() - objectOffset, tileLocation.getDcZ() - index, Layers.ITEM);

        // set the elevation for items that can carry
        final int level = item.getTemplate().getItemInfo().getLevel();
        if (level > 0) {
            // Set elevation only for first suitable item
            if ((elevation == 0) || (elevationIndex == index)) {
                elevation = level;
                elevationIndex = index;
            }
        }
    }

    /**
     * Notify the tile that the items got changed. That makes a recalculation of the lights and the line of sight
     * needed.
     */
    private void itemChanged() {
        // invalidate LOS data
        losDirty = true;
        // report a change of shadow
        World.getLights().notifyChange(tileLocation);
        // check for a light source
        checkLight();
    }

    /**
     * Determine whether the top item is a light source and needs to be registered. Also removes previous or changed
     * light sources.
     */
    private void checkLight() {
        // light sources are only on player level
        if (!World.getPlayer().isBaseLevel(tileLocation)) {
            return;
        }

        int newLightValue = 0;

        itemsLock.readLock().lock();
        try {
            if (items != null) {
                for (final Item item : items) {
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
    public void addLight(@Nonnull final Color color) {
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

        final Item topItem = getTopItem();
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
                    for (final Item item : items) {
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
        return new InteractiveMapTile(this);
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
        final Tile localTile = tile;
        if (localTile == null) {
            return Integer.MAX_VALUE;
        }
        return localTile.getTemplate().getTileInfo().getMovementCost();
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
        final Tile localTile = tile;
        if (localTile == null) {
            return true;
        }

        boolean obstacle = localTile.getTemplate().getTileInfo().getMovementCost() == 0;

        // check items
        itemsLock.readLock().lock();
        try {
            if (items != null) {
                for (final Item item : items) {
                    if (item.getTemplate().getItemInfo().isObstacle()) {
                        return true;
                    }
                    if (item.getTemplate().getItemInfo().isJesus()) {
                        obstacle = false;
                    }
                }
            }
        } finally {
            itemsLock.readLock().unlock();
        }

        return obstacle;
    }

    /**
     * Check if the tile is fully opaque and everything below can be hidden.
     *
     * @return <code>true</code> in case the tile is opaque and everything below is hidden entirely.
     */
    public boolean isOpaque() {
        if (removedTile) {
            LOGGER.warn("Checking opaque value of a removed tile.");
        }
        final Tile localTile = tile;
        if (tile == null) {
            return false;
        }

        final boolean opaqueFlag = localTile.getTemplate().getTileInfo().isOpaque();
        final boolean transparentFlag = localTile.isTransparent();
        return opaqueFlag && !transparentFlag;
    }

    /**
     * Render the light on this tile, using the ambient light of the weather and a factor how much the tile light
     * modifies the ambient light.
     *
     * @param factor       the factor how much the ambient light is modified by the tile light
     * @param ambientLight the ambient light from the weather
     */
    public void renderLight(final float factor, @Nonnull final Color ambientLight) {
        if (removedTile) {
            LOGGER.warn("Render light of a removed tile.");
            return;
        }
        tmpLight.scale(factor);
        tmpLight.add(ambientLight);
        light.a = 1.f;
        light.r = tmpLight.r;
        light.g = tmpLight.g;
        light.b = tmpLight.b;
    }

    /**
     * Show a graphical effect on the tile.
     *
     * @param effectId the ID of the effect
     */
    public void showEffect(final int effectId) {
        if (removedTile) {
            LOGGER.warn("Show a graphics effect on a removed tile.");
            return;
        }
        final Effect effect = Effect.create(effectId);
        effect.show(tileLocation);
    }

    /**
     * Update a map tile using the update data the server send.
     *
     * @param update the update data the server send
     */
    public void update(@Nonnull final TileUpdate update) {
        if (removedTile) {
            LOGGER.warn("Process update of a removed tile.");
            return;
        }
        // update tile
        setTileId(update.getTileId());

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
    public void setTileId(final int id) {
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
            tile = Tile.create(id, tileLocation, this);

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
        tmpLight.scale(0.f);
    }

    /**
     * Update all items on the stack of this tile at once.
     *
     * @param number    the new amount of items on this tile
     * @param itemId    the list of item ids for the items on this tile
     * @param itemCount the list of count values for the items on this tile
     */
    private void updateItemList(final int number, @Nonnull final List<ItemId> itemId,
                                @Nonnull final List<ItemCount> itemCount) {
        itemsLock.writeLock().lock();
        try {
            try {
                clampItems(number);
                for (int i = 0; i < number; i++) {
                    setItem(i, itemId.get(i), itemCount.get(i));
                }
                itemsLock.readLock().lock();
            } finally {
                itemsLock.writeLock().unlock();
            }

            // enable numbers for top item
            if (items != null) {
                final int pos = items.size() - 1;
                if (pos >= 0) {
                    items.get(pos).enableNumbers(true);
                }
            }
        } finally {
            itemsLock.readLock().unlock();
        }
    }

    /**
     * Delete any surplus items in an update.
     *
     * @param itemNumber the maximum amount of items that shall remain
     */
    private void clampItems(final int itemNumber) {
        // reset elevation data when items are updated
        elevation = 0;
        elevationIndex = -1;

        itemsLock.writeLock().lock();
        try {
            if (items == null) {
                return;
            }

            final int amount = items.size() - itemNumber;
            for (int i = 0; i < amount; i++) {
                // recycle the removed items
                final Item item = items.get(itemNumber);
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
     * @param itemId     List of the item IDs for all items that shall be created
     * @param itemCount  List of count values for all items
     */
    public void updateItems(final int itemNumber, @Nonnull final List<ItemId> itemId,
                            @Nonnull final List<ItemCount> itemCount) {
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
