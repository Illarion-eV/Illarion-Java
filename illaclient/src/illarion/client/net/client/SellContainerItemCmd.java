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
import illarion.common.annotation.NonNull;
import illarion.common.net.NetCommWriter;
import illarion.common.types.ItemCount;
import net.jcip.annotations.Immutable;

/**
 * This command is used to sell a item from a container to a trader.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
@Immutable
public final class SellContainerItemCmd extends AbstractCommand {
    /**
     * The ID of the trading dialog to sell to.
     */
    private final int dialogId;

    /**
     * The ID of the container to sell the item from.
     */
    private final short container;

    /**
     * The slot in the inventory to sell the item from.
     */
    private final int slot;

    /**
     * The amount of items to be sold.
     */
    @NonNull
    private final ItemCount amount;

    /**
     * Default constructor for the trade item command.
     *
     * @param dialogId  the ID of the trading dialog to sell the item to
     * @param container the ID of the container to sell the item from
     * @param slot      the slot in the container to sell the item from
     * @param count     the amount of items to be sold
     */
    public SellContainerItemCmd(final int dialogId, final int container, final int slot,
                                @NonNull final ItemCount count) {
        super(CommandList.CMD_TRADE_ITEM);

        this.dialogId = dialogId;
        this.container = (short) (container + 1);
        this.slot = slot;
        amount = count;
    }

    @Override
    public void encode(@NonNull final NetCommWriter writer) {
        writer.writeInt(dialogId);
        writer.writeByte((byte) 1);
        writer.writeUByte(container);
        writer.writeUShort(slot);
        amount.encode(writer);
    }

    @NonNull
    @Override
    public String toString() {
        return toString("dialog ID: " + dialogId + " Item: " + container + '/' + slot + ' ' + amount);
    }
}
