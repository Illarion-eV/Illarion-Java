/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import com.google.common.base.Strings;
import illarion.client.graphics.AnimatedMove;
import illarion.client.graphics.Avatar;
import illarion.client.graphics.AvatarClothManager.AvatarClothGroup;
import illarion.client.graphics.MoveAnimation;
import illarion.client.resources.ItemFactory;
import illarion.client.util.Lang;
import illarion.client.world.characters.CharacterAttribute;
import illarion.client.world.interactive.InteractiveChar;
import illarion.common.graphics.CharAnimations;
import illarion.common.graphics.Layer;
import illarion.common.types.*;
import illarion.common.util.FastMath;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.ImmutableColor;
import org.illarion.engine.graphic.LightSource;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a character: player, monster or npc.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("ClassNamingConvention")
@NotThreadSafe
public final class Char implements AnimatedMove {
    /**
     * The speed a animation runs with on default.
     */
    public static final int DEFAULT_ANIMATION_SPEED = 750;
    /**
     * Light Update status SET light value.
     */
    public static final int LIGHT_SET = 1;
    /**
     * Light Update status SOFTly change value.
     */
    public static final int LIGHT_SOFT = 2;
    /**
     * Light Update status UPDATE light value.
     */
    public static final int LIGHT_UPDATE = 3;
    /**
     * Maximum value for visibility.
     */
    public static final float VISIBILITY_MAX = 1.f;
    /**
     * The color that is used to show dead characters.
     */
    @Nonnull
    private static final Color DEAD_COLOR;
    /**
     * The instance of the logger that is used to write out the data.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(Char.class);
    /**
     * Maximal scale value for the character.
     */
    private static final float MAXIMAL_SCALE = 1.2f;
    /**
     * Minimal scale value for the character.
     */
    private static final float MINIMAL_SCALE = 0.8f;
    /**
     * This color is used to display the name in case the character is a player character.
     */
    @Nonnull
    private static final Color NAME_COLOR_HUMAN = Color.YELLOW;

    /**
     * This color is used to display the name in case the character is a monster.
     */
    @Nonnull
    private static final Color NAME_COLOR_MONSTER = Color.RED;

    /**
     * This color is used to display the name in case the character is a NPC.
     */
    @Nonnull
    private static final Color NAME_COLOR_NPC = new Color(128, 179, 255);

    static {
        DEAD_COLOR = new Color(1.f, 1.f, 1.f, 0.45f);
    }

    /**
     * Move animation handler for this character.
     */
    @Nonnull
    private final MoveAnimation move;
    /**
     * A list of items this avatar wears. This list is send to the avatar at a update.
     */
    @Nonnull
    private final Map<AvatarClothGroup, Integer> wearItems = new EnumMap<>(AvatarClothGroup.class);
    /**
     * A list of modified colors of the stuff a avatar wears.
     */
    @Nonnull
    private final Map<AvatarClothGroup, Color> wearItemsColors = new EnumMap<>(AvatarClothGroup.class);
    /**
     * This map stores the attribute values of this character.
     */
    @Nonnull
    private final Map<CharacterAttribute, Integer> attributes;
    @Nonnull
    private final AvatarId avatarId;
    /**
     * The alive state of the character. {@code true} in case the character is alive.
     */
    private boolean alive;
    private int currentAvatarId;

    /**
     * Avatar of the character.
     */
    @Nullable
    private Avatar avatar;

    /**
     * Character ID the the character.
     */
    @Nullable
    private CharacterId charId;

    @Nullable
    private DisplayCoordinate displayPos;
    /**
     * Last visibility value that was shown. Used for fading in and out animations.
     */
    private int lastVisibility;
    /**
     * Current light source of the character.
     */
    @Nullable
    private LightSource lightSrc;
    /**
     * Current light value of the character.
     */
    private int lightValue;
    /**
     * Current Location of the character on the map.
     */
    @Nullable
    private ServerCoordinate location;
    /**
     * Name of the character.
     */
    @Nullable
    private String name;
    @Nullable
    private String customName;
    /**
     * Color of the name of the character. default, melee fighting, distance fighting, magic
     */
    @Nullable
    private Color nameColor;
    /**
     * Scale of the character (based on its height).
     */
    private float scale;
    /**
     * The custom color of the characters skin.
     */
    @Nullable
    private Color skinColor;
    /**
     * Visibility bonus of the character (for large characters).
     */
    private int visibilityBonus;
    /**
     * The reference to the interactive character instance that points to this character.
     */
    @Nullable
    private Reference<InteractiveChar> interactiveCharRef;
    /**
     * This stores a delayed move in case there is one.
     */
    @Nullable
    private DelayedMoveData delayedMove;
    /**
     * This flag is used to store if there is currently a animation in progress.
     */
    private boolean animationInProgress;
    /**
     * Once this value is turned {@code true} the character is removed from the game.
     */
    private boolean removedCharacter;

    /**
     * Constructor to create a new character.
     */
    public Char() {
        move = new MoveAnimation(this);

        attributes = new EnumMap<>(CharacterAttribute.class);

        scale = 0;
        avatarId = new AvatarId(-1, 0, Direction.North, CharAnimations.STAND);
        currentAvatarId = -1;
    }

    private static void applyPaperdollingItem(@Nullable Avatar avatar, @Nonnull AvatarClothGroup group,
                                              @Nullable Integer itemId) {
        int realItemId = (itemId == null) ? 0 : itemId;
        applyPaperdollingItem(avatar, group, realItemId);
    }

    private static void applyPaperdollingItem(@Nullable Avatar avatar, @Nonnull AvatarClothGroup group, int itemId) {
        if (avatar != null) {
            if (itemId == 0) {
                avatar.removeClothItem(group);
            } else {
                avatar.setClothItem(group, itemId);
            }
        }
    }

    @Nonnull
    @Contract(pure = true)
    private static DisplayCoordinate getDisplayCoordinatesAt(@Nonnull ServerCoordinate coordinate) {
        int elevation = World.getMap().getElevationAt(coordinate);
        int x = coordinate.toDisplayX();
        int y = coordinate.toDisplayY() - elevation;
        int layer = coordinate.toDisplayLayer(Layer.Chars);

        return new DisplayCoordinate(x, y, layer);
    }

    /**
     * Get a specified attribute.
     *
     * @param attribute the attribute to fetch
     * @return the value of the attribute
     */
    public int getAttribute(@Nonnull CharacterAttribute attribute) {
        if (removedCharacter) {
            log.warn("Fetching the attributes of a removed character.");
        }
        if (attributes.containsKey(attribute)) {
            Integer value = attributes.get(attribute);
            if (value != null) {
                return value;
            }
        }
        return 0;
    }

    /**
     * Set a attribute to a new value.
     *
     * @param attribute the attribute value to update
     * @param value the new value of the attribute
     */
    public void setAttribute(@Nonnull CharacterAttribute attribute, int value) {
        if (removedCharacter) {
            return;
        }

        attributes.put(attribute, value);

        if (attribute == CharacterAttribute.HitPoints) {
            if (avatar != null) {
                avatar.setHealthPoints(value);
            }
            setAlive(value > 0);
        }

        if (World.getGameGui().isReady() && World.getPlayer().isPlayer(getCharId())) {
            World.getGameGui().getPlayerStatusGui().setAttribute(attribute, value);
        }
    }

    /**
     * Get the character ID of the character.
     *
     * @return the ID of the character
     */
    @Nullable
    public CharacterId getCharId() {
        return charId;
    }

    /**
     * Change the ID of the character.
     *
     * @param newCharId new ID of the character
     */
    @SuppressWarnings("IfStatementWithTooManyBranches")
    public void setCharId(@Nonnull CharacterId newCharId) {
        charId = newCharId;
        if (charId.isHuman()) {
            setNameColor(NAME_COLOR_HUMAN);
        } else if (charId.isNPC()) {
            setNameColor(NAME_COLOR_NPC);
        } else if (charId.isMonster()) {
            setNameColor(NAME_COLOR_MONSTER);
        } else {
            log.warn("Failed to detect character type for {}", charId);
        }
    }

    /**
     * Set the alive state of this character.
     *
     * @param newAliveState set the new alive state. {@code true} in case the character is alive.
     */
    public void setAlive(boolean newAliveState) {
        if (removedCharacter) {
            log.warn("Trying to update the alive state of a removed character.");
            return;
        }

        if (alive == newAliveState) {
            return;
        }

        alive = newAliveState;

        if (avatar == null) {
            return;
        }

        if (alive) {
            avatar.changeBaseColor(skinColor);
            wearItemsColors.forEach(avatar::changeClothColor);
        } else {
            avatar.changeBaseColor(DEAD_COLOR);

            //noinspection ConstantConditions
            Arrays.asList(AvatarClothGroup.values()).forEach(group -> avatar.changeClothColor(group, DEAD_COLOR));
        }
    }

    @Override
    public void animationStarted() {
        animationInProgress = true;
    }

    /**
     * Stop the walking animation of the character.
     *
     * @param finished not in use
     */
    @Override
    public void animationFinished(boolean finished) {
        if (location != null) {
            displayPos = getDisplayCoordinatesAt(location);
        }
        if (displayPos != null) {
            setPosition(displayPos);
        }
        animationInProgress = false;

        DelayedMoveData localDelayedMove = delayedMove;
        delayedMove = null;
        if (finished && (localDelayedMove != null)) {
            log.info("{}: Planning delayed move for execution", this);
            World.getUpdateTaskManager().addTaskForLater((container, delta) -> {
                log.info("{}: Executing delayed move", this);
                moveToInternal(localDelayedMove.targetLocation, localDelayedMove.mode, localDelayedMove.duration);
            });
        } else if (localDelayedMove != null) {
            World.getUpdateTaskManager().addTaskForLater((container, delta) -> {
                log.info("{}: Canceled move received while there still was a delayed more. Fixing the location.", this);
                updateLocation(localDelayedMove.targetLocation);
            });
        }
    }

    /**
     * Stop the execution of the current avatar animation.
     */
    public void stopAnimation() {
        if (avatar != null) {
            avatar.stopAnimation();
        }
        move.stop();
    }

    /**
     * Set the current animation back to its parent, update the avatar and invoke the needed animations.
     */
    public void resetAnimation(boolean finished) {
        if (delayedMove != null) {
            log.debug("{}: Resetting the animation skipped. There is a delayed move that may continue soon.", this);
        } else {
            log.debug("{}: Resetting the animation. Finished: {}", this, finished);
            if (finished) {
                avatarId.setAnimationId(CharAnimations.STAND);
                if (location != null) {
                    updateAvatar();
                    if (avatar != null) {
                        avatar.animate(DEFAULT_ANIMATION_SPEED, true);
                    }
                }
            }
        }
    }

    /**
     * Update the graphical appearance of the character.
     */
    private void updateAvatar() {
        ServerCoordinate currentCoordinate = location;
        if (currentCoordinate == null) {
            throw new IllegalStateException("Updating the avatar while the coordinates are not set is not valid.");
        }
        if (removedCharacter) {
            releaseAvatar();
            log.warn("{}: Trying to update the avatar of a removed avatar.", this);
            return;
        }

        // nothing to do for invisible folks
        if (avatarId.getAnimationId() < 0) {
            log.debug("{}: Can't show avatar with ID {}", this, avatarId);
            return;
        }

        // calculate avatar id
        //noinspection ConstantConditions
        int newAvatarId = avatarId.getAvatarId();

        // no change, return
        if ((currentAvatarId == newAvatarId) && (avatar != null)) {
            log.debug("{}: Avatar received update but did not change.", this);
            return;
        }

        int oldAlpha = 0;
        int oldAlphaTarget;

        if (avatar != null) {
            oldAlpha = avatar.getAlpha();
        }
        oldAlphaTarget = (int) (World.getPlayer().canSee(this) * Color.MAX_INT_VALUE);

        @Nullable Avatar newAvatar = Avatar.create(newAvatarId, this);

        if (newAvatar == null) {
            log.error("Failed to change the avatar as the new ID {} is NULL.", newAvatarId);
            return;
        }

        updatePaperdoll(newAvatar);

        if (alive) {
            wearItemsColors.forEach(newAvatar::changeClothColor);
            newAvatar.changeBaseColor(skinColor);
        } else {
            //noinspection ConstantConditions
            Arrays.asList(AvatarClothGroup.values()).forEach(group -> newAvatar.changeClothColor(group, DEAD_COLOR));
            newAvatar.changeBaseColor(DEAD_COLOR);
        }

        updatePosition(newAvatar, getDisplayCoordinatesAt(currentCoordinate));
        updateLight(newAvatar, LIGHT_SET);

        Integer healthPoints = attributes.get(CharacterAttribute.HitPoints);
        if (healthPoints == null) {
            newAvatar.setHealthPoints(10000);
        } else {
            newAvatar.setHealthPoints(healthPoints);
        }

        newAvatar.setScale(scale);
        newAvatar.setAlpha(oldAlpha);
        newAvatar.setAlphaTarget(oldAlphaTarget);
        newAvatar.setName(getName());

        if (nameColor != null) {
            newAvatar.setNameColor(nameColor);
        }

        newAvatar.show();
        log.debug("{}: Showing new avatar: {}", this, newAvatar);

        Avatar oldAvatar = avatar;
        currentAvatarId = newAvatarId;
        avatar = newAvatar;
        if (oldAvatar != null) {
            oldAvatar.markAsRemoved();
        }

        MapTile tile = World.getMap().getMapAt(currentCoordinate);
        if (tile != null) {
            tile.updateQuestMarkerElevation();
        }
    }

    /**
     * Update the paper doll, so set all items the characters wears to the avatar. Do this in case many cloth parts
     * changed or in case the avatar instance changed.
     *
     * @param avatar the avatar that is supposed to be updated
     */
    private void updatePaperdoll(@Nullable Avatar avatar) {
        if (removedCharacter) {
            log.warn("Trying to update the paperdoll of a removed character.");
            return;
        }
        if (avatar == null) {
            return;
        }

        Integer firstHandItem = wearItems.get(AvatarClothGroup.FirstHand);
        Integer secondHandItem = wearItems.get(AvatarClothGroup.SecondHand);
        if (hasWearingItem(avatar, AvatarClothGroup.FirstHand, firstHandItem) ||
            hasWearingItem(avatar, AvatarClothGroup.SecondHand, secondHandItem)) {
            applyPaperdollingItem(avatar, AvatarClothGroup.FirstHand, firstHandItem);
            applyPaperdollingItem(avatar, AvatarClothGroup.SecondHand, secondHandItem);
        } else {
            applyPaperdollingItem(avatar, AvatarClothGroup.FirstHand, secondHandItem);
            applyPaperdollingItem(avatar, AvatarClothGroup.SecondHand, firstHandItem);
        }

        //noinspection ConstantConditions
        Arrays.asList(AvatarClothGroup.values()).forEach(group -> {
            if ((group != AvatarClothGroup.FirstHand) && (group != AvatarClothGroup.SecondHand)) {
                applyPaperdollingItem(avatar, group, wearItems.get(group));
            }
        });
    }

    /**
     * Check if a cloth item is defined in a specified group.
     *
     * @param avatar the avatar to update
     * @param group the cloth group where the item shall be checked
     * @param id the id of the item that shall be checked
     * @return {@code true} in case a item is defined and displayable
     */
    public boolean hasWearingItem(@Nullable Avatar avatar, @Nonnull AvatarClothGroup group, int id) {
        return (id != 0) && ((avatar == null) || avatar.getTemplate().getClothes().doesClothExists(group, id));
    }

    private boolean hasWearingItem(@Nullable Avatar avatar, @Nonnull AvatarClothGroup group, @Nullable Integer id) {
        return hasWearingItem(avatar, group, (id == null) ? 0 : id);
    }

    private void applyLightValue(@Nullable ItemId itemId) {
        if (ItemId.isValidItem(itemId)) {
            int light = ItemFactory.getInstance().getTemplate(itemId.getValue()).getItemInfo().getLight();

            if (light > lightValue) {
                lightValue = light;
            }
        }
    }

    /**
     * Update the avatar display position.
     *
     * @param avatar the avatar that is altered
     */
    private void updatePosition(@Nullable Avatar avatar, @Nonnull DisplayCoordinate newPosition) {
        displayPos = newPosition;

        if (avatar != null) {
            avatar.setScreenPos(newPosition);
        }
    }

    /**
     * Update the light source of the character.
     *
     * @param avatar the avatar that is updated
     * @param mode the mode of the update
     */
    private void updateLight(@Nullable Avatar avatar, int mode) {
        if (removedCharacter) {
            log.error("Trying to update the light of a removed character.");
            return;
        }
        if (location == null) {
            return;
        }

        ServerCoordinate lightLoc = location;

        @Nullable MapTile tile = World.getMap().getMapAt(lightLoc);

        if ((avatar != null) && (tile != null)) {
            switch (mode) {
                case LIGHT_SET:
                    avatar.setLight(tile.getTargetLight());
                    break;
                case LIGHT_SOFT:
                    avatar.setLightTarget(tile.getTargetLight());
                    break;
                case LIGHT_UPDATE:
                    if (avatar.hasAnimatedLight()) {
                        avatar.setLightTarget(tile.getTargetLight());
                    } else {
                        avatar.setLight(tile.getTargetLight());
                    }
                    break;
                default:
                    log.warn("Wrong light update mode."); //$NON-NLS-1$
                    break;
            }
        }
    }

    /**
     * Mark this character as removed. Calling this function will cause the instance to clean its dependency and then
     * die gracefully.
     */
    public void markAsRemoved() {
        removedCharacter = true;

        move.stop();
        if (lightSrc != null) {
            World.getLights().remove(lightSrc);
            lightSrc = null;
        }

        releaseAvatar();
    }

    /**
     * Reset the cached light value to start sampling a new value.
     */
    public void resetLightValue() {
        lightValue = 0;
    }

    /**
     * Release the current avatar and free the resources.
     */
    private void releaseAvatar() {
        log.debug("{}: Releasing the avatar.", this);
        Avatar localAvatar = avatar;
        avatar = null;
        avatarId.setRaceId(-1);
        currentAvatarId = -1;
        if (localAvatar != null) {
            localAvatar.markAsRemoved();
        }
    }

    /**
     * Set the new position of the character.
     *
     * @param position the display position of the character
     */
    @Override
    public void setPosition(@Nonnull DisplayCoordinate position) {
        updatePosition(avatar, position);
    }

    /**
     * Set the color of the name of the character.
     *
     * @param color the new color value
     */
    private void setNameColor(@Nonnull Color color) {
        nameColor = color;

        Avatar avatar = this.avatar;
        if (avatar != null) {
            avatar.setNameColor(color);
        }
    }

    private void setAvatarName() {
        if (avatar != null) {
            avatar.setName(getName());
        }
    }

    /**
     * Set the scale of the character.
     *
     * @param newScale new scale value between 0.5f and 1.2f
     */
    public void setScale(float newScale) {
        if ((newScale < MINIMAL_SCALE) || (newScale > MAXIMAL_SCALE)) {
            log.warn("invalid character scale {} ignored for {}", newScale, charId);
        }

        scale = FastMath.clamp(newScale, MINIMAL_SCALE, MAXIMAL_SCALE);
        if (avatar != null) {
            avatar.setScale(newScale);
        }
    }

    /**
     * Get the current avatar of the character.
     *
     * @return the avatar of the character
     */
    @Nullable
    @Contract(pure = true)
    public Avatar getAvatar() {
        return avatar;
    }

    /**
     * Get the current direction the character is looking at.
     *
     * @return the direction value
     */
    @Nonnull
    @Contract(pure = true)
    public Direction getDirection() {
        return avatarId.getDirection();
    }

    /**
     * Change the direction the character is looking at.
     *
     * @param newDirection the new direction value
     */
    public void setDirection(@Nonnull Direction newDirection) {
        if (avatarId.getDirection() == newDirection) {
            log.debug("{}: Skipping direction change, because direction already matches: {}", this, newDirection);
            return;
        }
        log.debug("{}: Applying a new direction to the character: {}", this, newDirection);
        avatarId.setDirection(newDirection);

        // The update of the direction arrives before the location of the character is set. That is okay because
        // the update of the character will take the stored direction into account.
        World.getUpdateTaskManager().addTask((container, delta) -> {
            if (!move.isRunning() && (location != null)) {
                if (avatarId.getAnimationId() == CharAnimations.STAND) {
                    updateAvatar();
                } else {
                    resetAnimation(true);
                }
            }
        });
    }

    /**
     * Get a interactive reference to this character.
     *
     * @return a interactive reference to this character
     */
    @Nonnull
    public InteractiveChar getInteractive() {
        if (interactiveCharRef != null) {
            @Nullable InteractiveChar interactiveChar = interactiveCharRef.get();
            if (interactiveChar != null) {
                return interactiveChar;
            }
        }
        InteractiveChar interactiveChar = new InteractiveChar(this);
        interactiveCharRef = new SoftReference<>(interactiveChar);
        return interactiveChar;
    }

    /**
     * Get the location of the character where the character is currently visible.
     *
     * @return the location of the character
     */
    @Nullable
    @Contract(pure = true)
    public ServerCoordinate getVisibleLocation() {
        return location;
    }

    /**
     * Get the location of the character. This location tracks the changes applied by the server as close as possible.
     * The visible location may differ because the moves have not catched up.
     *
     * @return the location of the character
     */
    @Nullable
    @Contract(pure = true)
    public ServerCoordinate getLocation() {
        DelayedMoveData currentDelayedMove = delayedMove;
        if (currentDelayedMove != null) {
            return currentDelayedMove.targetLocation;
        }
        return location;
    }

    /**
     * Set the new location of the character.
     *
     * @param newLoc new location of the character
     */
    public void setLocation(@Nonnull ServerCoordinate newLoc) {
        if (removedCharacter) {
            log.warn("Trying to update the location of a removed character.");
            return;
        }
        CharacterId characterId = getCharId();
        if (characterId == null) {
            log.error("Trying to change the location of a character without a ID.");
            return;
        }

        if (updateLocation(newLoc)) {
            log.debug("{}: Setting character location to: {}", this, newLoc);
            setPosition(getDisplayCoordinatesAt(newLoc));
        }
    }

    /**
     * Get the name of the character.
     *
     * @return the name of the character
     */
    @Nonnull
    @Contract(pure = true)
    public String getName() {
        if (Strings.isNullOrEmpty(name)) {
            return Strings.isNullOrEmpty(customName) ? getFallbackName() : ('"' + customName + '"');
        } else {
            return Strings.isNullOrEmpty(customName) ? name : (name + " (" + customName + ')');
        }
    }

    /**
     * Set the name of the current character. Pre and suffixes are generated by this function as well
     *
     * @param newName the name of the character or null
     */
    public void setName(@Nullable String newName) {
        name = newName;
        setAvatarName();
    }

    @Nonnull
    @Contract(pure = true)
    private String getFallbackName() {
        String key;
        switch (avatarId.getRaceId()) {
            // humans
            case 0:
                if (avatarId.getTypeId() == 0) {
                    key = "character.name.fallback.human.male";
                } else {
                    key = "character.name.fallback.human.female";
                }
                break;

            // dwarfs
            case 1:
                if (avatarId.getTypeId() == 0) {
                    key = "character.name.fallback.dwarf.male";
                } else {
                    key = "character.name.fallback.dwarf.female";
                }
                break;

            // halflings
            case 2:
                if (avatarId.getTypeId() == 0) {
                    key = "character.name.fallback.halfling.male";
                } else {
                    key = "character.name.fallback.halfling.female";
                }
                break;

            // elves
            case 3:
                if (avatarId.getTypeId() == 0) {
                    key = "character.name.fallback.elf.male";
                } else {
                    key = "character.name.fallback.elf.female";
                }
                break;

            // orcs
            case 4:
                if (avatarId.getTypeId() == 0) {
                    key = "character.name.fallback.orc.male";
                } else {
                    key = "character.name.fallback.orc.female";
                }
                break;

            // lizards
            case 5:
                key = "character.name.fallback.lizard";
                break;

            // And everyone else
            default:
                key = "chat.someone";
                break;
        }
        return Lang.getMsg(key);
    }

    @Nullable
    @Contract(pure = true)
    public String getCustomName() {
        return customName;
    }

    public void setCustomName(@Nullable String customName) {
        this.customName = customName;
        setAvatarName();
    }

    /**
     * Get the visibility bonus value.
     *
     * @return visibility bonus value
     */
    @Contract(pure = true)
    public int getVisibilityBonus() {
        return visibilityBonus;
    }

    /**
     * Set the visibility bonus of this character.
     *
     * @param newVisibilityBonus the new visibility bonus value
     */
    public void setVisibilityBonus(int newVisibilityBonus) {
        if (removedCharacter) {
            log.warn("Trying to set the visibility bonus of a removed character.");
            return;
        }
        visibilityBonus = newVisibilityBonus;
    }

    /**
     * Check if the character is a human controlled character.
     *
     * @return {@code true} if the character is a human controlled character
     */
    @Contract(pure = true)
    public boolean isHuman() {
        return (charId != null) && charId.isHuman();
    }

    /**
     * Check if the character is a monster.
     *
     * @return true if the character is a monster, false if not.
     */
    @Contract(pure = true)
    public boolean isMonster() {
        return (charId != null) && charId.isMonster();
    }

    /**
     * Check if the character is a npc.
     *
     * @return true if the character is a npc, false if not.
     */
    @Contract(pure = true)
    public boolean isNPC() {
        return (charId != null) && charId.isNPC();
    }

    /**
     * Move the character to a new position with animation. This function takes absolute coordinates.
     *
     * @param newPos the target location of the move
     * @param mode the mode of the move
     * @param duration the duration of the animation in milliseconds
     */
    public void moveTo(@Nonnull ServerCoordinate newPos, @Nonnull CharMovementMode mode, int duration) {
        World.getUpdateTaskManager().addTask((container, delta) -> moveToInternal(newPos, mode, duration));
    }

    public void updateMoveDuration(int newDuration) {
        World.getUpdateTaskManager().addTask((container, delta) -> updateMoveDurationInternal(newDuration));
    }

    private void updateMoveDurationInternal(int newDuration) {
        if (move.isRunning()) {
            move.setDuration(newDuration);
            if (avatar != null) {
                avatar.changeAnimationDuration(newDuration);
            }
        }
    }

    private void moveToInternal(@Nonnull ServerCoordinate newPos, @Nonnull CharMovementMode mode, int duration) {
        if (mode == CharMovementMode.None) {
            return;
        }
        CharacterId characterId = getCharId();
        if (characterId == null) {
            log.error("Can't move a character without ID around.");
            return;
        }

        if (!World.getPlayer().isPlayer(characterId)) {
            if (mode == CharMovementMode.Push) {
                if (delayedMove != null) {
                    if (delayedMove.targetLocation.equals(newPos)) {
                        log.info("{}: Skipping push because there is a delayed move with the same target.", this);
                        return;
                    }
                } else {
                    if ((location != null) && location.equals(newPos)) {
                        log.info("{}: Skipping push because the character is already on the correct place.", this);
                        return;
                    }
                }
                log.info("{}: Executing push move right away.", this);
                resetAnimation(true);
            } else if (move.isRunning()) {
                if (delayedMove == null) {
                    delayedMove = new DelayedMoveData(mode, duration, newPos);
                    log.info("{}: Scheduled move for later execution: {}", this, delayedMove);
                    return;
                } else {
                    log.warn("{}: Can't delay the move. Spot is already taken. Executing now.", this);
                    resetAnimation(true);
                }
            }
        }

        delayedMove = null;

        if (move.isRunning()) {
            move.stop();
        }

        ServerCoordinate oldPos = location;
        if (!updateLocation(newPos)) {
            return;
        }

        // determine general visibility by players
        if (avatar != null) {
            // calculate movement direction
            Direction dir = (oldPos == null) ? null : oldPos.getDirection(newPos);

            // turn only when animating, not when pushed
            if ((mode != CharMovementMode.Push) && (dir != null)) {
                setDirection(dir);
            }

            // find target elevation

            int range = 1;
            if (mode == CharMovementMode.Run) {
                range = 2;
            }

            // start animations only if reasonable distance
            if ((oldPos == null) || (duration == 0) || (dir == null) || (mode == CharMovementMode.Push)) {
                // normal reasons for directly setting the avatar to the new location
                log.debug("{}: Setting avatar to {} without animation.", this, newPos);
                setPosition(getDisplayCoordinatesAt(newPos));
                animationFinished(true);
            } else if (newPos.getStepDistance(oldPos) > range) {
                // the locations are too far apart. Report and skip the animation anyway.
                log.warn("{}: Trying to walk from {} to {}: Out of range! Skipping animation.", this, oldPos, newPos);
                setPosition(getDisplayCoordinatesAt(newPos));
                animationFinished(true);
            } else {
                if (mode == CharMovementMode.Walk) {
                    startAnimation(CharAnimations.WALK, duration, true, dir.isDiagonal() ? FastMath.sqrt(2.f) : 1.f);
                } else if (mode == CharMovementMode.Run) {
                    startAnimation(CharAnimations.RUN, duration, true, dir.isDiagonal() ? FastMath.sqrt(2.f) : 1.f);
                }

                DisplayCoordinate oldDisplayPos = getDisplayCoordinatesAt(oldPos);
                DisplayCoordinate newDisplayPos = getDisplayCoordinatesAt(newPos);

                if (oldDisplayPos.getLayer() > newDisplayPos.getLayer()) {
                    oldDisplayPos =
                            new DisplayCoordinate(oldDisplayPos.getX(), oldDisplayPos.getY(), newDisplayPos.getLayer());
                } else if (oldDisplayPos.getLayer() < newDisplayPos.getLayer()) {
                    newDisplayPos =
                            new DisplayCoordinate(newDisplayPos.getX(), newDisplayPos.getY(), oldDisplayPos.getLayer());
                }

                move.start(oldDisplayPos, newDisplayPos, duration);
            }
            updateLight(LIGHT_SOFT);
        }

        if (oldPos != null) {
            MapTile oldTile = World.getMap().getMapAt(oldPos);
            if (oldTile != null) {
                oldTile.updateQuestMarkerElevation();
            }
        }

        MapTile newTile = World.getMap().getMapAt(newPos);
        if (newTile != null) {
            newTile.updateQuestMarkerElevation();
        }
    }

    private boolean updateLocation(@Nonnull ServerCoordinate newLocation) {
        if (newLocation.equals(location)) {
            return false;
        }
        ServerCoordinate oldCoordinates = location;
        location = newLocation;

        if (oldCoordinates == null) {
            updateAvatar();
            updateLight();
        }

        updateLight(location);
        return true;
    }

    /**
     * Change the position of the light source of the character and refresh the light.
     *
     * @param newLoc the new location of the light source
     */
    public void updateLight(@Nonnull ServerCoordinate newLoc) {
        LightSource localLightSource = lightSrc;
        if (localLightSource != null) {
            World.getLights().updateLightLocation(localLightSource, newLoc);
        }
    }

    public boolean isAnimationAvailable(int animation) {
        try {
            return (avatar != null) && avatar.getTemplate().getAvatarInfo().isAnimationAvailable(animation);
        }
        catch(IndexOutOfBoundsException e){
            log.warn("Tried to perform illegal animation #{} on character {}", animation, getCharId());
            return false;
        }
    }

    /**
     * Set and start a new animation for this character. The animation is shown and after its done the animation
     * handler
     * returns to the normal state.
     *
     * @param newAnimation the ID of the new animation
     * @param duration the duration of the animation in milliseconds
     */
    public void startAnimation(int newAnimation, int duration) {
        startAnimation(newAnimation, duration, false, 1.f);
    }

    /**
     * Set and start a new animation for this character. The animation is shown and after its done the animation
     * handler
     * returns to the normal state.
     *
     * @param newAnimation the ID of the new animation
     * @param duration the duration of the animation in milliseconds
     */
    private void startAnimation(int newAnimation, int duration, boolean shiftAnimation, float length) {
        if (removedCharacter) {
            log.warn("Trying to start a animation of a removed character.");
            return;
        }
        if (move.isRunning()) {
            log.warn("{}: Received new animation {} while move was in progress.", this, newAnimation);
        }

        if (avatar == null) {
            log.debug("{}: Starting a new animation is impossible. No avatar!", this);
            return; // avatar not ready, discard animation
        }

        if (!isAnimationAvailable(newAnimation)) {
            log.debug("{}: Animation {} is not available.", this, newAnimation);
            MapTile tile = World.getMap().getMapAt(getLocation());
            if (tile == null) {
                return;
            }

            //noinspection SwitchStatementWithoutDefaultBranch
            switch (newAnimation) {
                case CharAnimations.ATTACK_1HAND:
                case CharAnimations.ATTACK_2HAND:
                    tile.showEffect(21);
                    break;
                case CharAnimations.ATTACK_BOW:
                    tile.showEffect(15);
                    break;
                case CharAnimations.ATTACK_BLOCK:
                    tile.showEffect(18);
                    break;
            }
            return;
        }
        avatarId.setAnimationId(newAnimation);
        log.debug("{}: Starting new animation: {} for {}ms", this, newAnimation, duration);
        updateAvatar();

        if (avatar == null) {
            log.debug("{}: After updating the avatar, the avatar is gone. Animation impossible.", this);
            return; // avatar not ready, discard animation
        }
        avatar.animate(duration, false, shiftAnimation, length);
    }

    /**
     * Update the light source of the character.
     *
     * @param mode the mode of the update
     */
    void updateLight(int mode) {
        updateLight(avatar, mode);
    }

    /**
     * Change the appearance of the character.
     *
     * @param raceId the new race Id
     * @param typeId the new type Id
     */
    public void setAppearance(int raceId, int typeId) {
        if (removedCharacter) {
            log.warn("Trying to update the appearance of a removed character.");
            return;
        }
        if ((avatarId.getRaceId() != raceId) || (avatarId.getTypeId() != typeId)) {
            log.debug("{}: Changing race to {} and type to {}", this, raceId, typeId);
            avatarId.setRaceId(raceId);
            avatarId.setTypeId(typeId);
            if (location != null) {
                updateAvatar();
            }
        }
    }

    /**
     * Update the color of a specified cloth part.
     *
     * @param group the cloth group that should be changed
     * @param color the color this part shall be displayed in
     */
    public void setClothColor(@Nonnull AvatarClothGroup group, @Nonnull Color color) {
        if (removedCharacter) {
            log.warn("Trying to change the cloth color of a removed character.");
            return;
        }
        ImmutableColor usedColor = color.getImmutableCopy();
        wearItemsColors.put(group, usedColor);
        if (avatar != null) {
            avatar.changeClothColor(group, usedColor);
        }
    }

    /**
     * Set a item this character has in its inventory.
     *
     * @param slot the slot of the inventory
     * @param itemId the item id of the item at this slot
     */
    public void setInventoryItem(int slot, @Nonnull ItemId itemId) {
        if (removedCharacter) {
            log.warn("Trying to update the inventory of a removed character.");
            return;
        }
        applyLightValue(itemId);
        AvatarClothGroup group = AvatarClothGroup.getFromInventorySlot(slot);
        if (group != null) {
            setWearingItem(group, itemId.getValue());
        }
    }

    /**
     * Add a item the avatar wears to its current list. The changes do not become visible until
     * {@link #updatePaperdoll(Avatar)} is called.
     *
     * @param group the group the item belongs to
     * @param id the ID of the item the character wears
     */
    public void setWearingItem(@Nonnull AvatarClothGroup group, int id) {
        if (removedCharacter) {
            log.warn("Trying to update the worn items of a removed character.");
            return;
        }

        wearItems.put(group, id);
    }

    /**
     * Set the color of the skin of the avatar.
     *
     * @param color the color that is used to color the skin
     */
    public void setSkinColor(@Nullable Color color) {
        if (removedCharacter) {
            log.warn("Trying to set the skin color of a removed character.");
            return;
        }
        if (color == null) {
            skinColor = null;
        } else {
            skinColor = new Color(color);
        }
        if (avatar != null) {
            avatar.changeBaseColor(color);
        }
    }

    /**
     * Update the current light source of this character.
     */
    public void updateLight() {
        if (location == null) {
            log.debug("The position of the character is not set. The light can't be updated.");
            return;
        }
        if (removedCharacter) {
            log.warn("Trying to update the light of a removed character.");
            return;
        }
        if (lightValue > 0) {
            if (lightSrc != null) {
                if (lightSrc.getEncodedValue() != lightValue) {
                    LightSource newLight = new LightSource(location, lightValue);
                    World.getLights().replace(lightSrc, newLight);
                    lightSrc = newLight;
                }
            } else {
                lightSrc = new LightSource(location, lightValue);
                World.getLights().addLight(lightSrc);
            }
        } else {
            if (lightSrc != null) {
                World.getLights().remove(lightSrc);
                lightSrc = null;
            }
        }
    }

    /**
     * Update the paper doll, so set all items the characters wears to the avatar. Do this in case many cloth parts
     * changed or in case the avatar instance changed.
     */
    public void updatePaperdoll() {
        if (removedCharacter) {
            log.warn("Trying to update the paperdoll of a removed character.");
            return;
        }
        updatePaperdoll(avatar);
    }

    @Override
    public int hashCode() {
        return (charId == null) ? 0 : charId.hashCode();
    }

    @Override
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Char) && equals((Char) obj);
    }

    @Nonnull
    @Override
    public String toString() {
        String charIdString = (charId == null) ? "" : (" (" + charId.getValue() + ')');
        return "Character " + name + charIdString;
    }

    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Char other) {
        return (other != null) && (charId != null) && charId.equals(other.charId);
    }

    private static class DelayedMoveData {
        @Nonnull
        public final CharMovementMode mode;
        public final int duration;
        @Nonnull
        public final ServerCoordinate targetLocation;

        private DelayedMoveData(@Nonnull CharMovementMode mode, int duration,
                                @Nonnull ServerCoordinate targetLocation) {
            this.mode = mode;
            this.duration = duration;
            this.targetLocation = targetLocation;
        }

        @Override
        @Nonnull
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("DelayedMoveData(");
            switch (mode) {
                case Walk:
                    builder.append("Walk");
                    break;
                case Run:
                    builder.append("Run");
                    break;
                case None:
                    builder.append("None");
                    break;
                case Push:
                    builder.append("Push");
                    break;
            }
            builder.append(", ").append(duration).append("ms, ").append("to ").append(targetLocation);
            return builder.toString();
        }
    }

}
