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
 * This class is used to store all required values for the money consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceMoney implements TalkConsequence {
    /**
     * The LUA code needed to be included for a money consequence.
     */
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s(\"%2$s\", %3$s))" + LuaWriter.NL;

    /**
     * The LUA module that is needed for this consequence to work.
     */
    private static final String LUA_MODULE = BASE_LUA_MODULE + "money";

    /**
     * The operator this money consequence works with.
     */
    private final CalculationOperators operator;

    /**
     * The count of copper coins the player money is altered by.
     */
    private final AdvancedNumber value;

    /**
     * The constructor that allows setting the operation that is supposed to be done with the money of the player.
     *
     * @param op the operator the money of the player is altered with
     * @param newValue the value the amount of player money is altered by
     */
    public ConsequenceMoney(CalculationOperators op, AdvancedNumber newValue) {
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
    public void writeLua(@Nonnull Writer target, @Nonnull LuaRequireTable requires) throws IOException {
        target.write(String.format(LUA_CODE, requires.getStorage(LUA_MODULE), operator.getLuaOp(), value.getLua()));
    }
}
