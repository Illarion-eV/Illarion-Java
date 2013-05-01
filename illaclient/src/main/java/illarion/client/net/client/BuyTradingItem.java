/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
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

import illarion.client.net.CommandList;
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
public final class BuyTradingItem extends AbstractCommand {
    /**
     * The ID of the dialog to buy the item from.
     */
    private final int dialogId;

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
     * Default constructor for the trade item command.
     *
     * @param dialogId the ID of the dialog to buy the item from
     * @param index    the index of the item to buy
     * @param count    the amount of items to buy
     */
    public BuyTradingItem(final int dialogId, final int index, @Nonnull final ItemCount count) {
        super(CommandList.CMD_TRADE_ITEM);

        this.dialogId = dialogId;
        this.index = (short) index;
        amount = count;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeInt(dialogId);
        writer.writeByte((byte) 2);
        writer.writeUByte(index);
        amount.encode(writer);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString("dialog ID: " + dialogId + " Index: " + index + ' ' + amount);
    }
}
