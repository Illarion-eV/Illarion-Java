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
package illarion.client.net.server.events;

import illarion.client.world.items.MerchantItem;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * This event is send to the application once a merchant dialog is requested.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class DialogMerchantReceivedEvent extends AbstractDialogReceivedEvent implements ServerEvent {
    /**
     * The list of items sold by this NPC.
     */
    @Nonnull
    private final MerchantItem[] items;

    /**
     * Create a new instance of this event.
     *
     * @param dialogId the ID of this dialog
     * @param dialogTitle the title of the dialog
     * @param tradeItems a array of items that can be bought from the NPC
     */
    public DialogMerchantReceivedEvent(
            int dialogId,
            String dialogTitle,
            @Nonnull MerchantItem[] tradeItems) {
        super(dialogId, dialogTitle);
        items = Arrays.copyOf(tradeItems, tradeItems.length);
    }

    /**
     * Get the amount of items stored in this event.
     *
     * @return the amount of items
     */
    public int getItemCount() {
        return items.length;
    }

    /**
     * Get the item at a specified index in this event.
     *
     * @param index the index of the item
     * @return the merchant item
     */
    public MerchantItem getItem(int index) {
        return items[index];
    }
}
