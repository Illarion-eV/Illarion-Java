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
package illarion.easynpc.parser;

import illarion.easynpc.EasyNpcScript.Line;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.data.Items;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parsed.ParsedTradeSimple;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This parser is able to read the definitions for the traded items with the simple syntax.
 *
 * @author Martin Karing
 */
public final class NpcTradeSimple implements NpcType {
    /**
     * The pattern to fetch the items the NPC sells.
     */
    @SuppressWarnings("nls")
    private static final Pattern SELL_PATTERN = Pattern.compile("^\\s*(sellItems)\\s*=\\s*([0-9\\s,]+)[\\s;]*",
            Pattern.CASE_INSENSITIVE);

    /**
     * The pattern to fetch the items the NPC buys primary.
     */
    @SuppressWarnings("nls")
    private static final Pattern BUY_PRIMARY_PATTERN = Pattern.compile(
            "^\\s*(buyPrimaryItems)\\s*=\\s*([0-9\\s,]+)[\\s;]*", Pattern.CASE_INSENSITIVE);

    /**
     * The pattern to fetch the items the NPC buys secondary.
     */
    @SuppressWarnings("nls")
    private static final Pattern BUY_SECONDARY_PATTERN = Pattern.compile(
            "^\\s*(buySecondaryItems)\\s*=\\s*([0-9\\s,]+)[\\s;]*", Pattern.CASE_INSENSITIVE);

    /**
     * This pattern is used to split the item id lists
     */
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s*,\\s*");

    /**
     * The documentation entry for the sell items.
     */
    private final DocuEntry sellEntry = new DocuEntry() {
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
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Sell.description");
        }

        @Override
        @SuppressWarnings("nls")
        public String getExample() {
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Sell.example");
        }

        @Override
        @SuppressWarnings("nls")
        public String getSyntax() {
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Sell.syntax");
        }

        @Override
        @SuppressWarnings("nls")
        public String getTitle() {
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Sell.title");
        }
    };

    /**
     * The documentation entry for the buy primary items.
     */
    private final DocuEntry buyPrimaryEntry = new DocuEntry() {
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
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Buy.Primary.description");
        }

        @Override
        @SuppressWarnings("nls")
        public String getExample() {
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Buy.Primary.example");
        }

        @Override
        @SuppressWarnings("nls")
        public String getSyntax() {
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Buy.Primary.syntax");
        }

        @Override
        @SuppressWarnings("nls")
        public String getTitle() {
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Buy.Primary.title");
        }
    };

    /**
     * The documentation entry for the buy primary items.
     */
    private final DocuEntry buySecondaryEntry = new DocuEntry() {
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
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Buy.Secondary.description");
        }

        @Override
        @SuppressWarnings("nls")
        public String getExample() {
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Buy.Secondary.example");
        }

        @Override
        @SuppressWarnings("nls")
        public String getSyntax() {
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Buy.Secondary.syntax");
        }

        @Override
        @SuppressWarnings("nls")
        public String getTitle() {
            return Lang.getMsg(NpcTradeSimple.class, "Docu.Buy.Secondary.title");
        }
    };

    /**
     * Check if the line contains the definition of a hair ID or a beard ID.
     */
    @Override
    public boolean canParseLine(final Line lineStruct) {
        final String line = lineStruct.getLine();

        return SELL_PATTERN.matcher(line).find() || BUY_PRIMARY_PATTERN.matcher(line).find() ||
                BUY_SECONDARY_PATTERN.matcher(line).find();

    }

    /**
     * Get the documentation child.
     */
    @Override
    @SuppressWarnings("nls")
    public DocuEntry getChild(final int index) {
        if (index == 0) {
            return sellEntry;
        }
        if (index == 1) {
            return buyPrimaryEntry;
        }
        if (index == 2) {
            return buySecondaryEntry;
        }

        throw new IndexOutOfBoundsException("The index is too small or too large");
    }

    /**
     * This parser contains 2 children. One documentation children for the hair,
     * one for the beard.
     */
    @Override
    public int getChildCount() {
        return 3;
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

        matcher = SELL_PATTERN.matcher(line.getLine());
        if (matcher.find()) {
            final String itemList = matcher.group(2);
            final String[] items = SPLIT_PATTERN.split(itemList);

            final int[] itemIds = new int[items.length];
            for (int i = 0; i < itemIds.length; i++) {
                itemIds[i] = Integer.parseInt(items[i]);
                if (!Items.contains(itemIds[i])) {
                    npc.addError(line,
                            String.format(Lang.getMsg(getClass(), "illegalItem"), Integer.toString(itemIds[i])));
                }
            }

            npc.addNpcData(new ParsedTradeSimple(ParsedTradeSimple.TradeMode.selling, itemIds));
            return;
        }

        matcher = BUY_PRIMARY_PATTERN.matcher(line.getLine());
        if (matcher.find()) {
            final String itemList = matcher.group(2);
            final String[] items = SPLIT_PATTERN.split(itemList);

            final int[] itemIds = new int[items.length];
            for (int i = 0; i < itemIds.length; i++) {
                itemIds[i] = Integer.parseInt(items[i]);
                if (!Items.contains(itemIds[i])) {
                    npc.addError(line,
                            String.format(Lang.getMsg(getClass(), "illegalItem"), Integer.toString(itemIds[i])));
                }
            }

            npc.addNpcData(new ParsedTradeSimple(ParsedTradeSimple.TradeMode.buyingPrimary, itemIds));
            return;
        }

        matcher = BUY_SECONDARY_PATTERN.matcher(line.getLine());
        if (matcher.find()) {
            final String itemList = matcher.group(2);
            final String[] items = SPLIT_PATTERN.split(itemList);

            final int[] itemIds = new int[items.length];
            for (int i = 0; i < itemIds.length; i++) {
                itemIds[i] = Integer.parseInt(items[i]);
                if (!Items.contains(itemIds[i])) {
                    npc.addError(line, Lang.getMsg(getClass(), "illegalItem")); //$NON-NLS-1$
                }
            }

            npc.addNpcData(new ParsedTradeSimple(ParsedTradeSimple.TradeMode.buyingSecondary, itemIds));
            return;
        }
    }

    @Override
    public void enlistHighlightedWords(final TokenMap map) {
        map.put("sellItems", Token.RESERVED_WORD);
        map.put("buyPrimaryItems", Token.RESERVED_WORD);
        map.put("buySecondaryItems", Token.RESERVED_WORD);
    }
}
