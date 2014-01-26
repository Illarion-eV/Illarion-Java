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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * This class enables the NPC to trade and stores some items the NPC is trading.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParsedTradeSimple extends AbstractParsedTrade {
    /**
     * The IDs of the items that are supposed to be traded.
     */
    @Nonnull
    private final int[] itemIds;

    public ParsedTradeSimple(final ParsedTradeSimple.TradeMode tradeMode, @Nonnull final List<Integer> tradeItemIds) {
        super(tradeMode);
        itemIds = new int[tradeItemIds.size()];
        for (int i = 0; i < itemIds.length; i++) {
            itemIds[i] = tradeItemIds.get(i);
        }
    }

    public ParsedTradeSimple(final ParsedTradeSimple.TradeMode tradeMode, @Nonnull final int... tradeItemIds) {
        super(tradeMode);
        itemIds = Arrays.copyOf(tradeItemIds, tradeItemIds.length);
    }

    @Override
    public void writeEasyNpc(@Nonnull final Writer target, @Nonnull final EasyNpcWriter.WritingStage stage)
            throws IOException {
        if (stage == EasyNpcWriter.WritingStage.trading) {
            switch (getMode()) {
                case selling:
                    target.write("sellItems = ");
                    break;
                case buyingPrimary:
                    target.write("buyPrimaryItems = ");
                    break;
                case buyingSecondary:
                    target.write("buySecondaryItems = ");
                    break;
            }

            if (itemIds.length > 0) {
                target.write(Integer.toString(itemIds[0]));
                for (int i = 1; i < itemIds.length; i++) {
                    target.write(", ");
                    target.write(Integer.toString(itemIds[i]));
                }
            }
            target.write(EasyNpcWriter.NL);
        }
    }

    @Override
    public void buildSQL(@Nonnull final SQLBuilder builder) {
        // nothing to do
    }

    @Override
    public void writeLua(@Nonnull final Writer target, @Nonnull final LuaWriter.WritingStage stage) throws IOException {
        if (stage == LuaWriter.WritingStage.Trading) {
            for (final int itemId : itemIds) {
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
                target.write("));");
                target.write(LuaWriter.NL);
            }
        }
    }
}
