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
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ConsequenceArena implements TalkConsequence {

    public enum Task {
        RequestMonster,
        ShowStatistics,
        ShowRanking
    }

    @Nonnull
    private final Task task;

    public ConsequenceArena(@Nonnull Task task) {
        this.task = task;
    }

    @Nullable
    @Override
    public String getLuaModule() {
        return BASE_LUA_MODULE + "arena";
    }

    @Override
    public void writeEasyNpc(@Nonnull Writer target) throws IOException {
        switch (task) {
            case RequestMonster:
                target.write("arena(requestMonster)");
                break;
            case ShowStatistics:
                target.write("arena(getStats)");
                break;
            case ShowRanking:
                target.write("arena(getRanking)");
                break;
        }
    }

    @Override
    public void writeLua(@Nonnull Writer target) throws IOException {
        target.write("talkEntry:addConsequence(");
        target.write(getLuaModule() + ".arena(\"");
        switch (task) {
            case RequestMonster:
                target.write("request");
                break;
            case ShowStatistics:
                target.write("points");
                break;
            case ShowRanking:
                target.write("list");
                break;
        }
        target.write("\"));");
        target.write(LuaWriter.NL);
    }
}
