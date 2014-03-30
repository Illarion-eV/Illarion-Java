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
package illarion.easynpc.parsed.talk.consequences;

import illarion.easynpc.data.NpcBaseStateToggle;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the talk state consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceTalkstate implements TalkConsequence {
    /**
     * The LUA code needed to be included for a talk state consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s.talkstate(\"%2$s\"));" + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "talkstate";

    /**
     * The changing command for the basicNPC state.
     */
    private final NpcBaseStateToggle mode;

    /**
     * The constructor to set the talk state that is supposed to be applied with this consequence.
     *
     * @param newMode the mode used to change the basic state
     */
    public ConsequenceTalkstate(final NpcBaseStateToggle newMode) {
        mode = newMode;
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
     * Write this change basic NPC state condition into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(@Nonnull final Writer target) throws IOException {
        target.write(mode.name());
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(@Nonnull final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, mode.name()));
    }
}
