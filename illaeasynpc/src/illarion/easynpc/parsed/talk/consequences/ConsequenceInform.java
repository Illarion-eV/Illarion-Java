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
 * This consequence is used to store the data of a inform consequence of a
 * talking line.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConsequenceInform implements TalkConsequence {
    /**
     * The factory class that creates and buffers ConsequenceInform objects for
     * later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConsequenceInformFactory extends
        ObjectFactory<ConsequenceInform> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConsequenceInformFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConsequenceInform create() {
            return new ConsequenceInform();
        }
    }

    /**
     * The format string for the easy NPC code needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "inform(\"%1$s\")";

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConsequenceInformFactory FACTORY =
        new ConsequenceInformFactory();

    /**
     * The LUA code needed to be included for a inform consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addConsequence(%1$s.inform(\"%2$s\"));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA module that is required for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "inform";

    /**
     * The message that is send as inform to the player.
     */
    private String message;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConsequenceInform() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConsequenceInform getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the module that is needed for this consequence to work.
     */
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
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
        message = null;
    }

    /**
     * Set the data needed for this inform consequence.
     * 
     * @param messageText the text that is send as inform to the player.
     */
    public void setData(final String messageText) {
        message = messageText.trim();
    }

    /**
     * Write this inform consequence into its easyNPC form.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, message));
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, message));
    }
}
