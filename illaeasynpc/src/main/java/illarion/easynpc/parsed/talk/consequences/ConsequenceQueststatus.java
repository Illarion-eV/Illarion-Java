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
import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the quest status consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceQueststatus implements TalkConsequence {
    /**
     * The LUA code needed to be included for a quest status consequence.
     */
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s.quest(%2$s, \"%3$s\", %4$s))" + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    private static final String LUA_MODULE = BASE_LUA_MODULE + "quest";

    /**
     * The ID of the quest that is changed with this consequence.
     */
    private final int id;

    /**
     * The operator used to change the quest status.
     */
    private final CalculationOperators operator;

    /**
     * The value that is used to change the quest status.
     */
    private final AdvancedNumber value;

    /**
     * The constructor that allows setting the operation that should be done with the quest status.
     *
     * @param questId the ID of the quest that is changed with this consequence
     * @param op the operator used to change the quest status
     * @param newValue the value used to change the quest status
     */
    public ConsequenceQueststatus(int questId, CalculationOperators op, AdvancedNumber newValue) {
        operator = op;
        value = newValue;
        id = questId;
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
    public void writeLua(@Nonnull Writer target, @Nonnull LuaRequireTable requires) throws IOException {
        target.write(String.format(LUA_CODE, requires.getStorage(LUA_MODULE), Integer.toString(id),
                                   operator.getLuaOp(), value.getLua()));
    }
}
