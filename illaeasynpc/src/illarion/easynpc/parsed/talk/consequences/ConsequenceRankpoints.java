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
package illarion.easynpc.parsed.talk.consequences;

import illarion.easynpc.data.CalculationOperators;
import illarion.easynpc.data.Towns;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the attribute consequence.
 *
 * @author Martin Karing
 */
public final class ConsequenceRankpoints implements TalkConsequence {
    /**
     * The easyNPC code needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "rankpoints(%1$s) %2$s %3$s";

    /**
     * The LUA code needed to be included for a quest status consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s.rankpoints(\"%2$s\", \"%3$s\", %4$s));"
            + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "rankpoints";

    /**
     * The operator used to alter the rankpoints.
     */
    private final CalculationOperators operator;

    /**
     * The town thats rankpoints are altered.
     */
    private final Towns town;

    /**
     * The value the rankpoints are altered with.
     */
    private final AdvancedNumber value;

    /**
     * Constructor that allows setting the parameter of this rank-point consequence.
     *
     * @param newTown  the town that rankpoints are altered
     * @param op       the operator the rankpoints are altered by
     * @param newValue the value the rankpoints are altered with
     */
    public ConsequenceRankpoints(final Towns newTown, final CalculationOperators op, final AdvancedNumber newValue) {
        town = newTown;
        operator = op;
        value = newValue;
    }

    /**
     * Get the module that is needed for this consequence to work.
     */
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write this rankpoint consequence into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, town.name(), operator.getLuaOp(), value.getEasyNPC()));
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, town.name(), operator.getLuaOp(), value.getLua()));
    }
}
