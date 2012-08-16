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
 * This class enables the NPC to trade and stores a single item the NPC is trading.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParsedTradeSimple implements ParsedData {
    /**
     * This enumerator is used to define if this entry defines a buying or a selling operation.
     */
    public enum TradeMode {
        /**
         * The NPC is buying this primary craft item from the player.
         */
        buyingPrimary,

        /**
         * The NPC is buying this secondary craft item from the player.
         */
        buyingSecondary,

        /**
         * The NPC is selling to the player.
         */
        selling,
    }

    /**
     * The mode of this trading operation.
     */
    private final TradeMode mode;

    /**
     * The IDs of the items that are supposed to be traded.
     */
    private final int[] itemIds;

    public ParsedTradeSimple(final TradeMode tradeMode, final int... tradeItemIds) {
        mode = tradeMode;
        itemIds = tradeItemIds;
    }

    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.trading;
    }

    @Override
    public void writeEasyNpc(final Writer target, final EasyNpcWriter.WritingStage stage) throws IOException {
        if (stage == EasyNpcWriter.WritingStage.trading) {
            switch (mode) {
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
        }
    }

    @Override
    public void buildSQL(final SQLBuilder builder) {
        // nothing to do
    }

    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Trading;
    }

    /**
     * The modules required for the trader NPC implementation.
     */
    private static final String[] MODULES = {"npc.base.basic", "npc.base.trade"};

    @Override
    public String[] getRequiredModules() {
        return MODULES;
    }

    @Override
    public void writeLua(final Writer target, final LuaWriter.WritingStage stage) throws IOException {
        if (stage == LuaWriter.WritingStage.Trading) {
            for (final int itemId : itemIds) {
                target.write("tradingNPC:addItem(");
                target.write(Integer.toString(itemId));
                target.write(",");
                switch (mode) {
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
                target.write(");");
                target.write(LuaWriter.NL);
            }
        }
    }
}
