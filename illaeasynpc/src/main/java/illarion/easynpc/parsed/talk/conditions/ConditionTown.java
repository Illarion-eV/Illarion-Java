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
package illarion.easynpc.parsed.talk.conditions;

import illarion.easynpc.data.Towns;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the town condition.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConditionTown implements TalkCondition {
    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addCondition(%1$s.town(%2$s));" + LuaWriter.NL;

    /**
     * The LUA module required for this condition to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "town";

    /**
     * The name of the town the character is expected to be a part of.
     */
    private final Towns town;

    /**
     * The constructor that allows setting the town the player needs to be part of.
     *
     * @param newTown the town the character is expected to be a part of
     */
    public ConditionTown(final Towns newTown) {
        town = newTown;
    }

    /**
     * Get the LUA module needed for this condition.
     */
    @Nonnull
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write the LUA code needed for this town condition.
     */
    @Override
    public void writeLua(@Nonnull final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, Integer.toString(town.getFactionId())));
    }
}
