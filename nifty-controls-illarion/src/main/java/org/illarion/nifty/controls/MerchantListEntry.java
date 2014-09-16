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

import de.lessvoid.nifty.render.NiftyImage;
import illarion.common.types.ItemCount;
import illarion.common.types.Money;

import javax.annotation.Nonnull;

/**
 * This interfaces defines a entry in the merchant dialog that contains one item to buy.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface MerchantListEntry {
    enum EntryType {
        Selling, BuyPrimary, BuySecondary
    }
    /**
     * Get the image that is supposed to be displayed in the entry.
     *
     * @return the nifty image to display
     */
    @Nonnull
    NiftyImage getItemImage();

    /**
     * Get the name of the item.
     *
     * @return the name of the item
     */
    @Nonnull
    String getName();

    /**
     * Get the price of the item.
     *
     * @return the price of the item
     */
    @Nonnull
    Money getPrice();

    /**
     * Get the bundle size of the item. This is only required for items that are sold by the NPC.
     *
     * @return the amount of items bought at once
     */
    @Nonnull
    ItemCount getBundleSize();

    /**
     * The index of the merchant item in the list as it was transferred from the server.
     *
     * @return the index
     */
    int getIndex();

    /**
     * Get the type of the entry.
     *
     * @return the entry type
     */
    @Nonnull
    EntryType getEntryType();
}
