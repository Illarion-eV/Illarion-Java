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
package illarion.easynpc.parsed.talk.consequences;

import java.io.IOException;
import java.io.Writer;

import javolution.context.ObjectFactory;

import illarion.easynpc.parsed.talk.TalkConsequence;

/**
 * This consequence is used to store the data of a answer consequence of a
 * talking line.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConsequenceAnswer implements TalkConsequence {
    /**
     * The factory class that creates and buffers ConsequenceAnswer objects for
     * later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConsequenceAnswerFactory extends
        ObjectFactory<ConsequenceAnswer> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConsequenceAnswerFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConsequenceAnswer create() {
            return new ConsequenceAnswer();
        }
    }

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConsequenceAnswerFactory FACTORY =
        new ConsequenceAnswerFactory();

    /**
     * This answer of this consequence.
     */
    private String answer;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConsequenceAnswer() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConsequenceAnswer getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the LUA module required for the answer consequence.
     * 
     * @return <code>null</code> at all time because there is no additional
     *         module needed
     */
    @Override
    public String getLuaModule() {
        return null;
    }

    /**
     * Recycle the object so it can be used again later.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset the state of this instance to its ready to be used later.
     */
    @Override
    public void reset() {
        answer = null;
    }

    /**
     * Set the data for this answer consequence.
     * 
     * @param answerText the text of this answer
     */
    public void setData(final String answerText) {
        answer = answerText;
    }

    /**
     * Write this consequence to its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write('"');
        target.write(answer);
        target.write('"');
    }

    /**
     * Write the LUA code for this answer consequence to a LUA script.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write("talkEntry:addResponse(\""); //$NON-NLS-1$
        target.write(answer);
        target.write("\");"); //$NON-NLS-1$
        target.write(illarion.easynpc.writer.LuaWriter.NL);
    }
}
