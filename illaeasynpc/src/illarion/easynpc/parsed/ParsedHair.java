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
 * This parsed data storage is able to store the hair or the beard ID of the
 * NPC.
 * 
 * @author Martin Karing
 * @version 1.00
 * @since 1.02
 */
public final class ParsedHair implements ParsedData {
    /**
     * The factory for the parsed hair. This stores all formerly created and
     * currently unused instances of the ParsedHair class.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ParsedHairFactory extends
        ObjectFactory<ParsedHair> {
        /**
         * Public constructor to allow the parent class to create a proper
         * instance.
         */
        public ParsedHairFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of the recycled object.
         */
        @Override
        protected ParsedHair create() {
            return new ParsedHair();
        }
    }

    /**
     * The hair type constant for "Beard".
     */
    public static final int TYPE_BEARD = 0;

    /**
     * The hair type constant for "Hair".
     */
    public static final int TYPE_HAIR = 1;

    /**
     * The instance of the factory used to create the objects for this class.
     */
    private static final ParsedHairFactory FACTORY = new ParsedHairFactory();

    /**
     * The ID of the hair.
     */
    private int hairId;

    /**
     * The type of the hair.
     */
    private int hairType;

    /**
     * The constructor for this parsed hair data.
     */
    ParsedHair() {
        // nothing to do
    }

    /**
     * Get a newly created or a old reused instance of this class that stores
     * the set data.
     * 
     * @param type the type of the hair. Valid values are {@link #TYPE_BEARD}
     *            and {@link #TYPE_HAIR}
     * @param id the ID of the hair
     * @return the instance that stores the set data
     */
    public static ParsedHair getInstance(final int type, final int id) {
        final ParsedHair result = FACTORY.object();
        result.setHair(type, id);
        return result;
    }

    /**
     * Add the informations about the hair and the beard to the SQL query.
     */
    @Override
    @SuppressWarnings("nls")
    public void buildSQL(final SQLBuilder builder) {
        if (hairType == TYPE_BEARD) {
            builder.setNpcBeard(hairId);
        } else if (hairType == TYPE_HAIR) {
            builder.setNpcHair(hairId);
        } else {
            throw new IllegalArgumentException("Invalid type: " + hairType);
        }
    }

    /**
     * Check if the selected stage is effected by the data stored in this. This
     * is only <code>true</code> for the "hair" stage.
     */
    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return (stage == EasyNpcWriter.WritingStage.hair);
    }

    /**
     * Hair informations are not written into the LUA script. No stage is
     * effected.
     */
    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return false;
    }

    /**
     * No additional modules needed for this.
     */
    @Override
    public String[] getRequiredModules() {
        return null;
    }

    /**
     * Recycle this instance for later usage.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * There are no variables in this class that require a reset.
     */
    @Override
    public void reset() {
        // nothing to do
    }

    /**
     * Write the data stored in this line to the easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(final Writer target,
        final EasyNpcWriter.WritingStage stage) throws IOException {
        if (!effectsEasyNpcStage(stage)) {
            return;
        }

        if (hairType == TYPE_BEARD) {
            target.write("beardID = ");
        } else if (hairType == TYPE_HAIR) {
            target.write("hairID = ");
        } else {
            throw new IllegalArgumentException("Invalid type: " + hairType);
        }

        target.write(Integer.toString(hairId));
        target.write(EasyNpcWriter.NL);
    }

    /**
     * The LUA script is not effected by this data. This function does nothing
     * at all in this case.
     */
    @Override
    public void writeLua(final Writer target,
        final LuaWriter.WritingStage stage) throws IOException {
        // nothing
    }

    /**
     * Set the hair data stored in this instance.
     * 
     * @param type the type of the hair. Valid values are {@link #TYPE_BEARD}
     *            and {@link #TYPE_HAIR}
     * @param id the ID of the hair
     */
    @SuppressWarnings("nls")
    private void setHair(final int type, final int id) {
        if ((type != TYPE_BEARD) && (type != TYPE_HAIR)) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        hairType = type;
        hairId = id;
    }

}
