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
package illarion.easynpc.parsed;

import illarion.easynpc.data.Color;
import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter.WritingStage;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;

/**
 * This parsed color class stores the color values for the NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedColors implements ParsedData {
    /**
     * This enumerator stores the possible values of the color target.
     */
    public enum ColorTarget {
        /**
         * If this value is set as color target, the color applied to the hair.
         */
        Hair,

        /**
         * If this value is set as color target, the color applies to the skin.
         */
        Skin
    }

    /**
     * The target of this color.
     */
    @Nonnull
    private final ColorTarget colorTarget;

    /**
     * The actual color value.
     */
    @Nonnull
    private final Color color;

    /**
     * Create a parsed color entry.
     *
     * @param target the target of the color
     * @param color the color stored in this parsed color
     */
    public ParsedColors(@Nonnull ColorTarget target, @Nonnull Color color) {
        colorTarget = target;
        this.color = color;
    }

    /**
     * Insert the needed values into the SQL query.
     */
    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
        switch (colorTarget) {
            case Skin:
                builder.setNpcSkinColor(color.getRed(), color.getGreen(), color.getBlue());
                break;
            case Hair:
                builder.setNpcHairColor(color.getRed(), color.getGreen(), color.getBlue());
                break;
        }
    }

    /**
     * The color values are not written into the LUA script.
     *
     * @return {@code false} at all times
     */
    @Override
    public boolean effectsLuaWritingStage(@Nonnull WritingStage stage) {
        return false;
    }

    /**
     * This colors are not insert into the LUA script, so not modules are needed.
     *
     * @return {@code null} at all times
     */
    @Nonnull
    @Override
    public Collection<String> getRequiredModules() {
        return Collections.emptyList();
    }

    /**
     * Since the color values are not written into the LUA script, this function does nothing at all.
     */
    @Override
    public void writeLua(
            @Nonnull Writer target, @Nonnull LuaRequireTable requires, @Nonnull WritingStage stage) throws
            IOException {
        // nothing to do
    }
}
