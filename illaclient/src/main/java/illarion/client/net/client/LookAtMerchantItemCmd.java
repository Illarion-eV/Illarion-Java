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

import javax.annotation.Nonnull;

/**
 * This command is used to trigger the look at at a merchant dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class LookAtMerchantItemCmd extends AbstractTradeItemCmd {
    /**
     * The constant ID for the list of items sold.
     */
    public static final byte LIST_ID_SELL = 0;

    /**
     * The constant ID for the list of primary bought items.
     */
    public static final byte LIST_ID_BUY_PRIMARY = 1;

    /**
     * The constant ID for the list of secondary bought items.
     */
    public static final byte LIST_ID_BUY_SECONDARY = 2;

    /**
     * The sub command ID for this command.
     */
    private static final int SUB_CMD_ID = 3;

    /**
     * The ID of the list.
     */
    private final byte listId;

    /**
     * The ID of the slot.
     */
    private final short slotId;

    /**
     * The constructor for the look at a merchant item command.
     *
     * @param dialogId the merchant dialog
     * @param listId the index of the list, looked at. Can be {@link #LIST_ID_SELL}, {@link #LIST_ID_BUY_PRIMARY},
     * or {@link #LIST_ID_BUY_SECONDARY}
     * @param slotId the ID of the item slot
     */
    public LookAtMerchantItemCmd(int dialogId, byte listId, int slotId) {
        super(dialogId, SUB_CMD_ID);
        this.listId = listId;
        this.slotId = (short) slotId;
    }

    @Override
    public void encode(@Nonnull NetCommWriter writer) {
        super.encode(writer);
        writer.writeUByte(listId);
        writer.writeUByte(slotId);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString(super.toString() + " Look at list: " + listId + ", Look at index: " + slotId);
    }
}
