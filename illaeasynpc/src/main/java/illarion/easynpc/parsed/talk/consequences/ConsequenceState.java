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

import illarion.easynpc.data.CalculationOperators;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the state consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceState implements TalkConsequence {
    /**
     * The LUA code needed to be included for a state consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s.state(\"%2$s\", %3$s));" + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "state";

    /**
     * The operator the NPC state is changed with.
     */
    private final CalculationOperators operator;

    /**
     * The number the NPC state is changed by.
     */
    private final AdvancedNumber value;

    /**
     * The constructor that allows setting the parameters of this state consequence.
     *
     * @param op the operator used to change the state
     * @param newValue the value the state is changed by
     */
    public ConsequenceState(final CalculationOperators op, final AdvancedNumber newValue) {
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
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(@Nonnull final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, operator.getLuaOp(), value.getLua()));
    }
}
