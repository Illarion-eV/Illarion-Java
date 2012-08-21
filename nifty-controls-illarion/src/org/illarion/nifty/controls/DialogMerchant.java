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

/**
 * This interface is used to interact with a merchant dialog that is displayed inside the GUI.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public interface DialogMerchant extends Window {
    /**
     * This enumerator identifies the buttons displayed on this dialog.
     */
    enum DialogButtons {
        /**
         * The button that reads buy.
         */
        buy,

        /**
         * The button that reads close.
         */
        close;
    }

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
     * @return the selected item
     */
    MerchantListEntry getSelectedItem();

    /**
     * Get the selected index.
     *
     * @return the index that was selected
     */
    int getSelectedIndex();

    /**
     * Add a item to the list of items the trader is selling.
     *
     * @param entry the item to add
     */
    void addSellingItem(MerchantListEntry entry);

    /**
     * Add a item to the list of items the trader is buying.
     *
     * @param entry the item to add
     */
    void addBuyingItem(MerchantListEntry entry);
}
