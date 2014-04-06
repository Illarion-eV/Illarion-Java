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
package illarion.easynpc.parsed;

import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;

/**
 * This class is used to store a cycle texts of the NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedCycleText implements ParsedData {
    /**
     * The English cycle text stored in this object.
     */
    private final String english;

    /**
     * The German cycle text stored in this object.
     */
    private final String german;

    /**
     * Default constructor that sets the text value that were defined for this
     * cycle text entry.
     *
     * @param germanText the German version of the cycle text
     * @param englishText the English version of the cycle text
     */
    public ParsedCycleText(String germanText, String englishText) {
        english = englishText;
        german = germanText;
    }

    /**
     * Cycle texts do not effect the SQL query.
     */
    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
        // nothing to add to the query.
    }

    /**
     * Check if the selected stage is effected by this cycle text.
     */
    @Override
    public boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Talking;
    }

    /**
     * Get the modules required for this cycle text to work properly.
     */
    @Nonnull
    @Override
    public Collection<String> getRequiredModules() {
        return Collections.singleton("npc.base.talk");
    }

    /**
     * Write the LUA code required to ensure this cycle text is working.
     */
    @Override
    public void writeLua(
            @Nonnull Writer target, @Nonnull LuaWriter.WritingStage stage) throws IOException {
        if (stage == LuaWriter.WritingStage.Talking) {
            target.write("talkingNPC:addCycleText(\""); //$NON-NLS-1$
            target.write(german);
            target.write("\", \""); //$NON-NLS-1$
            target.write(english);
            target.write("\");"); //$NON-NLS-1$
            target.write(LuaWriter.NL);
        }
    }
}
