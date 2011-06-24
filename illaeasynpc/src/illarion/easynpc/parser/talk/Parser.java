/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.parser.talk;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.docu.DocuEntry;

/**
 * This parser interface is the shared interface to parse conditions and
 * consequences.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public abstract class Parser implements DocuEntry {
    /**
     * The line that is currently parsed.
     */
    private EasyNpcScript.Line currentLine;

    /**
     * The parsed NPC that is parsed now using this parser.
     */
    private ParsedNpc parentNpc;

    /**
     * The string this class is working with.
     */
    private String workingString;

    /**
     * Clean the values of this parser. This will be called once using it is
     * done.
     */
    public void cleanup() {
        parentNpc = null;
        currentLine = null;
    }

    @SuppressWarnings("nls")
    @Override
    public DocuEntry getChild(final int index) {
        throw new IllegalArgumentException("Index out of range");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    /**
     * Get the new state of the line after the extracting of the data is done.
     * 
     * @return the new state of the line
     */
    public final String getNewLine() {
        return workingString;
    }

    /**
     * The NPC that is the parent of all the parsing operations. It has to
     * receive the error messages in case there are any.
     * 
     * @param npc the NPC to receive the error messages
     * @param line the line that is currently parsed and needs to be attached to
     *            the error message
     */
    public final void setErrorParent(final ParsedNpc npc,
        final EasyNpcScript.Line line) {
        parentNpc = npc;
        currentLine = line;
    }

    /**
     * Set the line that is supposed to be parsed.
     * 
     * @param line the line that is supposed to be parsed
     */
    public final void setLine(final String line) {
        workingString = line;
    }

    /**
     * Report a error with the current settings to the NPC.
     * 
     * @param message the error message to report
     */
    protected final void reportError(final String message) {
        parentNpc.addError(currentLine, message);
    }
}
