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
package illarion.easynpc.parser.events;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.ParsedNpc;

/**
 * This is the event generated once the parser finished its task.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParserFinishedEvent {
    /**
     * The parsed NPC.
     */
    private final ParsedNpc npc;

    /**
     * The easyNPC script that was parsed to create the NPC.
     */
    private final EasyNpcScript script;

    /**
     * Create a event that stores a specified NPC that was parsed.
     *
     * @param easyNpcScript the script that was parsed
     * @param parsedNpc     the script to parse
     */
    public ParserFinishedEvent(final EasyNpcScript easyNpcScript, final ParsedNpc parsedNpc) {
        script = easyNpcScript;
        npc = parsedNpc;
    }

    /**
     * Get the result of the parser.
     *
     * @return the result of the parser
     */
    public ParsedNpc getNpc() {
        return npc;
    }

    /**
     * Get the script that was originally parsed.
     *
     * @return the script that was parsed
     */
    public EasyNpcScript getScript() {
        return script;
    }
}
