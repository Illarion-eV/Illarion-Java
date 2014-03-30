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
package illarion.client.net.client;

import illarion.common.net.NetCommWriter;
import illarion.common.types.ItemCount;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * This command is used to sell a item from a container to a trader.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
@Immutable
public final class SellContainerItemCmd extends AbstractTradeItemCmd {
    /**
     * The sub command ID for this command.
     */
    private static final int SUB_CMD_ID = 1;

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
    @Nonnull
    private final ItemCount amount;

    /**
     * Default constructor for the trade item command.
     *
     * @param dialogId the ID of the trading dialog to sell the item to
     * @param container the ID of the container to sell the item from
     * @param slot the slot in the container to sell the item from
     * @param count the amount of items to be sold
     */
    public SellContainerItemCmd(
            final int dialogId, final int container, final int slot, @Nonnull final ItemCount count) {
        super(dialogId, SUB_CMD_ID);

        this.container = (short) (container + 1);
        this.slot = slot;
        amount = count;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        super.encode(writer);
        writer.writeUByte(container);
        writer.writeUShort(slot);
        amount.encode(writer);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString(super.toString() + " Item: " + container + '/' + slot + ' ' + amount);
    }
}
