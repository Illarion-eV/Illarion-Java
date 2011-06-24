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
package illarion.easynpc.parsed.talk.consequences;

import java.io.IOException;
import java.io.Writer;

import javolution.context.ObjectFactory;

import illarion.easynpc.data.CalculationOperators;
import illarion.easynpc.data.CharacterAttribute;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;

/**
 * This class is used to store all required values for the attribute
 * consequence.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConsequenceAttribute implements TalkConsequence {
    /**
     * The factory class that creates and buffers ConsequenceAttribute objects
     * for later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConsequenceAttributeFactory extends
        ObjectFactory<ConsequenceAttribute> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConsequenceAttributeFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConsequenceAttribute create() {
            return new ConsequenceAttribute();
        }
    }

    /**
     * The format string for the easyNPC code.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "attrib(%1$s) %2$s %3$s";

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConsequenceAttributeFactory FACTORY =
        new ConsequenceAttributeFactory();

    /**
     * The LUA code needed to be included for a attribute consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addConsequence(%1$s.attribute(\"%2$s\", \"%3$s\", %4$s));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The module used to access this attribute consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "attribute";

    /**
     * The attribute that is effected by this consequence.
     */
    private CharacterAttribute attrib;
    /**
     * The operator used to modify this attribute.
     */
    private CalculationOperators operator;

    /**
     * The number this attribute is modified by.
     */
    private AdvancedNumber value;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConsequenceAttribute() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConsequenceAttribute getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the LUA module required to use this attribute consequence.
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
        attrib = null;
        operator = null;
        if (value != null) {
            value.recycle();
            value = null;
        }
    }

    /**
     * Set the data required for this consequence.
     * 
     * @param newAttrib The attribute that is effected by the consequence
     * @param op The operator used to change the attribute
     * @param newValue the value the attribute is changed by
     */
    public void setData(final CharacterAttribute newAttrib,
        final CalculationOperators op, final AdvancedNumber newValue) {
        attrib = newAttrib;
        operator = op;
        value = newValue;
    }

    /**
     * Write this attribute consequence to its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, attrib.name(),
            operator.getLuaOp(), value.getEasyNPC()));
    }

    /**
     * Write the LUA code for this line to the target writer.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, attrib.name(),
            operator.getLuaOp(), value.getLua()));
    }
}
