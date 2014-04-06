/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.easynpc;

import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

/**
 * This class contains all required functions and constants to write a parsed NPC back into a LUA or a easyNPC Script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ScriptWriter {
    /**
     * This writer will receive the written script.
     */
    @Nullable
    private Writer scriptTarget;

    /**
     * This is the NPC that is the source for the writer. It has to deliver all data required in the scripts.
     */
    @Nullable
    private ParsedNpc sourceNPC;

    /**
     * This flag should be set true in case the created source is only used as generated code and is never edited.
     */
    private boolean generated;

    /**
     * Default constructor.
     */
    public ScriptWriter() {
        scriptTarget = null;
        sourceNPC = null;
        generated = false;
    }

    /**
     * Set the parsed NPC that is the data source of this writer.
     *
     * @param source the source of this writer
     */
    public void setSource(ParsedNpc source) {
        sourceNPC = source;
    }

    /**
     * Set the writer that is supposed to receive the written script data.
     *
     * @param write the writer that receives the script data
     */
    public void setWritingTarget(Writer write) {
        scriptTarget = write;
    }

    /**
     * Set the writer to the generated mode. This way the created files may be smaller as the things not required are
     * left out.
     *
     * @param generated the generated flag
     */
    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    /**
     * Write the set NPC to a script.
     *
     * @throws IOException thrown in case writing to the assigned target fails
     */
    public void write() throws IOException {
        LuaWriter.getInstance().write(sourceNPC, scriptTarget, generated);
    }
}
