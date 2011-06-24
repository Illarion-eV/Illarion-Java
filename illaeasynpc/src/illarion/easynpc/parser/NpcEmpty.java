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
package illarion.easynpc.parser;

import java.util.List;
import java.util.regex.Pattern;

import javax.swing.text.Segment;

import jsyntaxpane.Token;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parsed.ParsedEmptyLine;

/**
 * This class simply handles all empty lines in a new NPC script. While they do
 * not actually "do" anything they still need to be parsed, else problem occur.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class NpcEmpty implements NpcType {

    /**
     * The pattern to find out of this line is a empty line
     */
    @SuppressWarnings("nls")
    private static final Pattern EMPTY_LINE = Pattern.compile("^\\s*$");

    /**
     * Check if the line is a empty line.
     * 
     * @param line the line that is supposed to be parsed.
     * @return <code>true</code> in case the line can be parsed by this class
     */
    @Override
    public boolean canParseLine(final EasyNpcScript.Line line) {
        return EMPTY_LINE.matcher(line.getLine()).matches();
    }

    @SuppressWarnings("nls")
    @Override
    public DocuEntry getChild(final int index) {
        throw new IllegalArgumentException("There are no children here.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(getClass(), "Docu.description"); //$NON-NLS-1$
    }

    @Override
    public String getExample() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(getClass(), "Docu.title"); //$NON-NLS-1$
    }

    /**
     * Add the empty line to the parsed NPC.
     */
    @Override
    public void parseLine(final EasyNpcScript.Line line, final ParsedNpc npc) {
        npc.addNpcData(ParsedEmptyLine.getInstance());
    }

    @Override
    public void parseSegment(final Segment segment, final int offset,
        final List<Token> tokens) {
        // nothing to do
    }

}
