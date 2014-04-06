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

import illarion.easynpc.data.CharacterMagicType;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the rune consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceRune implements TalkConsequence {
    /**
     * The LUA code needed to be included for a rune consequence.
     */
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s.rune(%2$s, %3$s));" + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    private static final String LUA_MODULE = BASE_LUA_MODULE + "rune";

    /**
     * The magic type the rune to learn is assigned to.
     */
    private final CharacterMagicType magicType;

    /**
     * The number of the rune to learn.
     */
    private final int value;

    /**
     * The constructor that allows setting the parameters of this rune consequence.
     *
     * @param newMagicType the magic type of the rune
     * @param newValue the number of the rune
     */
    public ConsequenceRune(CharacterMagicType newMagicType, int newValue) {
        magicType = newMagicType;
        value = newValue;
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
    public void writeLua(@Nonnull Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, Integer.toString(magicType.getMagicTypeId()),
                                   Integer.toString(value)));
    }
}
