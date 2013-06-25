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

import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;
import javolution.util.FastList;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a parsed talking line that stores the conditions and consequences stored in this line.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedTalk implements ParsedData {
    /**
     * The string that is used as separator between two conditions or between two consequences.
     */
    @SuppressWarnings("nls")
    private static final String SEPARATOR = ", ";

    /**
     * The list of conditions that are used in this line.
     */
    @Nonnull
    private final List<TalkCondition> conditions;

    /**
     * The list of consequences that are used in this line.
     */
    @Nonnull
    private final List<TalkConsequence> consequences;

    /**
     * The constructor that prepares this class to store all values properly.
     */
    public ParsedTalk() {
        conditions = new ArrayList<TalkCondition>();
        consequences = new ArrayList<TalkConsequence>();
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
        return stage == EasyNpcWriter.WritingStage.talking;
    }

    /**
     * Check if the LUA code is effected at the current stage by this parsed
     * talking lines.
     */
    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Talking;
    }

    /**
     * Get the list of modules this text line requires.
     */
    @Nonnull
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
     * Write this talking entry to a easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(@Nonnull final Writer target,
                             final EasyNpcWriter.WritingStage stage) throws IOException {

        if (stage == EasyNpcWriter.WritingStage.talking) {
            final int conditionCount = conditions.size();
            for (int i = 0; i < conditionCount; ++i) {
                if (i > 0) {
                    target.write(SEPARATOR);
                }
                conditions.get(i).writeEasyNpc(target);
            }

            target.write(" -> ");

            final int consequenceCount = consequences.size();
            for (int i = 0; i < consequenceCount; ++i) {
                if (i > 0) {
                    target.write(SEPARATOR);
                }
                consequences.get(i).writeEasyNpc(target);
            }

            target.write(EasyNpcWriter.NL);
        }
    }

    /**
     * Write the LUA code needed for this talking line.
     */
    @Override
    public void writeLua(@Nonnull final Writer target,
                         final LuaWriter.WritingStage stage) throws IOException {

        if (stage == LuaWriter.WritingStage.Talking) {
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
}
