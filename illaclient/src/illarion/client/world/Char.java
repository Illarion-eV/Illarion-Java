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

import org.apache.log4j.Logger;

import illarion.client.graphics.AnimatedMove;
import illarion.client.graphics.Avatar;
import illarion.client.graphics.AvatarClothManager;
import illarion.client.graphics.Colors;
import illarion.client.graphics.ItemFactory;
import illarion.client.graphics.Marker;
import illarion.client.graphics.MoveAnimation;
import illarion.client.guiNG.references.AbstractReference;
import illarion.client.guiNG.references.CharReference;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.LookatCharCmd;
import illarion.client.util.Lang;

import illarion.common.graphics.CharAnimations;
import illarion.common.graphics.Layers;
import illarion.common.graphics.MapConstants;
import illarion.common.util.Location;
import illarion.common.util.RecycleObject;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;
import illarion.graphics.common.LightSource;

/**
 * Represents a character: player, monster or npc.
 */
public final class Char extends Interaction implements RecycleObject,
    AnimatedMove {

    /**
     * The speed a animation runs with on default.
     */
    public static final int DEFAULT_ANIMATION_SPEED = 5;

    /**
     * Move mode constant for a pushed move.
     */
    public static final int MOVE_PUSH = 0;

    /**
     * Move mode constant for a running move.
     */
    public static final int MOVE_RUN = 2;

    /**
     * Move mode constant for a walking move.
     */
    public static final int MOVE_WALK = 1;

    /**
     * Light Update status SOFTly change value.
     */
    protected static final int LIGHT_SOFT = 2;

    /**
     * Light Update status UPDATE light value.
     */
    protected static final int LIGHT_UPDATE = 3;

    /**
     * Maximum value for visibility.
     */
    protected static final int VISIBILITY_MAX = 100;

    /**
     * The color that is used to show combat characters.
     */
    private static final SpriteColor COMBAT_COLOR;

    /**
     * The color that is used to show dead characters.
     */
    private static final SpriteColor DEAD_COLOR;

    /**
     * Light Update status SET light value.
     */
    private static final int LIGHT_SET = 1;

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(Char.class);

    /**
     * Maximal scale value for the character.
     */
    private static final float MAXIMAL_SCALE = 1.2f;

    /**
     * Minimal scale value for the character.
     */
    private static final float MINIMAL_SCALE = 0.5f;

    /**
     * Type value that the character is a monster.
     */
    private static final int MONSTER = 1;

    /**
     * Mask to check if the character ID belongs to a monster.
     */
    private static final long MONSTER_BASE = 0xFE000000L;

    /**
     * Type value that the character is a npc.
     */
    private static final int NPC = 2;

    /**
     * Mask to check if the character ID belongs to a npc.
     */
    private static final long NPC_BASE = 0xFF000000L;

    /**
     * Type value that the character is a player.
     */
    private static final int PLAYER = 0;

    /**
     * Minimum scale. Below the avatar does not get a title for example and is
     * not recognized at all.
     */
    private static final float SCALE_MIN = 0.1f;

    /**
     * Scale modifier that is needed at least so a prefix for a small character
     * is prepended.
     */
    private static final float SCALE_SMALL = 0.8f;

    /**
     * Scale modifier that is needed at least so a prefix for a tall character
     * is prepended.
     */
    private static final float SCALE_TALL = 1.05f;

    /**
     * Scale modifier that is needed at least so a prefix for a tiny character
     * is prepended.
     */
    private static final float SCALE_TINY = 0.6f;

    /**
     * Modifier to calculate the alpha value from the visibility.
     */
    private static final int VISIBILITY_ALPHA_MOD = 10;

    static {
        DEAD_COLOR = Graphics.getInstance().getSpriteColor();
        DEAD_COLOR.set(SpriteColor.COLOR_MAX);
        DEAD_COLOR.setAlpha(0.45f);

        COMBAT_COLOR = Graphics.getInstance().getSpriteColor();
        COMBAT_COLOR.set(1.f, 0.6f, 0.6f);
        COMBAT_COLOR.setAlpha(1.f);
    }

    /**
     * The alive state of the character. <code>true</code> in caes the character
     * is alive.
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
     * Attack marker on this character.
     */
    private Marker attack;

    /**
     * Avatar of the character.
     */
    private Avatar avatar;

    /**
     * ID of the avatar that represents the character.
     */
    private int avatarId;

    /**
     * Character ID the the character.
     */
    private long charId;

    /**
     * Current looking direction of the character.
     */
    private int direction;

    /**
     * X-Offset for the current move.
     */
    private int dX;

    /**
     * Y-Offset for the current move.
     */
    private int dY;

    /**
     * Z-Offset for the current move.
     */
    private int dZ;

    /**
     * Elevation of the character above the ground. Occurs if the character
     * steps on a item with elevation.
     */
    private int elevation;

    /**
     * Last visibility value that was shown. Used for fading in and out
     * animations.
     */
    private int lastVisibility;

    /**
     * Current light source of the character.
     */
    private LightSource lightSrc;

    /**
     * Current light value of the character.
     */
    private int lightValue;

    /**
     * Current Location of the character on the map.
     */
    private transient final Location loc;

    /**
     * Move animation handler for this character.
     */
    private transient final MoveAnimation move;

    /**
     * Name of the character.
     */
    private String name;

    /**
     * Color of the name of the character. default, melee fighting, distance
     * fighting, magic
     */
    private Colors nameColor;

    /**
     * Scale of the character (based on its height).
     */
    private float scale;

    /**
     * The custom color of the characters skin.
     */
    private transient SpriteColor skinColor = null;

    /**
     * Type of the character.
     */
    private int type;

    /**
     * Visibility bonus of the character (for large characters).
     */
    private int visibilityBonus;

    /**
     * Flag if the character is visible.
     */
    private boolean visible;

    /**
     * A list of items this avatar wears. This list is send to the avatar at a
     * update.
     */
    private final int[] wearItems = new int[AvatarClothManager.GROUP_COUNT];

    /**
     * A list of modified colors of the stuff a avatar wears.
     */
    private transient final SpriteColor[] wearItemsColors =
        new SpriteColor[AvatarClothManager.GROUP_COUNT];

    /**
     * Constructor to create a new character.
     */
    public Char() {
        loc = Location.getInstance();
        move = new MoveAnimation(this);
        scale = 0;
        animation = CharAnimations.STAND;
        avatarId = -1;
    }

    /**
     * Create a new character, using the GameFactory.
     * 
     * @return the new character
     */
    protected static Char create() {
        return (Char) GameFactory.getInstance().getCommand(
            GameFactory.OBJ_CHARACTER);
    }

    /**
     * Activate the character. That method is unused
     * 
     * @param id parameter is not in use
     */
    @Override
    public void activate(final int id) {
        // never happens as character has no direct graphics resources
    }

    /**
     * Stop the walking animation of the character.
     * 
     * @param ok not in use
     */
    @Override
    public void animationFinished(final boolean ok) {
        resetAnimation();
    }

    /**
     * Return if the character can be named or not.
     * 
     * @return true if the character can be named, else if not
     */
    public boolean canHaveName() {
        return !isMonster() && !isNPC();
    }

    /**
     * Cast a spell on the tile below the character. TODO: cast on the
     * character, rather then on the tile below
     * 
     * @return Reference to the map tile the spell was casted on
     */
    @Override
    public AbstractReference castSpellOn() {
        return Game.getMap().getMapAt(loc).castSpellOn();
    }

    /**
     * Create a copy of this character.
     * 
     * @return new character object
     */
    @Override
    public Char clone() {
        return new Char();
    }

    /**
     * Drag a character, in case its next to the player.
     * 
     * @param x not in use
     * @param y not in use
     * @return Reference to the move character
     */
    @Override
    public AbstractReference dragFrom(final int x, final int y) {
        // if the char is standing next to player or is player
        if (Game.getPlayer().getLocation().isNeighbour(loc)) {
            final CharReference ref = new CharReference();
            ref.setReferringCharacter(charId);
            // ref.setSource(this, avatar);
            return ref;
        }
        return null;
    }

    /**
     * Execute a lookat at the character with a special mode.
     * 
     * @param mode the mode that is used to look at the character
     */
    public void examineChar(final int mode) {
        final LookatCharCmd cmd =
            (LookatCharCmd) CommandFactory.getInstance().getCommand(
                CommandList.CMD_LOOKAT_CHAR);
        cmd.examine(charId, mode);
        cmd.send();
    }

    /**
     * Get the current avatar of the character.
     * 
     * @return the avatar of the character
     */
    public Avatar getAvatar() {
        return avatar;
    }

    /**
     * Get the character ID of the character.
     * 
     * @return the ID of the character
     */
    public long getCharId() {
        return charId;
    }

    /**
     * Get the character in case the player is pointing at it.
     * 
     * @param x X-Coordinate of the pointer on the game screen
     * @param y Y-Coordinate of the pointer on the game screen
     * @return The character or null
     */
    @Override
    public Interactive getComponentAt(final int x, final int y) {
        if (!visible) {
            return null;
        }
        if (scale < SCALE_MIN) {
            return null;
        }
        final int distance =
            Math.abs(x - loc.getDcX()) + (Math.abs(y - loc.getDcY()) * 2);
        if (distance < (MapConstants.TILE_W / 2)) {
            return this;
        }
        return null;
    }

    /**
     * Get the current direction the character is looking at.
     * 
     * @return the direction value
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Get the ID of the recycle object type.
     * 
     * @return ID of the recycle object type
     */
    @Override
    public int getId() {
        return GameFactory.OBJ_CHARACTER;
    }

    /**
     * Get the current location of the character.
     * 
     * @return the location of the character
     */
    public Location getLocation() {
        return loc;
    }

    /**
     * Get the context menu for this character.
     * 
     * @return The created context menu
     */
    // @Override
    // public ContextMenu getMenu() {
    // final ContextMenu menu =
    // MenuFactory.getInstance().getCommand(MenuFactory.MENU_CHAR);
    // menu.setContext(this);
    // return menu;
    // }

    /**
     * Get the name of the character.
     * 
     * @return the name of the character
     */
    public String getName() {
        if (name == null) {
            return Lang.getMsg("chat.someone"); //$NON-NLS-1$
        }
        return name;
    }

    /**
     * Get the visibility bonus value.
     * 
     * @return visibility bonus value
     */
    public int getVisibilityBonus() {
        return visibilityBonus;
    }

    /**
     * Check if a cloth item is defined in a specified group.
     * 
     * @param slot the slot where the item shall be checked
     * @param id the id of the item that shall be checked
     * @return <code>true</code> in case a item is defined and displayable
     */
    @SuppressWarnings("nls")
    public boolean hasWearingItem(final int slot, final int id) {
        if ((slot < 0) || (slot >= AvatarClothManager.GROUP_COUNT)) {
            LOGGER.warn("Wearing item check on invalid slot: " + slot);
            return false;
        }

        if (id == 0) {
            return false;
        }

        if (avatar == null) {
            return true;
        }

        return avatar.clothItemExist(slot, id);
    }

    /**
     * Check if the character is a monster.
     * 
     * @return true if the character is a monster, false if not.
     */
    public boolean isMonster() {
        return type == MONSTER;
    }

    /**
     * Check if the character is a npc.
     * 
     * @return true if the character is a npc, false if not.
     */
    public boolean isNPC() {
        return type == NPC;
    }

    /**
     * Check if the character is visible or not.
     * 
     * @return true if the character is visible, false if not
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Execute a look at on a character.
     */
    @Override
    public void lookAt() {
        examineChar(LookatCharCmd.LOOKAT_POLITE);
    }

    /**
     * Move the character to a new position with animation. This function takes
     * absolute coordinates.
     * 
     * @param newPos the target location of the move
     * @param mode the mode of the move, possible values are {@link #MOVE_PUSH},
     *            {@link #MOVE_WALK} and {@link #MOVE_RUN}
     * @param speed moving speed
     */
    public void moveTo(final Location newPos, final int mode, final int speed) {
        // get old position
        final Location tempLoc = Location.getInstance();
        tempLoc.set(loc);
        loc.set(newPos);

        if (tempLoc.equals(loc)) {
            return;
        }

        updateLight(loc);

        // determine general visibility by players
        setVisible(Game.getPlayer().canSee(this));
        if (!visible || (avatar == null)) {
            tempLoc.recycle();
            return;
        }

        // calculate movement direction
        final int dir = tempLoc.getDirection(loc);

        // turn only when animating, not when pushed
        if ((mode != MOVE_PUSH) && (dir != Location.DIR_ZERO)) {
            setDirection(dir);
        }

        // find target elevation
        final int fromElevation = elevation;
        elevation = Game.getMap().getElevationAt(loc);

        int range = 1;
        if (mode == MOVE_RUN) {
            range = 2;
        }

        // start animations only if reasonable distance
        if ((loc.getDistance(tempLoc) <= range) && (speed > 0)
            && (dir != Location.DIR_ZERO) && (mode != MOVE_PUSH)) {
            if (mode == MOVE_WALK) {
                startAnimation(CharAnimations.WALK, speed);
            } else if (mode == MOVE_RUN) {
                startAnimation(CharAnimations.RUN, speed);
            }
            move.start(tempLoc.getDcX() - loc.getDcX(),
                (tempLoc.getDcY() + fromElevation) - loc.getDcY(),
                tempLoc.getDcZ() - loc.getDcZ(), 0, elevation, 0, speed);
        } else {
            // reset last animation result
            dX = 0;
            dY = 0;
            dZ = 0;
            updatePosition(elevation);
        }
        tempLoc.recycle();
        updateLight(LIGHT_SOFT);

        // notify about character movement to adjust cursor
        // Gui.getInstance().getManager().notifyMovement();
    }

    /**
     * Check if its possible to update the Avatar of this character.
     * 
     * @return <code>true</code> in case the avatar is updateable
     */
    public boolean readyForUpdate() {
        return (avatar != null);
    }

    /**
     * Remove the character and put it back into the factory.
     */
    @Override
    public void recycle() {
        GameFactory.getInstance().recycle(this);
    }

    /**
     * Put the light source of the character into the list of light sources that
     * are rendered.
     */
    public void relistLight() {
        if (lightSrc != null) {
            Game.getLights().remove(lightSrc);
            LightSource.releaseLight(lightSrc);
            lightSrc = LightSource.createLight(loc, lightValue);
            Game.getLights().add(lightSrc);
        }
    }

    /**
     * Reset the character object and free the resources.
     */
    @Override
    public void reset() {
        // stop animation
        move.stop();
        resetAnimation();
        resetLight();
        releaseAvatar();

        name = null;
        dX = 0;
        dY = 0;
        appearance = 0;
        direction = 0;
        charId = 0;
        visible = false;
        skinColor = null;
    }

    /**
     * Remove the current light source of the character.
     */
    public void resetLight() {
        if (lightSrc != null) {
            Game.getLights().remove(lightSrc);
            LightSource.releaseLight(lightSrc);
            lightSrc = null;
            lightValue = 0;
        }
    }

    /**
     * Set the alive state of this character.
     * 
     * @param newAliveState set the new alive state. <code>true</code> in case
     *            the character is alive.
     */
    public void setAlive(final boolean newAliveState) {
        alive = newAliveState;

        if (avatar == null) {
            return;
        }

        if (!alive) {
            avatar.changeBaseColor(DEAD_COLOR);

            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                avatar.changeClothColor(i, DEAD_COLOR);
            }
        } else {
            avatar.changeBaseColor(skinColor);
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                if ((i == AvatarClothManager.GROUP_BEARD)
                    || (i == AvatarClothManager.GROUP_HAIR)) {
                    continue;
                }
                avatar.changeClothColor(i, wearItemsColors[i]);
            }
        }
    }

    /**
     * Change the appearance of the character.
     * 
     * @param newAppearance the new appearance value
     */
    public void setAppearance(final int newAppearance) {
        appearance = newAppearance;
        resetAnimation();
        Game.getPeople().updateName(this);
    }

    /**
     * Set or remove the marker from the character that selects the character as
     * active combat target.
     * 
     * @param activate <code>true</code> to enable the combat target marker on
     *            this character
     */
    public void setAttackMarker(final boolean activate) {
        if (avatar == null) {
            return;
        }

        if (activate) {
            avatar.setBaseColor(COMBAT_COLOR);

            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                avatar.changeClothColor(i, COMBAT_COLOR);
            }
        } else {
            avatar.changeBaseColor(skinColor);
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                avatar.changeClothColor(i, wearItemsColors[i]);
            }
        }
    }

    /**
     * Change the ID of the character.
     * 
     * @param newCharId new ID of the character
     */
    @SuppressWarnings("nls")
    public void setCharId(final long newCharId) {
        charId = newCharId;
        if (charId > NPC_BASE) {
            type = NPC;
            setTooltipKey("tooltip.character");
        } else if (charId > MONSTER_BASE) {
            type = MONSTER;
            setTooltipKey("tooltip.monster");
        } else if (Game.getPlayer().isPlayer(charId)) {
            type = PLAYER;
            setTooltipKey("tooltip.player");
        } else {
            type = PLAYER;
            setTooltipKey("tooltip.character");
        }

    }

    /**
     * Update the color of a specified cloth part.
     * 
     * @param slot the slot that shall be changed
     * @param color the color this part shall be displayed in
     */
    public void setClothColor(final int slot, final SpriteColor color) {
        if (wearItemsColors[slot] == null) {
            wearItemsColors[slot] = Graphics.getInstance().getSpriteColor();
        }
        if (avatar != null) {
            avatar.changeClothColor(slot, color);
        }
        wearItemsColors[slot].set(color);
        wearItemsColors[slot].setAlpha(color.getAlphai());
    }

    /**
     * Change the direction the character is looking at.
     * 
     * @param newDirection the new direction value
     */
    public void setDirection(final int newDirection) {
        direction = newDirection;
        resetAnimation();
    }

    /**
     * Show or hide a hover marker on the tile of the character.
     * 
     * @param hover true if the hover shall be shown, false if hidden
     */
    @Override
    public void setHover(final boolean hover) {
        // if (hover) {
        // final Marker sel = Marker.create(MarkerFactory.CHAR_SELECT);
        // sel.setScreenPos(loc, Layers.MARKER);
        // Gui.getInstance().getManager().showMarker(InputHandler.MARK_MAP,
        // this, sel);
        // } else {
        // Gui.getInstance().getManager().hideMarker(InputHandler.MARK_MAP,
        // this);
        // }
    }

    /**
     * Set the new location of the character.
     * 
     * @param newLoc new location of the character
     */
    public void setLocation(final Location newLoc) {
        // set logical location
        if (loc.equals(newLoc)) {
            return;
        }
        loc.set(newLoc);
        elevation = Game.getMap().getElevationAt(loc);
        updatePosition(elevation);
    }

    /**
     * Set the name of the current character. Pre and suffixes are generated by
     * this function as well
     * 
     * @param newName the name of the character or null
     */
    @SuppressWarnings("nls")
    public void setName(final String newName) {
        // substitute missing name with description
        if (newName == null) {
            if ((scale > SCALE_MIN) && (avatar != null)) {
                final StringBuffer result = new StringBuffer();
                boolean standard = false;

                // build scale qualifier
                if (scale > SCALE_TALL) {
                    result.append(Lang.getMsg("char.size.tall"));
                } else if (scale < SCALE_TINY) {
                    result.append(Lang.getMsg("char.size.tiny"));
                } else if (scale < SCALE_SMALL) {
                    result.append(Lang.getMsg("char.size.short"));
                } else {
                    standard = true;
                }

                // paste description
                final String text = avatar.getDescription();
                // no qualifier, remove ending if present
                if (text.length() == 0) {
                    result.setLength(0);
                } else if (standard) {
                    final int pos = text.indexOf(" ");
                    if ((pos == 1) || (pos == 2)) {
                        result.append(text.substring(pos + 1));
                    } else {
                        result.append(text);
                    }
                } else {
                    // insert space before description without an ending
                    if (Character.isUpperCase(text.charAt(0))) {
                        result.append(" ");
                    }

                    result.append(text);
                }
                name = result.toString();
            } else {
                name = Lang.getMsg("chat.someone");
            }
        } else {
            name = newName;
        }

        // clean up the name
        name = name.trim();

        if ((avatar != null) && (charId > 0)
            && !Game.getPlayer().isPlayer(charId)) {
            avatar.setName(name);
        }
    }

    /**
     * Set the color of the name of the character.
     * 
     * @param color the new color value
     */
    public void setNameColor(final Colors color) {
        nameColor = color;

        if (avatar != null) {
            avatar.setNameColor(color);
        }
    }

    /**
     * Set the new position of the character.
     * 
     * @param x X-Coordinate of the character
     * @param y Y-Coordinate of the character
     * @param z Z-Coordinate of the character
     */
    @Override
    public void setPosition(final int x, final int y, final int z) {
        dX = x;
        dY = y;
        dZ = z;

        updatePosition(0);
    }

    /**
     * Set the scale of the character.
     * 
     * @param newScale new scale value between 0.5f and 1.2f
     */
    @SuppressWarnings("nls")
    public void setScale(final float newScale) {
        if ((newScale < MINIMAL_SCALE) || (newScale > MAXIMAL_SCALE)) {
            LOGGER.warn("invalid character scale " + newScale
                + " ignored for " + charId);
            return;
        }

        scale = newScale;
        if (avatar != null) {
            avatar.setScale(newScale);
            Game.getPeople().updateName(this);
        }
    }

    /**
     * Set the color of the skin of the avatar.
     * 
     * @param color the color that is used to color the skin
     */
    public void setSkinColor(final SpriteColor color) {
        if (color == null) {
            skinColor = null;
        } else {
            if (skinColor == null) {
                skinColor = Graphics.getInstance().getSpriteColor();
            }
            skinColor.set(color);
            skinColor.setAlpha(color.getAlphai());
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
    public void setVisibilityBonus(final int newVisibilityBonus) {
        visibilityBonus = newVisibilityBonus;
    }

    /**
     * Set the visibility of a character.
     * 
     * @param visibility the new visibility of the character
     */
    public void setVisible(final int visibility) {
        final boolean newVisible = visibility > 0;
        // react only to change
        if ((lastVisibility != visibility) || (newVisible != visible)) {
            lastVisibility = visibility;
            visible = newVisible;

            // becoming visible
            if (visible) {
                updateAvatar();
                updateLight(LIGHT_SET);
                if (avatar != null) {
                    avatar.setAlphaTarget(VISIBILITY_ALPHA_MOD * visibility);
                }
            } else if (avatar != null) {
                avatar.setAlphaTarget(0);
            }
        }
    }

    /**
     * Add a item the avatar wears to its current list.
     * 
     * @param slot the slot the item is carried at
     * @param id the ID of the item the character wears
     */
    @SuppressWarnings("nls")
    public void setWearingItem(final int slot, final int id) {
        if ((slot < 0) || (slot >= AvatarClothManager.GROUP_COUNT)) {
            LOGGER.warn("Wearing item set to invalid slot: " + slot);
            return;
        }

        final int light =
            ItemFactory.getInstance().getPrototype(id).getItemLight();

        if (light > lightValue) {
            lightValue = light;
        }

        wearItems[slot] = id;

        if (avatar == null) {
            return;
        }

        if (id == 0) {
            avatar.removeClothItem(slot);
        } else {
            avatar.setClothItem(slot, id);
        }
    }

    /**
     * Set and start a new animation for this character. The animation is shown
     * and after its done the animation handler returns to the normal state.
     * 
     * @param newAnimation the ID of the new animation
     * @param speed the animation speed, the larger the value the slower the
     *            animation
     */
    public void startAnimation(final int newAnimation, final int speed) {
        if (avatar == null) {
            return; // avatar not ready, discard animation
        }
        if (!avatar.getAnimationAvaiable(newAnimation)) {
            return;
        }
        animation = newAnimation;
        updateAvatar();
        avatar.animate(speed, this, false);
    }

    /**
     * Update the current light source of this character.
     */
    public void updateLight() {
        if (lightValue > 0) {
            final int tempLightValue = lightValue;
            resetLight();
            lightSrc = LightSource.createLight(loc, tempLightValue);
            Game.getLights().add(lightSrc);
            lightValue = tempLightValue;
        }
    }

    /**
     * Change the position of the light source of the character and refresh the
     * light.
     * 
     * @param newLoc the new location of the light source
     */
    public void updateLight(final Location newLoc) {
        if (lightSrc != null) {
            lightSrc.getLocation().set(newLoc);
            Game.getLights().refreshLight(lightSrc);
        }
    }

    /**
     * Update the paper doll, so set all items the characters wears to the
     * avatar. Do this in case many cloth parts changed or in case the avatar
     * instance changed.
     */
    public void updatePaperdoll() {
        if (hasWearingItem(AvatarClothManager.GROUP_FIRST_HAND,
            wearItems[AvatarClothManager.GROUP_FIRST_HAND])
            || hasWearingItem(AvatarClothManager.GROUP_SECOND_HAND,
                wearItems[AvatarClothManager.GROUP_SECOND_HAND])) {
            setWearingItem(AvatarClothManager.GROUP_FIRST_HAND,
                wearItems[AvatarClothManager.GROUP_FIRST_HAND]);
            setWearingItem(AvatarClothManager.GROUP_SECOND_HAND,
                wearItems[AvatarClothManager.GROUP_SECOND_HAND]);
        } else {
            setWearingItem(AvatarClothManager.GROUP_FIRST_HAND,
                wearItems[AvatarClothManager.GROUP_SECOND_HAND]);
            setWearingItem(AvatarClothManager.GROUP_SECOND_HAND,
                wearItems[AvatarClothManager.GROUP_FIRST_HAND]);
        }
        for (int i = 0; i < wearItems.length; ++i) {
            if ((i == AvatarClothManager.GROUP_FIRST_HAND)
                || (i == AvatarClothManager.GROUP_SECOND_HAND)) {
                continue;
            }
            final int itemID = wearItems[i];
            if (itemID == 0) {
                avatar.removeClothItem(i);
            } else {
                avatar.setClothItem(i, itemID);
            }
        }
    }

    /**
     * Use the item below the character. TODO: use things on characters, use
     * tile under character instead
     * 
     * @return Reference on the maptile with the used item
     */
    @Override
    public AbstractReference useItem() {
        return Game.getMap().getMapAt(loc).useItem();
    }

    /**
     * Handle a change of the mouse wheel on the character.
     * 
     * @param delta value the mouse wheel change by
     */
    @Override
    public void wheelIncrement(final int delta) {
        if (Game.getPlayer().isPlayer(charId)) {
            if (delta > 0) { // left turn
                Game.getPlayer()
                    .getMovementHandler()
                    .requestTurn(
                        ((direction + Location.DIR_MOVE8) - 1)
                            % Location.DIR_MOVE8);
            } else { // right turn
                Game.getPlayer().getMovementHandler()
                    .requestTurn((direction + 1) % Location.DIR_MOVE8);
            }
        }
    }

    /**
     * Update the light source of the character.
     * 
     * @param mode the mode of the update
     */
    protected void updateLight(final int mode) {
        Location lightLoc;
        // different handling for own char
        final Player player = Game.getPlayer();
        if ((player != null) && (player.getPlayerId() == charId)) {
            lightLoc = player.getLocation();
        } else {
            lightLoc = loc;
        }

        final MapTile tile = Game.getMap().getMapAt(lightLoc);

        final Avatar localAvatar = avatar;
        final MapTile localTile = tile;
        if ((localAvatar != null) && (localTile != null)) {
            switch (mode) {
                case LIGHT_SET:
                    localAvatar.setLight(localTile.getLight());
                    break;
                case LIGHT_SOFT:
                    localAvatar.setLightTarget(localTile.getLight());
                    break;
                case LIGHT_UPDATE:
                    if (localAvatar.hasAnimatedLight()) {
                        localAvatar.setLightTarget(localTile.getLight());
                    } else {
                        localAvatar.setLight(localTile.getLight());
                    }
                    break;
                default:
                    LOGGER.warn("Wrong light update mode."); //$NON-NLS-1$
                    break;
            }
        }
    }

    /**
     * Update the avatar display position.
     * 
     * @param fix additional position offset for the character, used for the
     *            elevation.
     */
    protected void updatePosition(final int fix) {
        if (avatar != null) {
            avatar.setScreenPos(loc.getDcX() + dX, loc.getDcY() + dY + fix,
                loc.getDcZ() + dZ, Layers.CHARS);
        }

        if (attack != null) {
            attack.setScreenPos(loc.getDcX() + dX, loc.getDcY() + dY,
                loc.getDcZ() + dZ, Layers.MARKER);
        }
    }

    /**
     * Release the current avatar and free the resources.
     */
    private void releaseAvatar() {
        if (avatar != null) {
            avatar.recycle();
            avatar = null;
            avatarId = -1;
        }
    }

    /**
     * Set the current animation back to its parent, update the avatar and
     * invoke the needed animations.
     */
    private void resetAnimation() {
        animation = CharAnimations.STAND;
        updateAvatar();
        if (avatar != null) {
            avatar.animate(DEFAULT_ANIMATION_SPEED, this, true);
        }
    }

    /**
     * Update the graphical appearance of the character.
     */
    @SuppressWarnings("nls")
    private synchronized void updateAvatar() {
        // nothing to do for invisible folks
        if (!visible || (appearance == 0)) {
            return;
        }

        // calculate avatar id
        final int newAvatar =
            (((appearance * Location.DIR_MOVE8) + direction) * CharAnimations.TOTAL_ANIMATIONS)
                + animation;

        // no change, return
        if ((avatarId == newAvatar) && (avatar != null)) {
            return;
        }

        int oldAlpha = 0;

        if (avatar != null) {
            oldAlpha = avatar.getAlpha();
        }

        // get rid of old one
        releaseAvatar();

        // initialize new avatar
        avatarId = newAvatar;
        avatar = Avatar.create(avatarId);

        if (avatar == null) {
            throw new NullPointerException("Avatar for ID "
                + Integer.toString(avatarId) + " is NULL.");
        }

        updatePaperdoll();

        if (alive) {
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                avatar.changeClothColor(i, wearItemsColors[i]);
            }
            avatar.changeBaseColor(skinColor);
        } else {
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                avatar.changeClothColor(i, DEAD_COLOR);
            }
            avatar.changeBaseColor(DEAD_COLOR);
        }

        setName(name);
        setNameColor(nameColor);
        updatePosition(0);
        updateLight(LIGHT_SET);
        avatar.setScale(scale);
        avatar.setAlpha(oldAlpha);
        avatar.show();
    }
}
