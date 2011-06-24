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
package illarion.easynpc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;

/**
 * This class contains all required functions and constants to write a parsed
 * NPC back into a LUA or a easyNPC Script.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class ScriptWriter {
    /**
     * This constant sets the Script writer to write a easyNPC script.
     */
    public static final int TARGET_EASY = 2;

    /**
     * This constant sets the Script writer to write a LUA NPC script.
     */
    public static final int TARGET_LUA = 1;

    /**
     * This writer will receive the written script.
     */
    private Writer scriptTarget;

    /**
     * This is the NPC that is the source for the writer. It has to deliver all
     * data required in the scripts.
     */
    private ParsedNpc sourceNPC;

    /**
     * The language that is the target of the writer. Either LUA or easyNPC.
     */
    private int targetLang;

    /**
     * Default constructor.
     */
    public ScriptWriter() {
        scriptTarget = null;
        targetLang = -1;
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
     * @param lang the target language. Either {@link #TARGET_EASY} or
     *            {@link #TARGET_LUA}
     */
    @SuppressWarnings("nls")
    public void setTargetLanguage(final int lang) {
        if ((lang != TARGET_LUA) && (lang != TARGET_EASY)) {
            throw new IllegalArgumentException("Language does not exist.");
        }

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
        if (targetLang == TARGET_EASY) {
            final BufferedWriter writer = new BufferedWriter(scriptTarget);
            EasyNpcWriter.getInstance().write(sourceNPC, writer);
            writer.flush();
        } else if (targetLang == TARGET_LUA) {
            final BufferedWriter writer = new BufferedWriter(scriptTarget);
            LuaWriter.getInstance().write(sourceNPC, writer);
            writer.flush();
        }
    }
}
