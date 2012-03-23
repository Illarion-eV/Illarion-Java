/*
 * This file is part of the Illarion easyGUI Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyGUI Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The Illarion easyGUI Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyGUI Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easygui.writer;

import java.io.IOException;
import java.io.Writer;

/**
 * This implements needs to be implemented in all objects that are able to
 * supply data to the {@link LuaWriter}. This interface
 * is used to fetch the data that needs to be written into the script.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public interface LuaWritable {
    /**
     * Build the SQL query.
     * 
     * @param builder the builder that is used to construct the SQL query
     */
    void buildSQL(final SQLBuilder builder);

    /**
     * Check if this LUA writable has any effect on a selected stage.
     * 
     * @param stage the selected stage
     * @return <code>true<code> in case this LUA writable effects the stage
     */
    boolean effectsLuaWritingStage(LuaWriter.WritingStage stage);

    /**
     * Get the list of modules required to have the code written by this
     * LuaWritable to work.
     * 
     * @return the list of required LUA modules
     */
    String[] getRequiredModules();

    /**
     * Write the LUA Code fitting into the currently selected writing stage.
     * 
     * @param target the writer that is supposed to receive the script data
     * @param stage the stage that is currently written
     * @throws java.io.IOException thrown in case a writing error occurs
     */
    void writeLua(Writer target, LuaWriter.WritingStage stage)
        throws IOException;
}
