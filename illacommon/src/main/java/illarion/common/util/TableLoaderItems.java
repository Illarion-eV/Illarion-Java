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
package illarion.common.util;

import illarion.common.graphics.ItemInfo;

import javax.annotation.Nonnull;
import java.io.InputStream;

/**
 * This is a special implementation of the table loader that targets the item table file.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class TableLoaderItems extends TableLoader {
    /**
     * The item mode value for a simple item without animation and variances.
     */
    public static final int ITEM_MODE_SIMPLE = 0;

    /**
     * The item mode value for a item with a animated graphic.
     */
    public static final int ITEM_MODE_ANIMATION = 1;

    /**
     * The item mode value for a item with graphical variances.
     */
    public static final int ITEM_MODE_VARIANCES = 2;

    /**
     * The table index that stores the alpha modifier that shall be applied to the original color of this avatar
     * graphic.
     */
    private static final int TB_COLORMOD_ALPHA = 24;

    /**
     * The table index that stores the blue color modifier that shall be applied to the original color of this avatar
     * graphic.
     */
    private static final int TB_COLORMOD_BLUE = 23;

    /**
     * The table index that stores the green color modifier that shall be applied to the original color of this
     * avatar graphic.
     */
    private static final int TB_COLORMOD_GREEN = 22;

    /**
     * The table index that stores the red color modifier that shall be applied to the original color of this avatar
     * graphic.
     */
    private static final int TB_COLORMOD_RED = 21;

    /**
     * The table index of the item face that is used to determine the direction the item accepts light from and the
     * directions the item blocks the light from.
     */
    private static final int TB_FACE = 8;

    /**
     * The table index of the frame count of this item in the definition table.
     */
    private static final int TB_FRAME = 2;

    /**
     * The table index of the item id in the definition table.
     */
    private static final int TB_ID = 0;

    /**
     * The table index of the surface level of the item.
     */
    private static final int TB_LEVEL = 18;

    /**
     * The table index of the encoded value of the light that is emitted by this tem.
     */
    private static final int TB_LIGHT = 20;

    /**
     * The table index of the group for the map editor.
     */
    private static final int TB_MAP_EDITOR_GROUP = 19;

    /**
     * The table index of the mode of the item that is used to determine if the item is a animated one or one with
     * variances.
     */
    private static final int TB_MODE = 3;

    /**
     * The table index of the flag if the item is move able so the client has to allow to drag it around or not.
     */
    private static final int TB_MOVABLE = 9;

    /**
     * The table index of the item resource name, so the base name of the images for this item,
     * in the definition table.
     */
    private static final int TB_NAME = 1;

    /**
     * The table index of the flag if the item is obstacle or not.
     */
    private static final int TB_OBSTACLE = 15;

    /**
     * The table index of the x offset of the item graphic in the definition table.
     */
    private static final int TB_OFFX = 4;

    /**
     * The table index of the y offset of the item graphic in the definition
     * table.
     */
    private static final int TB_OFFY = 5;

    /**
     * The table index of the opacity of the item, so the value in percent the item blocks the line of sight.
     */
    private static final int TB_OPACITY = 14;

    /**
     * The table index of the reference ID to the paperdolling items.
     */
    private static final int TB_PAPERDOLL_REF_ID = 25;

    /**
     * The table index of the shadow offset that marks the area of the item graphic that is the shadow. This area is
     * not faded out in case it intersects the fading corridor of the player character.
     */
    private static final int TB_SHADOW = 11;

    /**
     * The table index of the special item flag that encodes if the item is a container, a book or a Jesus-Item(c).
     */
    private static final int TB_SPECIAL = 12;

    /**
     * The table index of the frame animation speed of this item in the definition table.
     */
    private static final int TB_SPEED = 6;

    /**
     * The table index of the variance value, so the value in percent the item graphic can be scaled up or down.
     */
    private static final int TB_VARIANCE = 13;

    public TableLoaderItems(@Nonnull final TableLoaderSink<TableLoaderItems> callback) {
        this("Items", callback);
    }

    public TableLoaderItems(final String table, @Nonnull final TableLoaderSink<TableLoaderItems> callback) {
        super(table, true, callback, ",");
    }

    public TableLoaderItems(final InputStream resource, @Nonnull final TableLoaderSink<TableLoaderItems> callback) {
        super(resource, true, callback, ",");
    }

    /**
     * Get the speed of the animation of this item.
     *
     * @return the animation speed of the item
     */
    public int getAnimationSpeed() {
        return getInt(TB_SPEED);
    }

    /**
     * Get the alpha component of the modification color that shall be applied to the paperdolling graphic that is
     * assigned to this item.
     *
     * @return the color value as values between {@code 0} and {@code 255}
     */
    public int getColorModAlpha() {
        return getInt(TB_COLORMOD_ALPHA);
    }

    /**
     * Get the blue component of the modification color that shall be applied to the paperdolling graphic that is
     * assigned to this item.
     *
     * @return the color value as values between {@code 0} and {@code 255}
     */
    public int getColorModBlue() {
        return getInt(TB_COLORMOD_BLUE);
    }

    /**
     * Get the green component of the modification color that shall be applied to the paperdolling graphic that is
     * assigned to this item.
     *
     * @return the color value as values between {@code 0} and {@code 255}
     */
    public int getColorModGreen() {
        return getInt(TB_COLORMOD_GREEN);
    }

    /**
     * Get the red component of the modification color that shall be applied to the paperdolling graphic that is
     * assigned to this item.
     *
     * @return the color value as values between {@code 0} and {@code 255}
     */
    public int getColorModRed() {
        return getInt(TB_COLORMOD_RED);
    }

    /**
     * Get the facing flag of the item. This is used to determine the directions the item accepts light from and the
     * directions it blocks the light.
     *
     * @return the facing value of this item
     * @see ItemInfo#getFace()
     */
    public int getFace() {
        return getInt(TB_FACE);
    }

    /**
     * Get the count of frames of this item.
     *
     * @return the frame count of this item
     */
    public int getFrameCount() {
        return getInt(TB_FRAME);
    }

    /**
     * Get the ID of the item.
     *
     * @return the item ID
     */
    public int getItemId() {
        return getInt(TB_ID);
    }

    /**
     * Get the data of the light that is emitted by this item.
     *
     * @return the light emitted by this item
     * @see ItemInfo#getLight()
     */
    public int getItemLight() {
        return getInt(TB_LIGHT);
    }

    /**
     * Get the mode value of the item. This stores if its a simple item, a animated item or a item with variances.
     *
     * @return the mode of the item
     * @see #ITEM_MODE_ANIMATION
     * @see #ITEM_MODE_SIMPLE
     * @see #ITEM_MODE_VARIANCES
     */
    public int getItemMode() {
        return getInt(TB_MODE);
    }

    /**
     * Get the ID of the map editor group that is assigned to this item.
     *
     * @return the map editor group ID
     */
    public int getMapEditorGroup() {
        return getInt(TB_MAP_EDITOR_GROUP);
    }

    /**
     * The X-component of the offset of the item graphics.
     *
     * @return the offset component that needs to be applied to the item graphics
     */
    public int getOffsetX() {
        return getInt(TB_OFFX);
    }

    /**
     * The Y-component of the offset of the item graphics.
     *
     * @return the offset component that needs to be applied to the item graphics
     */
    public int getOffsetY() {
        return getInt(TB_OFFY);
    }

    /**
     * Get the opacity of the item. This is used to calculate how much the line of sight is blocked by this item.
     *
     * @return the opacity of the item
     */
    public int getOpacity() {
        return getInt(TB_OPACITY);
    }

    /**
     * Get the ID of the paperdolling item that is supposed to be displayed in case the player wears this item.
     *
     * @return the ID of the paperdolling item
     */
    public int getPaperdollingItemId() {
        return getInt(TB_PAPERDOLL_REF_ID);
    }

    /**
     * Get the name of the resources assigned to this item. This name can be used to request the graphics for this item
     * from the texture loader.
     *
     * @return the name of the resources assigned to this item
     */
    public String getResourceName() {
        return getString(TB_NAME);
    }

    /**
     * Get the width of the shadow of this item. This marks the area of the texture graphics that is ignored when the
     * intersection of the player fading corridor and the outline of the item graphics are tested.
     *
     * @return the width of the item shadow
     */
    public int getShadowOffset() {
        return getInt(TB_SHADOW);
    }

    /**
     * Get the allowed size variance of the item.
     *
     * @return the size variance of the item
     */
    public int getSizeVariance() {
        return getInt(TB_VARIANCE);
    }

    /**
     * Get the special flag of the item. This is used to find out if the item is a container, a book or a jesus-item.
     *
     * @return the special flag of the item
     * @see ItemInfo#isBook()
     * @see ItemInfo#isContainer()
     * @see ItemInfo#isJesus()
     */
    public int getSpecialFlag() {
        return getInt(TB_SPECIAL);
    }

    /**
     * Get the surface level of this item. This is used for items like tables where other items need to be placed on
     * top of the item.
     *
     * @return the surface level of the item
     */
    public int getSurfaceLevel() {
        return getInt(TB_LEVEL);
    }

    /**
     * Check if the item is movable.
     *
     * @return {@code true} in case the player can move the item
     */
    public boolean isMovable() {
        return getBoolean(TB_MOVABLE);
    }

    /**
     * Check if this item is a obstacle. Obstacle items block the tiles they are placed on.
     *
     * @return {@code true} if the item is a obstacle
     */
    public boolean isObstacle() {
        return getBoolean(TB_OBSTACLE);
    }
}
