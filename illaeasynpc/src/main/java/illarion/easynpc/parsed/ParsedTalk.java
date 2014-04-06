/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class represents a parsed talking line that stores the conditions and consequences stored in this line.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedTalk implements ParsedData {

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
        conditions = new ArrayList<>();
        consequences = new ArrayList<>();
    }

    /**
     * Add the condition to the parsed line.
     *
     * @param con the condition to add
     */
    public void addCondition(TalkCondition con) {
        conditions.add(con);
    }

    /**
     * Add the consequence to the parsed line.
     *
     * @param con the consequence to add
     */
    public void addConsequence(TalkConsequence con) {
        consequences.add(con);
    }

    /**
     * Talking lines do not effect the SQL query.
     */
    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
        // nothing to add to the SQL query
    }

    /**
     * Check if the LUA code is effected at the current stage by this parsed
     * talking lines.
     */
    @Override
    public boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Talking;
    }

    /**
     * Get the list of modules this text line requires.
     */
    @Nonnull
    @Override
    public Collection<String> getRequiredModules() {
        Collection<String> moduleList = new ArrayList<>();

        for (TalkCondition condition : conditions) {
            String module = condition.getLuaModule();
            if (module != null) {
                moduleList.add(module);
            }
        }

        for (TalkConsequence consequence : consequences) {
            String module = consequence.getLuaModule();
            if (module != null) {
                moduleList.add(module);
            }
        }

        moduleList.add("npc.base.talk");
        moduleList.add("npc.base.basic");
        return moduleList;
    }

    /**
     * Write the LUA code needed for this talking line.
     */
    @Override
    public void writeLua(
            @Nonnull Writer target, @Nonnull LuaWriter.WritingStage stage) throws IOException {

        if (stage == LuaWriter.WritingStage.Talking) {
            target.write("if (true) then"); //$NON-NLS-1$
            target.write(LuaWriter.NL);
            target.write("local talkEntry = npc.base.talk.talkNPCEntry();"); //$NON-NLS-1$
            target.write(LuaWriter.NL);

            for (TalkCondition condition : conditions) {
                condition.writeLua(target);
            }
            for (TalkConsequence consequence : consequences) {
                consequence.writeLua(target);
            }

            target.write("talkingNPC:addTalkingEntry(talkEntry);"); //$NON-NLS-1$
            target.write(LuaWriter.NL);
            target.write("end;"); //$NON-NLS-1$
            target.write(LuaWriter.NL);
        }
    }
}
