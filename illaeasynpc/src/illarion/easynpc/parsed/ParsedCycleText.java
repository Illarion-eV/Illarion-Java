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
 * This class is used to store a cycle texts of the NPC.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ParsedCycleText implements ParsedData {
    /**
     * The factory for the parsed cycle text. This stores all formerly created
     * and currently unused instances of the ParsedCycleText class.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ParsedCycleTextFactory extends
        ObjectFactory<ParsedCycleText> {
        /**
         * Public constructor to allow the parent class to create a proper
         * instance.
         */
        public ParsedCycleTextFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of the recycled object.
         */
        @Override
        protected ParsedCycleText create() {
            return new ParsedCycleText();
        }
    }

    /**
     * The instance of the factory used to create the objects for this class.
     */
    private static final ParsedCycleTextFactory FACTORY =
        new ParsedCycleTextFactory();

    /**
     * The LUA modules required for this cycle texts to work.
     */
    @SuppressWarnings("nls")
    private static final String[] LUA_MODULES =
        new String[] { "npc.base.talk" };

    /**
     * The English cycle text stored in this object.
     */
    private String english;

    /**
     * The German cycle text stored in this object.
     */
    private String german;

    /**
     * Default constructor that sets the text value that were defined for this
     * cycle text entry.
     */
    ParsedCycleText() {
        // nothing to do
    }

    /**
     * Create a new or reuse a old instance of this class and fill it with the
     * needed texts to store.
     * 
     * @param germanText the German version of the cycle text
     * @param englishText the English version of the cycle text
     * @return the instance that stores the set texts
     */
    public static ParsedCycleText getInstance(final String germanText,
        final String englishText) {
        final ParsedCycleText result = FACTORY.object();
        result.setText(germanText, englishText);
        return result;
    }

    /**
     * Cycle texts do not effect the SQL query.
     */
    @Override
    public void buildSQL(final SQLBuilder builder) {
        // nothing to add to the query.
    }

    /**
     * Check the stage that is effected by this cycle texts entry.
     */
    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return (stage == EasyNpcWriter.WritingStage.cycleTexts);
    }

    /**
     * Check if the selected stage is effected by this cycle text.
     */
    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return (stage == LuaWriter.WritingStage.cycleText);
    }

    /**
     * Get the modules required for this cycle text to work properly.
     */
    @Override
    public String[] getRequiredModules() {
        return LUA_MODULES;
    }

    /**
     * Put the instance back into the factory so it can be reused later.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset the instance so it can be reused later.
     */
    @Override
    public void reset() {
        german = null;
        english = null;
    }

    /**
     * Write the cycle texts out to the easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(final Writer target,
        final EasyNpcWriter.WritingStage stage) throws IOException {
        if (stage == EasyNpcWriter.WritingStage.cycleTexts) {
            target.write("cycletext \"");
            target.write(german);
            target.write("\", \"");
            target.write(english);
            target.write("\"");
            target.write(illarion.easynpc.writer.EasyNpcWriter.NL);
        }
    }

    /**
     * Write the LUA code required to ensure this cycle text is working.
     */
    @Override
    public void writeLua(final Writer target,
        final LuaWriter.WritingStage stage) throws IOException {
        if (stage == LuaWriter.WritingStage.cycleText) {
            target.write("talkingNPC:addCycleText(\""); //$NON-NLS-1$
            target.write(german);
            target.write("\", \""); //$NON-NLS-1$
            target.write(english);
            target.write("\");"); //$NON-NLS-1$
            target.write(illarion.easynpc.writer.LuaWriter.NL);
        }
    }

    /**
     * Set the text stored in this instance.
     * 
     * @param germanText the German version of the cycle text
     * @param englishText the English version of the cycle text
     */
    private void setText(final String germanText, final String englishText) {
        german = germanText;
        english = englishText;
    }
}
