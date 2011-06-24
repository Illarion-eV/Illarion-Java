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
package illarion.easynpc.writer;

import java.io.IOException;
import java.io.Writer;

/**
 * This implements needs to be implemented in all objects that are able to
 * supply data to the {@link illarion.easynpc.writer.LuaWriter}. This interface
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
     * @throws IOException thrown in case a writing error occurs
     */
    void writeLua(Writer target, LuaWriter.WritingStage stage)
        throws IOException;
}
