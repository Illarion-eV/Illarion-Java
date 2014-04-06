/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.easynpc.parsed;

import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;

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
        TradingCanceledWithoutTrade,

        /**
         * This type stands for a text that is displayed in case the user tries to sell a invalid item.
         */
        WrongItem
    }

    /**
     * The type of this text entry.
     */
    private final TradeTextType type;

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
     * @param textType the type of the text
     * @param germanText the german text
     * @param englishText the english text
     */
    public ParsedTradeText(
            TradeTextType textType, String germanText, String englishText) {
        type = textType;
        german = germanText;
        english = englishText;
    }

    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
        // nothing
    }

    @Override
    public boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Trading;
    }

    @Nonnull
    @Override
    public Collection<String> getRequiredModules() {
        return Collections.singleton("npc.base.trade");
    }

    @Override
    public void writeLua(@Nonnull Writer target, @Nonnull LuaWriter.WritingStage stage) throws IOException {
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
            case WrongItem:
                target.write("addWrongItemMsg");
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
