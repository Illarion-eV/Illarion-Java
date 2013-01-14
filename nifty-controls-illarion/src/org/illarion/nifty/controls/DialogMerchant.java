/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls;

import de.lessvoid.nifty.controls.Window;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * This interface is used to interact with a merchant dialog that is displayed inside the GUI.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
@NotThreadSafe
public interface DialogMerchant extends Window {
    /**
     * Add all items in the supplied list to the list of items sold by the NPC.
     *
     * @param entries the list of entries to add to the list of sold items
     */
    void addAllSellingItems(@Nonnull List<MerchantListEntry> entries);

    /**
     * Add all items in the supplied list to the list of items bought by the NPC.
     *
     * @param entries the list of entries to add to the list of bought items
     */
    void addAllBuyingItems(@Nonnull List<MerchantListEntry> entries);

    /**
     * Set the ID of the dialog.
     *
     * @param id the dialog Id
     */
    void setDialogId(int id);

    /**
     * Get the dialog ID of this merchant dialog.
     *
     * @return the dialog ID
     */
    int getDialogId();

    /**
     * Get the amount of entries on the buying list of the merchant.
     *
     * @return the entry index
     */
    int getBuyEntryCount();

    /**
     * Get the amount of entries on the selling list of the merchant.
     *
     * @return the entry index
     */
    int getSellEntryCount();

    /**
     * Get the item that was selected.
     *
     * @return the selected item or {@code null} in case no entry is selected
     */
    @Nullable
    MerchantListEntry getSelectedItem();

    /**
     * Get the selected index.
     *
     * @return the index that was selected or {@code -1} in case no item is selected
     */
    int getSelectedIndex();

    /**
     * Add a item to the list of items the trader is selling.
     *
     * @param entry the item to add
     */
    void addSellingItem(@Nonnull MerchantListEntry entry);

    /**
     * Add a item to the list of items the trader is buying.
     *
     * @param entry the item to add
     */
    void addBuyingItem(@Nonnull MerchantListEntry entry);

    /**
     * Remove all items from both the buying and the selling list.
     */
    void clearItems();
}
