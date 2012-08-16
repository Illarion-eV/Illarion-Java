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

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

/**
 * This class is used to store a comment block in the parsed NPC.
 *
 * @author Martin Karing
 */
public final class ParsedComment implements ParsedData {
    /**
     * The pattern that is used to format the comment correctly for the script.
     */
    @SuppressWarnings("nls")
    private static final Pattern replacePattern = Pattern.compile("^(.*)$",
            Pattern.MULTILINE);

    /**
     * The comment that is stored in this object.
     */
    private final String comment;

    /**
     * Standard constructor that stores the comment in the object.
     *
     * @param newComment the comment to store
     */
    public ParsedComment(final String newComment) {
        comment = newComment;
    }

    /**
     * Comment data do not effect the query. Nothing to do.
     */
    @Override
    public void buildSQL(final SQLBuilder builder) {
        // nothing to add to the query.
    }

    /**
     * Check the stages effected by this comment entry.
     */
    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.talking;
    }

    /**
     * The comments are never written into the LUA script. So no stage is
     * effected.
     *
     * @return <code>false</code> in all cases
     */
    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return false;
    }

    /**
     * Get the required modules. Comments don't need modules, so nothing
     * returns.
     *
     * @return <code>null</code> in all cases
     */
    @Override
    public String[] getRequiredModules() {
        return null;
    }

    /**
     * Write the comment into the easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(final Writer target,
                             final EasyNpcWriter.WritingStage stage) throws IOException {
        if (stage == EasyNpcWriter.WritingStage.talking) {
            final String formattedComment = replacePattern.matcher(comment).replaceAll("-- $1");
            target.write(EasyNpcWriter.NL);
            target.write(formattedComment);
            target.write(EasyNpcWriter.NL);
        }
    }

    /**
     * Write the LUA code of this comment. Since the NPC scripts are just plain
     * generated scripts. All user defined comments are discarded.
     */
    @Override
    public void writeLua(final Writer target,
                         final LuaWriter.WritingStage stage) {
        // nothing to do here
    }
}
