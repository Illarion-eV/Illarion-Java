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
package illarion.easynpc.parsed.talk.conditions;

import illarion.easynpc.data.CompareOperators;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the state condition.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConditionState implements TalkCondition {
    /**
     * The code needed for this condition in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "state %1$s %2$s";

    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addCondition(%1$s.state(\"%2$s\", %3$s));" + LuaWriter.NL;

    /**
     * The LUA module required for this condition to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "state";

    /**
     * The operator that is used to compare the NPC state.
     */
    private final CompareOperators operator;

    /**
     * The value that is used to compare the NPC state.
     */
    private final AdvancedNumber value;

    /**
     * The constructor that is able to set the parameter of this state test.
     *
     * @param op the operator that is used to compare the NPC state with
     * @param newValue the value used to compare the NPC state to
     */
    public ConditionState(final CompareOperators op, final AdvancedNumber newValue) {
        operator = op;
        value = newValue;
    }

    /**
     * Get the LUA module needed for this condition.
     */
    @Nonnull
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write this NPC state condition into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(@Nonnull final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, operator.getLuaComp(), value.getEasyNPC()));
    }

    /**
     * Write the LUA code needed for this race condition.
     */
    @Override
    public void writeLua(@Nonnull final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, operator.getLuaComp(), value.getLua()));
    }
}
