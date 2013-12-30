/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.client;

import illarion.common.net.NetCommWriter;
import illarion.common.types.ItemCount;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * This command is used to buy a item from a trader.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
@Immutable
public final class BuyTradingItem extends AbstractTradeItemCmd {
    /**
     * The index of the item that is supposed to be bought.
     */
    private final short index;

    /**
     * The amount of items to buy.
     */
    @Nonnull
    private final ItemCount amount;

    /**
     * The sub command ID for this command.
     */
    private static final int SUB_CMD_ID = 2;

    /**
     * Default constructor for the trade item command.
     *
     * @param dialogId the ID of the dialog to buy the item from
     * @param index    the index of the item to buy
     * @param count    the amount of items to buy
     */
    public BuyTradingItem(final int dialogId, final int index, @Nonnull final ItemCount count) {
        super(dialogId, SUB_CMD_ID);

        this.index = (short) index;
        amount = count;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        super.encode(writer);
        writer.writeUByte(index);
        amount.encode(writer);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString(super.toString() + " Index: " + index + ' ' + amount);
    }
}
