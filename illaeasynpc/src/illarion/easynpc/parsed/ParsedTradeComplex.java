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

import illarion.easynpc.parsed.shared.ParsedItemData;
import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import java.io.IOException;
import java.io.Writer;

/**
 * This class enables the NPC to trade and stores a single item the NPC is trading.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParsedTradeComplex extends AbstractParsedTrade {
    /**
     * The IDs of the items that are supposed to be traded.
     */
    private final int itemId;
    private final String textDe;
    private final String textEn;
    private final int price;
    private final int stackSize;
    private final int quality;
    private final ParsedItemData data;


    public ParsedTradeComplex(final AbstractParsedTrade.TradeMode tradeMode, final int tradeItemId,
                              final String itemTextDe, final String itemTextEn, final int tradePrice,
                              final int tradeStackSize, final int itemQuality, final ParsedItemData itemData) {
        super(tradeMode);

        itemId = tradeItemId;
        textDe = itemTextDe;
        textEn = itemTextEn;
        price = tradePrice;
        stackSize = tradeStackSize;
        quality = itemQuality;
        data = itemData;
    }

    @Override
    public void writeEasyNpc(final Writer target, final EasyNpcWriter.WritingStage stage) throws IOException {
        if (stage == EasyNpcWriter.WritingStage.trading) {
            switch (getMode()) {
                case selling:
                    target.write("sellItem = ");
                    break;
                case buyingPrimary:
                    target.write("buyPrimaryItem = ");
                    break;
                case buyingSecondary:
                    target.write("buySecondaryItem = ");
                    break;
            }

            target.write(String.format("id(%1$d)", itemId));
            if (textDe != null) {
                target.write(String.format(", de(\"%1$s\")", textDe));
            }
            if (textEn != null) {
                target.write(String.format(", en(\"%1$s\")", textEn));
            }
            if (price > 0) {
                target.write(String.format(", price(%1$d)", price));
            }
            if (stackSize > 0) {
                target.write(String.format(", stack(%1$d)", stackSize));
            }
            if (quality > 0) {
                target.write(String.format(", quality(%1$d)", quality));
            }
            if (data.hasValues()) {
                target.write(String.format(", data(%1$s)", data.getEasyNPC()));
            }
            target.write(EasyNpcWriter.NL);
        }
    }

    @Override
    public void buildSQL(final SQLBuilder builder) {
        // nothing to do
    }

    @Override
    public void writeLua(final Writer target, final LuaWriter.WritingStage stage) throws IOException {
        if (stage == LuaWriter.WritingStage.Trading) {
            target.write("tradingNPC:addItem(npc.base.trade.tradeNPCItem(");
            target.write(Integer.toString(itemId));
            target.write(",");
            switch (getMode()) {
                case selling:
                    target.write("\"sell\"");
                    break;
                case buyingPrimary:
                    target.write("\"buyPrimary\"");
                    break;
                case buyingSecondary:
                    target.write("\"buySecondary\"");
                    break;
            }
            target.write(",");

            if (textDe != null) {
                target.write(String.format("\"%1$s\",", textDe));
            } else {
                target.write("nil,");
            }
            if (textEn != null) {
                target.write(String.format("\"%1$s\",", textEn));
            } else {
                target.write("nil,");
            }
            if (price > 0) {
                target.write(String.format("%1$d,", price));
            } else {
                target.write("nil,");
            }
            if (stackSize > 0) {
                target.write(String.format("%1$d,", stackSize));
            } else {
                target.write("nil,");
            }
            if (quality > 0) {
                target.write(String.format("%1$d,", quality));
            } else {
                target.write("nil,");
            }
            if (data.hasValues()) {
                target.write(data.getLua());
            } else {
                target.write("nil");
            }
            target.write("));");
            target.write(LuaWriter.NL);
        }
    }
}
