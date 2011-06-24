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

import javolution.lang.Immutable;

import illarion.easynpc.parsed.talk.TalkCondition;

/**
 * This class is used to store all required values for the administrator
 * condition.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConditionAdmin implements Immutable, TalkCondition {
    /**
     * The instance of this class that is used in all cases.
     */
    private static final ConditionAdmin INSTANCE = new ConditionAdmin();

    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addCondition(%1$s.admin());"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA module required for this condition to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "admin";

    /**
     * Private constructor to ensure that only the singleton instance is
     * created.
     */
    private ConditionAdmin() {
        // nothing to do
    }

    /**
     * Get a usable instance of this class. Since this class does not store any
     * data, the same instance is used in any case.
     * 
     * @return the instance of this class that is free to be used
     */
    public static ConditionAdmin getInstance() {
        return INSTANCE;
    }

    /**
     * Get the LUA module needed for this condition to work.
     */
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * This function does nothing in this case, because this class is implement
     * as singleton and not as factory class.
     */
    @Override
    public void recycle() {
        // nothing to do
    }

    /**
     * This function does nothing in this case, since there are no values to
     * reset in this class.
     */
    @Override
    public void reset() {
        // nothing to do
    }

    /**
     * Write this talking state condition to its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write("isAdmin"); //$NON-NLS-1$
    }

    /**
     * Write the LUA code needed for this race condition.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE));
    }
}
