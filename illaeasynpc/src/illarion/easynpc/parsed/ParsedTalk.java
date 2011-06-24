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
import javolution.util.FastList;
import javolution.util.FastTable;

import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

/**
 * This class represents a parsed talking line that stores the conditions and
 * consequences stored in this line.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ParsedTalk implements ParsedData {
    /**
     * The factory for the parsed talk. This stores all formerly created and
     * currently unused instances of the ParsedTalk class.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ParsedTalkFactory extends
        ObjectFactory<ParsedTalk> {
        /**
         * Public constructor to allow the parent class to create a proper
         * instance.
         */
        public ParsedTalkFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of the recycled object.
         */
        @Override
        protected ParsedTalk create() {
            return new ParsedTalk();
        }
    }

    /**
     * The instance of the factory used to create the objects for this class.
     */
    private static final ParsedTalkFactory FACTORY = new ParsedTalkFactory();

    /**
     * The string that is used as separator between two conditions or between
     * two consequences.
     */
    @SuppressWarnings("nls")
    private static final String seperator = ", ";

    /**
     * The list of conditions that are used in this line.
     */
    private FastTable<TalkCondition> conditions;

    /**
     * The list of consequences that are used in this line.
     */
    private FastTable<TalkConsequence> consequences;

    /**
     * The constructor that prepares this class to store all values properly.
     */
    ParsedTalk() {
        // nothing to do
    }

    /**
     * Get a newly created or a old reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ParsedTalk getInstance() {
        final ParsedTalk result = FACTORY.object();
        result.prepareLists();
        return result;
    }

    /**
     * Add the condition to the parsed line.
     * 
     * @param con the condition to add
     */
    public void addCondition(final TalkCondition con) {
        conditions.add(con);
    }

    /**
     * Add the consequence to the parsed line.
     * 
     * @param con the consequence to add
     */
    public void addConsequence(final TalkConsequence con) {
        consequences.add(con);
    }

    /**
     * Talking lines do not effect the SQL query.
     */
    @Override
    public void buildSQL(final SQLBuilder builder) {
        // nothing to add to the SQL query
    }

    /**
     * Check the stages effected by this talking line.
     */
    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return (stage == EasyNpcWriter.WritingStage.talking);
    }

    /**
     * Check if the LUA code is effected at the current stage by this parsed
     * talking lines.
     */
    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return (stage == LuaWriter.WritingStage.talking);
    }

    /**
     * Get the list of modules this text line requires.
     */
    @Override
    public String[] getRequiredModules() {
        final FastList<String> moduleList = FastList.newInstance();

        final int conditionCount = conditions.size();
        for (int i = 0; i < conditionCount; ++i) {
            final String module = conditions.get(i).getLuaModule();
            if (module != null) {
                moduleList.add(module);
            }
        }

        final int consequenceCount = consequences.size();
        for (int i = 0; i < consequenceCount; ++i) {
            final String module = consequences.get(i).getLuaModule();
            if (module != null) {
                moduleList.add(module);
            }
        }

        moduleList.add("npc.base.talk"); //$NON-NLS-1$
        moduleList.add("npc.base.basic"); //$NON-NLS-1$

        String[] result = new String[moduleList.size()];
        result = moduleList.toArray(result);
        FastList.recycle(moduleList);
        return result;
    }

    /**
     * Put the instance back into the storage for later usage.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset all stored values.
     */
    @Override
    public void reset() {
        if (conditions != null) {
            final int conditionCount = conditions.size();
            for (int i = 0; i < conditionCount; ++i) {
                conditions.get(i).recycle();
            }
            conditions.clear();
            FastTable.recycle(conditions);
            conditions = null;
        }
        if (consequences != null) {
            final int conditionCount = consequences.size();
            for (int i = 0; i < conditionCount; ++i) {
                consequences.get(i).recycle();
            }
            consequences.clear();
            FastTable.recycle(consequences);
            consequences = null;
        }
    }

    /**
     * Write this talking entry to a easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(final Writer target,
        final EasyNpcWriter.WritingStage stage) throws IOException {

        if (stage == EasyNpcWriter.WritingStage.talking) {
            final int conditionCount = conditions.size();
            for (int i = 0; i < conditionCount; ++i) {
                if (i > 0) {
                    target.write(seperator);
                }
                conditions.get(i).writeEasyNpc(target);
            }

            target.write(" -> ");

            final int consequenceCount = consequences.size();
            for (int i = 0; i < consequenceCount; ++i) {
                if (i > 0) {
                    target.write(seperator);
                }
                consequences.get(i).writeEasyNpc(target);
            }

            target.write(illarion.easynpc.writer.EasyNpcWriter.NL);
        }
    }

    /**
     * Write the LUA code needed for this talking line.
     */
    @Override
    public void writeLua(final Writer target,
        final LuaWriter.WritingStage stage) throws IOException {

        if (stage == LuaWriter.WritingStage.talking) {
            target.write("if (true) then"); //$NON-NLS-1$
            target.write(LuaWriter.NL);
            target.write("local talkEntry = npc.base.talk.talkNPCEntry();"); //$NON-NLS-1$
            target.write(LuaWriter.NL);

            final int conditionCount = conditions.size();
            for (int i = 0; i < conditionCount; ++i) {
                conditions.get(i).writeLua(target);
            }
            final int consequenceCount = consequences.size();
            for (int i = 0; i < consequenceCount; ++i) {
                consequences.get(i).writeLua(target);
            }

            target.write("talkingNPC:addTalkingEntry(talkEntry);"); //$NON-NLS-1$
            target.write(LuaWriter.NL);
            target.write("end;"); //$NON-NLS-1$
            target.write(LuaWriter.NL);
        }

    }

    /**
     * Create the lists in case its needed.
     */
    private void prepareLists() {
        if (conditions == null) {
            conditions = new FastTable<TalkCondition>();
        }
        if (consequences == null) {
            consequences = new FastTable<TalkConsequence>();
        }
    }
}
