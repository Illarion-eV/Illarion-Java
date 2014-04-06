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

import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

/**
 * This class stores a trigger that is used in a condition.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConditionTrigger implements TalkCondition {
    /**
     * The LUA code needed for this consequence to work.
     */
    private static final String LUA_CODE = "talkEntry:addTrigger(\"%1$s\");" + LuaWriter.NL;

    /**
     * The trigger text of this trigger condition.
     */
    private final String triggerString;

    /**
     * Constructor that allows setting the text the player needs to say to trigger this condition.
     *
     * @param newTriggerString the trigger used in this trigger condition.
     */
    public ConditionTrigger(String newTriggerString) {
        triggerString = newTriggerString;
    }

    /**
     * Get the LUA module needed for this condition.
     */
    @Nullable
    @Override
    public String getLuaModule() {
        return null;
    }

    /**
     * Write the LUA code needed for this trigger.
     */
    @Override
    public void writeLua(@Nonnull Writer target) throws IOException {
        target.write(String.format(LUA_CODE, triggerString.replace("%NUMBER", "(%d+)")));
    }
}
