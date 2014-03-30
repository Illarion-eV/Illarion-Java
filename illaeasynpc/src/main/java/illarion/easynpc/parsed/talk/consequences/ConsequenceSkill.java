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
package illarion.easynpc.parsed.talk.consequences;

import illarion.common.data.Skill;
import illarion.easynpc.data.CalculationOperators;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the skill consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceSkill implements TalkConsequence {
    /**
     * The easyNPC code needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "skill(%1$s) %2$s %3$s";

    /**
     * The LUA code needed to be included for a skill consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
            "talkEntry:addConsequence(%1$s.skill(Character.%2$s, \"%3$s\", %4$s));" + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "skill";

    /**
     * The operator the skill is altered with.
     */
    private final CalculationOperators operator;

    /**
     * The skill effected by this skill consequence.
     */
    private final Skill skill;

    /**
     * The value the skill is altered with.
     */
    private final AdvancedNumber value;

    /**
     * The constructor that allows setting the parameters of this skill consequence.
     *
     * @param newSkill the skill effected by this consequence
     * @param op the operator used to change the skill
     * @param newValue the value used to change the skill
     */
    public ConsequenceSkill(
            final Skill newSkill, final CalculationOperators op, final AdvancedNumber newValue) {
        skill = newSkill;
        operator = op;
        value = newValue;
    }

    /**
     * Get the module that is needed for this consequence to work.
     */
    @Nonnull
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write this skill consequence into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(@Nonnull final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, skill.getName(), operator.getLuaOp(), value.getEasyNPC()));
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(@Nonnull final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, skill.getName(), operator.getLuaOp(), value.getLua()));
    }
}
