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
 * This consequence is used to store the data of a warp consequence of a
 * talking line.
 * 
 * @author Martin Karing, Martin Polak
 * @since 1.02
 * @version 1.02
 */
public final class ConsequenceWarp implements TalkConsequence {
    /**
     * The factory class that creates and buffers ConsequenceWarp objects for
     * later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConsequenceWarpFactory extends
        ObjectFactory<ConsequenceWarp> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConsequenceWarpFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConsequenceWarp create() {
            return new ConsequenceWarp();
        }
    }

    /**
     * The format string for the easy NPC code needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "warp(%1$s, %2$s, %3$s)";

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConsequenceWarpFactory FACTORY =
        new ConsequenceWarpFactory();

    /**
     * The LUA code needed to be included for a warp consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addConsequence(%1$s.warp(%2$s, %3$s, %4$s));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA module that is required for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "warp";

    /**
     * The coordinates that the player is sent to.
     */
    private int x;
    
    private int y;
    
    private int z;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConsequenceWarp() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConsequenceWarp getInstance() {
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
        x = 0;
        y = 0;
        z = 0;
    }

    /**
     * Set the data needed for this warp consequence.
     * 
     * @param posx x-coordinate.
     * @param posx y-coordinate.
     * @param posx z-coordinate.
     */
    public void setCoordinates(final int posx, final int posy, final int posz) {
        x=posx;
        y=posy;
        z=posz;
    }

    /**
     * Write this warp consequence into its easyNPC form.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, x, y, z));
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, x, y, z));
    }
}
