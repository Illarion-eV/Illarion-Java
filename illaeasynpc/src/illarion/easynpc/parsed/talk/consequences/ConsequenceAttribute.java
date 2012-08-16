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
import illarion.easynpc.data.CharacterAttribute;
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
public final class ConsequenceAttribute implements TalkConsequence {
    /**
     * The format string for the easyNPC code.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "attrib(%1$s) %2$s %3$s";

    /**
     * The LUA code needed to be included for a attribute consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s.attribute(\"%2$s\", \"%3$s\", %4$s));"
            + LuaWriter.NL;

    /**
     * The module used to access this attribute consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "attribute";

    /**
     * The attribute that is effected by this consequence.
     */
    private final CharacterAttribute attrib;

    /**
     * The operator used to modify this attribute.
     */
    private final CalculationOperators operator;

    /**
     * The number this attribute is modified by.
     */
    private final AdvancedNumber value;

    /**
     * The constructor that allows setting the parameters of this attribute change.
     *
     * @param newAttrib The attribute that is effected by the consequence
     * @param op        The operator used to change the attribute
     * @param newValue  the value the attribute is changed by
     */
    public ConsequenceAttribute(final CharacterAttribute newAttrib, final CalculationOperators op,
                                final AdvancedNumber newValue) {
        attrib = newAttrib;
        operator = op;
        value = newValue;
    }

    /**
     * Get the LUA module required to use this attribute consequence.
     */
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write this attribute consequence to its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, attrib.name(), operator.getLuaOp(), value.getEasyNPC()));
    }

    /**
     * Write the LUA code for this line to the target writer.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, attrib.name(), operator.getLuaOp(), value.getLua()));
    }
}
