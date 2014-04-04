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
 * This class is used to store all required values for the quest status condition.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConditionQueststatus implements TalkCondition {
    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addCondition(%1$s.quest(%2$s, \"%3$s\", %4$s));" + LuaWriter.NL;

    /**
     * The LUA module required for this condition to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "quest";

    /**
     * The ID of the quest that is changed.
     */
    private final int id;

    /**
     * The operator that is used to compare with the player money.
     */
    private final CompareOperators operator;

    /**
     * The value used to compare the player money against.
     */
    private final AdvancedNumber value;

    /**
     * The constructor that takes the parameter for the quest status test.
     *
     * @param questId the ID of the quest that is changed
     * @param op the operator that is used to compare the quest status with
     * @param newValue the value the quest status is compared against
     */
    public ConditionQueststatus(final int questId, final CompareOperators op, final AdvancedNumber newValue) {
        operator = op;
        value = newValue;
        id = questId;
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
     * Write the LUA code needed for this quest status condition.
     */
    @Override
    public void writeLua(@Nonnull final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, Integer.toString(id), operator.getLuaComp(), value.getLua()));
    }
}
