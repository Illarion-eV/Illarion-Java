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

import illarion.easynpc.EasyNpcScript.Line;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parsed.ParsedHair;

/**
 * This parser is able to read the definitions for hair and beard of the NPC
 * from the script.
 * 
 * @author Martin Karing
 * @version 1.00
 * @since 1.01
 */
public final class NpcHair implements NpcType {
    /**
     * The pattern to fetch the beard id.
     */
    @SuppressWarnings("nls")
    private static final Pattern BEARD_ID = Pattern.compile(
        "^\\s*(beardID)\\s*=\\s*([0-9]{1,3})[\\s;]*", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The pattern to fetch the hair id.
     */
    @SuppressWarnings("nls")
    private static final Pattern HAIR_ID = Pattern.compile(
        "^\\s*(hairID)\\s*=\\s*([0-9]{1,3})[\\s;]*", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The documentation entry for the beard ID.
     */
    private final DocuEntry beardEntry = new DocuEntry() {
        @Override
        @SuppressWarnings("nls")
        public DocuEntry getChild(final int index) {
            throw new IndexOutOfBoundsException("No children here!");
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        @SuppressWarnings("nls")
        public String getDescription() {
            return Lang.getMsg(NpcHair.class, "Docu.Beard.description");
        }

        @Override
        @SuppressWarnings("nls")
        public String getExample() {
            return Lang.getMsg(NpcHair.class, "Docu.Beard.example");
        }

        @Override
        @SuppressWarnings("nls")
        public String getSyntax() {
            return Lang.getMsg(NpcHair.class, "Docu.Beard.syntax");
        }

        @Override
        @SuppressWarnings("nls")
        public String getTitle() {
            return Lang.getMsg(NpcHair.class, "Docu.Beard.title");
        }
    };

    /**
     * The documentation entry for the hair ID.
     */
    private final DocuEntry hairEntry = new DocuEntry() {
        @Override
        @SuppressWarnings("nls")
        public DocuEntry getChild(final int index) {
            throw new IndexOutOfBoundsException("No children here!");
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        @SuppressWarnings("nls")
        public String getDescription() {
            return Lang.getMsg(NpcHair.class, "Docu.Hair.description");
        }

        @Override
        @SuppressWarnings("nls")
        public String getExample() {
            return Lang.getMsg(NpcHair.class, "Docu.Hair.example");
        }

        @Override
        @SuppressWarnings("nls")
        public String getSyntax() {
            return Lang.getMsg(NpcHair.class, "Docu.Hair.syntax");
        }

        @Override
        @SuppressWarnings("nls")
        public String getTitle() {
            return Lang.getMsg(NpcHair.class, "Docu.Hair.title");
        }
    };

    /**
     * Check if the line contains the definition of a hair ID or a beard ID.
     */
    @Override
    public boolean canParseLine(final Line lineStruct) {
        final String line = lineStruct.getLine();

        if (HAIR_ID.matcher(line).find()) {
            return true;
        }

        if (BEARD_ID.matcher(line).find()) {
            return true;
        }
        return false;
    }

    /**
     * Get the documentation child.
     */
    @Override
    @SuppressWarnings("nls")
    public DocuEntry getChild(final int index) {
        if (index == 0) {
            return hairEntry;
        } else if (index == 1) {
            return beardEntry;
        }

        throw new IndexOutOfBoundsException(
            "The index is too small or too large");
    }

    /**
     * This parser contains 2 children. One documentation children for the hair,
     * one for the beard.
     */
    @Override
    public int getChildCount() {
        return 2;
    }

    /**
     * Get the description for the documentation of this parser.
     */
    @Override
    @SuppressWarnings("nls")
    public String getDescription() {
        return Lang.getMsg(getClass(), "Docu.description");
    }

    /**
     * This parser contains no example. The examples are stored in the children.
     */
    @Override
    public String getExample() {
        return null;
    }

    /**
     * This parser contains no syntax. The syntax is stored in the documentation
     * children of this parser.
     */
    @Override
    public String getSyntax() {
        return null;
    }

    /**
     * Get the title for the documentation of this parser.
     */
    @Override
    @SuppressWarnings("nls")
    public String getTitle() {
        return Lang.getMsg(getClass(), "Docu.title");
    }

    /**
     * Parse a line of the script and filter the required data out.
     */
    @Override
    public void parseLine(final Line line, final ParsedNpc npc) {
        Matcher matcher;

        matcher = HAIR_ID.matcher(line.getLine());
        if (matcher.find()) {
            final int id = Integer.parseInt(matcher.group(2));

            npc.addNpcData(ParsedHair.getInstance(ParsedHair.TYPE_HAIR, id));
            return;
        }

        matcher = BEARD_ID.matcher(line.getLine());
        if (matcher.find()) {
            final int id = Integer.parseInt(matcher.group(2));

            npc.addNpcData(ParsedHair.getInstance(ParsedHair.TYPE_BEARD, id));
            return;
        }
    }

    /**
     * Extract the tokens needed for the syntax highlighting from a text
     * segment.
     */
    @Override
    public void parseSegment(final Segment segment, final int offset,
        final List<Token> tokens) {
        Matcher matcher = HAIR_ID.matcher(segment);
        while (matcher.find()) {
            tokens.add(new Token(TokenType.KEYWORD, matcher.start(1) + offset,
                matcher.end(1) - matcher.start(1)));
        }

        matcher = BEARD_ID.matcher(segment);
        while (matcher.find()) {
            tokens.add(new Token(TokenType.KEYWORD, matcher.start(1) + offset,
                matcher.end(1) - matcher.start(1)));
        }
    }

}
