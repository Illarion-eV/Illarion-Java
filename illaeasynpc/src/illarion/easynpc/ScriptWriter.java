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
package illarion.easynpc;

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * This class contains all required functions and constants to write a parsed NPC back into a LUA or a easyNPC Script.
 *
 * @author Martin Karing
 */
public final class ScriptWriter {
    /**
     * This enumerator contains the possible targets of the script writer.
     */
    public enum ScriptWriterTarget {
        /**
         * This target makes the writer generate a easyNPC script.
         */
        EasyNPC,

        /**
         * This target makes the writer generate a LUA script.
         */
        LUA;
    }

    /**
     * This writer will receive the written script.
     */
    private Writer scriptTarget;

    /**
     * This is the NPC that is the source for the writer. It has to deliver all data required in the scripts.
     */
    private ParsedNpc sourceNPC;

    /**
     * The language that is the target of the writer. Either LUA or easyNPC.
     */
    private ScriptWriter.ScriptWriterTarget targetLang;

    /**
     * Default constructor.
     */
    public ScriptWriter() {
        scriptTarget = null;
        targetLang = null;
        sourceNPC = null;
    }

    /**
     * Set the parsed NPC that is the data source of this writer.
     *
     * @param source the source of this writer
     */
    public void setSource(final ParsedNpc source) {
        sourceNPC = source;
    }

    /**
     * Set the target language of the script writer.
     *
     * @param lang the target language
     */
    @SuppressWarnings("nls")
    public void setTargetLanguage(final ScriptWriter.ScriptWriterTarget lang) {
        targetLang = lang;
    }

    /**
     * Set the writer that is supposed to receive the written script data.
     *
     * @param write the writer that receives the script data
     */
    public void setWritingTarget(final Writer write) {
        scriptTarget = write;
    }

    /**
     * Write the set NPC to a script.
     *
     * @throws IOException thrown in case writing to the assigned target fails
     */
    public void write() throws IOException {
        switch (targetLang) {
            case EasyNPC:
                EasyNpcWriter.getInstance().write(sourceNPC, scriptTarget);
                break;
            case LUA:
                LuaWriter.getInstance().write(sourceNPC, scriptTarget);
                break;
        }
    }
}
