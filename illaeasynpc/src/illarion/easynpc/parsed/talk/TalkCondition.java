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
package illarion.easynpc.parsed.talk;

import java.io.IOException;
import java.io.Writer;

import illarion.common.util.Reusable;

/**
 * This interface is the common talking condition interface used to store the
 * conditions of a talking line.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public interface TalkCondition extends Reusable {
    /**
     * The base module of all condition.
     */
    @SuppressWarnings("nls")
    String BASE_LUA_MODULE = "npc.base.condition.";

    /**
     * Get the LUA module needed for this condition.
     * 
     * @return the LUA module needed for this condition
     */
    String getLuaModule();

    /**
     * Write the data of this talking condition to a easyNPC script.
     * 
     * @param target the writer that takes the data
     * @exception IOException thrown in case the writing operations fail.
     */
    void writeEasyNpc(Writer target) throws IOException;

    /**
     * Write the data of this talking condition to a LUA script.
     * 
     * @param target the writer that takes the data
     * @throws IOException thrown in case the writing operations fail
     */
    void writeLua(Writer target) throws IOException;
}
