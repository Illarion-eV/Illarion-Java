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
package illarion.easynpc.parsed.talk.consequences;

import illarion.common.types.Location;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This consequence is used to store the data of a warp consequence of a talking line.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Martin Polak
 */
public final class ConsequenceWarp implements TalkConsequence {
    /**
     * The format string for the easy NPC code needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "warp(%1$s, %2$s, %3$s)";

    /**
     * The LUA code needed to be included for a warp consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s.warp(%2$s, %3$s, %4$s));" + LuaWriter.NL;

    /**
     * The LUA module that is required for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "warp";

    /**
     * The location of the location that the player is sent to.
     */
    @Nonnull
    private final Location loc;

    /**
     * The constructor that allows setting the target coordinates of the warp.
     *
     * @param posX x-coordinate.
     * @param posY y-coordinate.
     * @param posZ z-coordinate.
     */
    public ConsequenceWarp(final int posX, final int posY, final int posZ) {
        loc = new Location(posX, posY, posZ);
    }

    /**
     * The constructor that allows setting the target coordinates of the warp.
     *
     * @param loc the location
     */
    public ConsequenceWarp(@Nonnull final Location loc) {
        this.loc = loc;
    }

    /**
     * Get the module that is needed for this consequence to work.
     */
    @Nonnull
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write this warp consequence into its easyNPC form.
     */
    @Override
    public void writeEasyNpc(@Nonnull final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, loc.getScX(), loc.getScY(), loc.getScZ()));
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(@Nonnull final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, loc.getScX(), loc.getScY(), loc.getScZ()));
    }
}
