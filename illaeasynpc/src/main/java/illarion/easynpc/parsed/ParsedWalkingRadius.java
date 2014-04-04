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
    public void buildSQL(@Nonnull final SQLBuilder builder) {
        // nothing to add to the query.
    }

    @Override
    public boolean effectsLuaWritingStage(@Nonnull final LuaWriter.WritingStage stage) {
        return false;
    }

    @Nullable
    @Override
    public String[] getRequiredModules() {
        return null;
    }

    @Override
    public void writeLua(
            @Nonnull final Writer target, @Nonnull final LuaWriter.WritingStage stage) throws IOException {
        // not implemented yet.
    }
}
