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
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parsed.AbstractParsedTrade;
import illarion.easynpc.parsed.ParsedTradeComplex;
import illarion.easynpc.parsed.shared.ParsedItemData;
import illarion.easynpc.parser.shared.ItemData;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This parser is able to read the definitions for the traded items with the simple syntax.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class NpcTradeComplex implements NpcType {
    @Nonnull
    private static String BASE_PATTERN = "^\\s*((sellItem)|(buyPrimaryItem)|(buySecondaryItem))\\s*=";

    private static final String ID_PATTERN = ",*\\s*id\\s*\\(\\s*(\\d{1,4})\\s*\\)\\s*";
    private static final String DE_PATTERN = ",*\\s*de\\s*\\(\\s*\"([^\"]+)\"\\s*\\)\\s*";
    private static final String EN_PATTERN = ",*\\s*en\\s*\\(\\s*\"([^\"]+)\"\\s*\\)\\s*";
    private static final String PRICE_PATTERN = ",*\\s*price\\s*\\(\\s*(\\d+)\\s*\\)\\s*";
    private static final String STACK_PATTERN = ",*\\s*stack\\s*\\(\\s*(\\d+)\\s*\\)\\s*";
    private static final String QUALITY_PATTERN = ",*\\s*quality\\s*\\(\\s*(\\d{3})\\s*\\)\\s*";
    private static final String DATA_PATTERN = ",*\\s*data\\s*\\(\\s*" + ItemData.REGEXP + "\\s*\\)\\s*";

    /**
     * The pattern to fetch the items the NPC buys and sells.
     */
    private static final Pattern COMPILED_PATTERN = Pattern.compile(BASE_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final Pattern COMPILED_ID_PATTERN = Pattern.compile(ID_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final Pattern COMPILED_DE_PATTERN = Pattern.compile(DE_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final Pattern COMPILED_EN_PATTERN = Pattern.compile(EN_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final Pattern COMPILED_PRICE_PATTERN = Pattern.compile(PRICE_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final Pattern COMPILED_STACK_PATTERN = Pattern.compile(STACK_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final Pattern COMPILED_QUALITY_PATTERN = Pattern.compile(QUALITY_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final Pattern COMPILED_DATA_PATTERN = Pattern.compile(DATA_PATTERN, Pattern.CASE_INSENSITIVE);

    /**
     * The documentation entry for the sell items.
     */
    private final DocuEntry sellEntry = new DocuEntry() {
        @Nonnull
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
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Sell.description");
        }

        @Override
        @SuppressWarnings("nls")
        public String getExample() {
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Sell.example");
        }

        @Override
        @SuppressWarnings("nls")
        public String getSyntax() {
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Sell.syntax");
        }

        @Override
        @SuppressWarnings("nls")
        public String getTitle() {
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Sell.title");
        }
    };

    /**
     * The documentation entry for the buy primary items.
     */
    private final DocuEntry buyPrimaryEntry = new DocuEntry() {
        @Nonnull
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
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Buy.Primary.description");
        }

        @Override
        @SuppressWarnings("nls")
        public String getExample() {
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Buy.Primary.example");
        }

        @Override
        @SuppressWarnings("nls")
        public String getSyntax() {
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Buy.Primary.syntax");
        }

        @Override
        @SuppressWarnings("nls")
        public String getTitle() {
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Buy.Primary.title");
        }
    };

    /**
     * The documentation entry for the buy primary items.
     */
    private final DocuEntry buySecondaryEntry = new DocuEntry() {
        @Nonnull
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
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Buy.Secondary.description");
        }

        @Override
        @SuppressWarnings("nls")
        public String getExample() {
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Buy.Secondary.example");
        }

        @Override
        @SuppressWarnings("nls")
        public String getSyntax() {
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Buy.Secondary.syntax");
        }

        @Override
        @SuppressWarnings("nls")
        public String getTitle() {
            return Lang.getMsg(NpcTradeComplex.class, "Docu.Buy.Secondary.title");
        }
    };

    /**
     * Check if the line contains the definition of a hair ID or a beard ID.
     */
    @Override
    public boolean canParseLine(@Nonnull final Line lineStruct) {
        final String line = lineStruct.getLine();

        return COMPILED_PATTERN.matcher(line).find();

    }

    /**
     * Get the documentation child.
     */
    @Nonnull
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
    @Nullable
    @Override
    public String getExample() {
        return null;
    }

    /**
     * This parser contains no syntax. The syntax is stored in the documentation
     * children of this parser.
     */
    @Nullable
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
    public void parseLine(@Nonnull final Line line, @Nonnull final ParsedNpc npc) {
        Matcher matcher;

        matcher = COMPILED_PATTERN.matcher(line.getLine());

        if (!matcher.find()) {
            return;
        }

        final AbstractParsedTrade.TradeMode mode;
        if (matcher.group(2) != null) {
            mode = AbstractParsedTrade.TradeMode.selling;
        } else if (matcher.group(3) != null) {
            mode = AbstractParsedTrade.TradeMode.buyingPrimary;
        } else if (matcher.group(4) != null) {
            mode = AbstractParsedTrade.TradeMode.buyingSecondary;
        } else {
            throw new IllegalStateException("sanity check failed.");
        }

        String currentLine = matcher.replaceAll("");

        final Matcher idMatcher = COMPILED_ID_PATTERN.matcher(currentLine);
        final int itemId;
        if (idMatcher.find()) {
            itemId = Integer.parseInt(idMatcher.group(1));
            currentLine = idMatcher.replaceFirst("");
        } else {
            npc.addError(line, "No ID specified.");
            return;
        }

        final Matcher deMatcher = COMPILED_DE_PATTERN.matcher(currentLine);
        final String nameDe;
        if (deMatcher.find()) {
            nameDe = deMatcher.group(1);
            currentLine = deMatcher.replaceFirst("");
        } else {
            nameDe = null;
        }

        final Matcher enMatcher = COMPILED_EN_PATTERN.matcher(currentLine);
        final String nameEn;
        if (enMatcher.find()) {
            nameEn = enMatcher.group(1);
            currentLine = enMatcher.replaceFirst("");
        } else {
            nameEn = null;
        }

        final Matcher priceMatcher = COMPILED_PRICE_PATTERN.matcher(currentLine);
        final int price;
        if (priceMatcher.find()) {
            price = Integer.parseInt(priceMatcher.group(1));
            currentLine = priceMatcher.replaceFirst("");
        } else {
            price = 0;
        }

        final Matcher stackMatcher = COMPILED_STACK_PATTERN.matcher(currentLine);
        final int stack;
        if (stackMatcher.find()) {
            stack = Integer.parseInt(stackMatcher.group(1));
            currentLine = stackMatcher.replaceFirst("");
        } else {
            stack = 0;
        }

        final Matcher qualityMatcher = COMPILED_QUALITY_PATTERN.matcher(currentLine);
        final int quality;
        if (qualityMatcher.find()) {
            quality = Integer.parseInt(qualityMatcher.group(1));
            currentLine = qualityMatcher.replaceFirst("");
        } else {
            quality = 0;
        }

        final Matcher dataMatcher = COMPILED_DATA_PATTERN.matcher(currentLine);
        final ParsedItemData data;
        if (dataMatcher.find()) {
            data = ItemData.getData(dataMatcher.group(1));
            currentLine = dataMatcher.replaceFirst("");
        } else {
            data = ItemData.getData("");
        }

        currentLine = currentLine.replaceAll("[\\s,]*", "");

        if (!currentLine.isEmpty()) {
            npc.addError(line, "Found strange remains: " + currentLine);
        }

        npc.addNpcData(new ParsedTradeComplex(mode, itemId, nameDe, nameEn, price, stack, quality, data));
    }

    @Override
    public void enlistHighlightedWords(@Nonnull final TokenMap map) {
        map.put("sellItem", Token.RESERVED_WORD);
        map.put("buyPrimaryItem", Token.RESERVED_WORD);
        map.put("buySecondaryItem", Token.RESERVED_WORD);
    }
}
