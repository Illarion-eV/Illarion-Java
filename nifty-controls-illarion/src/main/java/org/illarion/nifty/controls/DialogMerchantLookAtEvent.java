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
package org.illarion.nifty.controls;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This event is fired in case the player want to look at a item in the merchant dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
@Immutable
public final class DialogMerchantLookAtEvent extends DialogEvent {
    /**
     * The item that was bought.
     */
    @Nonnull
    private final MerchantListEntry item;

    /**
     * Create a new instance of this event and set the ID of the dialog that was used to buy items from.
     *
     * @param id the ID of the dialog
     * @param buyItem the item to buy
     */
    public DialogMerchantLookAtEvent(int id, @Nonnull MerchantListEntry buyItem) {
        super(id);
        item = buyItem;
    }

    /**
     * Get the item the player wants to buy.
     *
     * @return the item that is bought
     */
    @Nonnull
    public MerchantListEntry getItem() {
        return item;
    }
}
