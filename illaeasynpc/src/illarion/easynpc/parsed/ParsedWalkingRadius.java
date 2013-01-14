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
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store a walking radius in the parsed NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedWalkingRadius implements ParsedData {
    /**
     * The walking range that is defined by this command.
     */
    private final int range;

    /**
     * Constructor to create new blank instances of this class.
     */
    public ParsedWalkingRadius(final int newRange) {
        range = newRange;
    }

    /**
     * No effect on the SQL query.
     */
    @Override
    public void buildSQL(final SQLBuilder builder) {
        // nothing to add to the query.
    }

    /**
     * Check the stage effected by this walking radius value.
     */
    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.header;
    }

    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return false;
    }

    @Nullable
    @Override
    public String[] getRequiredModules() {
        return null;
    }

    /**
     * Write the walking radius to the easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(@Nonnull final Writer target, final EasyNpcWriter.WritingStage stage)
            throws IOException {
        if (stage == EasyNpcWriter.WritingStage.header) {
            target.write("radius = ");
            target.write(Integer.toString(range, 0));
            target.write(EasyNpcWriter.NL);
        }

    }

    @Override
    public void writeLua(final Writer target,
                         final LuaWriter.WritingStage stage)
            throws IOException {
        // not implemented yet.
    }
}
