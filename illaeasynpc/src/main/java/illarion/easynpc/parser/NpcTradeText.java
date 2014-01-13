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

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parsed.ParsedTradeText;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to parse the texts related to trading from the NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class NpcTradeText implements NpcType {
    private static final Map<ParsedTradeText.TradeTextType, Pattern> PATTERN_MAP;

    static {
        PATTERN_MAP = new EnumMap<>(ParsedTradeText.TradeTextType.class);
        PATTERN_MAP.put(ParsedTradeText.TradeTextType.NoMoney, Pattern.compile(
                "^\\s*(tradeNotEnoughMoneyMsg)\\s*[\\(]*\\s*\"([^\"]*)\"\\s*,\\s*\"([^\"]*)\"\\s*[\\)]*\\s*$"));
        PATTERN_MAP.put(ParsedTradeText.TradeTextType.TradingCanceled, Pattern.compile(
                "^\\s*(tradeFinishedMsg)\\s*[\\(]*\\s*\"([^\"]*)\"\\s*,\\s*\"([^\"]*)\"\\s*[\\)]*\\s*$"));
        PATTERN_MAP.put(ParsedTradeText.TradeTextType.TradingCanceledWithoutTrade, Pattern.compile(
                "^\\s*(tradeFinishedWithoutTradingMsg)\\s*[\\(]*\\s*\"([^\"]*)\"\\s*,\\s*\"([^\"]*)\"\\s*[\\)]*\\s*$"));
        PATTERN_MAP.put(ParsedTradeText.TradeTextType.WrongItem, Pattern.compile(
                "^\\s*(tradeWrongItemMsg)\\s*[\\(]*\\s*\"([^\"]*)\"\\s*,\\s*\"([^\"]*)\"\\s*[\\)]*\\s*$"));
    }

    @Override
    public boolean canParseLine(@Nonnull final EasyNpcScript.Line line) {
        for (Pattern pattern : PATTERN_MAP.values()) {
            if (pattern.matcher(line.getLine()).matches()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void parseLine(@Nonnull final EasyNpcScript.Line line, @Nonnull final ParsedNpc npc) {
        String stringLine = line.getLine();
        for (Map.Entry<ParsedTradeText.TradeTextType, Pattern> entry : PATTERN_MAP.entrySet()) {
            ParsedTradeText parsedText = parseHelper(entry.getValue().matcher(stringLine), entry.getKey());
            if (parsedText != null) {
                npc.addNpcData(parsedText);
                return;
            }
        }
    }

    /**
     * This support function is used to check if the matcher matches the assigned line and if it does to extract the
     * text and pack it into a parsed object.
     *
     * @param matcher the matcher
     * @param type    the type of the text tested
     * @return the parsed object in case the matcher matched, else {@code null}
     */
    @Nullable
    private static ParsedTradeText parseHelper(@Nonnull final Matcher matcher, final ParsedTradeText.TradeTextType type) {
        if (matcher.find()) {
            final String germanText = matcher.group(2);
            final String englishText = matcher.group(3);
            return new ParsedTradeText(type, germanText, englishText);
        }
        return null;
    }

    @Override
    public void enlistHighlightedWords(@Nonnull final TokenMap map) {
        map.put("tradeNotEnoughMoneyMsg", Token.RESERVED_WORD);
        map.put("tradeFinishedMsg", Token.RESERVED_WORD);
        map.put("tradeFinishedWithoutTradingMsg", Token.RESERVED_WORD);
        map.put("tradeWrongItemMsg", Token.RESERVED_WORD);
    }

    @Nonnull
    @Override
    public DocuEntry getChild(final int index) {
        throw new IllegalArgumentException("There are no children to request.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(NpcTradeText.class, "Docu.description");
    }

    @Override
    public String getExample() {
        return Lang.getMsg(NpcTradeText.class, "Docu.example");
    }

    @Override
    public String getSyntax() {
        return Lang.getMsg(NpcTradeText.class, "Docu.syntax");
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(NpcTradeText.class, "Docu.title");
    }
}
