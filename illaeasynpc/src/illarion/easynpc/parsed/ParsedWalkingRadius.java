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

import illarion.easynpc.writer.EasyNpcWriter.WritingStage;
import illarion.easynpc.writer.SQLBuilder;

/**
 * This class is used to store a walking radius in the parsed NPC.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ParsedWalkingRadius implements ParsedData {
    /**
     * The factory for the parsed walking radius. This stores all formerly
     * created and currently unused instances of the ParsedWalkingRadius class.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ParsedWalkingRadiusFactory extends
        ObjectFactory<ParsedWalkingRadius> {
        /**
         * Public constructor to allow the parent class to create a proper
         * instance.
         */
        public ParsedWalkingRadiusFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of the recycled object.
         */
        @Override
        protected ParsedWalkingRadius create() {
            return new ParsedWalkingRadius();
        }
    }

    /**
     * The instance of the factory used to create the objects for this class.
     */
    private static final ParsedWalkingRadiusFactory FACTORY =
        new ParsedWalkingRadiusFactory();

    /**
     * The walking range that is defined by this command.
     */
    private int range;

    /**
     * Constructor to create new blank instances of this class.
     */
    ParsedWalkingRadius() {
        // nothing to do
    }

    /**
     * Get a newly created or a old reused instance of this class that stores
     * the set data.
     * 
     * @param newRange the walking range defined by this command
     * @return the instance with the data stored in
     */
    public static ParsedWalkingRadius getInstance(final int newRange) {
        final ParsedWalkingRadius result = FACTORY.object();
        result.setRadius(newRange);
        return result;
    }

    /**
     * No effect on the SQL query.
     */
    @Override
    public void buildSQL(final SQLBuilder builder) {
        // nothing to add to the query.
    }

    /**
     * Check the stage effected by this walking radius value.
     */
    @Override
    public boolean effectsEasyNpcStage(final WritingStage stage) {
        return (stage == WritingStage.header);
    }

    @Override
    public boolean effectsLuaWritingStage(
        final illarion.easynpc.writer.LuaWriter.WritingStage stage) {
        return false;
    }

    @Override
    public String[] getRequiredModules() {
        return null;
    }

    /**
     * Put the instance back into the factory for later usage.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * This function does nothing since this class does not store any data that
     * require a reset.
     */
    @Override
    public void reset() {
        // nothing to reset
    }

    /**
     * Write the walking radius to the easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(final Writer target, final WritingStage stage)
        throws IOException {
        if (stage == WritingStage.header) {
            target.write("radius = ");
            target.write(Integer.toString(range, 0));
            target.write(illarion.easynpc.writer.EasyNpcWriter.NL);
        }

    }

    @Override
    public void writeLua(final Writer target,
        final illarion.easynpc.writer.LuaWriter.WritingStage stage)
        throws IOException {
        // not implemented yet.
    }

    /**
     * Set the radius of stored in this instance.
     * 
     * @param newRange the walking range defined by this command
     */
    private void setRadius(final int newRange) {
        range = newRange;
    }
}
