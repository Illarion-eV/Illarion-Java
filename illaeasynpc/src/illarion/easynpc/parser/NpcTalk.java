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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Segment;

import jsyntaxpane.Token;
import jsyntaxpane.TokenType;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parser.talk.TalkingLine;

/**
 * This class is used to parse the lines from a easyNPC script that are used for
 * talking NPCs.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class NpcTalk implements NpcType {
    /**
     * The pattern to find the quest id of this talking NPC in the easyNPC
     * script.
     */
    @SuppressWarnings("nls")
    private static final Pattern NORMAL_LINE = Pattern.compile(
        "^.*[\"].*[^\\\\][\"].*([-][>]).*$", Pattern.MULTILINE);

    /**
     * The parser that is used to parse one talking line.
     */
    private final TalkingLine talkLineParser = new TalkingLine();

    /**
     * Check if the line can be parsed as talking trigger.
     * 
     * @param lineStruct the line that is supposed to be parsed.
     * @return <code>true</code> in case the line can be parsed by this class
     */
    @Override
    public boolean canParseLine(final EasyNpcScript.Line lineStruct) {
        final String line = lineStruct.getLine();
        if (NORMAL_LINE.matcher(line).matches()) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("nls")
    @Override
    public DocuEntry getChild(final int index) {
        if (index == 0) {
            return talkLineParser.getConditionDocuEntry();
        } else if (index == 1) {
            return talkLineParser.getConsequenceDocuEntry();
        }
        throw new IllegalArgumentException("Index ouf of range.");
    }

    @Override
    public int getChildCount() {
        return 2;
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(getClass(), "Docu.description"); //$NON-NLS-1$
    }

    @Override
    public String getExample() {
        return Lang.getMsg(getClass(), "Docu.example"); //$NON-NLS-1$
    }

    @Override
    public String getSyntax() {
        return Lang.getMsg(getClass(), "Docu.syntax"); //$NON-NLS-1$
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(getClass(), "Docu.title"); //$NON-NLS-1$
    }

    /**
     * Parse the line and add the analyzed values to the parsed NPC.
     * 
     * @param lineStruct the line that is parsed
     * @param npc the NPC that is the target for the parsed values
     */
    @Override
    @SuppressWarnings("nls")
    public void parseLine(final EasyNpcScript.Line lineStruct,
        final ParsedNpc npc) {
        final String line = lineStruct.getLine();

        if (NORMAL_LINE.matcher(line).matches()) {
            talkLineParser.parseLine(lineStruct, npc);
            return;
        }

        npc.addError(lineStruct, Lang.getMsg(getClass(), "generalFailure"));
    }

    @Override
    public void parseSegment(final Segment segment, final int offset,
        final List<Token> tokens) {
        final Matcher matcher = NORMAL_LINE.matcher(segment);
        while (matcher.find()) {
            tokens.add(new Token(TokenType.KEYWORD, matcher.start(1) + offset,
                matcher.end(1) - matcher.start(1)));
        }
    }

}
