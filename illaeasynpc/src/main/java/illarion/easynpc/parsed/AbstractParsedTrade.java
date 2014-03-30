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

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;

/**
 * This class contains the shared code of the data storage for trading data.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractParsedTrade implements ParsedData {
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
     * The modules required for the trader NPC implementation.
     */
    private static final String[] MODULES = {"npc.base.basic", "npc.base.trade"};

    /**
     * The mode of this trading operation.
     */
    private final AbstractParsedTrade.TradeMode mode;

    /**
     * Default constructor that stores the type of the trade objects in this class.
     *
     * @param tradeMode the trading mode
     */
    protected AbstractParsedTrade(final AbstractParsedTrade.TradeMode tradeMode) {
        mode = tradeMode;
    }

    /**
     * Get the selected trading mode.
     *
     * @return the selected trading mode
     */
    protected AbstractParsedTrade.TradeMode getMode() {
        return mode;
    }

    @Override
    public boolean effectsEasyNpcStage(@Nonnull final EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.trading;
    }

    @Override
    public boolean effectsLuaWritingStage(@Nonnull final LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Trading;
    }

    @Nonnull
    @Override
    public String[] getRequiredModules() {
        return MODULES;
    }
}
