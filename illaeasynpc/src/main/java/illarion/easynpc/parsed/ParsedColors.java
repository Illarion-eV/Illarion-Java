/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyNPC Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyNPC Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.parsed;

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

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
        Skin;
    }

    /**
     * The target of this color.
     */
    private final ParsedColors.ColorTarget colorTarget;

    /**
     * This variable stores the red component of the color.
     */
    private final int colorRed;

    /**
     * This variable stores the red component of the color.
     */
    private final int colorGreen;

    /**
     * This variable stores the red component of the color.
     */
    private final int colorBlue;

    /**
     * Create a parsed color entry.
     *
     * @param target the target of the color
     * @param red    the red share of the color. Valid values from 0 to 255
     * @param green  the green share of the color. Valid values from 0 to 255
     * @param blue   the blue share of the color. Valid values from 0 to 255
     */
    public ParsedColors(final ParsedColors.ColorTarget target, final int red, final int green, final int blue) {
        colorTarget = target;
        colorRed = red;
        colorGreen = green;
        colorBlue = blue;
    }

    /**
     * Insert the needed values into the SQL query.
     */
    @SuppressWarnings("nls")
    @Override
    public void buildSQL(@Nonnull final SQLBuilder builder) {
        switch (colorTarget) {
            case Skin:
                builder.setNpcSkinColor(colorRed, colorGreen, colorBlue);
                break;
            case Hair:
                builder.setNpcHairColor(colorRed, colorGreen, colorBlue);
                break;
        }
    }

    /**
     * Check if the current script writing stage is effected by this color parser.
     */
    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.color;
    }

    /**
     * The color values are not written into the LUA script.
     *
     * @return {@code false} at all times
     */
    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return false;
    }

    /**
     * This colors are not insert into the LUA script, so not modules are needed.
     *
     * @return {@code null} at all times
     */
    @Nullable
    @Override
    public String[] getRequiredModules() {
        return null;
    }

    /**
     * Write the parsed data to the easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(@Nonnull final Writer target, final EasyNpcWriter.WritingStage stage) throws IOException {
        if (!effectsEasyNpcStage(stage)) {
            return;
        }

        switch (colorTarget) {
            case Skin:
                target.write("colorSkin = ");
                break;
            case Hair:
                target.write("colorHair = ");
                break;
        }

        target.write(Integer.toString(colorRed));
        target.write(", ");
        target.write(Integer.toString(colorGreen));
        target.write(", ");
        target.write(Integer.toString(colorBlue));
        target.write(EasyNpcWriter.NL);
    }

    /**
     * Since the color values are not written into the LUA script, this function does nothing at all.
     */
    @Override
    public void writeLua(final Writer target,
                         final LuaWriter.WritingStage stage) throws IOException {
        // nothing to do
    }
}
