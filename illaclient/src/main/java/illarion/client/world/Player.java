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

import illarion.client.gui.DialogType;
import illarion.client.net.client.RequestAppearanceCmd;
import illarion.client.util.ChatLog;
import illarion.client.world.items.*;
import illarion.client.world.movement.Movement;
import illarion.common.graphics.Layer;
import illarion.common.types.CharacterId;
import illarion.common.types.ServerCoordinate;
import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import illarion.common.util.FastMath;
import org.illarion.engine.Engine;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Main Class for the player controlled character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
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
    public static final String CFG_SOUND_ON = "soundOn"; //$NON-NLS-1$

    /**
     * The key in the configuration for the sound volume value.
     */
    @Nonnull
    public static final String CFG_SOUND_VOL = "soundVolume"; //$NON-NLS-1$

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
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

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
     * The player movement handler that takes care that the player character is walking around.
     */
    @Nonnull
    private final Movement movementHandler;
    /**
     * The path to the folder of character specific stuff, like the map or the names table.
     */
    @Nonnull
    private final Path path;
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
    private final Map<Integer, ItemContainer> containers;
    /**
     * This lock is used to synchronize the access on the containers.
     */
    @Nonnull
    private final ReadWriteLock containerLock;
    /**
     * The chat log instance that takes care for logging the text that is spoken in the game.
     */
    @Nonnull
    private final ChatLog chatLog;
    @Nonnull
    private final CarryLoad carryLoad;
    /**
     * The current location of the server map for the player.
     */
    @Nullable
    private ServerCoordinate playerLocation;
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
     * The merchant dialog that is currently open.
     */
    @Nullable
    private MerchantList merchantDialog;

    /**
     * Default constructor for the player.
     *
     * @param charName the character name of the player playing this game
     */
    public Player(@Nonnull Engine engine, @Nonnull String charName) {
        Path userDir = DirectoryManager.getInstance().getDirectory(Directory.User);
        path = userDir.resolve(charName);
        chatLog = new ChatLog(path);
        carryLoad = new CarryLoad();

        character = new Char();
        validLocation = false;

        try {
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create directory for user data.", e);
        }

        character.setName(charName);

        combatHandler = new CombatHandler();
        movementHandler = new Movement(this, engine.getInput(), World.getMapDisplay());
        inventory = new Inventory();
        containers = new HashMap<>();
        containerLock = new ReentrantReadWriteLock();
    }

    public void openMerchantDialog(int dialogId, @Nonnull String title, @Nonnull Collection<MerchantItem> items) {
        MerchantList list = new MerchantList(dialogId);
        items.forEach(list::addItem);

        MerchantList oldList = merchantDialog;
        merchantDialog = list;

        if (oldList != null) {
            closeDialog(oldList.getId(), EnumSet.of(DialogType.Merchant));
        }

        if (World.getGameGui().isReady()) {
            World.getGameGui().getDialogMerchantGui().showMerchantDialog(dialogId, title, items);
            World.getGameGui().getContainerGui().updateMerchantOverlay();
            World.getGameGui().getInventoryGui().updateMerchantOverlay();
        }
    }

    public void closeDialog(int dialogId, @Nonnull Collection<DialogType> dialogTypes) {
        if ((merchantDialog != null) && dialogTypes.contains(DialogType.Merchant)) {
            if (dialogId == merchantDialog.getId()) {
                MerchantList oldList = merchantDialog;
                merchantDialog = null;
                oldList.closeDialog();
                if (World.getGameGui().isReady()) {
                    World.getGameGui().getInventoryGui().updateMerchantOverlay();
                    World.getGameGui().getContainerGui().updateMerchantOverlay();
                }
            }
        }

        if (World.getGameGui().isReady()) {
            World.getGameGui().getDialogGui().closeDialog(dialogId, dialogTypes);
        }
    }

    @Nonnull
    public ItemContainer getOrCreateContainer(int id, @Nonnull String title, @Nonnull String description,
                                              int slotCount) {
        if (hasContainer(id)) {
            ItemContainer container = getContainer(id);
            if (container == null) {
                throw new IllegalStateException(
                        "Has container with ID but can't receive it. Internal state corrupted.");
            }
            if (container.getSlotCount() == slotCount) {
                return container;
            } else {
                LOGGER.error("Received container event for existing container but without fitting slot count!");
                removeContainer(id);

            }
        }
        return createNewContainer(id, title, description, slotCount);
    }

    /**
     * Check if there is currently a container with the specified ID open.
     *
     * @param id the ID of the container
     * @return {@code true} in case this container exists
     */
    public boolean hasContainer(int id) {
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
    public ItemContainer getContainer(int id) {
        containerLock.readLock().lock();
        try {
            return containers.get(id);
        } finally {
            containerLock.readLock().unlock();
        }
    }

    /**
     * Remove a container that is assigned to a specified key. This also removes the container from the GUI.
     *
     * @param id the key of the parameter to remove
     */
    public void removeContainer(int id) {
        synchronized (containers) {
            if (containers.containsKey(id)) {
                containers.remove(id);
            }
        }
        World.getGameGui().getContainerGui().closeContainer(id);
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
    public ItemContainer createNewContainer(int id, @Nonnull String title, @Nonnull String description, int slotCount) {
        containerLock.writeLock().lock();
        try {
            if (containers.containsKey(id)) {
                throw new IllegalArgumentException("Can't create item container that already exists.");
            }
            ItemContainer newContainer = new ItemContainer(id, title, description, slotCount);
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
    @Contract(pure = true)
    public Char getCharacter() {
        return character;
    }

    /**
     * Get the chat log that is active for this player.
     *
     * @return the chat log of this player
     */
    @Nonnull
    @Contract(pure = true)
    public ChatLog getChatLog() {
        return chatLog;
    }

    /**
     * Get the inventory of the player.
     *
     * @return the player inventory
     */
    @Nonnull
    @Contract(pure = true)
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get the current location of the character.
     *
     * @return The current location of the character
     */
    @Nonnull
    @Contract(pure = true)
    public ServerCoordinate getLocation() {
        if (playerLocation == null) {
            throw new IllegalStateException("The location of the player is not yet set.");
        }
        return playerLocation;
    }

    /**
     * Change the location of the character. This will instantly change the position on the map where the client is
     * shown. Calling this function is a result of any warp requested by the server. Calling this function will
     * subsequently cause all other components of the client to be updated according to the new location.
     *
     * @param newLoc new location of the character on the map
     */
    public void setLocation(@Nonnull ServerCoordinate newLoc) {
        validLocation = true;
        if (Objects.equals(playerLocation, newLoc)) {
            return;
        }

        boolean isLongRange = false;
        if (playerLocation == null) {
            isLongRange = true;
        } else {
            if (playerLocation.getDistance(newLoc) > 10) {
                isLongRange = true;
            }
            if (FastMath.abs(playerLocation.getZ() - newLoc.getZ()) > 3) {
                isLongRange = true;
            }
        }

        if (isLongRange) {
            World.getMapDisplay().setActive(false);
            World.getMap().clear();
        }

        // set logical location
        updateLocation(newLoc);
        character.setLocation(newLoc);
        character.stopAnimation();
        character.resetAnimation(true);
        World.getPlayer().getCombatHandler().standDown();
        World.getMapDisplay().setLocation(newLoc.toDisplayCoordinate(Layer.Chars));

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
    public void updateLocation(@Nonnull ServerCoordinate newLoc) {
        if (Objects.equals(playerLocation, newLoc)) {
            return;
        }

        LOGGER.debug("Setting player location to: {}", newLoc);

        playerLocation = newLoc;
        World.getMusicBox().updatePlayerLocation();
        World.getMap().getMiniMap().setPlayerLocation(newLoc);
        World.getMap().updateAllTiles();
        World.getMap().checkInside();
    }

    /**
     * Get the instance of the combat handler.
     *
     * @return the combat handler of this player
     */
    @Nonnull
    @Contract(pure = true)
    public CombatHandler getCombatHandler() {
        return combatHandler;
    }

    /**
     * Get the movement handler of the player character that allows controls the movement of the player.
     *
     * @return the movement handler
     */
    @Nonnull
    @Contract(pure = true)
    public Movement getMovementHandler() {
        return movementHandler;
    }

    /**
     * Get the path to the player directory.
     *
     * @return The path to the player directory
     */
    @Nonnull
    @Contract(pure = true)
    public Path getPath() {
        return path;
    }

    /**
     * Get the merchant list.
     *
     * @return the merchant list
     */
    @Nullable
    @Contract(pure = true)
    public MerchantList getMerchantList() {
        return merchantDialog;
    }

    /**
     * Get the current ID of the players character.
     *
     * @return The ID of the player character
     */
    @Nullable
    @Contract(pure = true)
    public CharacterId getPlayerId() {
        return playerId;
    }

    /**
     * Set the ID of the players character. This also causes a introducing of the player character to the rest of the
     * client, so the Chat box for example is able to show the name of the character without additional affords.
     *
     * @param newPlayerId the new ID of the player
     */
    public void setPlayerId(@Nonnull CharacterId newPlayerId) {
        playerId = newPlayerId;
        character.setCharId(playerId);

        World.getNet().sendCommand(new RequestAppearanceCmd(newPlayerId));
    }

    /**
     * Check if a location is at the same level as the player.
     *
     * @param checkLoc The location that shall be checked
     * @return true if the location is at the same level as the player
     */
    @Contract(pure = true)
    boolean isBaseLevel(@Nonnull ServerCoordinate checkLoc) {
        return (playerLocation != null) && (playerLocation.getZ() == checkLoc.getZ());
    }

    /**
     * Check how good the player character is able to see the target character.
     *
     * @param chara The character that is checked
     * @return the visibility of the character in percent
     */
    @Contract(pure = true)
    public float canSee(@Nonnull Char chara) {
        if (isPlayer(chara.getCharId())) {
            return Char.VISIBILITY_MAX;
        }
        MapTile tile = World.getMap().getMapAt(chara.getVisibleLocation());
        if ((tile == null) || tile.isHidden()) {
            return 0;
        }
        return Char.VISIBILITY_MAX;
    }

    /**
     * Check if a ID is the ID of the player.
     *
     * @param checkId the ID to be checked
     * @return true if it is the player, false if not
     */
    @Contract(value = "null -> false", pure = true)
    public boolean isPlayer(@Nullable CharacterId checkId) {
        return (playerId != null) && playerId.equals(checkId);
    }

    /**
     * Check if the player is setup and ready.
     * <p />
     * As long as this returns {@code false}, the player did not receive it's ID yet.
     *
     * @return {@code true} in case the player is properly set up.
     */
    @Contract(pure = true)
    public boolean isPlayerIdSet() {
        return playerId != null;
    }

    /**
     * Check if the location of the character is set.
     *
     * @return {@code true} in case the location is set.
     */
    @Contract(pure = true)
    public boolean isLocationSet() {
        return playerLocation != null;
    }

    /**
     * Get the map level the player character is currently standing on.
     *
     * @return The level the player character is currently standing on
     */
    @Contract(pure = true)
    public int getBaseLevel() {
        return playerLocation == null ? 0 : playerLocation.getZ();
    }

    /**
     * Check if there is currently a merchant list active.
     *
     * @return {@code true} in case a merchant list is active
     */
    @Contract(pure = true)
    public boolean hasMerchantList() {
        return merchantDialog != null;
    }

    @Contract(pure = true)
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
    @Contract(pure = true)
    public boolean isOnScreen(@Nonnull ServerCoordinate testLoc, int tolerance) {
        if (playerLocation == null) {
            throw new IllegalStateException("The player location is not yet set.");
        }
        if (Math.abs(testLoc.getZ() - playerLocation.getZ()) > 2) {
            return false;
        }
        int width = MapDimensions.getInstance().getStripesWidth() >> 1;
        int height = MapDimensions.getInstance().getStripesHeight() >> 1;
        int limit = (Math.max(width, height) + tolerance) - 2;

        return (Math.abs(playerLocation.getX() - testLoc.getX()) +
                Math.abs(playerLocation.getY() - testLoc.getY())) < limit;
    }

    /**
     * Then this instance of player is removed its content needs to removed correctly as well.
     */
    public void shutdown() {
        character.markAsRemoved();
        movementHandler.shutdown();
    }

    @Nonnull
    public CarryLoad getCarryLoad() {
        return carryLoad;
    }
}
