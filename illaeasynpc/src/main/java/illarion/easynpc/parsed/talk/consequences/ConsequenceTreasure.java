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

import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the treasure consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceTreasure implements TalkConsequence {
    /**
     * The LUA code needed to be included for a rune consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s.treasure(%2$s));" + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "treasure";

    /**
     * The level of the treasure to create.
     */
    private final AdvancedNumber level;

    /**
     * The constructor that allows setting the number of the treasure that is supposed to be generated.
     *
     * @param newLevel the level of the treasure to create
     */
    public ConsequenceTreasure(final AdvancedNumber newLevel) {
        level = newLevel;
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
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(@Nonnull final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, level.getLua()));
    }
}
