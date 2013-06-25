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
package illarion.easynpc.parser.tasks;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.Parser;
import illarion.easynpc.parser.events.ParserFinishedEvent;
import org.bushe.swing.event.EventBus;

import java.util.concurrent.Callable;

/**
 * This task is used to execute the parsing of a script asynchronously.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParseScriptTask implements Callable<ParsedNpc> {
    /**
     * The easyNPC script that is the target for this parser.
     */
    private final EasyNpcScript script;

    /**
     * Create this task that executes a specified easyNPC script.
     *
     * @param easyNpcScript the script to parse
     */
    public ParseScriptTask(final EasyNpcScript easyNpcScript) {
        script = easyNpcScript;
    }

    @Override
    public ParsedNpc call() throws Exception {
        final ParsedNpc resultNPC = Parser.getInstance().parse(script);
        EventBus.publish(new ParserFinishedEvent(script, resultNPC));
        return resultNPC;
    }
}
