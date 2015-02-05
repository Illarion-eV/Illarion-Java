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
 * This is a special implementation of the table loader that targets the effects table file.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class TableLoaderEffects extends TableLoader {
    /**
     * The table index of the column that stores the amount of frames of the effect animation.
     */
    private static final int TB_FRAME = 2;

    /**
     * The table index of the column that stores the effect ID.
     */
    private static final int TB_ID = 0;

    /**
     * The table index of the column that stores the encoded light value that is emitted by the effect.
     */
    private static final int TB_LIGHT = 7;

    /**
     * The table index of the column that stores the base filename of the effect graphics.
     */
    private static final int TB_NAME = 1;

    /**
     * The table index of the column that stores the x offset of the effect graphic.
     */
    private static final int TB_OFFX = 3;

    /**
     * The table index of the column that stores the y offset of the effect graphic.
     */
    private static final int TB_OFFY = 4;

    /**
     * The table index of the column that stores the speed of the effect animation.
     */
    private static final int TB_SPEED = 5;

    public TableLoaderEffects(@Nonnull TableLoaderSink<TableLoaderEffects> callback) {
        super("Effects", callback);
    }

    /**
     * Get the animation speed of this effect.
     *
     * @return the animation speed of the effect
     */
    @Contract(pure = true)
    public int getAnimationSpeed() {
        return getInt(TB_SPEED);
    }

    /**
     * Get the ID of this effect.
     *
     * @return the ID of the effect
     */
    @Contract(pure = true)
    public int getEffectId() {
        return getInt(TB_ID);
    }

    /**
     * Get the data of the light that is emitted by this effect.
     *
     * @return the light emitted by this effect
     */
    @Contract(pure = true)
    public int getEffectLight() {
        return getInt(TB_LIGHT);
    }

    /**
     * Get the amount of frames of the animation of this effect.
     *
     * @return the frame count of the effect
     */
    @Contract(pure = true)
    public int getFrameCount() {
        return getInt(TB_FRAME);
    }

    /**
     * The X-component of the offset of the effect graphics.
     *
     * @return the offset component that needs to be applied to the item graphics
     */
    @Contract(pure = true)
    public int getOffsetX() {
        return getInt(TB_OFFX);
    }

    /**
     * The Y-component of the offset of the effect graphics.
     *
     * @return the offset component that needs to be applied to the item graphics
     */
    @Contract(pure = true)
    public int getOffsetY() {
        return getInt(TB_OFFY);
    }

    /**
     * Get the resource name of the effect. This name is supposed to be used to fetch the graphics of this tile from
     * the resource loader.
     *
     * @return the resource name of this effect
     */
    @Nonnull
    @Contract(pure = true)
    public String getResourceName() {
        return getString(TB_NAME);
    }
}
