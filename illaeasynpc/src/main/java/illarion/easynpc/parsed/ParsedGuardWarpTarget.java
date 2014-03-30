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

import illarion.common.types.Location;
import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

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
    private final Location target;

    /**
     * Create a new instance of guard warp target.
     */
    public ParsedGuardWarpTarget(@Nonnull Location target) {
        this.target = target;
    }

    @Override
    public boolean effectsEasyNpcStage(@Nonnull EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.guarding;
    }

    @Override
    public void writeEasyNpc(@Nonnull Writer target, @Nonnull EasyNpcWriter.WritingStage stage) throws IOException {
        if (stage != EasyNpcWriter.WritingStage.guarding) {
            throw new IllegalArgumentException("This function did not request a call for a stage but guarding.");
        }

        target.write("guardWarpTarget = ");
        target.write(Integer.toString(this.target.getScX()));
        target.write(", ");
        target.write(Integer.toString(this.target.getScY()));
        target.write(", ");
        target.write(Integer.toString(this.target.getScZ()));
        target.write(EasyNpcWriter.NL);
    }

    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
    }

    @Override
    public boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Guarding;
    }

    @Nullable
    @Override
    public String[] getRequiredModules() {
        return new String[]{"npc.base.guard"};
    }

    @Override
    public void writeLua(@Nonnull Writer target, @Nonnull LuaWriter.WritingStage stage) throws IOException {
        if (stage != LuaWriter.WritingStage.Guarding) {
            throw new IllegalArgumentException("This function did not request a call for a stage but guarding.");
        }
        target.write("guardNPC:setWarpLocation(");
        target.write(Integer.toString(this.target.getScX()));
        target.write(',');
        target.write(Integer.toString(this.target.getScY()));
        target.write(',');
        target.write(Integer.toString(this.target.getScZ()));
        target.write(");");
        target.write(LuaWriter.NL);
    }
}
