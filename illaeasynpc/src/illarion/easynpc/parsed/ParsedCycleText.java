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
package illarion.easynpc.parsed;

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store a cycle texts of the NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedCycleText implements ParsedData {
    /**
     * The LUA modules required for this cycle texts to work.
     */
    @SuppressWarnings("nls")
    private static final String[] LUA_MODULES = {"npc.base.talk"};

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
     * @param germanText  the German version of the cycle text
     * @param englishText the English version of the cycle text
     */
    public ParsedCycleText(final String germanText, final String englishText) {
        english = englishText;
        german = germanText;
    }

    /**
     * Cycle texts do not effect the SQL query.
     */
    @Override
    public void buildSQL(final SQLBuilder builder) {
        // nothing to add to the query.
    }

    /**
     * Check the stage that is effected by this cycle texts entry.
     */
    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.cycleTexts;
    }

    /**
     * Check if the selected stage is effected by this cycle text.
     */
    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.CycleText;
    }

    /**
     * Get the modules required for this cycle text to work properly.
     */
    @Nonnull
    @Override
    public String[] getRequiredModules() {
        return LUA_MODULES;
    }

    /**
     * Write the cycle texts out to the easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(@Nonnull final Writer target,
                             final EasyNpcWriter.WritingStage stage) throws IOException {
        if (stage == EasyNpcWriter.WritingStage.cycleTexts) {
            target.write("cycletext \"");
            target.write(german);
            target.write("\", \"");
            target.write(english);
            target.write("\"");
            target.write(EasyNpcWriter.NL);
        }
    }

    /**
     * Write the LUA code required to ensure this cycle text is working.
     */
    @Override
    public void writeLua(@Nonnull final Writer target,
                         final LuaWriter.WritingStage stage) throws IOException {
        if (stage == LuaWriter.WritingStage.CycleText) {
            target.write("talkingNPC:addCycleText(\""); //$NON-NLS-1$
            target.write(german);
            target.write("\", \""); //$NON-NLS-1$
            target.write(english);
            target.write("\");"); //$NON-NLS-1$
            target.write(LuaWriter.NL);
        }
    }
}
