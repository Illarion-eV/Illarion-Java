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
package illarion.easynpc.parsed.talk.conditions;

import java.io.IOException;
import java.io.Writer;

import javolution.context.ObjectFactory;

import illarion.easynpc.data.CharacterSkill;
import illarion.easynpc.data.CompareOperators;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkCondition;

/**
 * This class is used to store all required values for the skill condition.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConditionSkill implements TalkCondition {
    /**
     * The factory class that creates and buffers ConditionSkill objects for
     * later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConditionSkillFactory extends
        ObjectFactory<ConditionSkill> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConditionSkillFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConditionSkill create() {
            return new ConditionSkill();
        }
    }

    /**
     * The code needed for this condition in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "skill(%1$s) %2$s %3$s";

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConditionSkillFactory FACTORY =
        new ConditionSkillFactory();

    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addCondition(%1$s.skill(%2$s, \"%3$s\", \"%4$s\", %5$s));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA module required for this condition to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "skill";

    /**
     * The operator that is used to compare the skill value.
     */
    private CompareOperators operator;
    /**
     * The skill that is checked by this skill condition.
     */
    private CharacterSkill skill;

    /**
     * The value of the skill that is used for the compare operation.
     */
    private AdvancedNumber value;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConditionSkill() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConditionSkill getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the LUA module needed for this condition.
     */
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Recycle the object so it can be used again later.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset the state of this instance to its ready to be used later.
     */
    @Override
    public void reset() {
        operator = null;
        skill = null;
        if (value != null) {
            value.recycle();
            value = null;
        }
    }

    /**
     * Set the required data for this skill condition.
     * 
     * @param skillData the skill used for this condition
     * @param op the compare operator used for the condition
     * @param newValue the value used to compare against
     */
    public void setData(final CharacterSkill skillData,
        final CompareOperators op, final AdvancedNumber newValue) {
        skill = skillData;
        operator = op;
        value = newValue;
    }

    /**
     * Write this skill condition into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, skill.getSkillName(),
            operator.getLuaComp(), value.getEasyNPC()));
    }

    /**
     * Write the LUA code needed for this race condition.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE,
            Integer.toString(skill.getSkillGroup()), skill.getSkillName(),
            operator.getLuaComp(), value.getLua()));
    }
}
