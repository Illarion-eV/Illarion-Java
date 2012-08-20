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

import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * This consequence is used to store the data of a answer consequence of a talking line.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceAnswer implements TalkConsequence {
    /**
     * This answer of this consequence.
     */
    private final String answer;

    /**
     * The constructor that allows setting the text that is said by the NPC.
     *
     * @param answerText the text of this answer
     */
    public ConsequenceAnswer(final String answerText) {
        answer = answerText;
    }

    /**
     * Get the LUA module required for the answer consequence.
     *
     * @return <code>null</code> at all time because there is no additional module needed
     */
    @Override
    public String getLuaModule() {
        return null;
    }

    /**
     * Write this consequence to its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write('"');
        target.write(answer);
        target.write('"');
    }

    /**
     * Write the LUA code for this answer consequence to a LUA script.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write("talkEntry:addResponse(\""); //$NON-NLS-1$
        target.write(answer);
        target.write("\");"); //$NON-NLS-1$
        target.write(LuaWriter.NL);
    }
}
