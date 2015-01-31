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
package illarion.common.util;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

/**
 * This is a special implementation of the table loader that targets the clothes table file.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class TableLoaderClothes extends TableLoader {
    /**
     * The table index that stores the number of frames of the animation.
     */
    private static final int TB_FRAME = 1;

    /**
     * The table index that stores the body location of the graphic.
     */
    private static final int TB_LOCATION = 5;

    /**
     * The table index that stores if the graphic shall be mirrored.
     */
    private static final int TB_MIRROR = 11;

    /**
     * The table index of the file name of the cloth that shall be displayed.
     */
    private static final int TB_NAME = 0;

    /**
     * The table index that stores the x offset of the graphic.
     */
    private static final int TB_OFFSET_X = 3;

    /**
     * The table index that stores the y offset of the graphic.
     */
    private static final int TB_OFFSET_Y = 4;

    /**
     * The table index that stores the avatar ID this cloth is assigned to.
     */
    private static final int TB_REF_CHAR_ID = 10;

    /**
     * The table index that stores the item ID this cloth is assigned to.
     */
    private static final int TB_REF_ITEM_ID = 6;

    public TableLoaderClothes(@Nonnull TableLoaderSink<TableLoaderClothes> callback) {
        super("Cloth", callback);
    }

    /**
     * Get the amount of frames of the animation of this cloth.
     *
     * @return the frame count of the avatar
     */
    @Contract(pure = true)
    public int getFrameCount() {
        return getInt(TB_FRAME);
    }

    /**
     * The X-component of the offset of the cloth graphics.
     *
     * @return the offset component that needs to be applied to the cloth graphics
     */
    @Contract(pure = true)
    public int getOffsetX() {
        return getInt(TB_OFFSET_X);
    }

    /**
     * The Y-component of the offset of the cloth graphics.
     *
     * @return the offset component that needs to be applied to the cloth graphics
     */
    @Contract(pure = true)
    public int getOffsetY() {
        return getInt(TB_OFFSET_Y);
    }

    /**
     * Check if the graphics of this cloth are supposed to be displayed mirrored.
     *
     * @return {@code true} in case the graphics are supposed to be rendered mirrored
     */
    @Contract(pure = true)
    public boolean isMirrored() {
        return getBoolean(TB_MIRROR);
    }

    /**
     * Get the resource name of the effect. This name is supposed to be used to fetch the graphics of this avatar from
     * the resource loader.
     *
     * @return the resource name of this effect
     */
    @Nonnull
    @Contract(pure = true)
    public String getResourceName() {
        return getString(TB_NAME);
    }

    /**
     * Get the ID of the avatar this clothes graphics belong to.
     *
     * @return the ID of character this clothes belong to
     */
    @Contract(pure = true)
    public int getReferenceCharacterId() {
        return getInt(TB_REF_CHAR_ID);
    }

    /**
     * Get the ID of the item this cloth belong to.
     *
     * @return the ID of the item connected to this piece of cloth
     */
    @Contract(pure = true)
    public int getReferenceItemId() {
        return getInt(TB_REF_ITEM_ID);
    }

    /**
     * Get the body slot this cloth belong to.
     *
     * @return the slot on the body this cloth belong to
     */
    @Contract(pure = true)
    public int getClothSlot() {
        return getInt(TB_LOCATION);
    }
}
