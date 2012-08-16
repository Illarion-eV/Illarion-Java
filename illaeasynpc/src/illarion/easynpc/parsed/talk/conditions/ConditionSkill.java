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
package illarion.easynpc.parsed.talk.conditions;

import illarion.easynpc.data.CharacterSkill;
import illarion.easynpc.data.CompareOperators;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.writer.LuaWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the skill condition.
 *
 * @author Martin Karing
 */
public final class ConditionSkill implements TalkCondition {
    /**
     * The code needed for this condition in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "skill(%1$s) %2$s %3$s";

    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addCondition(%1$s.skill(%2$s, \"%3$s\", \"%4$s\", %5$s));"
            + LuaWriter.NL;

    /**
     * The LUA module required for this condition to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "skill";

    /**
     * The operator that is used to compare the skill value.
     */
    private final CompareOperators operator;
    /**
     * The skill that is checked by this skill condition.
     */
    private final CharacterSkill skill;

    /**
     * The value of the skill that is used for the compare operation.
     */
    private final AdvancedNumber value;

    /**
     * Constructor that allows setting the parameters of this skill test.
     *
     * @param skillData the skill used for this condition
     * @param op        the compare operator used for the condition
     * @param newValue  the value used to compare against
     */
    public ConditionSkill(final CharacterSkill skillData, final CompareOperators op, final AdvancedNumber newValue) {
        skill = skillData;
        operator = op;
        value = newValue;
    }

    /**
     * Get the LUA module needed for this condition.
     */
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write this skill condition into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, skill.getSkillName(), operator.getLuaComp(), value.getEasyNPC()));
    }

    /**
     * Write the LUA code needed for this race condition.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, Integer.toString(skill.getSkillGroup()),
                skill.getSkillName(), operator.getLuaComp(), value.getLua()));
    }
}
