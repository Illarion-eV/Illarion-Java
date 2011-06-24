/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.parsed;

import java.io.IOException;
import java.io.Writer;

import javolution.context.ObjectFactory;

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

/**
 * This parsed color class stores the color values for the NPC.
 * 
 * @author Martin Karing
 * @version 1.00
 * @since 1.02
 */
public final class ParsedColors implements ParsedData {
    /**
     * The factory for the parsed colors. This stores all formerly created and
     * currently unused instances of the ParsedColors class.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ParsedColorsFactory extends
        ObjectFactory<ParsedColors> {
        /**
         * Public constructor to allow the parent class to create a proper
         * instance.
         */
        public ParsedColorsFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of the recycled object.
         */
        @Override
        protected ParsedColors create() {
            return new ParsedColors();
        }
    }

    /**
     * The type constant to set this a hair constant.
     */
    public static final int HAIR_COLOR = 1;

    /**
     * The constant to set this a skin color.
     */
    public static final int SKIN_COLOR = 0;

    /**
     * The instance of the factory used to create the objects for this class.
     */
    private static final ParsedColorsFactory FACTORY =
        new ParsedColorsFactory();

    /**
     * The target of this color. Either this color is a skin color (
     * {@link #SKIN_COLOR}) or a hair color ({@link #HAIR_COLOR}).
     */
    private int colorTarget;

    /**
     * This array stores the three color values of this color.
     */
    private final int colorValue[];

    /**
     * Create a parsed color entry.
     */
    ParsedColors() {
        colorValue = new int[3];
        reset();
    }

    /**
     * Get a new instance of this class storing the set values.
     * 
     * @param type the type of the color, valid values are {@link #SKIN_COLOR}
     *            and {@link #HAIR_COLOR}.
     * @param red the red share of the color. Valid values from 0 to 255
     * @param green the green share of the color. Valid values from 0 to 255
     * @param blue the blue share of the color. Valid values from 0 to 255
     * @return the instance of the parsed colors storing the set values
     */
    public static ParsedColors getInstance(final int type, final int red,
        final int green, final int blue) {
        final ParsedColors result = FACTORY.object();
        result.setData(type, red, green, blue);
        return result;
    }

    /**
     * Insert the needed values into the SQL query.
     */
    @SuppressWarnings("nls")
    @Override
    public void buildSQL(final SQLBuilder builder) {
        if (colorTarget == SKIN_COLOR) {
            builder.setNpcSkinColor(colorValue[0], colorValue[1],
                colorValue[2]);
        } else if (colorTarget == HAIR_COLOR) {
            builder.setNpcHairColor(colorValue[0], colorValue[1],
                colorValue[2]);
        } else {
            throw new IllegalStateException("Illegal color target");
        }
    }

    /**
     * Check if the current script writing stage is effected by this color
     * parser.
     */
    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return (stage == EasyNpcWriter.WritingStage.color);
    }

    /**
     * The color values are not written into the LUA script.
     * 
     * @return <code>false</code> at all times
     */
    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return false;
    }

    /**
     * This colors are not insert into the LUA script, so not modules are
     * needed.
     * 
     * @return <code>null</code> at all times
     */
    @Override
    public String[] getRequiredModules() {
        return null;
    }

    /**
     * Place the created instance back into the factory for later usage.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Cleanup this object so it can be used again later.
     */
    @Override
    public void reset() {
        // nothing to reset
    }

    /**
     * Write the parsed data to the easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(final Writer target,
        final EasyNpcWriter.WritingStage stage) throws IOException {
        if (!effectsEasyNpcStage(stage)) {
            return;
        }

        if (colorTarget == SKIN_COLOR) {
            target.write("colorSkin = ");
        } else if (colorTarget == HAIR_COLOR) {
            target.write("colorHair = ");
        } else {
            throw new IllegalStateException("Illegal color target");
        }

        target.write(Integer.toString(colorValue[0]));
        target.write(", ");
        target.write(Integer.toString(colorValue[1]));
        target.write(", ");
        target.write(Integer.toString(colorValue[2]));
        target.write(illarion.easynpc.writer.EasyNpcWriter.NL);
    }

    /**
     * Since the color values are not written into the LUA script, this function
     * does nothing at all.
     */
    @Override
    public void writeLua(final Writer target,
        final LuaWriter.WritingStage stage) throws IOException {
        // nothing to do
    }

    /**
     * Set the data needed for this instance of parsed colors.
     * 
     * @param type the type of the color, valid values are {@link #SKIN_COLOR}
     *            and {@link #HAIR_COLOR}.
     * @param red the red share of the color. Valid values from 0 to 255
     * @param green the green share of the color. Valid values from 0 to 255
     * @param blue the blue share of the color. Valid values from 0 to 255
     */
    private void setData(final int type, final int red, final int green,
        final int blue) {
        colorTarget = type;
        colorValue[0] = red;
        colorValue[1] = green;
        colorValue[2] = blue;
    }
}
