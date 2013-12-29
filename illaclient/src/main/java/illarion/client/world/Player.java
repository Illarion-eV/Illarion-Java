/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import illarion.client.IllaClient;
import illarion.client.Login;
import illarion.client.graphics.Avatar;
import illarion.client.net.client.RequestAppearanceCmd;
import illarion.client.net.server.events.DialogMerchantReceivedEvent;
import illarion.client.net.server.events.OpenContainerEvent;
import illarion.client.util.ChatLog;
import illarion.client.util.Lang;
import illarion.client.world.characters.CharacterAttribute;
import illarion.client.world.events.CloseDialogEvent;
import illarion.client.world.items.Inventory;
import illarion.client.world.items.ItemContainer;
import illarion.client.world.items.MerchantList;
import illarion.common.types.CharacterId;
import illarion.common.types.Location;
import illarion.common.util.Bresenham;
import illarion.common.util.DirectoryManager;
import illarion.common.util.FastMath;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.engine.Engine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Main Class for the player controlled character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@SuppressWarnings("ClassNamingConvention")
@ThreadSafe
public final class Player {
    /**
     * Maximal value for the volume of the sound.
     */
    public static final float MAX_CLIENT_VOL = 100.f;
    /**
     * The key in the configuration for the sound on/off flag.
     */
    @Nonnull
    private static final String CFG_SOUND_ON = "soundOn"; //$NON-NLS-1$

    /**
     * The key in the configuration for the sound volume value.
     */
    @Nonnull
    private static final String CFG_SOUND_VOL = "soundVolume"; //$NON-NLS-1$

    /**
     * Average value for the perception attribute.
     */
    private static final int PERCEPTION_AVERAGE = 8;

    /**
     * Share of one perception point on the coverage of a tile.
     */
    private static final int PERCEPTION_COVER_SHARE = 4;

    /**
     * The instance of the logger used by this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Player.class);

    /**
     * The graphical representation of the character.
     */
    @Nonnull
    private final Char character;

    /**
     * The inventory of the player.
     */
    @Nonnull
    private final Inventory inventory;

    /**
     * The current location of the server map for the player.
     */
    @Nonnull
    private final Location playerLocation = new Location();

    /**
     * The player movement handler that takes care that the player character is walking around.
     */
    @Nonnull
    private final PlayerMovement movementHandler;

    /**
     * The path to the folder of character specific stuff, like the map or the names table.
     */
    @Nonnull
    private final File path;

    /**
     * The character ID of the player.
     */
    @Nullable
    private CharacterId playerId;

    /**
     * This flag is changed to {@code true} once the location of the player was set once.
     */
    private boolean validLocation;

    /**
     * The instance of the combat handler that maintains the attack targets of this player.
     */
    @Nonnull
    private final CombatHandler combatHandler;

    /**
     * This map contains the containers that are known for the player.
     */
    @Nonnull
    @GuardedBy("containerLock")
    private final TIntObjectHashMap<ItemContainer> containers;

    /**
     * This lock is used to synchronize the access on the containers.
     */
    @Nonnull
    private final ReadWriteLock containerLock;

    /**
     * The merchant dialog that is currently open.
     */
    @Nullable
    private MerchantList merchantDialog;

    /**
     * The chat log instance that takes care for logging the text that is spoken in the game.
     */
    @Nonnull
    private final ChatLog chatLog;

    /**
     * Constructor for the player that receives the character name from the login data automatically.
     */
    public Player(@Nonnull final Engine engine) {
        this(engine, Login.getInstance().getLoginCharacter());
    }

    /**
     * Default constructor for the player.
     *
     * @param charName the character name of the player playing this game
     */
    @SuppressWarnings("nls")
    public Player(@Nonnull final Engine engine, @Nonnull final String charName) {
        path = new File(DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User), charName);
        chatLog = new ChatLog(path);

        character = new Char();
        validLocation = false;

        if (!path.isDirectory() && !path.mkdir()) {
            IllaClient.fallbackToLogin(Lang.getMsg("error.character_settings"));
            throw new IllegalStateException("Failed to init player.");
        }

        character.setName(charName);

        combatHandler = new CombatHandler();
        movementHandler = new PlayerMovement(engine.getInput(), this);
        inventory = new Inventory();
        containers = new TIntObjectHashMap<>();
        containerLock = new ReentrantReadWriteLock();

        AnnotationProcessor.process(this);
    }

    /**
     * Remove a container that is assigned to a specified key. This also removes the container from the GUI.
     *
     * @param id the key of the parameter to remove
     */
    public void removeContainer(final int id) {
        synchronized (containers) {
            if (containers.containsKey(id)) {
                containers.remove(id);
            }
        }
        World.getGameGui().getContainerGui().closeContainer(id);
    }

    @EventSubscriber
    public void onDialogClosedEvent(@Nonnull final CloseDialogEvent event) {
        if (merchantDialog == null) {
            return;
        }

        if (event.isClosingDialogType(CloseDialogEvent.DialogType.Merchant)) {
            if (event.getDialogId() == merchantDialog.getId()) {
                final MerchantList oldList = merchantDialog;
                merchantDialog = null;
                oldList.closeDialog();
            }
        }
    }

    @EventSubscriber
    public void onMerchantDialogOpenedEvent(@Nonnull final DialogMerchantReceivedEvent event) {
        final MerchantList list = new MerchantList(event.getId(), event.getItemCount());
        for (int i = 0; i < event.getItemCount(); i++) {
            list.setItem(i, event.getItem(i));
        }

        final MerchantList oldList = merchantDialog;
        merchantDialog = list;

        if (oldList != null) {
            EventBus.publish(new CloseDialogEvent(oldList.getId(), CloseDialogEvent.DialogType.Merchant));
        }
    }

    @EventSubscriber
    public void onOpenContainerEvent(@Nonnull final OpenContainerEvent event) {
        final ItemContainer container;
        final int slotCount = event.getSlotCount();
        if (hasContainer(event.getContainerId())) {
            container = getContainer(event.getContainerId());
            if (container == null) {
                throw new IllegalStateException(
                        "Has container with ID but can't receive it. " + "Internal state corrupted.");
            }
            if (container.getSlotCount() != slotCount) {
                LOGGER.error("Received container event for existing container but without fitting slot count!");
                removeContainer(event.getContainerId());
                EventBus.publish(event);
                return;
            }
        } else {
            container = createNewContainer(event.getContainerId(), slotCount);
        }

        final boolean[] updatedSlot = new boolean[slotCount];
        Arrays.fill(updatedSlot, false);

        final TIntObjectIterator<OpenContainerEvent.Item> itr = event.getItemIterator();
        while (itr.hasNext()) {
            itr.advance();
            updatedSlot[itr.key()] = true;
            container.setItem(itr.key(), itr.value().getItemId(), itr.value().getCount());
        }

        for (int i = 0; i < slotCount; i++) {
            if (!updatedSlot[i]) {
                container.setItem(i, null, null);
            }
        }

        World.getGameGui().getContainerGui().showContainer(container);
    }

    /**
     * Check if there is currently a container with the specified ID open.
     *
     * @param id the ID of the container
     * @return {@code true} in case this container exists
     */
    public boolean hasContainer(final int id) {
        containerLock.readLock().lock();
        try {
            return containers.containsKey(id);
        } finally {
            containerLock.readLock().unlock();
        }
    }

    /**
     * Get the container that is assigned to the ID. This either creates a new container or opens and existing one.
     *
     * @param id the ID of the container
     * @return the container assigned to this ID or a newly created container
     */
    @Nullable
    public ItemContainer getContainer(final int id) {
        containerLock.readLock().lock();
        try {
            return containers.get(id);
        } finally {
            containerLock.readLock().unlock();
        }
    }

    /**
     * Create a new item container instance.
     *
     * @param id the ID of the container
     * @param slotCount the amount of slots the new container is supposed to have
     * @return the new item container
     * @throws IllegalArgumentException in case there is already a container with the same ID
     */
    @Nonnull
    public ItemContainer createNewContainer(final int id, final int slotCount) {
        containerLock.writeLock().lock();
        try {
            if (containers.containsKey(id)) {
                throw new IllegalArgumentException("Can't create item container that already exists.");
            }
            final ItemContainer newContainer = new ItemContainer(id, slotCount);
            containers.put(id, newContainer);
            return newContainer;
        } finally {
            containerLock.writeLock().unlock();
        }
    }

    /**
     * Get the graphical representation of the players character.
     *
     * @return The character of the player
     */
    @Nonnull
    public Char getCharacter() {
        return character;
    }

    /**
     * Get the chat log that is active for this player.
     *
     * @return the chat log of this player
     */
    @Nonnull
    public ChatLog getChatLog() {
        return chatLog;
    }

    /**
     * Get the instance of the combat handler.
     *
     * @return the combat handler of this player
     */
    @Nonnull
    public CombatHandler getCombatHandler() {
        return combatHandler;
    }

    /**
     * Get the inventory of the player.
     *
     * @return the player inventory
     */
    @Nonnull
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get the current location of the character.
     *
     * @return The current location of the character
     */
    @Nonnull
    public Location getLocation() {
        return playerLocation;
    }

    /**
     * Get the movement handler of the player character that allows controls the movement of the player.
     *
     * @return the movement handler
     */
    @Nonnull
    public PlayerMovement getMovementHandler() {
        return movementHandler;
    }

    /**
     * Get the path to the player directory.
     *
     * @return The path to the player directory
     */
    @Nonnull
    public File getPath() {
        return path;
    }

    /**
     * Get the merchant list.
     *
     * @return the merchant list
     */
    @Nullable
    public MerchantList getMerchantList() {
        return merchantDialog;
    }

    /**
     * Get the current ID of the players character.
     *
     * @return The ID of the player character
     */
    @Nullable
    public CharacterId getPlayerId() {
        return playerId;
    }

    /**
     * Check if a location is at the same level as the player.
     *
     * @param checkLoc The location that shall be checked
     * @return true if the location is at the same level as the player
     */
    boolean isBaseLevel(@Nonnull final Location checkLoc) {
        return getLocation().getScZ() == checkLoc.getScZ();
    }

    /**
     * Check how good the player character is able to see the target character.
     *
     * @param chara The character that is checked
     * @return the visibility of the character in percent
     */
    public int canSee(@Nonnull final Char chara) {
        if (isPlayer(chara.getCharId())) {
            return Char.VISIBILITY_MAX;
        }

        int visibility = Char.VISIBILITY_MAX;
        final Avatar avatar = chara.getAvatar();
        if (avatar != null) {
            visibility = avatar.getTemplate().getAvatarInfo().getVisibility();
        }
        visibility += chara.getVisibilityBonus();

        return getVisibility(chara.getLocation(), visibility);
    }

    /**
     * Check if a ID is the ID of the player.
     *
     * @param checkId the ID to be checked
     * @return true if it is the player, false if not
     */
    public boolean isPlayer(@Nullable final CharacterId checkId) {
        return (playerId != null) && playerId.equals(checkId);
    }

    /**
     * Get the visibility of a target location for the players character.
     *
     * @param targetLoc The location that is checked for visibility
     * @param limit The maximum value for the visibility
     * @return The visibility of the target location
     */
    private int getVisibility(@Nonnull final Location targetLoc, final int limit) {
        // target is at same level or above char
        final boolean visible = targetLoc.getScZ() <= character.getLocation().getScZ();
        // calculate line-of-sight
        if (visible && (character.getLocation().getDistance(targetLoc) < 30)) {
            // calculate intervening fields.
            // note that the order of fields may be inverted
            final Bresenham line = Bresenham.getInstance();
            try {
                line.calculate(playerLocation, targetLoc);
            } catch (@Nonnull final IllegalStateException ex) {
                return 0;
            }
            // examine line without start and end point
            final int length = line.getLength() - 1;
            final GameMap map = World.getMap();
            final Point point = new Point();
            int coverage = World.getWeather().getVisiblity() -
                    ((getCharacter().getAttribute(CharacterAttribute.Perception) - PERCEPTION_AVERAGE) *
                            PERCEPTION_COVER_SHARE);
            // skip tile the character is standing on
            for (int i = 1; i < length; i++) {
                line.getPoint(i, point);
                final MapTile tile = map.getMapAt(point.x, point.y, playerLocation.getScZ());
                if (tile != null) {
                    coverage += tile.getCoverage();

                    if (coverage >= limit) {
                        return 0;
                    }
                }
                // include distance in calculation 1% per tile
                coverage++;
            }
            final int quality = limit - coverage;
            if (quality > (Char.VISIBILITY_MAX / 2)) {
                return Char.VISIBILITY_MAX;
            }
            return quality * 2;
        }
        return 0;
    }

    /**
     * Get the map level the player character is currently standing on.
     *
     * @return The level the player character is currently standing on
     */
    public int getBaseLevel() {
        return playerLocation.getScZ();
    }

    /**
     * Check if there is currently a merchant list active.
     *
     * @return {@code true} in case a merchant list is active
     */
    public boolean hasMerchantList() {
        return merchantDialog != null;
    }

    public boolean hasValidLocation() {
        return validLocation;
    }

    /**
     * Check of a position in server coordinates is on the screen of the player.
     *
     * @param testLoc The location that shall be checked.
     * @param tolerance an additional tolerance added to the default clipping distance
     * @return true if the position is within the clipping distance and the tolerance
     */
    public boolean isOnScreen(@Nonnull final Location testLoc, final int tolerance) {
        if (Math.abs(testLoc.getScZ() - playerLocation.getScZ()) > 2) {
            return false;
        }
        final int width = MapDimensions.getInstance().getStripesWidth() >> 1;
        final int height = MapDimensions.getInstance().getStripesHeight() >> 1;
        final int limit = (Math.max(width, height) + tolerance) - 2;

        return (Math.abs(playerLocation.getScX() - testLoc.getScX()) +
                Math.abs(playerLocation.getScY() - testLoc.getScY())) < limit;
    }

    /**
     * Change the location of the character. This will instantly change the position on the map where the client is
     * shown. Calling this function is a result of any warp requested by the server. Calling this function will
     * subsequently cause all other components of the client to be updated according to the new location.
     *
     * @param newLoc new location of the character on the map
     */
    public void setLocation(@Nonnull final Location newLoc) {
        validLocation = true;
        if (playerLocation.equals(newLoc)) {
            return;
        }

        boolean isLongRange = false;
        if (playerLocation.getSqrtDistance(newLoc) > 4) {
            isLongRange = true;
        }
        if (FastMath.abs(playerLocation.getScZ() - newLoc.getScZ()) > 3) {
            isLongRange = true;
        }

        if (isLongRange) {
            World.getMapDisplay().setActive(false);
            World.getMap().clear();
        }

        // set logical location
        movementHandler.cancelAutoWalk();
        updateLocation(newLoc);
        character.setLocation(newLoc);
        character.stopAnimation();
        movementHandler.stopAnimation();
        World.getPlayer().getCombatHandler().standDown();
        World.getMapDisplay().setLocation(newLoc);

        if (isLongRange) {
            World.getPlayer().getCharacter().relistLight();
        }
        World.getPlayer().getCharacter().updateLight(newLoc);

        if (isLongRange) {
            World.getPeople().clear();
        } else {
            World.getMap().checkInside();
            World.getPeople().clipCharacters();
        }
    }

    /**
     * Update the location that is bound to this player. This does not have any side effects but the location of the
     * player and the sound listener changed.
     *
     * @param newLoc the new location of the player
     */
    public void updateLocation(@Nonnull final Location newLoc) {
        if (playerLocation.equals(newLoc)) {
            return;
        }

        playerLocation.set(newLoc);
        World.getMusicBox().updatePlayerLocation();
        World.getMap().updateAllTiles();
    }

    /**
     * Set the ID of the players character. This also causes a introducing of the player character to the rest of the
     * client, so the Chat box for example is able to show the name of the character without additional affords.
     *
     * @param newPlayerId the new ID of the player
     */
    public void setPlayerId(@Nonnull final CharacterId newPlayerId) {
        playerId = newPlayerId;
        character.setCharId(playerId);

        World.getNet().sendCommand(new RequestAppearanceCmd(newPlayerId));
    }

    /**
     * Then this instance of player is removed its content needs to removed correctly as well.
     */
    public void shutdown() {
        character.markAsRemoved();
        movementHandler.shutdown();
    }
}
