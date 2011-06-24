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
import illarion.easynpc.parsed.ParsedColors;

/**
 * This parser is able to extract the color values of the NPCs from a easyNPC
 * script.
 * 
 * @author Martin Karing
 * @version 1.00
 * @since 1.01
 */
public final class NpcColors implements NpcType {
    /**
     * The pattern to fetch the hair color.
     */
    @SuppressWarnings("nls")
    private static final Pattern COLOR_HAIR =
        Pattern
            .compile(
                "^\\s*(colorHair)\\s*=\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})[\\s;]*",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /**
     * The pattern to fetch the skin color.
     */
    @SuppressWarnings("nls")
    private static final Pattern COLOR_SKIN =
        Pattern
            .compile(
                "^\\s*(colorSkin)\\s*=\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})[\\s;]*",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /**
     * The documentation entry for the hair color.
     */
    private final DocuEntry hairDocu = new DocuEntry() {
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
            return Lang.getMsg(NpcColors.class, "Hair.Docu.description");
        }

        @Override
        @SuppressWarnings("nls")
        public String getExample() {
            return Lang.getMsg(NpcColors.class, "Hair.Docu.example");
        }

        @Override
        @SuppressWarnings("nls")
        public String getSyntax() {
            return Lang.getMsg(NpcColors.class, "Hair.Docu.syntax");
        }

        @Override
        @SuppressWarnings("nls")
        public String getTitle() {
            return Lang.getMsg(NpcColors.class, "Hair.Docu.title");
        }
    };

    /**
     * The documentation entry for the skin color.
     */
    private final DocuEntry skinDocu = new DocuEntry() {
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
            return Lang.getMsg(NpcColors.class, "Skin.Docu.description");
        }

        @Override
        @SuppressWarnings("nls")
        public String getExample() {
            return Lang.getMsg(NpcColors.class, "Skin.Docu.example");
        }

        @Override
        @SuppressWarnings("nls")
        public String getSyntax() {
            return Lang.getMsg(NpcColors.class, "Skin.Docu.syntax");
        }

        @Override
        @SuppressWarnings("nls")
        public String getTitle() {
            return Lang.getMsg(NpcColors.class, "Skin.Docu.title");
        }
    };

    /**
     * Check if the line is a color line and this parser is able to handle it.
     */
    @Override
    public boolean canParseLine(final Line lineStruct) {
        final String line = lineStruct.getLine();
        if (COLOR_SKIN.matcher(line).find()) {
            return true;
        }

        if (COLOR_HAIR.matcher(line).find()) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("nls")
    @Override
    public DocuEntry getChild(final int index) {
        if (index == 0) {
            return skinDocu;
        } else if (index == 1) {
            return hairDocu;
        }

        throw new IndexOutOfBoundsException(
            "The index is too small or too large.");
    }

    /**
     * Get the amount of children of this documentation entry. This is set to 2
     * always since there is only the skin and the hair color.
     */
    @Override
    public int getChildCount() {
        return 2;
    }

    /**
     * Get the description of this documentation entry.
     */
    @Override
    @SuppressWarnings("nls")
    public String getDescription() {
        return Lang.getMsg(getClass(), "Docu.description");
    }

    /**
     * No example for the colors. The children contain the examples.
     */
    @Override
    public String getExample() {
        return null;
    }

    /**
     * The color definition has not syntax. The syntax is written in the
     * children of this.
     */
    @Override
    public String getSyntax() {
        return null;
    }

    /**
     * Get the title of the documentation entry.
     */
    @Override
    @SuppressWarnings("nls")
    public String getTitle() {
        return Lang.getMsg(getClass(), "Docu.title");
    }

    @SuppressWarnings("nls")
    @Override
    public void parseLine(final Line lineStruct, final ParsedNpc npc) {
        final String line = lineStruct.getLine();
        Matcher matcher;

        matcher = COLOR_SKIN.matcher(line);
        if (matcher.find()) {
            final int red = Integer.parseInt(matcher.group(2));
            final int green = Integer.parseInt(matcher.group(3));
            final int blue = Integer.parseInt(matcher.group(4));

            if ((red < 0) || (red > 255)) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "red"));
                return;
            }
            if ((green < 0) || (green > 255)) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "green"));
                return;
            }
            if ((blue < 0) || (blue > 255)) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "blue"));
                return;
            }

            npc.addNpcData(ParsedColors.getInstance(ParsedColors.SKIN_COLOR,
                red, green, blue));
            return;
        }

        matcher = COLOR_HAIR.matcher(line);
        if (matcher.find()) {
            final int red = Integer.parseInt(matcher.group(2));
            final int green = Integer.parseInt(matcher.group(3));
            final int blue = Integer.parseInt(matcher.group(4));

            if ((red < 0) || (red > 255)) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "red"));
                return;
            }
            if ((green < 0) || (green > 255)) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "green"));
                return;
            }
            if ((blue < 0) || (blue > 255)) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "blue"));
                return;
            }

            npc.addNpcData(ParsedColors.getInstance(ParsedColors.HAIR_COLOR,
                red, green, blue));
            return;
        }
    }

    @Override
    public void parseSegment(final Segment segment, final int offset,
        final List<Token> tokens) {
        Matcher matcher = COLOR_SKIN.matcher(segment);
        while (matcher.find()) {
            tokens.add(new Token(TokenType.KEYWORD, matcher.start(1) + offset,
                matcher.end(1) - matcher.start(1)));
        }

        matcher = COLOR_HAIR.matcher(segment);
        while (matcher.find()) {
            tokens.add(new Token(TokenType.KEYWORD, matcher.start(1) + offset,
                matcher.end(1) - matcher.start(1)));
        }
    }

}
