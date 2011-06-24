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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Segment;

import jsyntaxpane.Token;
import jsyntaxpane.TokenType;

import illarion.easynpc.EasyNpcScript.Line;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.data.EquipmentSlots;
import illarion.easynpc.data.Items;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parsed.ParsedEquipment;

/**
 * Parser to fetch the equipment a character wears from the easyNPC script.
 * 
 * @author Martin Karing
 * @version 1.00
 * @since 1.01
 */
public final class NpcEquipment implements NpcType {
    /**
     * This internal class is a helper class for the documentation. Each
     * instance of this class contains the documentation data for one command
     * the NPC Equipment parser manages.
     * 
     * @author Martin Karing
     * @version 1.00
     * @since 1.01
     */
    private static final class ChildDocuClass implements DocuEntry {
        /**
         * The key for the description of this command.
         */
        private final String docuDesc;

        /**
         * The key for the examples of this command.
         */
        private final String docuEx;

        /**
         * The key for the syntax of this command.
         */
        private final String docuSyntax;

        /**
         * The key for the title of this command.
         */
        private final String docuTitle;

        /**
         * The default constructor that prepares the key values.
         * 
         * @param name the name that is part of the key value to identify the
         *            header entry
         */
        @SuppressWarnings("nls")
        public ChildDocuClass(final String name) {
            docuTitle = "Docu." + name + ".title";
            docuDesc = "Docu." + name + ".description";
            docuEx = "Docu." + name + ".example";
            docuSyntax = "Docu." + name + ".syntax";
        }

        @SuppressWarnings("nls")
        @Override
        public DocuEntry getChild(final int index) {
            throw new IllegalArgumentException(
                "There are no childs to request.");
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public String getDescription() {
            return Lang.getMsg(NpcEquipment.class, docuDesc);
        }

        @Override
        public String getExample() {
            return Lang.getMsg(NpcEquipment.class, docuEx);
        }

        @Override
        public String getSyntax() {
            return Lang.getMsg(NpcEquipment.class, docuSyntax);
        }

        @Override
        public String getTitle() {
            return Lang.getMsg(NpcEquipment.class, docuTitle);
        }

    }

    /**
     * The pattern to find the item on the chest of the NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern ITEM_CHEST = Pattern.compile(
        "^\\s*(itemChest)\\s*=\\s*([0-9]{1,4})[\\s;]*", Pattern.MULTILINE);

    /**
     * The pattern to find the item on the coat of the NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern ITEM_COAT = Pattern.compile(
        "^\\s*(itemCoat)\\s*=\\s*([0-9]{1,4})[\\s;]*", Pattern.MULTILINE);

    /**
     * The pattern to find the item on the hands of the NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern ITEM_HANDS = Pattern.compile(
        "^\\s*(itemHands)\\s*=\\s*([0-9]{1,4})[\\s;]*", Pattern.MULTILINE);

    /**
     * The pattern to find the item on the head of the NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern ITEM_HEAD = Pattern.compile(
        "^\\s*(itemHead)\\s*=\\s*([0-9]{1,4})[\\s;]*", Pattern.MULTILINE);

    /**
     * The pattern to find the item on the main hand of the NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern ITEM_MAIN_HAND = Pattern.compile(
        "^\\s*(itemMainHand)\\s*=\\s*([0-9]{1,4})[\\s;]*", Pattern.MULTILINE);

    /**
     * The pattern to find the item on the second hand of the NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern ITEM_SECOND_HAND = Pattern
        .compile("^\\s*(itemSecondHand)\\s*=\\s*([0-9]{1,4})[\\s;]*",
            Pattern.MULTILINE);

    /**
     * The pattern to find the item on the shoes of the NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern ITEM_SHOES = Pattern.compile(
        "^\\s*(itemShoes)\\s*=\\s*([0-9]{1,4})[\\s;]*", Pattern.MULTILINE);

    /**
     * The pattern to find the item on the trousers of the NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern ITEM_TROUSERS = Pattern.compile(
        "^\\s*(itemTrousers)\\s*=\\s*([0-9]{1,4})[\\s;]*", Pattern.MULTILINE);

    /**
     * The documentation entries for the children of this entry.
     */
    private final DocuEntry docuChildren[];

    /**
     * Prepare all required values for this part of the parser.
     */
    @SuppressWarnings("nls")
    public NpcEquipment() {
        final List<DocuEntry> tempList = new ArrayList<DocuEntry>();
        tempList.add(new ChildDocuClass("Head"));
        tempList.add(new ChildDocuClass("Chest"));
        tempList.add(new ChildDocuClass("Coat"));
        tempList.add(new ChildDocuClass("MainHand"));
        tempList.add(new ChildDocuClass("SecHand"));
        tempList.add(new ChildDocuClass("Hands"));
        tempList.add(new ChildDocuClass("Trousers"));
        tempList.add(new ChildDocuClass("Shoes"));

        docuChildren = tempList.toArray(new DocuEntry[tempList.size()]);
    }

    @Override
    public boolean canParseLine(final Line lineStruct) {
        final String line = lineStruct.getLine();
        if (ITEM_HEAD.matcher(line).find()) {
            return true;
        }

        if (ITEM_CHEST.matcher(line).find()) {
            return true;
        }

        if (ITEM_COAT.matcher(line).find()) {
            return true;
        }

        if (ITEM_MAIN_HAND.matcher(line).find()) {
            return true;
        }

        if (ITEM_SECOND_HAND.matcher(line).find()) {
            return true;
        }

        if (ITEM_HANDS.matcher(line).find()) {
            return true;
        }

        if (ITEM_TROUSERS.matcher(line).find()) {
            return true;
        }

        if (ITEM_SHOES.matcher(line).find()) {
            return true;
        }

        return false;
    }

    @Override
    @SuppressWarnings("nls")
    public DocuEntry getChild(final int index) {
        if ((index < 0) || (index >= docuChildren.length)) {
            throw new IndexOutOfBoundsException(
                "Index does not match the amount of children.");
        }

        return docuChildren[index];
    }

    @Override
    public int getChildCount() {
        return docuChildren.length;
    }

    @Override
    @SuppressWarnings("nls")
    public String getDescription() {
        return Lang.getMsg(getClass(), "Docu.description");
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
    @SuppressWarnings("nls")
    public String getTitle() {
        return Lang.getMsg(getClass(), "Docu.title");
    }

    /**
     * Parse the line and extract the required data.
     */
    @Override
    public void parseLine(final Line line, final ParsedNpc npc) {
        Matcher match = ITEM_HEAD.matcher(line.getLine());
        if (match.find()) {
            final Items item = extractItem(match, line, npc);
            if (item != null) {
                npc.addNpcData(ParsedEquipment.getInstance(
                    EquipmentSlots.head, item));
            }
            return;
        }

        match = ITEM_CHEST.matcher(line.getLine());
        if (match.find()) {
            final Items item = extractItem(match, line, npc);
            if (item != null) {
                npc.addNpcData(ParsedEquipment.getInstance(
                    EquipmentSlots.chest, item));
            }
            return;
        }

        match = ITEM_COAT.matcher(line.getLine());
        if (match.find()) {
            final Items item = extractItem(match, line, npc);
            if (item != null) {
                npc.addNpcData(ParsedEquipment.getInstance(
                    EquipmentSlots.coat, item));
            }
            return;
        }

        match = ITEM_MAIN_HAND.matcher(line.getLine());
        if (match.find()) {
            final Items item = extractItem(match, line, npc);
            if (item != null) {
                npc.addNpcData(ParsedEquipment.getInstance(
                    EquipmentSlots.mainHand, item));
            }
            return;
        }

        match = ITEM_SECOND_HAND.matcher(line.getLine());
        if (match.find()) {
            final Items item = extractItem(match, line, npc);
            if (item != null) {
                npc.addNpcData(ParsedEquipment.getInstance(
                    EquipmentSlots.secondHand, item));
            }
            return;
        }

        match = ITEM_HANDS.matcher(line.getLine());
        if (match.find()) {
            final Items item = extractItem(match, line, npc);
            if (item != null) {
                npc.addNpcData(ParsedEquipment.getInstance(
                    EquipmentSlots.hands, item));
            }
            return;
        }

        match = ITEM_TROUSERS.matcher(line.getLine());
        if (match.find()) {
            final Items item = extractItem(match, line, npc);
            if (item != null) {
                npc.addNpcData(ParsedEquipment.getInstance(
                    EquipmentSlots.trousers, item));
            }
            return;
        }

        match = ITEM_SHOES.matcher(line.getLine());
        if (match.find()) {
            final Items item = extractItem(match, line, npc);
            if (item != null) {
                npc.addNpcData(ParsedEquipment.getInstance(
                    EquipmentSlots.feet, item));
            }
            return;
        }

        npc.addError(line, Lang.getMsg(getClass(), "general")); //$NON-NLS-1$
    }

    /**
     * Parse a segment and extract the tokens for the syntax highlighting.
     */
    @Override
    public void parseSegment(final Segment segment, final int offset,
        final List<Token> tokens) {
        parseSegmentImpl(segment, offset, tokens, ITEM_HEAD);
        parseSegmentImpl(segment, offset, tokens, ITEM_CHEST);
        parseSegmentImpl(segment, offset, tokens, ITEM_COAT);
        parseSegmentImpl(segment, offset, tokens, ITEM_MAIN_HAND);
        parseSegmentImpl(segment, offset, tokens, ITEM_SECOND_HAND);
        parseSegmentImpl(segment, offset, tokens, ITEM_HANDS);
        parseSegmentImpl(segment, offset, tokens, ITEM_TROUSERS);
        parseSegmentImpl(segment, offset, tokens, ITEM_SHOES);
    }

    /**
     * Extract a item from the matcher. This is a helper function since the
     * pattern in this parser all look nearly the same and the extraction syntax
     * is in all cases the same.
     * 
     * @param matcher the matcher to extract the text
     * @param line the line that is currently handled
     * @param npc the NPC that is currently processed
     * @return the item that was extracted or <code>null</code>
     */
    private Items extractItem(final Matcher matcher, final Line line,
        final ParsedNpc npc) {
        final int itemId = Integer.parseInt(matcher.group(2));

        Items item = null;
        for (final Items it : Items.values()) {
            if (it.getItemId() == itemId) {
                item = it;
                break;
            }
        }

        if (item == null) {
            npc.addError(
                line,
                String.format(
                    Lang.getMsg(getClass(), "item"), Integer.toString(itemId), matcher.group(0))); //$NON-NLS-1$
        }

        return item;
    }

    /**
     * Additional implementation to parse a segment. This reads all lines in the
     * segment in and matches it against the pattern handed over.
     * 
     * @param segment the segment
     * @param offset the offset to the start of the segment
     * @param tokens the list of old tokens
     * @param pattern the pattern used to check
     */
    private void parseSegmentImpl(final Segment segment, final int offset,
        final List<Token> tokens, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(segment);
        while (matcher.find()) {
            tokens.add(new Token(TokenType.KEYWORD, matcher.start(1) + offset,
                matcher.end(1) - matcher.start(1)));
        }
    }
}
