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

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import illarion.client.IllaClient;
import illarion.client.Login;
import illarion.client.graphics.Avatar;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.RequestAppearanceCmd;
import illarion.client.net.server.events.DialogMerchantReceivedEvent;
import illarion.client.net.server.events.OpenContainerEvent;
import illarion.client.util.Lang;
import illarion.client.world.characters.CharacterAttribute;
import illarion.client.world.events.CloseDialogEvent;
import illarion.client.world.items.Inventory;
import illarion.client.world.items.ItemContainer;
import illarion.client.world.items.MerchantList;
import illarion.common.config.Config;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.config.ConfigSystem;
import illarion.common.types.CharacterId;
import illarion.common.util.Bresenham;
import illarion.common.util.DirectoryManager;
import illarion.common.util.Location;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.bushe.swing.event.annotation.EventTopicPatternSubscriber;
import org.newdawn.slick.openal.SoundStore;

import java.awt.*;
import java.io.File;

/**
 * Main Class for the player controlled character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class Player {
    /**
     * The key in the configuration for the music on/off flag.
     */
    private static final String CFG_MUSIC_ON = "musicOn"; //$NON-NLS-1$

    /**
     * The key in the configuration for the music volume value.
     */
    private static final String CFG_MUSIC_VOL = "musicVolume"; //$NON-NLS-1$

    /**
     * The key in the configuration for the sound on/off flag.
     */
    private static final String CFG_SOUND_ON = "soundOn"; //$NON-NLS-1$

    /**
     * The key in the configuration for the sound volume value.
     */
    private static final String CFG_SOUND_VOL = "soundVolume"; //$NON-NLS-1$

    /**
     * Maximal distance to visible objects.
     */
    private static final int CLIP_DISTANCE = 14;

    /**
     * Maximal value for the volume of the sound.
     */
    public static final float MAX_CLIENT_VOL = 100.f;

    /**
     * The maximum range between 2 characters, that can look at each other.
     */
    private static final int MAXIMUM_LOOKING_AT_RANGE = 6;

    /**
     * The minimum range between 2 characters, that can look at each other.
     */
    private static final int MINIMUM_LOOKING_AT_RANGE = 2;

    /**
     * Average value for the perception attribute.
     */
    private static final int PERCEPTION_AVERAGE = 8;

    /**
     * Share of one perception point on the coverage of a tile.
     */
    private static final int PERCEPTION_COVER_SHARE = 4;

    /**
     * The player specific configuration of the client.
     */
    private final ConfigSystem cfg;

    /**
     * The graphical representation of the character.
     */
    private final Char character;

    /**
     * The inventory of the player.
     */
    private final Inventory inventory;

    /**
     * The current location of the server map for the player.
     */
    private final Location loc = new Location();

    /**
     * The player movement handler that takes care that the player character is walking around.
     */
    private final PlayerMovement movementHandler;

    /**
     * The name of the players character.
     */
    private final String name;

    /**
     * The path to the folder of character specific stuff, like the map or the names table.
     */
    private final File path;

    /**
     * The character ID of the player.
     */
    private CharacterId playerId;

    /**
     * This flag is changed to {@code true} once the location of the player was set once.
     */
    private boolean validLocation;

    /**
     * This map contains the containers that are known for the player.
     */
    private final TIntObjectHashMap<ItemContainer> containers;

    /**
     * The merchant dialog that is currently open.
     */
    private MerchantList merchantDialog;

    /**
     * Constructor for the player that receives the character name from the login data automatically.
     */
    public Player() {
        this(Login.getInstance().getSelectedCharacterName());
    }

    /**
     * Default constructor for the player.
     *
     * @param newName the character name of the player playing this game
     */
    @SuppressWarnings("nls")
    public Player(final String newName) {
        name = newName;

        path = new File(DirectoryManager.getInstance().getUserDirectory(), name);

        character = Char.create();
        validLocation = false;

        if (!path.isDirectory() && !path.mkdir()) {
            IllaClient.fallbackToLogin(Lang.getMsg("error.character_settings"));
            cfg = null;
            movementHandler = null;
            inventory = null;

            containers = null;
            return;
        }

        cfg = new ConfigSystem(new File(path, "Player.xml.gz"));
        character.setName(name);
        // character.setVisible(Char.VISIBILITY_MAX);
        World.getPeople().setPlayerCharacter(character);

        // followed = null;
        movementHandler = new PlayerMovement(this);
        inventory = new Inventory();
        containers = new TIntObjectHashMap<ItemContainer>();

        updateListener();
        AnnotationProcessor.process(this);
    }

    @EventSubscriber
    public void onOpenContainerEvent(final OpenContainerEvent event) {
        final ItemContainer container = getContainer(event.getContainerId());
        final TIntObjectIterator<OpenContainerEvent.Item> itr = event.getItemIterator();
        while (itr.hasNext()) {
            itr.advance();
            container.setItem(itr.key(), itr.value().getItemId(), itr.value().getCount());
        }
    }

    @EventSubscriber
    public void onMerchantDialogOpenedEvent(final DialogMerchantReceivedEvent event) {
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
    public void onDialogClosedEvent(final CloseDialogEvent event) {
        if (merchantDialog == null) {
            return;
        }

        switch (event.getDialogType()) {
            case Any:
            case Merchant:
                if (event.getDialogId() == merchantDialog.getId()) {
                    final MerchantList oldList = merchantDialog;
                    merchantDialog = null;
                    oldList.closeDialog();
                }

            case Message:
                break;
            case Input:
                break;
        }
    }

    /**
     * The monitor function that is notified in case the configuration changes and triggers the required updates.
     */
    @EventTopicPatternSubscriber(topicPattern = "((music)|(sound))((On)|(Volume))")
    public void onConfigChangedEvent(final String topic, final ConfigChangedEvent event) {
        updateListener();
    }

    /**
     * Get the container that is assigned to the ID. This either creates a new container or opens and existing one.
     *
     * @param id the ID of the container
     * @return the container assigned to this ID or a newly created container
     */
    public ItemContainer getContainer(final int id) {
        synchronized (containers) {
            if (containers.containsKey(id)) {
                return containers.get(id);
            }
            final ItemContainer newContainer = new ItemContainer(id);
            containers.put(id, newContainer);
            return newContainer;
        }
    }

    /**
     * Get the merchant list.
     *
     * @return the merchant list
     */
    public MerchantList getMerchantList() {
        return merchantDialog;
    }

    /**
     * Check if there is currently a merchant list active.
     *
     * @return {@code true} in case a merchant list is active
     */
    public boolean hasMerchantList() {
        return merchantDialog != null;
    }

    /**
     * Remove a container that is assigned to a specified key.
     *
     * @param id the key of the parameter to remove
     */
    public void removeContainer(final int id) {
        synchronized (containers) {
            if (containers.containsKey(id)) {
                containers.remove(id);
            }
        }
    }

    /**
     * Check how good the player character is able to see the target character.
     *
     * @param chara The character that is checked
     * @return the visibility of the character in percent
     */
    public int canSee(final Char chara) {
        if (isPlayer(chara.getCharId())) {
            return Char.VISIBILITY_MAX;
        }

        int visibility = Char.VISIBILITY_MAX;
        final Avatar avatar = chara.getAvatar();
        if (avatar != null) {
            visibility = avatar.getVisibility();
        }
        visibility += chara.getVisibilityBonus();

        return getVisibility(chara.getLocation(), visibility);
    }

    /**
     * Get the map level the player character is currently standing on.
     *
     * @return The level the player character is currently standing on
     */
    public int getBaseLevel() {
        return loc.getScZ();
    }

    /**
     * Get the configuration interface for the character specific settings.
     *
     * @return the configuration interface
     */
    public Config getCfg() {
        return cfg;
    }

    /**
     * Get the graphical representation of the players character.
     *
     * @return The character of the player
     */
    public Char getCharacter() {
        return character;
    }

    /**
     * Get the location in the front of the character.
     *
     * @return The location right in the front of the character
     * @deprecated Better use {@link #getFrontLocation(Location)} to avoid the creation of too many new objects
     */
    @Deprecated
    public Location getFrontLocation() {
        final Location front = new Location();
        getFrontLocation(front);

        return front;
    }

    /**
     * Get the location in the front of the character.
     *
     * @param targetLoc the front location of the character is stored in this location object
     */
    public void getFrontLocation(final Location targetLoc) {
        targetLoc.set(loc);
        targetLoc.moveSC(character.getDirection());
    }

    /**
     * Get the inventory of the player.
     *
     * @return the player inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get the current location of the character.
     *
     * @return The current location of the character
     */
    public Location getLocation() {
        return loc;
    }

    /**
     * Get the movement handler of the player character that allows controls the movement of the player.
     *
     * @return the movement handler
     */
    public PlayerMovement getMovementHandler() {
        return movementHandler;
    }

    /**
     * Get the path to the player directory.
     *
     * @return The path to the player directory
     */
    public File getPath() {
        return path;
    }

    /**
     * Get the current ID of the players character.
     *
     * @return The ID of the player character
     */
    public CharacterId getPlayerId() {
        return playerId;
    }

    /**
     * Get the visibility of a target location for the players character.
     *
     * @param targetLoc The location that is checked for visibility
     * @param limit     The maximum value for the visibility
     * @return The visibility of the target location
     */
    private int getVisibility(final Location targetLoc, final int limit) {
        // target is at same level or above char
        final boolean visible = targetLoc.getScZ() <= character.getLocation().getScZ();
        // calculate line-of-sight
        if (visible && (character.getLocation().getDistance(targetLoc) < 30)) {
            // calculate intervening fields.
            // note that the order of fields may be inverted
            final Bresenham line = Bresenham.getInstance();
            line.calculate(loc, targetLoc);
            // examine line without start and end point
            final int length = line.getLength() - 1;
            MapTile tile = null;
            final GameMap map = World.getMap();
            final Point point = new Point();
            int coverage = World.getWeather().getVisiblity() - ((getCharacter().getAttribute(CharacterAttribute.Perception) - PERCEPTION_AVERAGE) *
                    PERCEPTION_COVER_SHARE);
            // skip tile the character is standing on
            for (int i = 1; i < length; i++) {
                line.getPoint(i, point);
                tile = map.getMapAt(point.x, point.y, loc.getScZ());
                if (tile != null) {
                    coverage += tile.getCoverage();

                    if (coverage >= limit) {
                        return 0;
                    }
                }
                // include distance in calulation 1% per tile
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

    public boolean hasValidLocation() {
        return validLocation;
    }

    /**
     * Check if a location is at the same level as the player.
     *
     * @param checkLoc The location that shall be checked
     * @return true if the location is at the same level as the player
     */
    boolean isBaseLevel(final Location checkLoc) {
        return loc.getScZ() == checkLoc.getScZ();
    }

    /**
     * Check if the player character is currently looking at a specified character.
     *
     * @param chara The character the player character could look at
     * @return true if the player character is looking at the character
     */
    public boolean isLookingAt(final Char chara) {
        final int dist = Math.abs(character.getDirection() - getLocation().getDirection(chara.getLocation()));
        return (dist < MINIMUM_LOOKING_AT_RANGE) || (dist > MAXIMUM_LOOKING_AT_RANGE);
    }

    /**
     * Determines whether the character is currently moving.
     *
     * @return <code>true</code> if the character is moving
     * @deprecated directly get the information from the movement handler
     */
    @Deprecated
    public boolean isMoving() {
        return movementHandler.isMoving();
    }

    /**
     * Check of a position in server coordinates is on the screen of the player.
     *
     * @param testLoc   The location that shall be checked.
     * @param tolerance an additional tolerance added to the default clipping distance
     * @return true if the position is within the clipping distance and the tolerance
     */
    public boolean isOnScreen(final Location testLoc, final int tolerance) {
        final int limit = CLIP_DISTANCE + tolerance;
        return loc.getDistance(testLoc) < limit;
    }

    /**
     * Check if a ID is the ID of the player.
     *
     * @param checkId the ID to be checked
     * @return true if it is the player, false if not
     */
    public boolean isPlayer(final CharacterId checkId) {
        return playerId.equals(checkId);
    }

    /**
     * Change the location of the character. This will instantly change the position on the map where the client is
     * shown. Calling this function is a result of any warp requested by the server.
     *
     * @param newLoc new location of the character on the map
     */
    public void setLocation(final Location newLoc) {
        validLocation = true;
        if (loc.equals(newLoc)) {
            return;
        }

        final boolean levelChange = newLoc.getScZ() != loc.getScZ();

        // set logical location
        movementHandler.cancelAutoWalk();
        loc.set(newLoc);
        character.setLocation(newLoc);

        // clear away invisible characters
        if (levelChange) {
            World.getPeople().clear();
        } else {
            World.getPeople().clipCharacters();
        }
    }

    /**
     * Set the ID of the players character. This also causes a introducing of the player character to the rest of the
     * client, so the chat box for example is able to show the name of the character without additional affords.
     *
     * @param newPlayerId the new ID of the player
     */
    public void setPlayerId(final CharacterId newPlayerId) {
        playerId = newPlayerId;
        character.setCharId(playerId);

        final RequestAppearanceCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_REQUEST_APPEARANCE,
                RequestAppearanceCmd.class);
        cmd.request(newPlayerId);
        cmd.send();

        World.getPeople().introduce(playerId, name);

    }

    /**
     * Then this instance of player is removed its content needs to removed correctly as well.
     */
    public void shutdown() {
        World.getPeople().setPlayerCharacter(null);
        character.recycle();
        movementHandler.shutdown();
    }

    /**
     * Update the sound listener of this player.
     */
    private static void updateListener() {
        if (IllaClient.getCfg().getBoolean(CFG_SOUND_ON)) {
            final float effVol = IllaClient.getCfg().getInteger(CFG_SOUND_VOL) / MAX_CLIENT_VOL;
            SoundStore.get().setSoundsOn(true);
            SoundStore.get().setSoundVolume(effVol);
        } else {
            SoundStore.get().setSoundsOn(false);
        }

        if (IllaClient.getCfg().getBoolean(CFG_MUSIC_ON)) {
            final float musVol = IllaClient.getCfg().getInteger(CFG_MUSIC_VOL) / MAX_CLIENT_VOL;
            SoundStore.get().setMusicOn(true);
            SoundStore.get().setMusicVolume(musVol);
        } else {
            SoundStore.get().setMusicOn(false);
        }
    }

    /**
     * Update the location that is bound to this player. This does not have any side effects but the location of the
     * player and the sound listener changed.
     *
     * @param newLoc the new location of the player
     */
    public void updateLocation(final Location newLoc) {
        if (loc.equals(newLoc)) {
            return;
        }

        loc.set(newLoc);
    }
}
