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

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    private static final List<String> MODULES = Arrays.asList("npc.base.basic", "npc.base.trade");

    /**
     * The mode of this trading operation.
     */
    private final TradeMode mode;

    /**
     * Default constructor that stores the type of the trade objects in this class.
     *
     * @param tradeMode the trading mode
     */
    AbstractParsedTrade(TradeMode tradeMode) {
        mode = tradeMode;
    }

    /**
     * Get the selected trading mode.
     *
     * @return the selected trading mode
     */
    TradeMode getMode() {
        return mode;
    }

    @Override
    public boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Trading;
    }

    @Nonnull
    @Override
    public Collection<String> getRequiredModules() {
        return MODULES;
    }
}
