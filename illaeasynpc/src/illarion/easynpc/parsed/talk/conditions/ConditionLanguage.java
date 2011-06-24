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

import illarion.easynpc.data.PlayerLanguage;
import illarion.easynpc.parsed.talk.TalkCondition;

/**
 * This class is used to store all required values for the language condition.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConditionLanguage implements TalkCondition {
    /**
     * The factory class that creates and buffers ConditionLanguage objects for
     * later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConditionLanguageFactory extends
        ObjectFactory<ConditionLanguage> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConditionLanguageFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConditionLanguage create() {
            return new ConditionLanguage();
        }
    }

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConditionLanguageFactory FACTORY =
        new ConditionLanguageFactory();

    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addCondition(%1$s.language(\"%2$s\"));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA module required for this condition to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "language";

    /**
     * The language the player language is compared against.
     */
    private PlayerLanguage lang;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConditionLanguage() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConditionLanguage getInstance() {
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
        lang = null;
    }

    /**
     * Set the data that is required for the language condition.
     * 
     * @param newLang the language that the player language is tested against
     */
    public void setData(final PlayerLanguage newLang) {
        lang = newLang;
    }

    /**
     * Write this language condition into its easyNPC change.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(lang.name());
    }

    /**
     * Write the LUA code needed for this language condition.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, lang.name()));
    }
}
