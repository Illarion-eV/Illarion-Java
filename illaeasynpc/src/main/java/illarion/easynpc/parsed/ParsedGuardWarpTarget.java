/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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

import illarion.common.types.ServerCoordinate;
import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.LuaWriter.WritingStage;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;

/**
 * This is the parsed instance of the NPC guard warp location.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParsedGuardWarpTarget implements ParsedData {
    /**
     * The target location of the warp.
     */
    @Nonnull
    private final ServerCoordinate target;

    /**
     * Create a new instance of guard warp target.
     */
    public ParsedGuardWarpTarget(@Nonnull ServerCoordinate target) {
        this.target = target;
    }

    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
    }

    @Override
    public boolean effectsLuaWritingStage(@Nonnull WritingStage stage) {
        return stage == WritingStage.Guarding;
    }

    @Nonnull
    @Override
    public Collection<String> getRequiredModules() {
        return Collections.singleton("npc.base.guard");
    }

    @Override
    public void writeLua(@Nonnull Writer target, @Nonnull LuaRequireTable requires, @Nonnull WritingStage stage) throws IOException {
        if (stage != WritingStage.Guarding) {
            throw new IllegalArgumentException("This function did not request a call for a stage but guarding.");
        }
        target.write("guardNPC:setWarpLocation(");
        target.write(Integer.toString(this.target.getX()));
        target.write(',');
        target.write(Integer.toString(this.target.getY()));
        target.write(',');
        target.write(Integer.toString(this.target.getZ()));
        target.write(")");
        target.write(LuaWriter.NL);
    }
}
