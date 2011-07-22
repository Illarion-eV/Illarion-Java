/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world;

import java.util.List;

import javolution.util.FastTable;

import org.apache.log4j.Logger;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TShortArrayList;

import illarion.client.graphics.AlphaChangeListener;
import illarion.client.graphics.Effect;
import illarion.client.graphics.EffectPool;
import illarion.client.graphics.Item;
import illarion.client.graphics.Marker;
import illarion.client.graphics.MarkerFactory;
import illarion.client.graphics.Tile;
import illarion.client.graphics.particle.ParticlePool;
import illarion.client.guiNG.references.AbstractReference;
import illarion.client.guiNG.references.MapReference;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.LookatTileCmd;
import illarion.client.net.server.TileUpdate;

import illarion.common.graphics.Layers;
import illarion.common.graphics.MapConstants;
import illarion.common.util.Location;
import illarion.common.util.RecycleObject;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;
import illarion.graphics.common.LightSource;

/**
 * A tile on the map. Contains the tile graphics and items.
 */
public final class MapTile extends Interaction implements AlphaChangeListener,
    RecycleObject {

    /**
     * Default Tile ID for no tile at this position.
     */
    protected static final int ID_NONE = -1;

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(MapTile.class);

    /**
     * Higest elevation caused by an item on this tile.
     */
    private int elevation;

    /**
     * Index of the tile with the highest elevation in the item array.
     */
    private int elevationIndex;

    /**
     * Flag if the tile is hidden or not.
     */
    private boolean hidden;

    /**
     * The hover marker that is displayed in case the mouse moves over the tile.
     * 
     * @deprecated This was part of the old GUI
     */
    @Deprecated
    private Marker hoverMarker = null;

    /**
     * List of items on the tile.
     */
    private FastTable<Item> items;

    /**
     * rendered light value on this tile.
     */
    private transient final SpriteColor light = Graphics.getInstance()
        .getSpriteColor();

    /**
     * Light Source that is on the tile.
     */
    private LightSource lightSrc;

    /**
     * Light value of the light on this tile.
     */
    private int lightValue;

    /**
     * Location of the tile.
     */
    private transient Location loc;

    /**
     * Flag if there is still work to do for the LOS calculation on this tile.
     */
    private boolean losDirty;

    /**
     * The ID of the sound track that is played while the player is standing on
     * this tile.
     */
    private int musicId;

    /**
     * Flag if the tile is obstructed by upper levels or not.
     */
    private boolean obstructed;

    /**
     * Value for partial obstruction.
     */
    private int obstruction;

    /**
     * Graphical representation of the tile.
     */
    private Tile tile;

    /**
     * ID of the tile.
     */
    private int tileId;

    /**
     * The particle pool that handles all particles on this tile.
     */
    private transient EffectPool tilePool;

    /**
     * Temporary not rendered light value on this tile.
     */
    private transient final SpriteColor tmpLight = Graphics.getInstance()
        .getSpriteColor();

    /**
     * Default Constructor for a map tile. Generates the tooltip and sets up all
     * initial values for the tile.
     */
    @SuppressWarnings("nls")
    public MapTile() {
        super("tooltip.map");
        tileId = ID_NONE;
        tile = null;
        lightSrc = null;
        losDirty = true;
    }

    /**
     * Create a new instance of a map tile using the GameFactory.
     * 
     * @return the new instance of the map tile
     */
    protected static MapTile create() {
        return (MapTile) GameFactory.getInstance().getCommand(
            GameFactory.OBJ_MAPTILE);
    }

    /**
     * Activate the tile and ready it for usage. This fetches the objects that
     * are required for this instance to work properly.
     * 
     * @param id parameter not in use
     */
    @Override
    public void activate(final int id) {
        loc = Location.getInstance();
    }

    /**
     * Add a single item to the item stack. The new item is placed at the last
     * position and is shown on top this way.
     * 
     * @param itemId the ID of the item that is created
     * @param count the count value of the item that is created
     */
    public void addItem(final int itemId, final int count) {
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
        itemChanged();
    }

    /**
     * This function receives updates in case the alpha value of the tile
     * changes. Its possible that this happens in case the character is walking
     * past this tile. The effect of this is that the map processor checks the
     * tile again and displays it, so the tile below the tile that got faded out
     * is visible properly.
     */
    @Override
    public void alphaChanged(final int from, final int to) {
        if (((from == SpriteColor.COLOR_MAX) && (to < from))
            || ((from < to) && (to == SpriteColor.COLOR_MAX))) {
            Game.getMap().updateTile(this);
        }
    }

    /**
     * Check if the player can move the top item on this tile.
     * 
     * @return true if the player can move the item around
     */
    public boolean canMoveItem() {
        // only if items are present
        if (items != null) {
            final int top = items.size() - 1;
            if (top >= 0) {
                // only movable items
                if (items.get(top).isMovable()) {
                    // can only manipulate items around the character
                    if (Game.getPlayer().getLocation().isNeighbour(loc)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if it is possible to open a container on the tile.
     * 
     * @return true if there is a container that can be opend, else false
     */
    public boolean canOpenContainer() {
        if (items != null) {
            final int top = items.size() - 1;
            if (top >= 0) {
                // check distance from player, only neighbouring fields
                if (Game.getPlayer().getLocation().isNeighbour(loc)) {
                    // it really is a container
                    if (items.get(top).isContainer()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if it is possible to use this tile.
     * 
     * @return true if the tile is useable, false if not
     */
    public boolean canUse() {
        // select only once
        // if (!Gui.getInstance().getManager().isUsed(this)) {
        // // check distance from player, only neighbouring fields
        // if (Game.getPlayer().getLocation().isNeighbour(loc)) {
        // return true;
        // }
        // }
        return false;
    }

    /**
     * Cast a spell on the tile.
     * 
     * @return the casting object or null in case the cast failed
     * @see illarion.client.world.Interaction#castSpellOn()
     */
    @Override
    public AbstractReference castSpellOn() {
        // select only once
        // if (!Gui.getInstance().getManager().isUsed(this)) {
        // if (canSeeLocation()) {
        // // create usage object
        // Reference use = new RefMap(loc);
        // use.setMapUse(this, loc);
        // return use;
        // }
        // Toolkit.getDefaultToolkit().beep();
        // }
        return null;
    }

    /**
     * Update the item at the top position of the stack of items.
     * 
     * @param oldItemId the ID of the item that is currently on the top position
     * @param itemId the new ID that shall be set on the item
     * @param count the new count value of the item in top position
     */
    @SuppressWarnings("nls")
    public void changeTopItem(final int oldItemId, final int itemId,
        final int count) {
        if (items == null) {
            LOGGER
                .warn("There are no items on this field. Change top impossible.");
            return;
        }
        final int pos = items.size() - 1;
        if (pos < 0) {
            LOGGER.warn("error: change top item on empty field");
            return;
        }

        if (items.get(pos).getId() == oldItemId) {
            setItem(pos, itemId, count);
        } else {
            LOGGER.warn("change top item mismatch. Expected " + oldItemId
                + " found " + items.get(pos).getId());
        }
        itemChanged();
    }

    /**
     * Create a clone of the tile.
     * 
     * @return the clone of this tile
     */
    @Override
    public MapTile clone() {
        return new MapTile();
    }

    /**
     * Start a dragging event of the top item on this tile.
     * 
     * @param x X-Coordinate of the mouse on the screen
     * @param y Y-Coordinate of the mouse on the screen
     * @return Reference to the dragging event
     */
    @Override
    public AbstractReference dragFrom(final int x, final int y) {
        if (canMoveItem()) {
            // cannot move large items - just an attempt to limit it
            // final Item item = items.get(items.size() - 1);
            final MapReference ref = new MapReference();
            ref.setReferringTile(this);
            // ref.setItemId(item.getId());
            // ref.setSource(this, item);
            return ref;
        }
        return null;
    }

    /**
     * Finish a dragging action on this map tile.
     * 
     * @param dragSrc the Reference to the dragging event
     * @return the reference to this map tile where the dragging finished
     */
    @Override
    public AbstractReference dragTo(final AbstractReference dragSrc) {
        // accept all item types, chars only if they're moving a single step
        // if (((dragSrc.getType() != Reference.CHARACTER) && canSeeLocation())
        // || ((dragSrc.getType() == Reference.CHARACTER) && ((Char) dragSrc
        // .getSource()).getLocation().isNeighbour(loc))) {
        // final RefMap ref = new RefMap(loc);
        // ref.setSource(this);
        // return ref;
        // }
        return null;
    }

    /**
     * Get the map tile in case the user is currently pointing at. If the user
     * is pointing somewhere else, return null.
     * 
     * @param x X-Coordinate on the screen the user is pointing at
     * @param y Y-Coordinate on the screen the user is pointing at
     * @return the map tile in case the user is pointing at, or null
     */
    @Override
    public MapTile getComponentAt(final int x, final int y) {
        if (!hidden && !obstructed) {
            // calculate distance from tile center to mouse
            final int distance =
                Math.abs(x - loc.getDcX()) + (Math.abs(y - loc.getDcY()) * 2);
            if (distance < (MapConstants.TILE_W / 2)) {
                return this;
            }
        }

        return null;
    }

    /**
     * Determine how much of the tile is hidden due items on it. Needed for LOS
     * calculation.
     * 
     * @return identifier how much of the tile is hidden
     */
    public int getCoverage() {
        if (losDirty) {
            obstruction = 0;
            if (items != null) {
                final int count = items.size();
                for (int i = 0; i < count; i++) {
                    obstruction += items.get(i).getCoverage();
                }
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
        return elevation;
    }

    /**
     * Check from what sides the tile acceps light.
     * 
     * @return the identifier for the side the tile accepts light from
     */
    public int getFace() {
        // empty tile accept all light
        if ((items == null) || items.isEmpty()) {
            return 0;
        }

        // non-movable items are only lit from the front
        return items.get(0).getFace();
    }

    /**
     * Get the ID of this object type in the game factory.
     * 
     * @return The ID of the object type for the game factroy
     */
    @Override
    public int getId() {
        return GameFactory.OBJ_MAPTILE;
    }

    /**
     * Get the current rendered light.
     * 
     * @return the light color on this tile
     */
    public SpriteColor getLight() {
        return light;
    }

    /**
     * Get the location of the tile.
     * 
     * @return the location of the tile
     */
    public Location getLocation() {
        return loc;
    }

    /**
     * Request a menu for this tile on the map.
     * 
     * @return the generated context menu
     */
    // @Override
    // public ContextMenu getMenu() {
    // final ContextMenu menu =
    // MenuFactory.getInstance().getCommand(MenuFactory.MENU_MAP);
    // menu.setContext(this);
    // return menu;
    // }

    /**
     * Get the color for the minimap of this tile.
     * 
     * @return the identifier of the color value for this tile
     */
    public int getMapColor() {
        final Tile localTile = tile;
        if (localTile == null) {
            return 0;
        }
        return localTile.getMapColor();
    }

    /**
     * Get the cost of moving over this item. Needed was walking patch
     * calculations.
     * 
     * @return the costs for moving over this tile
     */
    public int getMovementCost() {
        final Tile localTile = tile;
        if (localTile == null) {
            return Integer.MAX_VALUE;
        }
        return localTile.getMovementCost();
    }

    /**
     * Get the particle pool of this tile that is used to handle particles of
     * this tile.
     * 
     * @return the particle pool of this map tile
     */
    public ParticlePool getParticlePool() {
        EffectPool localPool = tilePool;
        if (localPool == null) {
            localPool = EffectPool.getInstance(this);
            localPool.setScreenPos(loc, Layers.EFFECTS);
            tilePool = localPool;
        }
        return localPool;
    }

    /**
     * Get the ID of the tile.
     * 
     * @return the ID of the tile
     */
    public int getTileId() {
        return tileId;
    }

    /**
     * Get the ID of the background music track that is supposed to be played on
     * this tile.
     * 
     * @return the background music track for this tile
     */
    public int getTileMusic() {
        return musicId;
    }

    /**
     * Get the item on the top of this tile.
     * 
     * @return the top tile or <code>null</code> in case there is none
     */
    public Item getTopItem() {
        final List<Item> localItems = items;
        if (localItems != null) {
            final int top = localItems.size() - 1;
            if (top >= 0) {
                return localItems.get(top);
            }
        }
        return null;
    }

    /**
     * Check if there is anything on the tile that blocks moving over the tile.
     * 
     * @return true in case it is not possible to walk over this tile
     */
    public boolean isBlocked() {
        if (isObstacle()) {
            return true;
        }

        // check for chars
        if (Game.getPeople().getCharacterAt(loc) != null) {
            return true;
        }

        return false;
    }

    /**
     * Get the hidden flag of the tile.
     * 
     * @return true if the tile is hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Check if the tile is obstacle or if there is a item that blocks the
     * movement over this tile.
     * 
     * @return true if the tile is obstacle
     */
    public boolean isObstacle() {
        final Tile localTile = tile;
        if (localTile == null) {
            return true;
        }

        boolean obstacle = localTile.isObstacle();

        // check items
        final List<Item> localItems = items;
        if (localItems != null) {
            final int count = localItems.size();
            Item item;
            for (int i = 0; i < count; i++) {
                item = localItems.get(i);
                if (item.isObstacle()) {
                    return true;
                } else if (item.isJesus()) {
                    obstacle = false;
                }
            }
        }

        return obstacle;
    }

    /**
     * Get the obstructed flag of the tile.
     * 
     * @return true if the tile is obstructed
     */
    public boolean isObstructed() {
        return obstructed;
    }

    /**
     * Check if the tile is fully opaque and everything below can be hidden.
     * 
     * @return <code>true</code> in case the tile is opaque and everything below
     *         is hidden entirely.
     */
    public boolean isOpaque() {
        final Tile localTile = tile;
        if (localTile == null) {
            return false;
        }
        return localTile.isOpapue();
    }

    /**
     * Requesting the lookat informations for a tile from the server.
     */
    @Override
    public void lookAt() {
        final LookatTileCmd cmd =
            (LookatTileCmd) CommandFactory.getInstance().getCommand(
                CommandList.CMD_LOOKAT_TILE);
        cmd.setPosition(loc);
        cmd.send();

    }

    /**
     * Open a container on the tile or walk to the the tile.
     */
    @Override
    public void openContainer() {
        if (canOpenContainer()) {
            // final int sc =
            // Gui.getInstance().getContainers()
            // .requestShowcase(Containers.NONE);
            // final OpenMapCmd cmd =
            // (OpenMapCmd) CommandFactory.getInstance().getCommand(
            // CommandList.CMD_OPEN_MAP);
            // cmd.setShowcase(sc);
            // cmd.setPosition(loc);
            // cmd.send();
        }
    }

    /**
     * Recycle the map tile and prepare it for reuse.
     */
    @Override
    public void recycle() {
        if (loc != null) {
            loc.recycle();
            loc = null;
        }
        GameFactory.getInstance().recycle(this);
    }

    /**
     * Remove the reference to the particle pool of this tile.
     */
    public void removeParticlePool() {
        tilePool = null;
    }

    /**
     * Remove the item at the top position of the item stack.
     */
    @SuppressWarnings("nls")
    public void removeTopItem() {
        final List<Item> localItems = items;
        if (localItems == null) {
            LOGGER.warn("error: remove top item on empty field");
            return;
        }
        final int pos = localItems.size() - 1;
        if (pos < 0) {
            LOGGER.warn("error: remove top item on empty field");
            return;
        }

        // supporting item was removed
        if ((elevation > 0) && (elevationIndex == pos)) {
            elevation = 0;
            elevationIndex = 0;
        }

        localItems.get(pos).recycle();
        localItems.remove(pos);
        // enable numbers for next top item
        if ((pos - 1) >= 0) {
            localItems.get(pos - 1).enableNumbers(true);
        } else {
            FastTable.recycle(items);
            items = null;
        }
        itemChanged();
    }

    /**
     * Clear the tile and recycle it. This also recycles all item and light
     * sources on this tile.
     */
    @Override
    public void reset() {
        // recycle tile data
        if (tile != null) {
            tile.recycle();
        }
        tile = null;
        tileId = 0;

        // recycle item data
        if (items != null) {
            final int count = items.size();
            for (int i = 0; i < count; i++) {
                items.get(i).recycle();
            }
            items.clear();
            FastTable.recycle(items);
            items = null;
        }

        elevation = 0;
        elevationIndex = 0;
        hidden = false;
        obstructed = false;

        lightValue = 0;

        if (lightSrc != null) {
            Game.getLights().remove(lightSrc);
            LightSource.releaseLight(lightSrc);
            lightSrc = null;
        }

        light.resetColor();
        tmpLight.resetColor();

        if (tilePool != null) {
            tilePool.clearPool();
        }

        if (hoverMarker != null) {
            hoverMarker.makeInvisible();
            hoverMarker.recycle();
        }

        // clear possible markers
        // Gui.getInstance().getManager().removeMarker(InputHandler.MARK_MAP,
        // this);
        // Gui.getInstance().getManager().removeMarker(InputHandler.MARK_ITEM,
        // this);
    }

    /**
     * Set the hidden flag for tiles that are hidden to show building interiors.
     * Hidden tiles are completely hidden.
     * 
     * @param hide true if the tile shall be hidden
     */
    public void setHidden(final boolean hide) {
        if (hidden == hide) {
            return;
        }
        setInvisible(hide, obstructed);
        hidden = hide;
    }

    /**
     * Enable or disable a hover on this map tile.
     * 
     * @param hover true to enable the hover, false to disable it
     */
    @Override
    public void setHover(final boolean hover) {
        if (hover) {
            if (hoverMarker == null) {
                final Marker sel = Marker.create(MarkerFactory.MAP_SELECT);
                sel.setScreenPos(loc, Layers.MARKER);
                hoverMarker = sel;
                sel.show();
                Game.getDisplay().add(sel);
            } else {
                hoverMarker.show();
            }
        } else if (hoverMarker != null) {
            hoverMarker.hide();
            hoverMarker = null;
        }
    }

    /**
     * Set the obstructed flag for tiles that are obstructed by upper levels.
     * 
     * @param obstruct true if the tile shall be marked as obstructed
     */
    public void setObstructed(final boolean obstruct) {
        if (obstructed == obstruct) {
            return;
        }
        setInvisible(hidden, obstruct);
        obstructed = obstruct;
    }

    /**
     * Set the ID of the tile and change the type of the tile this way. This
     * function also sets up a new tile at this position if there was no.
     * Furthermore all calculations that are needed for a new tile are triggered
     * by this function.
     * 
     * @param id the new ID if the tile
     */
    public void setTileId(final int id) {
        losDirty = true;
        resetLight();

        // no replacement necessary
        if ((tileId == id) && (tile != null)) {
            tile.setScreenPos(loc, Layers.TILE);
            return;
        }

        tileId = id;

        // free old tile to factory
        if (tile != null) {
            tile.recycle();
            tile = null;
        }

        // get a new tile to display
        if (id >= 0) {
            // create a tile, possibly with variants
            tile = Tile.create(id, loc);

            tile.addAlphaChangeListener(this);
            tile.setScreenPos(loc, Layers.TILE);
            tile.setLight(light);
            tile.show();
        }
    }

    /**
     * Show a graphical effect on the tile.
     * 
     * @param effectId the ID of the effect
     */
    public void showEffect(final int effectId) {
        final Effect effect = Effect.create(effectId);
        effect.show(loc);
    }

    /**
     * Pick a item and put it into the bag. TODO: This function need to stack
     * the items into the back correctly
     */
    public void takeItem() {
        // final Reference from = dragFrom(0, 0);
        // if (from == null) {
        // return;
        // }

        // final int free =
        // ((Inventory) Gui.getInstance().getContainers()
        // .getContainer(Containers.INVENTORY)).getFree();
        // if (free >= 0) {
        // from.dragTo(new RefInventory(free));
        // }
        // if (Gui.getInstance().getContainers().getContainer(Containers.RIGHT)
        // .isOpen()) {
        // from.dragTo(new RefContainer(Containers.RIGHT, 1));
        // } else {
        // if (Gui.getInstance().getContainers()
        // .getContainer(Containers.LEFT).isOpen()) {
        // from.dragTo(new RefContainer(Containers.LEFT, 1));
        // }
        // }
    }

    /**
     * Create a string that identifies the tile and its current state.
     * 
     * @return the generated string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "MapTile " + loc.toString() + " tile=" + tileId + " items="
            + (items != null ? items.size() : 0);
    }

    /**
     * Update the entire item list. This function is called by the net interface
     * in case the server sends a full set of new item data to the client.
     * 
     * @param itemNumber Amount of items within the list of items
     * @param itemId List of the item IDs for all items that shall be created
     * @param itemCount List of count values for all items
     */
    public void updateItems(final int itemNumber, final TIntArrayList itemId,
        final TShortArrayList itemCount) {
        updateItemList(itemNumber, itemId, itemCount);
        itemChanged();
    }

    /**
     * Use a item on the map.
     * 
     * @return the reference to the use event
     */
    @Override
    public AbstractReference useItem() {
        // create usage object
        final MapReference use = new MapReference();
        use.setReferringTile(this);
        // add item id to detect special items
        if (items != null) {
            final int top = items.size() - 1;
            if (top >= 0) {
                // use.setItemId(items.get(top).getId());
            }
        }

        return use;
    }

    /**
     * Add some light influence to this tile. This is added to the already
     * existing light on this tile
     * 
     * @param color the light that shall be added
     */
    protected void addLight(final SpriteColor color) {
        tmpLight.add(color);
    }

    /**
     * Render the light on this tile, using the ambient light of the weather and
     * a factor how much the tile light modifies the ambient light.
     * 
     * @param factor the factor how much the ambient light is modified by the
     *            tile light
     * @param ambientLight the ambient light from the weather
     */
    protected void renderLight(final float factor,
        final SpriteColor ambientLight) {
        tmpLight.multiply(factor);
        tmpLight.add(ambientLight);
        light.set(tmpLight);
    }

    /**
     * Reset the light value back to 0.
     */
    protected void resetLight() {
        tmpLight.resetColor();
    }

    /**
     * Update a map tile using the update data the server send.
     * 
     * @param update the update data the server send
     */
    protected void update(final TileUpdate update) {
        // update tile
        setTileId(update.getTileId());

        musicId = update.getTileMusic();

        // update items
        updateItemList(update.getItemNumber(), update.getItemId(),
            update.getItemCount());

        itemChanged();
    }

    /**
     * Determine whether the top item is a light source and needs to be
     * registered. Also removes previous or changed light sources.
     */
    private void checkLight() {
        // light sources are only on player level
        if (!Game.getPlayer().isBaseLevel(loc)) {
            return;
        }

        int newLightValue = 0;

        final List<Item> localItems = items;
        if (localItems != null) {
            final int count = localItems.size();
            Item item;
            for (int i = 0; i < count; i++) {
                item = localItems.get(i);
                if (item.isLight()) {
                    newLightValue = item.getItemLight();
                    break;
                }
            }
        }

        if (lightValue == newLightValue) {
            return;
        }

        if (lightSrc != null) {
            Game.getLights().remove(lightSrc);
            LightSource.releaseLight(lightSrc);
            lightSrc = null;
        }

        if (newLightValue > 0) {
            lightSrc = LightSource.createLight(loc, newLightValue);
            Game.getLights().add(lightSrc);
        }

        lightValue = newLightValue;
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

        if (items == null) {
            return;
        }

        Item item;
        final List<Item> localItems = items;
        final int amount = localItems.size() - itemNumber;
        for (int i = 0; i < amount; i++) {
            // recylce the removed items
            item = localItems.get(itemNumber);
            item.recycle();

            // keep deleting in the same place as the list becomes shorter
            localItems.remove(itemNumber);
        }

        if (localItems.isEmpty()) {
            FastTable.recycle(items);
            items = null;
        }
    }

    /**
     * Notify the tile that the items got changed. That makes a recalculation of
     * the lights and the line of sight needed.
     */
    private void itemChanged() {
        // invalidate LOS data
        losDirty = true;
        // report a change of shadow
        Game.getLights().notifyChange(loc);
        // check for a light source
        checkLight();
    }

    /**
     * Adjust visiblitiy to match hidden and obstructed flag.
     * 
     * @param hide the target hide flag
     * @param obstruct the target obstruct flag
     * @see illarion.client.world.MapTile#setHidden(boolean)
     * @see illarion.client.world.MapTile#setObstructed(boolean)
     */
    private void setInvisible(final boolean hide, final boolean obstruct) {
        final boolean showOld = !hidden && !obstructed;
        final boolean show = !hide && !obstruct;

        if (show != showOld) {
            if (show) {
                if (tile != null) {
                    tile.show();
                }

                final List<Item> localItems = items;
                if (localItems != null) {
                    final int count = localItems.size();
                    for (int i = 0; i < count; i++) {
                        localItems.get(i).show();
                    }
                }
            } else {
                if (tile != null) {
                    tile.hide();
                }

                final List<Item> localItems = items;
                if (localItems != null) {
                    final int count = localItems.size();
                    for (int i = 0; i < count; i++) {
                        localItems.get(i).hide();
                    }
                }
            }
        }
    }

    /**
     * Set a item at a special position of the item stack on this tile.
     * 
     * @param index The index within the item list of this tile
     * @param itemId The new item ID of the item
     * @param itemCount The new count value of this item
     */
    @SuppressWarnings("nls")
    private void setItem(final int index, final int itemId, final int itemCount) {
        Item item = null;
        // look for present item in map tile
        List<Item> localItems = items;
        if (localItems != null) {
            if (index < localItems.size()) {
                item = localItems.get(index);
                // just an update of present item
                if (item.getId() == itemId) {
                    updateItem(item, itemCount, index);
                } else {
                    // different item: clear old item
                    item.recycle();
                    item = null;

                    // carrying item was removed
                    if (index == elevationIndex) {
                        elevation = 0;
                        elevationIndex = 0;
                    }
                }
            }
        } else {
            localItems = FastTable.newInstance();
            items = (FastTable<Item>) localItems;
        }
        // add a new item
        if (item == null) {
            // create new item
            item = Item.create(itemId, loc);
            item.setLight(light);

            updateItem(item, itemCount, index);
            // display on screen
            item.show();

            // add it to list
            if (index < items.size()) {
                localItems.set(index, item);
            } else if (index == items.size()) { // extend list by 1 row
                localItems.add(item);
            } else { // index mismatch
                throw new IllegalArgumentException(
                    "update behind end of items list");
            }
        }
        // termporarily disable all numbers
        item.enableNumbers(false);
    }

    /**
     * Update a single item with new data.
     * 
     * @param item the item that shall be updated
     * @param itemCount the count value of the new item
     * @param index the index of the item within the stack of items on this tile
     */
    private void updateItem(final Item item, final int itemCount,
        final int index) {
        // set number
        item.setCount(itemCount);
        // calculate offset from items carrying other items
        int objectOffset = 0;
        if ((elevation > 0) && (index > elevationIndex)) {
            objectOffset = elevation;
        }
        // position on tile with increasing z-order
        item.setScreenPos(loc.getDcX(), loc.getDcY() + objectOffset,
            loc.getDcZ() - index, Layers.ITEM);

        // set the elevation for items that can carry
        final int level = item.getLevel();
        if (level > 0) {
            // Set elevation only for first suitable item
            if ((elevation == 0) || (elevationIndex == index)) {
                elevation = level;
                elevationIndex = index;
            }
        }
    }

    /**
     * Update all items on the stack of this tile at once.
     * 
     * @param number the new amount of items on this tile
     * @param itemId the list of item ids for the items on this tile
     * @param itemCount the list of count values for the items on this tile
     */
    private void updateItemList(final int number, final TIntArrayList itemId,
        final TShortArrayList itemCount) {
        clampItems(number);
        for (int i = 0; i < number; i++) {
            setItem(i, itemId.get(i), itemCount.get(i));
        }

        // enable numbers for top item
        if (items != null) {
            final int pos = items.size() - 1;
            if (pos >= 0) {
                items.get(pos).enableNumbers(true);
            }
        }
    }
}
