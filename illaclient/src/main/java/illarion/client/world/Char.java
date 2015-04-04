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

import illarion.client.graphics.AnimatedMove;
import illarion.client.graphics.Avatar;
import illarion.client.graphics.AvatarClothManager;
import illarion.client.graphics.MoveAnimation;
import illarion.client.resources.ItemFactory;
import illarion.client.util.Lang;
import illarion.client.util.UpdateTask;
import illarion.client.world.characters.CharacterAttribute;
import illarion.client.world.interactive.InteractiveChar;
import illarion.common.graphics.CharAnimations;
import illarion.common.graphics.Layer;
import illarion.common.types.*;
import illarion.common.util.FastMath;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.LightSource;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

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
     * The color that is used to show dead characters.
     */
    @Nonnull
    private static final Color DEAD_COLOR;

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
     * Maximum value for visibility.
     */
    public static final float VISIBILITY_MAX = 1.f;

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

    /**
     * The alive state of the character. {@code true} in case the character is alive.
     */
    private boolean alive;

    /**
     * The animation that is currently shown by the character.
     */
    private int animation;

    /**
     * Current appearance value. Depends on race and gender of the character.
     */
    private int appearance;

    /**
     * Avatar of the character.
     */
    @Nullable
    private Avatar avatar;

    /**
     * ID of the avatar that represents the character.
     */
    private int avatarId;

    /**
     * Character ID the the character.
     */
    @Nullable
    private CharacterId charId;

    /**
     * Current looking direction of the character.
     */
    @Nullable
    private Direction direction;

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
    private ServerCoordinate coordinate;

    /**
     * Move animation handler for this character.
     */
    @Nonnull
    private final MoveAnimation move;

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
     * A list of items this avatar wears. This list is send to the avatar at a update.
     */
    @Nonnull
    private final int[] wearItems = new int[AvatarClothManager.GROUP_COUNT];

    /**
     * A list of modified colors of the stuff a avatar wears.
     */
    @Nonnull
    private final Color[] wearItemsColors = new Color[AvatarClothManager.GROUP_COUNT];

    /**
     * This map stores the attribute values of this character.
     */
    @Nonnull
    private final Map<CharacterAttribute, Integer> attributes;

    /**
     * The reference to the interactive character instance that points to this character.
     */
    @Nullable
    private Reference<InteractiveChar> interactiveCharRef;

    private class DelayedMoveData {
        public CharMovementMode mode;
        public int duration;
        public ServerCoordinate targetLocation;
    }

    /**
     * This stores a delayed move in case there is one.
     */
    @Nullable
    private DelayedMoveData delayedMove;

    /**
     * Constructor to create a new character.
     */
    public Char() {
        move = new MoveAnimation(this);

        attributes = new EnumMap<>(CharacterAttribute.class);

        scale = 0;
        avatarId = -1;
        appearance = 0;
        animation = -1;
        direction = Direction.North;
    }

    static {
        DEAD_COLOR = new Color(1.f, 1.f, 1.f, 0.45f);
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
            return attributes.get(attribute);
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
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                if ((i == AvatarClothManager.GROUP_BEARD) || (i == AvatarClothManager.GROUP_HAIR)) {
                    continue;
                }
                avatar.changeClothColor(i, wearItemsColors[i]);
            }
        } else {
            avatar.changeBaseColor(DEAD_COLOR);

            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                avatar.changeClothColor(i, DEAD_COLOR);
            }
        }
    }

    /**
     * This flag is used to store if there is currently a animation in progress.
     */
    private boolean animationInProgress;

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
        updatePosition();
        animationInProgress = false;

        final DelayedMoveData localDelayedMove = delayedMove;
        delayedMove = null;
        if (finished && (localDelayedMove != null)) {
            log.info("{}: Planning delayed move for execution", this);
            World.getUpdateTaskManager().addTaskForLater(new UpdateTask() {
                @Override
                public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                    log.info("{}: Executing delayed move", Char.this);
                    moveToInternal(localDelayedMove.targetLocation, localDelayedMove.mode, localDelayedMove.duration);
                }
            });
        } else if (localDelayedMove != null) {
            World.getUpdateTaskManager().addTaskForLater(new UpdateTask() {
                @Override
                public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                    log.info("{}: Canceled move received while there still was a delayed more. Fixing the location.",
                             Char.this);
                    updateLocation(localDelayedMove.targetLocation);
                }
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
        if (skipAnimationReset) {
            log.debug("{}: Reset of the animation was skipped.", this);
            skipAnimationReset = false;
            return;
        }
        log.debug("{}: Resetting the animation. Finished: {}", this, finished);
        if (finished) {
            animation = CharAnimations.STAND;
            if (coordinate != null) {
                updateAvatar();
                if (avatar != null) {
                    avatar.animate(DEFAULT_ANIMATION_SPEED, true);
                }
            }
        }
    }

    private boolean skipAnimationReset;

    public void holdBackAnimationReset() {
        skipAnimationReset = true;
    }

    /**
     * Update the graphical appearance of the character.
     */
    @SuppressWarnings("nls")
    private synchronized void updateAvatar() {
        if (coordinate == null) {
            throw new IllegalStateException("Updating the avatar while the coordinates are not set is not valid.");
        }
        if (removedCharacter) {
            releaseAvatar();
            log.warn("{}: Trying to update the avatar of a removed avatar.", this);
            return;
        }

        // nothing to do for invisible folks
        if ((appearance == 0) || (animation < 0)) {
            log.debug("{}: Can't show avatar with appearance {} and animation {}", this, appearance, animation);
            return;
        }

        // calculate avatar id
        int newAvatarId = (((appearance * Direction.values().length) + direction.getServerId()) *
                CharAnimations.TOTAL_ANIMATIONS) + animation;

        // no change, return
        if ((avatarId == newAvatarId) && (avatar != null)) {
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
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                newAvatar.changeClothColor(i, wearItemsColors[i]);
            }
            newAvatar.changeBaseColor(skinColor);
        } else {
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                newAvatar.changeClothColor(i, DEAD_COLOR);
            }
            newAvatar.changeBaseColor(DEAD_COLOR);
        }

        displayPos = getDisplayCoordinatesAt(coordinate);
        updatePosition(newAvatar);
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
        avatarId = newAvatarId;
        avatar = newAvatar;
        if (oldAvatar != null) {
            oldAvatar.markAsRemoved();
        }

        if (coordinate != null) {
            MapTile tile = World.getMap().getMapAt(coordinate);
            if (tile != null) {
                tile.updateQuestMarkerElevation();
            }
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
        if (hasWearingItem(avatar, AvatarClothManager.GROUP_FIRST_HAND,
                           wearItems[AvatarClothManager.GROUP_FIRST_HAND]) ||
                hasWearingItem(avatar, AvatarClothManager.GROUP_SECOND_HAND,
                               wearItems[AvatarClothManager.GROUP_SECOND_HAND])) {
            applyPaperdollingItem(avatar, AvatarClothManager.GROUP_FIRST_HAND,
                                  wearItems[AvatarClothManager.GROUP_FIRST_HAND]);
            applyPaperdollingItem(avatar, AvatarClothManager.GROUP_SECOND_HAND,
                                  wearItems[AvatarClothManager.GROUP_SECOND_HAND]);
        } else {
            applyPaperdollingItem(avatar, AvatarClothManager.GROUP_FIRST_HAND,
                                  wearItems[AvatarClothManager.GROUP_SECOND_HAND]);
            applyPaperdollingItem(avatar, AvatarClothManager.GROUP_SECOND_HAND,
                                  wearItems[AvatarClothManager.GROUP_FIRST_HAND]);
        }
        for (int i = 0; i < wearItems.length; ++i) {
            if ((i == AvatarClothManager.GROUP_FIRST_HAND) || (i == AvatarClothManager.GROUP_SECOND_HAND)) {
                continue;
            }
            applyPaperdollingItem(avatar, i, wearItems[i]);
        }
    }

    /**
     * Check if a cloth item is defined in a specified group.
     *
     * @param avatar the avatar to update
     * @param slot the slot where the item shall be checked
     * @param id the id of the item that shall be checked
     * @return {@code true} in case a item is defined and displayable
     */
    @SuppressWarnings("nls")
    public boolean hasWearingItem(@Nullable Avatar avatar, int slot, int id) {
        if ((slot < 0) || (slot >= AvatarClothManager.GROUP_COUNT)) {
            log.warn("Wearing item check on invalid slot: {}", slot);
            return false;
        }

        return (id != 0) && ((avatar == null) || avatar.getTemplate().getClothes().doesClothExists(slot, id));
    }

    private void applyLightValue(@Nullable ItemId itemId) {
        if (ItemId.isValidItem(itemId)) {
            int light = ItemFactory.getInstance().getTemplate(itemId.getValue()).getItemInfo().getLight();

            if (light > lightValue) {
                lightValue = light;
            }
        }
    }

    private static void applyPaperdollingItem(@Nullable Avatar avatar, int slot, int itemId) {
        if (avatar != null) {
            if (itemId == 0) {
                avatar.removeClothItem(slot);
            } else {
                avatar.setClothItem(slot, itemId);
            }
        }
    }

    /**
     * Update the avatar display position.
     *
     * @param avatar the avatar that is altered
     */
    private void updatePosition(@Nullable Avatar avatar) {
        if (displayPos == null) {
            throw new IllegalStateException(
                    "Updating the avatar position while the display pos is not set is forbidden.");
        }

        if (removedCharacter) {
            return;
        }
        if (avatar != null) {
            avatar.setScreenPos(displayPos);
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
        if (coordinate == null) {
            return;
        }

        ServerCoordinate lightLoc = coordinate;

        @Nullable MapTile tile = World.getMap().getMapAt(lightLoc);

        if ((avatar != null) && (tile != null)) {
            switch (mode) {
                case LIGHT_SET:
                    avatar.setLight(tile.getLight());
                    break;
                case LIGHT_SOFT:
                    avatar.setLightTarget(tile.getLight());
                    break;
                case LIGHT_UPDATE:
                    if (avatar.hasAnimatedLight()) {
                        avatar.setLightTarget(tile.getLight());
                    } else {
                        avatar.setLight(tile.getLight());
                    }
                    break;
                default:
                    log.warn("Wrong light update mode."); //$NON-NLS-1$
                    break;
            }
        }
    }

    /**
     * Once this value is turned {@code true} the character is removed from the game.
     */
    private boolean removedCharacter;

    /**
     * Mark this character as removed. Calling this function will cause the instance to clean its dependency and then
     * die gracefully.
     */
    public void markAsRemoved() {
        removedCharacter = true;

        move.stop();
        resetLight();
        releaseAvatar();
    }

    /**
     * Remove the current light source of the character.
     */
    public void resetLight() {
        if (lightSrc != null) {
            World.getLights().remove(lightSrc);
            lightSrc = null;
            lightValue = 0;
        }
    }

    /**
     * Release the current avatar and free the resources.
     */
    private void releaseAvatar() {
        log.debug("{}: Releasing the avatar.", this);
        Avatar localAvatar = avatar;
        avatar = null;
        avatarId = -1;
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
        displayPos = position;
        updatePosition();
    }

    /**
     * Change the ID of the character.
     *
     * @param newCharId new ID of the character
     */
    @SuppressWarnings({"nls", "IfStatementWithTooManyBranches"})
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
     * Set the color of the name of the character.
     *
     * @param color the new color value
     */
    private void setNameColor(@Nonnull Color color) {
        nameColor = color;

        if (avatar != null) {
            avatar.setNameColor(color);
        }
    }

    /**
     * Set the name of the current character. Pre and suffixes are generated by this function as well
     *
     * @param newName the name of the character or null
     */
    @SuppressWarnings("nls")
    public void setName(@Nullable String newName) {
        name = newName;
        setAvatarName();
    }

    public void setCustomName(@Nullable String customName) {
        this.customName = customName;
        setAvatarName();
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
    @SuppressWarnings("nls")
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
        return direction;
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
     * Get the current location of the character.
     *
     * @return the location of the character
     */
    @Nonnull
    @Contract(pure = true)
    public ServerCoordinate getLocation() {
        return coordinate;
    }

    /**
     * Get the name of the character.
     *
     * @return the name of the character
     */
    @Nonnull
    @Contract(pure = true)
    public String getName() {
        if ((name == null) || name.isEmpty()) {
            if ((customName == null) || customName.isEmpty()) {
                return Lang.getMsg("chat.someone"); //$NON-NLS-1$
            } else {
                return '"' + customName + '"';
            }
        } else {
            if ((customName == null) || customName.isEmpty()) {
                return name;
            } else {
                return name + " (" + customName + ')';
            }
        }
    }

    @Nullable
    @Contract(pure = true)
    public String getCustomName() {
        return customName;
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
    public void moveTo(@Nonnull final ServerCoordinate newPos, @Nonnull final CharMovementMode mode, final int duration) {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                moveToInternal(newPos, mode, duration);
            }
        });
    }

    public void updateMoveDuration(final int newDuration) {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                updateMoveDurationInteral(newDuration);
            }
        });
    }

    private void updateMoveDurationInteral(int newDuration) {
        if (move.isRunning()) {
            move.setDuration(newDuration);
            if (avatar != null) {
                avatar.changeAnimationDuration(newDuration);
            }
        }
    }

    private void moveToInternal(@Nonnull ServerCoordinate newPos, @Nonnull CharMovementMode mode, int duration) {
        CharacterId characterId = getCharId();
        if (characterId == null) {
            log.error("Can't move a character without ID around.");
            return;
        }

        if (!World.getPlayer().isPlayer(characterId)) {
            if (move.isRunning()) {
                if (delayedMove == null) {
                    delayedMove = new DelayedMoveData();
                    delayedMove.duration = duration;
                    delayedMove.mode = mode;
                    delayedMove.targetLocation = newPos;
                    holdBackAnimationReset();
                    log.info("{}: Scheduled move for later execution.", this);
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

        ServerCoordinate oldPos = coordinate;
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
            if ((oldPos != null) && (newPos.getStepDistance(oldPos) <= range) && (duration > 0) && (dir != null) &&
                    (mode != CharMovementMode.Push)) {
                if (mode == CharMovementMode.Walk) {
                    startAnimation(CharAnimations.WALK, duration, true, dir.isDiagonal() ? FastMath.sqrt(2.f) : 1.f);
                } else if (mode == CharMovementMode.Run) {
                    startAnimation(CharAnimations.RUN, duration, true, dir.isDiagonal() ? FastMath.sqrt(2.f) : 1.f);
                }

                DisplayCoordinate oldDisplayPos = getDisplayCoordinatesAt(oldPos);
                DisplayCoordinate newDisplayPos = getDisplayCoordinatesAt(newPos);

                move.start(oldDisplayPos, newDisplayPos, duration);
            } else {
                // reset last animation result
                setPosition(getDisplayCoordinatesAt(newPos));
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

    @Nonnull
    @Contract(pure = true)
    private static DisplayCoordinate getDisplayCoordinatesAt(@Nonnull ServerCoordinate coordinate) {
        int elevation = World.getMap().getElevationAt(coordinate);
        int x = coordinate.toDisplayX();
        int y = coordinate.toDisplayY() - elevation;
        int layer = coordinate.toDisplayLayer(Layer.Chars);

        return new DisplayCoordinate(x, y, layer);
    }

    private boolean updateLocation(@Nonnull ServerCoordinate newLocation) {
        if (newLocation.equals(coordinate)) {
            return false;
        }
        coordinate = newLocation;

        updateLight(coordinate);
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

    /**
     * Change the direction the character is looking at.
     *
     * @param newDirection the new direction value
     */
    public void setDirection(@Nonnull Direction newDirection) {
        log.debug("{}: Applying a new direction to the character: {}", this, newDirection);
        direction = newDirection;

        // The update of the direction arrives before the location of the character is set. That is okay because
        // the update of the character will take the stored direction into account.
        if (!move.isRunning() && (coordinate != null)) {
            updateAvatar();
        }
    }

    public boolean isAnimationAvailable(int animation) {
        return (avatar != null) && avatar.getTemplate().getAvatarInfo().isAnimationAvailable(animation);
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
        skipAnimationReset = false;
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
            log.debug("{}: Animation {} is not available.", this, animation);
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
        animation = newAnimation;
        log.debug("{}: Starting new animation: {} for {}ms", this, animation, duration);
        updateAvatar();

        if (avatar == null) {
            log.debug("{}: After updating the avatar, the avatar is gone. Animation impossible.", this);
            return; // avatar not ready, discard animation
        }
        avatar.animate(duration, false, shiftAnimation, length);
    }

    /**
     * Update the avatar display position.
     */
    void updatePosition() {
        updatePosition(avatar);
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
     * Put the light source of the character into the list of light sources that are rendered.
     */
    public void relistLight() {
        if (removedCharacter) {
            log.warn("Trying to enlist the light of a removed character again.");
            return;
        }

        LightSource localLightSource = lightSrc;
        if (localLightSource != null) {
            World.getLights().refreshLight(localLightSource);
        }
    }

    /**
     * Change the appearance of the character.
     *
     * @param newAppearance the new appearance value
     */
    public void setAppearance(int newAppearance) {
        if (removedCharacter) {
            log.warn("Trying to update the appearance of a removed character.");
            return;
        }
        if (appearance != newAppearance) {
            appearance = newAppearance;
            resetAnimation(true);
        }
    }

    /**
     * Update the color of a specified cloth part.
     *
     * @param slot the slot that shall be changed
     * @param color the color this part shall be displayed in
     */
    public void setClothColor(int slot, @Nonnull Color color) {
        if (removedCharacter) {
            log.warn("Trying to change the cloth color of a removed character.");
            return;
        }
        wearItemsColors[slot] = new Color(color);
        if (avatar != null) {
            avatar.changeClothColor(slot, color);
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
        switch (slot) {
            case 1:
                setWearingItem(AvatarClothManager.GROUP_HAT, itemId.getValue());
                break;
            case 3:
                setWearingItem(AvatarClothManager.GROUP_CHEST, itemId.getValue());
                break;
            case 5:
                setWearingItem(AvatarClothManager.GROUP_FIRST_HAND, itemId.getValue());
                break;
            case 6:
                setWearingItem(AvatarClothManager.GROUP_SECOND_HAND, itemId.getValue());
                break;
            case 9:
                setWearingItem(AvatarClothManager.GROUP_TROUSERS, itemId.getValue());
                break;
            case 10:
                setWearingItem(AvatarClothManager.GROUP_SHOES, itemId.getValue());
                break;
            case 11:
                setWearingItem(AvatarClothManager.GROUP_COAT, itemId.getValue());
                break;
            default:
                break;
        }
    }

    /**
     * Add a item the avatar wears to its current list. The changes do not become visible until
     * {@link #updatePaperdoll(Avatar)} is called.
     *
     * @param slot the slot the item is carried at
     * @param id the ID of the item the character wears
     */
    @SuppressWarnings("nls")
    public void setWearingItem(int slot, int id) {
        if (removedCharacter) {
            log.warn("Trying to update the worn items of a removed character.");
            return;
        }

        if ((slot < 0) || (slot >= AvatarClothManager.GROUP_COUNT)) {
            log.warn("Wearing item set to invalid slot: {}", slot);
            return;
        }

        wearItems[slot] = id;
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
        // set logical location
        if (Objects.equals(coordinate, newLoc)) {
            return;
        }
        log.debug("{}: Setting character location to: {}", this, newLoc);
        coordinate = newLoc;
        setPosition(newLoc.toDisplayCoordinate(Layer.Chars));
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
     * Update the current light source of this character.
     */
    public void updateLight() {
        if (coordinate == null) {
            log.error("The position of the character is not set. The light can't be updated.");
            return;
        }
        if (removedCharacter) {
            log.error("Trying to update the light of a removed character.");
            return;
        }
        if (lightValue > 0) {
            if (lightSrc != null) {
                if (lightSrc.getEncodedValue() != lightValue) {
                    LightSource newLight = new LightSource(coordinate, lightValue);
                    World.getLights().replace(lightSrc, newLight);
                    lightSrc = newLight;
                }
            } else {
                lightSrc = new LightSource(coordinate, lightValue);
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

    @Nonnull
    @Override
    public String toString() {
        String charIdString = (charId == null) ? "" : (" (" + charId.getValue() + ')');
        return "Character " + name + charIdString;
    }

    @Override
    public int hashCode() {
        return (charId == null) ? 0 : charId.hashCode();
    }
}
