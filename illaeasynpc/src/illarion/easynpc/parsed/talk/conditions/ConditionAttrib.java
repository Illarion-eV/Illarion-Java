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
package illarion.easynpc.parsed.talk.conditions;

import java.io.IOException;
import java.io.Writer;

import javolution.context.ObjectFactory;

import illarion.easynpc.data.CharacterAttribute;
import illarion.easynpc.data.CompareOperators;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkCondition;

/**
 * This class is used to store all required values for the attribute condition.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConditionAttrib implements TalkCondition {
    /**
     * The factory class that creates and buffers ConditionAttrib objects for
     * later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConditionAttribFactory extends
        ObjectFactory<ConditionAttrib> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConditionAttribFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConditionAttrib create() {
            return new ConditionAttrib();
        }
    }

    /**
     * The code needed for this condition in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "attrib(%1$s) %2$s %3$s";

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConditionAttribFactory FACTORY =
        new ConditionAttribFactory();

    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addCondition(%1$s.attribute(\"%2$s\", \"%3$s\", %4$s));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA module required for this condition to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "attribute";

    /**
     * The attribute that is checked with this condition.
     */
    private CharacterAttribute attrib;

    /**
     * The compare operator.
     */
    private CompareOperators operator;

    /**
     * The value the attribute is compared with.
     */
    private AdvancedNumber value;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConditionAttrib() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConditionAttrib getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the LUA module needed for this condition.
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
     * Set the data for this attribute condition.
     * 
     * @param attribData the attribute of this attribute condition
     * @param op the compare operator
     * @param newValue the value the attribute is compared against
     */
    public void setData(final CharacterAttribute attribData,
        final CompareOperators op, final AdvancedNumber newValue) {
        attrib = attribData;
        operator = op;
        value = newValue;
    }

    /**
     * Write this attribute condition into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, attrib.name(),
            operator.getLuaComp(), value.getEasyNPC()));
    }

    /**
     * Write the LUA code needed for this attribute condition.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, attrib.name(),
            operator.getLuaComp(), value.getLua()));
    }
}
