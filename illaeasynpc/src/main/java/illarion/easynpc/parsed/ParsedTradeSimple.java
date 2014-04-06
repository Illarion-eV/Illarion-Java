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

    public ParsedTradeSimple(ParsedTradeSimple.TradeMode tradeMode, @Nonnull List<Integer> tradeItemIds) {
        super(tradeMode);
        itemIds = new int[tradeItemIds.size()];
        for (int i = 0; i < itemIds.length; i++) {
            itemIds[i] = tradeItemIds.get(i);
        }
    }

    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
        // nothing to do
    }

    @Override
    public void writeLua(@Nonnull Writer target, @Nonnull LuaWriter.WritingStage stage) throws IOException {
        if (stage == LuaWriter.WritingStage.Trading) {
            for (int itemId : itemIds) {
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
