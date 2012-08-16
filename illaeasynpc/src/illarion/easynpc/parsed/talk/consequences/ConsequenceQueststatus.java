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
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the quest status consequence.
 *
 * @author Martin Karing
 */
public final class ConsequenceQueststatus implements TalkConsequence {
    /**
     * The easyNPC code needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "queststatus(%1$s) %2$s %3$s";

    /**
     * The LUA code needed to be included for a quest status consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s.quest(%2$s, \"%3$s\", %4$s));"
            + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    @SuppressWarnings("nls")
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
     * @param questId  the ID of the quest that is changed with this consequence
     * @param op       the operator used to change the quest status
     * @param newValue the value used to change the quest status
     */
    public ConsequenceQueststatus(final int questId, final CalculationOperators op, final AdvancedNumber newValue) {
        operator = op;
        value = newValue;
        id = questId;
    }

    /**
     * Get the module that is needed for this consequence to work.
     */
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write this quest status consequence into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, Integer.toString(id), operator.getLuaOp(), value.getEasyNPC()));
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, Integer.toString(id), operator.getLuaOp(), value.getLua()));
    }
}
