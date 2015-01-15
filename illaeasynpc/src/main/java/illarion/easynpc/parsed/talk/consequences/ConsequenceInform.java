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

import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This consequence is used to store the data of a inform consequence of a talking line.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceInform implements TalkConsequence {
    /**
     * The LUA code needed to be included for a inform consequence.
     */
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s(\"%2$s\"))" + LuaWriter.NL;

    /**
     * The LUA module that is required for this consequence to work.
     */
    private static final String LUA_MODULE = BASE_LUA_MODULE + "inform";

    /**
     * The message that is send as inform to the player.
     */
    private final String message;

    /**
     * The constructor that allows setting the text that is supposed to be displayed as inform message.
     *
     * @param messageText the text that is send as inform to the player
     */
    public ConsequenceInform(String messageText) {
        message = messageText;
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
    public void writeLua(@Nonnull Writer target, @Nonnull LuaRequireTable requires) throws IOException {
        target.write(String.format(LUA_CODE, requires.getStorage(LUA_MODULE), message));
    }
}
