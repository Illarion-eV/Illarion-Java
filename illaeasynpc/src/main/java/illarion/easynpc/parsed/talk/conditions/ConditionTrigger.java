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
     * The code needed for this condition in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "\"%1$s\"";

    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
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
    public ConditionTrigger(final String newTriggerString) {
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
     * Write this trigger condition into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(@Nonnull final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, triggerString));
    }

    /**
     * Write the LUA code needed for this trigger.
     */
    @Override
    @SuppressWarnings("nls")
    public void writeLua(@Nonnull final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, triggerString.replace("%NUMBER", "(%d+)")));
    }
}
