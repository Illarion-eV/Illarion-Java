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

import illarion.easynpc.data.CharacterMagicType;
import illarion.easynpc.parsed.talk.TalkConsequence;

/**
 * This class is used to store all required values for the rune consequence.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConsequenceRune implements TalkConsequence {
    /**
     * The factory class that creates and buffers ConsequenceRune objects for
     * later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConsequenceRuneFactory extends
        ObjectFactory<ConsequenceRune> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConsequenceRuneFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConsequenceRune create() {
            return new ConsequenceRune();
        }
    }

    /**
     * The easyNPC code needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "rune(%1$s, %2$s)";

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConsequenceRuneFactory FACTORY =
        new ConsequenceRuneFactory();

    /**
     * The LUA code needed to be included for a rune consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addConsequence(%1$s.rune(%2$s, %3$s));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "rune";

    /**
     * The magic type the rune to learn is assigned to.
     */
    private CharacterMagicType magicType;
    /**
     * The number of the rune to learn.
     */
    private int value;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConsequenceRune() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConsequenceRune getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the module that is needed for this consequence to work.
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
        magicType = null;
    }

    /**
     * Set the data needed for this learn rune consequence.
     * 
     * @param newMagicType the magic type of the rune
     * @param newValue the number of the rune
     */
    public void setData(final CharacterMagicType newMagicType,
        final int newValue) {
        magicType = newMagicType;
        value = newValue;
    }

    /**
     * Write this rune consequence into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE,
            Integer.valueOf(magicType.getMagicTypeId()),
            Integer.toString(value)));
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE,
            Integer.valueOf(magicType.getMagicTypeId()),
            Integer.toString(value)));
    }
}
