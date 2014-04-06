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
 * This is the parsed instance of the guard range settings.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParsedGuardRange implements ParsedData {
    /**
     * The guarding range of the NPC towards north.
     */
    private final int rangeNorth;
    /**
     * The guarding range of the NPC towards south.
     */
    private final int rangeSouth;
    /**
     * The guarding range of the NPC towards east.
     */
    private final int rangeEast;
    /**
     * The guarding range of the NPC towards west.
     */
    private final int rangeWest;

    /**
     * Create a new instance of the checking range.
     */
    public ParsedGuardRange(int rangeNorth, int rangeSouth, int rangeWest, int rangeEast) {
        this.rangeNorth = rangeNorth;
        this.rangeSouth = rangeSouth;
        this.rangeEast = rangeEast;
        this.rangeWest = rangeWest;
    }

    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
    }

    @Override
    public boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Guarding;
    }

    @Nonnull
    @Override
    public Collection<String> getRequiredModules() {
        return Collections.singleton("npc.base.guard");
    }

    @Override
    public void writeLua(@Nonnull Writer target, @Nonnull LuaWriter.WritingStage stage) throws IOException {
        if (stage != LuaWriter.WritingStage.Guarding) {
            throw new IllegalArgumentException("This function did not request a call for a stage but guarding.");
        }
        target.write("guardNPC:setGuardRange(");
        target.write(Integer.toString(rangeNorth));
        target.write(',');
        target.write(Integer.toString(rangeSouth));
        target.write(',');
        target.write(Integer.toString(rangeWest));
        target.write(',');
        target.write(Integer.toString(rangeEast));
        target.write(");");
        target.write(LuaWriter.NL);
    }
}
