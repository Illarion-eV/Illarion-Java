/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.graphics;

import java.util.ArrayList;

import illarion.common.util.FastMath;

/**
 * The item info class stores some general informations about the items that are
 * shared by all instances of the same item type and by some instances of
 * different item types in case its possible.
 * <p>
 * The class stores final values and does not support to change the values once
 * its created
 * </p>
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.98
 */
public final class ItemInfo {
    /**
     * Facing value for accepting the light from all sides.
     */
    public static final int FACE_ALL = 0;

    /**
     * Facing value for accepting the light from south and blocking it from all
     * other sides.
     */
    public static final int FACE_S = 3;

    /**
     * Facing value for accepting the light from west and from south and
     * blocking it from all other sides.
     */
    public static final int FACE_SW = 2;

    /**
     * Facing value for accepting the light from west and blocking it from all
     * other sides.
     */
    public static final int FACE_W = 1;

    /**
     * The special item value in case the book is a container.
     */
    private static final int BOOK = 2;

    /**
     * The buffer storage that is used to save the created item info instances
     * to compare them during the loading time so a minimal amount of instances
     * is created.
     */
    private static ArrayList<ItemInfo> buffer = new ArrayList<ItemInfo>();

    /**
     * The special item value in case the item is a container.
     */
    private static final int CONTAINER = 1;

    /**
     * The names of the item groups known.
     */
    @SuppressWarnings("nls")
    private static final String[] GROUPS = new String[] { "Tables",
        "Decoration (small)", "Seats", "Container", "Clerical", "Doors",
        "Tools", "House Wall", "Rock Wall", "Castle Wall", "Roof", "Bridges",
        "Fortress", "Trees", "Dungeon", "Plants", "Rocks", "Lights",
        "Weapons/Shields", "Resources", "Cloth/Armor", "Hand tools", "Others",
        "Books", "Food", "Decoration (big)" };

    /**
     * The special item value in case the Jesus item is a container.
     */
    private static final int JESUS = 3;

    /**
     * The value the variance is divided with to get the actual modificator on
     * the scale of the item.
     */
    private static final float VARIANCE_MOD = 100.f;

    /**
     * The facing of the item so the direction this item accepts light from.
     * Possible values are {@link #FACE_ALL}, {@link #FACE_W}, {@link #FACE_SW}
     * and {@link #FACE_S}.
     */
    private final int face;

    /**
     * The checking flag if this item uses variances by {@link #variance}. So
     * this flag in general is <code>true</code> in case the {@link #variance}
     * value is greater then 0.f
     */
    private final boolean hasVariance;

    /**
     * The surface level of the item, so the offset from the origin of the item
     * upwards where the next item is placed on top of the item.
     */
    private final int level;

    /**
     * The encoded value of the light source this item emits.
     */
    private final int light;

    /**
     * The movable flag, so in case this item can be moved by the player this is
     * set to <code>true</code>.
     */
    private final boolean movable;

    /**
     * The flag if the pathfinder can step in this item or not. In case this is
     * set to <code>false</code> the pathfinder has to move around this item.
     */
    private final boolean obstacle;

    /**
     * The opacity of the item in percent, so the value how much the item blocks
     * the line of sight of the player.
     */
    private final int opacity;

    /**
     * The special item flag that determines if this item is a normal item or a
     * {@link #CONTAINER}, a {@link #BOOK} or a {@link #JESUS} item.
     */
    private final int special;

    /**
     * The variance of the size of this item. In case this is set to a value
     * greater then 0.f this item is scaled.
     */
    private final float variance;

    /**
     * Create a new instance of a ItemInfo object.
     * 
     * @param facing The facing flag that contains the informations from what
     *            direction the item accepts light. The possible values are
     *            {@link #FACE_ALL}, {@link #FACE_SW}, {@link #FACE_S},
     *            {@link #FACE_W}.
     * @param itemMoveable the movable flag that stores if the item can be moved
     *            or not
     * @param specialFlag the special flag that indicates of the item is a
     *            normal item or a container, a book or a Jesus item.
     * @param itemObstacle the obstacle flag determines if the characters can
     *            walk over this item or not, more exactly it blocks the way for
     *            the automated pathfinder
     * @param varianceRange the variance of the item in since, if this variable
     *            stores a value larger then 0 the item is scaled up and down by
     *            the percent value handed over in this variable
     * @param itemOpacity the opacity of this object so the value in percent
     *            this item blocks the line of sight
     * @param surfaceLevel the offset of the surface level of the item relative
     *            to the origin of the item, this is used to add a additional
     *            offset to the item in case there are additional items upon
     *            this item, so it looks like the other line lies on this item
     * @param lightSource the encoded value of the light of this item, any value
     *            greater then 0 causes this item to be a light source emitting
     *            constantly light on the map
     */
    private ItemInfo(final int facing, final boolean itemMoveable,
        final int specialFlag, final boolean itemObstacle,
        final float varianceRange, final int itemOpacity,
        final int surfaceLevel, final int lightSource) {
        face = facing;
        movable = itemMoveable;
        special = specialFlag;
        obstacle = itemObstacle;
        level = surfaceLevel;
        if (varianceRange > 0.f) {
            hasVariance = true;
            variance = varianceRange;
        } else {
            hasVariance = false;
            variance = 0.f;
        }
        opacity = itemOpacity;
        light = lightSource;
    }

    /**
     * Cleanup the buffer that is created to support the loading of the items.
     * This should be done after all items are loaded up.
     */
    public static void cleanup() {
        buffer.clear();
        buffer = null;
    }

    /**
     * Create a new instance of a ItemInfo object. This instance also checks if
     * there is already a object of this kind with exactly the same values as
     * the new one that is going to be created and returns this one rather then
     * a new one.
     * 
     * @param facing The facing flag that contains the informations from what
     *            direction the item accepts light. The possible values are
     *            {@link #FACE_ALL}, {@link #FACE_SW}, {@link #FACE_S},
     *            {@link #FACE_W}.
     * @param movable the movable flag that stores if the item can be moved or
     *            not
     * @param special the special flag that indicates of the item is a normal
     *            item or a container, a book or a Jesus item.
     * @param obstacle the obstacle flag determines if the characters can walk
     *            over this item or not, more exactly it blocks the way for the
     *            automated pathfinder
     * @param variance the variance of the item in since, if this variable
     *            stores a value larger then 0 the item is scaled up and down by
     *            the percent value handed over in this variable
     * @param opacity the opacity of this object so the value in percent this
     *            item blocks the line of sight
     * @param level the offset of the surface level of the item relative to the
     *            origin of the item, this is used to add a additional offset to
     *            the item in case there are additional items upon this item, so
     *            it looks like the other line lies on this item
     * @param lightSource the encoded value of the light of this item, any value
     *            greater then 0 causes this item to be a light source emitting
     *            constantly light on the map
     * @return the ItemInfo object, either a newly created one, or one that was
     *         loaded from the buffer
     */
    public static strictfp ItemInfo create(final int facing,
        final boolean movable, final int special, final boolean obstacle,
        final int variance, final int opacity, final int level,
        final int lightSource) {

        final float prepVariance = variance / VARIANCE_MOD;
        for (final ItemInfo testItemInfo : buffer) {
            if (testItemInfo.face != facing) {
                continue;
            }
            if (testItemInfo.movable != movable) {
                continue;
            }
            if (testItemInfo.special != special) {
                continue;
            }
            if (testItemInfo.obstacle != obstacle) {
                continue;
            }
            if (FastMath.abs(testItemInfo.variance - prepVariance) < FastMath.FLT_EPSILON) {
                continue;
            }
            if (testItemInfo.opacity != opacity) {
                continue;
            }
            if (testItemInfo.level != level) {
                continue;
            }
            if (testItemInfo.light != lightSource) {
                continue;
            }
            return testItemInfo;
        }

        final ItemInfo retInfo =
            new ItemInfo(facing, movable, special, obstacle, prepVariance,
                opacity, level, lightSource);
        buffer.add(retInfo);
        return retInfo;
    }

    /**
     * Get the amount of item group known.
     * 
     * @return the amount of known item groups
     */
    public static int getGroupCount() {
        return GROUPS.length;
    }

    /**
     * Get the name of one item group.
     * 
     * @param index the index of the item group
     * @return the name of the item group
     */
    public static String getGroupName(final int index) {
        return GROUPS[index];
    }

    /**
     * Get the facing of the item. So the direction the item accepts light from.
     * 
     * @return the facing of the item
     * @see #FACE_ALL
     * @see #FACE_S
     * @see #FACE_SW
     * @see #FACE_W
     */
    public int getFace() {
        return face;
    }

    /**
     * Get the surface level of the item. So the offset how much a item that
     * lies on this item has to move up to appear to lie on this item.
     * 
     * @return the amount of pixels the next item offset has to move up
     */
    public int getLevel() {
        return level;
    }

    /**
     * The value of the light source.
     * 
     * @return the encodes parameters of the light source of this item
     */
    public int getLight() {
        return light;
    }

    /**
     * Get the opacity if the item so the value how the item blocks the line of
     * sight.
     * 
     * @return the percent value how much of the line of sight is blocked by
     *         this item
     */
    public int getOpacity() {
        return opacity;
    }

    /**
     * Get the variances of this item.
     * 
     * @return the modificator of the default scaling to decrease or increase
     *         the default scaling by
     */
    public float getVariance() {
        return variance;
    }

    /**
     * Check if this item uses scale variances.
     * 
     * @return <code>true</code> in case the item is able to use scale variances
     */
    public boolean hasVariance() {
        return hasVariance;
    }

    /**
     * Check if the item is a book.
     * 
     * @return <code>true</code> in case the item is a book
     */
    public boolean isBook() {
        return special == BOOK;
    }

    /**
     * Check if the item is a container.
     * 
     * @return <code>true</code> in case the item is a container
     */
    public boolean isContainer() {
        return special == CONTAINER;
    }

    /**
     * Check if the item is a Jesus item.
     * 
     * @return <code>true</code> in case the item is a Jesus item
     */
    public boolean isJesus() {
        return special == JESUS;
    }

    /**
     * Check if this item is a source of light.
     * 
     * @return <code>true</code> in case the item is a source of light
     */
    public boolean isLight() {
        return light > 0;
    }

    /**
     * Check if the item is movable by the player.
     * 
     * @return <code>true</code> in case the player can move the item around
     */
    public boolean isMovable() {
        return movable;
    }

    /**
     * Check if the item is obstacle.
     * 
     * @return <code>true</code> if the item is obstacle and the pathfinder has
     *         to search a way around it
     */
    public boolean isObstacle() {
        return obstacle;
    }
}
