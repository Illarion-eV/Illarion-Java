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
package illarion.easynpc.parsed;

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store one text line for the trader NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedTradeText implements ParsedData {
    /**
     * This enumerator stores the different types of text that can be stored in this class.
     */
    public enum TradeTextType {
        /**
         * This type stands for a text that is said by the NPC in case the player does not have enough money.
         */
        NoMoney,

        /**
         * This type stands for a text that is said in case the player cancels the trading with the NPC.
         */
        TradingCanceled,

        /**
         * This type stands for a text that is said in case the player cancels the trading with the NPC,
         * without having traded anything.
         */
        TradingCanceledWithoutTrade
    }

    /**
     * The type of this text entry.
     */
    private final ParsedTradeText.TradeTextType type;

    /**
     * The german text.
     */
    private final String german;

    /**
     * The english text.
     */
    private final String english;

    /**
     * This constructor creates this parsed instance with all required values.
     *
     * @param textType    the type of the text
     * @param germanText  the german text
     * @param englishText the english text
     */
    public ParsedTradeText(final ParsedTradeText.TradeTextType textType, final String germanText,
                           final String englishText) {
        type = textType;
        german = germanText;
        english = englishText;
    }

    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.trading;
    }

    @Override
    public void writeEasyNpc(final Writer target, final EasyNpcWriter.WritingStage stage) throws IOException {
        switch (type) {
            case NoMoney:
                target.write("tradeNotEnoughMoneyMsg");
                break;
            case TradingCanceled:
                target.write("tradeFinishedMsg");
                break;
            case TradingCanceledWithoutTrade:
                target.write("tradeFinishedWithoutTradingMsg");
                break;
        }

        target.write(" \"");
        target.write(german);
        target.write("\", \"");
        target.write(english);
        target.write('"');
        target.write(EasyNpcWriter.NL);
    }

    @Override
    public void buildSQL(final SQLBuilder builder) {
        // nothing
    }

    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Trading;
    }

    @Override
    public String[] getRequiredModules() {
        return new String[]{"npc.base.trade"};
    }

    @Override
    public void writeLua(final Writer target, final LuaWriter.WritingStage stage) throws IOException {
        target.write("tradingNPC:");
        switch (type) {
            case NoMoney:
                target.write("addNotEnoughMoneyMsg");
                break;
            case TradingCanceled:
                target.write("addDialogClosedMsg");
                break;
            case TradingCanceledWithoutTrade:
                target.write("addDialogClosedNoTradeMsg");
                break;
        }

        target.write("(\"");
        target.write(german);
        target.write("\", \"");
        target.write(english);
        target.write("\");");
        target.write(LuaWriter.NL);
    }
}
