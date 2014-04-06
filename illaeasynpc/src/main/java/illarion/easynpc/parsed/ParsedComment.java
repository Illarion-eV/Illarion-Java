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
import java.io.Writer;
import java.util.regex.Pattern;

/**
 * This class is used to store a comment block in the parsed NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedComment implements ParsedData {
    /**
     * The pattern that is used to format the comment correctly for the script.
     */
    private static final Pattern replacePattern = Pattern.compile("^(.*)$", Pattern.MULTILINE);

    /**
     * The comment that is stored in this object.
     */
    private final String comment;

    /**
     * Standard constructor that stores the comment in the object.
     *
     * @param newComment the comment to store
     */
    public ParsedComment(String newComment) {
        comment = newComment;
    }

    /**
     * Comment data do not effect the query. Nothing to do.
     */
    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
        // nothing to add to the query.
    }

    /**
     * The comments are never written into the LUA script. So no stage is
     * effected.
     *
     * @return {@code false} in all cases
     */
    @Override
    public boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage) {
        return false;
    }

    /**
     * Get the required modules. Comments don't need modules, so nothing
     * returns.
     *
     * @return {@code null} in all cases
     */
    @Nullable
    @Override
    public String[] getRequiredModules() {
        return null;
    }

    /**
     * Write the LUA code of this comment. Since the NPC scripts are just plain
     * generated scripts. All user defined comments are discarded.
     */
    @Override
    public void writeLua(
            @Nonnull Writer target, @Nonnull LuaWriter.WritingStage stage) {
        // nothing to do here
    }
}
