/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.parsed;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import javolution.context.ObjectFactory;

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

/**
 * This class is used to store a comment block in the parsed NPC.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ParsedComment implements ParsedData {
    /**
     * The factory for the parsed comment. This stores all formerly created and
     * currently unused instances of the ParsedComment class.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ParsedCommentFactory extends
        ObjectFactory<ParsedComment> {
        /**
         * Public constructor to allow the parent class to create a proper
         * instance.
         */
        public ParsedCommentFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of the recycled object.
         */
        @Override
        protected ParsedComment create() {
            return new ParsedComment();
        }
    }

    /**
     * The instance of the factory used to create the objects for this class.
     */
    private static final ParsedCommentFactory FACTORY =
        new ParsedCommentFactory();

    /**
     * The pattern that is used to format the comment correctly for the script.
     */
    @SuppressWarnings("nls")
    private static final Pattern replacePattern = Pattern.compile("^(.*)$",
        Pattern.MULTILINE);

    /**
     * The comment that is stored in this object.
     */
    private String comment;

    /**
     * Standard constructor that stores the comment in the object.
     */
    ParsedComment() {
        // nothing to do
    }

    /**
     * Create a new or reuse a old instance of the ParsedComment class.
     * 
     * @param newComment the comment to store
     * @return the instance that stores the comment
     */
    public static ParsedComment getInstance(final String newComment) {
        final ParsedComment result = FACTORY.object();
        result.setComment(newComment);
        return result;
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
        return (stage == EasyNpcWriter.WritingStage.talking);
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
     * Place the created instance back into the factory for later usage.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Cleanup this instance for later reuse.
     */
    @Override
    public void reset() {
        comment = null;
    }

    /**
     * Write the comment into the easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(final Writer target,
        final EasyNpcWriter.WritingStage stage) throws IOException {
        if (stage == EasyNpcWriter.WritingStage.talking) {
            final String formattedComment =
                replacePattern.matcher(comment).replaceAll("-- $1");
            target.write(illarion.easynpc.writer.EasyNpcWriter.NL);
            target.write(formattedComment);
            target.write(illarion.easynpc.writer.EasyNpcWriter.NL);
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

    /**
     * Set the comment stored in this object.
     * 
     * @param newComment the comment to store
     */
    private void setComment(final String newComment) {
        comment = newComment.trim();
    }
}
