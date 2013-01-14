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

/**
 *
 */
package org.illarion.nifty.controls;

import de.lessvoid.nifty.controls.NiftyControl;
import de.lessvoid.nifty.render.NiftyImage;

import javax.annotation.Nonnull;

/**
 * The interface to control a single slot of the inventory.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface InventorySlot extends NiftyControl {
    /**
     * The different stages for the merchant overlay.
     */
    enum MerchantBuyLevel {
        /**
         * The lowest level. Displayed with a copper coin.
         */
        Copper,

        /**
         * Medium level. Displayed with a silver coin.
         */
        Silver,

        /**
         * Highest level. Displayed with a gold coin.
         */
        @SuppressWarnings("EnumeratedConstantNamingConvention")
        Gold
    }

    /**
     * Set the image that is supposed to be displayed in this inventory slot.
     *
     * @param image the displayed image
     */
    void setImage(@Nonnull NiftyImage image);

    /**
     * Set the image that is displayed as background of this slot.
     *
     * @param image the image in the background of this slot
     */
    void setBackgroundImage(@Nonnull NiftyImage image);

    /**
     * Show the label in this inventory slot.
     */
    void showLabel();

    /**
     * Hide the label in this inventory slot.
     */
    void hideLabel();

    /**
     * Set the text that is displayed in the label of this slot.
     *
     * @param text the label text
     */
    void setLabelText(@Nonnull String text);

    /**
     * Send the draggable object back to this slot.
     */
    void retrieveDraggable();

    /**
     * This function causes the visibility stare of the component to be restored. This is needed in case the parent
     * object changed its visibility.
     */
    void restoreVisibility();

    /**
     * Hide the merchant overlay in case its currently displayed.
     */
    void hideMerchantOverlay();

    /**
     * Show the specified merchant overlay.
     *
     * @param level the overlay level to show
     */
    void showMerchantOverlay(@Nonnull InventorySlot.MerchantBuyLevel level);
}
